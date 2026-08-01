package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
