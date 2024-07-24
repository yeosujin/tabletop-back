package com.example.tabletop.menu.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.tabletop.menu.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // 특정 매장의 모든 메뉴 조회
    List<Menu> findByStore_StoreId(Long storeId);

    // 특정 매장의 모든 메뉴 삭제
    void deleteByStore_StoreId(Long storeId);

    // 무한 스크롤을 위한 커서 기반 쿼리
    @Query("SELECT m FROM Menu m WHERE m.store.storeId = :storeId AND m.id > :lastMenuId ORDER BY m.id ASC")
    List<Menu> findMenusForInfiniteScroll(@Param("storeId") Long storeId,
                                          @Param("lastMenuId") Long lastMenuId,
                                          Pageable pageable);

    // 첫 페이지 로딩을 위한 쿼리 (lastMenuId가 null일 때 사용)
    @Query("SELECT m FROM Menu m WHERE m.store.storeId = :storeId ORDER BY m.id ASC")
    List<Menu> findInitialMenusForInfiniteScroll(@Param("storeId") Long storeId,
                                                 Pageable pageable);
}