package ru.practicum.shareit.exception;

public class ResourceAlreadyExistsError extends RuntimeException {
    public ResourceAlreadyExistsError(String message) {
        super(message);
    }
}
