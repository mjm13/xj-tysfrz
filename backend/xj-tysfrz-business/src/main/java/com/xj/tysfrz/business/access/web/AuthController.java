package com.xj.tysfrz.business.access.web;

import com.xj.tysfrz.business.access.service.AuthAppService;
import com.xj.tysfrz.business.access.web.dto.LoginRequest;
import com.xj.tysfrz.business.access.web.dto.LoginResponse;
import com.xj.tysfrz.business.access.web.dto.UserProfileDto;
import com.xj.tysfrz.common.access.OperatorContext;
import com.xj.tysfrz.common.response.ApiResponse;
import com.xj.tysfrz.framework.auth.CurrentOperator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authAppService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileDto> me(@CurrentOperator OperatorContext operator) {
        return ApiResponse.ok(authAppService.currentUser(operator));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authAppService.logout();
        return ApiResponse.ok(null);
    }
}
