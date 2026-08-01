package ru.practicum.shareit.controllerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoShortInfo;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    JsonMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ItemRequestService requestService;

    private ItemRequestPostDto postDto = new ItemRequestPostDto("kfgjhfhgdkfjghkdfhg");

    private ItemDtoShortInfo itemShortInfo = new ItemDtoShortInfo(
            1L,
            "BGFFHGFHGF",
            2L);

    private ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "bkfjhgjhf",
            LocalDateTime.now(),
            List.of(itemShortInfo));

    private final String HEADER = "X-Sharer-User-Id";

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void shouldAddRequest() throws Exception {
        when(requestService.add(anyLong(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(postDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(formatter)))
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        when(requestService.getUserRequests(anyLong()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        when(requestService.getAllRequests())
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()));
    }

    @Test
    void shouldGetRequest() throws Exception {
        when(requestService.getRequestById(anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(formatter)))
                .andExpect(jsonPath("$.items.length()").value(1));
    }
}
