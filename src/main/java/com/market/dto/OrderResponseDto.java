package com.market.dto;

import java.util.List;

public record OrderResponseDto(Long id, List<OrderItemDto> items, long totalSum) {
}
