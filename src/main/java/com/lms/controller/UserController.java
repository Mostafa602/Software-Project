package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.user.UserDto;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.user.User;
import com.lms.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        if(!Objects.equals(userService.getCurrentUserId(), id)) {
            throw new UnauthorizedAccessException();
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.getUserById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        if(!Objects.equals(userService.getCurrentUserId(), id)) {
            throw new UnauthorizedAccessException();
        }
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new BasicResponseDto(
                        "success",
                        "user deleted successfully"
                )
        );
    }
}