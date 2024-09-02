package com.example.tabletop.menu.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.menu.dto.MenuDTO;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.exception.MenuNotFoundException;
import com.example.tabletop.menu.service.MenuService;
import com.example.tabletop.menuimage.service.MenuImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stores/{storeId}/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;
  private final MenuImageService imageService;

  // 메뉴 조회
  @GetMapping()
  public ResponseEntity<List<MenuDTO>> getMenus(
      @PathVariable Long storeId,
      @RequestParam(required = false) Long lastMenuId,
      @RequestParam(defaultValue = "20") int limit) {
    List<Menu> menus = menuService.getMenusForInfiniteScroll(storeId, lastMenuId, limit);
    List<MenuDTO> menuDTOs = menus.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(menuDTOs);
  }


  // 메뉴 등록
  /**
   * 메뉴를 생성하고, 선택적으로 메뉴 이미지 파일을 업로드하는 HTTP POST 요청을 처리하는 메서드입니다.
   * 요청 본문에서 메뉴 데이터와 선택적 이미지 파일을 받아 메뉴를 생성하고, 생성된 메뉴 정보를 {@link MenuDTO} 형식으로 반환합니다.
   *
   * @param storeId       메뉴를 추가할 {@link Store}의 ID입니다. URL 경로 변수로 제공됩니다.
   * @param menuDTO       생성할 메뉴의 데이터를 담고 있는 {@link MenuDTO} 객체입니다. 요청 본문에서 제공됩니다.
   * @param image         메뉴의 이미지 파일을 나타내는 {@link MultipartFile} 객체입니다. 요청 본문에서 제공되며, 선택적입니다.
   * @return              생성된 {@link MenuDTO} 객체를 포함하는 {@link ResponseEntity}를 반환합니다.
   *                      성공적으로 생성된 경우 HTTP 상태 201(CREATED)을 반환합니다.
   *                      이미지 저장 중 또는 다른 오류 발생 시 HTTP 상태 500(INTERNAL_SERVER_ERROR)을 반환합니다.
   * @throws IOException  이미지 파일 처리 중 발생할 수 있는 예외입니다.
   * @throws Exception    일반적인 예외가 발생할 수 있습니다.
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MenuDTO> createMenu(
      @PathVariable Long storeId,
      @RequestPart(name = "menuData") MenuDTO menuDTO,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    try {
      // 메뉴를 생성합니다.
      Menu newMenu = menuService.createMenu(
          storeId,
          menuDTO.getName(),
          menuDTO.getPrice(),
          menuDTO.getDescription(),
          menuDTO.getIsAvailable(),
          image
      );

      // 생성된 메뉴를 DTO로 변환하여 응답합니다.
      return new ResponseEntity<>(convertToDTO(newMenu), HttpStatus.CREATED);
    } catch (IOException e) {
      // 이미지 파일 처리 중 오류 발생 시
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      // 일반적인 오류 발생 시
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * 메뉴를 업데이트하고, 선택적으로 메뉴 이미지 파일을 업로드하는 HTTP PUT 요청을 처리하는 메서드입니다.
   * 요청 본문에서 메뉴 데이터와 선택적 이미지 파일을 받아 메뉴를 업데이트하고, 업데이트된 메뉴 정보를 {@link MenuDTO} 형식으로 반환합니다.
   *
   * @param storeId       메뉴가 속한 {@link Store}의 ID입니다. URL 경로 변수로 제공됩니다.
   * @param menuId        업데이트할 메뉴의 ID입니다. URL 경로 변수로 제공됩니다.
   * @param menuDTO       업데이트할 메뉴의 데이터를 담고 있는 {@link MenuDTO} 객체입니다. 요청 본문에서 제공됩니다.
   * @param image         새로운 메뉴 이미지 파일을 나타내는 {@link MultipartFile} 객체입니다. 요청 본문에서 제공되며, 선택적입니다.
   * @return              업데이트된 {@link MenuDTO} 객체를 포함하는 {@link ResponseEntity}를 반환합니다.
   *                      성공적으로 업데이트된 경우 HTTP 상태 200(OK)을 반환합니다.
   *                      오류 발생 시 HTTP 상태 500(INTERNAL_SERVER_ERROR)과 오류 메시지를 반환합니다.
   * @throws Exception    일반적인 예외가 발생할 수 있습니다.
   */
  @PutMapping(value = "/{menuId}")
  public ResponseEntity<?> updateMenu(
      @PathVariable Long storeId,
      @PathVariable Long menuId,
      @RequestPart("menuData") MenuDTO menuDTO,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    try {
      // 메뉴를 업데이트합니다.
      Menu updatedMenu = menuService.updateMenu(
          storeId,
          menuId,
          menuDTO.getName(),
          menuDTO.getPrice(),
          menuDTO.getDescription(),
          menuDTO.getIsAvailable(),
          image
      );

      // 업데이트된 메뉴를 DTO로 변환하여 응답합니다.
      return ResponseEntity.ok(convertToDTO(updatedMenu));
    } catch (Exception e) {
      // 일반적인 오류 발생 시
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error updating menu: " + e.getMessage());
    }
  }


  // 메뉴 삭제
  @DeleteMapping("/{menuId}")
  public ResponseEntity<?> deleteMenu(@PathVariable Long storeId, @PathVariable Long menuId) {
    try {
      menuService.deleteMenu(menuId);
      return ResponseEntity.noContent().build();
    } catch (MenuNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error deleting menu: " + e.getMessage());
    }
  }

  @GetMapping("/{menuId}")
  public ResponseEntity<MenuDTO> getMenu(@PathVariable Long menuId) {
    Menu menu = menuService.getMenu(menuId);
    return ResponseEntity.ok(convertToDTO(menu));
  }

  private MenuDTO convertToDTO(Menu menu) {
    MenuDTO dto = new MenuDTO();
    dto.setId(menu.getId());
    dto.setStoreId(menu.getStore().getStoreId());
    dto.setName(menu.getName());
    dto.setPrice(menu.getPrice());
    dto.setDescription(menu.getDescription());
    dto.setIsAvailable(menu.getIsAvailable());
    dto.setS3MenuUrl(menu.getMenuImage().getS3Url());
    return dto;
  }
}