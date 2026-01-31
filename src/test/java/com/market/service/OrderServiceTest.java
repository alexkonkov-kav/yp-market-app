package com.market.service;

import com.market.dto.OrderItemDto;
import com.market.dto.OrderResponseDto;
import com.market.mapper.OrderMapper;
import com.market.model.Order;
import com.market.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Test
    void get_Order_By_Id() {
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setTotalSum(1000L);
        when(orderRepository.findByIdlWithItems(orderId)).thenReturn(Optional.of(mockOrder));
        Order result = orderService.getOrderById(orderId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getTotalSum()).isEqualTo(1000L);
        verify(orderRepository).findByIdlWithItems(orderId);
    }

    @Test
    void get_Order_By_Id_Not_Found() {
        Long orderId = 100L;
        when(orderRepository.findByIdlWithItems(orderId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found Order with ID: " + orderId);
        verify(orderRepository).findByIdlWithItems(orderId);
    }

    @Test
    void shouldReturnListOfOrderResponseDtos() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderItemDto oiDto2 = new OrderItemDto(2L, "мяч", 300, 1);
        OrderResponseDto oDto1 = new OrderResponseDto(1L, List.of(oiDto1), 50);
        OrderResponseDto oDto2 = new OrderResponseDto(2L, List.of(oiDto2), 350);
        when(orderRepository.findAllWithItems()).thenReturn(List.of(order1, order2));
        when(orderMapper.mapToOrderResponseDto(order1)).thenReturn(oDto1);
        when(orderMapper.mapToOrderResponseDto(order2)).thenReturn(oDto2);
        List<OrderResponseDto> result = orderService.getAllOrders();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        verify(orderRepository, times(1)).findAllWithItems();
        verify(orderMapper, times(2)).mapToOrderResponseDto(any(Order.class));
    }

    @Test
    void get_Order_Response_By_Id() {
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderResponseDto mockDto = new OrderResponseDto(1L, List.of(oiDto1), 50);
        when(orderRepository.findByIdlWithItems(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderMapper.mapToOrderResponseDto(mockOrder)).thenReturn(mockDto);
        OrderResponseDto result = orderService.getOrderResponseById(orderId);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(orderId);
        assertThat(result.totalSum()).isEqualTo(50);
        verify(orderRepository).findByIdlWithItems(orderId);
        verify(orderMapper).mapToOrderResponseDto(mockOrder);
    }
}
