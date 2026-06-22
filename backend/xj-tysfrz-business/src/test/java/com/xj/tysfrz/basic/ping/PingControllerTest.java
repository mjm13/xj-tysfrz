package com.xj.tysfrz.basic.ping;

import com.xj.tysfrz.framework.exception.GlobalExceptionHandler;
import com.xj.tysfrz.framework.config.CorsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 切片测试（不连数据库）：验证统一响应与全局异常两条横切能力。
 */
@WebMvcTest(
        controllers = PingController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.xj\\.tysfrz\\.framework\\.auth\\..*|com\\.xj\\.tysfrz\\.business\\.access\\..*"
        )
)
@Import({GlobalExceptionHandler.class, CorsConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
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
    void ping_shouldAllowDevOrigin() throws Exception {
        mockMvc.perform(get("/api/ping").header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
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
