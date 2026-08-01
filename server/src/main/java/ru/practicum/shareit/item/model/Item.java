package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import static ru.practicum.shareit.exception.ErrorDetails.*;

/**
 * TODO Sprint add-controllers.
 */
@EqualsAndHashCode(exclude = "id")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @NonNull
    @NotBlank(message = BLANK_DESCRIPTION_ERROR)
    private String description;

    @NonNull
    @NotNull(message = AVAILABILITY_ERROR)
    private Boolean available;

    @Positive(message = USER_NOT_FOUND)
    @Column(name = "owner_id")
    private long owner;

    @ManyToOne
    @JoinColumn(name = "item_request_id")
    private ItemRequest request;

    public Item(String name, String description, Boolean available, long owner, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
