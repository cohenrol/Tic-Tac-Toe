package ru.cohenrol.ap1jvt03.domain.model.request;

import lombok.Data;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;

@Data
public class MakeGameMoveRequest {
    private CellStatus[][] cells;
}