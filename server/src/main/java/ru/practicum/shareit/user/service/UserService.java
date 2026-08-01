package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    UserDto get(long userId);

    void delete(long userId);
}
