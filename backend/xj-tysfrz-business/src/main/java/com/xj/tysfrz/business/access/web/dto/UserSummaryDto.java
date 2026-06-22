package com.xj.tysfrz.business.access.web.dto;

import com.xj.tysfrz.common.access.DataScope;

public record UserSummaryDto(
        String platformUserId,
        String username,
        String status,
        String departmentCode,
        DataScope dataScope
) {
}
