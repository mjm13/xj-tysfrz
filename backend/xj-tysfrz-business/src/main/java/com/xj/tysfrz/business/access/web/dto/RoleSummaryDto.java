package com.xj.tysfrz.business.access.web.dto;

import java.util.List;

public record RoleSummaryDto(
        String roleCode,
        String name,
        String description,
        List<String> permissionCodes,
        boolean systemRole,
        boolean permissionsEditable
) {
}
