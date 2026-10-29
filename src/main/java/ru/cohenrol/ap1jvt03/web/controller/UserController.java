package ru.cohenrol.ap1jvt03.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.cohenrol.ap1jvt03.domain.ServiceGate;
import ru.cohenrol.ap1jvt03.web.model.response.GetUserResponseDto;

import java.util.UUID;

@Controller()
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final ServiceGate services;

    @GetMapping("/{uuid}")
    public ResponseEntity<GetUserResponseDto> getUuid(
        @PathVariable UUID uuid
    ) {
        GetUserResponseDto getUserResponseDto = services.getUserByUuid(uuid);
        return ResponseEntity.ok(getUserResponseDto);
    }

//    @GetMapping("/games")
//    public ResponseEntity<GetMyCurrentGamesResponseDto> getMyCurrentGames(
//        @PageableDefault(page = 0, size = 10) Pageable pageable,
//        @Valid @RequestBody GetMyCurrentGamesRequestDto requestDto
//    ) {
//        GetMyCurrentGamesResponseDto responseDto = services.getMyCurrentGames(requestDto, pageable);
//        return ResponseEntity.ok(responseDto);
//    }
}