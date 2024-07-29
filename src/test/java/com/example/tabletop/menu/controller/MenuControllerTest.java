package com.example.tabletop.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.example.tabletop.auth.service.MailService;
import com.example.tabletop.commons.config.JwtTokenProvider;
import com.example.tabletop.image.service.ImageService;
import com.example.tabletop.menu.dto.MenuDTO;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menu.service.MenuService;
import com.example.tabletop.seller.repository.SellerRepository;
import com.example.tabletop.store.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({MenuService.class})
@MockBean(JavaMailSender.class)
@TestPropertySource(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test",
    "spring.mail.password=test"
})
public class MenuControllerTest {
    @MockBean
    private MenuRepository menuRepository;

    @MockBean
    private StoreRepository storeRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private SellerRepository sellerRepository;
    
    @MockBean
    private MailService mailService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @Autowired
    private ObjectMapper objectMapper;

    private Menu testMenu;

    @BeforeEach
    void setUp() {
        testMenu = new Menu();
        testMenu.setId(1L);
        testMenu.setName("Test Menu");
        testMenu.setPrice(1000);
        testMenu.setDescription("Test Description");
        testMenu.setIsAvailable(true);
    }

    @Test
    void testGetMenu() throws Exception {
        when(menuService.getMenu(1L)).thenReturn(testMenu);

        mockMvc.perform(get("/api/stores/1/menus/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Test Menu"))
               .andExpect(jsonPath("$.price").value(1000));
    }

    @Test
    void testCreateMenu() throws Exception {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setName("New Menu");
        menuDTO.setPrice(2000);
        menuDTO.setDescription("New Description");
        menuDTO.setIsAvailable(true);

        when(menuService.createMenu(eq(1L), anyString(), anyInt(), anyString(), anyBoolean(), any()))
            .thenReturn(testMenu);

        mockMvc.perform(post("/api/stores/1/menus")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(menuDTO)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.name").value("Test Menu"));
    }

    @Test
    void testUpdateMenu() throws Exception {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setName("Updated Menu");
        menuDTO.setPrice(3000);
        menuDTO.setDescription("Updated Description");
        menuDTO.setIsAvailable(false);

        when(menuService.updateMenu(eq(1L), eq(1L), anyString(), anyInt(), anyString(), anyBoolean(), any()))
            .thenReturn(testMenu);

        mockMvc.perform(put("/api/stores/1/menus/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(menuDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Test Menu"));
    }

    @Test
    void testDeleteMenu() throws Exception {
        mockMvc.perform(delete("/api/stores/1/menus/1"))
               .andExpect(status().isNoContent());

        verify(menuService, times(1)).deleteMenu(1L, 1L);
    }
}