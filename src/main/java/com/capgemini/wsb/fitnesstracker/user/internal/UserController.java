package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Controller that manages endpoints for user-related operations.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    /**
     * Retrieves all users with detailed information.
     *
     * @return a list of UserDto containing full details of each user.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                          .stream()
                          .map(userMapper::toDto)
                          .toList();
    }

    /**
     * Retrieves a simplified list of users, including only ID, first name, and last name.
     *
     * @return a list of SimpleUserDto with basic user details.
     */
    @GetMapping("/simple")
    public List<SimpleUserDto> getSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    /**
     * Retrieves detailed information for a specific user by ID.
     *
     * @param id the unique identifier of the user.
     * @return a ResponseEntity containing UserDto if the user is found, or 404 Not Found otherwise.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Creates a new user with the provided information.
     *
     * @param userDto the user data for creating a new user.
     * @return a ResponseEntity containing the saved UserDto with HTTP 201 Created status.
     */
    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        System.out.println("User with e-mail: " + userDto.email() + " passed to the request");
        User user = userMapper.toEntity(userDto);
        User savedUser = userService.createUser(user);
        UserDto savedUserDto = userMapper.toDto(savedUser);

        return ResponseEntity
                .created(URI.create("/v1/users/" + savedUser.getId()))
                .body(savedUserDto);
    }

    /**
     * Deletes a user by their unique ID. If the user has associated records that prevent deletion,
     * or if the user does not exist, an appropriate HTTP status and message are returned.
     *
     * @param id the unique identifier of the user to delete.
     * @return ResponseEntity with:
     *         <ul>
     *           <li>204 No Content if the deletion is successful.</li>
     *           <li>409 Conflict if the user has associated records that prevent deletion.</li>
     *           <li>404 Not Found if the user does not exist.</li>
     *         </ul>
     * @throws IllegalStateException if the user cannot be deleted due to associated records.
     * @throws NoSuchElementException if the user with the given ID is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();  // 204 No Content if successful
        } catch (IllegalStateException e) {
            // Return 409 Conflict with a general message for associated records
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User cannot be deleted because they are related to other records.");
        } catch (NoSuchElementException e) {
            // Return 404 Not Found with a message when user doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + id + " not found.");
        }
    }

    /**
     * Searches for users by a partial email match, ignoring case sensitivity.
     * Returns a list of users matching the specified email criteria, containing only the ID and email fields.
     *
     * @param email the partial or full email string to search for, case-insensitively.
     * @return a ResponseEntity containing a list of UserEmailDto with HTTP status 200 (OK).
     *         If no users match, an empty list is returned.
     */
    @GetMapping("/email")
    public ResponseEntity<List<UserEmailDto>> getUsersByEmail(@RequestParam String email) {
        List<UserEmailDto> users = userService.getUserByEmail(email)
                .stream()
                .map(user -> new UserEmailDto(user.getId(), user.getEmail()))
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves all users who are older than the specified date.
     *
     * @param date the date to compare against.
     * @return a list of users older than the specified date, with limited user details.
     */
    @GetMapping("/older/{time}")
    public ResponseEntity<List<UserOlderThanDto>> getAllUsersOlderThan(
            @PathVariable("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<UserOlderThanDto> users = userService.findAllUsersOlderThan(date)
                .stream()
                .map(userMapper::toOlderThanDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /**
     * Updates a user by their unique ID.
     *
     * @param id the unique ID of the user to update.
     * @param userDto the updated user details.
     * @return a ResponseEntity containing the updated UserDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto) {

        User updatedUser = userMapper.toEntity(userDto);
        User savedUser = userService.updateUserById(id, updatedUser);
        UserDto savedUserDto = userMapper.toDto(savedUser);

        return ResponseEntity.ok(savedUserDto);
    }
}