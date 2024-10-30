package com.capgemini.wsb.fitnesstracker.user.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;

import java.time.LocalDate;

record UserDto(@Nullable Long Id, String firstName, String lastName,
               @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthdate,
               String email) {

}

/**
 * A Data Transfer Object (DTO) representing a simplified view of a user,
 * containing only the essential information.
 *
 * @param id        the unique identifier of the user.
 * @param firstName the first name of the user.
 * @param lastName  the last name of the user.
 */
record SimpleUserDto(Long id, String firstName, String lastName) {}

/**
 * A Data Transfer Object representing a user with only ID and email.
 *
 * @param id    the unique identifier of the user.
 * @param email the email address of the user.
 */
record UserEmailDto(Long id, String email) {}

/**
 * A Data Transfer Object representing a user with limited fields for age-based queries.
 *
 * @param firstName the first name of the user.
 * @param lastName the last name of the user.
 * @param birthdate the birthdate of the user.
 */
record UserOlderThanDto(String firstName, String lastName, LocalDate birthdate) {}