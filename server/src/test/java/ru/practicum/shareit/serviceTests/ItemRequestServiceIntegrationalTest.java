package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentMapperImpl;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestMapperImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static ru.practicum.shareit.exception.ErrorDetails.ITEM_REQUEST_NOT_FOUND;
import static ru.practicum.shareit.exception.ErrorDetails.REQUEST_DUPLICATE_ERROR;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({ItemRequestServiceImpl.class, UserServiceImpl.class, ItemServiceImpl.class,
        ItemRequestMapperImpl.class, UserMapperImpl.class, ItemMapperImpl.class,
        CommentMapperImpl.class})
public class ItemRequestServiceIntegrationalTest extends BaseIntegrationalTest {
    private ItemRequestService requestService;
    private ItemRequestRepository requestRepository;
    private ItemRepository itemRepository;
    private ItemService itemService;
    private UserService userService;
    private UserRepository userRepository;
    private ItemRequestMapper requestMapper;
    private ItemMapper itemMapper;
    private UserMapper userMapper;
    private CommentMapper commentMapper;

    @Autowired
    public ItemRequestServiceIntegrationalTest(ItemRequestService requestService,
                                               ItemRequestRepository requestRepository,
                                               ItemRepository itemRepository,
                                               ItemService itemService,
                                               UserService userService,
                                               UserRepository userRepository,
                                               ItemRequestMapper requestMapper,
                                               ItemMapper itemMapper,
                                               UserMapper userMapper,
                                               CommentMapper commentMapper) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
    }

    private UserDto ownerDto = new UserDto(null,
            "Yuzuru Hanyu",
            "yuzuru@email.com");

    private UserDto requestorDto = new UserDto(null,
            "SDGGA dlkvldklckx",
            "sdkkjsjf11@gmail.com");

    @Test
    void shouldAddRequest() {
        UserDto requestor = userService.add(requestorDto);
        ItemRequestDto addedRequest = requestService.add(requestor.getId(), new ItemRequestPostDto("fjhgfdjhjdgh"));

        assertNotNull(addedRequest.getId());
        assertEquals(addedRequest.getDescription(), "fjhgfdjhjdgh");
        assertNotNull(addedRequest.getCreated());
    }

    @Test
    void shouldThrowErrorWhenAddingDuplicate() {
        UserDto requestor = userService.add(requestorDto);
        ItemRequestPostDto postDto = new ItemRequestPostDto("fjhgfdjhjdgh");
        ItemRequestDto addedRequest = requestService.add(requestor.getId(), postDto);

        ResourceAlreadyExistsError error = assertThrows(ResourceAlreadyExistsError.class,
                () -> requestService.add(requestor.getId(), postDto));

        assertEquals(error.getMessage(), REQUEST_DUPLICATE_ERROR);
    }

    @Test
    void shouldGetAllRequests() {
        UserDto requestor = userService.add(requestorDto);
        ItemRequestPostDto postDto = new ItemRequestPostDto("fjhgfdjhjdgh");
        ItemRequestDto addedRequest = requestService.add(requestor.getId(), postDto);
        ItemRequestPostDto anotherPostDto = new ItemRequestPostDto("xxxx");
        ItemRequestDto anotherAddedRequest = requestService.add(requestor.getId(), anotherPostDto);

        List<ItemRequestDto> foundRequests = requestService.getAllRequests();

        assertEquals(foundRequests.size(), 2);
    }

    @Test
    void shouldGetUserRequests() {
        UserDto requestor = userService.add(requestorDto);
        ItemRequestPostDto postDto = new ItemRequestPostDto("fjhgfdjhjdgh");
        ItemRequestDto addedRequest = requestService.add(requestor.getId(), postDto);

        UserDto anotherRequestor = userService.add(new UserDto(null, "PPPPP", "djfh@mail.com"));
        ItemRequestPostDto anotherPostDto = new ItemRequestPostDto("xxxx");
        ItemRequestDto anotherAddedRequest = requestService.add(anotherRequestor.getId(), anotherPostDto);

        List<ItemRequestDto> foundRequests = requestService.getUserRequests(requestor.getId());

        assertEquals(foundRequests.get(0).getId(), addedRequest.getId());
        assertEquals(foundRequests.get(0).getDescription(), addedRequest.getDescription());
    }

    @Test
    void shouldGetEmptyListWhenNoRequests() {
        List<ItemRequestDto> foundRequests = requestService.getAllRequests();

        assertTrue(foundRequests.isEmpty());
    }

    @Test
    void shouldGetRequest() {
        UserDto requestor = userService.add(requestorDto);
        ItemRequestPostDto postDto = new ItemRequestPostDto("fjhgfdjhjdgh");
        ItemRequestDto addedRequest = requestService.add(requestor.getId(), postDto);

        UserDto anotherRequestor = userService.add(new UserDto(null, "PPPPP", "djfh@mail.com"));
        ItemRequestPostDto anotherPostDto = new ItemRequestPostDto("xxxx");
        ItemRequestDto anotherAddedRequest = requestService.add(anotherRequestor.getId(), anotherPostDto);

        ItemRequestDto foundRequest = requestService.getRequestById(addedRequest.getId());

        assertEquals(foundRequest.getDescription(), addedRequest.getDescription());
        assertEquals(foundRequest.getCreated(), addedRequest.getCreated());
    }

    @Test
    void shouldThrowErrorWhenGettingNonExistingRequest() {
        UserDto requestor = userService.add(requestorDto);
        ItemRequestPostDto postDto = new ItemRequestPostDto("fjhgfdjhjdgh");
        ItemRequestDto addedRequest = requestService.add(requestor.getId(), postDto);

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> requestService.getRequestById(addedRequest.getId() + 1));

        assertEquals(error.getMessage(), ITEM_REQUEST_NOT_FOUND);
    }
}
