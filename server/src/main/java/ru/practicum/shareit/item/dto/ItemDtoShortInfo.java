package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.exception.ErrorDetails.BLANK_NAME_ERROR;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoShortInfo {
    @NotNull
    Long id;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @NotNull
    private Long ownerId;
}
