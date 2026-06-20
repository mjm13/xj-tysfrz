---
title: 平台模块布局与前端骨架
status: shipped
change: platform-module-layout
owner: team
createdAt: 2026-06-19
shippedAt: 2026-06-20
tier: 🟡
changeType: 技术
dependsOn: backend-platform-foundation（shipped）, release-version-foundation（shipped）
plan: docs/plans/platform-module-layout.md
demoRef: docs/原始demo/m1-basic-identity.html（双栏布局参照）
relatedPlan: temp/05-demo-feature-tasks.md（T0-01~05）, temp/04-work-backlog.md（P0-05~08）
---

# 背景

当前前端仅有顶栏 + 首页 + 登录（mock）两条路由，缺少承载业务模块的「模块布局壳层」。Demo 全部业务页（m1~m7）共用**顶栏 + 左侧两段式侧栏 + 主区**的双栏结构。在铺开任何业务模块前，必须先把这套骨架与公共组件做出来，否则每个模块都会重复造布局。

本 change 为**技术 change**：只做工程骨架与横切 UI 能力，不引入业务规则、不建模领域、不接后端业务 API。

# 业务目标

- 业务模块开发者拿到稳定的 `ModuleLayout` + 公共组件，专注写视图
- 顶栏/侧栏导航可配置化驱动，新增模块只改配置
- 每个业务视图有独立路由（支持深链），替代 demo 的 `data-view` JS 切换
- Demo mock 数据沉淀为 TS 模块，供 Phase 1 各页面消费

# 范围

## In Scope（对应 temp/05 T0-01~05）

1. `ModuleLayout`：顶栏下「左侧栏 + 主区」两栏布局
2. 两段式可配置侧栏：跨模块组 + 模块内 `data-view` 组
3. 公共组件第一批：`AppBreadcrumb` / `PageHead` / `SectionTitle` / `FilterBar` / `DataCard` / `StatusBadge` / `Drawer` / `Toast`
4. 业务路由骨架：各模块占位路由 + 子视图独立 path（解决 demo 深链失效 QG-06）
5. Mock 数据迁移：`sysu-cls.js` / `sysu-org.js` → `frontend/src/mocks/*.ts`
6. 顶栏/首页功能卡 RouterLink 接线到占位路由

## Out of Scope

- 任何业务页面的真实功能实现（属各 `ui-*` change）
- 后端业务 API / 建表
- 真实鉴权 RBAC（属 identity-access）
- 像素级还原 demo（采用「结构 + 交互对齐」，CSS 收敛到公共组件）

# 涉及限界上下文

- 无业务 context（技术 change）
- 更新 `platform-shell` spec 反映「顶栏 + 模块布局」（替换旧侧栏指标平台描述，对应 P0-09 文档漂移）
- 必要时补 ADR：前端布局与路由约定

# 验收标准

- [x] GIVEN 任一业务路由 WHEN 进入页面 THEN 显示顶栏 + 模块侧栏 + 面包屑 + page-head 的标准骨架
- [x] GIVEN 侧栏配置数组 WHEN 新增一项 THEN 无需改布局组件即出现新菜单
- [x] GIVEN 某模块的子视图 WHEN 通过 URL 直接访问 THEN 能定位到该子视图（深链可用）
- [x] GIVEN 首页 7 功能卡 / 顶栏项 WHEN 点击 THEN 跳转到对应模块占位路由
- [x] GIVEN `sysu-cls` / `sysu-org` mock WHEN 在组件中 import THEN 以 TS 类型化模块提供数据
- [x] GIVEN `npm run build` 与 `npm run test` THEN 通过

# 建议任务

| ID | 任务 | temp/05 对应 |
| --- | --- | --- |
| L1 | ModuleLayout + 两段式侧栏 | T0-01, T0-02 |
| L2 | 公共组件第一批 | T0-03 |
| L3 | 业务路由骨架 + 子视图深链 | T0-04 |
| L4 | Mock 数据迁移到 TS | T0-05 |
| L5 | 顶栏/首页导航接线 | T-HOME-02 |

# 流程

- 🟡 中等技术 change：Plan Mode；计划见 [`docs/plans/platform-module-layout.md`](../../plans/platform-module-layout.md)
- 不进 `docs/domain`（无业务建模）
- 与 `identity-platform-domain`（B 线，🔴）并行，互不阻塞

# 备注

- 本 change 是 Phase 1 前端 parity 的**前置骨架**，完成后各 `ui-*` 模块可独立开工
- 逻辑断点台账见 `temp/06-open-questions.md`；本 change 不涉及 🔴 业务断点
- ADR：`docs/decisions/0006-frontend-module-layout.md`
- `npm run typecheck` 仍有既有路径别名 / vite 插件类型问题，不阻塞本 change
