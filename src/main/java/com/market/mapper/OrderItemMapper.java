package com.market.mapper;

import com.market.dto.OrderItemDto;
import com.market.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemMapper() {
    }

    public OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getTitle(),
                orderItem.getPrice(),
                orderItem.getCount()
        );
    }
}
