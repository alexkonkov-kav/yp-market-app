package com.market.service;

import com.market.mapper.OrderMapper;
import com.market.model.Order;
import com.market.model.OrderItem;
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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Test
    void get_Order_By_Id_With_Items() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setTotalSum(1000L);
        OrderItem item1 = new OrderItem();
        item1.setId(10L);
        item1.setTitle("яблоко");
        OrderItem item2 = new OrderItem();
        item2.setId(11L);
        item2.setTitle("мяч");
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.just(item1, item2));
        StepVerifier.create(orderService.getOrderByIdWithItems(orderId))
                .assertNext(resultOrder -> {
                    assertThat(resultOrder.getId()).isEqualTo(orderId);
                    assertThat(resultOrder.getItems()).hasSize(2);
                    assertThat(resultOrder.getItems().get(0).getTitle()).isEqualTo("яблоко");
                })
                .verifyComplete();
        verify(orderRepository).findById(orderId);
        verify(orderItemRepository).findByOrderId(orderId);
    }

    @Test
    void get_All_Order_With_Items() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(101L);
        orderItem1.setOrderId(1L);
        orderItem1.setTitle("яблоко");
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(102L);
        orderItem2.setOrderId(2L);
        orderItem2.setTitle("мяч");
        when(orderRepository.findAll()).thenReturn(Flux.just(order1, order2));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Flux.just(orderItem1));
        when(orderItemRepository.findByOrderId(2L)).thenReturn(Flux.just(orderItem2));
        StepVerifier.create(orderService.getAllOrderWithItems())
                .recordWith(ArrayList::new)
                .expectNextCount(2)
                .consumeRecordedWith(results -> {
                    assertThat(results).hasSize(2);
                    Order res1 = results.stream().filter(o -> o.getId().equals(1L)).findFirst().orElseThrow();
                    assertThat(res1.getItems()).hasSize(1);
                    assertThat(res1.getItems().get(0).getTitle()).isEqualTo("яблоко");
                    Order res2 = results.stream().filter(o -> o.getId().equals(2L)).findFirst().orElseThrow();
                    assertThat(res2.getItems()).hasSize(1);
                    assertThat(res2.getItems().get(0).getTitle()).isEqualTo("мяч");
                })
                .verifyComplete();
        verify(orderRepository).findAll();
        verify(orderItemRepository, times(2)).findByOrderId(anyLong());
    }
}
