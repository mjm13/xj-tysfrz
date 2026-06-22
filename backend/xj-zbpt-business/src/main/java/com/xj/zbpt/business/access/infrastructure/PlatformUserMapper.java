package com.xj.zbpt.business.access.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlatformUserMapper extends BaseMapper<PlatformUserEntity> {

    @Select("""
            SELECT p.permission_code
            FROM platform_permission p
            JOIN platform_role_permission rp ON rp.permission_id = p.id
            JOIN platform_user_role ur ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId}
            """)
    List<String> findPermissionCodesByUserId(Long userId);

    @Select("""
            SELECT r.role_code
            FROM platform_role r
            JOIN platform_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            """)
    List<String> findRoleCodesByUserId(Long userId);
}
