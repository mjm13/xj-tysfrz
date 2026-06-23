package com.xj.tysfrz.basic.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SystemInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void info_shouldReturnReleaseAndFlywayVersion() throws Exception {
        mockMvc.perform(get("/api/system/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.platformName").value("高校综合身份数据平台"))
                .andExpect(jsonPath("$.data.release").value("0.2.0-test"))
                .andExpect(jsonPath("$.data.backendVersion").value("0.2.0-test"))
                .andExpect(jsonPath("$.data.flywayVersion").value("6"))
                .andExpect(jsonPath("$.data.apiPrefix").value("/api"))
                .andExpect(jsonPath("$.data.maintenance").value(false))
                .andExpect(jsonPath("$.data.gitCommit").exists())
                .andExpect(jsonPath("$.data.buildTime").exists());
    }
}
