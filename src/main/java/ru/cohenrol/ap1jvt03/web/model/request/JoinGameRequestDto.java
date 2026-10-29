package ru.cohenrol.ap1jvt03.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class JoinGameRequestDto {
    @NotNull
    private UUID gameUuid;
}