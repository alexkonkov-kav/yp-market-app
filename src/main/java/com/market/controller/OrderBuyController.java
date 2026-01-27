package com.market.controller;

import com.market.service.OrderBuyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buy")
public class OrderBuyController {

    private final OrderBuyService orderBuyService;

    public OrderBuyController(OrderBuyService orderBuyService) {
        this.orderBuyService = orderBuyService;
    }

    @PostMapping
    public String buyOrder() {
        Long orderId = orderBuyService.createOrder();
        return "redirect:/orders/" + orderId + "?newOrder=true";
    }
}
