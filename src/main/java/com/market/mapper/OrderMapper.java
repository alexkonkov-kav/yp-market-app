package com.market.mapper;

import com.market.dto.OrderResponseDto;
import com.market.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public OrderResponseDto mapToOrderResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getItems().stream()
                        .map(orderItemMapper::mapToOrderItemDto)
                        .toList(),
                order.getTotalSum()
        );
    }
}
