package com.xj.tysfrz.business.access.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xj.tysfrz.business.access.service.MenuAdminAppService;
import com.xj.tysfrz.business.access.service.NavigationAppService;
import com.xj.tysfrz.business.access.web.dto.CreateMenuRequest;
import com.xj.tysfrz.business.access.web.dto.MenuNodeDto;
import com.xj.tysfrz.business.access.web.dto.NavigationDto;
import com.xj.tysfrz.business.access.web.dto.UpdateMenuRequest;
import com.xj.tysfrz.business.access.web.dto.UpdateMenuVisibleRequest;
import com.xj.tysfrz.common.access.OperatorContext;
import com.xj.tysfrz.framework.auth.CurrentOperator;
import com.xj.tysfrz.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuAdminController {

    private final MenuAdminAppService menuAdminAppService;
    private final NavigationAppService navigationAppService;

    public MenuAdminController(MenuAdminAppService menuAdminAppService, NavigationAppService navigationAppService) {
        this.menuAdminAppService = menuAdminAppService;
        this.navigationAppService = navigationAppService;
    }

    @GetMapping("/navigation")
    @SaCheckLogin
    public ApiResponse<NavigationDto> getNavigation(@CurrentOperator OperatorContext operator) {
        return ApiResponse.ok(navigationAppService.getNavigation(new HashSet<>(operator.permissions())));
    }

    @GetMapping("/admin/menus")
    @SaCheckPermission("admin:menus:read")
    public ApiResponse<List<MenuNodeDto>> listMenus() {
        return ApiResponse.ok(menuAdminAppService.listMenuTree());
    }

    @GetMapping("/admin/menus/permission-tree")
    @SaCheckPermission("admin:roles:read")
    public ApiResponse<List<MenuNodeDto>> listPermissionTree() {
        return ApiResponse.ok(menuAdminAppService.listPermissionTree());
    }

    @PostMapping("/admin/menus")
    @SaCheckPermission("admin:menus:write")
    public ApiResponse<MenuNodeDto> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return ApiResponse.ok(menuAdminAppService.createMenu(request));
    }

    @PutMapping("/admin/menus/{menuCode}")
    @SaCheckPermission("admin:menus:write")
    public ApiResponse<MenuNodeDto> updateMenu(
            @PathVariable String menuCode,
            @Valid @RequestBody UpdateMenuRequest request
    ) {
        return ApiResponse.ok(menuAdminAppService.updateMenu(menuCode, request));
    }

    @PatchMapping("/admin/menus/{menuCode}/visible")
    @SaCheckPermission("admin:menus:write")
    public ApiResponse<MenuNodeDto> updateVisible(
            @PathVariable String menuCode,
            @Valid @RequestBody UpdateMenuVisibleRequest request
    ) {
        return ApiResponse.ok(menuAdminAppService.updateVisible(menuCode, request.visible()));
    }
}
