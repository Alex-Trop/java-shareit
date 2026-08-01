package ru.practicum.shareit.exception;

public class NotFoundError extends RuntimeException {

    public NotFoundError(String message) {
        super(message);
    }
}
