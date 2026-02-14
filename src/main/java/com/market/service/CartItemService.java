package com.market.service;

import com.market.dto.ItemResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.mapper.ItemMapper;
import com.market.model.CartItem;
import com.market.model.Item;
import com.market.repository.CartItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Mono<CartItem> getCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not found CartItem with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> getCartItemByItemId(Long itemId) {
        return cartItemRepository.findByItemId(itemId);
    }

    @Transactional(readOnly = true)
    public Mono<ItemResponseDto> getItemPage(Long itemId) {
        return Mono.zip(
                itemService.getItemById(itemId),
                getCartItemById(itemId))
                .map(e -> mapper.mapToItemResponseDto(e.getT1(), e.getT2().getCount()));
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllCartItems() {
        return cartItemRepository.findAllWithItems().stream()
                .map(e -> mapper.mapToItemResponseDto(e.getItem(), e.getCount()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ItemResponseDto> getItems(String search, SortType sortType, int pageNumber, int pageSize) {
        Pageable pageable = getPageable(sortType, pageNumber, pageSize);
        Page<Item> page;
        if (search != null && !search.isBlank()) {
            page = itemService.findByTitleOrDescription(search, search, pageable);
        } else {
            page = itemService.findAll(pageable);
        }
        Map<Long, Integer> comparisonMap = cartItemRepository.findAllWithItems().stream()
                .collect(Collectors.toMap(e -> e.getItem().getId(), CartItem::getCount));

        return page.map(e -> mapper.mapToItemResponseDto(e, comparisonMap.getOrDefault(e.getId(), 0)));
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

    private Pageable getPageable(SortType sortType, int pageNumber, int pageSize) {
        Sort sort = switch (sortType) {
            case ALPHA -> Sort.by(Sort.Order.asc("title"));
            case PRICE -> Sort.by(Sort.Order.asc("price"));
            default -> Sort.unsorted();
        };
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
