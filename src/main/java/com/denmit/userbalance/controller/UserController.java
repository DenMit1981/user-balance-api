package com.denmit.userbalance.controller;

import com.denmit.userbalance.config.security.provider.UserProvider;
import com.denmit.userbalance.dto.request.EmailDataRequestDto;
import com.denmit.userbalance.dto.request.PhoneDataRequestDto;
import com.denmit.userbalance.dto.request.UserSearchDocument;
import com.denmit.userbalance.dto.response.UserResponseDto;
import com.denmit.userbalance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User controller")
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserProvider userProvider;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users with filters")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserSearchDocument> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate dateOfBirth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.searchUsers(name, email, phone, dateOfBirth, page, size);
    }

    @PostMapping("/email")
    @Operation(summary = "Add new email to current user")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEmail(@RequestBody @Valid EmailDataRequestDto dto) {
        userService.addEmail(userProvider.getUserId(), dto);
    }

    @PutMapping("/email/{emailId}")
    @Operation(summary = "Update existing email for current user")
    @ResponseStatus(HttpStatus.OK)
    public void updateEmail(@PathVariable Long emailId,
                            @RequestBody @Valid EmailDataRequestDto newEmail) {
        userService.updateEmail(userProvider.getUserId(), emailId, newEmail);
    }

    @DeleteMapping("/email/{emailId}")
    @Operation(summary = "Delete email from current user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmail(@PathVariable Long emailId) {
        userService.deleteEmail(userProvider.getUserId(), emailId);
    }

    @PostMapping("/phone")
    @Operation(summary = "Add new phone to current user")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPhone(@RequestBody @Valid PhoneDataRequestDto dto) {
        userService.addPhone(userProvider.getUserId(), dto);
    }

    @PutMapping("/phone/{phoneId}")
    @Operation(summary = "Update existing phone for current user")
    @ResponseStatus(HttpStatus.OK)
    public void updatePhone(@PathVariable Long phoneId,
                            @RequestBody @Valid PhoneDataRequestDto newPhone) {
        userService.updatePhone(userProvider.getUserId(), phoneId, newPhone);
    }

    @DeleteMapping("/phone/{phoneId}")
    @Operation(summary = "Delete phone from current user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhone(@PathVariable Long phoneId) {
        userService.deletePhone(userProvider.getUserId(), phoneId);
    }
}
