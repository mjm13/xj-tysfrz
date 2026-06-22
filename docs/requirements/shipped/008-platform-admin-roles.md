---
title: 平台管理 — 角色与权限分配（切片 2）
status: shipped
change: platform-admin-roles
openspecChange: docs/openspec/changes/archive/2026-06-22-platform-admin-roles/
owner: team
createdAt: 2026-06-22
shippedAt: 2026-06-22
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

- [x] GIVEN ADMIN 用户 WHEN 打开「平台管理 → 角色管理」THEN 可见 ADMIN/GOVERNANCE 及各自 Permission
- [x] GIVEN ADMIN 用户 WHEN 创建新角色 THEN POST 成功且列表可见
- [x] GIVEN ADMIN 用户 WHEN 修改 GOVERNANCE 的 Permission 并保存 THEN GET 反映新集合
- [x] GIVEN ADMIN 用户 WHEN 尝试修改 ADMIN 角色 Permission THEN 被拒绝
- [x] GIVEN 无 admin:roles:read WHEN 访问 `/admin/roles` THEN 路由拒绝
- [x] GIVEN 已修改角色 Permission WHEN 该角色用户未重新登录 THEN session permissions 不变

# 验收记录

## 人工验收说明（Acceptance Note）— 2026-06-22

- 涉及菜单 / 模块：`platform-admin` → `/admin/roles`（角色管理）
- 改了什么功能：平台 RBAC 角色列表、新建角色、为非 ADMIN 角色编辑 Permission
- 验收场景：
  1. GIVEN admin 登录 WHEN 进入「平台管理 → 角色管理」THEN 可见 ADMIN/GOVERNANCE 及 Permission 数量
  2. GIVEN admin WHEN 新建角色 AUDITOR THEN 列表出现且 Permission 为空
  3. GIVEN admin WHEN 编辑 GOVERNANCE 勾选 identity-org:read 并保存 THEN 列表 Permission 数变化
  4. GIVEN admin WHEN 查看 ADMIN 角色 THEN 无保存按钮 / 后端拒绝 PUT permissions
  5. GIVEN dept_admin 登录 WHEN 访问 `/admin/roles` THEN 重定向首页
- 手动验证步骤：
  - admin 登录 → 角色管理 → 新建 AUDITOR → 编辑 GOVERNANCE 权限 → 保存
  - 查看 ADMIN 仅「查看权限」，不可保存
  - dept_admin 无「角色管理」菜单项
- 自动化覆盖：`RoleAdminIntegrationTest`（8 用例）；`permissions.test.ts`；`admin.test.ts`
- 本次范围外 / Deferred：用户绑角色、角色删除、session 在线刷新 Permission

# 备注

- 前置：`shipped/007-platform-admin-users`
- 后续：009 部门管理、010 菜单 DB 化
