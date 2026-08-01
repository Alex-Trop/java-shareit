package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.shareit.exception.ErrorDetails.BLANK_NAME_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.BLANK_TEXT_ERROR;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = BLANK_TEXT_ERROR)
    private String text;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String authorName;

    @NotNull
    private LocalDateTime created;
}
