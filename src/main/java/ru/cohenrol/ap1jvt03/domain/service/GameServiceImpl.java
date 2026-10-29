package ru.cohenrol.ap1jvt03.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.ap1jvt03.datasource.mapper.DatasourceMapper;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.datasource.model.UserEntity;
import ru.cohenrol.ap1jvt03.datasource.repository.GameRepository;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;
import ru.cohenrol.ap1jvt03.domain.enums.GameOpponent;
import ru.cohenrol.ap1jvt03.domain.enums.GameStatus;
import ru.cohenrol.ap1jvt03.domain.exception.game.GameAlreadyFinishedException;
import ru.cohenrol.ap1jvt03.domain.exception.game.GameNotExistsException;
import ru.cohenrol.ap1jvt03.domain.exception.game.IlligalGameException;
import ru.cohenrol.ap1jvt03.domain.exception.game.WrongMoveRequestException;
import ru.cohenrol.ap1jvt03.domain.model.GameHistoryResponse;
import ru.cohenrol.ap1jvt03.domain.model.GamesHistoryResponse;
import ru.cohenrol.ap1jvt03.domain.model.LeaderboardEntry;
import ru.cohenrol.ap1jvt03.domain.model.LeaderboardResponse;
import ru.cohenrol.ap1jvt03.domain.model.request.CreateGameRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.GetGameRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.GetGamesRequest;
import ru.cohenrol.ap1jvt03.domain.model.request.MakeGameMoveRequest;
import ru.cohenrol.ap1jvt03.domain.model.response.*;
import ru.cohenrol.ap1jvt03.web.mapper.WebMapper;

import java.util.List;
import java.util.UUID;

