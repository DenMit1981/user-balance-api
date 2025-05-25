package com.denmit.userbalance.controller;

import com.denmit.userbalance.dto.request.UserLoginRequestDto;
import com.denmit.userbalance.dto.request.UserRegisterRequestDto;
import com.denmit.userbalance.dto.response.UserLoginResponseDto;
import com.denmit.userbalance.dto.response.UserRegisterResponseDto;
import com.denmit.userbalance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Authentication controller")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Registration a new user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponseDto registration(@RequestBody @Valid UserRegisterRequestDto userDto) {
        return userService.registration(userDto);
    }

    @PostMapping("/signin")
    @Operation(summary = "Authentication and generation JWT token")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto authentication(@RequestBody @Valid UserLoginRequestDto userDto) {
        return userService.authentication(userDto);
    }
}
