package ru.cohenrol.ap1jvt03.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;
import ru.cohenrol.ap1jvt03.domain.enums.GameOpponent;
import ru.cohenrol.ap1jvt03.domain.enums.GameStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MakeGameMoveResponseDto {
    private UUID uuid;
    private CellStatus[][] cells;
    private GameStatus status;
    private GameOpponent opponentType;
    private UUID xxPlayerUuid;
    private UUID ooPlayerUuid;
    private UUID makesNextMove;
}