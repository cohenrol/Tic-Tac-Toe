package ru.cohenrol.ap1jvt03.domain.exception.auth;

public final class WrongLoginOrPassword extends AuthException {
    public WrongLoginOrPassword() {
        super("Wrong login or password.");
    }

    public WrongLoginOrPassword(String why) {
        super(why);
    }
}
