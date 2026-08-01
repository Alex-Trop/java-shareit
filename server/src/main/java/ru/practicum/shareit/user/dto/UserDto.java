package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.exception.ErrorDetails.BLANK_NAME_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.EMAIL_FORMAT_ERROR;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @Email(message = EMAIL_FORMAT_ERROR)
    @NotBlank(message = EMAIL_FORMAT_ERROR)
    private String email;
}
