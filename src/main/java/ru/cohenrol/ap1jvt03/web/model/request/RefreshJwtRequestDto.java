package ru.cohenrol.ap1jvt03.web.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RefreshJwtRequestDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String refreshToken;
}