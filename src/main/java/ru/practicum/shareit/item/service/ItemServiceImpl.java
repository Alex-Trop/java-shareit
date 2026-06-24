package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.request.storage.InMemoryItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorDetails.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage itemStorage;
    private final InMemoryUserStorage userStorage;
    private final InMemoryItemRequestStorage itemRequestStorage;
    private static final long TMP_ID = 0;

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        log.info("Получен запрос на добавление вещи " + itemDto + "пользователем " + userId);

        User foundUser = userStorage.getUserById(userId);

        if (foundUser == null) {
            throw new NotFoundError(USER_NOT_FOUND);
        }

        Item newItem = new Item(
                TMP_ID,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                itemDto.getItemRequestId() == 0 ? null : itemRequestStorage.getItemRequestById(itemDto.getItemRequestId()));

        if (itemStorage.checkDuplicates(newItem)) {
            throw new ResourceAlreadyExistsError(ITEM_DUPLICATE_ERROR);
        }
        return ItemMapper.toItemDto(itemStorage.add(newItem));
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) {
        log.info("Получен запрос на обновление сведений о вещи " + itemDto + " с Id " + itemId
                + " пользователем " + userId);

        User foundUser = userStorage.getUserById(userId);

        if (foundUser == null) {
            throw new NotFoundError(USER_NOT_FOUND);
        }

        Item foundItem = itemStorage.getItemById(itemId);

        if (foundItem == null) {
            throw new NotFoundError(ITEM_NOT_FOUND);
        }
        if (foundItem.getOwner() != userId) {
            throw new NotFoundError(WRONG_OWNER);
        }
        return ItemMapper.toItemDto(itemStorage.update(itemId, itemDto));
    }

    @Override
    public ItemDto getItem(long itemId) {
        log.info("Получен запрос на получение сведений о вещи " + itemId);

        Item foundItem = itemStorage.getItemById(itemId);

        if (foundItem == null) {
            throw new NotFoundError(ITEM_NOT_FOUND);
        }
        return ItemMapper.toItemDto(foundItem);
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        List<Item> foundItems = itemStorage.getAllItems(userId);

        return foundItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Получен запрос на поиск вещей, где в описании/имени след. текст: " + text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
