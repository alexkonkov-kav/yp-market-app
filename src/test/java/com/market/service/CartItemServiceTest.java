package com.market.service;

import com.market.dto.ItemResponseDto;
import com.market.dto.ItemWithPagingResponseDto;
import com.market.dto.PagingResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.mapper.ItemMapper;
import com.market.mapper.ItemWithPagingMapper;
import com.market.model.CartItem;
import com.market.model.Item;
import com.market.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    private ItemWithPagingMapper itemWithPagingMapper;

    @Test
    void get_Cart_Item_By_Item_Id() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setItemId(itemId);
        cartItem.setCount(10);
        when(cartItemRepository.findByItemId(itemId)).thenReturn(Mono.just(cartItem));
        Mono<CartItem> resultCartItem = cartItemService.getCartItemByItemId(itemId);
        StepVerifier.create(resultCartItem)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getItemId()).isEqualTo(itemId);
                    assertThat(result.getCount()).isEqualTo(10);
                })
                .verifyComplete();
        verify(cartItemRepository).findByItemId(itemId);
    }

    @Test
    void get_Cart_Item_By_Item_Id_NotFound() {
        Long itemId = 2L;
        when(cartItemRepository.findByItemId(itemId)).thenReturn(Mono.empty());
        Mono<CartItem> resultMono = cartItemService.getCartItemByItemId(itemId);
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException)
                .verify();
        verify(cartItemRepository, times(1)).findByItemId(itemId);
    }

    @Test
    void get_All_Cart_Items_With_Item() {
        Long itemId = 1L;
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item.setId(itemId);
        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setItemId(itemId);
        cartItem.setCount(10);
        when(cartItemRepository.findAll()).thenReturn(Flux.just(cartItem));
        when(itemService.getItemById(itemId)).thenReturn(Mono.just(item));
        Flux<CartItem> resultFlux = cartItemService.getAlLCartItemWithItem();
        StepVerifier.create(resultFlux)
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo(100L);
                    assertThat(result.getItem()).isNotNull();
                    assertThat(result.getItem().getTitle()).isEqualTo("яблоко");
                    assertThat(result.getItemId()).isEqualTo(result.getItem().getId());
                })
                .verifyComplete();
        verify(cartItemRepository).findAll();
        verify(itemService).getItemById(itemId);
    }

    @Test
    void get_All_Cart_Items() {
        Long itemId = 1L;
        int count = 5;
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item.setId(itemId);
        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setItemId(itemId);
        cartItem.setCount(count);
        ItemResponseDto dto = new ItemResponseDto(item.getId(), item.getTitle(), item.getDescription(), item.getImgPath(), item.getPrice(), count);
        when(cartItemRepository.findAll()).thenReturn(Flux.just(cartItem));
        when(itemService.getItemById(itemId)).thenReturn(Mono.just(item));
        when(mapper.mapToItemResponseDto(item, count)).thenReturn(dto);
        Flux<ItemResponseDto> resultFlux = cartItemService.getAllCartItems();
        StepVerifier.create(resultFlux)
                .assertNext(e -> {
                    assertThat(e).isNotNull();
                    assertThat(e.title()).isEqualTo("яблоко");
                    assertThat(e.count()).isEqualTo(count);
                    assertThat(e.id()).isEqualTo(itemId);
                })
                .verifyComplete();
        verify(cartItemRepository).findAll();
        verify(itemService).getItemById(itemId);
        verify(mapper).mapToItemResponseDto(item, count);
    }

    @Test
    void getItems_WithSearch_ReturnsMappedDtos() {
        String search = "яблоко";
        int count = 5;
        int page = 0;
        int size = 10;
        long totalCount = 1L;
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item.setId(1L);
        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setItemId(item.getId());
        cartItem.setCount(count);
        ItemResponseDto itemResponseDto = new ItemResponseDto(item.getId(), item.getTitle(), item.getDescription(), item.getImgPath(), item.getPrice(), count);
        PagingResponseDto pagingResponseDto = new PagingResponseDto(size, page, false, false);
        ItemWithPagingResponseDto dto = new ItemWithPagingResponseDto(itemResponseDto, pagingResponseDto);
        when(cartItemRepository.findAll()).thenReturn(Flux.just(cartItem));
        when(itemService.getItemById(item.getId())).thenReturn(Mono.just(item));
        when(itemService.findByTitleOrDescription(eq(search), eq(search), any(Pageable.class))).thenReturn(Flux.just(item));
        when(itemService.countByTitleOrDescription(search, search)).thenReturn(Mono.just(totalCount));
        when(itemWithPagingMapper.mapToItemWithPagingResponseDto(eq(item), eq(count), eq(page), eq(size), eq(totalCount))).thenReturn(dto);
        Flux<ItemWithPagingResponseDto> result = cartItemService.getItems(search, SortType.ALPHA, page, size);
        StepVerifier.create(result)
                .expectNext(dto)
                .verifyComplete();
        verify(cartItemRepository).findAll();
    }

    @Test
    void handle_Plus_Action() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setCount(2);
        when(cartItemRepository.findByItemId(itemId)).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        Mono<Void> result = cartItemService.updateItemCount(itemId, CartAction.PLUS);
        StepVerifier.create(result).expectComplete().verify();
        assertThat(cartItem.getCount()).isEqualTo(3);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void handle_Minus_Action() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setCount(2);
        when(cartItemRepository.findByItemId(itemId)).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mono<Void> result = cartItemService.updateItemCount(itemId, CartAction.MINUS);
        StepVerifier.create(result).expectComplete().verify();
        assertThat(cartItem.getCount()).isEqualTo(1);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void handle_Delete_Action() {
        Long itemId = 100L;
        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setCount(10);
        when(cartItemRepository.findByItemId(itemId)).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mono<Void> result = cartItemService.updateItemCount(itemId, CartAction.DELETE);
        StepVerifier.create(result).expectComplete().verify();
        assertThat(cartItem.getCount()).isZero();
        verify(cartItemRepository).save(cartItem);
    }
}
