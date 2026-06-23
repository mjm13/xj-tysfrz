package com.xj.tysfrz.business.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NavigationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getNavigation_asAdmin_shouldIncludePlatformMenus() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/navigation")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.topBar[?(@.menuCode == 'nav.top.platform')]").exists())
                .andExpect(jsonPath("$.data.sidebars['platform-admin']").isArray());
    }

    @Test
    void getNavigation_asGovernance_shouldExcludeOrgMenu() throws Exception {
        String token = loginAndGetToken("dept_admin", "admin123");
        mockMvc.perform(get("/api/navigation")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.topBar[?(@.menuCode == 'nav.top.identity')].children[?(@.path == '/identity/org')]").doesNotExist())
                .andExpect(jsonPath("$.data.topBar[?(@.menuCode == 'nav.top.platform')]").doesNotExist());
    }

    @Test
    void getNavigation_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/navigation"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(login.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
    }
}
