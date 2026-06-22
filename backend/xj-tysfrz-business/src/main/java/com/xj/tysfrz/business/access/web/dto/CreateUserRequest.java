package com.xj.tysfrz.business.access.web.dto;

import com.xj.tysfrz.common.access.DataScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String departmentCode,
        @NotNull DataScope dataScope
) {
}
