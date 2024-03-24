package com.backend.ord.controllers;

import com.backend.ord.domain.dto.UserDTO;
import com.backend.ord.domain.mappers.UserMapper;
import com.backend.ord.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/")
    public List<UserDTO> getAll() {
        return userService.getAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}
