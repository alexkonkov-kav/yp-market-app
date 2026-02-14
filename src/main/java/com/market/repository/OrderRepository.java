package com.market.repository;

import com.market.model.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("select distinct o from Order o join fetch o.items where o.id = :id")
    Mono<Order> findByIdlWithItems(@Param("id") Long id);

    @Query("select distinct o from Order o join fetch o.items")
    Flux<Order> findAllWithItems();
}
