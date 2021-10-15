package api;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.repository.CourseRepository;
import com.shmigel.promotionproject.repository.LessonRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import com.shmigel.promotionproject.util.CollectionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import util.ApiTestConfiguration;
import util.TestUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.JacksonUtil.deserialize;
import static util.JacksonUtil.serialize;

@ApiTestConfiguration
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TestUtil testUtil;

    @Test
    void adminCanAssignRoleToUser() throws Exception {
        // GIVEN existing admin and user without role
        Admin admin = testUtil.createTestAdmin();
        User user = userService.saveUser(new User("student", "pass"));

        // WHEN admin setts role to a user without role
        String jwt = testUtil.getAuthToken(admin);

        ResultActions resultActions = mockMvc.perform(put("/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content(Roles.ROLE_STUDENT.name()));

        // THEN role is set for user
        resultActions.andExpect(status().isOk());
    }

    @Test
    void adminCantAssignRoleToUser_whenRoleAlreadySet() throws Exception {
        // GIVEN existing admin and user with role set
        Admin admin = testUtil.createTestAdmin();
        Student student = testUtil.createTestStudent();

        // WHEN admin setts role to a user with role
        String jwt = testUtil.getAuthToken(admin);

        ResultActions resultActions = mockMvc.perform(put("/admin/users/" + student.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content(Roles.ROLE_STUDENT.name()));

        // THEN response is error
        resultActions.andExpect(status().is4xxClientError());

        User savedStudent = userService.getStudentById(student.getId());
        assertNotNull(savedStudent);
        assertEquals(Roles.ROLE_STUDENT, savedStudent.getRole());
    }

    @Test
    void nonAdminUserCantAssignRoleToUser() throws Exception {
        // GIVEN instructor and user with role set
        Instructor instructor = testUtil.createTestInstructor();
        User user = userService.saveUser(new User("student", "pass"));

        // WHEN admin setts role to a user with role
        ResultActions resultActions = mockMvc.perform(put("/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor))
                .content(Roles.ROLE_STUDENT.name()));

        // THEN response is error
        resultActions.andExpect(status().isForbidden());

        assertThrows(EntityNotFoundException.class, () -> userService.getStudentById(user.getId()));
    }

    @Test
    void adminCanCreateCourse() throws Exception {
        // GIVEN user with admin rights
        User admin = testUtil.createTestAdmin();

        User instructor = testUtil.createTestInstructor();
        List<String> lessonTitles = IntStream.rangeClosed(1, 5).mapToObj(i -> "lesson_name_" + i).collect(Collectors.toList());
        CreateCourseDTO createCourse = new CreateCourseDTO("course_title", List.of(instructor.getId()), lessonTitles);

        // WHEN creates new course
        ResultActions resultActions = mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(createCourse))
                .header("Authorization", "Bearer " + testUtil.getAuthToken(admin)));

        // THEN response contains information
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.instructorIds").isNotEmpty());

        CourseDTO createdCourse = deserialize(resultActions.andReturn().getResponse().getContentAsString(), CourseDTO.class);

        assertEquals(1, courseRepository.count());
        Course course = courseRepository.findById(createdCourse.getId()).orElseThrow();

        assertEquals(createdCourse.getId(), course.getId());
        assertEquals(createCourse.getTitle(), course.getTitle());

        List<String> courseLessons = CollectionUtil.toCollection(lessonRepository.findAllById(createdCourse.getLessonIds()))
                .stream().map(Lesson::getTitle).collect(Collectors.toList());
        assertEquals(lessonTitles, courseLessons);
    }

}
