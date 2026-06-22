package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrgNodeRequest(
        @NotBlank String name,
        String parentCode
) {
}
