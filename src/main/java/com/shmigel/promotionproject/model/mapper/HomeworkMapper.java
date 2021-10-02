package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.dto.HomeworkDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface HomeworkMapper {

    @Mappings(value = {
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "lesson.id", target = "lessonId"),
            @Mapping(source = "student.id", target = "studentId"),
            @Mapping(source = "homeworkFileKey", target = "homeworkFilePresent", qualifiedByName = "isTextEmpty")
    })
    HomeworkDTO toHomeworkDTO(Homework homework);

    @Named("isTextEmpty")
    static boolean isTextEmpty(String text) {
        return StringUtils.hasText(text);
    }

}
