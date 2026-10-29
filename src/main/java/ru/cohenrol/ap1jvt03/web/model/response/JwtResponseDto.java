package ru.cohenrol.ap1jvt03.web.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDto {
    private String type;
    private String accessToken;
    private String refreshToken;
}