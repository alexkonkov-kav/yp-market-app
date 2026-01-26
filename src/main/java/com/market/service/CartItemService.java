package com.market.service;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.mapper.ItemMapper;
import com.market.model.CartItem;
import com.market.model.Item;
import com.market.repository.CartItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;
    private final ItemMapper mapper;

    public CartItemService(CartItemRepository cartItemRepository,
                           ItemService itemService,
                           ItemMapper mapper) {
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
        this.mapper = mapper;
    }

    @Transactional
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartItem getCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found CartItem with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> getCartItemByItemId(Long itemId) {
        return cartItemRepository.findByItemId(itemId);
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getItemPage(Long itemId) {
        Item item = itemService.getItemById(itemId);
        CartItem cartItem = getCartItemById(itemId);
        return mapper.mapToItemResponseDto(item, cartItem.getCount());
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllCartItems() {
        return cartItemRepository.findAllWithItems().stream()
                .map(e -> mapper.mapToItemResponseDto(e.getItem(), e.getCount()))
                .toList();
    }

    @Transactional
    public void updateItemCount(Long itemId, CartAction action) {
        Optional<CartItem> optCartItem = getCartItemByItemId(itemId);
        switch (action) {
            case PLUS -> handlePlusAction(itemId, optCartItem);
            case MINUS -> handleMinusAction(optCartItem);
            case DELETE -> handleDeleteAction(optCartItem);
        }
    }

    private void handlePlusAction(Long itemId, Optional<CartItem> optCartItem) {
        CartItem cartItem;
        if (optCartItem.isPresent()) {
            cartItem = optCartItem.get();
            cartItem.setCount(cartItem.getCount() + 1);
        } else {
            Item item = itemService.getItemById(itemId);
            cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setCount(1);
        }
        save(cartItem);
    }

    private void handleMinusAction(Optional<CartItem> optCartItem) {
        if (optCartItem.isPresent()) {
            CartItem cartItem = optCartItem.get();
            if (cartItem.getCount() > 1) {
                cartItem.setCount(cartItem.getCount() - 1);
            } else {
                cartItem.setCount(0);
            }
            save(cartItem);
        }
    }

    private void handleDeleteAction(Optional<CartItem> optCartItem) {
        if (optCartItem.isPresent()) {
            CartItem cartItem = optCartItem.get();
            cartItem.setCount(0);
            save(cartItem);
        }
    }
}
