package com.market.service;

import com.market.dto.ItemResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DisplayItemServiceTest {

    private final DisplayItemService displayItemService = new DisplayItemService();

    @Test
    void display_Item() {
        List<ItemResponseDto> items = List.of(
                createDto(1L), createDto(2L), createDto(3L), createDto(4L)
        );
        List<List<ItemResponseDto>> result = displayItemService.displayItem(items);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).hasSize(3);
        assertThat(result.get(0).get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).get(2).id()).isEqualTo(3L);
        assertThat(result.get(1)).hasSize(3);
        assertThat(result.get(1).get(0).id()).isEqualTo(4L);
        assertThat(result.get(1).get(1).id()).isEqualTo(-1L);
        assertThat(result.get(1).get(2).id()).isEqualTo(-1L);
    }

    @Test
    void display_Item_Is_Empty() {
        List<List<ItemResponseDto>> result = displayItemService.displayItem(List.of());
        assertThat(result).isEmpty();
    }

    private ItemResponseDto createDto(Long id) {
        return new ItemResponseDto(id, "Title", "Desc", "Path", 100, 1);
    }
}
