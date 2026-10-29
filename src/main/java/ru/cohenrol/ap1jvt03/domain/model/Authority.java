package ru.cohenrol.ap1jvt03.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import ru.cohenrol.ap1jvt03.domain.enums.Role;

@Getter
@AllArgsConstructor
public class Authority implements GrantedAuthority {
    private final Role role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}