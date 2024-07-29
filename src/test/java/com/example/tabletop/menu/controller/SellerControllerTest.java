package com.example.tabletop.menu.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.tabletop.seller.controller.SellerController;
import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.seller.dto.SellerResponseDTO;
import com.example.tabletop.seller.exception.DuplicateLoginIdException;
import com.example.tabletop.seller.exception.InvalidSellerDataException;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.service.SellerService;

@SpringBootTest
class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController sellerController;

    private SellerDTO testSellerDTO;
    private SellerResponseDTO.SellerInfoDTO testSellerInfoDTO;
    private SellerResponseDTO testSellerResponseDTO;

    @BeforeEach
    void setUp() {
        testSellerDTO = SellerDTO.builder()
                .loginId("testid")
                .username("testuser")
                .password("password1234")
                .email("ggbb0308@naver.com")
                .mobile("010-1111-2222")
                .build();

        testSellerInfoDTO = SellerResponseDTO.SellerInfoDTO.builder()
                .loginId("testid")
                .username("testuser")
                .email("ggbb0308@naver.com")
                .mobile("010-1111-2222")
                .doneClickCountSetting(false)
                .build();

        testSellerResponseDTO = SellerResponseDTO.builder()
                .loginId("testid")
                .username("testuser")
                .email("ggbb0308@naver.com")
                .mobile("010-1111-2222")
                .doneClickCountSetting(false)
                .build();
    }

    @Test
    void testSignUp() {
        doNothing().when(sellerService).signUp(testSellerDTO);

        ResponseEntity<String> response = sellerController.signUp(testSellerDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("회원가입이 성공적으로 완료되었습니다.", response.getBody());
    }

    @Test
    void testCheckLoginId() throws DuplicateLoginIdException {
        doNothing().when(sellerService).isLoginIdDuplicate("testid");

        ResponseEntity<String> response = sellerController.checkLoginId("testid");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("사용 가능한 ID입니다.", response.getBody());
    }

    @Test
    void testGetSeller() throws SellerNotFoundException {
        when(sellerService.getSeller("testid")).thenReturn(testSellerInfoDTO);

        ResponseEntity<SellerResponseDTO.SellerInfoDTO> response = sellerController.getSeller("testid");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testSellerInfoDTO.getLoginId(), response.getBody().getLoginId());
    }

    @Test
    void testUpdateSeller() throws SellerNotFoundException, InvalidSellerDataException {
        when(sellerService.updateSeller("testid", testSellerDTO)).thenReturn(testSellerResponseDTO);

        ResponseEntity<SellerResponseDTO> response = sellerController.updateSeller("testid", testSellerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testSellerResponseDTO.getLoginId(), response.getBody().getLoginId());
    }

    @Test
    void testDeleteSeller() throws SellerNotFoundException {
        doNothing().when(sellerService).deleteSeller("testid");

        ResponseEntity<String> response = sellerController.deleteSeller("testid");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}