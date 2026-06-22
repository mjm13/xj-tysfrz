## ADDED Requirements

### Requirement: 组织节点管理权限种子

系统 SHALL 通过 Flyway 提供 `admin:departments:read` 与 `admin:departments:write`，并授予 ADMIN 角色。

#### Scenario: 管理员具备部门管理权限

- **WHEN** 数据库迁移至 V5 完成
- **THEN** ADMIN MUST 拥有 admin:departments:read 与 admin:departments:write
