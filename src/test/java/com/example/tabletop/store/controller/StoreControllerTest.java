package com.example.tabletop.store.controller;

import com.example.tabletop.store.controller.StoreController;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.service.StoreService;
import com.example.tabletop.store.service.StoreImageService;
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

import java.util.Arrays;

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
    void testUploadStoreImage() throws Exception {
        // given
        Long storeId = 1L;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{});

        // when
        doNothing().when(storeImageService).uploadStoreImage(storeId, image);

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/stores/{storeId}/image", storeId)
                        .file(image))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(storeImageService, times(1)).uploadStoreImage(storeId, image);
    }

    @Test
    void testGetStoreImage() throws Exception {
        // given
        Long storeId = 1L;
        byte[] imageBytes = new byte[]{1, 2, 3};
        when(storeImageService.getStoreImage(storeId)).thenReturn(imageBytes);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/{storeId}/image", storeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(imageBytes));

        // then
        verify(storeImageService, times(1)).getStoreImage(storeId);
    }
}