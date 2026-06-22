---
title: 平台管理 — 角色与权限分配（切片 2）
status: inbox
change: platform-admin-roles
owner: team
createdAt: 2026-06-22
tier: 🔴
changeType: 业务
dependsOn: shipped/007-platform-admin-users
relatedDomain: docs/domain/established/（identity-access）
---

# 背景

平台 RBAC 已有 `platform_role`、`platform_permission`、`platform_role_permission` 表与种子数据，但**无管理 API 与前端**。管理员无法在 UI 中维护角色及其权限集合。

# 业务目标

- 「平台管理 → 角色管理」：角色列表、创建/编辑、为角色分配 Permission（`module:action`）
- 与用户管理的角色分配联动（后续可与 007 扩展合并）

# 范围切分

## 本切片 In Scope（规划，未启动）

- CRUD API：`/api/admin/roles`、角色-权限绑定
- 前端 `/admin/roles` 页面
- 权限码 `admin:roles:read` / `admin:roles:write`（Flyway 新增）

## Out of Scope

- 菜单管理（010）
- 部门树编辑（009）
- 与外部权限系统对账（permission-reconciliation）

## Open Questions & Deferred

- 是否允许删除已绑定用户的角色？
- 系统内置 ADMIN 角色是否不可删/不可降权？

# 验收标准（待实现时细化）

- [ ] GIVEN 管理员 WHEN 维护角色权限 THEN 变更后登录用户 permissions 生效（需重新登录或 session 刷新策略）

# 备注

- 阻塞于 007 平台管理壳层与导航模式确立
