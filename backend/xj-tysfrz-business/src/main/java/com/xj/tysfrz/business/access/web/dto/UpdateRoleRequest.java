package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(
        @NotBlank String name,
        String description
) {
}
