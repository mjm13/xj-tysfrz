## Why

007/008 已建立平台管理壳层与用户、角色维护，但 `org_node` 组织主数据仍只能改库或依赖 Flyway 种子。管理员无法在 UI 维护组织树，平台用户 `department_code` 无法随组织变更扩展。

## What Changes

- Flyway V5：`admin:departments:read` / `admin:departments:write`（ADMIN 角色）
- 扩展 Admin org API：懒加载子节点、创建、更新 OrgNode
- 前端 `/admin/departments`：ElTree 懒加载 + 新建/编辑
- 平台管理导航增加「部门管理」

**不在本 change**：org 节点删除、OrgMapping、HR 同步、identity-org 业务页

## Capabilities

### Modified Capabilities

- `org-structure`：平台侧 org_node 维护 API 与不变量
- `platform-shell`：`/admin/departments` 路由与导航
- `identity-access`：admin:departments 权限种子（Flyway）

## Impact

| 区域 | 影响 |
| --- | --- |
| `db/migration` | V5 admin:departments 权限 |
| `backend/access` 或 `org` 包 | OrgNodeAdmin 扩展 CRUD |
| `frontend` | AdminDepartmentsView、permissions、admin API |
| `009-platform-admin-departments.md` | in-change |

## 来源需求

- `docs/requirements/inbox/009-platform-admin-departments.md`

## Change 类型

**业务 change**（org-structure OrgNode 平台管理维护）

## In Scope

- 懒加载：`GET /api/admin/org-nodes?parentCode=`（无参或空=根层）
- `POST /api/admin/org-nodes` 创建；`PUT /api/admin/org-nodes/{code}` 更新 name/parent
- level 由 parent 派生；禁止成环、parent 必须存在
- 无 delete API
- `/admin/departments` UI + 测试

## Out of Scope

- 删除 org 节点
- OrgMapping / OrgRoster
- 790 节点全量一次加载（用懒加载）

## Open Questions & Deferred

- 删除有用户挂靠节点 → Deferred（无 delete API）
- 移动节点导致 DataScope 语义变化 → 接受（org 权威以 org_node 为准）；不自动改用户 department
