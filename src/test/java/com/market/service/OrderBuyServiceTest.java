package com.market.service;

import com.market.model.CartItem;
import com.market.model.Item;
import com.market.model.Order;
import com.market.repository.CartItemRepository;
import com.market.repository.ItemRepository;
import com.market.repository.OrderItemRepository;
import com.market.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderBuyServiceTest {

    @InjectMocks
    private OrderBuyService orderBuyService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void create_Order() {
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item.setId(1L);
        CartItem cartItem = new CartItem(item, 1);
        List<CartItem> cartItems = List.of(cartItem);
        when(cartItemRepository.findAllWithItems()).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        Long orderId = orderBuyService.createOrder();
        assertThat(orderId).isEqualTo(1L);
        verify(orderRepository).save(argThat(order -> order.getTotalSum() == 50));
        verify(cartItemRepository).deleteAll(cartItems);
    }

    @Test
    void create_Order_Cart_Items_Is_Empty() {
        when(cartItemRepository.findAllWithItems()).thenReturn(List.of());
        assertThatThrownBy(() -> orderBuyService.createOrder())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Cart is empty");
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
    }
}
