package ru.cohenrol.ap1jvt03.datasource.repository;

import java.util.UUID;

public interface LeaderboardEntryProjection {
    UUID getUuid();
    String getLogin();
    Double getWinRatio();
}