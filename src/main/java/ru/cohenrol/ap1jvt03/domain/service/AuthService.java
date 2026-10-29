package ru.cohenrol.ap1jvt03.domain.service;

import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.ap1jvt03.domain.model.JwtRequest;
import ru.cohenrol.ap1jvt03.domain.model.JwtResponse;
import ru.cohenrol.ap1jvt03.domain.model.RefreshJwtRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.SignUpRequest;

import java.util.UUID;

public interface AuthService {
    void register(SignUpRequest domain);
    JwtResponse login(JwtRequest request);

    @Transactional(readOnly = true)
    JwtResponse refreshAccessToken(RefreshJwtRequest domain);

    @Transactional(readOnly = true)
    JwtResponse refreshRefreshToken(RefreshJwtRequest domain);

    UUID getCurrentUserUuid();
}
