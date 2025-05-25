package com.denmit.userbalance.mapper;

import com.denmit.userbalance.dto.request.UserRegisterRequestDto;
import com.denmit.userbalance.dto.response.UserLoginResponseDto;
import com.denmit.userbalance.dto.response.UserRegisterResponseDto;
import com.denmit.userbalance.dto.response.UserResponseDto;
import com.denmit.userbalance.model.Account;
import com.denmit.userbalance.model.EmailData;
import com.denmit.userbalance.model.PhoneData;
import com.denmit.userbalance.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", expression = "java(password)")
    @Mapping(target = "emails", source = "userRegisterRequestDto.emails", qualifiedByName = "mapToEmailData")
    @Mapping(target = "phones", source = "userRegisterRequestDto.phones", qualifiedByName = "mapToPhoneData")
    @Mapping(target = "account", source = "userRegisterRequestDto.initialBalance", qualifiedByName = "mapToAccount")
    User toUser(UserRegisterRequestDto userRegisterRequestDto, String password);

    @Mapping(target = "initialBalance", source = "user.account", qualifiedByName = "mapBalance")
    @Mapping(target = "emails", source = "user.emails", qualifiedByName = "mapEmails")
    @Mapping(target = "phones", source = "user.phones", qualifiedByName = "mapPhones")
    UserRegisterResponseDto toUserRegisterResponseDto(User user, String message);

    @Mapping(target = "initialBalance", source = "user.account", qualifiedByName = "mapBalance")
    @Mapping(target = "emails", source = "user.emails", qualifiedByName = "mapEmails")
    @Mapping(target = "phones", source = "user.phones", qualifiedByName = "mapPhones")
    UserLoginResponseDto toUserLoginResponseDto(User user, String token);

    @Mapping(target = "initialBalance", source = "account", qualifiedByName = "mapBalance")
    @Mapping(target = "emails", source = "emails", qualifiedByName = "mapEmails")
    @Mapping(target = "phones", source = "phones", qualifiedByName = "mapPhones")
    UserRegisterResponseDto toUserResponseDto(User user);

    default List<UserResponseDto> toDtos(List<User> users) {
        return users.stream()
                .map(this::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Named("mapBalance")
    static BigDecimal mapBalance(Account account) {
        return account != null ? account.getBalance() : null;
    }

    @Named("mapEmails")
    static Set<String> mapEmails(Set<EmailData> emails) {
        return emails != null
                ? emails.stream().map(EmailData::getEmail).collect(Collectors.toSet())
                : Set.of();
    }

    @Named("mapPhones")
    static Set<String> mapPhones(Set<PhoneData> phones) {
        return phones != null
                ? phones.stream().map(PhoneData::getPhone).collect(Collectors.toSet())
                : Set.of();
    }

    @Named("mapToEmailData")
    static Set<EmailData> mapToEmailData(Set<String> emails) {
        return emails.stream()
                .map(email -> {
                    EmailData emailData = new EmailData();
                    emailData.setEmail(email);
                    return emailData;
                }).collect(Collectors.toSet());
    }

    @Named("mapToPhoneData")
    static Set<PhoneData> mapToPhoneData(Set<String> phones) {
        return phones.stream()
                .map(phone -> {
                    PhoneData phoneData = new PhoneData();
                    phoneData.setPhone(phone);
                    return phoneData;
                }).collect(Collectors.toSet());
    }

    @Named("mapToAccount")
    static Account mapToAccount(BigDecimal balance) {
        Account account = new Account();
        account.setBalance(balance);
        return account;
    }
}
