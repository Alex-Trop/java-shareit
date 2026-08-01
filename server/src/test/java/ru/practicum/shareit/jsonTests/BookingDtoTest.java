package ru.practicum.shareit.jsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JsonMapper mapper;

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

    @Test
    void shouldTransformWithJsonProperty() {
        String json = mapper.writeValueAsString(bookingDto);

        assertTrue(json.contains("\"status\":"));

        BookingDto object = mapper.readValue(json, BookingDto.class);

        assertEquals(object.getBookingStatus(), BookingStatus.WAITING);
    }
}
