## ADDED Requirements

### Requirement: 平台管理菜单子菜单

「平台管理」SHALL 提供「菜单管理」→ `/admin/menus`；需 `admin:menus:read`。

#### Scenario: 有菜单读权限可见

- **WHEN** 用户 permissions 含 `admin:menus:read`
- **THEN** MUST 显示「菜单管理」入口

### Requirement: 菜单管理路由守卫

无 `admin:menus:read` 访问 `/admin/menus` MUST 被拒绝。

#### Scenario: 越权 URL

- **WHEN** 无 admin:menus:read 的用户访问 `/admin/menus`
- **THEN** MUST 重定向至 `/` 或等效拒绝

### Requirement: DB 驱动顶栏与侧栏

前端顶栏 dropdown 与 ModuleLayout 侧栏 SHALL 从 `GET /api/navigation` 加载；MUST NOT 以 `module-nav.ts` / `AppTopbar` 硬编码为生产路径唯一真相。

#### Scenario: 登录后加载导航

- **WHEN** 用户登录成功或 restoreSession 成功
- **THEN** MUST 请求 `/api/navigation` 并渲染顶栏/侧栏

#### Scenario: 调整排序后一致

- **WHEN** 管理员修改 platform_menu.sort_order 并用户刷新
- **THEN** 导航顺序 MUST 与库一致
