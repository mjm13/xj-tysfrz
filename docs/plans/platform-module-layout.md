# Platform Module Layout Implementation Plan

> **For agentic workers:** 🟡 技术 change；按 L1→L5 顺序执行，每步有验证命令。  
> **Goal:** 交付 ModuleLayout + 公共 UI 组件 + 业务路由骨架 + mock 迁移，使各 demo 模块可独立 Vue 化。  
> **Architecture:** 配置驱动侧栏（两段式菜单）+ 嵌套路由深链；MainLayout 保留首页无侧栏，业务页用 ModuleLayout；无后端、无 domain。  
> **Tech Stack:** Vue 3 + Vue Router + Element Plus + 现有 design tokens  
> **Change:** `platform-module-layout`  
> **Inbox:** `docs/requirements/inbox/004-platform-module-layout.md`

---

## 文件结构预览

| 文件 | 动作 | 职责 |
| --- | --- | --- |
| `frontend/src/layouts/ModuleLayout.vue` | 新增 | 顶栏下左栏+主区；消费侧栏配置 |
| `frontend/src/config/module-nav.ts` | 新增 | 各模块侧栏/顶栏路由配置 |
| `frontend/src/components/shell/ModuleSidebar.vue` | 新增 | 两段式可配置侧栏 |
| `frontend/src/components/shell/AppBreadcrumb.vue` | 新增 | 面包屑 |
| `frontend/src/components/shell/PageHead.vue` | 新增 | 页头（标题+描述+badge） |
| `frontend/src/components/ui/SectionTitle.vue` | 新增 | 段落标题（左色条） |
| `frontend/src/components/ui/FilterBar.vue` | 新增 | 筛选条容器 |
| `frontend/src/components/ui/DataCard.vue` | 新增 | 数据卡片 |
| `frontend/src/components/ui/StatusBadge.vue` | 新增 | 状态徽章 |
| `frontend/src/components/ui/AppDrawer.vue` | 新增 | 抽屉封装 |
| `frontend/src/components/ui/AppToast.vue` | 新增 | Toast（或 composable） |
| `frontend/src/views/placeholders/*.vue` | 新增 | 各模块占位页 |
| `frontend/src/mocks/sysu-cls.ts` | 新增 | 分类树 mock |
| `frontend/src/mocks/sysu-org.ts` | 新增 | 组织树 mock |
| `frontend/src/router/index.ts` | 修改 | 业务嵌套路由 + 深链 |
| `frontend/src/components/shell/AppTopbar.vue` | 修改 | 顶栏 RouterLink 接线 |
| `frontend/src/views/HomeView.vue` | 修改 | 功能卡 RouterLink |
| `docs/decisions/0006-frontend-module-layout.md` | 新增 | 布局与路由约定 ADR |

**Out of scope：** 业务页真实功能、后端 API、RBAC、platform-shell OpenSpec 大改（可轻量注释 drift）。

---

## Task L1: ModuleLayout + 两段式侧栏

**Files:**
- Create: `ModuleLayout.vue`, `ModuleSidebar.vue`, `config/module-nav.ts`

- [ ] **Step 1:** 定义 `SidebarGroup` / `SidebarItem` 类型（label, path?, badge?, children?）
- [ ] **Step 2:** `ModuleSidebar` 渲染两段式菜单（组标题 + 项），active 由 `$route.path` 匹配
- [ ] **Step 3:** `ModuleLayout` = 复用现有顶栏/页脚 + `ModuleSidebar` + `<router-view />`
- [ ] **Step 4:** 在 `module-nav.ts` 为 m1 配置示例（人员管理 5 项 + 模块内子项）

**Verify:** 手动挂载一条测试路由 `/identity/basic`，可见顶栏+侧栏+占位主区。

---

## Task L2: 公共组件第一批

**Files:**
- Create: `AppBreadcrumb.vue`, `PageHead.vue`, `SectionTitle.vue`, `FilterBar.vue`, `DataCard.vue`, `StatusBadge.vue`, `AppDrawer.vue`

- [ ] **Step 1:** 对齐 demo CSS 变量（tokens.css 已有）
- [ ] **Step 2:** 各组件 props 最小集（见 demo m1 筛选条/卡片样式）
- [ ] **Step 3:** 在占位页组合展示 Breadcrumb + PageHead

**Verify:** 占位页视觉与 demo 结构一致（不要求像素级）。

---

## Task L3: 业务路由骨架 + 深链

**Files:**
- Modify: `router/index.ts`
- Create: `views/placeholders/*`

| 路由 | 占位 |
| --- | --- |
| `/identity/basic` | 人员基础身份 |
| `/identity/classification` (+ admin 子路由) | 分类身份 |
| `/identity/position` | 岗位身份 |
| `/identity/org` | 组织机构 |
| `/identity/tags` | 自定义标签 |
| `/identity/permission` | 权限管理 |
| `/services/query` (+ identity/theme/sql 子路由) | 数据查询 |
| `/services/etl` | ETL 监控 |
| `/services/sources` | 源头维护 |

- [ ] **Step 1:** 业务路由统一 `component: ModuleLayout`，children 挂占位 view
- [ ] **Step 2:** 子视图独立 path（如 `/services/query/sql`），禁止仅用 hash
- [ ] **Step 3:** 侧栏配置与路由 path 一一对应

**Verify:** 浏览器直接访问 `/services/query/sql` 可打开对应占位页且侧栏高亮正确。

---

## Task L4: Mock 数据迁移

**Files:**
- Create: `frontend/src/mocks/sysu-cls.ts`, `sysu-org.ts`

- [ ] **Step 1:** 从 `docs/原始demo/sysu-cls.js` 导出 `SYSU_CLASSIFICATION` + TS 类型
- [ ] **Step 2:** 从 `sysu-org.js` 导出 `SYSU_ORG` + 辅助函数
- [ ] **Step 3:** 占位页或 vitest 断言数组长度 > 0

**Verify:** `npm run test` 通过；import mock 无 TS 错误。

---

## Task L5: 顶栏/首页导航接线

**Files:**
- Modify: `AppTopbar.vue`, `HomeView.vue`

- [ ] **Step 1:** 顶栏身份管理下拉、权限、数据查询改为 `RouterLink`
- [ ] **Step 2:** 首页 7 功能卡 `RouterLink` 到 L3 路由
- [ ] **Step 3:** active 态与当前路由一致

**Verify:** 从首页点击各卡进入对应占位路由；顶栏往返主页正常。

---

## 收尾

- [ ] `npm run build` && `npm run test` && `npm run typecheck`（typecheck 若已有既有问题，记录不阻塞项）
- [ ] inbox 004 验收标准逐项勾选
- [ ] ADR 0006 accepted
- [ ] 需求 `status: shipped` + `git mv` → `docs/requirements/shipped/`
- [ ] 可选：轻量更新 OpenSpec platform-shell 注释（P0-09，非阻塞）

**Yellow 档闭环：** 无需 OpenSpec archive/sync-knowledge（无 domain）；plan + 验证 + shipped 迁移即可。
