package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    default ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() == null ? 0 : item.getRequest().getId()
        );
    }

    default ItemDtoFullInfo toItemDtoFullInfo(Item item, Optional<Booking> latest, Optional<Booking> next, List<CommentDto> comments) {
        return new ItemDtoFullInfo(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() == null ? 0 : item.getRequest().getId(),
                latest.orElse(null),
                next.orElse(null),
                comments
        );
    }

    default ItemDtoWithComment toItemDtoWithComment(Item item, List<CommentDto> comments) {
        return new ItemDtoWithComment(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() == null ? 0 : item.getRequest().getId(),
                comments
        );
    }

    default ItemDtoShortInfo toItemDtoShortInfo(Item item) {
        return new ItemDtoShortInfo(item.getId(),
                item.getName(),
                item.getOwner());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateItemFromItemDto(ItemDto itemDto, @MappingTarget Item item);
}
