package com.denmit.userbalance.service;

import com.denmit.userbalance.dto.request.*;
import com.denmit.userbalance.dto.response.UserLoginResponseDto;
import com.denmit.userbalance.dto.response.UserRegisterResponseDto;
import com.denmit.userbalance.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface UserService {

    UserRegisterResponseDto registration(UserRegisterRequestDto userRegisterRequestDto);

    UserLoginResponseDto authentication(UserLoginRequestDto userDto);

    void addEmail(Long userId, EmailDataRequestDto dto);

    void updateEmail(Long userId, Long emailId, EmailDataRequestDto dto);

    void deleteEmail(Long userId, Long emailId);

    void addPhone(Long userId, PhoneDataRequestDto dto);

    void updatePhone(Long userId, Long phoneId, PhoneDataRequestDto dto);

    void deletePhone(Long userId, Long phoneId);

    UserResponseDto getById(Long userid);

    Page<UserSearchDocument> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, int page, int size);

    void reindexAll();
}
