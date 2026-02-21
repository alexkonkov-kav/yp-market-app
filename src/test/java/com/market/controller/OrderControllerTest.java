package com.market.controller;

import com.market.dto.OrderItemDto;
import com.market.dto.OrderResponseDto;
import com.market.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    void get_Orders_Page() {
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderItemDto oiDto2 = new OrderItemDto(2L, "мяч", 300, 1);
        OrderResponseDto oDto1 = new OrderResponseDto(1L, List.of(oiDto1), 50);
        OrderResponseDto oDto2 = new OrderResponseDto(2L, List.of(oiDto2), 350);
        when(orderService.getAllOrders()).thenReturn(Flux.just(oDto1, oDto2));
        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("1");
                    assertThat(html).contains("2");
                    assertThat(html).contains("50");
                });
        verify(orderService).getAllOrders();
    }

    @Test
    void get_Order_Page_With_Default_Value() {
        Long orderId = 1L;
        OrderItemDto oiDto1 = new OrderItemDto(1L, "яблоко", 50, 1);
        OrderItemDto oiDto2 = new OrderItemDto(2L, "мяч", 300, 1);
        OrderResponseDto mockDto = new OrderResponseDto(1L, List.of(oiDto1, oiDto2), 350);
        when(orderService.getOrderResponseById(orderId)).thenReturn(Mono.just(mockDto));
        webTestClient.get()
                .uri("/orders/{id}", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertThat(html).contains("1"));
    }
}
