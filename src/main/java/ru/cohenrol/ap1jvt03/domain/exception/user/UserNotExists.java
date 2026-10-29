package ru.cohenrol.ap1jvt03.domain.exception.user;

public final class UserNotExists extends UserException {
    public UserNotExists() {
        super("User does not exists.");
    }

    public UserNotExists(String why) {
        super(why);
    }
}
