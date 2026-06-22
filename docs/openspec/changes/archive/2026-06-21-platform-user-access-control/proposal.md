## Why

当前平台登录为前端 **Mock**（`identity-access-mock`），无真实账号、角色、权限与部门数据范围。后续所有业务 change（data-ingestion、identity-master、data-query 等）均依赖统一的 operator 上下文与 RBAC/DataScope 横切能力。若不先落地 `identity-access`，权限模型会在各上下文重复实现或定错方向，导致全局返工。

## What Changes

- 落地 **`identity-access`** 真实访问控制上下文：自建账号认证、JWT 会话、RBAC、DataScope
- 后端 Spring Security + JWT；Flyway 建表（用户/角色/权限/组织种子）
- 前端登录/守卫/导航从 Mock 切换为真实 API
- **最小 org_node 种子表**（E1 已确认）：从 SYSU_ORG mock 导入，供 DepartmentRef 校验与 DataScopeResolver
- 领域模型预留 `Principal` / `ServiceAccount`，本期仅实现 `InteractiveUser`
- **BREAKING**：`identity-access-mock` 退役；所有受保护路由需有效 Token

**不在本 change 范围**：SSO 对接、ServiceAccount 实现、各业务上下文的完整查询页、org-structure 完整功能（OrgMapping/Roster）

## Capabilities

### New Capabilities

- `identity-access`: 平台访问控制——InteractiveUser、Role/Permission、DataScope、JWT 认证、OperatorContext、DataScopeResolver

### Modified Capabilities

- `platform-shell`: 登录对接后端 API；路由守卫校验 Token；按 permissions 过滤导航
- `org-structure`: 新增最小 org_node 种子要求（供 identity-access 引用，非完整 org change）
- `identity-access-mock`: 全部 Requirement 移除（由 identity-access 替代）

## Impact

| 区域 | 影响 |
| --- | --- |
| `backend/` | 新增 Spring Security、JWT、auth/user/role API、Flyway 迁移、DataScopeResolver |
| `frontend/` | auth store 重构、LoginView 调 API、路由守卫 + 导航 ACL |
| `docs/domain/developing/*` | identity-access 三件套（explore 已写，propose 对齐） |
| `docs/openspec/specs/*` | 新增 identity-access；delta platform-shell、org-structure；移除 identity-access-mock |
| 后续 change | data-ingestion、data-query 等消费 OperatorContext / ScopedDeptSet |

## 来源需求

- `docs/requirements/inbox/006-platform-user-access-control.md`
- `docs/domain/developing/`（explore 产出，2026-06-20）

## Change 类型

**业务 change**（权限模型 + 数据范围，横切多上下文）
