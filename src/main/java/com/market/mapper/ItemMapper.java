package com.market.mapper;

import com.market.dto.ItemResponseDto;
import com.market.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemMapper() {
    }

    public ItemResponseDto mapToItemResponseDto(Item item, int count) {
        return new ItemResponseDto(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice(),
                count
        );
    }
}
