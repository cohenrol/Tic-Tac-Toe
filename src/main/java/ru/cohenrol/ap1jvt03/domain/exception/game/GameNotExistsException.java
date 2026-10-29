package ru.cohenrol.ap1jvt03.domain.exception.game;

public final class GameNotExistsException extends DomainException {
    public GameNotExistsException() {
        super("Game not exists.");
    }

    public GameNotExistsException(String why) {
        super(why);
    }
}
