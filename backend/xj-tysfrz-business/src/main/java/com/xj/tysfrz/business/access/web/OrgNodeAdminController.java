package com.xj.tysfrz.business.access.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xj.tysfrz.business.access.service.OrgNodeAdminAppService;
import com.xj.tysfrz.business.access.web.dto.OrgNodeSummaryDto;
import com.xj.tysfrz.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
