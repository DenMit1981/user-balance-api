package com.denmit.userbalance.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.Set;

@Document(indexName = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDocument {

    @Id
    private String id;

    private String name;

    private Set<String> emails;

    private Set<String> phones;

    @Field(type = FieldType.Date, format = DateFormat.date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;
}
