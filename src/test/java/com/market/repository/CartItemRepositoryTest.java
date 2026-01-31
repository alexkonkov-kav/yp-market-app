package com.market.repository;

import com.market.model.CartItem;
import com.market.model.Item;
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
public class CartItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void found_By_Item_Id() {
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Item saveItem = itemRepository.save(item);
        CartItem cartItem = new CartItem(saveItem, 50);
        cartItemRepository.save(cartItem);

        Optional<CartItem> found = cartItemRepository.findByItemId(saveItem.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getItem().getTitle()).isEqualTo("яблоко");
        assertThat(found.get().getCount()).isEqualTo(50);
    }

    @Test
    public void find_All_With_Items() {
        Item item1 = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Item item2 = new Item("мяч", "футбольный мяч", "images/ball.jpg", 300);
        List<Item> saveItems = itemRepository.saveAll(List.of(item1, item2));
        CartItem cartItem1 = new CartItem(saveItems.get(0), 50);
        CartItem cartItem2 = new CartItem(saveItems.get(1), 300);
        cartItemRepository.saveAll(List.of(cartItem1, cartItem2));

        List<CartItem> results = cartItemRepository.findAllWithItems();

        assertThat(results).hasSize(2);
        assertThat(results).extracting(ci -> ci.getItem().getTitle())
                .containsExactlyInAnyOrder("яблоко", "мяч");
    }

    @Test
    public void found_By_Item_Id_Not_Found() {
        Optional<CartItem> found = cartItemRepository.findByItemId(999L);
        assertThat(found).isEmpty();
    }
}
