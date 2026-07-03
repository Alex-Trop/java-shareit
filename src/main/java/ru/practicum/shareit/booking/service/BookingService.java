package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingPostDto bookingPostDto, long userId);

    BookingDto approve(long bookingId, boolean approved, long userId);

    BookingDto getBooking(long bookingId, long userId);

    List<BookingDto> getUserBookings(String state, long userId);

    List<BookingDto> getOwnerBookings(String state, long userId);
}
