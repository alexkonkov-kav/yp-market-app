package com.market.controller;

import com.market.service.OrderBuyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderBuyController.class)
@ActiveProfiles("test")
public class OrderBuyControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderBuyService orderBuyService;

    @Test
    void buy_Order() {
        Long orderId = 1L;
        when(orderBuyService.createOrder()).thenReturn(Mono.just(orderId));
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals("Location", "/orders/" + orderId + "?newOrder=true");
        verify(orderBuyService).createOrder();
    }
}
