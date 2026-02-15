package com.market.service;

import com.market.dto.OrderResponseDto;
import com.market.mapper.OrderMapper;
import com.market.model.Order;
import com.market.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findByIdlWithItems(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not found Order with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public Flux<OrderResponseDto> getAllOrders() {
        return orderRepository.findAllWithItems()
                .map(orderMapper::mapToOrderResponseDto);
    }

    @Transactional(readOnly = true)
    public Mono<OrderResponseDto> getOrderResponseById(Long id) {
        return getOrderById(id)
                .map(orderMapper::mapToOrderResponseDto);
    }
}
