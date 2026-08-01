package ru.practicum.shareit.controllerTests;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    JsonMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BookingService bookingService;

    private BookingPostDto postDto = new BookingPostDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(1));

    private ItemDto itemDto = new ItemDto(
            1L,
            "gjhfR",
            "gfhjhjjghfjhg322d",
            true,
            null);

    private UserDto userDto = new UserDto(1L,
            "Yuzuru Hanyu",
            "yuzuru@email.com");

    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(1),
            itemDto,
            userDto,
            BookingStatus.WAITING);

    private BookingDto anotherBookingDto = new BookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusMinutes(1),
            itemDto,
            userDto,
            BookingStatus.WAITING);

    private final String header = "X-Sharer-User-Id";

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void shouldAddBooking() throws Exception {
        when(bookingService.add(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(postDto))
                .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, userDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.status").value(bookingDto.getBookingStatus().toString()));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                .header(header, userDto.getId())
                        .queryParam("approved", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.status").value(bookingDto.getBookingStatus().toString()));
    }

    @Test
    void shouldGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.status").value(bookingDto.getBookingStatus().toString()));
    }

    @Test
    void shouldGetUserBookingsWithDefaultState() throws Exception {
        when(bookingService.getUserBookings(anyString(), anyLong(), any()))
                .thenReturn(List.of(bookingDto, anotherBookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[1].id").value(anotherBookingDto.getId()));
    }

    @Test
    void shouldGetOwnerBookingsWithDefaultState() throws Exception {
        when(bookingService.getOwnerBookings(anyString(), anyLong(), any()))
                .thenReturn(List.of(bookingDto, anotherBookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[1].id").value(anotherBookingDto.getId()));
    }
}
