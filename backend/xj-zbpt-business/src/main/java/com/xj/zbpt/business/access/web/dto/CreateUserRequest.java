package com.xj.zbpt.business.access.web.dto;

import com.xj.zbpt.common.access.DataScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String departmentCode,
        @NotNull DataScope dataScope
) {
}
