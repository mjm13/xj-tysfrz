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
class OrgNodeAdminIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void listRoots_asAdmin_shouldReturnSysu() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/org-nodes/roots")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[?(@.code == 'SYSU')]").exists());
    }

    @Test
    void listChildren_asAdmin_shouldReturnDirectChildren() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/org-nodes/children")
                        .param("parentCode", "SYSU")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[?(@.code == 'CAT-party')]").exists());
    }

    @Test
    void listRoots_asDeptAdmin_shouldReturn403() throws Exception {
        String token = loginAndGetToken("dept_admin", "admin123");
        mockMvc.perform(get("/api/admin/org-nodes/roots")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void createOrgNode_asAdmin_shouldSucceed() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/org-nodes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"TEST-DEPT","name":"测试部门","parentCode":"CAT-party"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.code").value("TEST-DEPT"))
                .andExpect(jsonPath("$.data.parentCode").value("CAT-party"));
    }

    @Test
    void createOrgNode_withInvalidParent_shouldFail() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/org-nodes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"BAD-PARENT","name":"无效父节点","parentCode":"NO-SUCH"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("父节点不存在: NO-SUCH"));
    }

    @Test
    void updateOrgNode_asAdmin_shouldSucceed() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(put("/api/admin/org-nodes/CAT-party")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"党群部门（已更新）"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("党群部门（已更新）"));
    }

    @Test
    void updateOrgNode_withCycle_shouldFail() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(put("/api/admin/org-nodes/SYSU")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"中山大学","parentCode":"01020"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("父节点设置会导致组织环"));
    }

    @Test
    void createOrgNode_thenListAll_shouldIncludeNewNode() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/admin/org-nodes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"USER-FORM-DEPT","name":"用户表单可见部门","parentCode":"SYSU"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/admin/org-nodes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.code == 'USER-FORM-DEPT')]").exists());
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
