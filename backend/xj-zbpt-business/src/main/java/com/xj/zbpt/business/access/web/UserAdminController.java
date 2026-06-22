package com.xj.zbpt.business.access.web;

import com.xj.zbpt.business.access.service.UserAdminAppService;
import com.xj.zbpt.business.access.web.dto.CreateUserRequest;
import com.xj.zbpt.business.access.web.dto.UserSummaryDto;
import com.xj.zbpt.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final UserAdminAppService userAdminAppService;

    public UserAdminController(UserAdminAppService userAdminAppService) {
        this.userAdminAppService = userAdminAppService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin:users:read')")
    public ApiResponse<List<UserSummaryDto>> listUsers() {
        return ApiResponse.ok(userAdminAppService.listUsers());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:users:write')")
    public ApiResponse<UserSummaryDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.ok(userAdminAppService.createUser(request));
    }
}
