package com.xj.tysfrz.business.access.service;

import com.xj.tysfrz.business.access.infrastructure.MenuEntity;
import com.xj.tysfrz.business.access.web.dto.MenuNodeDto;
import com.xj.tysfrz.business.access.web.dto.NavigationDto;
import com.xj.tysfrz.business.access.web.dto.SidebarGroupDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NavigationAppService {

    private final MenuAdminAppService menuAdminAppService;

    public NavigationAppService(MenuAdminAppService menuAdminAppService) {
        this.menuAdminAppService = menuAdminAppService;
    }

    public NavigationDto getNavigation(Set<String> userPermissions) {
        List<MenuEntity> allMenus = menuAdminAppService.loadAllMenus().stream()
                .filter(m -> Boolean.TRUE.equals(m.getVisible()))
                .toList();
        Map<String, List<String>> permissionCodesByMenu = menuAdminAppService.loadPermissionCodesByMenu();

        Set<String> allowedCodes = filterAllowedMenuCodes(allMenus, permissionCodesByMenu, userPermissions);
        List<MenuEntity> allowedMenus = allMenus.stream()
                .filter(m -> allowedCodes.contains(m.getMenuCode()))
                .toList();

        List<MenuEntity> topBarMenus = allowedMenus.stream()
                .filter(m -> m.getModuleKey() == null || m.getModuleKey().isBlank())
                .toList();
        List<MenuNodeDto> topBar = MenuTreeBuilder.buildFullTree(topBarMenus, permissionCodesByMenu);

        Map<String, List<SidebarGroupDto>> sidebars = new HashMap<>();
        Map<String, List<MenuEntity>> byModule = allowedMenus.stream()
                .filter(m -> m.getModuleKey() != null && !m.getModuleKey().isBlank())
                .collect(Collectors.groupingBy(MenuEntity::getModuleKey));

        for (Map.Entry<String, List<MenuEntity>> entry : byModule.entrySet()) {
            sidebars.put(entry.getKey(), buildSidebarGroups(entry.getValue(), permissionCodesByMenu));
        }

        return new NavigationDto(topBar, sidebars);
    }

    private Set<String> filterAllowedMenuCodes(
            List<MenuEntity> menus,
            Map<String, List<String>> permissionCodesByMenu,
            Set<String> userPermissions
    ) {
        Map<String, MenuEntity> byCode = menus.stream()
                .collect(Collectors.toMap(MenuEntity::getMenuCode, m -> m));
        Map<String, List<String>> childrenByParent = new HashMap<>();
        for (MenuEntity menu : menus) {
            if (menu.getParentCode() != null) {
                childrenByParent.computeIfAbsent(menu.getParentCode(), k -> new ArrayList<>())
                        .add(menu.getMenuCode());
            }
        }

        Set<String> allowed = new HashSet<>();
        for (MenuEntity menu : menus) {
            if (MenuAdminAppService.MENU_TYPE_LINK.equals(menu.getMenuType())) {
                List<String> required = permissionCodesByMenu.getOrDefault(menu.getMenuCode(), List.of());
                boolean ok = required.stream().anyMatch(userPermissions::contains);
                if (ok) {
                    allowed.add(menu.getMenuCode());
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (MenuEntity menu : menus) {
                if (!MenuAdminAppService.MENU_TYPE_GROUP.equals(menu.getMenuType())) {
                    continue;
                }
                if (allowed.contains(menu.getMenuCode())) {
                    continue;
                }
                List<String> children = childrenByParent.getOrDefault(menu.getMenuCode(), List.of());
                if (children.stream().anyMatch(allowed::contains)) {
                    allowed.add(menu.getMenuCode());
                    changed = true;
                }
            }
        }
        return allowed;
    }

    private List<SidebarGroupDto> buildSidebarGroups(
            List<MenuEntity> moduleMenus,
            Map<String, List<String>> permissionCodesByMenu
    ) {
        Set<String> codes = moduleMenus.stream().map(MenuEntity::getMenuCode).collect(Collectors.toSet());
        List<MenuEntity> groupRoots = moduleMenus.stream()
                .filter(m -> MenuAdminAppService.MENU_TYPE_GROUP.equals(m.getMenuType()))
                .filter(m -> m.getParentCode() == null || !codes.contains(m.getParentCode()))
                .sorted(Comparator.comparingInt(MenuEntity::getSortOrder))
                .toList();

        List<SidebarGroupDto> groups = new ArrayList<>();
        for (MenuEntity group : groupRoots) {
            List<MenuNodeDto> items = moduleMenus.stream()
                    .filter(m -> group.getMenuCode().equals(m.getParentCode()))
                    .filter(m -> MenuAdminAppService.MENU_TYPE_LINK.equals(m.getMenuType()))
                    .sorted(Comparator.comparingInt(MenuEntity::getSortOrder))
                    .map(m -> toFlatNode(m, permissionCodesByMenu))
                    .toList();
            if (!items.isEmpty()) {
                groups.add(new SidebarGroupDto(group.getLabel(), items));
            }
        }
        return groups;
    }

    private MenuNodeDto toFlatNode(MenuEntity menu, Map<String, List<String>> permissionCodesByMenu) {
        return new MenuNodeDto(
                menu.getMenuCode(),
                menu.getLabel(),
                menu.getPath(),
                menu.getParentCode(),
                menu.getSortOrder(),
                menu.getMenuType(),
                menu.getModuleKey(),
                Boolean.TRUE.equals(menu.getVisible()),
                menu.getDescription(),
                permissionCodesByMenu.getOrDefault(menu.getMenuCode(), List.of()),
                List.of()
        );
    }

    static final class MenuTreeBuilder {

        private MenuTreeBuilder() {
        }

        static List<MenuNodeDto> buildFullTree(
                List<MenuEntity> menus,
                Map<String, List<String>> permissionCodesByMenu
        ) {
            List<MenuEntity> sorted = menus.stream()
                    .sorted(Comparator.comparingInt(MenuEntity::getSortOrder)
                            .thenComparing(MenuEntity::getMenuCode))
                    .toList();
            Map<String, MenuNodeDto> nodes = new HashMap<>();
            Set<String> codes = sorted.stream().map(MenuEntity::getMenuCode).collect(Collectors.toSet());

            for (MenuEntity menu : sorted) {
                nodes.put(menu.getMenuCode(), new MenuNodeDto(
                        menu.getMenuCode(),
                        menu.getLabel(),
                        menu.getPath(),
                        menu.getParentCode(),
                        menu.getSortOrder(),
                        menu.getMenuType(),
                        menu.getModuleKey(),
                        Boolean.TRUE.equals(menu.getVisible()),
                        menu.getDescription(),
                        permissionCodesByMenu.getOrDefault(menu.getMenuCode(), List.of()),
                        new ArrayList<>()
                ));
            }

            List<MenuNodeDto> roots = new ArrayList<>();
            for (MenuEntity menu : sorted) {
                MenuNodeDto node = nodes.get(menu.getMenuCode());
                if (menu.getParentCode() == null || !codes.contains(menu.getParentCode())) {
                    roots.add(node);
                } else {
                    nodes.get(menu.getParentCode()).children().add(node);
                }
            }
            sortChildrenRecursively(roots);
            return roots;
        }

        private static void sortChildrenRecursively(List<MenuNodeDto> nodes) {
            nodes.sort(Comparator.comparingInt(MenuNodeDto::sortOrder).thenComparing(MenuNodeDto::menuCode));
            for (MenuNodeDto node : nodes) {
                if (node.children() != null && !node.children().isEmpty()) {
                    sortChildrenRecursively(node.children());
                }
            }
        }
    }
}
