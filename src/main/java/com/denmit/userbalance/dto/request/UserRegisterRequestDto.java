package com.denmit.userbalance.dto.request;

import com.denmit.userbalance.validation.UniqueElementsInSet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Setter
public class UserRegisterRequestDto {

    private static final String NAME_REQUIRED = "Name is required";
    private static final String PASSWORD_REQUIRED = "Password is required";
    private static final String PASSWORD_LENGTH = "Password must be between 8 and 500 characters";
    private static final String CONFIRM_PASSWORD_REQUIRED = "Confirm password is required";
    private static final String INVALID_CONFIRM = "Confirm password must match password";
    private static final String INVALID_DATE = "Date of birth must be in format dd.MM.yyyy";
    private static final String BALANCE_REQUIRED = "Initial balance is required";
    private static final String BALANCE_NEGATIVE = "Balance must be non-negative";
    private static final String AT_LEAST_ONE_EMAIL = "At least one valid email is required";
    private static final String AT_LEAST_ONE_PHONE = "At least one valid phone number is required";

    @NotBlank(message = NAME_REQUIRED)
    @Size(max = 500)
    private String name;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = 8, max = 500, message = PASSWORD_LENGTH)
    private String password;

    @NotBlank(message = CONFIRM_PASSWORD_REQUIRED)
    private String confirmPassword;

    private LocalDate dateOfBirth;

    @NotNull(message = BALANCE_REQUIRED)
    @DecimalMin(value = "0.00", inclusive = true, message = BALANCE_NEGATIVE)
    private BigDecimal initialBalance;

    @NotEmpty(message = AT_LEAST_ONE_EMAIL)
    @Valid
    @UniqueElementsInSet(message = "Emails must be unique")
    private Set<@Email(message = "Invalid email format") @Size(max = 200) String> emails;

    @NotEmpty(message = AT_LEAST_ONE_PHONE)
    @Valid
    @UniqueElementsInSet(message = "Phones must be unique")
    private Set<@Pattern(regexp = "^\\d{11,13}$", message = "Phone number must contain 11 to 13 digits only") String> phones;
}
