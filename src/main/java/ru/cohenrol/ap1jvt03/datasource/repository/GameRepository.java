package ru.cohenrol.ap1jvt03.datasource.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.domain.enums.GameOpponent;
import ru.cohenrol.ap1jvt03.domain.enums.GameStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<GameEntity, UUID> {
    Optional<GameEntity> findByUuid(UUID uuid);

    Page<GameEntity> findAllByOpponentTypeAndStatus(GameOpponent opponentType, GameStatus status, Pageable pageable);
    default Page<GameEntity> findAllWaitingHumanGames(Pageable pageable) {
        return findAllByOpponentTypeAndStatus(
            GameOpponent.HUMAN,
            GameStatus.WAITING_FOR_OPPONENT,
            pageable
        );
    }

    Page<GameEntity> findAllByUuidAndStatusIn(UUID uuid, Collection<GameStatus> statuses, Pageable pageable);
    default Page<GameEntity> findAllMyCurrentGames(UUID uuid, Pageable pageable) {
        return findAllByUuidAndStatusIn(
            uuid,
            java.util.List.of(
                GameStatus.WAITING_FOR_OPPONENT,
                GameStatus.X_MOVES_NEXT,
                GameStatus.O_MOVES_NEXT
            ),
            pageable
        );
    }

    @Query("""
        SELECT g FROM GameEntity g
        LEFT JOIN g.xxPlayer xp
        LEFT JOIN g.ooPlayer op
        WHERE g.status IN :finishedStatuses
          AND (xp.uuid = :uuid OR op.uuid = :uuid)
        ORDER BY g.createdAt DESC
        """)
    List<GameEntity> findFinishedGamesByUserUuid(
        @Param("uuid") UUID uuid,
        @Param("finishedStatuses") List<GameStatus> finishedStatuses
    );

    @Query(value = """
        WITH stats AS (
            SELECT u.id, u.uuid, u.login,
                   SUM(CASE WHEN (g.status='X_WINS' AND g.x_player_id=u.id)
                             OR (g.status='O_WINS' AND g.y_player_id=u.id) THEN 1 ELSE 0 END) AS wins,
                   COUNT(g.id) AS total_games
            FROM users u
            LEFT JOIN games g ON (g.x_player_id=u.id OR g.y_player_id=u.id)
                AND g.status IN ('X_WINS','O_WINS','DRAW')
            GROUP BY u.id, u.uuid, u.login
        )
        SELECT uuid, login,
               CASE WHEN total_games=0 THEN 0
                    ELSE wins::float / total_games END AS win_ratio
        FROM stats
        ORDER BY win_ratio DESC
        LIMIT :n
        """, nativeQuery = true)
    List<LeaderboardEntryProjection> findTopPlayers(@Param("n") int n);
}
