## Why

指标平台前端尚无工程基线与统一视觉规范。已有静态 Demo（`indicator-platform_7.html`）定义了品牌与主布局风格，但 `frontend/` 目录仅 README 占位。需要首个 change 搭建**应用壳层（App Shell）**：登录页 + 主布局（侧栏 + 内容区），为后续指标业务页面提供可扩展框架。

## What Changes

- 在 `frontend/` 初始化 Vite + Vue 3 + TypeScript + Vue Router + Pinia + Element Plus 工程
- 抽取 Demo Design Token（色彩、字体、圆角、阴影）为全局样式变量
- 新增登录页 `/login`（Mock 登录，不调用后端）
- 新增主布局 `/`：左侧 232px Sidebar（可为空）+ 右侧 Content 区（占位首页）
- 实现路由守卫：未登录跳转登录页，已登录访问登录页重定向首页
- 预留 `SessionStore` 抽象，为后续 `identity-access` 真实鉴权对接做准备
- 新增 `.env.example` 预留 `VITE_API_BASE_URL`

**不在本 change 范围**：后端登录 API、侧栏搜索/通知/用户菜单、具体业务菜单与页面、KPI/图表组件。

## Capabilities

### New Capabilities

- `platform-shell`: 应用壳层——Design Token、主布局（Sidebar + Main Content）、品牌区、占位视图
- `identity-access-mock`: Mock 认证——登录表单、会话存储、路由守卫、退出登录

### Modified Capabilities

（无，项目尚无既有 specs）

## Impact

| 区域 | 影响 |
| --- | --- |
| `frontend/` | 新建完整前端工程与目录结构 |
| `docs/domain/developing/*` | 开发中术语与模型（可能回滚） |
| `backend/` | 无影响 |
| 依赖 | 新增 npm 包：vue、vue-router、pinia、element-plus、vite 等 |
| API 契约 | 无 |

## 来源需求

- `docs/requirements/inbox/001-frontend-app-shell.md`
