package com.example.tabletop.store.controller;

import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.service.StoreService;
import com.example.tabletop.storeimage.service.StoreImageService;
import com.example.tabletop.storeimage.entity.StoreImage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@WebMvcTest(StoreController.class)
class StoreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @MockBean
    private StoreImageService storeImageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSaveStoreImage() throws Exception {
        // given
        Long storeId = 1L;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{});
        StoreImage savedImage = new StoreImage("filename", "test.jpg", "filepath", "s3url");
        
        // when
        when(storeImageService.saveImage(eq(storeId), any(MockMultipartFile.class))).thenReturn(savedImage);

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/stores/{storeId}/image", storeId)
                        .file(image))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value("filename"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fileOriginalName").value("test.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.filepath").value("filepath"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.s3Url").value("s3url"));

        verify(storeImageService, times(1)).saveImage(eq(storeId), any(MockMultipartFile.class));
    }

    @Test
    void testDeleteStoreImage() throws Exception {
        // given
        Long imageId = 1L;

        // when
        doNothing().when(storeImageService).deleteImageFromS3(imageId);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/stores/image/{imageId}", imageId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(storeImageService, times(1)).deleteImageFromS3(imageId);
    }
}