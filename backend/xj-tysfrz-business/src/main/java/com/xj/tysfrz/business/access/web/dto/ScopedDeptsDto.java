package com.xj.tysfrz.business.access.web.dto;

import java.util.Set;

public record ScopedDeptsDto(
        boolean globalScope,
        String dataScope,
        String departmentCode,
        Set<String> scopedDeptCodes
) {
}
