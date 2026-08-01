package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDtoShortInfo;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    default ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemDtoShortInfo> items) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                items);
    }
}
