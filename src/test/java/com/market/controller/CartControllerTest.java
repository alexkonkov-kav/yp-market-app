package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.service.CartItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@ActiveProfiles("test")
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartItemService cartItemService;

    @Test
    void get_Cart_Page() throws Exception {
        ItemResponseDto itemDto1 = new ItemResponseDto(1L, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        ItemResponseDto itemDto2 = new ItemResponseDto(2L, "мяч", "мяч футбольный", "images/ball.jpg", 300, 1);
        List<ItemResponseDto> mockItems = List.of(itemDto1, itemDto2);
        long total = mockItems.stream()
                .mapToLong(e -> e.price() * e.count())
                .sum();
        when(cartItemService.getAllCartItems()).thenReturn(mockItems);
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("items", mockItems))
                .andExpect(model().attribute("total", total));
    }

    @Test
    void update_Cart_Item_Count() throws Exception {
        CartAction action = CartAction.PLUS;
        ItemResponseDto itemDto = new ItemResponseDto(1L, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        List<ItemResponseDto> items = List.of(itemDto);
        long total = itemDto.price() * itemDto.count();
        when(cartItemService.getAllCartItems()).thenReturn(items);
        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", action.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("items", items))
                .andExpect(model().attribute("total", total));
        verify(cartItemService).updateItemCount(eq(itemDto.id()), eq(action));
    }
}
