package ru.cohenrol.ap1jvt03.domain.model.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String login;
    private String password;
}
