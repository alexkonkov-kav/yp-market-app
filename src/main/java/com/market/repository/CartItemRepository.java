package com.market.repository;

import com.market.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByItemId(Long itemId);

    @Query("select ci from CartItem ci join fetch ci.item")
    List<CartItem> findAllWithItems();
}
