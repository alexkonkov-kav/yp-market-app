package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.dto.PagingResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.service.CartItemService;
import com.market.service.DisplayItemService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String getItemPage(@PathVariable Long id, Model model) {
        ItemResponseDto dto = cartItemService.getItemPage(id);
        model.addAttribute("item", dto);
        return "item";
    }

    @GetMapping({"/", "/items"})
    public String getItemsPage(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "sort", defaultValue = "NO") SortType sort,
                               @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               Model model) {
        Page<ItemResponseDto> page = cartItemService.getItems(search, sort, pageNumber, pageSize);
        model.addAttribute("items", displayItemService.displayItem(page.getContent()));
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", new PagingResponseDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext()));
        return "items";
    }

    @PostMapping("/items/{id}")
    public String updateItemCount(@PathVariable Long id, @RequestParam CartAction action, Model model) {
        cartItemService.updateItemCount(id, action);
        return getItemPage(id, model);
    }

    @PostMapping("/items")
    public String updateItemCountFromCart(@RequestParam Long id,
                                          @RequestParam(value = "search", required = false) String search,
                                          @RequestParam(value = "sort", defaultValue = "NO") SortType sort,
                                          @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                          @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                          @RequestParam("action") CartAction action) {
        cartItemService.updateItemCount(id, action);
        return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%s&pageSize=%s",
                search != null ? search : "", sort, pageNumber, pageSize);
    }
}
