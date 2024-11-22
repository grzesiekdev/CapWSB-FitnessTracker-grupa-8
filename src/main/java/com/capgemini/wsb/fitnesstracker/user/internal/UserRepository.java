package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds users whose email contains the specified value, ignoring case.
     *
     * @param email the partial or full email to search for.
     * @return a list of users matching the partial email.
     */
    List<User> findByEmailContainingIgnoreCase(String email);

    /**
     * Finds all users born before the specified date.
     *
     * @param date the date to compare against.
     * @return a list of users born before the given date.
     */
    List<User> findByBirthdateBefore(LocalDate date);
}
