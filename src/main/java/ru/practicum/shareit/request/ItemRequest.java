package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotBlank
    String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @NotNull
    private Instant created;
}
