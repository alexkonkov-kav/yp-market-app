package com.market.controller;

import com.market.service.OrderBuyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderBuyController.class)
@ActiveProfiles("test")
public class OrderBuyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderBuyService orderBuyService;

    @Test
    void buy_Order() throws Exception {
        Long orderId = 1L;
        when(orderBuyService.createOrder()).thenReturn(orderId);
        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/" + orderId + "?newOrder=true"));
        verify(orderBuyService).createOrder();
    }
}
