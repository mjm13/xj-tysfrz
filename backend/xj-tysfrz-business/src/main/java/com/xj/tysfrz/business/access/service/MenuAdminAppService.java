package com.xj.tysfrz.business.access.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xj.tysfrz.business.access.infrastructure.MenuEntity;
import com.xj.tysfrz.business.access.infrastructure.MenuMapper;
import com.xj.tysfrz.business.access.infrastructure.PermissionEntity;
import com.xj.tysfrz.business.access.infrastructure.PermissionMapper;
import com.xj.tysfrz.business.access.web.dto.CreateMenuRequest;
import com.xj.tysfrz.business.access.web.dto.MenuNodeDto;
import com.xj.tysfrz.business.access.web.dto.UpdateMenuRequest;
import com.xj.tysfrz.common.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuAdminAppService {

    static final String MENU_TYPE_LINK = "LINK";
    static final String MENU_TYPE_GROUP = "GROUP";

    private final MenuMapper menuMapper;
    private final PermissionMapper permissionMapper;

    public MenuAdminAppService(MenuMapper menuMapper, PermissionMapper permissionMapper) {
        this.menuMapper = menuMapper;
        this.permissionMapper = permissionMapper;
    }

    public List<MenuNodeDto> listMenuTree() {
        List<MenuEntity> menus = loadAllMenus();
        return NavigationAppService.MenuTreeBuilder.buildFullTree(menus, loadPermissionCodesByMenu());
    }

    public List<MenuNodeDto> listPermissionTree() {
        return listMenuTree();
    }

    @Transactional
    public MenuNodeDto createMenu(CreateMenuRequest request) {
        if (menuMapper.selectById(request.menuCode()) != null) {
            throw new BizException("菜单 code 已存在: " + request.menuCode());
        }
        validateMenuTypeRules(request.menuType(), request.path(), request.permissionCodes());
        if (request.parentCode() != null) {
            requireMenu(request.parentCode());
            assertNoCycle(request.menuCode(), request.parentCode());
        }
        MenuEntity entity = toEntity(request.menuCode(), request.label(), request.path(),
                request.parentCode(), request.sortOrder(), request.menuType(),
                request.moduleKey(), true, request.description());
        menuMapper.insert(entity);
        replaceMenuPermissions(request.menuCode(), request.permissionCodes());
        return toNode(entity);
    }

    @Transactional
    public MenuNodeDto updateMenu(String menuCode, UpdateMenuRequest request) {
        MenuEntity entity = requireMenu(menuCode);
        validateMenuTypeRules(request.menuType(), request.path(), request.permissionCodes());
        String newParent = request.parentCode();
        if (newParent != null && !newParent.equals(entity.getParentCode())) {
            requireMenu(newParent);
            assertNoCycle(menuCode, newParent);
        }
        entity.setLabel(request.label());
        entity.setPath(request.path());
        entity.setParentCode(newParent);
        entity.setSortOrder(request.sortOrder());
        entity.setMenuType(request.menuType());
        entity.setModuleKey(request.moduleKey());
        entity.setDescription(request.description());
        menuMapper.updateById(entity);
        if (request.permissionCodes() != null) {
            replaceMenuPermissions(menuCode, request.permissionCodes());
        }
        return toNode(entity);
    }

    @Transactional
    public MenuNodeDto updateVisible(String menuCode, boolean visible) {
        MenuEntity entity = requireMenu(menuCode);
        entity.setVisible(visible);
        menuMapper.updateById(entity);
        return toNode(entity);
    }

    Map<String, List<String>> loadPermissionCodesByMenu() {
        Map<String, List<String>> map = new HashMap<>();
        for (MenuEntity menu : loadAllMenus()) {
            map.put(menu.getMenuCode(), menuMapper.findPermissionCodesByMenuCode(menu.getMenuCode()));
        }
        return map;
    }

    private MenuEntity requireMenu(String menuCode) {
        MenuEntity entity = menuMapper.selectById(menuCode);
        if (entity == null) {
            throw new BizException("菜单不存在: " + menuCode);
        }
        return entity;
    }

    private void validateMenuTypeRules(String menuType, String path, List<String> permissionCodes) {
        List<String> codes = permissionCodes == null ? List.of() : permissionCodes;
        if (MENU_TYPE_LINK.equals(menuType)) {
            if (path == null || path.isBlank()) {
                throw new BizException("LINK 菜单必须填写 path");
            }
            if (codes.isEmpty()) {
                throw new BizException("LINK 菜单必须关联至少一个 permission");
            }
        }
        if (MENU_TYPE_GROUP.equals(menuType) && !codes.isEmpty()) {
            throw new BizException("GROUP 菜单不可关联 permission");
        }
        resolvePermissionIds(codes);
    }

    private void replaceMenuPermissions(String menuCode, List<String> permissionCodes) {
        List<String> codes = permissionCodes == null ? List.of() : permissionCodes;
        Map<String, Long> permissionIds = resolvePermissionIds(codes);
        menuMapper.deleteMenuPermissions(menuCode);
        for (String code : new HashSet<>(codes)) {
            menuMapper.insertMenuPermission(menuCode, permissionIds.get(code));
        }
    }

    private Map<String, Long> resolvePermissionIds(List<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return Map.of();
        }
        Map<String, Long> map = permissionMapper.selectList(null).stream()
                .collect(Collectors.toMap(PermissionEntity::getPermissionCode, PermissionEntity::getId));
        for (String code : new HashSet<>(permissionCodes)) {
            if (!map.containsKey(code)) {
                throw new BizException("未知 Permission: " + code);
            }
        }
        return map;
    }

    List<MenuEntity> loadAllMenus() {
        return menuMapper.selectList(new LambdaQueryWrapper<MenuEntity>()
                .orderByAsc(MenuEntity::getSortOrder)
                .orderByAsc(MenuEntity::getMenuCode));
    }

    private void assertNoCycle(String menuCode, String newParentCode) {
        if (menuCode.equals(newParentCode)) {
            throw new BizException("父菜单设置会导致菜单环");
        }
        Map<String, String> parentByCode = new HashMap<>();
        for (MenuEntity menu : loadAllMenus()) {
            parentByCode.put(menu.getMenuCode(), menu.getParentCode());
        }
        String current = newParentCode;
        while (current != null) {
            if (menuCode.equals(current)) {
                throw new BizException("父菜单设置会导致菜单环");
            }
            current = parentByCode.get(current);
        }
    }

    private MenuEntity toEntity(String menuCode, String label, String path, String parentCode,
                                Integer sortOrder, String menuType, String moduleKey,
                                boolean visible, String description) {
        MenuEntity entity = new MenuEntity();
        entity.setMenuCode(menuCode);
        entity.setLabel(label);
        entity.setPath(path);
        entity.setParentCode(parentCode);
        entity.setSortOrder(sortOrder);
        entity.setMenuType(menuType);
        entity.setModuleKey(moduleKey);
        entity.setVisible(visible);
        entity.setDescription(description);
        return entity;
    }

    private MenuNodeDto toNode(MenuEntity entity) {
        return new MenuNodeDto(
                entity.getMenuCode(),
                entity.getLabel(),
                entity.getPath(),
                entity.getParentCode(),
                entity.getSortOrder(),
                entity.getMenuType(),
                entity.getModuleKey(),
                Boolean.TRUE.equals(entity.getVisible()),
                entity.getDescription(),
                menuMapper.findPermissionCodesByMenuCode(entity.getMenuCode()),
                List.of()
        );
    }
}
