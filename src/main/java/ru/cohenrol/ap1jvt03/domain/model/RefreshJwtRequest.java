package ru.cohenrol.ap1jvt03.domain.model;

import lombok.Data;

@Data
public class RefreshJwtRequest {
    private String refreshToken;
}