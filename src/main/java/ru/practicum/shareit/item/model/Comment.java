package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static ru.practicum.shareit.exception.ErrorDetails.BLANK_NAME_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.BLANK_TEXT_ERROR;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank(message = BLANK_TEXT_ERROR)
    private String text;

    @NonNull
    @NotBlank(message = BLANK_NAME_ERROR)
    private String authorName;

    @NonNull
    @NotNull
    private Long itemId;

    @NonNull
    @NotNull
    private LocalDateTime created;

}
