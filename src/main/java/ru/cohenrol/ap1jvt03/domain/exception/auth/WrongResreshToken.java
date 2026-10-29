package ru.cohenrol.ap1jvt03.domain.exception.auth;

public final class WrongResreshToken extends AuthException {
    public WrongResreshToken() {
        super("Wrong refresh token.");
    }

    public WrongResreshToken(String why) {
        super(why);
    }
}
