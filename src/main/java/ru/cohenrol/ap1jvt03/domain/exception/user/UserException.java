package ru.cohenrol.ap1jvt03.domain.exception.user;

public abstract sealed class UserException extends RuntimeException
    permits UserAlreadyExists, UserNotExists
{
    public UserException(String why) {
        super(why);
    }
}
