package com.market.controller;

import com.market.dto.OrderResponseDto;
import com.market.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getOrdersPage(Model model) {
        List<OrderResponseDto> dtos = orderService.getAllOrders();
        model.addAttribute("orders", dtos);
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrderPage(@PathVariable Long id,
                               @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
                               Model model) {
        OrderResponseDto dto = orderService.getOrderResponseById(id);
        model.addAttribute("order", dto);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }
}
