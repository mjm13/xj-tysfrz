package com.xj.tysfrz.business.access.web.dto;

public record OrgNodeSummaryDto(
        String code,
        String name,
        String parentCode,
        boolean hasChildren
) {
}
