package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import static ru.practicum.shareit.exception.ErrorDetails.USER_DUPLICATE_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final long TMP_ID = 0;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        log.info("Получен запрос на добавление пользователя: " + userDto);

        User user = new User(TMP_ID, userDto.getName(), userDto.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsError(USER_DUPLICATE_ERROR);
        }
        log.info("Пользователь добавлен");
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.info("Получен запрос на обновление сведений о пользователе с id " + userId
                + ", новые сведения: " + userDto);

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError(USER_NOT_FOUND));
        String newEmail = userDto.getEmail();

        if (newEmail != null) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new ResourceAlreadyExistsError(USER_DUPLICATE_ERROR);
            }
        }
        userMapper.updateUserFromUserDto(userDto, foundUser);
        return userMapper.toUserDto(userRepository.save(foundUser));
    }

    @Override
    public UserDto get(long userId) {
        log.info("Получен запрос на поиск информации о пользователе с id " + userId);

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError(USER_NOT_FOUND));

        log.info("Пользователь с id " + userId + " найден");
        return userMapper.toUserDto(foundUser);
    }

    @Override
    public void delete(long userId) {
        log.info("Получен запрос на удаление пользователя с id " + userId);
        userRepository.deleteById(userId);
    }
}