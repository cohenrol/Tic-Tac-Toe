package ru.cohenrol.ap1jvt03.domain.enums;

public enum CellStatus {
    X(1),
    O(2),
    EMPTY(0);

    private final int value;

    CellStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CellStatus[][] createEmptyGameBoard3x3() {
        return new CellStatus[][]{
            { CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY },
            { CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY },
            { CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY }
        };
    }

}
