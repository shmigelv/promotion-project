package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.UserLessonOverviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserLessonOverviewMapper {

    @Mappings(value = {
            @Mapping(source = "lesson.id", target = "id"),
            @Mapping(source = "lesson.title", target = "title"),
            @Mapping(source = "homework.mark", target = "mark"),
    })
    UserLessonOverviewDTO toDTO(Lesson lesson, Homework homework);

}
