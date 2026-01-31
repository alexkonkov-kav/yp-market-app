package com.market.repository;

import com.market.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void should_find_items_by_title_or_description_ignoring_case() {
        Item item1 = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Item item2 = new Item("мяч", "футбольный мяч", "images/ball.jpg", 300);
        Item item3 = new Item("футболка", "футболка черная", "images/t-shirt.jpg", 1000);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        Page<Item> result = itemRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("мяч", "мяч", PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("мяч");
    }
}
