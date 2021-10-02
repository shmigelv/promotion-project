package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CourseDetailsDTO;
import com.shmigel.promotionproject.model.dto.LessonDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    default CourseDetailsDTO toCourseDetails(Course course) {
        CourseDetailsDTO courseDetails = new CourseDetailsDTO();
        ArrayList<LessonDetailsDTO> lessonDetails = course.getLessons().stream()
                .map(this::toLessonDetailsDTO)
                .collect(Collectors.toCollection(ArrayList::new));

        courseDetails.setCourse(toCourseDto(course));
        courseDetails.setLessonDetails(lessonDetails);

        return courseDetails;
    }

    default LessonDetailsDTO toLessonDetailsDTO(Lesson lesson) {
        LessonDetailsDTO lessonDetailsDTO = new LessonDetailsDTO();

        lessonDetailsDTO.setId(lesson.getId());
        lessonDetailsDTO.setTitle(lesson.getTitle());

        if (Objects.nonNull(lesson.getHomeworks())) {
//            lessonDetailsDTO.setMark(lesson.);
//            lessonDetailsDTO.setHomeworkUploaded();
        }

        return lessonDetailsDTO;
    }

    @Mappings(value = {
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "instructors", target = "instructorIds", qualifiedByName = "usersToIds"),
            @Mapping(source = "students", target = "studentIds", qualifiedByName = "usersToIds")
    })
    CourseDTO toCourseDto(Course course);

    Collection<CourseDTO> toCourseDTOs(Collection<Course> courses);

    @Named("usersToIds")
    static Collection<Long> usersToIds(Collection<User> users) {
        if (Objects.isNull(users)) {
            return Collections.emptyList();
        }
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

}
