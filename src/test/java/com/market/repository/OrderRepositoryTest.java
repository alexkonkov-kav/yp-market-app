package com.market.repository;

import com.market.model.Order;
import com.market.model.OrderItem;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void find_ById_With_Items() {
        Order order = new Order();
        order.setTotalSum(1000L);
        Order saveOrder = orderRepository.save(order);
        OrderItem orderItem = new OrderItem(1L, "яблоко", 1000, 1, saveOrder);
        orderItemRepository.save(orderItem);
        orderRepository.flush();
        entityManager.clear();

        Optional<Order> found = orderRepository.findByIdlWithItems(saveOrder.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getItems()).hasSize(1);
        assertThat(found.get().getItems())
                .extracting(OrderItem::getTitle)
                .containsExactlyInAnyOrder("яблоко");
    }

    @Test
    public void find_All_With_Items() {
        Order order1 = new Order();
        order1.setTotalSum(1000L);
        Order order2 = new Order();
        order2.setTotalSum(300L);
        List<Order> saveOrders = orderRepository.saveAll(List.of(order1, order2));
        OrderItem orderItem1 = new OrderItem(1L, "яблоко", 50, 1, saveOrders.getFirst());
        OrderItem orderItem2 = new OrderItem(1L, "мяч", 300, 1, saveOrders.get(1));
        orderItemRepository.saveAll(List.of(orderItem1, orderItem2));
        orderRepository.flush();
        entityManager.clear();

        List<Order> found = orderRepository.findAllWithItems();
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getItems()).isNotEmpty();
        assertThat(Hibernate.isInitialized(found.get(0).getItems())).isTrue();
    }

    @Test
    public void found_By_Item_Id_Not_Found() {
        Optional<Order> found = orderRepository.findByIdlWithItems(999L);
        assertThat(found).isEmpty();
    }
}
