package com.market.service;

import com.market.dto.OrderResponseDto;
import com.market.mapper.OrderMapper;
import com.market.model.Order;
import com.market.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdlWithItems(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found Order with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAllWithItems().stream()
                .map(orderMapper::mapToOrderResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderResponseById(Long id) {
        Order order = getOrderById(id);
        return orderMapper.mapToOrderResponseDto(order);
    }
}
