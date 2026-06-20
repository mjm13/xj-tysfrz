# ADR 0006: 前端业务模块布局与路由约定

- **Status:** accepted
- **Date:** 2026-06-20
- **Change:** platform-module-layout

## Context

Demo 为静态 HTML，各模块共享顶栏 + 两段式侧栏 + 主内容区。Vue 化需统一布局壳层，使后续业务 change 只写视图，不重复搭骨架。

## Decision

1. **双布局**
   - `MainLayout`：首页（`/``），无侧栏，保留现有总览 Mock。
   - `ModuleLayout`：业务模块，顶栏 + 左栏 + 主区 + 页脚。

2. **配置驱动侧栏**
   - 侧栏定义在 `frontend/src/config/module-nav.ts`。
   - 路由 `meta.moduleKey` 指向对应配置；两段式菜单（组标题 + 链接项）。

3. **嵌套路由 + 深链**
   - 身份模块前缀 `/identity/*`，系统服务 `/services/*`。
   - 子视图独立 path（如 `/services/query/sql`），禁止 hash 切换。
   - 占位页统一 `ModulePlaceholder.vue`，通过 route props 传标题。

4. **Mock 数据**
   - `docs/原始demo/sysu-cls.js` / `sysu-org.js` 经 `scripts/convert-demo-mocks.mjs` 转为 `frontend/src/mocks/*.ts`。
   - 业务页 import mock，不直接引用 docs 下 JS。

5. **公共 UI 组件**
   - `components/shell/`：Breadcrumb、PageHead
   - `components/ui/`：SectionTitle、FilterBar、DataCard、StatusBadge、AppDrawer

## Consequences

- 新模块：在 `module-nav.ts` 增配置 + `router/index.ts` 挂占位路由即可。
- 首页与顶栏下拉通过 `RouterLink` 深链到 L3 路由。
- 本 change 不含 RBAC、后端 API、真实业务逻辑。

## Alternatives considered

- 单布局 + 条件侧栏：首页与业务页结构差异大，拆分更清晰。
- Hash 子视图：不利于深链分享与浏览器历史，已拒绝。
