package com.example.Controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.controller.ItemsApiController;
import com.example.exception.handle.GlobalExceptionHandler;
import com.example.service.ItemService;

@ExtendWith(MockitoExtension.class)
public class ItemsApiControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ItemsApiController itemsApiController;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setup() {
        // MockMvcにハンドラーを登録することで、例外を500エラーとして処理させる
        mockMvc = MockMvcBuilders.standaloneSetup(itemsApiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Handlerがステータスコード500のエラーを取得できているかのテスト
     */

    @Test
    void test500Error_ShouldReturnCustomJson() throws Exception {
        // 予期せぬエラー（500相当）を発生させる
        when(itemService.findByName(anyString())).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/items").param("name", "test"))
                .andExpect(status().isInternalServerError()) // 500
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }
}
