package ru.cohenrol.ap1jvt03.domain.exception.game;

public final class IlligalGameException extends DomainException {
    public IlligalGameException() {
        super("Illigal game.");
    }

    public IlligalGameException(String why) {
        super(why);
    }
}
