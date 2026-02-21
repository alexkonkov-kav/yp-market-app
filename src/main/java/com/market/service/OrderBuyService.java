package com.market.service;

import com.market.model.CartItem;
import com.market.model.Order;
import com.market.model.OrderItem;
import com.market.repository.CartItemRepository;
import com.market.repository.OrderItemRepository;
import com.market.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderBuyService {

    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderBuyService(CartItemRepository cartItemRepository,
                           CartItemService cartItemService,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemService = cartItemService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Mono<Long> createOrder() {
        return cartItemService.getAlLCartItemWithItem()
                .collectList()
                .flatMap(e -> {
                    if (e.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Cart is empty"));
                    }
                    return createOrder(e);
                }).map(Order::getId);
    }

    private Mono<Order> createOrder(List<CartItem> cartItems) {
        Order order = new Order();
        Long totalSum = cartItems.stream()
                .mapToLong(e -> e.getItem().getPrice() * e.getCount())
                .sum();
        order.setTotalSum(totalSum);
        return orderRepository.save(order)
                .flatMap(e ->
                        savedOrderItem(cartItems, e)
                                .then(deleteCartItem(cartItems))
                                .thenReturn(e));
    }

    private Mono<Void> savedOrderItem(List<CartItem> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(cartItem.getItem().getId());
            orderItem.setTitle(cartItem.getItem().getTitle());
            orderItem.setPrice(cartItem.getItem().getPrice());
            orderItem.setCount(cartItem.getCount());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        return orderItemRepository.saveAll(orderItems).then();
    }

    private Mono<Void> deleteCartItem(List<CartItem> cartItems) {
        return cartItemRepository.deleteAll(cartItems);
    }
}
