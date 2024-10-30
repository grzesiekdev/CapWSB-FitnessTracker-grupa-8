package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;
import com.capgemini.wsb.fitnesstracker.user.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service implementation for managing user operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private final UserRepository userRepository;

    /**
     * Creates and saves a new user in the system.
     *
     * @param user the User entity to create.
     * @return the saved User entity with an assigned ID.
     * @throws IllegalArgumentException if the User entity already has an ID.
     */
    @Override
    public User createUser(final User user) {
        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }
        return userRepository.save(user);
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param userId the ID of the user to find.
     * @return an Optional containing the User if found, or empty if not found.
     */
    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Finds users by email, allowing for case-insensitive and partial matches.
     *
     * @param email the partial or full email to search for.
     * @return a list of users whose email contains the specified value, case-insensitively.
     */
    @Override
    public List<User> getUserByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email);
    }

    /**
     * Finds all users in the system.
     *
     * @return a list of all User entities.
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deletes a user by their unique ID from the database. If the user has associated records that
     * prevent deletion (such as foreign key constraints), an exception is thrown.
     *
     * @param id the unique identifier of the user to delete.
     * @throws NoSuchElementException if the user does not exist.
     * @throws IllegalStateException if the user has associated records that prevent deletion,
     *         for instance due to foreign key constraints in related tables.
     */
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with ID " + id + " not found.");
        }

        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalStateException("Error while deleting user: ", e);
        }
    }

}