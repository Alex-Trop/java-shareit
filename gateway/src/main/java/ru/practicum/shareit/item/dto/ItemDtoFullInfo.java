package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

import static ru.practicum.shareit.exception.ValidationErrorDetails.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoFullInfo {
    //и бронирования, и комментарии - для владельца по get items
    private Long id;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @NotBlank(message = BLANK_DESCRIPTION_ERROR)
    private String description;

    @NotNull(message = AVAILABILITY_ERROR)
    private Boolean available;

    private Long itemRequestId;

    private BookingResponseDto lastBooking;

    private BookingResponseDto nextBooking;

    List<CommentDto> comments;
}
