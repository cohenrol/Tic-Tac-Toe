package ru.cohenrol.ap1jvt03.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cohenrol.ap1jvt03.domain.ServiceGate;
import ru.cohenrol.ap1jvt03.web.model.request.CreateGameRequestDto;
import ru.cohenrol.ap1jvt03.web.model.request.GetGameRequestDto;
import ru.cohenrol.ap1jvt03.web.model.request.GetGamesRequestDto;
import ru.cohenrol.ap1jvt03.web.model.request.MakeGameMoveRequestDto;
import ru.cohenrol.ap1jvt03.web.model.response.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final ServiceGate serviceGate;

    @PostMapping("/{uuid}")
    public ResponseEntity<MakeGameMoveResponseDto> makeMove(
        @PathVariable UUID uuid,
        @Valid @RequestBody MakeGameMoveRequestDto makeGameMoveRequestDto
    ) {
        makeGameMoveRequestDto.check();
        MakeGameMoveResponseDto responseDto = serviceGate.makeMove(uuid, makeGameMoveRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<CreateGameResponseDto> createGame(
        @Valid @RequestBody CreateGameRequestDto createGameRequestDto
    ) {
        CreateGameResponseDto responseDto = serviceGate.createGame(createGameRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GetGameResponseDto> getGame(
        @PathVariable UUID uuid
//        ,
//        @Valid @RequestBody GetGameRequestDto getGameRequestDto
    ) {
        GetGameRequestDto getGameRequestDto = null;
        GetGameResponseDto responseDto = serviceGate.getGame(uuid, getGameRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<GetGamesResponseDto> getGames(
        @PageableDefault(page = 0, size = 10) Pageable pageable
//        ,
//        @Valid @RequestBody GetGamesRequestDto getGamesRequestDto
    ) {
        GetGamesRequestDto getGamesRequestDto = null;
        GetGamesResponseDto responseDto = serviceGate.getGames(getGamesRequestDto, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{uuid}/join")
    public ResponseEntity<JoinGameResponseDto> joinGame(
        @PathVariable UUID uuid
    ) {
        JoinGameResponseDto responseDto = serviceGate.joinGame(uuid);
        return ResponseEntity.ok(responseDto);
    }


}