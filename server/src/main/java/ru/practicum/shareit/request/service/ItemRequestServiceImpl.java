package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoShortInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorDetails.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;
    private final ItemMapper itemMapper;


    @Override
    public ItemRequestDto add(long userId, ItemRequestPostDto requestDto) {
        log.info("Получен запрос на добавление запроса " + requestDto + "пользователем " + userId);

        LocalDateTime now = LocalDateTime.now();

        if (requestRepository.existsByDescriptionAndRequestorId(requestDto.getDescription(), userId)) {
            throw new ResourceAlreadyExistsError(REQUEST_DUPLICATE_ERROR);
        }

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError(USER_NOT_FOUND));
        ItemRequest request = new ItemRequest(requestDto.getDescription(),
                requestor,
                now);
        List<ItemDtoShortInfo> items = itemService.searchItems(requestDto.getDescription())
                .stream()
                .map(itemMapper::toItemDtoShortInfo)
                .collect(Collectors.toList());

        return requestMapper.toItemRequestDto(requestRepository.save(request), items);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        log.info("Получен запрос на получение списка всех запросов пользователя {}", userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError(USER_NOT_FOUND));
        List<ItemRequest> allRequestsByRequestor = requestRepository.findAllByRequestor(userId);

        return makeListOfItemRequestDto(allRequestsByRequestor);
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        List<ItemRequest> allItemRequests = requestRepository.findAllOrderByCreatedDesc();

        return makeListOfItemRequestDto(allItemRequests);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundError(ITEM_REQUEST_NOT_FOUND));
        List<Item> itemsOnRequest = itemRepository.findAllByRequestId(requestId);
        List<ItemDtoShortInfo> itemsOnRequestShort = new ArrayList<>();

        if (!itemsOnRequest.isEmpty()) {
            for (Item item : itemsOnRequest) {
                itemsOnRequestShort.add(itemMapper.toItemDtoShortInfo(item));
            }
        }
        return requestMapper.toItemRequestDto(request, itemsOnRequestShort);
    }

    private List<ItemRequestDto> makeListOfItemRequestDto(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> requestIdSet = new HashSet<>();

        for (ItemRequest request : itemRequests) {
            requestIdSet.add(request.getId());
        }

        List<Item> itemsOnRequests = itemRepository.findAllByRequestIdIn(requestIdSet);
        List<ItemRequestDto> allRequestsWithItems = new ArrayList<>();

        for (ItemRequest request : itemRequests) {
            List<ItemDtoShortInfo> itemsOnRequest = new ArrayList<>();

            for (Item item : itemsOnRequests) {
                if (request.getId().equals(item.getRequest().getId())) {
                    itemsOnRequest.add(new ItemDtoShortInfo(
                            item.getId(),
                            item.getDescription(),
                            item.getOwner()));
                }
            }
            allRequestsWithItems.add(requestMapper.toItemRequestDto(request, itemsOnRequest));
        }
        return allRequestsWithItems;
    }
}
