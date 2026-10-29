package ru.cohenrol.ap1jvt03.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.cohenrol.ap1jvt03.domain.service.AuthServiceImpl;
import ru.cohenrol.ap1jvt03.domain.service.GameService;
import ru.cohenrol.ap1jvt03.domain.service.UserService;
import ru.cohenrol.ap1jvt03.web.mapper.WebMapper;
import ru.cohenrol.ap1jvt03.web.model.GamesHistoryResponseDto;
import ru.cohenrol.ap1jvt03.web.model.LeaderboardResponseDto;
import ru.cohenrol.ap1jvt03.web.model.request.*;
import ru.cohenrol.ap1jvt03.web.model.response.*;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceGate {
    private final GameService gameServiceImpl;
    private final AuthServiceImpl authServiceImpl;
    private final WebMapper webMapper;
    private final UserService userServiceImpl;

    public void register(SignUpRequestDto signUpRequestDto) {
        authServiceImpl.register(
            webMapper.toDomain(signUpRequestDto)
        );
    }

    public CreateGameResponseDto createGame(CreateGameRequestDto requestDto) {
        return webMapper.toDto(
            gameServiceImpl.createGame(
                webMapper.toDomain(requestDto)
            )
        );
    }

    public GetGameResponseDto getGame(UUID uuid, GetGameRequestDto requestDto) {
        return webMapper.toDto(
            gameServiceImpl.getGame(
                uuid,
                webMapper.toDomain(requestDto)
            )
        );
    }

    public MakeGameMoveResponseDto makeMove(UUID uuid, MakeGameMoveRequestDto requestDto) {
        return webMapper.toDto(
            gameServiceImpl.makeGameMove(
                uuid,
                webMapper.toDomain(requestDto)
            )
        );
    }

    public GetGamesResponseDto getGames(GetGamesRequestDto requestDto, Pageable pageable) {
        return webMapper.toDto(
            gameServiceImpl.getGames(
                webMapper.toDomain(requestDto),
                pageable
            )
        );
    }

    public JoinGameResponseDto joinGame(UUID uuid) {
        return webMapper.toDto(
            gameServiceImpl.joinGame(
                uuid
            )
        );
    }

    public GetUserResponseDto getUserByUuid(UUID uuid) {
        return webMapper.toDto(
            userServiceImpl.getUserByUuid(uuid)
        );
    }

    public GetMyCurrentGamesResponseDto getMyCurrentGames(GetMyCurrentGamesRequestDto requestDto, Pageable pageable) {
        return webMapper.toDto(
            userServiceImpl.getMyCurrentGames(
                webMapper.toDomain(requestDto),
                pageable
            )
        );
    }

    public JwtResponseDto login(JwtRequestDto requestDto) {
        return webMapper.toDto(
            authServiceImpl.login(
                webMapper.toDomain(requestDto)
            )
        );
    }

    public JwtResponseDto refreshAccessToken(RefreshJwtRequestDto requestDto) {
        return webMapper.toDto(
            authServiceImpl.refreshAccessToken(
                webMapper.toDomain(requestDto)
            )
        );
    }

    public JwtResponseDto refreshRefreshToken(RefreshJwtRequestDto requestDto) {
        return webMapper.toDto(
            authServiceImpl.refreshRefreshToken(
                webMapper.toDomain(requestDto)
            )
        );
    }

    public UserInfoResponseDto getCurrentUserInfo() {
        return webMapper.toDto(
            userServiceImpl.getCurrentUserInfo()
        );
    }

    public GamesHistoryResponseDto getHistory() {
        return webMapper.toDto(
            gameServiceImpl.getHistory()
        );
    }

    public LeaderboardResponseDto leaderboard(int n) {
        return webMapper.toDto(
            gameServiceImpl.leaderboard(n)
        );
    }
}
