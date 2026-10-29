package ru.cohenrol.ap1jvt03.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaderboardEntryDto {
    private UUID uuid;
    private String login;
    private Double winRatio;
}