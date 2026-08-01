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
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotNull(message = DATETIME_ERROR)
    @Column(name = "start_time")
    private LocalDateTime start;

    @NonNull
    @NotNull(message = DATETIME_ERROR)
    @Column(name = "end_time")
    private LocalDateTime end;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    BookingStatus bookingStatus;
}
