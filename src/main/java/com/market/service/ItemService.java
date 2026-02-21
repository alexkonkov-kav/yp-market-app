package com.market.service;

import com.market.model.Item;
import com.market.repository.ItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public Mono<Item> getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not found Item with ID: " + itemId)));
    }

    @Transactional(readOnly = true)
    public Flux<Item> findByTitleOrDescription(String title, String description, Pageable pageable) {
        return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title, description, pageable);
    }

    @Transactional(readOnly = true)
    public Flux<Item> findAll(Pageable pageable) {
        return itemRepository.findAllBy(pageable);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countByTitleOrDescription(String title, String description) {
        return itemRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title, description);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return itemRepository.count();
    }
}
