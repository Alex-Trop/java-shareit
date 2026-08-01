package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(long userId, ItemRequestPostDto requestDto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests();

    ItemRequestDto getRequestById(long requestId);
}
