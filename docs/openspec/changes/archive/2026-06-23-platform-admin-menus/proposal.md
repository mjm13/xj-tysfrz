## Why

007–009 已完成平台用户/角色/部门管理，但顶栏与侧栏仍写死在 `AppTopbar.vue`、`module-nav.ts`；角色 Permission 分配为**扁平 checkbox 列表**，与业务菜单结构脱节。运营无法在不发版的情况下调整导航，管理员也难以按「菜单 → 所需权限」理解授权关系。

## What Changes

- 新表 `platform_menu`（菜单树）+ **`platform_menu_permission`（菜单权限子表，一对多）**
- Flyway V6：`admin:menus:read/write` + 从现有静态导航导入种子
- Admin API + `/admin/menus`：维护菜单树及每菜单关联的多个 permission
- 运行时 `GET /api/navigation`：用户拥有菜单**任一**关联 permission 则可见（OR）
- 前端顶栏/侧栏改消费 navigation API
- **角色 Permission 抽屉：菜单树 → 其下多个 permission 勾选**

**不在本 change**：业务页内容、多租户、外链菜单

## Capabilities

### Modified Capabilities

- `identity-access`：`platform_menu` + `platform_menu_permission`；admin:menus 权限种子
- `platform-shell`：DB 驱动导航；`/admin/menus`；角色/菜单 UI 菜单化

## Impact

| 区域 | 影响 |
| --- | --- |
| `db/migration` | V6 两表 + admin:menus 权限 + 导航种子 |
| `backend/access` | MenuAdmin + NavigationAppService |
| `frontend` | AdminMenusView（多 permission）、AdminRolesView 抽屉、navigation store |
| `010-platform-admin-menus.md` | in-change |

## 来源需求

- `docs/requirements/inbox/010-platform-admin-menus.md`
- 用户补充：权限为菜单**子表**，一菜单可关联**多个** permission；配置按「菜单 → 权限」模式

## Change 类型

**混合 change**

## In Scope

- `platform_menu` + `platform_menu_permission(menu_code, permission_id)` 子表
- 不变量：LINK 必有 path 且子表 ≥1 permission；permission 必须存在；禁止成环
- 运行时可见：用户 permissions ∩ 菜单关联 permissions ≠ ∅（OR）
- Admin CRUD、`/admin/menus`、navigation API、角色抽屉菜单树化
- Flyway 种子（含每 LINK 的 permission 关联，如 users 绑 read+write）

## Out of Scope

- 多租户、外链、物理删除菜单

## Open Questions & Deferred

- GROUP 是否允许绑 permission？→ **否**；仅 LINK 使用子表
- 菜单多 permission 的 OR 语义 → **已采纳**（任一即可见）
- session 热更新 → Deferred

## Approval Gate

- **新表 `platform_menu` + `platform_menu_permission`** — schema 见 design.md；**apply 前需用户确认**
