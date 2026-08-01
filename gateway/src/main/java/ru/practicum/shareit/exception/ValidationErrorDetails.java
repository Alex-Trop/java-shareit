package ru.practicum.shareit.exception;

public class ValidationErrorDetails {
    public static final String DATETIME_ERROR = "Для бронирования необходимо указать текущую дату или более позднюю";
    public static final String BLANK_NAME_ERROR = "Название/имя не может быть пустым";
    public static final String BLANK_DESCRIPTION_ERROR = "Описание не может быть пустым";
    public static final String AVAILABILITY_ERROR = "Необходимо указать, доступна ли вещь для бронирования";
    public static final String BLANK_TEXT_ERROR = "Текст комментария не может быть пустым";
    public static final String EMAIL_FORMAT_ERROR = "Необходимо указать Email в корректном формате";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
}
