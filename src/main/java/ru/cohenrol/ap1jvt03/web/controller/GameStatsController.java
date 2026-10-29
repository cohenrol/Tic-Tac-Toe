package ru.cohenrol.ap1jvt03.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.cohenrol.ap1jvt03.domain.ServiceGate;
import ru.cohenrol.ap1jvt03.web.model.GamesHistoryResponseDto;
import ru.cohenrol.ap1jvt03.web.model.LeaderboardResponseDto;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class GameStatsController {

    private final ServiceGate serviceGate;

    @GetMapping("/game/history")
    public ResponseEntity<GamesHistoryResponseDto> getHistory() {
        GamesHistoryResponseDto responseDto = serviceGate.getHistory();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/game/leaderboard")
    public ResponseEntity<LeaderboardResponseDto> leaderboard(
        @RequestParam("n") int n
    ) {
        LeaderboardResponseDto responseDto = serviceGate.leaderboard(n);
        return ResponseEntity.ok(responseDto);
    }
}