package com.example.tabletop.storeimage.service;

import com.example.tabletop.store.entity.StoreImage;
import com.example.tabletop.store.repository.StoreImageRepository;
import com.example.tabletop.store.service.StoreImageService;
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

class StoreImageServiceTest {

    @Mock
    private StoreImageRepository storeImageRepository;

    @InjectMocks
    private StoreImageService storeImageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadStoreImage() throws IOException {
        // given
        Long storeId = 1L;
        MultipartFile image = mock(MultipartFile.class);
        when(image.getBytes()).thenReturn(new byte[]{});

        // when
        storeImageService.uploadStoreImage(storeId, image);

        // then
        verify(storeImageRepository, times(1)).save(any(StoreImage.class));
    }

    @Test
    void testGetStoreImage() {
        // given
        Long storeId = 1L;
        byte[] imageBytes = new byte[]{1, 2, 3};
        when(storeImageRepository.findByStoreId(storeId)).thenReturn(Optional.of(imageBytes));

        // when
        byte[] result = storeImageService.getStoreImage(storeId);

        // then
        Assertions.assertArrayEquals(imageBytes, result);
        verify(storeImageRepository, times(1)).findByStoreId(storeId);
    }
}