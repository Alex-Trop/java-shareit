package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.exception.ErrorDetails.DATETIME_ERROR;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class BookingDto {
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
    @JsonProperty("status")
    BookingStatus bookingStatus;
}
