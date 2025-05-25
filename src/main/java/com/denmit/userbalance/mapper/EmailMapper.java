package com.denmit.userbalance.mapper;

import com.denmit.userbalance.dto.request.EmailDataRequestDto;
import com.denmit.userbalance.model.EmailData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    EmailData toEntity(EmailDataRequestDto dto);
}
