package ru.cohenrol.ap1jvt03.domain.exception.auth;

public abstract sealed class AuthException extends RuntimeException
    permits WrongLoginOrPassword, WrongResreshToken, UserIsNotAuthenticated
{
    public AuthException(String why) {
        super(why);
    }
}
