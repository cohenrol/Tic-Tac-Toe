package ru.cohenrol.ap1jvt03.domain.exception.game;

public final class GameAlreadyFinishedException extends DomainException {
    public GameAlreadyFinishedException() {
        super("Game already finished.");
    }

    public GameAlreadyFinishedException(String why) {
        super(why);
    }
}
