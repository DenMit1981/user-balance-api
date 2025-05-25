package com.denmit.userbalance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UserLoginRequestDto {

    private static final String LOGIN_REQUIRED = "Enter email or phone";
    private static final String PASSWORD_REQUIRED = "Enter password";
    private static final String PASSWORD_TOO_SHORT = "Password must be at least 8 characters";
    private static final String INVALID_LOGIN = "Login must be a valid email or phone number (e.g. 79207865432)";

    @NotBlank(message = LOGIN_REQUIRED)
    @Pattern(
            regexp = "^(\\d{11,13}|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})$",
            message = INVALID_LOGIN
    )
    private String login;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = 8, max = 500, message = PASSWORD_TOO_SHORT)
    private String password;
}
