package ru.cohenrol.ap1jvt03.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetMyCurrentGamesResponse {
    private List<GetGameResponse> games;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}