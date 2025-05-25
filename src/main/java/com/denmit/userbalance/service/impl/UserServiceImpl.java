package com.denmit.userbalance.service.impl;

import com.denmit.userbalance.config.security.jwt.JwtTokenProvider;
import com.denmit.userbalance.dto.request.*;
import com.denmit.userbalance.dto.response.UserLoginResponseDto;
import com.denmit.userbalance.dto.response.UserRegisterResponseDto;
import com.denmit.userbalance.dto.response.UserResponseDto;
import com.denmit.userbalance.exception.*;
import com.denmit.userbalance.mapper.EmailMapper;
import com.denmit.userbalance.mapper.PhoneMapper;
import com.denmit.userbalance.mapper.UserMapper;
import com.denmit.userbalance.model.EmailData;
import com.denmit.userbalance.model.PhoneData;
import com.denmit.userbalance.model.User;
import com.denmit.userbalance.repository.EmailDataRepository;
import com.denmit.userbalance.repository.PhoneDataRepository;
import com.denmit.userbalance.repository.UserRepository;
import com.denmit.userbalance.service.BalanceService;
import com.denmit.userbalance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_IS_PRESENT = "User with login %s is already present";
    private static final String USER_NOT_FOUND = "User with %s %s not found";
    private static final String USER_HAS_ANOTHER_PASSWORD = "User with login %s has another password. " +
            "Go to register or enter valid credentials";
    private static final String SUCCESSFUL_REGISTRATION = "User %s has been successfully registered";
    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords don't match";

    private final UserRepository userRepository;
    private final EmailDataRepository emailRepository;
    private final PhoneDataRepository phoneRepository;
    private final UserMapper userMapper;
    private final EmailMapper emailMapper;
    private final PhoneMapper phoneMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BalanceService balanceService;

    @Override
    @Transactional
    public UserRegisterResponseDto registration(UserRegisterRequestDto userRegisterRequestDto) {
        validateUserBeforeSave(userRegisterRequestDto);

        String password = passwordEncoder.encode(userRegisterRequestDto.getPassword());
        User user = userMapper.toUser(userRegisterRequestDto, password);

        if (user.getAccount().getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BalanceException("Initial balance must be greater than zero");
        }

        userRepository.save(user);

        balanceService.put(user.getId(), user.getAccount().getBalance());

        log.info("New user : {}", user.getName());

        return userMapper.toUserRegisterResponseDto(user, String.format(SUCCESSFUL_REGISTRATION, user.getName()));
    }

    @Override
    @Transactional
    public UserLoginResponseDto authentication(UserLoginRequestDto userLoginRequestDto) {
        User user = findByLoginAndPassword(userLoginRequestDto.getLogin(), userLoginRequestDto.getPassword());
        String token = jwtTokenProvider.createToken(user.getId());

        return userMapper.toUserLoginResponseDto(user, token);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "users", key = "#userId")
    public void addEmail(Long userId, EmailDataRequestDto dto) {
        checkEmailUniqueness(dto.getEmail());

        User user = findById(userId);
        EmailData email = emailMapper.toEntity(dto);
        email.setUser(user);

        saveWithConstraintCheck(
                () -> emailRepository.saveAndFlush(email),
                "email_data_email_key",
                new EmailAlreadyExistsException()
        );

        log.info("User with id {} added new email successfully", user.getId());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "users", key = "#userId")
    public void updateEmail(Long userId, Long emailId, EmailDataRequestDto dto) {
        checkEmailUniqueness(dto.getEmail());

        EmailData email = getEmailOfUser(userId, emailId);
        email.setEmail(dto.getEmail());

        saveWithConstraintCheck(
                () -> emailRepository.saveAndFlush(email),
                "email_data_email_key",
                new EmailAlreadyExistsException()
        );

        log.info("User with id {} updated old email successfully", userId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "users", key = "#userId")
    public void deleteEmail(Long userId, Long emailId) {
        EmailData email = getEmailOfUser(userId, emailId);

        emailRepository.delete(email);
        log.info("User with id {} deleted email successfully", userId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "users", key = "#userId")
    public void addPhone(Long userId, PhoneDataRequestDto dto) {
        checkPhoneUniqueness(dto.getPhone());

        User user = findById(userId);
        PhoneData phone = phoneMapper.toEntity(dto);
        phone.setUser(user);

        saveWithConstraintCheck(
                () -> phoneRepository.saveAndFlush(phone),
                "phone_data_phone_key",
                new PhoneAlreadyExistsException()
        );

        log.info("User with id {} added new phone successfully", user.getId());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "users", key = "#userId")
    public void updatePhone(Long userId, Long phoneId, PhoneDataRequestDto dto) {
        checkPhoneUniqueness(dto.getPhone());

        PhoneData phone = getPhoneOfUser(userId, phoneId);
        phone.setPhone(dto.getPhone());

        saveWithConstraintCheck(
                () -> phoneRepository.saveAndFlush(phone),
                "phone_data_phone_key",
                new PhoneAlreadyExistsException()
        );

        log.info("User with id {} updated old phone successfully", userId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "users", key = "#userId")
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData phone = getPhoneOfUser(userId, phoneId);

        phoneRepository.delete(phone);
        log.info("User with id {} deleted phone successfully", userId);
    }

    @Override
    @Cacheable(value = "userDtos", key = "#userId")
    public UserResponseDto getById(Long userId) {
        log.info("Fetching user {} from DB", userId);
        return userMapper.toUserResponseDto(findById(userId));
    }

    @Override
    public Page<UserSearchDocument> searchUsers(
            String name,
            String email,
            String phone,
            LocalDate dateOfBirth,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        boolean hasAnyFilter = StringUtils.hasText(name)
                || StringUtils.hasText(email)
                || StringUtils.hasText(phone)
                || dateOfBirth != null;

        Query query;
        if (!hasAnyFilter) {
            query = Query.findAll().setPageable(pageable);
        } else {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(name)) {
                criteria = criteria.and("name").startsWith(name);
            }
            if (StringUtils.hasText(email)) {
                criteria = criteria.and("emails").is(email);
            }
            if (StringUtils.hasText(phone)) {
                criteria = criteria.and("phones").is(phone);
            }
            if (dateOfBirth != null) {
                criteria = criteria.and("dateOfBirth").greaterThan(dateOfBirth);
            }

            query = new CriteriaQuery(criteria, pageable);
        }

        SearchHits<UserSearchDocument> hits =
                elasticsearchOperations.search(query, UserSearchDocument.class);

        List<UserSearchDocument> docs = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(docs, pageable, hits.getTotalHits());
    }

    @Override
    @Transactional
    public void reindexAll() {
        elasticsearchOperations.indexOps(UserSearchDocument.class).delete();
        elasticsearchOperations.indexOps(UserSearchDocument.class).create();
        elasticsearchOperations.indexOps(UserSearchDocument.class).putMapping();

        List<User> users = userRepository.findAll();

        List<UserSearchDocument> docs = users.stream()
                .map(u -> UserSearchDocument.builder()
                        .id(u.getId().toString())
                        .name(u.getName())
                        .emails(u.getEmails().stream()
                                .map(EmailData::getEmail)
                                .collect(Collectors.toSet()))
                        .phones(u.getPhones().stream()
                                .map(PhoneData::getPhone)
                                .collect(Collectors.toSet()))
                        .dateOfBirth(u.getDateOfBirth())
                        .build())
                .toList();

        docs.forEach(elasticsearchOperations::save);
        elasticsearchOperations.indexOps(UserSearchDocument.class).refresh();
    }

    private void saveWithConstraintCheck(Runnable saveAction, String constraintKey, RuntimeException exceptionToThrow) {
        try {
            saveAction.run();
        } catch (DataIntegrityViolationException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains(constraintKey.toLowerCase())) {
                throw exceptionToThrow;
            }
            throw ex;
        }
    }

    private EmailData getEmailOfUser(Long userId, Long emailId) {
        User user = findById(userId);
        return user.getEmails().stream()
                .filter(e -> e.getId().equals(emailId))
                .findFirst()
                .orElseThrow(() -> new EmailException("Email not found or not owned by user"));
    }

    private PhoneData getPhoneOfUser(Long userId, Long phoneId) {
        User user = findById(userId);
        return user.getPhones().stream()
                .filter(p -> p.getId().equals(phoneId))
                .findFirst()
                .orElseThrow(() -> new PhoneException("Phone not found or not owned by user"));
    }

    private void checkEmailUniqueness(String email) {
        if (emailRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
    }

    private void checkPhoneUniqueness(String phone) {
        if (phoneRepository.existsByPhone(phone)) {
            throw new PhoneAlreadyExistsException();
        }
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, "id", userId)));
    }

    private User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException(String.format(USER_HAS_ANOTHER_PASSWORD, login));
        }

        return user;
    }

    private User findByLogin(String login) {
        return userRepository.findByEmail(login)
                .or(() -> userRepository.findByPhone(login))
                .orElseThrow(() -> new UserNotFoundException("User not found with login: " + login));
    }

    private void validateUserBeforeSave(UserRegisterRequestDto userDto) {
        for (String email : userDto.getEmails()) {
            if (existsByEmailOrPhone(email)) {
                throw new UserIsPresentException(String.format(USER_IS_PRESENT, email));
            }
        }

        for (String phone : userDto.getPhones()) {
            if (existsByEmailOrPhone(phone)) {
                throw new UserIsPresentException(String.format(USER_IS_PRESENT, phone));
            }
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new PasswordMismatchException(PASSWORDS_DO_NOT_MATCH);
        }
    }

    private boolean existsByEmailOrPhone(String login) {
        return userRepository.existsByEmails_Email(login) || userRepository.existsByPhones_Phone(login);
    }
}
