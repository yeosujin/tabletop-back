package com.example.tabletop.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.seller.dto.SellerResponseDTO;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.exception.DuplicateLoginIdException;
import com.example.tabletop.seller.exception.InvalidSellerDataException;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.repository.SellerRepository;
import com.example.tabletop.seller.service.SellerService;

class SellerServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private SellerDTO testSellerDTO;
    private Seller testSeller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testSellerDTO = SellerDTO.builder()
                .loginId("testid")
                .username("testuser")
                .password("password1234")
                .email("ggbb0308@naver.com")
                .mobile("010-1111-2222")
                .build();

        testSeller = Seller.builder()
                .loginId("testid")
                .username("testuser")
                .password("encodedPassword")
                .email("ggbb0308@naver.com")
                .mobile("010-1111-2222")
                .build();
    }

    @Test
    void testSignUp() {
        when(passwordEncoder.encode(testSellerDTO.getPassword())).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        sellerService.signUp(testSellerDTO);

        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    void testIsLoginIdDuplicate() {
        when(sellerRepository.findByLoginId("testid")).thenReturn(Optional.of(testSeller));

        assertThrows(DuplicateLoginIdException.class, () -> {
            sellerService.isLoginIdDuplicate("testid");
        });
    }

    @Test
    void testGetSeller() throws SellerNotFoundException {
        when(sellerRepository.findByLoginId("testid")).thenReturn(Optional.of(testSeller));

        SellerResponseDTO.SellerInfoDTO sellerInfoDTO = sellerService.getSeller("testid");

        assertNotNull(sellerInfoDTO);
        assertEquals(testSeller.getLoginId(), sellerInfoDTO.getLoginId());
    }

    @Test
    void testUpdateSeller() throws SellerNotFoundException, InvalidSellerDataException {
        when(sellerRepository.findByLoginId("testid")).thenReturn(Optional.of(testSeller));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerResponseDTO updatedSeller = sellerService.updateSeller("testid", testSellerDTO);

        assertNotNull(updatedSeller);
        assertEquals(testSellerDTO.getUsername(), updatedSeller.getUsername());
    }

    @Test
    void testDeleteSeller() throws SellerNotFoundException {
        when(sellerRepository.findByLoginId("testid")).thenReturn(Optional.of(testSeller));
        doNothing().when(sellerRepository).delete(testSeller);

        sellerService.deleteSeller("testid");

        verify(sellerRepository, times(1)).delete(testSeller);
    }
}