## ADDED Requirements

### Requirement: 导航菜单权限种子

系统 SHALL 通过 Flyway 提供 `admin:menus:read` 与 `admin:menus:write`，并授予 ADMIN 角色。

#### Scenario: 管理员具备菜单管理权限

- **WHEN** 数据库迁移至 V6 完成
- **THEN** ADMIN MUST 拥有 admin:menus:read 与 admin:menus:write

### Requirement: 导航菜单与菜单权限子表

系统 SHALL 持久化 `platform_menu` 菜单树，并通过 **`platform_menu_permission` 子表**关联多个 Permission（一对多）；MUST NOT 在 menu 表上用单字段存 permission。

#### Scenario: 创建 LINK 菜单并绑定多个 permission

- **WHEN** 管理员 POST 合法 menu 且 `permissionCodes` 含 ≥1 个已注册 permission
- **THEN** MUST 持久化 menu 及子表多条关联

#### Scenario: LINK 无 permission 拒绝

- **WHEN** LINK 菜单 POST/PUT 时 permissionCodes 为空
- **THEN** MUST 返回业务错误并拒绝保存

#### Scenario: 无效 permission 拒绝

- **WHEN** permissionCodes 含未注册 code
- **THEN** MUST 返回业务错误并拒绝保存

### Requirement: 运行时导航 API

系统 SHALL 提供 `GET /api/navigation`；LINK 可见 ⟺ visible=true 且用户 permissions 与菜单关联 permissions **至少交集一个**（OR）。

#### Scenario: 拥有任一关联 permission 可见

- **WHEN** 菜单关联 `admin:users:read` 与 `admin:users:write`，用户仅有 write
- **THEN** navigation MUST 含该菜单

#### Scenario: 无任何关联 permission 不可见

- **WHEN** GOVERNANCE 用户无任何 identity-org 关联 permission
- **THEN** navigation MUST NOT 含绑定 identity-org 权限的 LINK

### Requirement: 角色 Permission 菜单化配置

角色 Permission 编辑 UI SHALL 按菜单树展示；**每个菜单节点下展示其关联的多个 permission** 供勾选（非扁平全量列表）。

#### Scenario: 菜单下多 permission 勾选

- **WHEN** 管理员打开角色 Permission 抽屉
- **THEN** MUST 见菜单分组，且同一 LINK 下可勾选多个 permission（如 read + write）
