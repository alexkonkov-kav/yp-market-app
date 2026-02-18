package com.market.service;

import com.market.model.CartItem;
import com.market.model.Item;
import com.market.model.Order;
import com.market.repository.CartItemRepository;
import com.market.repository.OrderItemRepository;
import com.market.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderBuyServiceTest {

    @InjectMocks
    private OrderBuyService orderBuyService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void createOrder_Success() {
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item.setId(1L);
        CartItem cartItem = new CartItem(item, 1);
        Order savedOrder = new Order();
        savedOrder.setId(500L);
        when(cartItemService.getAlLCartItemWithItem()).thenReturn(Flux.just(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));
        when(orderItemRepository.saveAll(anyIterable())).thenReturn(Flux.empty());
        when(cartItemRepository.deleteAll(anyIterable())).thenReturn(Mono.empty());
        StepVerifier.create(orderBuyService.createOrder()).expectNext(500L).verifyComplete();
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyIterable());
        verify(cartItemRepository).deleteAll(anyIterable());
    }

    @Test
    void createOrder_ThrowsException_WhenCartIsEmpty() {
        when(cartItemService.getAlLCartItemWithItem()).thenReturn(Flux.empty());
        StepVerifier.create(orderBuyService.createOrder())
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Cart is empty"))
                .verify();
        verify(orderRepository, never()).save(any());
    }
}
