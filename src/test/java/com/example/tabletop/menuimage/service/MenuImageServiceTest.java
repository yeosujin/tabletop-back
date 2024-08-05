package com.example.tabletop.menuimage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menuimage.entity.MenuImage;
import com.example.tabletop.menuimage.exception.MenuImageNotFoundException;
import com.example.tabletop.menuimage.exception.MenuImageProcessingException;
import com.example.tabletop.menuimage.repository.MenuImageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.*;

class MenuImageServiceTest {
    @Mock
    private MenuImageRepository menuImageRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private MenuImageService menuImageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveImage() throws Exception {
        // given
        Long menuId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        Menu menu = Menu.builder().id(menuId).build();
        MenuImage menuImage = MenuImage.builder()
                .menuImageId(1L)
                .menuId(menuId)
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("C:\\tabletop")
                .S3Url("https://tabletop-tabletop.s3.ap-northeast-2.amazonaws.com/tabletop/menu/1/uuid_filename.jpg")
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(file.getOriginalFilename()).thenReturn("original_filename.jpg");
        when(menuImageRepository.save(any(MenuImage.class))).thenReturn(menuImage);

        // when
        MenuImage result = menuImageService.saveImage(file, menuId);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(menuId, result.getMenuId());
        verify(menuImageRepository, times(1)).save(any(MenuImage.class));
        verify(amazonS3, times(1)).putObject(any());
    }

    @Test
    void testGetImage() {
        // given
        Long imageId = 1L;
        MenuImage menuImage = MenuImage.builder()
                .menuImageId(imageId)
                .menuId(1L)
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("C:\\tabletop")
                .S3Url("https://tabletop-tabletop.s3.ap-northeast-2.amazonaws.com/tabletop/menu/1/uuid_filename.jpg")
                .build();

        when(menuImageRepository.findById(imageId)).thenReturn(Optional.of(menuImage));

        // when
        MenuImage result = menuImageService.getImage(imageId);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(imageId, result.getMenuImageId());
        verify(menuImageRepository, times(1)).findById(imageId);
    }

    @Test
    void testGetImageNotFound() {
        // given
        Long imageId = 1L;
        when(menuImageRepository.findById(imageId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(MenuImageNotFoundException.class, () -> menuImageService.getImage(imageId));
    }

    @Test
    void testDeleteImage() throws MenuImageProcessingException {
        // given
        Long imageId = 1L;
        MenuImage menuImage = MenuImage.builder()
                .menuImageId(imageId)
                .menuId(1L)
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("C:\\tabletop")
                .S3Url("https://tabletop-tabletop.s3.ap-northeast-2.amazonaws.com/tabletop/menu/1/uuid_filename.jpg")
                .build();

        when(menuImageRepository.findById(imageId)).thenReturn(Optional.of(menuImage));

        // when
        menuImageService.deleteImage(imageId);

        // then
        verify(amazonS3, times(1)).deleteObject(anyString(), anyString());
        verify(menuImageRepository, times(1)).delete(menuImage);
    }
}