package ru.cohenrol.ap1jvt03.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private final UUID uuid;
}
