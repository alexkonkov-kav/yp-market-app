package com.market.controller;

import com.market.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Mono<Rendering> getOrdersPage() {
        return Mono.just(
                Rendering.view("orders")
                        .modelAttribute("orders", orderService.getAllOrders())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrderPage(@PathVariable Long id,
                                        @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder) {
        return Mono.just(
                Rendering.view("order")
                        .modelAttribute("order", orderService.getOrderResponseById(id))
                        .modelAttribute("newOrder", newOrder)
                        .build()
        );
    }
}
