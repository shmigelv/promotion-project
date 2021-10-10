package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.dto.CourseFeedbackDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CourseFeedbackMapper {

    @Mappings(value = {
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "course.id", target = "courseId"),
            @Mapping(source = "student.id", target = "studentId"),
            @Mapping(source = "feedback", target = "feedback")
    })
    CourseFeedbackDTO toCourseFeedbackDto(CourseFeedback courseFeedback);

}
