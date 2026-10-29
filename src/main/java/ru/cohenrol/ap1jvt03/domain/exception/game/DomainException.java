package ru.cohenrol.ap1jvt03.domain.exception.game;

public abstract sealed class DomainException extends RuntimeException
    permits GameAlreadyFinishedException, GameNotExistsException, IlligalGameException, InvalidBoardException, WrongMoveRequestException
{
    public DomainException(String why) {
        super(why);
    }
}
