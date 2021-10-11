package api;

import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.repository.CourseRepository;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import util.ApiTestConfiguration;
import util.TestUtil;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.JacksonUtil.serialize;

@ApiTestConfiguration
public class LessonControllerTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Test
    void instructorCanUploadHomeworkForStudent() throws Exception {
        // GIVEN
        Instructor instructor = testUtil.createTestInstructor();
        Student student = testUtil.createTestStudent();

        Course course = new Course("course", List.of(instructor));
        Lesson lesson = new Lesson("lesson");

        course.addStudent(student);
        course.addLesson(lesson);
        courseRepository.save(course);

        MockMultipartFile homeworkFile =
                new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "file_content".getBytes(StandardCharsets.UTF_8));

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                multipart("/lessons/" + lesson.getId() + "/homeworks")
                        .file(homeworkFile)
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(student)));

        //THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.lessonId").value(lesson.getId()))
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.homeworkFilePresent").value(true));

        Collection<Homework> studentHomeworks = homeworkService.getAllHomeworksByCourseIdAndStudentId(course.getId(), student.getId());

        assertEquals(1, studentHomeworks.size());
        assertTrue(studentHomeworks.stream().allMatch(i -> i.getLesson().equals(lesson)));
        assertTrue(studentHomeworks.stream().allMatch(i -> i.getStudent().equals(student)));
        assertTrue(studentHomeworks.stream().noneMatch(i -> i.getHomeworkFileKey().isEmpty()));
    }

    @Test
    void instructorCantUploadHomeworkForStudent_whenHomeworkAlreadySend() throws Exception {
        // GIVEN
        Instructor instructor = testUtil.createTestInstructor();
        Student student = testUtil.createTestStudent();

        Course course = new Course("course", List.of(instructor));
        Lesson lesson = new Lesson("lesson");

        course.addStudent(student);
        course.addLesson(lesson);
        courseRepository.save(course);

        Homework homework = homeworkService.uploadHomework(student.getId(), lesson.getId(),
                new MockMultipartFile("origin_file", "origin.txt", MediaType.TEXT_PLAIN_VALUE, "origin_file_content".getBytes(StandardCharsets.UTF_8)));

        MockMultipartFile homeworkFile =
                new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "file_content".getBytes(StandardCharsets.UTF_8));

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                multipart("/lessons/" + lesson.getId() + "/homeworks")
                        .file(homeworkFile)
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(student)));

        //THEN
        resultActions.andExpect(status().is4xxClientError());

        Collection<Homework> studentHomeworks = homeworkService.getAllHomeworksByCourseIdAndStudentId(course.getId(), student.getId());

        assertEquals(1, studentHomeworks.size());
        assertTrue(studentHomeworks.stream().allMatch(i -> i.getLesson().equals(lesson)));
        assertTrue(studentHomeworks.stream().allMatch(i -> i.getStudent().equals(student)));
        assertTrue(studentHomeworks.stream().allMatch(i -> i.getHomeworkFileKey().equals(homework.getHomeworkFileKey())));
    }

    @Test
    void instructorCanPutMarkForStudentLesson() throws Exception {
        // GIVEN
        Instructor instructor = testUtil.createTestInstructor();
        Student student = testUtil.createTestStudent();

        Course course = new Course("course", List.of(instructor));
        Lesson lesson = new Lesson("lesson");

        course.addStudent(student);
        course.addLesson(lesson);
        courseRepository.save(course);

        MarkDTO mark = new MarkDTO(80);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/lessons/" + lesson.getId() + "/students/" + student.getId() + "/marks")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor))
                .contentType(MediaType.APPLICATION_JSON).content(serialize(mark)));

        //THEN
        resultActions.andExpect(status().isOk());

        Collection<Homework> userHomeworks =
                homeworkService.getAllHomeworksByCourseIdAndStudentId(course.getId(), student.getId());

        assertEquals(1, userHomeworks.size());
        assertTrue(userHomeworks.stream().allMatch(i -> i.getMark().equals(80)));
        assertTrue(userHomeworks.stream().allMatch(i -> i.getLesson().equals(lesson)));
        assertTrue(userHomeworks.stream().allMatch(i -> i.getStudent().equals(student)));
    }

    @Test
    void instructorCantPutMarkForStudentLesson_whenMarkAlreadySet() throws Exception {
        // GIVEN
        Instructor instructor = testUtil.createTestInstructor();
        Student student = testUtil.createTestStudent();

        Course course = new Course("course", List.of(instructor));
        Lesson lesson = new Lesson("lesson");

        new Homework();

        course.addStudent(student);
        course.addLesson(lesson);
        courseRepository.save(course);

        MarkDTO mark = new MarkDTO(80);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/lessons/" + lesson.getId() + "/students/" + student.getId() + "/marks")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor))
                .contentType(MediaType.APPLICATION_JSON).content(serialize(mark)));

        //THEN
        resultActions.andExpect(status().isOk());

        Collection<Homework> userHomeworks =
                homeworkService.getAllHomeworksByCourseIdAndStudentId(course.getId(), student.getId());

        assertEquals(1, userHomeworks.size());
        assertTrue(userHomeworks.stream().allMatch(i -> i.getMark().equals(80)));
        assertTrue(userHomeworks.stream().allMatch(i -> i.getLesson().equals(lesson)));
        assertTrue(userHomeworks.stream().allMatch(i -> i.getStudent().equals(student)));
    }

}
