package com.backend.ord.domain.mappers.impl;

import com.backend.ord.domain.dto.UserDTO;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.mappers.UserMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UserMapperImpl implements UserMapper {
    private final ModelMapper mapper;

    @Override
    public UserDTO toDTO(User user) {
        return mapper.map(user, UserDTO.class);
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        return mapper.map(userDTO, User.class);
    }
}
