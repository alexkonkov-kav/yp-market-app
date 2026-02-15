package com.market.service;

import com.market.dto.ItemResponseDto;
import com.market.dto.ItemWithPagingResponseDto;
import com.market.enumeration.CartAction;
import com.market.enumeration.SortType;
import com.market.mapper.ItemMapper;
import com.market.mapper.ItemWithPagingMapper;
import com.market.model.CartItem;
import com.market.model.Item;
import com.market.repository.CartItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final ItemWithPagingMapper itemWithPagingMapper;

    public CartItemService(CartItemRepository cartItemRepository,
                           ItemService itemService,
                           ItemMapper itemMapper,
                           ItemWithPagingMapper itemWithPagingMapper) {
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.itemWithPagingMapper = itemWithPagingMapper;
    }

    @Transactional
    public Mono<CartItem> save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public Mono<CartItem> getCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not found CartItem with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public Mono<CartItem> getCartItemByItemId(Long itemId) {
        return cartItemRepository.findByItemId(itemId);
    }

    @Transactional(readOnly = true)
    public Mono<ItemResponseDto> getItemPage(Long itemId) {
        return Mono.zip(
                        itemService.getItemById(itemId),
                        getCartItemById(itemId))
                .map(e -> itemMapper.mapToItemResponseDto(e.getT1(), e.getT2().getCount()));
    }

    @Transactional(readOnly = true)
    public Flux<ItemResponseDto> getAllCartItems() {
        return cartItemRepository.findAllWithItems()
                .map(e -> itemMapper.mapToItemResponseDto(e.getItem(), e.getCount()));
    }

    @Transactional(readOnly = true)
    public Flux<ItemWithPagingResponseDto> getItems(String search, SortType sortType, int pageNumber, int pageSize) {
        Pageable pageable = getPageable(sortType, pageNumber, pageSize);
        Flux<Item> items;
        Mono<Long> count;
        if (search != null && !search.isBlank()) {
            items = itemService.findByTitleOrDescription(search, search, pageable);
            count = itemService.countByTitleOrDescription(search, search);
        } else {
            items = itemService.findAll(pageable);
            count = itemService.countAll();
        }
        Mono<Map<Long, Integer>> comparisonMap = cartItemRepository.findAllWithItems()
                .collect(Collectors.toMap(e -> e.getItem().getId(), CartItem::getCount));

        return Mono.zip(count, comparisonMap)
                .flatMapMany(e -> items
                        .map(item -> itemWithPagingMapper.mapToItemWithPagingResponseDto(item,
                                e.getT2().getOrDefault(item.getId(), 0),
                                pageNumber,
                                pageSize,
                                e.getT1())
                        )
                );
    }

    @Transactional
    public Mono<Void> updateItemCount(Long itemId, CartAction action) {
        Mono<CartItem> optCartItem = getCartItemByItemId(itemId);
        return switch (action) {
            case PLUS -> handlePlusAction(itemId, optCartItem);
            case MINUS -> handleMinusAction(optCartItem);
            case DELETE -> handleDeleteAction(optCartItem);
        };
    }

    private Mono<Void> handlePlusAction(Long itemId, Mono<CartItem> optCartItem) {
        return optCartItem.flatMap(e -> {
                    e.setCount(e.getCount() + 1);
                    return save(e);
                })
                .switchIfEmpty(
                        itemService.getItemById(itemId).flatMap(e -> {
                            CartItem cartItem = new CartItem();
                            cartItem.setItem(e);
                            cartItem.setCount(1);
                            return save(cartItem);
                        }))
                .then();
    }

    private Mono<Void> handleMinusAction(Mono<CartItem> optCartItem) {
        return optCartItem
                .flatMap(e -> {
                    if (e.getCount() > 1) {
                        e.setCount(e.getCount() - 1);
                    } else {
                        e.setCount(0);
                    }
                    return save(e);
                })
                .then();
    }

    private Mono<Void> handleDeleteAction(Mono<CartItem> optCartItem) {
        return optCartItem
                .flatMap(e -> {
                    e.setCount(0);
                    return save(e);
                })
                .then();
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
