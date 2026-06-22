package com.xj.tysfrz.business.access.service;

import cn.dev33.satoken.stp.StpUtil;
import com.xj.tysfrz.business.access.domain.DataScopeResolver;
import com.xj.tysfrz.business.access.infrastructure.PlatformUserEntity;
import com.xj.tysfrz.business.access.infrastructure.UserRepository;
import com.xj.tysfrz.business.access.web.dto.LoginRequest;
import com.xj.tysfrz.business.access.web.dto.LoginResponse;
import com.xj.tysfrz.business.access.web.dto.UserProfileDto;
import com.xj.tysfrz.common.access.DataScope;
import com.xj.tysfrz.common.access.OperatorContext;
import com.xj.tysfrz.common.exception.BizException;
import com.xj.tysfrz.framework.auth.OperatorSessionSupport;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthAppService {

    private final UserRepository userRepository;
    private final SelfBuiltAuthProvider authProvider;
    private final DataScopeResolver dataScopeResolver;

    public AuthAppService(
            UserRepository userRepository,
            SelfBuiltAuthProvider authProvider,
            DataScopeResolver dataScopeResolver
    ) {
        this.userRepository = userRepository;
        this.authProvider = authProvider;
        this.dataScopeResolver = dataScopeResolver;
    }

    public LoginResponse login(LoginRequest request) {
        AuthProvider.AuthResult result = authProvider.authenticate(request.username(), request.password());
        if (!result.success()) {
            throw new BizException(401, result.failureReason());
        }
        PlatformUserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BizException(401, "用户名或密码错误"));
        OperatorContext operator = buildOperatorContext(user);
        StpUtil.login(operator.platformUserId());
        OperatorSessionSupport.bindOperator(operator);
        return new LoginResponse(StpUtil.getTokenValue(), toProfile(operator));
    }

    public UserProfileDto currentUser(OperatorContext operator) {
        return toProfile(operator);
    }

    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    public OperatorContext buildOperatorContext(PlatformUserEntity user) {
        DataScope dataScope = user.getDataScopeEnum();
        var orgNodes = userRepository.loadOrgNodeRecords();
        Set<String> scoped = dataScopeResolver.resolve(dataScope, user.getDepartmentCode(), orgNodes);
        boolean global = dataScopeResolver.isGlobalScope(dataScope);
        return new OperatorContext(
                user.getPlatformUserId(),
                user.getUsername(),
                new HashSet<>(userRepository.findRoleCodes(user.getId())),
                new HashSet<>(userRepository.findPermissionCodes(user.getId())),
                dataScope,
                user.getDepartmentCode(),
                scoped,
                global
        );
    }

    private UserProfileDto toProfile(OperatorContext operator) {
        return new UserProfileDto(
                operator.platformUserId(),
                operator.username(),
                operator.roles(),
                operator.permissions(),
                operator.dataScope().name(),
                operator.departmentCode(),
                operator.isGlobal(),
                operator.scopedDeptCodes()
        );
    }
}
