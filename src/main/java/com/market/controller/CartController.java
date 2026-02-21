package com.market.controller;

import com.market.enumeration.CartAction;
import com.market.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartItemService cartItemService;

    public CartController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/items")
    public Mono<Rendering> getCartPage() {
        return cartItemService.getAllCartItems()
                .collectList()
                .map(e -> {
                    long total = e.stream()
                            .mapToLong(dto -> dto.price() * dto.count())
                            .sum();
                    return Rendering.view("cart")
                            .modelAttribute("items", e)
                            .modelAttribute("total", total)
                            .build();
                });
    }

    @PostMapping("/items")
    public Mono<Rendering> updateCartItemCount(@RequestParam("id") Long id, @RequestParam("action") CartAction action) {
        return cartItemService.updateItemCount(id, action)
                .then(Mono.just(Rendering.redirectTo("/items").build()));
    }
}
