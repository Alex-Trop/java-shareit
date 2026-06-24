package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;

import java.util.HashMap;

@Component
public class InMemoryItemRequestStorage {
    private static final HashMap<Long, ItemRequest> allRequests = new HashMap<>();

    public ItemRequest getItemRequestById(long requestId) {
        return allRequests.get(requestId);
    }
}
