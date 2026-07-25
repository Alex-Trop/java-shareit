package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static ru.practicum.shareit.exception.ErrorDetails.*;

@Data
@AllArgsConstructor
public class ItemDtoWithComment {
    private Long id;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @NotBlank(message = BLANK_DESCRIPTION_ERROR)
    private String description;

    @NotNull(message = AVAILABILITY_ERROR)
    private Boolean available;

    private long itemRequestId;

    List<CommentDto> comments;
}
