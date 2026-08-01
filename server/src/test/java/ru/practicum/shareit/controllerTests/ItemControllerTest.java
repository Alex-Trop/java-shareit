package ru.practicum.shareit.controllerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFullInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    JsonMapper mapper;

    @MockitoBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto = new ItemDto(
            1L,
            "gjhfR",
            "gfhjhjjghfjhg322d",
            true,
            null);

    private ItemDto anotherItemDto = new ItemDto(
            2L,
            "gjhfRSSSSSS",
            "gfhjhjjghfjhg322d",
            true,
            null);

    private User user = new User(
            1L,
            "Yuzuru Hanyu",
            "yuzuru@email.com");

    private ItemRequest request = new ItemRequest(
            1L,
            "bkjgkjhfh",
            user,
            LocalDateTime.now());

    private Item item = new Item(
            "gjhfR",
            "gfhjhjjghfjhg322d",
            true,
            1L,
            request);

    private Booking lastBooking = new Booking(
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(1),
            item,
            user,
            BookingStatus.APPROVED
            );

    private Booking nextBooking = new Booking(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusMinutes(1),
            item,
            user,
            BookingStatus.APPROVED);

    CommentDto commentDto = new CommentDto(
            1L,
            "gjhfdkjghdkfjghkfdjg",
            "Ubkfngbgjbn",
            LocalDateTime.now().minusWeeks(1));

    private ItemDtoFullInfo fullItemDto = new ItemDtoFullInfo(1L,
            "gjhfR",
            "gfhjhjjghfjhg322d",
            true,
            request.getId(),
            lastBooking,
            nextBooking,
            List.of(commentDto));

    private ItemDtoFullInfo anotherFullItemDto = new ItemDtoFullInfo(2L,
            "fdgjdlffffffffffffffffffffff",
            "gfhjhjjghfjhg322d",
            true,
            request.getId(),
            lastBooking,
            nextBooking,
            List.of(commentDto));

    private CommentPostDto commentPostDto = new CommentPostDto("kjdfgkjdfkgjdfkdjg");

    private final String HEADER = "X-Sharer-User-Id";

    @Test
    void shouldAddItem() throws Exception {
        when(itemService.add(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getItemRequestId()));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/" + itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getItemRequestId()));
    }

    @Test
    void shouldGetItem() throws Exception {
        when(itemService.getItem(anyLong()))
                .thenReturn(fullItemDto);

        mvc.perform(get("/items/" + itemDto.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fullItemDto.getId()))
                .andExpect(jsonPath("$.name").value(fullItemDto.getName()))
                .andExpect(jsonPath("$.description").value(fullItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(fullItemDto.getAvailable()))
                .andExpect(jsonPath("$.itemRequestId").value(fullItemDto.getItemRequestId()))
                .andExpect(jsonPath("$.lastBooking.id").value(lastBooking.getId()))
                .andExpect(jsonPath("$.nextBooking.id").value(nextBooking.getId()))
                .andExpect(jsonPath("$.comments.length()").value(1));
    }

    @Test
    void shouldGetAllItems() throws Exception {
        when(itemService.getAllItems(anyLong()))
                .thenReturn(List.of(fullItemDto, anotherFullItemDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(fullItemDto.getId()))
                .andExpect(jsonPath("$[1].id").value(anotherFullItemDto.getId()));
    }

    @Test
    void shouldAddComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                .content(mapper.writeValueAsString(commentPostDto))
                .header(HEADER, 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }

    @Test
    void shouldSearchText() throws Exception {
        when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto, anotherItemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .queryParam("text", "gjhgjfhg")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                        .andExpect(jsonPath("$[1].id").value(anotherItemDto.getId()));
    }
}
