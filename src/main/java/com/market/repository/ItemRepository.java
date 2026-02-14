package com.market.repository;

import com.market.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

    Flux<Item> findAll(Pageable pageable);

    Flux<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
}
