package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReplaceRolePermissionsRequest(
        @NotNull List<String> permissionCodes
) {
}
