package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentMapperImpl;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFullInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.exception.ErrorDetails.ITEM_DUPLICATE_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.ITEM_NOT_FOUND;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Import({ItemServiceImpl.class, ItemMapperImpl.class, CommentMapperImpl.class, UserServiceImpl.class,
BookingServiceImpl.class, UserMapperImpl.class, BookingMapperImpl.class})
public class ItemServiceIntegrationalTest extends BaseIntegrationalTest {
    private ItemService itemService;
    private UserService userService;
    private BookingService bookingService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;

    @Autowired
    public ItemServiceIntegrationalTest(UserService userService,
                                        ItemService itemService,
                                        BookingService bookingService,
                                         ItemRepository itemRepository,
                                        UserRepository userRepository,
                                        ItemRequestRepository itemRequestRepository,
                                        BookingRepository bookingRepository,
                                        CommentRepository commentRepository,
                                        ItemMapper itemMapper,
                                        CommentMapper commentMapper) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    private ItemDto itemDto = new ItemDto(null,
            "GFHGJGJFHV",
            "gfbkjv zxlxckaxk",
            true,
            null);

    private UserDto userDto = new UserDto(null,
            "Yuzuru Hanyu",
            "yuzuru@email.com");

    @Test
    void shouldAddItem() {
        UserDto addedUser = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedUser.getId());

        assertNotNull(addedItem.getId());
        assertEquals(addedItem.getName(), itemDto.getName());
        assertEquals(addedItem.getDescription(), itemDto.getDescription());
        assertEquals(addedItem.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void shouldThrowErrorWhenAddingItemDuplicate() {
        UserDto addedUser = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedUser.getId());

        ResourceAlreadyExistsError error = assertThrows(ResourceAlreadyExistsError.class,
                () -> itemService.add(itemDto, addedUser.getId()));

        assertEquals(error.getMessage(), ITEM_DUPLICATE_ERROR);
    }

    @Test
    void shouldUpdateItem() {
        UserDto addedUser = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedUser.getId());
        ItemDto newItem = new ItemDto(
                null,
                "pwpqpqpq",
                "spspspsps",
                true,
                null);
        ItemDto updatedItem = itemService.update(addedItem.getId(), newItem, addedUser.getId());

        assertNotNull(updatedItem.getId());
        assertEquals(updatedItem.getName(), newItem.getName());
        assertEquals(updatedItem.getDescription(), newItem.getDescription());
        assertEquals(updatedItem.getAvailable(), newItem.getAvailable());
    }

    @Test
    void shouldThrowErrorWhenUpdatingNonExistentItem() {
        UserDto addedUser = userService.add(userDto);

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> itemService.update(17L, itemDto, addedUser.getId()));

        assertEquals(error.getMessage(), ITEM_NOT_FOUND);
    }

    @Test
    void shouldGetItem() {
        UserDto addedUser = userService.add(userDto);

        ItemDto addedItem = itemService.add(itemDto, addedUser.getId());

        ItemDtoFullInfo fullItem = itemService.getItem(addedItem.getId());

        assertEquals(fullItem.getName(), addedItem.getName());
        assertEquals(fullItem.getDescription(), addedItem.getDescription());
        assertEquals(fullItem.getAvailable(), addedItem.getAvailable());
    }

    @Test
    void shouldGetAllItems() {
        UserDto addedUser = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedUser.getId());
        ItemDto newItem = new ItemDto(
                null,
                "pwpqpqpq",
                "spspspsps",
                true,
                null);
        ItemDto anotherAddedItem = itemService.add(newItem, addedUser.getId());
        List<ItemDtoFullInfo> foundItems = itemService.getAllItems(addedUser.getId());

        assertEquals(foundItems.size(), 2);
    }

    @Test
    void shouldGetAllItemsWithBookings() {
        UserDto addedOwner = userService.add(userDto);
        UserDto bookerDto = new UserDto(
                null,
                "QPQPW CIC",
                "wsuodsa@mail.com");
        UserDto addedBooker = userService.add(bookerDto);
        ItemDto addedItem = itemService.add(itemDto, addedOwner.getId());
        ItemDto newItem = new ItemDto(
                null,
                "pwpqpqpq",
                "spspspsps",
                true,
                null);
        ItemDto anotherAddedItem = itemService.add(newItem, addedOwner.getId());

        BookingPostDto postDto = new BookingPostDto(
                addedItem.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));

        BookingDto addedBooking = bookingService.add(postDto, addedBooker.getId());

        BookingPostDto anotherPostDto = new BookingPostDto(
                addedItem.getId(),
                LocalDateTime.of(2030, 12, 18, 12, 0),
                LocalDateTime.of(2030, 12, 23,12, 0));

        BookingDto anotherAddedBooking = bookingService.add(anotherPostDto, addedBooker.getId());

        List<ItemDtoFullInfo> foundItems = itemService.getAllItems(addedOwner.getId());

        assertTrue(foundItems.size() == 2);
    }

    @Test
    void shouldSearchText() {
        UserDto addedUser = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedUser.getId());

        List<ItemDto> foundItems = itemService.search("GJFHV".toLowerCase());

        assertEquals(foundItems.size(), 1);
        assertEquals(foundItems.get(0).getName(), itemDto.getName());
        assertEquals(foundItems.get(0).getDescription(), itemDto.getDescription());
    }

    @Test
    void shouldAddComment() {
        UserDto addedOwner = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedOwner.getId());
        UserDto newBooker = new UserDto(
                null,
                "QPQPW CIC",
                "wsuodsa@mail.com");
        UserDto addedBooker = userService.add(newBooker);
        Item item = new Item(
                addedItem.getId(),
               addedItem.getName(),
                addedItem.getDescription(),
                true,
                addedOwner.getId(),
                null);
        User booker = new User(
                addedBooker.getId(),
                addedBooker.getName(),
                addedBooker.getEmail());
        BookingPostDto bookingPostDto = new BookingPostDto(
                item.getId(),
                LocalDateTime.of(2000, 12, 18, 12, 0),
                LocalDateTime.of(2000, 12, 23,12, 0));
        BookingDto addedBooking = bookingService.add(bookingPostDto, booker.getId());
        CommentPostDto commentDto = new CommentPostDto("qqqqqqqq");

        CommentDto addedComment = itemService.addComment(commentDto, item.getId(),
                booker.getId(),
                LocalDateTime.of(2026, 10, 18, 13, 0));

        assertNotNull(addedComment.getId());
        assertEquals(addedComment.getText(), commentDto.getText());
        assertEquals(addedComment.getAuthorName(), booker.getName());
        assertNotNull(addedComment.getCreated());
    }
}
