package ru.cohenrol.ap1jvt03.domain.model.response;

import lombok.Data;

import java.util.List;

@Data
public class GetGamesResponse {
    private List<GetGameResponse> games;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}