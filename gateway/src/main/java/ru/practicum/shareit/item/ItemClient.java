package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemDto requestDto, long userId) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateItem(long itemId, long userId, ItemDto requestDto) {
        String path = "/" + itemId;

        return patch(path, userId, requestDto);
    }

    public ResponseEntity<Object> getItem(long itemId) {
        String path = "/" + itemId;

        return get(path);
    }

    public ResponseEntity<Object> getAllItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text) {
        String path = "/search?text={text}";
        Map<String, Object> parameters = Map.of("text", text);

        return get(path, parameters);
    }

    public ResponseEntity<Object> addComment(CommentPostDto comment, long itemId, long userId) {
        String path = "/" + itemId + "/comment";

        return post(path, userId, comment);
    }
}
