package com.market.service;

import com.market.model.Item;
import com.market.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Not found Item with ID: " + itemId));
    }

    @Transactional(readOnly = true)
    public Page<Item> findByTitleOrDescription(String title, String description, Pageable pageable) {
        return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title, description, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }
}
