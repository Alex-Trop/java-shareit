package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.shareit.exception.ErrorDetails.DATETIME_ERROR;

@Data
@AllArgsConstructor
public class BookingPostDto {
    @NotNull
    @Positive
    private long itemId;

    @NotNull(message = DATETIME_ERROR)
    private LocalDateTime start;

    @NotNull(message = DATETIME_ERROR)
    private LocalDateTime end;
}
