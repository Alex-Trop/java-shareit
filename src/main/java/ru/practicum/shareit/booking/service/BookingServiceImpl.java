package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorDetails.*;

@AllArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private static final long TMP_ID = 0;
    private static final long POSTMAN_TEST_PAUSE = 10;

    public BookingDto add(BookingPostDto bookingPostDto, long userId) {
        if (bookingPostDto.getStart().isBefore(LocalDateTime.now().minusSeconds(POSTMAN_TEST_PAUSE))) {
            throw new RuntimeException(DATETIME_ERROR);
        }

        log.info("Получен запрос на бронирование вещи " + bookingPostDto + "пользователем " + userId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError(USER_NOT_FOUND));
        Item foundItem = itemRepository.findById(bookingPostDto.getItemId())
                .orElseThrow(() -> new NotFoundError(ITEM_NOT_FOUND));

        if (!foundItem.getAvailable()) {
            throw new RuntimeException(UNAVAILABLE_ITEM_ERROR);
        }

        Booking bookingDraft = new Booking(
                TMP_ID,
                bookingPostDto.getStart(),
                bookingPostDto.getEnd(),
                foundItem,
                booker,
                BookingStatus.WAITING);

        return bookingMapper.toBookingDto(bookingRepository.save(bookingDraft));
    }

    public BookingDto approve(long bookingId, boolean approved, long userId) {
        Booking foundBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundError(BOOKING_NOT_FOUND));

        if (foundBooking.getItem().getOwner() != userId) {
            throw new RuntimeException(WRONG_OWNER);
        }

        foundBooking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(foundBooking));
    }

    public BookingDto getBooking(long bookingId, long userId) {
        Booking foundBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundError(BOOKING_NOT_FOUND));

        Long bookerId = foundBooking.getBooker().getId();
        Long ownerId = foundBooking.getItem().getOwner();

        if (userId != bookerId && userId != ownerId) {
            throw new NotFoundError(ACCESS_DENIED);
        }
        return bookingMapper.toBookingDto(foundBooking);
    }

    public List<BookingDto> getUserBookings(String stateParam, long userId) {
        BookingState state = BookingState.of(stateParam)
                .orElseThrow(() -> new RuntimeException(INVALID_PARAMETER));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundError(USER_NOT_FOUND);
        }

        List<Booking> userBookings = new ArrayList<>();

        switch (state) {
            case ALL -> userBookings = bookingRepository.findAllByBookerOrderByStartDesc(userId);
            case PAST -> userBookings = bookingRepository.getPastBookings(userId, LocalDateTime.now());
            case FUTURE -> userBookings = bookingRepository.getFutureBookings(userId, LocalDateTime.now());
            case CURRENT -> userBookings = bookingRepository.getCurrentBookings(userId, LocalDateTime.now());
            case WAITING -> userBookings = bookingRepository.findAllByBookerAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> userBookings = bookingRepository.findAllByBookerAndBookingStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        }

        return userBookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getOwnerBookings(String stateParam, long userId) {
        BookingState state = BookingState.of(stateParam)
                .orElseThrow(() -> new RuntimeException(INVALID_PARAMETER));

        if (!itemRepository.existsByOwner(userId)) {
            throw new NotFoundError(USER_NOT_FOUND);
        }

        List<Booking> userBookings = new ArrayList<>();

        switch (state) {
            case ALL -> userBookings = bookingRepository.getOwnerAllBookings(userId);
            case CURRENT -> userBookings = bookingRepository.getOwnerCurrentBookings(userId, LocalDateTime.now());
            case PAST -> userBookings = bookingRepository.getOwnerPastBookings(userId, LocalDateTime.now());
            case FUTURE -> userBookings = bookingRepository.getOwnerFutureBookings(userId, LocalDateTime.now());
            case WAITING -> userBookings = bookingRepository.getOwnerAllBookingsByBookingStatus(userId, BookingStatus.WAITING);
            case REJECTED -> userBookings = bookingRepository.getOwnerAllBookingsByBookingStatus(userId, BookingStatus.REJECTED);
        }

        return userBookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
