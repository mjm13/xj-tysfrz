## Context

- 后端：`UserAdminController` 已有 list/create；`CreateUserRequest` 含 username、password、departmentCode、dataScope
- 前端：顶栏静态配置；`admin:users:read/write` 仅 ADMIN 角色拥有
- org_node 已种子化，无只读 API 供 UI 下拉

## Goals / Non-Goals

**Goals**

- 管理员可在 UI 完成平台用户查询与创建
- 顶栏建立「平台管理」模块范式，供 008–010 复用

**Non-Goals**

- 角色/部门/菜单 CRUD
- 动态菜单
- 平台用户全生命周期（编辑/禁用）

## 领域设计（identity-access 扩展）

- 管理对象：**InteractiveUser**（`platform_user`），与 PersonUID 无关
- 不变量沿用 006：DepartmentRef MUST 存在于 org_node；DataScope 三档；用户名唯一
- 本切片不新增聚合，仅暴露既有应用服务 + 只读 org 查询

## 技术方案（platform-shell + 前端）

### 路由与布局

| 路径 | 布局 | moduleKey | 权限 |
| --- | --- | --- | --- |
| `/admin/users` | ModuleLayout | `platform-admin` | `admin:users:read` |
| POST 创建 | 同上 | | `admin:users:write` |

- `permissions.ts` 增加 `/admin/users` → `platform-admin` 映射；模块 read 仍用 `admin:users:read`（或 `canAccessPath` 对 admin 路径单独判断 `admin:users:read`）

### 顶栏

- 新增 dropdown「平台管理」，子项首期仅 **平台用户** → `/admin/users`
- 可见条件：`auth.canAccessPath('/admin/users')` 或 permissions 含 `admin:users:read`

### 后端 API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/users` | 已有 |
| POST | `/api/admin/users` | 已有 |
| GET | `/api/admin/org-nodes` | **新增** 只读 `{code,name,parentCode}` 列表，需 `admin:users:read` |

### 前端页面

- `AdminUsersView.vue`：ElTable 列表 + ElDialog 创建表单
- `api/admin.ts`：listUsers、createUser、listOrgNodes
- 错误：401/403 由 client 与守卫处理；BizException 展示 message

## 分层裁剪

- 后端：Controller → AppService → Mapper（沿用 access 包，不新建限界上下文）
- 前端：View + api + 复用 auth store

## 测试策略

- 后端：`AdminUsersControllerTest` 或扩展集成测试（list/create/org-nodes + 403）
- 前端：router 守卫、顶栏可见性、admin API client（Vitest）

## Migration Plan

1. 实现 backend org-nodes 只读端点（无 DDL）
2. 前端路由 + 顶栏 + 用户页
3. 测试全绿后 apply 收尾

## Open Questions

- 角色分配：留 008/007 扩展，本切片 create 不绑 role
