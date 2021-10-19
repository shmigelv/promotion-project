package api;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import util.DefaultTestConfiguration;
import util.TestUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.JacksonUtil.serialize;

@DefaultTestConfiguration
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TestUtil testUtil;

    @Test
    void adminCanAssignRoleToUser() throws Exception {
        // GIVEN existing admin and user without role
        User admin = userService.saveUser(new User("admin", "pass1", Roles.ROLE_ADMIN));
        User user = userService.saveUser("student", "pass1");

        // WHEN admin setts role to a user without role
        String jwt = testUtil.getAuthToken(admin);

        ResultActions resultActions = mockMvc.perform(put("/admin/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content(Roles.ROLE_STUDENT.name()));

        // THEN role is set for user
        resultActions.andExpect(status().isOk());

        User student = userService.findByIdAndRole(user.getId(), Roles.ROLE_STUDENT);
        assertNotNull(student);
        assertNotNull(student.getRole());
    }

    @Test
    void adminCantAssignRoleToUser_whenRoleAlreadySet() throws Exception {
        // GIVEN existing admin and user with role set
        User admin = userService.saveUser(new User("admin", "pass1", Roles.ROLE_ADMIN));
        User user = userService.saveUser(new User("student", "pass1", Roles.ROLE_STUDENT));

        // WHEN admin setts role to a user with role
        String jwt = testUtil.getAuthToken(admin);

        ResultActions resultActions = mockMvc.perform(put("/admin/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content(Roles.ROLE_STUDENT.name()));

        // THEN response is error
        resultActions.andExpect(status().is4xxClientError());

        User student = userService.findByIdAndRole(user.getId(), Roles.ROLE_STUDENT);
        assertNotNull(student);
        assertNotNull(student.getRole());
    }

    @Test
    void nonAdminUserCantAssignRoleToUser() throws Exception {
        // GIVEN existing admin and user with role set
        User admin = userService.saveUser(new User("admin", "pass1", Roles.ROLE_INSTRUCTOR));
        User user = userService.saveUser(new User("student", "pass1"));

        // WHEN admin setts role to a user with role
        String jwt = testUtil.getAuthToken(admin);

        ResultActions resultActions = mockMvc.perform(put("/admin/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content(Roles.ROLE_STUDENT.name()));

        // THEN response is error
        resultActions.andExpect(status().isForbidden());

        assertThrows(ResponseStatusException.class, () -> userService.findByIdAndRole(user.getId(), Roles.ROLE_STUDENT));
    }

    @Test
    void adminCanCreateCourse() throws Exception {
        // GIVEN user with admin rights
        User admin = userService.saveUser(new User("amin", "pass", Roles.ROLE_ADMIN));

        User instructor = userService.saveUser(new User("instructor", "pass", Roles.ROLE_INSTRUCTOR));
        List<String> lessonTitles = IntStream.of(1, 5).mapToObj(i -> "lesson_name_" + i).collect(Collectors.toList());
        CreateCourseDTO createCourse = new CreateCourseDTO("course_title", List.of(instructor.getId()), lessonTitles);

        // WHEN creates new course
        ResultActions resultActions = mockMvc.perform(post("/admin/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(createCourse))
                .header("Authorization", "Bearer " + testUtil.getAuthToken(admin)));

        // THEN response contains information
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.instructorIds").isNotEmpty());

//        courseService.get
    }

}
