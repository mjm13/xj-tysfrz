package com.xj.tysfrz.business.access.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xj.tysfrz.business.access.service.OrgNodeAdminAppService;
import com.xj.tysfrz.business.access.web.dto.CreateOrgNodeRequest;
import com.xj.tysfrz.business.access.web.dto.OrgNodeSummaryDto;
import com.xj.tysfrz.business.access.web.dto.UpdateOrgNodeRequest;
import com.xj.tysfrz.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/org-nodes")
public class OrgNodeAdminController {

    private final OrgNodeAdminAppService orgNodeAdminAppService;

    public OrgNodeAdminController(OrgNodeAdminAppService orgNodeAdminAppService) {
        this.orgNodeAdminAppService = orgNodeAdminAppService;
    }

    @GetMapping
    @SaCheckPermission("admin:users:read")
    public ApiResponse<List<OrgNodeSummaryDto>> listOrgNodes() {
        return ApiResponse.ok(orgNodeAdminAppService.listOrgNodes());
    }

    @GetMapping("/roots")
    @SaCheckPermission("admin:departments:read")
    public ApiResponse<List<OrgNodeSummaryDto>> listRootNodes() {
        return ApiResponse.ok(orgNodeAdminAppService.listRootNodes());
    }

    @GetMapping("/children")
    @SaCheckPermission("admin:departments:read")
    public ApiResponse<List<OrgNodeSummaryDto>> listChildNodes(@RequestParam String parentCode) {
        return ApiResponse.ok(orgNodeAdminAppService.listChildNodes(parentCode));
    }

    @PostMapping
    @SaCheckPermission("admin:departments:write")
    public ApiResponse<OrgNodeSummaryDto> createOrgNode(@Valid @RequestBody CreateOrgNodeRequest request) {
        return ApiResponse.ok(orgNodeAdminAppService.createOrgNode(request));
    }

    @PutMapping("/{code}")
    @SaCheckPermission("admin:departments:write")
    public ApiResponse<OrgNodeSummaryDto> updateOrgNode(
            @PathVariable String code,
            @Valid @RequestBody UpdateOrgNodeRequest request
    ) {
        return ApiResponse.ok(orgNodeAdminAppService.updateOrgNode(code, request));
    }
}
