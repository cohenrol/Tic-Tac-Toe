package ru.cohenrol.ap1jvt03.datasource.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cohenrol.ap1jvt03.datasource.mapper.CellArrayConverter;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;
import ru.cohenrol.ap1jvt03.domain.enums.GameOpponent;
import ru.cohenrol.ap1jvt03.domain.enums.GameStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "games")
@NoArgsConstructor
@Data
public class GameEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID uuid;

    @Convert(converter = CellArrayConverter.class)
    @Column(name = "cells", columnDefinition = "TEXT", nullable = false)
    private CellStatus[][] cells;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "opponent_type", nullable = false)
    private GameOpponent opponentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "x_player_id", nullable = false)
    private UserEntity xxPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "y_player_id")
    private UserEntity ooPlayer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public GameEntity(CellStatus[][] cells, GameStatus status, GameOpponent opponentType, UserEntity xxPlayer, UserEntity ooPlayer) {
        this.cells = cells;
        this.status = status;
        this.opponentType = opponentType;
        this.xxPlayer = xxPlayer;
        this.ooPlayer = ooPlayer;
    }

    public static GameEntity createGameWithComputer(UserEntity xxPlayer) {
        return createGameWith(xxPlayer, GameOpponent.COMPUTER);
    }

    public static GameEntity createGameWithHuman(UserEntity xxPlayer) {
        return createGameWith(xxPlayer, GameOpponent.HUMAN);
    }

    public static GameEntity createGameWith(UserEntity xxPlayer, GameOpponent opponentType) {
        return new GameEntity(
            CellStatus.createEmptyGameBoard3x3(),
            opponentType == GameOpponent.HUMAN ? GameStatus.WAITING_FOR_OPPONENT : GameStatus.X_MOVES_NEXT,
            opponentType,
            xxPlayer,
            null
        );
    }

    public boolean isGameOver() {
        return this.status == GameStatus.DRAW || this.status == GameStatus.O_WINS || this.status == GameStatus.X_WINS;
    }

}
