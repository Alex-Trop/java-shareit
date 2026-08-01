package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentMapperImpl;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.exception.ErrorDetails.BOOKING_NOT_FOUND;
import static ru.practicum.shareit.exception.ErrorDetails.USER_NOT_FOUND;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
@Import({BookingServiceImpl.class, UserServiceImpl.class, ItemServiceImpl.class,
        BookingMapperImpl.class, UserMapperImpl.class, ItemMapperImpl.class,
        CommentMapperImpl.class})
public class BookingServiceIntegrationalTest extends BaseIntegrationalTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingMapper bookingMapper;
    private UserService userService;
    private ItemService itemService;
    private CommentMapper commentMapper;

    @Autowired
    public BookingServiceIntegrationalTest(BookingService bookingService, BookingRepository bookingRepository,
                                           UserRepository userRepository, ItemRepository itemRepository,
                                           BookingMapper bookingMapper, UserService userService,
                                           ItemService itemService, CommentMapper commentMapper) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.itemService = itemService;
        this.commentMapper = commentMapper;
    }

    private UserDto ownerDto = new UserDto(null,
            "Yuzuru Hanyu",
            "yuzuru@email.com");

    private UserDto bookerDto = new UserDto(
            null,
            "QPQPW CIC",
            "wsuodsa@mail.com");

    private ItemDto itemDto = new ItemDto(null,
            "GFHGJGJFHV",
            "gfbkjv zxlxckaxk",
            true,
            null);

    @Test
    void shouldAddBooking() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedBooking = bookingService.add(postDto, addedBookerDto.getId());

        assertNotNull(addedBooking.getId());
        assertEquals(addedBooking.getStart(), postDto.getStart());
        assertEquals(addedBooking.getEnd(), postDto.getEnd());
        assertEquals(addedBooking.getItem().getId(), addedItemDto.getId());
        assertEquals(addedBooking.getItem().getName(), addedItemDto.getName());
        assertEquals(addedBooking.getBooker().getId(), addedBookerDto.getId());
        assertEquals(addedBooking.getBookingStatus(), BookingStatus.WAITING);
    }

    @Test
    void shouldApproveBooking() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingDto approvedBooking = bookingService.approve(addedBooking.getId(), true,
                addedOwnerDto.getId());

        assertEquals(approvedBooking.getId(), addedBooking.getId());
        assertEquals(approvedBooking.getStart(), addedBooking.getStart());
        assertEquals(approvedBooking.getEnd(), addedBooking.getEnd());
        assertEquals(approvedBooking.getItem().getId(), addedBooking.getItem().getId());
        assertEquals(approvedBooking.getItem().getName(), addedBooking.getItem().getName());
        assertEquals(approvedBooking.getBooker().getId(), addedBooking.getBooker().getId());
        assertEquals(approvedBooking.getBookingStatus(), BookingStatus.APPROVED);
    }

    @Test
    void shouldGetUserBookingsByStatePast() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getUserBookings("past", addedBookerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 1);
        assertEquals(foundBookings.get(0).getId(), addedPastBooking.getId());
        assertEquals(foundBookings.get(0).getStart(), addedPastBooking.getStart());
        assertEquals(foundBookings.get(0).getEnd(), addedPastBooking.getEnd());
    }

    @Test
    void shouldGetUserBookingsByStateFuture() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getUserBookings("future", addedBookerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 1);
        assertEquals(foundBookings.get(0).getId(), addedFutureBooking.getId());
        assertEquals(foundBookings.get(0).getStart(), addedFutureBooking.getStart());
        assertEquals(foundBookings.get(0).getEnd(), addedFutureBooking.getEnd());
    }

    @Test
    void shouldGetEmptyListByStateRejected() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getUserBookings("rejected", addedBookerDto.getId(),
                LocalDateTime.now());

        assertTrue(foundBookings.isEmpty());
    }

    @Test
    void shouldThrowErrorWhenGetBookingsForNonExistentUser() {
        NotFoundError error = assertThrows(NotFoundError.class,
                () -> bookingService.getUserBookings("future", 15L,
                        LocalDateTime.now()));

        assertEquals(error.getMessage(), USER_NOT_FOUND);
    }

    @Test
    void shouldThrowErrorWhenGetBookingsForNonExistentOwner() {
        NotFoundError error = assertThrows(NotFoundError.class,
                () -> bookingService.getOwnerBookings("future", 15L,
                        LocalDateTime.now()));

        assertEquals(error.getMessage(), USER_NOT_FOUND);
    }

    @Test
    void shouldGetUserBookingsByDefaultState() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getUserBookings("ALL", addedBookerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 2);
    }

    @Test
    void shouldGetUserBookingsByStateWaiting() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getUserBookings("waiting", addedBookerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 2);
    }

    @Test
    void shouldGetOwnerBookingsByStatePast() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto anotherOwnerDto = new UserDto(
                null,
                "Aspspdpdp FHFCKJX",
                "sspsdp@mail.com"
        );
        UserDto anotherAddedOwnerDto = userService.add(anotherOwnerDto);
        UserDto addedBookerDto = userService.add(bookerDto);

        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());
        ItemDto anotherItemDto = new ItemDto(
                null,
                "pwslkalckx",
                "qqqqq",
                true,
                null
        );
        ItemDto anotherAddedItemDto = itemService.add(anotherItemDto, anotherAddedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                anotherAddedItemDto.getId(),
                LocalDateTime.of(2010, 12, 18, 12, 0),
                LocalDateTime.of(2010, 12, 23,12, 0));

        BookingDto anotherAddedPastBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getOwnerBookings("past",
                addedOwnerDto.getId(), LocalDateTime.now());

        assertEquals(foundBookings.size(), 1);
        assertEquals(foundBookings.get(0).getId(), addedPastBooking.getId());
        assertEquals(foundBookings.get(0).getStart(), addedPastBooking.getStart());
        assertEquals(foundBookings.get(0).getEnd(), addedPastBooking.getEnd());
    }

    @Test
    void shouldGetOwnerBookingsByStateFuture() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getOwnerBookings("future", addedOwnerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 1);
        assertEquals(foundBookings.get(0).getId(), addedFutureBooking.getId());
        assertEquals(foundBookings.get(0).getStart(), addedFutureBooking.getStart());
        assertEquals(foundBookings.get(0).getEnd(), addedFutureBooking.getEnd());
    }

    @Test
    void shouldGetOwnerBookingsByDefaultState() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getOwnerBookings("ALL", addedOwnerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 2);
    }

    @Test
    void shouldGetOwnerBookingsByStateWaiting() {
        UserDto addedOwnerDto = userService.add(ownerDto);
        UserDto addedBookerDto = userService.add(bookerDto);
        ItemDto addedItemDto = itemService.add(itemDto, addedOwnerDto.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedPastBooking = bookingService.add(postDto, addedBookerDto.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItemDto.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto addedFutureBooking = bookingService.add(anotherPostDto, addedBookerDto.getId());

        List<BookingDto> foundBookings = bookingService.getOwnerBookings("waiting", addedOwnerDto.getId(),
                LocalDateTime.now());

        assertEquals(foundBookings.size(), 2);
    }

    @Test
    void shouldThrowErrorWhenGetNonExistentBooking() {
        NotFoundError error = assertThrows(NotFoundError.class,
                () -> bookingService.getBooking(10L, 5L));

        assertEquals(error.getMessage(), BOOKING_NOT_FOUND);
    }
}
