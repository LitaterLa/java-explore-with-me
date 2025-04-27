package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest userRequest);

    UserDto findUserById(Integer id);

    List<UserDto> findAll(int from, int size, List<Integer> usersIds);

    void delete(Integer id);
}
