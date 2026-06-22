package com.xj.zbpt.business.access.service;

import com.xj.zbpt.business.access.domain.DataScope;
import com.xj.zbpt.business.access.domain.DataScopeResolver;
import com.xj.zbpt.business.access.domain.OperatorContext;
import com.xj.zbpt.business.access.infrastructure.PlatformUserEntity;
import com.xj.zbpt.business.access.infrastructure.UserRepository;
import com.xj.zbpt.business.access.security.JwtTokenService;
import com.xj.zbpt.business.access.web.dto.LoginRequest;
import com.xj.zbpt.business.access.web.dto.LoginResponse;
import com.xj.zbpt.business.access.web.dto.UserProfileDto;
import com.xj.zbpt.common.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthAppService {

    private final UserRepository userRepository;
    private final SelfBuiltAuthProvider authProvider;
    private final DataScopeResolver dataScopeResolver;
    private final JwtTokenService jwtTokenService;

    public AuthAppService(
            UserRepository userRepository,
            SelfBuiltAuthProvider authProvider,
            DataScopeResolver dataScopeResolver,
            JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.authProvider = authProvider;
        this.dataScopeResolver = dataScopeResolver;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        AuthProvider.AuthResult result = authProvider.authenticate(request.username(), request.password());
        if (!result.success()) {
            throw new BizException(401, result.failureReason());
        }
        PlatformUserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BizException(401, "用户名或密码错误"));
        OperatorContext operator = buildOperatorContext(user);
        String token = jwtTokenService.createToken(operator);
        return new LoginResponse(token, toProfile(operator));
    }

    public UserProfileDto currentUser(OperatorContext operator) {
        return toProfile(operator);
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
