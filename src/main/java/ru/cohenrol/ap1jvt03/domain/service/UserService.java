package ru.cohenrol.ap1jvt03.domain.service;

import org.springframework.data.domain.Pageable;
import ru.cohenrol.ap1jvt03.datasource.model.UserEntity;
import ru.cohenrol.ap1jvt03.domain.model.UserInfoResponse;
import ru.cohenrol.ap1jvt03.domain.model.request.GetMyCurrentGamesRequest;
import ru.cohenrol.ap1jvt03.domain.model.response.GetMyCurrentGamesResponse;
import ru.cohenrol.ap1jvt03.domain.model.response.GetUserResponse;

import java.util.UUID;

public interface UserService {
    public UserEntity getCurrentUser();

    GetUserResponse getUserByUuid(UUID uuid);

    GetMyCurrentGamesResponse getMyCurrentGames(GetMyCurrentGamesRequest domain, Pageable pageable);

    UserInfoResponse getCurrentUserInfo();
}
