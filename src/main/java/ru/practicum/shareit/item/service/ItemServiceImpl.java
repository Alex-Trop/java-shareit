package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundError;
import ru.practicum.shareit.exception.ResourceAlreadyExistsError;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorDetails.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private static final long TMP_ID = 0;
    private static final long POSTMAN_TEST_PAUSE = 10;

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        log.info("Получен запрос на добавление вещи " + itemDto + "пользователем " + userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundError(USER_NOT_FOUND);
        }

        Item newItem = new Item(
                TMP_ID,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                itemDto.getItemRequestId() == 0 ? null : itemRequestRepository.findById(itemDto.getItemRequestId())
                        .orElseThrow(() -> new NotFoundError(ITEM_REQUEST_NOT_FOUND)));

        if (itemRepository.existsByNameAndDescriptionAndAvailableAndOwnerAndRequest(newItem.getName(),
                newItem.getDescription(),
                newItem.getAvailable(),
                userId,
                newItem.getRequest())) {
            throw new ResourceAlreadyExistsError(ITEM_DUPLICATE_ERROR);
        }
        return itemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) {
        log.info("Получен запрос на обновление сведений о вещи " + itemDto + " с Id " + itemId
                + " пользователем " + userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundError(USER_NOT_FOUND);
        }

        Item foundItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundError(ITEM_NOT_FOUND));

        if (foundItem.getOwner() != userId) {
            throw new NotFoundError(WRONG_OWNER);
        }

        itemMapper.updateItemFromItemDto(itemDto, foundItem);
        return itemMapper.toItemDto(itemRepository.save(foundItem));
    }

    @Override
    public ItemDtoFullInfo getItem(long itemId) {
        log.info("Получен запрос на получение сведений о вещи " + itemId);

        Item foundItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundError(ITEM_NOT_FOUND));

        log.info("Вещь найдена: " + foundItem);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        log.info("Комментарии к вещи: " + comments);

        List<Booking> foundBookings = bookingRepository.findAllByItemId(itemId);

        Optional<Booking> latest = foundBookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                        && booking.getEnd().isBefore(LocalDateTime.now().minusSeconds(POSTMAN_TEST_PAUSE)))
                .max(Comparator.comparing(Booking::getStart));

        Optional<Booking> next = foundBookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart));

        return itemMapper.toItemDtoFullInfo(foundItem, latest, next, comments);
    }

    @Override
    public List<ItemDtoFullInfo> getAllItems(long userId) {
        log.info("Получен запрос на получение списка всех вещей пользователя с id " + userId);

        Map<Long, Item> foundItems = itemRepository.findAllByOwner(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Item, List<Booking>> foundBookings = bookingRepository.findAllByItemIdInOrderByStart(foundItems.keySet())
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        Map<Long, List<Comment>> foundComments = commentRepository.findAllByItemIdIn(foundItems.keySet())
                .stream()
                .collect(Collectors.groupingBy(Comment::getItemId));

        List<ItemDtoFullInfo> itemsWithBookings = new ArrayList<>();

        for (Item item : foundItems.values()) {
            Optional<Booking> latest = foundBookings.getOrDefault(item, Collections.emptyList()).stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getStart));

            Optional<Booking> next = foundBookings.getOrDefault(item, Collections.emptyList()).stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart));

            List<CommentDto> comments = foundComments.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .map(commentMapper::toCommentDto)
                    .collect(Collectors.toList());

            log.info("Последнее бронирование вещи " + item + ": " + latest + "; следующее - " + next
                    + ". Всего %d комментариев" + comments.size());

            itemsWithBookings.add(itemMapper.toItemDtoFullInfo(item, latest, next, comments));
        }

        log.info("У пользователя с id " + userId + "выставлено " + foundItems.size() + " вещей");

        return itemsWithBookings;
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Получен запрос на поиск вещей, где в описании/имени след. текст: " + text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(CommentPostDto commentPostDto, long itemId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError(USER_NOT_FOUND));

        if (!bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())) {
            throw new RuntimeException(COMMENT_ERROR);
        }

        Comment newComment = new Comment(
                TMP_ID,
                commentPostDto.getText(),
                user.getName(),
                itemId,
                LocalDateTime.now()
        );

        log.info("Сохранение комментария " + newComment);
        return commentMapper.toCommentDto(commentRepository.save(newComment));
    }

}
