package com.xj.zbpt.business.access.infrastructure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xj.zbpt.business.access.domain.DataScope;
import com.xj.zbpt.business.access.domain.UserStatus;

@TableName("platform_user")
public class PlatformUserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String platformUserId;
    private String username;
    private String passwordHash;
    private String status;
    private String departmentCode;
    private String dataScope;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserStatus getStatusEnum() {
        return UserStatus.valueOf(status);
    }

    public void setStatus(UserStatus status) {
        this.status = status.name();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public DataScope getDataScopeEnum() {
        return DataScope.valueOf(dataScope);
    }

    public void setDataScope(DataScope dataScope) {
        this.dataScope = dataScope.name();
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }
}
