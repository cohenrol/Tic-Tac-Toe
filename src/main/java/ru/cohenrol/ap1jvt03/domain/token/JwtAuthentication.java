package ru.cohenrol.ap1jvt03.domain.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.cohenrol.ap1jvt03.domain.model.Authority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class JwtAuthentication implements Authentication {

    private final UUID uuid;
    private final List<Authority> authorities;
    private boolean authenticated;

    public JwtAuthentication(UUID uuid, List<Authority> authorities) {
        this.uuid = uuid;
        this.authorities = authorities;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getDetails() { return null; }

    @Override
    public Object getPrincipal() { return uuid; }

    @Override
    public boolean isAuthenticated() { return authenticated; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) { this.authenticated = isAuthenticated; }

    @Override
    public String getName() { return uuid.toString(); }
}