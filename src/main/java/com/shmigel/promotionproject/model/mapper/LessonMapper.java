package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.LessonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mappings(value = {
            @Mapping(source = "course.id", target = "courseId")
    })
    LessonDTO toDTO(Lesson lesson);

}
