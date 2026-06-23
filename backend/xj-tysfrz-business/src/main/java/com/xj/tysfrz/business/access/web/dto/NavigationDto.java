package com.xj.tysfrz.business.access.web.dto;

import java.util.List;
import java.util.Map;

public record NavigationDto(
        List<MenuNodeDto> topBar,
        Map<String, List<SidebarGroupDto>> sidebars
) {
}
