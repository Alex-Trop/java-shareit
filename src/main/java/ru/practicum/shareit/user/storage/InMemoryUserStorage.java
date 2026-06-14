package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;

import static ru.practicum.shareit.exception.ErrorDetails.USER_DUPLICATE_ERROR;

@Component
@Slf4j
public class InMemoryUserStorage {
    private static final HashMap<Long, User> allUsers = new HashMap<>();
    private static long id = 0;

    private boolean checkEmailDuplicates(String email) {
        for (User user: allUsers.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public User add(UserDto userDto) {
        id++;
        User user = new User(id, userDto.getName(), userDto.getEmail());

        if (checkEmailDuplicates(user.getEmail())) {
            throw new ResourceAlreadyExistsError(USER_DUPLICATE_ERROR);
        }
        allUsers.put(user.getId(), user);
        log.info("Добавлен пользователь: " + user);
        return user;
    }

    public User update(long userId, UserDto userDto) {
        User foundUser = getUserById(userId);
        String newEmail = userDto.getEmail();

        if (newEmail != null) {
            if (checkEmailDuplicates(newEmail)) {
                throw new ResourceAlreadyExistsError(USER_DUPLICATE_ERROR);
            }
            foundUser.setEmail(newEmail);
        }
        if (userDto.getName() != null) {
            foundUser.setName(userDto.getName());
        }
        allUsers.replace(userId, foundUser);
        log.info("Обновлены сведения о пользователе: " + foundUser);
        return foundUser;
    }

    public User getUserById(long userId) {
        return allUsers.get(userId);
    }

    public void delete(long userId) {
        allUsers.remove(userId);
    }
}
