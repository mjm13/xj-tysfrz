package com.xj.tysfrz.business.access.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xj.tysfrz.business.access.domain.DataScopeResolver;
import com.xj.tysfrz.common.exception.BizException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final PlatformUserMapper userMapper;
    private final OrgNodeMapper orgNodeMapper;

    public UserRepository(PlatformUserMapper userMapper, OrgNodeMapper orgNodeMapper) {
        this.userMapper = userMapper;
        this.orgNodeMapper = orgNodeMapper;
    }

    public Optional<PlatformUserEntity> findByUsername(String username) {
        return Optional.ofNullable(userMapper.selectOne(new LambdaQueryWrapper<PlatformUserEntity>()
                .eq(PlatformUserEntity::getUsername, username)));
    }

    public Optional<PlatformUserEntity> findByPlatformUserId(String platformUserId) {
        return Optional.ofNullable(userMapper.selectOne(new LambdaQueryWrapper<PlatformUserEntity>()
                .eq(PlatformUserEntity::getPlatformUserId, platformUserId)));
    }

    public List<String> findRoleCodes(Long userId) {
        return userMapper.findRoleCodesByUserId(userId);
    }

    public List<String> findPermissionCodes(Long userId) {
        return userMapper.findPermissionCodesByUserId(userId);
    }

    public void assertDepartmentExists(String departmentCode) {
        if (orgNodeMapper.selectById(departmentCode) == null) {
            throw new BizException("部门 code 不存在: " + departmentCode);
        }
    }

    public List<DataScopeResolver.OrgNodeRecord> loadOrgNodeRecords() {
        return orgNodeMapper.selectList(null).stream()
                .map(n -> new DataScopeResolver.OrgNodeRecord(n.getCode(), n.getParentCode()))
                .toList();
    }
}
