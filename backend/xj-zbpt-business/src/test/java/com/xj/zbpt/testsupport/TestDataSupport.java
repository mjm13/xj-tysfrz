package com.xj.zbpt.testsupport;

import com.xj.zbpt.common.access.DataScope;
import com.xj.zbpt.business.access.domain.UserStatus;
import com.xj.zbpt.business.access.infrastructure.PlatformUserEntity;
import com.xj.zbpt.business.access.infrastructure.PlatformUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/**
 * 集成测试业务数据工厂：当 Flyway 种子不足以覆盖用例时，在测试内创建符合领域规则的真实 DB 数据。
 * 禁止用 @MockBean 伪造 Mapper 返回值替代持久化。
 */
public final class TestDataSupport {

    private TestDataSupport() {
    }

    /**
     * 创建并持久化一名 ACTIVE 平台用户（密码经 BCrypt 编码）。
     */
    public static PlatformUserEntity insertActiveUser(
            PlatformUserMapper userMapper,
            PasswordEncoder passwordEncoder,
            String username,
            String rawPassword,
            String departmentCode,
            DataScope dataScope
    ) {
        PlatformUserEntity entity = new PlatformUserEntity();
        entity.setPlatformUserId("PU" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        entity.setUsername(username);
        entity.setPasswordHash(passwordEncoder.encode(rawPassword));
        entity.setStatus(UserStatus.ACTIVE);
        entity.setDepartmentCode(departmentCode);
        entity.setDataScope(dataScope);
        userMapper.insert(entity);
        return entity;
    }
}
