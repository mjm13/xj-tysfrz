## Context

- 现有：`platform_permission`（module:action）、`PATH_PERMISSION_MAP` / `module-nav.ts` 静态映射
- 角色 UI：`GET /api/admin/permissions` 扁平列表 + checkbox
- 009 已建立 platform-admin 四子菜单模式；本切片为第 5 项「菜单管理」并完成导航 DB 化

## Goals / Non-Goals

**Goals**

- 菜单为导航权威源；**Permission 为菜单子表**（一对多），非菜单字段单值
- 管理员在「菜单管理」维护树及每菜单关联的 permission 集合
- 角色管理按**菜单树 → 其下多个 permission** 勾选
- 登录用户只见其有任一关联 permission 的 LINK 菜单（OR 语义）

**Non-Goals**

- 改造业务占位页
- 菜单改 permission 不自动改 role 关联（role 仍显式维护 platform_role_permission）

## 领域设计（identity-access + platform-shell）

### NavigationMenu（platform_menu）

| 字段 | 说明 |
| --- | --- |
| menu_code | PK，如 `nav.platform.users` |
| label | 展示名 |
| path | 路由；GROUP 可为空 |
| parent_code | 父节点；空=顶栏根或模块根 |
| sort_order | 同级排序 |
| menu_type | `GROUP` \| `LINK` |
| module_key | ModuleLayout 上下文 |
| visible | 是否启用 |
| description | 可选，顶栏 dropdown 副标题 |

**不含** `permission_code` 字段 — 权限走子表。

### MenuPermission（platform_menu_permission）— 菜单权限子表

| 字段 | 说明 |
| --- | --- |
| menu_code | FK → platform_menu.menu_code |
| permission_id | FK → platform_permission.id |

- 联合主键 `(menu_code, permission_id)`
- 与 `platform_role_permission` 对称：菜单侧「本入口涉及哪些 permission」

**不变量**

- LINK：MUST 有 path，且 MUST 在子表中有 **≥1** 条 permission 关联
- GROUP：无 path；子表通常为空；可见性由子孙推导
- 子表 permission_id MUST 引用已存在的 platform_permission
- 同一 menu 不可重复关联同一 permission
- 树结构禁止成环

### 运行时过滤

```
GET /api/navigation
→ 加载 visible=true 的全树（含各 menu 的 permissionCodes[]）
→ LINK 保留 iff ∃ 关联 permission p 且 user.permissions ∋ p（OR）
→ GROUP 保留 iff 有保留子节点
```

**路由守卫：** direct URL 仍按 path 所需 read permission 校验（`permissions.ts` 兜底）；菜单可见与路由准入可因多 permission 而略有差异，以关联 permission 集合为准。

### 角色 Permission UI（菜单 → 多权限）

- `GET /api/admin/menus/permission-tree` 返回菜单树，每个 LINK/GROUP 下挂 `permissions[]`（来自子表）
- AdminRolesView：按菜单树折叠展示；**每个菜单节点下勾选其关联的多个 permission**
- 保存仍 `PUT /api/admin/roles/{code}/permissions`（扁平 permissionCodes，去重）

### 菜单管理 UI

- 编辑 LINK：维护 `permissionCodes[]` 多选（写入子表全量替换）
- 示例：「平台用户」菜单关联 `admin:users:read` + `admin:users:write`

## 技术方案

### Flyway V6

```sql
CREATE TABLE platform_menu (...);  -- 无 permission_code 列
CREATE TABLE platform_menu_permission (
  menu_code VARCHAR(...) NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (menu_code, permission_id),
  FOREIGN KEY ...
);
INSERT admin:menus:read/write → ADMIN;
INSERT 菜单种子 + 各 LINK 的 platform_menu_permission 种子
```

### API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/navigation` | 当前用户可见菜单树（含 permissionCodes 供调试，前端路由 guard 仍用 path 规则） |
| GET | `/api/admin/menus` | 全量菜单树 + 每节点 permissionCodes[] |
| GET | `/api/admin/menus/permission-tree` | 角色抽屉：菜单树 + permissions[] |
| POST/PUT | `/api/admin/menus` | body 含 `permissionCodes[]`（LINK 必填 ≥1） |
| PATCH | `/api/admin/menus/{menuCode}/visible` | 显示/隐藏 |

### 前端

- `/admin/menus` — 编辑菜单 + **多选 permission**
- AdminRolesView — 菜单树 + 每节点下多个 checkbox
- AppTopbar / ModuleSidebar — navigation store

### 测试

- 子表：一菜单多 permission CRUD；无效 permission 拒绝
- Navigation：用户仅有 write 无 read 时，若菜单只绑 write 仍可见（OR）

## 分层裁剪

- `MenuAdminAppService` 维护 menu + menu_permission 同事务；`NavigationAppService` 过滤

## Open Questions

- 种子：每个 LINK 绑 read；平台管理类 LINK 额外绑 write（与现网能力一致）
