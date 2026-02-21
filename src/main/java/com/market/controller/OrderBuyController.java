package com.market.controller;

import com.market.service.OrderBuyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/buy")
public class OrderBuyController {

    private final OrderBuyService orderBuyService;

    public OrderBuyController(OrderBuyService orderBuyService) {
        this.orderBuyService = orderBuyService;
    }

    @PostMapping
    public Mono<Rendering> buyOrder() {
        return orderBuyService.createOrder()
                .flatMap(e -> Mono.just(Rendering.redirectTo("/orders/" + e + "?newOrder=true").build()));
    }
}
