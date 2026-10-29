package ru.cohenrol.ap1jvt03.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaderboardResponse {
    private List<LeaderboardEntry> players;
}