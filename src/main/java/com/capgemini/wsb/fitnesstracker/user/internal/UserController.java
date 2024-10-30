package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
     * Deletes a user by their unique ID.
     *
     * @param id the ID of the user to delete.
     * @return a ResponseEntity with HTTP status 204 (No Content) if deletion is successful.
     * If the user does not exist, a 404 (Not Found) status is returned.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
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
}