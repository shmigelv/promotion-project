package com.shmigel.promotionproject.model.mapper;

import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings(value = {
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username")
    })
    UserDTO toUserDTO(User user);

    Collection<UserDTO> toUserDTOs(Collection<User> users);

}
