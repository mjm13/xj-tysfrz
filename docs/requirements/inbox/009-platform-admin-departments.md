---
title: 平台管理 — 部门（组织节点）管理（切片 3）
status: inbox
change: platform-admin-departments
owner: team
createdAt: 2026-06-22
tier: 🔴
changeType: 业务
dependsOn: shipped/007-platform-admin-users
relatedDomain: docs/domain/established/（org-structure、identity-access）
---

# 背景

`org_node` 表与 SYSU 种子已存在，供 DepartmentRef 与 DataScopeResolver 使用，但**无组织树维护 UI/API**。平台用户创建时的部门归属无法由业务人员在界面维护组织数据。

# 业务目标

- 「平台管理 → 部门管理」：组织树浏览、节点增删改（在 org-structure 规则约束下）
- 与用户 DepartmentRef、DataScope 派生保持一致

# 范围切分

## 本切片 In Scope（规划，未启动）

- org_node CRUD API（树形）
- 前端 `/admin/departments` 树形管理页
- 不变量：code 唯一、parent 合法、禁止破坏已有用户 departmentRef

## Out of Scope

- OrgMapping / OrgRoster（完整 org-structure change）
- 与 HR 同步

## Open Questions & Deferred

- 是否允许删除仍有用户挂靠的 org 节点？
- 790 节点全树 UI 性能与懒加载策略

# 验收标准（待实现时细化）

- [ ] GIVEN 管理员 WHEN 新增 org 节点 THEN 平台用户创建表单的部门下拉可见新节点

# 备注

- 与 identity-org 业务模块（人员组织视图）不同：本切片是**平台管理**侧的组织主数据维护
