package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.service.CartItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(CartController.class)
@ActiveProfiles("test")
public class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartItemService cartItemService;

    @Test
    void get_Cart_Page() {
        ItemResponseDto itemDto1 = new ItemResponseDto(1L, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        ItemResponseDto itemDto2 = new ItemResponseDto(2L, "мяч", "мяч футбольный", "images/ball.jpg", 300, 1);
        when(cartItemService.getAllCartItems()).thenReturn(Flux.just(itemDto1, itemDto2));
        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("яблоко");
                    assertThat(html).contains("мяч");
                });
    }

    @Test
    void update_Cart_Item_Count() {
        Long itemId = 1L;
        CartAction action = CartAction.PLUS;
        when(cartItemService.updateItemCount(itemId, action)).thenReturn(Mono.empty());
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", itemId)
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals("Location", "/items");
        verify(cartItemService).updateItemCount(itemId, action);
    }
}
