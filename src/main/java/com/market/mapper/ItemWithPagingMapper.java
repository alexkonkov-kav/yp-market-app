package com.market.mapper;

import com.market.dto.ItemWithPagingResponseDto;
import com.market.dto.PagingResponseDto;
import com.market.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemWithPagingMapper {

    private final ItemMapper itemMapper;

    public ItemWithPagingMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public ItemWithPagingResponseDto mapToItemWithPagingResponseDto(Item item, int count, int pageNumber, int pageSize, Long itemCount) {
        return new ItemWithPagingResponseDto(
                itemMapper.mapToItemResponseDto(item, count),
                createPagingResponseDto(pageNumber, pageSize, itemCount)
        );
    }

    private PagingResponseDto createPagingResponseDto(int pageNumber, int pageSize, Long itemCount) {
        boolean hasPrevious = pageNumber > 0;
        boolean hasNext = (long) (pageNumber + 1) * pageSize < itemCount;
        return new PagingResponseDto(pageSize, pageNumber, hasPrevious, hasNext);
    }
}
