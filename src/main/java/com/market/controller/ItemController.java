package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final CartItemService cartItemService;

    public ItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/{id}")
    public String getItemPage(@PathVariable Long id, Model model) {
        ItemResponseDto dto = cartItemService.getItemPage(id);
        model.addAttribute("item", dto);
        return "item";
    }

    @PostMapping("/{id}")
    public String updateItemCount(@PathVariable Long id, @RequestParam CartAction action, Model model) {
        cartItemService.updateItemCount(id, action);
        return getItemPage(id, model);
    }
}
