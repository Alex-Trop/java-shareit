package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import static ru.practicum.shareit.exception.ErrorDetails.USER_DUPLICATE_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage userStorage;
    private static final long TMP_ID = 0;

    @Override
    public UserDto add(UserDto userDto) {
        log.info("Получен запрос на добавление пользователя: " + userDto);

        User user = new User(TMP_ID, userDto.getName(), userDto.getEmail());

        if (userStorage.checkEmailDuplicates(user.getEmail())) {
            throw new ResourceAlreadyExistsError(USER_DUPLICATE_ERROR);
        }
        return UserMapper.toUserDto(userStorage.add(user));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.info("Получен запрос на обновление сведений о пользователе с id " + userId
                + ", новые сведения: " + userDto);

        User foundUser = userStorage.getUserById(userId);
        String newEmail = userDto.getEmail();

        if (foundUser == null) {
            throw new NotFoundError(USER_NOT_FOUND);
        }
        if (newEmail != null) {
            if (userStorage.checkEmailDuplicates(newEmail)) {
                throw new ResourceAlreadyExistsError(USER_DUPLICATE_ERROR);
            }
        }
        return UserMapper.toUserDto(userStorage.update(userId, userDto));
    }

    @Override
    public UserDto get(long userId) {
        log.info("Получен запрос на поиск информации о пользователе с id " + userId);

        User foundUser = userStorage.getUserById(userId);

        if (foundUser == null) {
            throw new NotFoundError(USER_NOT_FOUND);
        }
        return UserMapper.toUserDto(foundUser);
    }

    @Override
    public void delete(long userId) {
        log.info("Получен запрос на удаление пользователя с id " + userId);

        User foundUser = userStorage.getUserById(userId);

        if (foundUser == null) {
            throw new NotFoundError(USER_NOT_FOUND);
        }
        userStorage.delete(userId);
        log.info("Пользователь удален");
    }
}
