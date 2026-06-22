---
title: 平台管理 — 角色与权限分配（切片 2）
status: in-change
change: platform-admin-roles
openspecChange: docs/openspec/changes/platform-admin-roles/
owner: team
createdAt: 2026-06-22
tier: 🔴
changeType: 业务
dependsOn: shipped/007-platform-admin-users
relatedDomain: docs/domain/established/（identity-access）
---

# 背景

平台 RBAC 已有 `platform_role`、`platform_permission`、`platform_role_permission` 表与种子数据，但**无管理 API 与前端**。管理员无法在 UI 中维护角色及其 Permission 集合。

# 业务目标

- 「平台管理 → 角色管理」：角色列表、创建、编辑 Permission（`module:action`）
- 为后续「用户绑角色」（007 扩展）提供角色主数据

# 涉及限界上下文

- `identity-access`（RoleCatalog）
- `platform-shell`（平台管理导航扩展）

# 关键领域概念

- **Role**：平台角色（如 ADMIN、GOVERNANCE）
- **Permission**：`module:action` 功能授权单元
- **RolePermission**：角色与 Permission 多对多关联

# 范围切分

## 本切片 In Scope

- Flyway V4：`admin:roles:read` / `admin:roles:write`
- API：GET/POST/PUT roles、GET permissions、PUT role permissions
- 前端 `/admin/roles`
- ADMIN 角色权限集 UI 只读；GOVERNANCE 等可编辑
- 权限变更后需**重新登录**生效

## Out of Scope

- 用户-角色分配（后续切片）
- 角色删除 API
- 菜单管理（010）、部门管理（009）
- Permission 运行时新增（仍由 Flyway 维护）

## Open Questions & Deferred

- 删除已绑定用户的角色 → Deferred（本切片不提供 delete）
- 在线刷新 session 权限 → Deferred

# 验收标准

- [ ] GIVEN ADMIN 用户 WHEN 打开「平台管理 → 角色管理」THEN 可见 ADMIN/GOVERNANCE 及各自 Permission
- [ ] GIVEN ADMIN 用户 WHEN 创建新角色 THEN POST 成功且列表可见
- [ ] GIVEN ADMIN 用户 WHEN 修改 GOVERNANCE 的 Permission 并保存 THEN GET 反映新集合
- [ ] GIVEN ADMIN 用户 WHEN 尝试修改 ADMIN 角色 Permission THEN 被拒绝
- [ ] GIVEN 无 admin:roles:read WHEN 访问 `/admin/roles` THEN 路由拒绝
- [ ] GIVEN 已修改角色 Permission WHEN 该角色用户未重新登录 THEN session permissions 不变

# 备注

- OpenSpec：`docs/openspec/changes/platform-admin-roles/`
- 前置：`shipped/007-platform-admin-users`
