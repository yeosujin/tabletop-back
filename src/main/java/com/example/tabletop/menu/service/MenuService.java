package com.example.tabletop.menu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.exception.MenuNotFoundException;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menuimage.entity.MenuImage;
import com.example.tabletop.menuimage.exception.MenuImageProcessingException;
import com.example.tabletop.menuimage.service.MenuImageService;
import com.example.tabletop.orderitem.repository.OrderitemRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.exception.StoreNotFoundException;
import com.example.tabletop.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository menuRepository;
  private final StoreRepository storeRepository;
  private final MenuImageService imageService;
  private final OrderitemRepository orderitemRepository;

  /**
   *
   * @param storeId       {@link Long} 가게 Id
   * @param lastMenuId    {@link Long} 이전에 등록된 메뉴 id
   * @param limit         {@link int} 불러오는 메뉴 마지막
   * @return              해당 가게의 메뉴 전체
   */
  @Transactional(readOnly = true)
  public List<Menu> getMenusForInfiniteScroll(Long storeId, Long lastMenuId, int limit) {
    log.info("Fetching menus for store id: {}, last menu id: {}, limit: {}", storeId, lastMenuId,
        limit);
    Pageable pageable = PageRequest.of(0, limit);
    if (lastMenuId == null) {
      return menuRepository.findInitialMenusForInfiniteScroll(storeId, pageable);
    } else {
      return menuRepository.findMenusForInfiniteScroll(storeId, lastMenuId, pageable);
    }
  }

  @Transactional(readOnly = true)
  public List<Menu> getMenusByStoreId(Long storeId) {
    log.info("Fetching all menus for store id: {}", storeId);
    return menuRepository.findByStore_StoreId(storeId);
  }



   /**
   * 새로운 메뉴를 생성하고, 선택적으로 메뉴 이미지 파일을 저장하는 메서드입니다.
   * 주어진 스토어 ID에 해당하는 스토어가 존재하지 않을 경우 예외를 발생시키며,
   * 메뉴 이름이 null이거나 비어 있는 경우에도 예외를 발생시킵니다.
   * 이미지 파일이 제공된 경우, 이미지 파일을 저장합니다.
   *
   * @param storeId       메뉴를 추가할 {@link Store}의 ID입니다.
   * @param name          메뉴의 이름입니다. null 또는 빈 문자열일 수 없습니다.
   * @param price         메뉴의 가격입니다. null일 수 없습니다.
   * @param description   메뉴의 설명입니다. null일 수 있습니다.
   * @param isAvailable   메뉴의 사용 가능 여부를 나타내는 {@link Boolean} 값입니다.
   * @param imageFile     메뉴 이미지 파일을 나타내는 {@link MultipartFile} 객체입니다. 이미지 파일이 null일 수 있습니다.
   * @return              생성된 {@link Menu} 객체를 반환합니다.
   * @throws IllegalArgumentException  메뉴 이름이 null이거나 빈 문자열일 경우 발생하는 예외입니다.
   * @throws StoreNotFoundException    주어진 ID로 스토어를 찾을 수 없는 경우 발생하는 예외입니다.
   * @throws MenuImageProcessingException
   *                                이미지 파일을 저장하는 과정에서 발생하는 예외입니다.
   * @throws Exception                 일반적인 예외가 발생할 수 있습니다.
   */
  @Transactional
  public Menu createMenu(Long storeId, String name, Integer price, String description,
      Boolean isAvailable, MultipartFile imageFile) throws MenuImageProcessingException, Exception {
    log.info("Creating new menu for store id: {}", storeId);
    if (name == null || name.trim().isEmpty()) {
      log.error("Attempt to create menu with null or empty name");
      throw new IllegalArgumentException("Menu name cannot be null or empty");
    }
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> {
          log.error("Store not found with id: {}", storeId);
          return new StoreNotFoundException("Store not found with id: " + storeId);
        });

    Menu menu = Menu.builder()
        .name(name)
        .price(price)
        .description(description)
        .isAvailable(isAvailable)
        .store(store)
        .build();

    Long savedMenuId = menuRepository.save(menu).getId();
    System.out.println("menuid : " + savedMenuId);
    if (imageFile != null && !imageFile.isEmpty()) {
      MenuImage image = imageService.saveImage(imageFile, savedMenuId);
//            menu.setMenuImage(image);
    }

    log.info("Created new menu with id: {} for store id: {}", savedMenuId, storeId);
    return menu;
  }

  /**
   * 메뉴를 업데이트하는 메서드입니다. 주어진 메뉴 ID에 해당하는 메뉴를 수정하고,
   * 선택적으로 새로운 이미지 파일을 저장합니다.
   * 주어진 메뉴 ID에 해당하는 메뉴가 존재하지 않거나, 메뉴가 지정된 스토어에 속하지 않을 경우 예외를 발생시킵니다.
   * 이미지 파일이 제공된 경우, 기존 이미지를 삭제하고 새 이미지를 저장합니다.
   *
   * @param storeId       메뉴가 속한 {@link Store}의 ID입니다. 메뉴가 이 스토어에 속하는지 검증합니다.
   * @param menuId        업데이트할 메뉴의 ID입니다.
   * @param name          메뉴의 새로운 이름입니다. null일 수 있습니다.
   * @param price         메뉴의 새로운 가격입니다. null일 수 있습니다.
   * @param description   메뉴의 새로운 설명입니다. null일 수 있습니다.
   * @param isAvailable   메뉴의 사용 가능 여부를 나타내는 {@link Boolean} 값입니다. null일 수 있습니다.
   * @param imageFile     새로운 메뉴 이미지 파일을 나타내는 {@link MultipartFile} 객체입니다. 이미지 파일이 null일 수 있습니다.
   * @return              업데이트된 {@link Menu} 객체를 반환합니다.
   * @throws MenuNotFoundException       주어진 ID로 메뉴를 찾을 수 없는 경우 발생하는 예외입니다.
   * @throws IllegalArgumentException   메뉴가 지정된 스토어에 속하지 않는 경우 발생하는 예외입니다.
   * @throws MenuImageProcessingException
   *                                  이미지 파일을 처리하는 과정에서 발생하는 예외입니다.
   * @throws Exception                  일반적인 예외가 발생할 수 있습니다.
   */
  @Transactional
  public Menu updateMenu(Long storeId, Long menuId, String name, Integer price, String description,
      Boolean isAvailable, MultipartFile imageFile) throws MenuImageProcessingException, Exception {
    log.info("Updating menu with id: {} for menuImage id: {}", menuId);
    Menu menu = menuRepository.findById(menuId)
        .orElseThrow(() -> {
          log.error("Menu not found with id: {}", menuId);
          return new MenuNotFoundException("Menu not found with id: " + menuId);
        });

    // 메뉴가 해당 스토어에 속하는지 확인
    if (!menu.getStore().getStoreId().equals(storeId)) {
      log.error("Menu with id: {} does not belong to store", menuId);
      throw new IllegalArgumentException("Menu does not belong to the specified store");
    }

    menu.setName(name);
    menu.setPrice(price);
    menu.setDescription(description);
    menu.setIsAvailable(isAvailable);

    if (imageFile != null && !imageFile.isEmpty()) {
      if (menu.getMenuImage() != null) {
        imageService.deleteImage(menu.getMenuImage().getMenuImageId());
      }
      MenuImage newMenuImage = imageService.saveImage(imageFile, menuId);
      menu.setMenuImage(newMenuImage);
    }

    Menu updatedMenu = menuRepository.save(menu);
    log.info("Updated menu with id: {} for store id: {}", menuId, menuId);
    return updatedMenu;
  }

  @Transactional
  public void deleteMenu(Long menuId) throws IOException {
    Menu menu = menuRepository.findById(menuId)
        .orElseThrow(() -> new MenuNotFoundException("Menu not found with id: " + menuId));

    // 메뉴가 해당 스토어에 속하는지 확인
    if (!menu.getStore().getStoreId().equals(menu.getStore().getStoreId())) {
      throw new IllegalArgumentException("Menu does not belong to the specified store");
    }

    // 이미지가 있다면 삭제
    if (menu.getMenuImage() != null) {
      imageService.deleteImage(menu.getMenuImage().getMenuImageId());
    }

    // order items 있다면 null 전환
    orderitemRepository.nullifyMenuReference(menu.getId());

    // 메뉴만 삭제
    menuRepository.delete(menu);

    log.info("Deleted menu with id: {}", menuId);
  }

  @Transactional(readOnly = true)
  public Menu getMenu(Long menuId) {
    log.info("Fetching menu with id: {}", menuId);
    return menuRepository.findById(menuId)
        .orElseThrow(() -> {
          log.error("Menu not found with id: {}", menuId);
          return new MenuNotFoundException("Menu not found with id: " + menuId);
        });
  }
}