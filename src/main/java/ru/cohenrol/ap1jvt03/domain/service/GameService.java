package ru.cohenrol.ap1jvt03.domain.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;
import ru.cohenrol.ap1jvt03.domain.model.GamesHistoryResponse;
import ru.cohenrol.ap1jvt03.domain.model.LeaderboardResponse;
import ru.cohenrol.ap1jvt03.domain.model.request.CreateGameRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.GetGameRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.GetGamesRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.MakeGameMoveRequest;
import ru.cohenrol.ap1jvt03.domain.model.response.*;

import java.util.List;
import java.util.UUID;

@Service
public interface GameService {

    CreateGameResponse createGame(CreateGameRequest createGameRequest);

    GetGameResponse getGame(UUID uuid, GetGameRequest domain);

    MakeGameMoveResponse makeGameMove(UUID uuid, MakeGameMoveRequest domain);

    void validateCurrentUser(GameEntity gameEntity);

    GetGamesResponse getGames(GetGamesRequest domain, Pageable pageable);

    JoinGameResponse joinGame(UUID uuid);

    void validNewCellsValid(GameEntity oldGameEntity, CellStatus[][] beingVerifiedCells);

    boolean checkIfGameOver(CellStatus[][] cells);

    GamesHistoryResponse getHistory();

    List<GameEntity> getFinishedGames(UUID userUuid);

    LeaderboardResponse leaderboard(int n);
}
