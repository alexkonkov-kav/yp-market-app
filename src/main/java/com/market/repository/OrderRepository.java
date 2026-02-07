package com.market.repository;

import com.market.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o join fetch o.items where o.id = :id")
    Optional<Order> findByIdlWithItems(@Param("id") Long id);

    @Query("select distinct o from Order o join fetch o.items")
    List<Order> findAllWithItems();
}
