package ru.cohenrol.ap1jvt03.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.cohenrol.ap1jvt03.domain.enums.GameOpponent;

@Data
public class CreateGameRequestDto {
    @NotNull
    private final GameOpponent opponent;
}
