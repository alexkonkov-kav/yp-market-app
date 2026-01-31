package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.service.CartItemService;
import com.market.service.DisplayItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartItemService cartItemService;

    @MockitoBean
    private DisplayItemService displayItemService;

    @Test
    void get_Item_Page() throws Exception {
        Long itemId = 1L;
        ItemResponseDto mockDto = new ItemResponseDto(itemId, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        when(cartItemService.getItemPage(itemId)).thenReturn(mockDto);

        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("item", mockDto));
    }

    @Test
    void get_Item_Page_Not_Found() throws Exception {
        Long itemId = 999L;
        when(cartItemService.getItemPage(itemId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isNotFound());

    }
}
