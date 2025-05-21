package org.jnjeaaaat.snms.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.exception.DuplicateEmailException;
import org.jnjeaaaat.snms.domain.auth.exception.UnmatchedDefaultFile;
import org.jnjeaaaat.snms.domain.auth.exception.UnmatchedPassword;
import org.jnjeaaaat.snms.domain.user.entity.User;
import org.jnjeaaaat.snms.domain.user.repository.UserRepository;
import org.jnjeaaaat.snms.domain.user.type.LoginType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signUp(SignUpRequest request) {

        validateExistEmail(request.email());
        validatePassword(request.password(), request.rePassword());
        validateDefaultFile(request.profileImgUrl());

        User savedUser = userRepository.save(
                SignUpRequest.toEntity(
                        request,
                        passwordEncoder.encode(request.password()),
                        LoginType.LOCAL)
        );

        log.info("회원가입 성공 user_id : {}", savedUser.getId());

        return SignUpResponse.fromEntity(savedUser);
    }

    private void validateExistEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    private void validatePassword(String password, String rePassword) {
        if (!password.equals(rePassword)) {
            throw new UnmatchedPassword();
        }
    }

    private void validateDefaultFile(String profileImgUrl) {

        String defaultFilename = "/default.jpg";

        if (!profileImgUrl.endsWith(defaultFilename)) {
            throw new UnmatchedDefaultFile();
        }
    }
}
