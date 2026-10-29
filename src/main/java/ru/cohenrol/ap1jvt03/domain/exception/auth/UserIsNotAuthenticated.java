package ru.cohenrol.ap1jvt03.domain.exception.auth;

public final class UserIsNotAuthenticated extends AuthException {
    public UserIsNotAuthenticated() {
        super("User is not authenticated.");
    }

    public UserIsNotAuthenticated(String why) {
        super(why);
    }
}
