package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.exception.ValidationErrorDetails.BLANK_DESCRIPTION_ERROR;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestPostDto {
    @NotBlank(message = BLANK_DESCRIPTION_ERROR)
    private String description;
}
