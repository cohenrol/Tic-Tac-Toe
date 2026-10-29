package ru.cohenrol.ap1jvt03.domain.exception.game;

public final class InvalidBoardException extends DomainException {
    public InvalidBoardException() {
        super("Invalid board.");
    }

    public InvalidBoardException(String why) {
        super(why);
    }
}
