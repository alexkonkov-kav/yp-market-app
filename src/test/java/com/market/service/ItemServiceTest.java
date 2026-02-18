package com.market.service;

import com.market.model.Item;
import com.market.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void get_Item_By_Id_Success() {
        Long itemId = 1L;
        Item item = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        item.setId(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Mono.just(item));
        Mono<Item> resultMono = itemService.getItemById(itemId);
        StepVerifier.create(resultMono)
                .assertNext(e -> {
                    assertThat(e).isNotNull();
                    assertThat(e.getId()).isEqualTo(itemId);
                    assertThat(e.getTitle()).isEqualTo("яблоко");
                })
                .verifyComplete();
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void get_Item_By_Id_NotFound() {
        Long itemId = 99L;
        when(itemRepository.findById(itemId)).thenReturn(Mono.empty());
        Mono<Item> Item = itemService.getItemById(itemId);
        StepVerifier.create(Item)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException)
                .verify();
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void find_All() {
        Pageable pageable = PageRequest.of(0, 2);
        Item item1 = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Item item2 = new Item("мяч", "футбольный мяч", "images/ball.jpg", 300);
        when(itemRepository.findAllBy(pageable)).thenReturn(Flux.just(item1, item2));
        Flux<Item> result = itemService.findAll(pageable);
        StepVerifier.create(result)
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();
        verify(itemRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void find_By_Title_Or_Description() {
        Pageable pageable = PageRequest.of(0, 5);
        Item item1 = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Item item2 = new Item("мяч", "футбольный мяч", "images/ball.jpg", 300);
        when(itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                item1.getTitle(), item1.getTitle(), pageable))
                .thenReturn(Flux.just(item1, item2));
        Flux<Item> result = itemService.findByTitleOrDescription(item1.getTitle(), item1.getTitle(), pageable);
        StepVerifier.create(result)
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();
        verify(itemRepository, times(1))
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(item1.getTitle(), item1.getTitle(), pageable);
    }

    @Test
    void count_All() {
        Long expectedCount = 10L;
        when(itemRepository.count()).thenReturn(Mono.just(expectedCount));
        Mono<Long> result = itemService.countAll();
        StepVerifier.create(result)
                .expectNext(10L)
                .verifyComplete();
        verify(itemRepository, times(1)).count();
    }
}
