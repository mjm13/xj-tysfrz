## Context

- `org_node`：code PK，parent_code，level；生产 V2 种子约 790 节点
- 已有 `GET /api/admin/org-nodes` 全量列表（admin:users:read）供用户表单——本切片可保留；部门管理用 **懒加载** 新查询参数或独立 tree 端点
- `UserRepository.assertDepartmentExists` 校验 DepartmentRef
- 平台 admin 权限模式：V4 roles；本 change V5 departments

## Goals / Non-Goals

**Goals**

- 管理员在 UI 浏览组织树（懒加载）并新增/编辑节点
- 新节点可被平台用户创建表单选用

**Non-Goals**

- 删除节点
- identity-org 业务组织视图
- OrgMapping

## 领域设计（org-structure）

- 管理对象：**OrgNode**
- 不变量：
  - code 全局唯一
  - parent_code 为 NULL（根）或引用已存在节点
  - 更新 parent 不得成环（不可设为自己或子孙）
  - 有 `platform_user.department_code` 引用的节点本切片不删（无 delete）
- level：创建/移动时 `parent.level + 1`；根 level=1

## 技术方案

### Flyway V5

```sql
INSERT platform_permission (15, 'admin:departments:read', ...),
                         (16, 'admin:departments:write', ...);
GRANT to ADMIN role_id=1
```

### API

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/api/admin/org-nodes?parentCode=` | departments:read **或** users:read | 无 parentCode：根节点；有值：直接子节点 |
| GET | `/api/admin/org-nodes`（无参，现有） | admin:users:read | 保留全量供用户下拉（或改为可选 query） |
| POST | `/api/admin/org-nodes` | admin:departments:write | `{code,name,parentCode?}` |
| PUT | `/api/admin/org-nodes/{code}` | admin:departments:write | `{name,parentCode?}` |

**实现注意**：统一 `OrgNodeAdminController`；GET 带 `parentCode` 时用 departments:read；全量 list 保持 users:read（007 兼容）。

或：GET children 单独路径 `/api/admin/org-nodes/children?parentCode=` 需 departments:read。

设计选择：**单一路径** `GET /api/admin/org-nodes`：
- 无 query → 全量（需 admin:users:read OR admin:departments:read？）→ 部门页用 `?parentCode=X` 懒加载需 departments:read
- `?parentCode=SYSU` → 子节点列表（departments:read）
- `?parentCode=` 或省略且 `roots=true` → 根节点

简化：
- `GET /api/admin/org-nodes/roots` + `GET /api/admin/org-nodes/children?parentCode=` — departments:read
- 保留 `GET /api/admin/org-nodes` 全量 — users:read（007 不变）

### 前端

- `/admin/departments`，`AdminDepartmentsView.vue`，ElTree lazy load
- `permissions.ts`：`/admin/departments` → `admin:departments:read`；PLATFORM_ADMIN 列表加 departments:read
- `module-nav` + AppTopbar「部门管理」

### 成环检测

- 更新 parent 时：自 parent 向上遍历至根，不得遇到 self；或 BFS 从 node 向下不得包含 new parent

### 测试

- `OrgNodeAdminIntegrationTest`：roots/children/create/update/403/ cycle reject
- 前端 permissions + admin API tests

## 分层裁剪

- `OrgNodeAdminAppService` 扩展；Controller 扩展；复用 OrgNodeMapper

## Open Questions

- 全量 GET 与懒加载并存：是（不同权限场景）
