package ru.cohenrol.ap1jvt03.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GameHistoryResponseDto {
    private UUID uuid;
    private String status;
    private String opponentType;
    private UUID xxPlayerUuid;
    private UUID ooPlayerUuid;
    private LocalDateTime createdAt;
}