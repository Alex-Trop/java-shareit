package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.exception.ValidationErrorDetails.DATETIME_ERROR;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    @NotNull
    @Positive
    private long itemId;

    @NotNull(message = DATETIME_ERROR)
    private LocalDateTime start;

    @NotNull(message = DATETIME_ERROR)
    private LocalDateTime end;
}
