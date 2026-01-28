package com.market.dto;

public record PagingResponseDto(int pageSize, int pageNumber, boolean hasPrevious, boolean hasNext) {
}
