package ru.cohenrol.ap1jvt03.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private UUID uuid;
    private String login;
    private Double winRatio;
}