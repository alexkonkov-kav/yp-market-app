package com.market.controller;

import com.market.dto.ItemWithPagingResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.service.CartItemService;
import com.market.service.DisplayItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class ItemController {

    private final CartItemService cartItemService;
    private final DisplayItemService displayItemService;

    public ItemController(CartItemService cartItemService,
                          DisplayItemService displayItemService) {
        this.cartItemService = cartItemService;
        this.displayItemService = displayItemService;
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> getItemPage(@PathVariable Long id) {
        return Mono.just(
                Rendering.view("item")
                        .modelAttribute("users", cartItemService.getItemPage(id))
                        .build());
    }

    @GetMapping({"/", "/items"})
    public Mono<Rendering> getItemsPage(@RequestParam(value = "search", required = false) String search,
                                        @RequestParam(value = "sort", defaultValue = "NO") SortType sort,
                                        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {

        return cartItemService.getItems(search, sort, pageNumber, pageSize)
                .collectList()
                .map(e -> Rendering.view("items")
                        .modelAttribute("items", displayItemService.displayItem(e.stream().map(ItemWithPagingResponseDto::itemResponseDto).toList()))
                        .modelAttribute("search", search)
                        .modelAttribute("sort", sort)
                        .modelAttribute("paging", e.getFirst().pagingResponseDto())
                        .build()
                );
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> updateItemCount(@PathVariable Long id, @RequestParam CartAction action) {
        return cartItemService.updateItemCount(id, action)
                .then(Mono.just(Rendering.redirectTo("/items/" + id).build()));
    }

    @PostMapping("/items")
    public Mono<Rendering> updateItemCountFromCart(@RequestParam Long id,
                                                   @RequestParam(value = "search", required = false) String search,
                                                   @RequestParam(value = "sort", defaultValue = "NO") SortType sort,
                                                   @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                                   @RequestParam("action") CartAction action) {
        return cartItemService.updateItemCount(id, action)
                .then(Mono.just(Rendering
                        .redirectTo(
                                String.format("redirect:/items?search=%s&sort=%s&pageNumber=%s&pageSize=%s",
                                        search != null ? search : "", sort, pageNumber, pageSize))
                        .build()));
    }
}
