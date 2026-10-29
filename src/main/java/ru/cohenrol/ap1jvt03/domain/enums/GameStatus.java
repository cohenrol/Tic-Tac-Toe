package ru.cohenrol.ap1jvt03.domain.enums;

//@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GameStatus {
    X_WINS, O_WINS,
    DRAW,
    X_MOVES_NEXT, O_MOVES_NEXT,
    WAITING_FOR_OPPONENT
}