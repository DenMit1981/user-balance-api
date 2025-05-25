package com.denmit.userbalance.mapper;

import com.denmit.userbalance.dto.request.PhoneDataRequestDto;
import com.denmit.userbalance.model.PhoneData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhoneMapper {

    PhoneData toEntity(PhoneDataRequestDto dto);
}
