package ru.cohenrol.ap1jvt03.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.cohenrol.ap1jvt03.domain.enums.Role;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private UUID uuid;
    private String login;
    private List<Role> roles;
}