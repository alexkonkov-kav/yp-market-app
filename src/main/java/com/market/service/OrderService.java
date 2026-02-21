package com.market.service;

import com.market.dto.OrderResponseDto;
import com.market.mapper.OrderMapper;
import com.market.model.Order;
import com.market.repository.OrderItemRepository;
import com.market.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public Mono<Order> getOrderByIdWithItems(Long orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not found Order with ID: " + orderId)))
                .flatMap(o -> orderItemRepository.findByOrderId(orderId)
                        .collectList()
                        .map(oi -> {
                            o.setItems(oi);
                            return o;
                        }).defaultIfEmpty(o));
    }

    @Transactional(readOnly = true)
    public Flux<Order> getAllOrderWithItems() {
        return orderRepository.findAll()
                .flatMap(o -> orderItemRepository.findByOrderId(o.getId())
                        .collectList()
                        .map(oi -> {
                            o.setItems(oi);
                            return o;
                        }).defaultIfEmpty(o));
    }

    @Transactional(readOnly = true)
    public Flux<OrderResponseDto> getAllOrders() {
        return getAllOrderWithItems()
                .map(orderMapper::mapToOrderResponseDto);
    }

    @Transactional(readOnly = true)
    public Mono<OrderResponseDto> getOrderResponseById(Long id) {
        return getOrderByIdWithItems(id)
                .map(orderMapper::mapToOrderResponseDto);
    }
}
