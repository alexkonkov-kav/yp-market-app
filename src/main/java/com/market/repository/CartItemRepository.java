package com.market.repository;

import com.market.model.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {

    Mono<CartItem> findByItemId(Long itemId);

    @Query("select ci from CartItem ci join fetch ci.item")
    Flux<CartItem> findAllWithItems();
}
