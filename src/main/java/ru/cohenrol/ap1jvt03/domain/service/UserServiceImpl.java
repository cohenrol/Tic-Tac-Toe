package ru.cohenrol.ap1jvt03.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.ap1jvt03.datasource.mapper.DatasourceMapper;
import ru.cohenrol.ap1jvt03.datasource.model.GameEntity;
import ru.cohenrol.ap1jvt03.datasource.model.UserEntity;
import ru.cohenrol.ap1jvt03.datasource.repository.GameRepository;
import ru.cohenrol.ap1jvt03.datasource.repository.UserRepository;
import ru.cohenrol.ap1jvt03.domain.exception.auth.UserIsNotAuthenticated;
import ru.cohenrol.ap1jvt03.domain.exception.user.UserNotExists;
import ru.cohenrol.ap1jvt03.domain.model.UserInfoResponse;
import ru.cohenrol.ap1jvt03.domain.model.request.GetMyCurrentGamesRequest;
import ru.cohenrol.ap1jvt03.domain.model.response.GetMyCurrentGamesResponse;
import ru.cohenrol.ap1jvt03.domain.model.response.GetUserResponse;
import ru.cohenrol.ap1jvt03.domain.token.JwtAuthentication;
import ru.cohenrol.ap1jvt03.web.mapper.WebMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final WebMapper webMapper;
    private final DatasourceMapper datasourceMapper;
    private final AuthService authService;

    @Override
    @Transactional
    public UserEntity getCurrentUser() {
        UUID uuid = authService.getCurrentUserUuid();
        return userRepository.findByUuid(uuid).orElseThrow(UserNotExists::new);
    }

    @Override
    public GetUserResponse getUserByUuid(UUID uuid) {
        UserEntity userEntity = userRepository.findByUuid(uuid).orElseThrow(UserNotExists::new);
        return new GetUserResponse(userEntity.getLogin());
    }

    @Override
    @Transactional
    public GetMyCurrentGamesResponse getMyCurrentGames(GetMyCurrentGamesRequest domain, Pageable pageable) {
        Page<GameEntity> gamesPage = gameRepository.findAllMyCurrentGames(
            getCurrentUser().getUuid(),
            pageable
        );
        return webMapper.toGetMyCurrentGamesResponse(gamesPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUserInfo() {
        JwtAuthentication auth = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UserIsNotAuthenticated();
        }

        UUID uuid = (UUID) auth.getPrincipal();
        UserEntity user = userRepository.findByUuid(uuid)
            .orElseThrow(UserNotExists::new);

        return new UserInfoResponse(user.getUuid(), user.getLogin(), user.getRoles());
    }
}
