package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoShortInfo;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.exception.ValidationErrorDetails.BLANK_DESCRIPTION_ERROR;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    @NotNull
    private Long id;

    @NotBlank(message = BLANK_DESCRIPTION_ERROR)
    private String description;

    @NotNull
    private LocalDateTime created;

    List<ItemDtoShortInfo> items;
}
