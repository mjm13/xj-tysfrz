package com.xj.zbpt.business.access.web.dto;

import com.xj.zbpt.common.access.DataScope;

public record UserSummaryDto(
        String platformUserId,
        String username,
        String status,
        String departmentCode,
        DataScope dataScope
) {
}
