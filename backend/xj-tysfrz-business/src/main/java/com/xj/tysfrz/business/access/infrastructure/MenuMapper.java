package com.xj.tysfrz.business.access.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    @Select("""
            SELECT p.permission_code
            FROM platform_permission p
            JOIN platform_menu_permission mp ON mp.permission_id = p.id
            WHERE mp.menu_code = #{menuCode}
            ORDER BY p.permission_code
            """)
    List<String> findPermissionCodesByMenuCode(@Param("menuCode") String menuCode);

    @Delete("DELETE FROM platform_menu_permission WHERE menu_code = #{menuCode}")
    void deleteMenuPermissions(@Param("menuCode") String menuCode);

    @Insert("""
            INSERT INTO platform_menu_permission (menu_code, permission_id)
            VALUES (#{menuCode}, #{permissionId})
            """)
    void insertMenuPermission(@Param("menuCode") String menuCode, @Param("permissionId") Long permissionId);
}
