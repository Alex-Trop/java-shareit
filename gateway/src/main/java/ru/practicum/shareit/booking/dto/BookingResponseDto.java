package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.exception.ValidationErrorDetails.DATETIME_ERROR;

@Data
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;

    @NotNull(message = DATETIME_ERROR)
    private LocalDateTime start;

    @NotNull(message = DATETIME_ERROR)
    private LocalDateTime end;

    @NotNull
    private ItemDto item;

    @NotNull
    private UserDto booker;

    @NotNull
    BookingStatus status;
}
