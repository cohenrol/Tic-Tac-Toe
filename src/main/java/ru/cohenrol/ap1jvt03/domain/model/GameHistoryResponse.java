package ru.cohenrol.ap1jvt03.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GameHistoryResponse {
    private UUID uuid;
    private String status;
    private String opponentType;
    private UUID xxPlayerUuid;
    private UUID ooPlayerUuid;
    private LocalDateTime createdAt;

    public static GameHistoryResponse from(GameEntity g) {
        return new GameHistoryResponse(
            g.getUuid(),
            g.getStatus().name(),
            g.getOpponentType().name(),
            g.getXxPlayer() != null ? g.getXxPlayer().getUuid() : null,
            g.getOoPlayer() != null ? g.getOoPlayer().getUuid() : null,
            g.getCreatedAt()
        );
    }
}