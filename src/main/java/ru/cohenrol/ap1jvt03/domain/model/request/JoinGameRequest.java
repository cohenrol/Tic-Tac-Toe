package ru.cohenrol.ap1jvt03.domain.model.request;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinGameRequest {
    private UUID gameUuid;
}