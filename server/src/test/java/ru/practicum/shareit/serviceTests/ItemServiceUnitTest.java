package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.exception.ErrorDetails.ITEM_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @InjectMocks
    private ItemServiceImpl service;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper mapper;

    @Test
    void shouldReturnEmptyList() {
        List<ItemDto> foundList = service.search("");

        assertTrue(foundList.isEmpty());
    }

    @Test
    void shouldThrowErrorWhenNoItem() {
        NotFoundError error = assertThrows(NotFoundError.class,
                () -> service.getItem(2L));

        assertEquals(error.getMessage(), ITEM_NOT_FOUND);
    }
}
