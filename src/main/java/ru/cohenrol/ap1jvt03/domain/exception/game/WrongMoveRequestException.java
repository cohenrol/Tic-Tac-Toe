package ru.cohenrol.ap1jvt03.domain.exception.game;

public final class WrongMoveRequestException extends DomainException {
    public WrongMoveRequestException() {
        super("Wrong move request.");
    }

    public WrongMoveRequestException(String why) {
        super(why);
    }
}
