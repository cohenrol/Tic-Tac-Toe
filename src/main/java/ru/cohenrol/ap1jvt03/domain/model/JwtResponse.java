package ru.cohenrol.ap1jvt03.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String type;
    private String accessToken;
    private String refreshToken;
}