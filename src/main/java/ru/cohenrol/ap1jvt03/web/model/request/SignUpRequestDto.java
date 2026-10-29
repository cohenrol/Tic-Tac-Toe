package ru.cohenrol.ap1jvt03.web.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDto {
    @NotNull
    @Size(min = 6)
    private String login;

    @NotNull
    @Size(min = 6)
    private String password;
}
