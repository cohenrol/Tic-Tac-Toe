package ru.cohenrol.ap1jvt03.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;
import ru.cohenrol.ap1jvt03.domain.exception.game.InvalidBoardException;

@Data
public class MakeGameMoveRequestDto {
    @NotNull
    private CellStatus[][] cells;

    public void check() {
        if (cells.length != 3) {
            throw new InvalidBoardException("Number of rows must be 3");
        }

        for (int i = 1; i < 3; i++) {
            if (cells[i] == null || cells[i].length != 3) {
                throw new InvalidBoardException("All rows must have the same length");
            }
        }
    }
}