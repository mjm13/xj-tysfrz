package com.xj.tysfrz.business.access.web.dto;

import java.util.List;

public record SidebarGroupDto(
        String label,
        List<MenuNodeDto> items
) {
}
