package ru.cohenrol.ap1jvt03.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaderboardResponseDto {
    private List<LeaderboardEntryDto> players;
}