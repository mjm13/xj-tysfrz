---
title: 平台管理 — 平台用户（切片 1）
status: shipped
change: platform-admin-users
openspecChange: docs/openspec/changes/archive/2026-06-22-platform-admin-users/
owner: team
createdAt: 2026-06-22
shippedAt: 2026-06-22
tier: 🔴
changeType: 混合
blocks: 008-platform-admin-roles, 009-platform-admin-departments, 010-platform-admin-menus
relatedDomain: docs/domain/established/（identity-access）
---

# 背景

006 已落地登录、RBAC 与 DataScope，后端已有 `GET/POST /api/admin/users`，但**无前端管理界面**，管理员无法通过 UI 维护**平台操作者账号**（InteractiveUser）。

本需求是「平台管理」四子菜单中的**切片 1**：顶栏入口 + **平台用户**列表/创建。**人员管理**仅指平台登录账号，与「身份管理 → 人员基础身份」（PersonUID 业务数据）严格区分。

# 业务目标

- 顶栏新增 **平台管理** 模块入口（moduleKey: `platform-admin`）
- 提供 **平台用户** 管理页：列表、创建（对接已有 API）
- 无 `admin:users:read` 的用户不可见入口且不可访问

# 用例 / 用户故事

1. 作为系统管理员，我希望在「平台管理 → 平台用户」查看所有平台账号，以便运维与审计。
2. 作为系统管理员，我希望创建新平台用户并指定部门与 DataScope，以便分配职责范围。
3. 作为治理岗用户，我不应看到平台管理入口（无 admin 权限）。

# 涉及限界上下文

- `identity-access`（平台用户 CRUD API，已部分存在）
- `org-structure`（创建用户时 departmentRef 校验，只读 org_node 下拉）
- `platform-shell`（顶栏、路由、ModuleLayout 侧栏）

# 关键领域概念

- **平台用户（InteractiveUser）**：登录平台的操作者；本切片管理对象，**不是** PersonUID 自然人。
- **DepartmentRef**：用户所属 `org_node.code`。
- **DataScope**：GLOBAL / OWN_DEPT / OWN_DEPT_AND_SUB。

# 范围切分

## 本切片 In Scope

- 顶栏「平台管理」dropdown；子菜单 **平台用户** → `/admin/users`
- 平台用户列表页、创建对话框/表单（username、password、departmentCode、dataScope）
- 对接 `GET/POST /api/admin/users`；只读 `GET /api/admin/org-nodes`（供部门下拉，若 slice 内实现）
- 权限：`admin:users:read` / `admin:users:write`；路由与导航 ACL
- 前端/后端测试覆盖本切片 AC

## Out of Scope

- 角色管理、部门管理、菜单管理页面（见 008/009/010）
- 平台用户编辑/禁用/重置密码/角色分配（后续切片）
- 动态菜单从库加载（见 010）

## Open Questions & Deferred

- 创建用户时是否必须分配角色？（本期 create API 未绑 role，Deferred）
- 部门下拉是否分页？（org 种子 790 节点，本期可全量只读列表或 top-N，实现期定）

# 验收标准

- [x] GIVEN 管理员已登录 WHEN 打开顶栏 THEN 可见「平台管理」且可进入「平台用户」
- [x] GIVEN 无 `admin:users:read` WHEN 访问 `/admin/users` THEN 被守卫拒绝或重定向
- [x] GIVEN 管理员 WHEN 打开平台用户页 THEN 展示用户列表（含 username、部门、DataScope、status）
- [x] GIVEN 管理员 WHEN 创建合法用户 THEN 调用 POST `/api/admin/users` 成功且列表刷新
- [x] GIVEN 非法 departmentCode WHEN 创建用户 THEN 后端拒绝且前端展示错误
- [x] GIVEN 治理岗用户 WHEN 浏览顶栏 THEN 不可见「平台管理」

# 验收记录

## 人工验收说明（Acceptance Note）— 2026-06-22

- 涉及菜单 / 模块：`platform-admin` → 顶栏「平台管理」→ `/admin/users`（平台用户）
- 改了什么功能：平台操作者账号（InteractiveUser）列表与新建，对接已有 RBAC API
- 验收场景（人可复现）：
  1. GIVEN 以 `admin` / `admin123` 登录 WHEN 悬停顶栏「平台管理」THEN 可见「平台用户」并可进入
  2. GIVEN 管理员在平台用户页 WHEN 点击「新建用户」并填写合法信息 THEN 列表出现新用户
  3. GIVEN 以 `dept_admin` / `admin123` 登录 WHEN 浏览顶栏 THEN 不可见「平台管理」；直接访问 `/admin/users` 重定向首页
  4. GIVEN 创建用户时选择不存在的部门 code WHEN 提交 THEN 前端展示「部门 code 不存在: …」
- 手动验证步骤：
  - 启动后端 + 前端 → 登录 admin → 平台管理 → 平台用户 → 确认列表含 admin/dept_admin
  - 新建用户：username=`test_user`、password=`Passw0rd!`、部门选「党群部门 (CAT-party)」、数据范围 OWN_DEPT → 创建成功
  - 登出后用 dept_admin 登录，确认无「平台管理」菜单
- 自动化覆盖：
  - 后端 `UserAdminIntegrationTest`（list/create/org-nodes + 403）
  - 前端 `permissions.test.ts`、`admin.test.ts`
  - 命令：`cd backend && .\mvnw.cmd -pl xj-tysfrz-business -am test`；`cd frontend && npm run test && npm run build`
- 本次范围外 / Deferred：角色/部门/菜单管理（008–010）；用户编辑/禁用/绑角色；菜单 DB 化（010）；创建时绑角色（Deferred）

# 备注

- 后续切片：`docs/requirements/inbox/008-platform-admin-roles.md`、`009-platform-admin-departments.md`、`010-platform-admin-menus.md`
- Demo 参考：无专用 demo，UI 对齐现有 ModuleLayout + Element Plus 风格
