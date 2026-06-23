---
title: 平台管理 — 菜单管理（DB 驱动导航，切片 4）
status: shipped
change: platform-admin-menus
openspecChange: docs/openspec/changes/archive/2026-06-23-platform-admin-menus/
owner: team
createdAt: 2026-06-22
tier: 🔴
changeType: 混合
dependsOn: shipped/007-platform-admin-users, shipped/008-platform-admin-roles, shipped/009-platform-admin-departments
relatedDomain: docs/domain/established/（identity-access、platform-shell）
---

# 背景

当前顶栏与侧栏写死；角色 Permission 为扁平 checkbox。需：**菜单管理** + **权限为菜单子表（一对多）**；角色/菜单配置均按「**菜单 → 多个 permission**」树形展示。

# 业务目标

- 「平台管理 → 菜单管理」：维护菜单树 + 每菜单关联的 permission 集合（子表）
- `GET /api/navigation` 驱动顶栏/侧栏；用户拥有菜单**任一**关联 permission 则可见
- 角色 Permission 抽屉：菜单树 → 其下多个 permission 勾选

# 关键领域概念

- **NavigationMenu** + **MenuPermission（子表）**：一菜单多 permission
- **Permission**：仍用 `platform_permission`；角色授权存储不变

# 范围切分

## 本切片 In Scope

- Flyway V6：`platform_menu` + **`platform_menu_permission`** + admin:menus 权限 + 种子
- 不变量：LINK 有 path 且子表 ≥1 permission；OR 可见性；禁止成环
- Admin CRUD（body 含 permissionCodes[]）、navigation API、角色/菜单 UI

## Out of Scope

- 多租户、外链、物理删除

## Open Questions & Deferred

- GROUP 不绑子表 permission
- session 热更新 → Deferred

# 验收标准

- [x] GIVEN ADMIN WHEN 编辑 LINK 菜单 THEN 可绑定多个 permission（如 read+write）且子表持久化
- [x] GIVEN 菜单绑 read+write WHEN 用户仅有 write THEN navigation 仍显示该菜单（OR）
- [x] GIVEN visible=false WHEN 普通用户登录 THEN 不显示且 URL 仍被守卫拦截
- [x] GIVEN ADMIN WHEN 编辑角色 Permission THEN 按菜单树展示其下多个 permission 勾选
- [x] GIVEN 无 admin:menus:read WHEN 访问 `/admin/menus` THEN 路由拒绝

# 验收记录

## 人工验收说明（Acceptance Note）— 2026-06-23

- 涉及菜单 / 模块：`platform-admin` → `/admin/menus`（菜单管理）；顶栏/侧栏由 `GET /api/navigation` 驱动
- 改了什么功能：DB 驱动导航（platform_menu + platform_menu_permission 子表）；菜单 CRUD/显隐；角色权限抽屉按菜单树多 permission 勾选；修复 CORS OPTIONS 预检被 Sa-Token 拦截
- 验收场景：
  1. GIVEN admin 登录 WHEN POST LINK 菜单绑定 admin:users:read + write THEN 子表持久化两条关联
  2. GIVEN 菜单绑 read+write WHEN 用户仅有 write THEN `/api/navigation` 仍含该菜单（OR）
  3. GIVEN admin WHEN PATCH 隐藏 nav.top.identity.org THEN navigation 不含该菜单；direct URL 仍被路由守卫拦截
  4. GIVEN admin WHEN 打开角色 Permission 抽屉 THEN 见菜单树分组及每 LINK 下多个 permission 勾选
  5. GIVEN 无 admin:menus:read WHEN 访问 `/admin/menus` THEN 重定向首页
- 手动验证步骤：
  - admin 登录 → 确认顶栏/平台管理侧栏来自 API（含「菜单管理」）
  - 菜单管理 → 选中节点 → 编辑/隐藏 → 刷新顶栏验证顺序与可见性
  - 角色管理 → 编辑 GOVERNANCE → 按菜单树勾选 permission → 保存后重新登录验证
  - dept_admin 无平台管理/菜单管理入口；直接访问 `/admin/menus` 被重定向
- 自动化覆盖：`MenuAdminIntegrationTest`（6 用例）、`NavigationIntegrationTest`（3 用例）；`backend/mvnw -pl xj-tysfrz-business -am test` 46 passed；`npm test` 26 passed；`npm run build`
- 本次范围外 / Deferred：外链菜单、多租户、删除菜单 API、session 权限热更新；V6 种子仅顶栏 + platform-admin 侧栏（其他 module 侧栏可后续 Admin UI 扩展）

# 备注

- OpenSpec 已归档：`docs/openspec/changes/archive/2026-06-23-platform-admin-menus/`
