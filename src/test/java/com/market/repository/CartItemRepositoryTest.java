package com.market.repository;

import com.market.model.CartItem;
import com.market.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
public class CartItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void found_By_Item_Id() {
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Mono<CartItem> saveCartItem = itemRepository.save(item)
                .flatMap(saveItem -> {
                    CartItem cartItem = new CartItem(saveItem, 2);
                    return cartItemRepository.save(cartItem);
                });

        StepVerifier.create(saveCartItem)
                .assertNext(foundCartItem -> {
                    assertThat(foundCartItem.getItemId()).isNotNull();
                    assertThat(foundCartItem.getCount()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    public void found_By_Item_Id_Not_Found() {
        Mono<CartItem> found = cartItemRepository.findByItemId(999L);
        StepVerifier.create(found).verifyComplete();
    }
}
