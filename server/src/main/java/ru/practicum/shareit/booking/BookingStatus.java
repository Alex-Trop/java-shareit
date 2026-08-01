package ru.practicum.shareit.booking;

import lombok.ToString;

@ToString
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
