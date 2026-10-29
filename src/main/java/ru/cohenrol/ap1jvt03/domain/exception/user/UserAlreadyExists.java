package ru.cohenrol.ap1jvt03.domain.exception.user;

public final class UserAlreadyExists extends UserException {
    public UserAlreadyExists() {
        super("User already exists.");
    }

    public UserAlreadyExists(String why) {
        super(why);
    }
}
