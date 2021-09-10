package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CourseMapper {

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
