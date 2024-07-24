package com.example.tabletop.menu.service;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    /**
     * 특정 가게의 메뉴를 조회합니다.
     *
     * @param storeId 가게 ID
     * @return 해당 가게의 메뉴
     * @throws EntityNotFoundException 메뉴가 없을 경우 예외 발생
     */
    public Menu getMenuByStoreId(Long storeId) {
        return menuRepository.findByStore_StoreId(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found for store ID: " + storeId));
    }
}
