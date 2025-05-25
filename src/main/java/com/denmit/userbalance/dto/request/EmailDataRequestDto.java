package com.denmit.userbalance.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class EmailDataRequestDto {

    @Email(message = "Invalid email format")
    @Size(max = 200)
    private String email;
}
