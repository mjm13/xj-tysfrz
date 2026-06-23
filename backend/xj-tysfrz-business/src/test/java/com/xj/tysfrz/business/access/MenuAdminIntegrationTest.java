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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuAdminIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void listMenus_asAdmin_shouldReturnTree() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/menus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[?(@.menuCode == 'nav.top.home')]").exists());
    }

    @Test
    void listMenus_asDeptAdmin_shouldReturn403() throws Exception {
        String token = loginAndGetToken("dept_admin", "admin123");
        mockMvc.perform(get("/api/admin/menus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void createLinkMenu_withMultiplePermissions_shouldSucceed() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/menus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuCode": "nav.test.multi",
                                  "label": "测试多权限",
                                  "path": "/test/multi",
                                  "parentCode": "nav.top.platform",
                                  "sortOrder": 99,
                                  "menuType": "LINK",
                                  "moduleKey": null,
                                  "permissionCodes": ["admin:users:read", "admin:users:write"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.permissionCodes.length()").value(2));
    }

    @Test
    void createLinkMenu_withoutPermissions_shouldFail() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/menus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuCode": "nav.test.bad",
                                  "label": "无效",
                                  "path": "/test/bad",
                                  "sortOrder": 1,
                                  "menuType": "LINK",
                                  "permissionCodes": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));
    }

    @Test
    void hideMenu_thenNavigation_shouldExclude() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(patch("/api/admin/menus/nav.top.identity.org/visible")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visible\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/navigation")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topBar[?(@.menuCode == 'nav.top.identity')].children[?(@.menuCode == 'nav.top.identity.org')]").doesNotExist());
    }

    @Test
    void permissionTree_asAdmin_shouldIncludeMenuPermissions() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/menus/permission-tree")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data..[?(@.menuCode == 'nav.top.platform.users')].permissionCodes").isArray());
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
