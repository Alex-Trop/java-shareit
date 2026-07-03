package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.exception.ErrorDetails.DATETIME_ERROR;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = DATETIME_ERROR)
    @Column(name = "start_time")
    private LocalDateTime start;

    @NotNull(message = DATETIME_ERROR)
    @Column(name = "end_time")
    private LocalDateTime end;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    BookingStatus bookingStatus;
}
