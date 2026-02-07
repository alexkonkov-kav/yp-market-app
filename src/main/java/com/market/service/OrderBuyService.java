package com.market.service;

import com.market.model.CartItem;
import com.market.model.Order;
import com.market.model.OrderItem;
import com.market.repository.CartItemRepository;
import com.market.repository.OrderItemRepository;
import com.market.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderBuyService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderBuyService(CartItemRepository cartItemRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Long createOrder() {
        List<CartItem> cartItems = cartItemRepository.findAllWithItems();
        if (cartItems.isEmpty()) {
            throw new EntityNotFoundException("Cart is empty");
        }
        Order saveOrder = createOrder(cartItems);
        return saveOrder.getId();
    }

    private Order createOrder(List<CartItem> cartItems) {
        Order order = new Order();
        Long totalSum = cartItems.stream()
                .mapToLong(e -> e.getItem().getPrice() * e.getCount())
                .sum();
        order.setTotalSum(totalSum);
        Order saveOrder = orderRepository.save(order);
        savedOrderItem(cartItems, saveOrder);
        deleteCartItem(cartItems);
        return saveOrder;
    }

    private void savedOrderItem(List<CartItem> cartItems, Order order) {
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
        orderItemRepository.saveAll(orderItems);
    }

    private void deleteCartItem(List<CartItem> cartItems) {
        cartItemRepository.deleteAll(cartItems);
    }
}
