package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(long itemId, ItemDto itemDto, long userId);

    ItemDtoFullInfo getItem(long itemId);

    List<ItemDtoFullInfo> getAllItems(long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(CommentPostDto commentPostDto, long itemId, long userId, LocalDateTime now);

    List<Item> searchItems(String description);
}
