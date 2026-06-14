package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(long itemId, ItemDto itemDto, long userId);

    ItemDto getItem(long itemId);

    List<ItemDto> getAllItems(long userId);

    List<ItemDto> search(String text);
}
