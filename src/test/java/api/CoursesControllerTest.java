package api;

import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.repository.*;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import util.ApiTestConfiguration;
import util.TestUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static util.JacksonUtil.*;

@ApiTestConfiguration
public class CoursesControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Test
    void studentCanSubscribeToTheCourse() throws Exception {
        // GIVEN student and course
        Student student = testUtil.createTestStudent();
        Course course = courseRepository.save(new Course("course"));

        // WHEN student subscribes to the course
        mockMvc.perform(post("/courses/" + course.getId() + "/students/" + student.getId())
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(student)))
                .andExpect(status().isOk());

        // THEN course is created
        assertTrue(courseService.getStudentCourses(student.getId()).contains(course));
    }

    @Test
    void studentCantSubscribeToTheCourse_whenStudentHasMoreThanFourCourses() throws Exception {
        // GIVEN student with 5 courses and new course
        Student student = testUtil.createTestStudent();
        IntStream.rangeClosed(1, 5).mapToObj(i -> new Course("course" + i))
                .peek(i -> i.addStudent(student))
                .forEach(courseRepository::save);

        Course newCourse = courseRepository.save(new Course("course6"));

        // WHEN student subscribes to the course
        mockMvc.perform(post("/courses/" + newCourse.getId() + "/subscribe")
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(student)))
                .andExpect(status().is4xxClientError());

        // THEN student is not subscribed to the course
        assertFalse(courseService.getStudentCourses(student.getId()).contains(newCourse));
    }

    @Test
    void studentCantSubscribeToTheCourse_whenStudentAlreadySubscribedToCourse() throws Exception {
        // GIVEN student that already subscribed to course
        Student student = testUtil.createTestStudent();
        Course newCourse = courseRepository.save(new Course("course6"));

        newCourse.addStudent(student);
        courseRepository.save(newCourse);

        // WHEN student subscribes to the course
        mockMvc.perform(post("/courses/" + newCourse.getId() + "/subscribe")
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(student)))
                .andExpect(status().is4xxClientError());

        // THEN student is still subscribed to the course
        assertTrue(courseService.getStudentCourses(student.getId()).contains(newCourse));
    }

    @Test
    void adminCanCreateCourse() throws Exception {
        // GIVEN admin with course creation information
        Admin admin = testUtil.createTestAdmin();
        Instructor instructor = testUtil.createTestInstructor();

        List<String> lessonNames = IntStream.rangeClosed(1, 5).mapToObj(i -> "lesson" + i).collect(Collectors.toList());
        CreateCourseDTO createCourse = new CreateCourseDTO("courseTitle", List.of(instructor.getId()), lessonNames);


        // WHEN admin creates course
        MvcResult mvcResult = mockMvc.perform(post("/courses/")
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialize(createCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("courseTitle"))
                .andExpect(jsonPath("$.instructorIds", contains(instructor.getId().intValue())))
                .andExpect(jsonPath("$.studentIds").isEmpty())
                .andReturn();

        // THEN course is created
        CourseDTO responseCourse = deserialize(mvcResult.getResponse().getContentAsString(), CourseDTO.class);
        Course newCourse = courseService.getCourseById(responseCourse.getId());
        Collection<Lesson> courseLessons = lessonService.getCourseLessons(newCourse.getId());

        assertEquals(List.of(instructor), newCourse.getInstructors());
        assertTrue(newCourse.getStudents().isEmpty());
        assertEquals(courseLessons.size(), newCourse.getLessons().size());
    }

    @Test
    void adminCantCreateCourse_whenProvidedCourseInstructorsAreEmpty() throws Exception {
        // GIVEN admin with course creation information
        Admin admin = testUtil.createTestAdmin();

        CreateCourseDTO createCourse = new CreateCourseDTO("course", List.of(), List.of("1", "2", "3", "4", "5"));

        // WHEN admin creates course
        mockMvc.perform(post("/courses/")
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialize(createCourse)))
                .andExpect(status().is4xxClientError());

        // THEN course is not created
        assertEquals(0, courseRepository.findAll().spliterator().getExactSizeIfKnown());
    }

    @Test
    void adminCantCreateCourse_whenProvidedLessonsNameIsLessThanFive() throws Exception {
        // GIVEN admin with course creation information
        Admin admin = testUtil.createTestAdmin();
        Instructor instructor = testUtil.createTestInstructor();

        CreateCourseDTO createCourse = new CreateCourseDTO("course", List.of(instructor.getId()), List.of("1", "2", "3", "4"));

        // WHEN admin creates course
        mockMvc.perform(post("/courses/")
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialize(createCourse)))
                .andExpect(status().is4xxClientError());

        // THEN course is not created
        assertEquals(0, courseRepository.findAll().spliterator().getExactSizeIfKnown());
    }

    @Test
    void studentCanGetHisCourses() throws Exception {
        // GIVEN user with subscribed course
        Student student = testUtil.createTestStudent();
        Course course = new Course("course");
        course.addStudent(student);
        courseRepository.save(course);

        // WHEN user wants to see his courses
        ResultActions resultActions = mockMvc.perform(get("/courses")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(student)));

        // THEN valid course is returned
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].title").value(course.getTitle()))
                .andExpect(jsonPath("$.[0].studentIds", contains(student.getId().intValue())));
    }

    @Test
    void instructorCanGetHisCourses() throws Exception {
        // GIVEN instructor with course
        Instructor instructor = testUtil.createTestInstructor();
        Course course = courseRepository.save(Course.builder().title("course").instructors(List.of(instructor)).build());

        // WHEN instructor wants to see his courses
        ResultActions resultActions = mockMvc.perform(get("/courses")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor)));

        // THEN valid course is returned
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].title").value(course.getTitle()))
                .andExpect(jsonPath("$.[0].instructorIds", contains(instructor.getId().intValue())));
    }

    @Test
    void studentCanSeeHisCourseStatusInProgress_whenNoMarksHaveBeanSet() throws Exception {
        // GIVEN student with started course without marks
        Student student = testUtil.createTestStudent();

        Course newCourse = new Course("course");
        newCourse.addLesson(new Lesson("lesson"));
        newCourse.addStudent(student);

        courseRepository.save(newCourse);

        // WHEN student requests his course status
        ResultActions resultActions = mockMvc.perform(get(String.format("/courses/%s/status", newCourse.getId(), student.getId()))
                .header("Authorization", "Bearer " + testUtil.getAuthToken(student)));

        // THEN response is IN_PROGRESS
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void studentCanSeeHisCourseStatusInProgress_whenAverageCourseMarkIsAcceptable() throws Exception {
        // GIVEN student with started course without marks
        Student student = testUtil.createTestStudent();

        Course newCourse = new Course("course");
        Lesson lesson = new Lesson("lesson");

        newCourse.addLesson(lesson);
        newCourse.addStudent(student);
        courseRepository.save(newCourse);

        Homework homework = Homework.builder().lesson(lesson).student(student).mark(80).build();
        homeworkRepository.save(homework);

        // WHEN student requests his course status
        ResultActions resultActions = mockMvc.perform(get(String.format("/courses/%s/status", newCourse.getId(), student.getId()))
                .header("Authorization", "Bearer " + testUtil.getAuthToken(student)));

        // THEN response is PASSED
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PASSED"));
    }


    @Test
    void studentCanSeeHisCourseStatusInProgress_whenAverageCourseMarkIsNotAcceptable() throws Exception {
        // GIVEN student with started course without marks
        Student student = testUtil.createTestStudent();

        Course newCourse = new Course("course");
        Lesson lesson = new Lesson("lesson");

        newCourse.addLesson(lesson);
        newCourse.addStudent(student);
        courseRepository.save(newCourse);

        Homework homework = Homework.builder().lesson(lesson).student(student).mark(70).build();
        homeworkRepository.save(homework);

        // WHEN student requests his course status
        ResultActions resultActions = mockMvc.perform(get(String.format("/courses/%s/status", newCourse.getId(), student.getId()))
                .header("Authorization", "Bearer " + testUtil.getAuthToken(student)));

        // THEN response is PASSED
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void instructorCanSeeListOfStudentsPerCourse() throws Exception {
        // GIVEN instructor with course, and student that subscribed to course
        Instructor instructor = testUtil.createTestInstructor();
        Student student = testUtil.createTestStudent();

        Course course = new Course("course", List.of(instructor));
        course.setStudents(List.of(student));
        courseRepository.save(course);

        // WHEN instructor requests list of students for that course
        ResultActions resultActions = mockMvc.perform(get("/courses/" + course.getId() + "/students")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor)));

        //THEN response contains list of one student
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].username").value("student"))
                .andExpect(jsonPath("$[0].role").value("ROLE_STUDENT"));
    }

    @Test
    void instructorCanSeeEmptyListOfStudents_whenNoUserSubscribedToTheCourse() throws Exception {
        // GIVEN instructor with course
        Instructor instructor = testUtil.createTestInstructor();

        Course course = courseRepository.save(new Course("course", List.of(instructor)));

        // WHEN instructor requests list of students for that course
        ResultActions resultActions = mockMvc.perform(get("/courses/" + course.getId() + "/students")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor)));

        //THEN response contains empty list
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void instructorCanCreateFeedbackForCourseStudent() throws Exception {
        // GIVEN instructor with course and student subscribed to it
        Instructor instructor = testUtil.createTestInstructor();
        Student student = testUtil.createTestStudent();

        Course course = new Course("course", List.of(instructor));
        course.addStudent(student);
        courseRepository.save(course);

        // WHEN instructor creates feedback
        ResultActions resultActions = mockMvc.perform(post("/courses/" + course.getId() + "/students/" + student.getId() + "/feedback")
                .header("Authorization", "Bearer " + testUtil.getAuthToken(instructor))
                .contentType(MediaType.TEXT_PLAIN)
                .content("course user feedback"));

        // THEN response contains information about created feedback
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.courseId").value(course.getId()))
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.feedback").value("course user feedback"));
    }

    @Test
    void studentCanSeeLessonsPerCourseWithAllRelatedInformation() throws Exception {
        // GIVEN student with course and homeworks for some lessons
        Student student = testUtil.createTestStudent();

        Course course = new Course("course");
        Lesson firstLesson = new Lesson("lesson1");
        Lesson secondLesson = new Lesson("lesson2");

        Homework homework1 = Homework.builder().student(student).mark(80).homeworkFileKey("/test").lesson(firstLesson).build();
        Homework homework2 = Homework.builder().student(student).mark(20).homeworkFileKey(null).lesson(secondLesson).build();

        course.setStudents(List.of(student));
        course.addLesson(firstLesson);
        course.addLesson(secondLesson);
        firstLesson.setHomeworks(List.of(homework1));
        secondLesson.setHomeworks(List.of(homework2));

        courseRepository.save(course);

        // WHEN
        mockMvc.perform(get("/courses/" + course.getId() + "/lesson-details")
                        .header("Authorization", "Bearer " + testUtil.getAuthToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // THEN
    }

    @Test
    void adminShouldBeAbleToAssignInstructorToTheCourse() throws Exception {
        // GIVEN admin and instructor that is not assigned to course
        Admin admin = testUtil.createTestAdmin();
        Instructor instructor = testUtil.createTestInstructor();
        Course course = courseRepository.save(new Course("course"));

        // WHEN admin makes request to assign instructor to given course
        ResultActions resultActions = mockMvc.perform(put("/courses/" + course.getId() + "/instructors/" + instructor.getId())
                .header("Authorization", "Bearer " + testUtil.getAuthToken(admin)));

        // THEN response is valid and instructor assigned to course
        resultActions.andExpect(status().isOk());

        Course updatedCourse = courseService.getCourseById(course.getId());
        assertEquals(List.of(instructor), updatedCourse.getInstructors());
    }

}
