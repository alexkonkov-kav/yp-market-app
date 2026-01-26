package com.market.dto;

public record ItemResponseDto(Long id, String title, String description, String imgPath, long price, long count) {
}
