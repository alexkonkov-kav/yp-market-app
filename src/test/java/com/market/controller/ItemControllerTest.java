package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.dto.ItemWithPagingResponseDto;
import com.market.dto.PagingResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.service.CartItemService;
import com.market.service.DisplayItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ItemController.class)
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartItemService cartItemService;

    @MockitoBean
    private DisplayItemService displayItemService;

    @Test
    void get_Item_Page() {
        Long itemId = 1L;
        ItemResponseDto mockDto = new ItemResponseDto(itemId, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        when(cartItemService.getItemPage(itemId)).thenReturn(Mono.just(mockDto));
        webTestClient.get()
                .uri("/items/{id}", itemId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("яблоко");
                    assertThat(html).contains("яблоко красное");
                    assertThat(html).contains("50");
                });
        verify(cartItemService).getItemPage(itemId);
    }

    @Test
    void get_Item_Page_Not_Found() {
        when(cartItemService.getItems(any(), any(), anyInt(), anyInt())).thenReturn(Flux.empty());
        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void get_Items_Page() {
        Long itemId = 1L;
        SortType sort = SortType.ALPHA;
        int page = 0;
        int size = 5;

        PagingResponseDto paging = new PagingResponseDto(page, size, false, false);
        ItemResponseDto itemDto = new ItemResponseDto(itemId, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        ItemWithPagingResponseDto responseDto = new ItemWithPagingResponseDto(itemDto, paging);
        when(cartItemService.getItems("яблоко", sort, page, size)).thenReturn(Flux.just(responseDto));
        when(displayItemService.displayItem(anyList())).thenReturn(List.of(List.of(itemDto)));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("search", "яблоко")
                        .queryParam("sort", sort)
                        .queryParam("pageNumber", page)
                        .queryParam("pageSize", size)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertThat(html).contains("яблоко"));
        verify(cartItemService).getItems("яблоко", sort, page, size);
    }

    @Test
    void update_Item_Count() {
        Long itemId = 1L;
        CartAction action = CartAction.PLUS;
        when(cartItemService.updateItemCount(itemId, action)).thenReturn(Mono.empty());
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items/{id}")
                        .queryParam("action", action)
                        .build(itemId))
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals("Location", "/items/" + itemId);
        verify(cartItemService).updateItemCount(itemId, action);
    }

    @Test
    void update_Item_Count_From_Cart() {
        Long id = 1L;
        String search = "яблоко";
        SortType sort = SortType.ALPHA;
        int pageNumber = 0;
        int pageSize = 5;
        CartAction action = CartAction.PLUS;
        when(cartItemService.updateItemCount(id, action)).thenReturn(Mono.empty());
        String expectedLocation = UriComponentsBuilder.fromPath("/items")
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .encode()
                .toUriString();
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id", id)
                        .queryParam("search", search)
                        .queryParam("sort", sort)
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals("Location", expectedLocation);
        verify(cartItemService).updateItemCount(id, action);
    }
}
