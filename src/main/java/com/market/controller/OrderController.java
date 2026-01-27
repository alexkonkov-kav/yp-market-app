package com.market.controller;

import com.market.dto.OrderResponseDto;
import com.market.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getOrderPage(Model model) {
        List<OrderResponseDto> dtos = orderService.getAllOrders();
        model.addAttribute("orders", dtos);
        return "orders";
    }
}
