package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static ru.practicum.shareit.exception.ErrorDetails.BLANK_NAME_ERROR;
import static ru.practicum.shareit.exception.ErrorDetails.EMAIL_FORMAT_ERROR;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = BLANK_NAME_ERROR)
    private String name;

    @Email(message = EMAIL_FORMAT_ERROR)
    @NotBlank(message = EMAIL_FORMAT_ERROR)
    private String email;
}
