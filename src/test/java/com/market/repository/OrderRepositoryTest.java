package com.market.repository;

import com.market.model.Item;
import com.market.model.Order;
import com.market.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void cleanUp() {
        orderItemRepository.deleteAll().block();
        orderRepository.deleteAll().block();
    }

    @Test
    public void find_ById_With_Items() {
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);

        Flux<OrderItem> findOrderItem = itemRepository.save(item)
                .flatMap(savedItem -> {
                    Order order = new Order();
                    order.setTotalSum(50L);
                    return orderRepository.save(order)
                            .map(savedOrder -> new OrderItem(savedItem.getId(), savedItem.getTitle(),
                                    savedItem.getPrice(), 1, savedOrder));
                })
                .flatMap(orderItem -> orderItemRepository.save(orderItem))
                .flatMapMany(savedOrderItem -> orderItemRepository.findByOrderId(savedOrderItem.getOrderId()));

        StepVerifier.create(findOrderItem)
                .assertNext(foundOrderItem -> {
                    assertThat(foundOrderItem.getTitle()).isEqualTo("яблоко");
                    assertThat(foundOrderItem.getCount()).isEqualTo(1);
                    assertThat(foundOrderItem.getOrderId()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void found_By_Item_Id_Not_Found() {
        Flux<OrderItem> found = orderItemRepository.findByOrderId(999L);
        StepVerifier.create(found).verifyComplete();
    }
}
