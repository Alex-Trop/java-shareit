package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;

@Component
@Slf4j
public class InMemoryUserStorage {
    private static final HashMap<Long, User> allUsers = new HashMap<>();
    private static long id = 0;

    public boolean checkEmailDuplicates(String email) {
        for (User user: allUsers.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public User add(User user) {
        id++;
        user.setId(id);
        allUsers.put(id, user);
        log.info("Добавлен пользователь: " + user);
        return user;
    }

    public User update(long userId, UserDto userDto) {
        User foundUser = getUserById(userId);
        String newEmail = userDto.getEmail();
        String newName = userDto.getName();

        if (newEmail != null) {
            foundUser.setEmail(newEmail);
        }
        if (newName != null) {
            foundUser.setName(newName);
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
