package com.market.service;

import com.market.model.Item;
import com.market.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void get_Item_By_Id() {
        Long itemId = 1L;
        Item mockItem = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        Item result = itemService.getItemById(itemId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getTitle()).isEqualTo("Test Item");
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void get_Item_By_Id_Not_Found() {
        Long itemId = 999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.getItemById(itemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found Item with ID: " + itemId);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void find_By_Title_Or_Description() {
        String searchText = "яблоко";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title"));
        Item mockItem = new Item("яблоко", "яблоко красное", "images/apple.jpg", 50);
        Page<Item> mockPage = new PageImpl<>(List.of(mockItem), pageable, 1);
        when(itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                eq(searchText), eq(searchText), eq(pageable))).thenReturn(mockPage);
        Page<Item> result = itemService.findByTitleOrDescription(searchText, searchText, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("яблоко");
        verify(itemRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, pageable);
    }
}
