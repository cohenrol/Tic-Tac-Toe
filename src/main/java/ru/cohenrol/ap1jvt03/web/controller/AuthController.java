package ru.cohenrol.ap1jvt03.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cohenrol.ap1jvt03.domain.ServiceGate;
import ru.cohenrol.ap1jvt03.web.model.request.JwtRequestDto;
import ru.cohenrol.ap1jvt03.web.model.request.RefreshJwtRequestDto;
import ru.cohenrol.ap1jvt03.web.model.request.SignUpRequestDto;
import ru.cohenrol.ap1jvt03.web.model.response.JwtResponseDto;
import ru.cohenrol.ap1jvt03.web.model.response.UserInfoResponseDto;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ServiceGate services;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
        @Valid @RequestBody SignUpRequestDto dto
    ) {
        services.register(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(
        @Valid @RequestBody JwtRequestDto request
    ) {
        return ResponseEntity.ok(services.login(request));
    }

    @PostMapping("/refresh/access")
    public ResponseEntity<JwtResponseDto> refreshAccess(
        @Valid @RequestBody RefreshJwtRequestDto request
    ) {
        return ResponseEntity.ok(services.refreshAccessToken(request));
    }

    @PostMapping("/refresh/refresh")
    public ResponseEntity<JwtResponseDto> refreshRefresh(
        @Valid @RequestBody RefreshJwtRequestDto request
    ) {
        return ResponseEntity.ok(services.refreshRefreshToken(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> me() {
        return ResponseEntity.ok(services.getCurrentUserInfo());
    }
}