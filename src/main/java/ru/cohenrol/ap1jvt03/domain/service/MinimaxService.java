package ru.cohenrol.ap1jvt03.domain.service;

import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;

public interface MinimaxService {
    void findAndApplyBestMove(GameEntity gameEntity);
    boolean isBoardFullForMinimax(CellStatus[][] board);
    boolean hasWinnerForMinimax(CellStatus[][] board, CellStatus player);
}