import static ru.cohenrol.ap1jvt03.domain.enums.CellStatus.O;
import static ru.cohenrol.ap1jvt03.domain.enums.CellStatus.X;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final MinimaxServiceImpl minimaxServiceImpl;
    private final DatasourceMapper datasourceMapper;
    private final WebMapper webMapper;
    private final UserService userServiceImpl;
    private final AuthServiceImpl authServiceImpl;

    @Override
    @Transactional
    public MakeGameMoveResponse makeGameMove(UUID uuid, MakeGameMoveRequest makeGameMoveRequest) {
        GameEntity gameEntity = gameRepository.findByUuid(uuid).orElseThrow(GameNotExistsException::new);

        if (gameEntity.isGameOver()) {
            throw new GameAlreadyFinishedException();
        }

        validateCurrentUser(gameEntity);
        validNewCellsValid(gameEntity, makeGameMoveRequest.getCells());
        gameEntity.setCells(makeGameMoveRequest.getCells());

        if (checkIfGameOver(makeGameMoveRequest.getCells())) {
            if (hasWinner(gameEntity.getCells())) {
                if (gameEntity.getStatus() == GameStatus.X_MOVES_NEXT) {
                    gameEntity.setStatus(GameStatus.X_WINS);
                } else {
                    gameEntity.setStatus(GameStatus.O_WINS);
                }
            } else {
                gameEntity.setStatus(GameStatus.DRAW);
            }
        } else {
            if (gameEntity.getOpponentType() == GameOpponent.COMPUTER) {
                minimaxServiceImpl.findAndApplyBestMove(gameEntity);

                if (checkIfGameOver(gameEntity.getCells())) {
                    if (hasWinner(gameEntity.getCells())) {
                        gameEntity.setStatus(GameStatus.O_WINS);
                    } else {
                        gameEntity.setStatus(GameStatus.DRAW);
                    }
                } else {
                    gameEntity.setStatus(GameStatus.X_MOVES_NEXT);
                }
            } else {
                if (gameEntity.getStatus() == GameStatus.X_MOVES_NEXT) {
                    gameEntity.setStatus(GameStatus.O_MOVES_NEXT);
                } else {
                    gameEntity.setStatus(GameStatus.X_MOVES_NEXT);
                }
            }
        }

        gameRepository.save(gameEntity);
        MakeGameMoveResponse response = webMapper.toMakeGameMoveResponse(gameEntity);
        if (gameEntity.getStatus() == GameStatus.DRAW) {
            response.setMakesNextMove(null);
        } else if (gameEntity.getStatus() == GameStatus.X_MOVES_NEXT){
            response.setMakesNextMove(response.getXxPlayerUuid());
        } else {
            response.setMakesNextMove(response.getOoPlayerUuid());
        }

        return response;
    }

    @Transactional
    @Override
    public void validateCurrentUser(GameEntity gameEntity) {
        UserEntity userEntity = userServiceImpl.getCurrentUser();
        if (gameEntity.getStatus() == GameStatus.X_MOVES_NEXT && gameEntity.getXxPlayer().getUuid() != userEntity.getUuid()){
            throw new WrongMoveRequestException("It's not your move now, it's X's.");
        }
        if (gameEntity.getStatus() == GameStatus.O_MOVES_NEXT && gameEntity.getOoPlayer().getUuid() != userEntity.getUuid()){
            throw new WrongMoveRequestException("It's not your move now, it's O's.");
        }
    }

    @Override
    @Transactional
    public GetGamesResponse getGames(GetGamesRequest domain, Pageable pageable) {
        Page<GameEntity> gamesPage = gameRepository.findAllWaitingHumanGames(pageable);
        return webMapper.toGetGamesResponse(gamesPage);
    }

    @Override
    @Transactional
    public JoinGameResponse joinGame(UUID gameUuid) {
        GameEntity gameEntity = gameRepository.findByUuid(gameUuid).orElseThrow(GameNotExistsException::new);
        UserEntity userEntity = userServiceImpl.getCurrentUser();

        if (gameEntity.getXxPlayer() != null
            && gameEntity.getXxPlayer().getUuid().equals(userEntity.getUuid())) {
            throw new WrongMoveRequestException("один и тот же id");  // или другой эксепшн
        }

        if (gameEntity.getOoPlayer() != null) {
            throw new WrongMoveRequestException("игра уже занята");    //
        }

        gameEntity.setOoPlayer(userEntity);
        gameEntity.setStatus(GameStatus.X_MOVES_NEXT);
        gameRepository.save(gameEntity);
        JoinGameResponse response = webMapper.toJoinGameResponse(gameEntity);
        response.setMakesNextMove(response.getXxPlayerUuid());
        return response;
    }

    @Override
    @Transactional
    public CreateGameResponse createGame(CreateGameRequest createGameRequest) {
        UserEntity userEntity = userServiceImpl.getCurrentUser();
        if (createGameRequest.getOpponent() == GameOpponent.COMPUTER) {
            GameEntity gameEntity = gameRepository.save(
                GameEntity.createGameWithComputer(userEntity)
            );
            return webMapper.toCreateGameResponse(gameEntity);
        } else {
            GameEntity gameEntity = gameRepository.save(
                GameEntity.createGameWithHuman(userEntity)
            );
            CreateGameResponse response = webMapper.toCreateGameResponse(gameEntity);
            response.setMakesNextMove(userEntity.getUuid());
            return response;
        }
    }

    @Override
    @Transactional
    public GetGameResponse getGame(UUID gameUuid, GetGameRequest domain) {
        GameEntity gameEntity = gameRepository.findByUuid(gameUuid).orElseThrow(GameNotExistsException::new);
        GetGameResponse response = webMapper.toGetGameResponse(gameEntity);

        if (gameEntity.getStatus() == GameStatus.DRAW) {
            response.setMakesNextMove(null);
        } else if (gameEntity.getStatus() == GameStatus.X_MOVES_NEXT){
            response.setMakesNextMove(response.getXxPlayerUuid());
        } else {
            response.setMakesNextMove(response.getOoPlayerUuid());
        }

        return response;
    }

    @Override
    public void validNewCellsValid(GameEntity oldGameEntity, CellStatus[][] beingVerifiedCells) {
        CellStatus[][] savedGameCells = oldGameEntity.getCells();
        int size = savedGameCells.length;
        int newMovesCount = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                CellStatus prevCell = savedGameCells[i][j];
                CellStatus currCell = beingVerifiedCells[i][j];

                if (prevCell != CellStatus.EMPTY && prevCell != currCell) {
                    throw new WrongMoveRequestException();
                }

                if (prevCell == CellStatus.EMPTY && currCell != CellStatus.EMPTY) {
                    if (oldGameEntity.getStatus() == GameStatus.X_MOVES_NEXT && currCell != X) {
                        throw new IlligalGameException("You must use the X");
                    } else if (oldGameEntity.getStatus() == GameStatus.O_MOVES_NEXT && currCell != O) {
                        throw new IlligalGameException("You must use the O");
                    }
                    newMovesCount++;
                }
            }
        }

        if (newMovesCount != 1) {
            throw new WrongMoveRequestException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GamesHistoryResponse getHistory() {
        UUID uuid = authServiceImpl.getCurrentUserUuid();
        List<GameHistoryResponse> histories = getFinishedGames(uuid).stream()
            .map(GameHistoryResponse::from)
            .toList();
        return new GamesHistoryResponse(histories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameEntity> getFinishedGames(UUID userUuid) {
        return gameRepository.findFinishedGamesByUserUuid(
            userUuid,
            List.of(GameStatus.X_WINS, GameStatus.O_WINS, GameStatus.DRAW)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LeaderboardResponse leaderboard(int n) {
        List<LeaderboardEntry> players = gameRepository.findTopPlayers(n).stream()
            .map(p -> new LeaderboardEntry(p.getUuid(), p.getLogin(), p.getWinRatio()))
            .toList();
        return new LeaderboardResponse(players);
    }

    @Override
    public boolean checkIfGameOver(CellStatus[][] cells) {
        int size = cells.length;

        if (hasWinner(cells)) {
            return true;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == CellStatus.EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasWinner(CellStatus[][] board) {
        int size = board.length;

        for (int i = 0; i < size; i++) {
            CellStatus first = board[i][0];
            if (first == CellStatus.EMPTY) continue;

            boolean rowWin = true;
            for (int j = 1; j < size; j++) {
                if (board[i][j] != first) {
                    rowWin = false;
                    break;
                }
            }
            if (rowWin) return rowWin;
        }

        for (int j = 0; j < size; j++) {
            CellStatus first = board[0][j];
            if (first == CellStatus.EMPTY) continue;

            boolean colWin = true;
            for (int i = 1; i < size; i++) {
                if (board[i][j] != first) {
                    colWin = false;
                    break;
                }
            }
            if (colWin) return true;
        }

        CellStatus mainFirst = board[0][0];
        if (mainFirst != CellStatus.EMPTY) {
            boolean mainDiagWin = true;
            for (int i = 1; i < size; i++) {
                if (board[i][i] != mainFirst) {
                    mainDiagWin = false;
                    break;
                }
            }
            if (mainDiagWin) return true;
        }

        CellStatus sideFirst = board[0][size - 1];
        if (sideFirst != CellStatus.EMPTY) {
            boolean sideDiagWin = true;
            for (int i = 1; i < size; i++) {
                if (board[i][size - 1 - i] != sideFirst) {
                    sideDiagWin = false;
                    break;
                }
            }
            if (sideDiagWin) return true;
        }

        return false;
    }
}