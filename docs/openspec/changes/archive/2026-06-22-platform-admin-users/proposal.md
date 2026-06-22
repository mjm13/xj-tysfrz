## Why

006 已落地登录与 RBAC，但平台**操作者账号**只能在数据库或 API 层维护，管理员缺少「平台管理 → 平台用户」界面。同时后续角色/部门/菜单（008–010）需要统一的顶栏入口与路由模式，本切片先打通**平台用户**端到端链路。

**术语**：「平台用户」= InteractiveUser 登录账号；**不是** identity 业务里的自然人（PersonUID）。

## What Changes

- 顶栏新增 **平台管理** dropdown（moduleKey `platform-admin`）
- 子路由 `/admin/users`：**平台用户**列表 + 创建（Element Plus）
- 对接已有 `GET/POST /api/admin/users`；新增只读 `GET /api/admin/org-nodes` 供部门下拉
- 权限：`admin:users:read` / `admin:users:write`（已有）；导航对无权限用户隐藏
- ModuleLayout 侧栏配置 `platform-admin` 模块导航

**不在本 change**：角色管理、部门管理、菜单 DB 化（见需求 008–010）；用户编辑/禁用/角色分配

## Capabilities

### New Capabilities

（无全新 capability；本切片扩展既有能力）

### Modified Capabilities

- `platform-shell`: 顶栏「平台管理」入口；`/admin/*` 路由与 ModuleLayout；按 permissions 显示平台管理
- `identity-access`: 平台用户管理 UI 与 org_node 只读列表 API；明确 InteractiveUser 管理场景

## Impact

| 区域 | 影响 |
| --- | --- |
| `frontend/` | AppTopbar、router、module-nav、permissions 映射；新 `AdminUsersView`、API client |
| `backend/` | 可选 `OrgNodeAdminController` 只读列表；UserAdmin 测试/文档对齐 |
| `docs/requirements/inbox/` | 007 in-change；008–010 已登记后续切片 |
| `docs/capability-map.md` | 发布节点补 `platform-admin` 指针 |

## 来源需求

- `docs/requirements/shipped/007-platform-admin-users.md`
- 后续：`008-platform-admin-roles.md`、`009-platform-admin-departments.md`、`010-platform-admin-menus.md`

## Change 类型

**混合 change**（identity-access 业务 UI + platform-shell 导航壳层）

## In Scope

- 平台管理顶栏 + `/admin/users` 平台用户列表/创建
- org_node 只读 API + 创建表单部门下拉
- 前后端测试与本切片 AC

## Out of Scope

- 角色/部门/菜单管理页
- 用户编辑、禁用、重置密码、分配角色
- 动态菜单从库加载

## Open Questions & Deferred

- 创建用户是否必须同时分配角色？（API 本期不绑 role → Deferred）
- 010 菜单 DB 化后，`module-nav.ts` 退役策略（010 change 处理）
