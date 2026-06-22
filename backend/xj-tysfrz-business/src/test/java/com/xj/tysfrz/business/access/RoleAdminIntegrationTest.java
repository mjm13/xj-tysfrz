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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleAdminIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void listRoles_asAdmin_shouldReturnRolesWithPermissions() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[?(@.roleCode == 'ADMIN')]").exists())
                .andExpect(jsonPath("$.data[?(@.roleCode == 'ADMIN')].permissionsEditable").value(false))
                .andExpect(jsonPath("$.data[?(@.roleCode == 'ADMIN')].permissionCodes").isArray());
    }

    @Test
    void listRoles_asDeptAdmin_shouldReturn403() throws Exception {
        String token = loginAndGetToken("dept_admin", "admin123");
        mockMvc.perform(get("/api/admin/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void listPermissions_asAdmin_shouldIncludeAdminRolesRead() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/permissions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[?(@.permissionCode == 'admin:roles:read')]").exists());
    }

    @Test
    void createRole_asAdmin_shouldSucceed() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roleCode":"AUDITOR","name":"审计岗","description":"只读审计"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.roleCode").value("AUDITOR"))
                .andExpect(jsonPath("$.data.permissionCodes").isEmpty());
    }

    @Test
    void createRole_withAdminCode_shouldFail() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roleCode":"ADMIN","name":"重复管理员"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void replacePermissions_onGovernance_shouldSucceed() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(put("/api/admin/roles/GOVERNANCE/permissions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"permissionCodes":["home:read","identity-basic:read","identity-org:read"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.permissionCodes.length()").value(3));

        mockMvc.perform(get("/api/admin/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.data[?(@.roleCode == 'GOVERNANCE')].permissionCodes[?(@ == 'identity-org:read')]").exists());
    }

    @Test
    void replacePermissions_onAdmin_shouldFail() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(put("/api/admin/roles/ADMIN/permissions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"permissionCodes":["home:read"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void replacePermissions_withUnknownCode_shouldFail() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(put("/api/admin/roles/GOVERNANCE/permissions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"permissionCodes":["no-such:read"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));
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
