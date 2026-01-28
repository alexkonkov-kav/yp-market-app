package com.market.service;

import com.market.dto.ItemResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DisplayItemService {

    public DisplayItemService() {
    }

    public List<List<ItemResponseDto>> displayItem(List<ItemResponseDto> items) {
        List<List<ItemResponseDto>> pages = new ArrayList<>();
        for (int i = 0; i < items.size(); i += 3) {
            List<ItemResponseDto> page = new ArrayList<>(items.subList(i, Math.min(i + 3, items.size())));
            while (page.size() < 3) {
                page.add(new ItemResponseDto(-1L, "", "", "", 0, 0));
            }
            pages.add(page);
        }
        return pages;
    }
}
