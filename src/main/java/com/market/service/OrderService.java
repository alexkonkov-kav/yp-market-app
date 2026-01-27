package com.market.service;

import com.market.dto.OrderResponseDto;
import com.market.mapper.OrderMapper;
import com.market.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAllWithItems().stream()
                .map(orderMapper::mapToOrderResponseDto)
                .toList();
    }
}
