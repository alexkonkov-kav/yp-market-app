package com.market.repository;

import com.market.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void cleanUp() {
        itemRepository.deleteAll().block();
    }

    @Test
    public void find_All_By() {
        Flux<Item> items = itemRepository.saveAll(List.of(
                new Item("яблоко", "яблоко красное", "images/apple.jpg", 50),
                new Item("мяч", "футбольный мяч", "images/ball.jpg", 300),
                new Item("футболка", "футболка черная", "images/t-shirt.jpg", 1000)
        ));
        Flux<Item> find = items.thenMany(itemRepository.findAllBy(PageRequest.of(0, 1)));
        StepVerifier.create(find)
                .assertNext(item -> assertThat(item.getTitle()).isEqualTo("яблоко"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void find_By_Title_Or_Description() {
        Flux<Item> items = itemRepository.saveAll(List.of(
                new Item("яблоко", "яблоко красное", "images/apple.jpg", 50),
                new Item("мяч", "футбольный мяч", "images/ball.jpg", 300),
                new Item("футболка", "футболка черная", "images/t-shirt.jpg", 1000)
        ));
        Flux<Item> find = items.thenMany(itemRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("яблоко", "яблоко", PageRequest.of(0, 2)));
        StepVerifier.create(find)
                .assertNext(item -> assertThat(item.getTitle()).isEqualTo("яблоко"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void count_By_Title_Or_Description() {
        Flux<Item> items = itemRepository.saveAll(List.of(
                new Item("яблоко", "яблоко красное", "images/apple.jpg", 50),
                new Item("мяч", "футбольный мяч", "images/ball.jpg", 300),
                new Item("футболка", "футболка черная", "images/t-shirt.jpg", 1000)
        ));
        Flux<Long> find = items.thenMany(itemRepository
                .countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("о", "о"));
        StepVerifier.create(find)
                .expectNext(3L)
                .verifyComplete();
    }
}
