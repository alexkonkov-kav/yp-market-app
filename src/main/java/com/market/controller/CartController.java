package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartItemService cartItemService;

    public CartController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/items")
    public String getCartPage(Model model) {
        List<ItemResponseDto> dtos = cartItemService.getAllCartItems();
        long total = dtos.stream()
                .mapToLong(e -> e.price() * e.count())
                .sum();
        model.addAttribute("items", dtos);
        model.addAttribute("total", total);

        return "cart";
    }
}
