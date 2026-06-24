package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.ItemRequest;

import static ru.practicum.shareit.exception.ErrorDetails.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
public class Item {
    private long id;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @NotBlank(message = BLANK_DESCRIPTION_ERROR)
    private String description;

    @NotNull(message = AVAILABILITY_ERROR)
    private Boolean available;

    @Positive(message = USER_NOT_FOUND)
    private long owner;

    private ItemRequest request;
}
