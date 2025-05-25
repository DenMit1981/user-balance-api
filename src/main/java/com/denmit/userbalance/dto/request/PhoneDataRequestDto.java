package com.denmit.userbalance.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class PhoneDataRequestDto {

    @Pattern(regexp = "^\\d{11,13}$", message = "Phone number must contain 11 to 13 digits only")
    private String phone;
}
