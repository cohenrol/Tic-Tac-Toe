package ru.cohenrol.ap1jvt03.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetMyCurrentGamesResponseDto {
    private List<GetGameResponseDto> games;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}