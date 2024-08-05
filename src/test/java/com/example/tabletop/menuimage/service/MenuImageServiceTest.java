package com.example.tabletop.menuimage.service;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuImageRepository;
import com.example.tabletop.menu.service.MenuImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class MenuImageServiceTest {

    @Mock
    private MenuImageRepository menuImageRepository;

    @InjectMocks
    private MenuImageService menuImageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadMenuImage() throws IOException {
        // given
        Long menuId = 1L;
        MultipartFile image = mock(MultipartFile.class);
        when(image.getBytes()).thenReturn(new byte[]{});

        // when
        menuImageService.uploadMenuImage(menuId, image);

        // then
        verify(menuImageRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void testGetMenuImage() {
        // given
        Long menuId = 1L;
        byte[] imageBytes = new byte[]{1, 2, 3};
        when(menuImageRepository.findByMenuId(menuId)).thenReturn(Optional.of(imageBytes));

        // when
        byte[] result = menuImageService.getMenuImage(menuId);

        // then
        Assertions.assertArrayEquals(imageBytes, result);
        verify(menuImageRepository, times(1)).findByMenuId(menuId);
    }
}