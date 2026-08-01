package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.exception.ErrorDetails.USER_DUPLICATE_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.USER_NOT_FOUND;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({UserServiceImpl.class, UserMapperImpl.class})
public class UserServiceIntegrationalTest extends BaseIntegrationalTest {
    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;

    @Autowired
    public UserServiceIntegrationalTest(UserService userService, UserRepository userRepository,
                                        UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    private UserDto userDto = new UserDto(1L,
            "Yuzuru Hanyu",
            "yuzuru@email.com");

    @Test
    void shouldSaveUser() {
        UserDto addedUser = userService.add(userDto);

        assertNotNull(addedUser.getId(), "ID не может быть пуст");
        assertEquals(addedUser.getName(), userDto.getName(), "Имена не совпадают");
        assertEquals(addedUser.getEmail(), userDto.getEmail(), "Email не совпадают");
    }

    @Test
    void shouldThrowErrorWhenAddingUserWithExistingEmail() {
        UserDto duplicateUserDto = new UserDto(2L, "JGHFHG", "yuzuru@email.com");

        UserDto existingUser = userService.add(userDto);

        ResourceAlreadyExistsError error = assertThrows(ResourceAlreadyExistsError.class,
                () -> userService.add(duplicateUserDto));

        assertEquals(error.getMessage(), USER_DUPLICATE_ERROR);
    }

    @Test
    void shouldUpdateUser() {
        UserDto addedUser = userService.add(userDto);
        UserDto newUser = new UserDto(3L,"JGHFHG", "newEmail@email.com");
        UserDto updatedUser = userService.update(addedUser.getId(), newUser);

        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals(newUser.getName(), updatedUser.getName());
        assertEquals(newUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void shouldThrowErrorWhenUpdatingNonExistentUser() {
        UserDto newUser = new UserDto(3L,"JGHFHG", "newEmail@email.com");

        userService.add(userDto);

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> userService.update(18L, newUser));

        assertEquals(error.getMessage(), USER_NOT_FOUND);
    }

    @Test
    void shouldGetUser() {
        UserDto addedUser = userService.add(userDto);

        UserDto foundUser = userService.get(addedUser.getId());

        assertEquals(addedUser.getId(), foundUser.getId());
        assertEquals(userDto.getName(), foundUser.getName());
        assertEquals(userDto.getEmail(), foundUser.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        UserDto addedUser = userService.add(userDto);

        assertNotNull(addedUser.getId(), "ID не может быть пуст");
        assertEquals(addedUser.getName(), userDto.getName(), "Имена не совпадают");
        assertEquals(addedUser.getEmail(), userDto.getEmail(), "Email не совпадают");

        userService.delete(addedUser.getId());

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> userService.get(addedUser.getId()));

        assertEquals(error.getMessage(), USER_NOT_FOUND);
    }
}
