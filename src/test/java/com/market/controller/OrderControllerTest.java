package com.market.controller;

import com.market.dto.OrderItemDto;
import com.market.dto.OrderResponseDto;
import com.market.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void get_Orders_Page() throws Exception {
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderItemDto oiDto2 = new OrderItemDto(2L, "мяч", 300, 1);
        OrderResponseDto oDto1 = new OrderResponseDto(1L, List.of(oiDto1), 50);
        OrderResponseDto oDto2 = new OrderResponseDto(2L, List.of(oiDto2), 350);
        List<OrderResponseDto> mockOrders = List.of(oDto1, oDto2);
        when(orderService.getAllOrders()).thenReturn(mockOrders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("orders", mockOrders));
    }

    @Test
    void get_Order_Page_With_Default_Value() throws Exception {
        Long orderId = 1L;
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderItemDto oiDto2 = new OrderItemDto(2L, "мяч", 300, 1);
        OrderResponseDto mockDto = new OrderResponseDto(1L, List.of(oiDto1, oiDto2), 350);

        when(orderService.getOrderResponseById(orderId)).thenReturn(mockDto);
        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("order", mockDto))
                .andExpect(model().attribute("newOrder", false));
    }

    @Test
    void get_Order_Page_With_New_Order_True() throws Exception {
        Long orderId = 1L;
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderItemDto oiDto2 = new OrderItemDto(2L, "мяч", 300, 1);
        OrderResponseDto mockDto = new OrderResponseDto(1L, List.of(oiDto1, oiDto2), 350);

        when(orderService.getOrderResponseById(orderId)).thenReturn(mockDto);
        mockMvc.perform(get("/orders/{id}", orderId).param("newOrder", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("order", mockDto))
                .andExpect(model().attribute("newOrder", true));
    }
}
