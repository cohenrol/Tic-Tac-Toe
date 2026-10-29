package ru.cohenrol.ap1jvt03.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.domain.model.*;
import ru.cohenrol.ap1jvt03.domain.model.request.*;
import ru.cohenrol.ap1jvt03.domain.model.response.*;
import ru.cohenrol.ap1jvt03.web.model.GameHistoryResponseDto;
import ru.cohenrol.ap1jvt03.web.model.GamesHistoryResponseDto;
import ru.cohenrol.ap1jvt03.web.model.LeaderboardResponseDto;
import ru.cohenrol.ap1jvt03.web.model.request.*;
import ru.cohenrol.ap1jvt03.web.model.response.*;

@Mapper(componentModel = "spring")
public interface WebMapper {
    SignUpRequest toDomain(SignUpRequestDto signUpRequestDto);
    LoginResponseDto toDto(LoginResponse loginResponse);

    CreateGameRequest toDomain(CreateGameRequestDto createGameRequestDto);
    CreateGameResponseDto toDto(CreateGameResponse game);

    GetGameRequest toDomain(GetGameRequestDto getGameRequestDto);
    GetGameResponseDto toDto(GetGameResponse game);

    MakeGameMoveRequest toDomain(MakeGameMoveRequestDto makeGameMoveRequestDto);
    MakeGameMoveResponseDto toDto(MakeGameMoveResponse makeGameMoveResponse);

    GetGamesRequest toDomain(GetGamesRequestDto getGamesRequestDto);
    GetGamesResponseDto toDto(GetGamesResponse games);

    JoinGameRequest toDomain(JoinGameRequestDto joinGameRequestDto);
    JoinGameResponseDto toDto(JoinGameResponse joinGameResponse);

    GetUserResponseDto toDto(GetUserResponse userByUuid);

    GetMyCurrentGamesRequest toDomain(GetMyCurrentGamesRequestDto requestDto);
    GetMyCurrentGamesResponseDto toDto(GetMyCurrentGamesResponse myCurrentGames);

    JwtRequest toDomain(JwtRequestDto jwtRequest);
    JwtResponseDto toDto(JwtResponse jwtRequest);

    RefreshJwtRequest toDomain(RefreshJwtRequestDto jwtRequestDto);

    UserInfoResponseDto toDto(UserInfoResponse userInfoResponse);
    GameHistoryResponseDto toDto(GameHistoryResponse gameHistoryResponse);
    GamesHistoryResponseDto toDto(GamesHistoryResponse gameHistoryResponse);
    LeaderboardResponseDto toDto(LeaderboardResponse gameHistoryResponse);

    @Mapping(target = "ooPlayerUuid", source = "ooPlayer.uuid")
    @Mapping(target = "xxPlayerUuid", source = "xxPlayer.uuid")
    @Mapping(target = "makesNextMove", ignore = true)
    CreateGameResponse toCreateGameResponse(GameEntity entity);

    @Mapping(target = "ooPlayerUuid", source = "ooPlayer.uuid")
    @Mapping(target = "xxPlayerUuid", source = "xxPlayer.uuid")
    @Mapping(target = "makesNextMove", ignore = true)
    MakeGameMoveResponse toMakeGameMoveResponse(GameEntity entity);

    @Mapping(target = "ooPlayerUuid", source = "ooPlayer.uuid")
    @Mapping(target = "xxPlayerUuid", source = "xxPlayer.uuid")
    @Mapping(target = "makesNextMove", ignore = true)
    GetGameResponse toGetGameResponse(GameEntity gameEntity);

    @Mapping(target = "ooPlayerUuid", source = "ooPlayer.uuid")
    @Mapping(target = "xxPlayerUuid", source = "xxPlayer.uuid")
    @Mapping(target = "makesNextMove", ignore = true)
    JoinGameResponse toJoinGameResponse(GameEntity gameEntity);

    @Mapping(target = "games", source = "content")
    @Mapping(target = "pageNumber", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    GetGamesResponse toGetGamesResponse(Page<GameEntity> gamesPage);

    @Mapping(target = "games", source = "content")
    @Mapping(target = "pageNumber", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    GetMyCurrentGamesResponse toGetMyCurrentGamesResponse(Page<GameEntity> gamesPage);
}
