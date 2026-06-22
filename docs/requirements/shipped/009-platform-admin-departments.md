---
title: 平台管理 — 部门（组织节点）管理（切片 3）
status: shipped
change: platform-admin-departments
openspecChange: docs/openspec/changes/archive/2026-06-22-platform-admin-departments/
owner: team
createdAt: 2026-06-22
tier: 🔴
changeType: 业务
dependsOn: shipped/007-platform-admin-users, shipped/008-platform-admin-roles
relatedDomain: docs/domain/established/（org-structure、identity-access）
---

# 背景

`org_node` 表与 SYSU 种子已存在，供 DepartmentRef 与 DataScopeResolver 使用。007 仅有 org_node **只读**列表（供用户创建下拉），**无组织树维护 UI/API**。平台侧组织主数据无法由管理员在界面维护。

# 业务目标

- 「平台管理 → 部门管理」：组织树浏览、节点新增与编辑（org-structure 不变量约束下）
- 新增节点后，平台用户创建表单的部门下拉可见（复用既有只读 API 或同源数据）

# 涉及限界上下文

- `org-structure`（OrgNode 主数据维护）
- `identity-access`（DepartmentRef 引用 org_node；admin 权限种子）
- `platform-shell`（`/admin/departments` 导航）

# 关键领域概念

- **OrgNode**：权威组织树节点（code、name、parent_code、level）
- **DepartmentRef**：平台用户 `department_code` MUST 引用存在的 OrgNode.code

# 范围切分

## 本切片 In Scope

- Flyway V5：`admin:departments:read` / `admin:departments:write`
- Admin API：懒加载子节点查询、创建、更新（name / parent_code）；level 自动派生
- 不变量：code 唯一；parent 必须存在；禁止成环；禁止将 parent 设为自己或子孙
- 有 platform_user 挂靠的节点：**不可删除**（本切片不提供 delete API）
- 前端 `/admin/departments`：ElTree 懒加载 + 新建/编辑对话框
- 权限路由与 platform-admin 导航扩展

## Out of Scope

- 删除 org 节点 API（Deferred）
- OrgMapping / OrgRoster、HR 同步
- identity-org 业务模块组织视图

## Open Questions & Deferred

- 790 节点全树一次性加载 → 本切片 **懒加载**（parentCode 查询子节点）
- 删除仍有用户/子节点的 org 节点 → Deferred（本切片无 delete）

# 验收标准

- [x] GIVEN ADMIN 用户 WHEN 打开「平台管理 → 部门管理」THEN 可见组织树根（SYSU）并可展开子节点
- [x] GIVEN ADMIN 用户 WHEN 在某节点下新增子节点 THEN POST 成功且树刷新可见
- [x] GIVEN ADMIN 用户 WHEN 编辑节点名称 THEN PUT 成功且列表/树反映新名称
- [x] GIVEN 非法 parent_code（不存在或成环）WHEN 创建/更新 THEN 后端拒绝
- [x] GIVEN 无 admin:departments:read WHEN 访问 `/admin/departments` THEN 路由拒绝
- [x] GIVEN 管理员新增 org 节点 WHEN 打开平台用户创建表单部门下拉 THEN 可见新节点 code

# 验收记录

## 人工验收说明（Acceptance Note）— 2026-06-22

- 涉及菜单 / 模块：`platform-admin` → `/admin/departments`（部门管理）
- 改了什么功能：org_node 懒加载树维护（roots/children API + 新建/编辑），Flyway V5 部门管理权限
- 验收场景：
  1. GIVEN admin 登录 WHEN 进入「平台管理 → 部门管理」THEN 可见 SYSU 根节点并可展开 CAT-party 等子节点
  2. GIVEN admin 选中 CAT-party WHEN 新建子节点 TEST-DEPT THEN 树刷新可见新节点
  3. GIVEN admin WHEN 编辑节点名称 THEN 树节点标签更新
  4. GIVEN admin WHEN POST 非法 parent 或 PUT 成环 parent THEN 后端返回业务错误
  5. GIVEN dept_admin WHEN 访问 `/admin/departments` THEN 重定向首页
  6. GIVEN admin 新建 org 节点 WHEN 平台用户页打开部门下拉 THEN 可见新 code
- 手动验证步骤：
  - admin 登录 → 部门管理 → 选中 SYSU/CAT-party → 新建子节点 → 编辑名称
  - 平台用户 → 新建用户 → 部门下拉确认新节点
  - dept_admin 无「部门管理」菜单；直接访问 `/admin/departments` 被重定向
- 自动化覆盖：`OrgNodeAdminIntegrationTest`（8 用例）；`permissions.test.ts`；`admin.test.ts`；`npm run build`
- 本次范围外 / Deferred：删除 org 节点、OrgMapping、identity-org 业务组织视图、移动 parent 的 UI（API 支持 PUT parentCode）

# 备注

- 与 `identity-org`（人员组织业务视图）不同：本切片是**平台管理**侧 org_node 主数据
- OpenSpec 已归档：`docs/openspec/changes/archive/2026-06-22-platform-admin-departments/`
- 后续：010 菜单 DB 化
