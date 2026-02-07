package com.market.service;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.mapper.ItemMapper;
import com.market.model.CartItem;
import com.market.model.Item;
import com.market.repository.CartItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {

    @InjectMocks
    private CartItemService cartItemService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemMapper mapper;

    @Test
    void get_Cart_Item_By_Id() {
        Long id = 1L;
        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(id);
        mockCartItem.setCount(10);
        when(cartItemRepository.findById(id)).thenReturn(Optional.of(mockCartItem));
        CartItem result = cartItemService.getCartItemById(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCount()).isEqualTo(10);
        verify(cartItemRepository).findById(id);
    }

    @Test
    void get_Cart_Item_By_Id_Not_Found() {
        Long id = 99L;
        when(cartItemRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cartItemService.getCartItemById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found CartItem with ID: " + id);
        verify(cartItemRepository).findById(id);
    }

    @Test
    void get_Item_Page() {
        Long itemId = 1L;
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        CartItem mockCartItem = new CartItem(item, 1);
        ItemResponseDto dto = new ItemResponseDto(itemId, "яблоко", "яблоко красное", "images/apple.jpg", 50, mockCartItem.getCount());
        when(itemService.getItemById(itemId)).thenReturn(item);
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(mockCartItem));
        when(mapper.mapToItemResponseDto(item, mockCartItem.getCount())).thenReturn(dto);
        ItemResponseDto result = cartItemService.getItemPage(itemId);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(itemId);
        assertThat(result.count()).isEqualTo(1);
        verify(itemService).getItemById(itemId);
        verify(cartItemRepository).findById(itemId);
        verify(mapper).mapToItemResponseDto(any(Item.class), eq(mockCartItem.getCount()));
    }

    @Test
    void get_All_Cart_Items() {
        Item item1 = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item1.setId(1L);
        Item item2 = new Item("мяч", "футбольный мяч", "images/ball.jpg", 300);
        item2.setId(2L);
        CartItem mockCartItem1 = new CartItem(item1, 1);
        CartItem mockCartItem2 = new CartItem(item2, 1);
        ItemResponseDto dto1 = new ItemResponseDto(item1.getId(), "яблоко", "яблоко красное", "images/apple.jpg", 50, mockCartItem1.getCount());
        ItemResponseDto dto2 = new ItemResponseDto(item2.getId(), "мяч", "футбольный мяч", "images/ball.jpg", 300, mockCartItem2.getCount());
        when(cartItemRepository.findAllWithItems()).thenReturn(List.of(mockCartItem1, mockCartItem2));
        when(mapper.mapToItemResponseDto(item1, mockCartItem1.getCount())).thenReturn(dto1);
        when(mapper.mapToItemResponseDto(item2, mockCartItem2.getCount())).thenReturn(dto2);
        List<ItemResponseDto> result = cartItemService.getAllCartItems();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(item1.getId());
        assertThat(result.get(0).count()).isEqualTo(mockCartItem1.getCount());
        assertThat(result.get(1).id()).isEqualTo(item2.getId());
        assertThat(result.get(1).count()).isEqualTo(mockCartItem2.getCount());
        verify(cartItemRepository).findAllWithItems();
        verify(mapper, times(2)).mapToItemResponseDto(any(Item.class), anyInt());
    }

    @Test
    void get_Items() {
        String search = "яблоко";
        Item item1 = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item1.setId(1L);
        Item item2 = new Item("мяч", "футбольный мяч", "images/ball.jpg", 300);
        item2.setId(2L);
        Page<Item> itemPage = new PageImpl<>(List.of(item1, item2), PageRequest.of(0, 5), 2);
        CartItem cartItem = new CartItem(item1, 1);
        when(itemService.findByTitleOrDescription(eq(search), eq(search), any())).thenReturn(itemPage);
        when(cartItemRepository.findAllWithItems()).thenReturn(List.of(cartItem));
        when(mapper.mapToItemResponseDto(item1, 1)).thenReturn(new ItemResponseDto(1L, "яблоко", "яблоко красное", "images/apple.jpg", 50, 1));
        when(mapper.mapToItemResponseDto(item2, 0)).thenReturn(new ItemResponseDto(2L, "мяч", "футбольный мяч", "images/ball.jpg", 300, 0));
        Page<ItemResponseDto> result = cartItemService.getItems(search, SortType.ALPHA, 0, 5);
        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).count()).isEqualTo(1);
        assertThat(result.getContent().get(1).count()).isEqualTo(0);
        verify(itemService).findByTitleOrDescription(anyString(), anyString(), any());
        verify(cartItemRepository).findAllWithItems();
        verify(mapper, times(2)).mapToItemResponseDto(any(), anyInt());
    }

    @Test
    void handle_Plus_Action() {
        Long itemId = 1L;
        CartItem existingItem = new CartItem();
        existingItem.setCount(2);
        getCartItem(itemId, existingItem);
        cartItemService.updateItemCount(itemId, CartAction.PLUS);
        assertThat(existingItem.getCount()).isEqualTo(3);
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void handle_Minus_Action() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setCount(1);
        getCartItem(itemId, cartItem);
        cartItemService.updateItemCount(itemId, CartAction.MINUS);
        assertThat(cartItem.getCount()).isZero();
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void handle_Delete_Action() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setCount(10);
        getCartItem(itemId, cartItem);
        cartItemService.updateItemCount(itemId, CartAction.DELETE);
        assertThat(cartItem.getCount()).isZero();
        verify(cartItemRepository).save(cartItem);
    }

    private void getCartItem(Long id, CartItem item) {
        when(cartItemRepository.findByItemId(id)).thenReturn(Optional.ofNullable(item));
    }
}
