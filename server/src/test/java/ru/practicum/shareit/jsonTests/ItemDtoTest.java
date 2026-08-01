package ru.practicum.shareit.jsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JsonMapper mapper;

    private ItemDto itemDto = new ItemDto(
            1L,
            "gjhfR",
            "gfhjhjjghfjhg322d",
            true,
            2L);

    @Test
    void shouldTransformWithJsonProperty() {
        String json = mapper.writeValueAsString(itemDto);

        assertTrue(json.contains("\"requestId\":"));

        ItemDto object = mapper.readValue(json, ItemDto.class);

        assertEquals(object.getItemRequestId(), 2);
    }
}
