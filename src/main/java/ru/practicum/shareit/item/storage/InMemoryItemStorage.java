package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemStorage {
    private static final HashMap<Long, Item> allItems = new HashMap<>();
    private static long id = 0;

    public boolean checkDuplicates(Item item) {
        return allItems.containsValue(item);
    }

    public Item add(Item newItem) {
        id++;
        newItem.setId(id);
        allItems.put(id, newItem);
        log.info("Добавлена вещь: " + newItem);
        return newItem;
    }

    public Item update(long itemId, ItemDto itemDto) {
        Item foundItem = getItemById(itemId);

        if (itemDto.getName() != null) {
            foundItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            foundItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            foundItem.setAvailable(itemDto.getAvailable());
        }
        allItems.replace(itemId, foundItem);
        log.info("Обновлены сведения о вещи: " + foundItem);
        return foundItem;
    }

    public Item getItemById(long itemId) {
        return allItems.get(itemId);
    }

    public List<Item> getAllItems(long userId) {
        List<Item> userItems = allItems.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());

        log.info("У пользователя с id " + userId + "выставлено " + userItems.size() + " вещей");
        return userItems;
    }

    public List<Item> search(String text) {
        List<Item> foundItems = allItems.values().stream()
                .filter(item -> item.getAvailable() &&
                            (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getName().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());

        log.info("Найдено " + foundItems.size() + " вещей с подходящим названием/описанием");
        return foundItems;
    }
}