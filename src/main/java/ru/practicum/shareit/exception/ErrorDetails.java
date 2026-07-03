package ru.practicum.shareit.exception;

public class ErrorDetails {
    public static final String ITEM_DUPLICATE_ERROR = "Такая вещь уже существует";
    public static final String ITEM_NOT_FOUND = "Вещь не найдена";
    public static final String ITEM_REQUEST_NOT_FOUND = "Запрос не найден";
    public static final String BOOKING_NOT_FOUND = "Бронирование не найдено";
    public static final String ACCESS_DENIED = "Информация недоступна";
    public static final String WRONG_OWNER = "Владельцем вещи является другой пользователь";
    public static final String USER_DUPLICATE_ERROR = "Пользователь с такими данными уже существует";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String BLANK_NAME_ERROR = "Название/имя не может быть пустым";
    public static final String EMAIL_FORMAT_ERROR = "Необходимо указать Email в корректном формате";
    public static final String BLANK_DESCRIPTION_ERROR = "Описание не может быть пустым";
    public static final String AVAILABILITY_ERROR = "Необходимо указать, доступна ли вещь для бронирования";
    public static final String DATETIME_ERROR = "Для бронирования необходимо указать текущую дату или более позднюю";
    public static final String UNAVAILABLE_ITEM_ERROR = "Вещь недоступна для бронирования";
    public static final String INVALID_PARAMETER = "Параметр запроса передан некорректно";
    public static final String BLANK_TEXT_ERROR = "Текст комментария не может быть пустым";
    public static final String COMMENT_ERROR = "Возможность оставлять комментарии для этой вещи недоступна";
}
