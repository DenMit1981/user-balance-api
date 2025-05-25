package com.denmit.userbalance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    private BigDecimal initialBalance;

    private Set<String> emails;

    private Set<String> phones;
}
