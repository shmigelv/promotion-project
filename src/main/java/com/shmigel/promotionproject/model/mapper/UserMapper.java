package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Collection;

import static java.util.Objects.nonNull;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings(value = {
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "role", target = "role", qualifiedByName = "mapToRoleName")
    })
    UserDTO toUserDTO(User user);

    Collection<UserDTO> toUserDTOs(Collection<User> users);

    Collection<UserDTO> toUserDTOsFS(Collection<Student> students);

    @Named("mapToRoleName")
    default String mapToRoleName(Roles role) {
        return nonNull(role) ? role.name() : null;
    }

}
