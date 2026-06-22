package com.xj.tysfrz.business.access.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xj.tysfrz.business.access.domain.UserStatus;
import com.xj.tysfrz.business.access.infrastructure.PlatformUserEntity;
import com.xj.tysfrz.business.access.infrastructure.PlatformUserMapper;
import com.xj.tysfrz.business.access.infrastructure.UserRepository;
import com.xj.tysfrz.business.access.web.dto.CreateUserRequest;
import com.xj.tysfrz.business.access.web.dto.UserSummaryDto;
import com.xj.tysfrz.common.exception.BizException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdminAppService {

    private final PlatformUserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminAppService(
            PlatformUserMapper userMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserSummaryDto> listUsers() {
        return userMapper.selectList(null).stream()
                .map(u -> new UserSummaryDto(
                        u.getPlatformUserId(),
                        u.getUsername(),
                        u.getStatus(),
                        u.getDepartmentCode(),
                        u.getDataScopeEnum()))
                .toList();
    }

    public UserSummaryDto createUser(CreateUserRequest request) {
        userRepository.assertDepartmentExists(request.departmentCode());
        if (userMapper.selectCount(new LambdaQueryWrapper<PlatformUserEntity>()
                .eq(PlatformUserEntity::getUsername, request.username())) > 0) {
            throw new BizException("用户名已存在");
        }
        PlatformUserEntity entity = new PlatformUserEntity();
        entity.setPlatformUserId("PU" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        entity.setUsername(request.username());
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setStatus(UserStatus.ACTIVE);
        entity.setDepartmentCode(request.departmentCode());
        entity.setDataScope(request.dataScope());
        userMapper.insert(entity);
        return new UserSummaryDto(
                entity.getPlatformUserId(),
                entity.getUsername(),
                entity.getStatus(),
                entity.getDepartmentCode(),
                entity.getDataScopeEnum()
        );
    }
}
