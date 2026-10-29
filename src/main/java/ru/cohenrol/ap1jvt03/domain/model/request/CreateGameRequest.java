package ru.cohenrol.ap1jvt03.domain.model.request;

import lombok.Data;
import ru.cohenrol.ap1jvt03.domain.enums.GameOpponent;

@Data
public class CreateGameRequest {
    private final GameOpponent opponent;
}
