package com.xj.zbpt.ping;

import com.xj.zbpt.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 切片测试（不连数据库）：验证统一响应与全局异常两条横切能力。
 */
@WebMvcTest(PingController.class)
@Import(GlobalExceptionHandler.class)
class PingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ping_shouldReturnUnifiedSuccess() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("pong"));
    }

    @Test
    void boom_shouldReturnUnifiedFailure() throws Exception {
        mockMvc.perform(get("/api/ping/boom").param("message", "测试异常"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("测试异常"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
