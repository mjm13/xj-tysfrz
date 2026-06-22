# 02 — 当前项目状态

快照日期：2026-06-19

## 1. 前端（`frontend/`）

### 1.1 技术栈

Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus + SCSS

### 1.2 已实现路由

| 路径 | 组件 | 状态 |
| --- | --- | --- |
| `/login` | `LoginView.vue` | ✅ 双栏登录页，Mock 凭证 |
| `/` | `HomeView.vue`（`MainLayout` 内） | ✅ UI 对齐 demo 首页；数据为前端硬编码 Mock |

### 1.3 壳层组件

| 组件 | 状态 | 说明 |
| --- | --- | --- |
| `AppTopbar.vue` | ✅ 部分 | 64px 顶栏、时钟、用户区、退出；**下拉/链接均为 `#` 占位** |
| `AppFooter.vue` | ✅ | 与 demo 一致 |
| `MainLayout.vue` | ✅ | Topbar + main(1440) + Footer |
| `BrandLogo.vue` | ✅ | 「身份数据平台」+ SVG  mark |
| `tokens.css` / `global.scss` | ✅ | design-spec-light 变量 |

### 1.4 未实现

- 模块级 `ModuleLayout`（顶栏下 **左侧模块 Sidebar**）
- 12 个 demo 业务页 / 对应路由
- 共用 UI 组件库（filter-bar、data-card、tree-table、page-head、breadcrumb 等）
- `src/api/` 层（无 HTTP 客户端封装）
- 首页/业务数据对接后端
- 顶栏导航真实 `RouterLink`

### 1.5 鉴权

| 能力 | 状态 |
| --- | --- |
| Mock 登录 | ✅ `stores/auth.ts` + localStorage `tysfrz-auth` |
| 路由守卫 | ✅ 未登录 → `/login` |
| 真实后端鉴权 | ❌ |

## 2. 后端（`backend/`）

| 能力 | 状态 |
| --- | --- |
| Spring Boot 骨架 | ✅ |
| 统一响应 / 全局异常 | ✅ |
| Actuator / OpenAPI | ✅ |
| Flyway | ✅ `V1__baseline.sql`（仅 `SELECT 1`，**无业务表**） |
| 业务 Controller / Service | ❌ 仅 `PingController` |

## 3. OpenSpec / 领域文档

### 3.1 已归档 Spec

| Spec | 来源 | 与 Demo 关系 |
| --- | --- | --- |
| `platform-shell` | frontend-app-shell | ⚠️ 描述**侧栏布局 + 指标平台** Demo，与当前身份平台顶栏 demo **不一致** |
| `identity-access-mock` | frontend-app-shell | ✅ Mock 登录仍适用 |
| `api-foundation` / `health-monitoring` | backend skeleton | ✅ 技术基建 |

### 3.2 领域知识

| 位置 | 状态 |
| --- | --- |
| `docs/domain/developing/*` | 空（无活跃 change） |
| `docs/domain/established/context-map` | 仅 `platform-shell` + `identity-access-mock` |
| `docs/domain/established/ubiquitous-language` | 仍为「侧栏 / 指标平台」术语 |

### 3.3 Requirements Inbox

- `001-frontend-app-shell.md` → **已 shipped**（参照旧 indicator demo，需 supersede 或新需求覆盖）

## 4. 与 Demo 对齐度速览

| 维度 | 完成度 | 备注 |
| --- | --- | --- |
| 设计 Token | ~95% | 已迁移 design-spec-light |
| 登录页 | ~80% | Demo 无参照，按同规范自建 |
| 首页 UI | ~90% | 结构 + Mock 联动已有；缺真实 API |
| 顶栏导航 | ~40% | 视觉有，路由/下拉未通 |
| 模块内布局 | 0% | Demo 核心业务页均含 Sidebar |
| 业务页面 | 0/12 | 无 Vue 页面 |
| 后端业务 | 0% | 无领域 API / 表结构 |
