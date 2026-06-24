package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    @NotNull
    long id;

    @NotBlank
    String description;

    @NotNull
    User requestor;

    @NotNull
    LocalDateTime created;
}
