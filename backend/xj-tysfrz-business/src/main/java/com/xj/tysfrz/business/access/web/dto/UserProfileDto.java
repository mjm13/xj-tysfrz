package com.xj.tysfrz.business.access.web.dto;

import java.util.Set;

public record UserProfileDto(
        String platformUserId,
        String username,
        Set<String> roles,
        Set<String> permissions,
        String dataScope,
        String departmentCode,
        boolean globalScope,
        Set<String> scopedDeptCodes
) {
}
