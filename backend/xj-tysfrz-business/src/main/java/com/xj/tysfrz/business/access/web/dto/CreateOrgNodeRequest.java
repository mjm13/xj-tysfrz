package com.xj.tysfrz.business.access.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrgNodeRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotBlank String parentCode
) {
}
