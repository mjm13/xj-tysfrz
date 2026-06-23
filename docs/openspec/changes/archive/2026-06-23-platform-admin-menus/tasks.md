## 1. 对齐

- [x] 1.1 阅读 `010-platform-admin-menus.md` 与 proposal
- [x] 1.2 **Approval Gate**：确认 platform_menu schema（design.md）
- [x] 1.3 developing 领域条目（NavigationMenu / platform_menu）

## 2. Flyway V6

- [x] 2.1 `platform_menu` + **`platform_menu_permission` 子表** + admin:menus 权限
- [x] 2.2 导航种子（每 LINK 含 permissionCodes 子表记录，如 users 绑 read+write）
- [x] 2.3 SystemInfoControllerTest flyway 版本 → 6

## 3. 后端 — Menu Admin + Navigation

- [x] 3.1 MenuEntity/Mapper、DTO、MenuAdminAppService、NavigationAppService
- [x] 3.2 MenuAdminController + NavigationController
- [x] 3.3 GET `/api/admin/menus/permission-tree`
- [x] 3.4 MenuAdminIntegrationTest + NavigationIntegrationTest 全绿

## 4. 后端 — 验证

- [x] 4.1 `backend/mvnw -pl xj-tysfrz-business -am test` 全绿

## 5. 前端

- [x] 5.1 permissions/router/nav：`/admin/menus` + admin:menus
- [x] 5.2 `stores/navigation.ts` + `/api/navigation`
- [x] 5.3 AppTopbar / ModuleSidebar 改 API 驱动
- [x] 5.4 `AdminMenusView.vue`（菜单树 + **多 permission 多选**）
- [x] 5.5 AdminRolesView 抽屉 → 菜单树 checkbox
- [x] 5.6 tests + build

## 6. 文档与收尾

- [x] 6.1 capability-map、010 AC、验收记录
- [x] 6.2 sync → archive → sync-knowledge（沉淀三问硬门禁）
- [x] 6.3 **git commit**（需求最终操作）

## 7. 不在本 change

- 外链菜单、多租户、删除菜单 API
