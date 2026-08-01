package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.exception.ErrorDetails.INVALID_PARAMETER;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldThrowErrorWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(NotFoundError.class);

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> bookingService.getBooking(1L, 2L));
    }

    @Test
    void shouldThrowErrorWhenInvalidParameter() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> bookingService.getUserBookings("wrong", 1L, LocalDateTime.now()));

        assertEquals(e.getMessage(), INVALID_PARAMETER);

        RuntimeException exc = assertThrows(RuntimeException.class,
                () -> bookingService.getOwnerBookings("wrong", 1L, LocalDateTime.now()));

        assertEquals(exc.getMessage(), INVALID_PARAMETER);
    }
}
