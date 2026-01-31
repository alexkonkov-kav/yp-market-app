package com.market.controller;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.service.CartItemService;
import com.market.service.DisplayItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void get_Items_Page() throws Exception {
        Long itemId = 1L;
        ItemResponseDto itemDto = new ItemResponseDto(itemId, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        List<ItemResponseDto> itemsList = List.of(itemDto);
        Page<ItemResponseDto> mockPage = new PageImpl<>(itemsList, PageRequest.of(0, 5), 1);

        when(cartItemService.getItems(eq("яблоко"), any(), anyInt(), anyInt())).thenReturn(mockPage);
        when(displayItemService.displayItem(anyList())).thenReturn(List.of(itemsList));
        mockMvc.perform(get("/items")
                        .param("search", "яблоко")
                        .param("sort", "ALPHA")
                        .param("pageNumber", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("items", List.of(itemsList)))
                .andExpect(model().attribute("search", "яблоко"))
                .andExpect(model().attribute("sort", SortType.ALPHA))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void get_Items_PageDefault_Parameters() throws Exception {
        when(cartItemService.getItems(isNull(), any(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attribute("search", org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void update_Item_Count() throws Exception {
        Long itemId = 1L;
        ItemResponseDto mockDto = new ItemResponseDto(itemId, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1);
        when(cartItemService.getItemPage(itemId)).thenReturn(mockDto);

        mockMvc.perform(post("/items/{id}", itemId)
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attribute("item", mockDto));
        verify(cartItemService).updateItemCount(eq(itemId), eq(CartAction.PLUS));
    }

    @Test
    void update_Item_Count_From_Cart() throws Exception {
        Long id = 1L;
        String search = "яблоко";
        SortType sort = SortType.ALPHA;
        int pageNumber = 0;
        int pageSize = 5;
        CartAction action = CartAction.PLUS;

        mockMvc.perform(post("/items")
                        .param("id", id.toString())
                        .param("search", search)
                        .param("sort", sort.name())
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("action", action.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/items?search=%s&sort=%s&pageNumber=%s&pageSize=%s",
                        search, sort, pageNumber, pageSize)));
        verify(cartItemService).updateItemCount(eq(id), eq(action));
    }
}
