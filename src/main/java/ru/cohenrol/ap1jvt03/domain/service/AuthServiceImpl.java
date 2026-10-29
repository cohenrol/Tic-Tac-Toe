package ru.cohenrol.ap1jvt03.domain.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.ap1jvt03.datasource.model.UserEntity;
import ru.cohenrol.ap1jvt03.datasource.repository.UserRepository;
import ru.cohenrol.ap1jvt03.domain.exception.auth.WrongLoginOrPassword;
import ru.cohenrol.ap1jvt03.domain.exception.auth.WrongResreshToken;
import ru.cohenrol.ap1jvt03.domain.exception.user.UserAlreadyExists;
import ru.cohenrol.ap1jvt03.domain.exception.user.UserNotExists;
import ru.cohenrol.ap1jvt03.domain.model.JwtRequest;
import ru.cohenrol.ap1jvt03.domain.model.JwtResponse;
import ru.cohenrol.ap1jvt03.domain.model.RefreshJwtRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.SignUpRequest;
import ru.cohenrol.ap1jvt03.domain.token.JwtAuthentication;
import ru.cohenrol.ap1jvt03.domain.token.JwtProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements  AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public void register(SignUpRequest domain) {
        userRepository.findByLogin(domain.getLogin()).ifPresent((userEntity) -> { throw new UserAlreadyExists(); });
        userRepository.save(new UserEntity(domain.getLogin(), passwordEncoder.encode(domain.getPassword())));
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse login(JwtRequest request) {
        UserEntity user = userRepository.findByLogin(request.getLogin())
            .orElseThrow(WrongLoginOrPassword::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongLoginOrPassword();
        }

        return JwtResponse.builder()
            .type("Bearer")
            .accessToken(jwtProvider.generateAccessToken(user))
            .refreshToken(jwtProvider.generateRefreshToken(user))
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse refreshAccessToken(RefreshJwtRequest domain) {
        UserEntity user = resolveUserByRefreshToken(domain.getRefreshToken());

        return JwtResponse.builder()
            .type("Bearer")
            .accessToken(jwtProvider.generateAccessToken(user))
            .refreshToken(domain.getRefreshToken())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse refreshRefreshToken(RefreshJwtRequest domain) {
        UserEntity user = resolveUserByRefreshToken(domain.getRefreshToken());

        return JwtResponse.builder()
            .type("Bearer")
            .accessToken(jwtProvider.generateAccessToken(user))
            .refreshToken(jwtProvider.generateRefreshToken(user))
            .build();
    }

    private UserEntity resolveUserByRefreshToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new WrongResreshToken();
        }
        Claims claims = jwtProvider.getClaims(refreshToken);
        UUID uuid = UUID.fromString(claims.get("uuid", String.class));
        return userRepository.findByUuid(uuid)
            .orElseThrow(UserNotExists::new);
    }

    @Override
    public UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthentication jwt)) {
            throw new UserNotExists();
        }
        return (UUID) jwt.getPrincipal();
    }
}
