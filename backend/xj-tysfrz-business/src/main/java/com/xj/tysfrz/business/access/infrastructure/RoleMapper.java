package com.xj.tysfrz.business.access.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("""
            SELECT p.permission_code
            FROM platform_permission p
            JOIN platform_role_permission rp ON rp.permission_id = p.id
            WHERE rp.role_id = #{roleId}
            ORDER BY p.permission_code
            """)
    List<String> findPermissionCodesByRoleId(@Param("roleId") Long roleId);

    @Delete("DELETE FROM platform_role_permission WHERE role_id = #{roleId}")
    void deleteRolePermissions(@Param("roleId") Long roleId);

    @Insert("""
            INSERT INTO platform_role_permission (role_id, permission_id)
            VALUES (#{roleId}, #{permissionId})
            """)
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
