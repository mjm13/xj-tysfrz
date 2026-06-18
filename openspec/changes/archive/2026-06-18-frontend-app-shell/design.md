## Context

- **当前状态**：`frontend/` 仅有 README，无工程脚手架；静态 Demo `indicator-platform_7.html` 定义了主界面视觉，但无登录页。
- **约束**：技术栈遵循 ADR 0001（Vue 3 + TypeScript + Element Plus）；本 change 无后端 API 依赖。
- **关联领域文档**：`docs/domain/developing/context-map.md`、`docs/domain/developing/ubiquitous-language.md`、`docs/domain/developing/domain-model.md`（开发中状态，可能回滚）。

## Goals / Non-Goals

**Goals:**

- 初始化可运行的 Vite + Vue 3 前端工程
- 落地 Design Token 与主布局（232px Sidebar + Main Content）
- 实现登录页 + Mock 认证 + 路由守卫闭环
- 组件与目录结构可支撑后续业务 change 渐进填充

**Non-Goals:**

- 后端登录 API、Token 刷新、权限模型
- Demo 侧栏增强（搜索、通知、帮助、用户菜单）
- 具体业务菜单与 KPI/图表页面
- 移动端适配、国际化、单元测试

## Decisions

### 1. 工程脚手架：Vite + Vue 3 + TS

**选择**：`npm create vite@latest` 模板 `vue-ts`，手动添加 vue-router、pinia、element-plus。

**理由**：Vite 冷启动快，与 Vue 3 生态默认搭配；项目尚无历史包袱。

**备选**：Vue CLI（已维护模式减弱，不选）。

### 2. 样式方案：CSS 变量 + SCSS 覆盖 Element Plus

**选择**：`src/assets/styles/tokens.css` 存放 Demo `:root` 变量；`element-overrides.scss` 覆盖 Element 默认主题色。

**理由**：Demo 已是 CSS 变量体系，直接迁移成本最低；Element Plus 需覆盖才能对齐 Demo 主色 `#2563EB`。

**备选**：Tailwind（引入新范式，与 Demo 迁移成本高，不选）。

### 3. 路由结构

| 路径 | 组件 | 布局 | 守卫 |
| --- | --- | --- | --- |
| `/login` | `LoginView` | 无 | 已登录 → 重定向 `/` |
| `/` | `HomeView` | `MainLayout` | 未登录 → 重定向 `/login` |

**理由**：单入口首页 `/` 简洁；登录页独立于主布局，符合 Demo 无侧栏的登录场景。

### 4. 状态管理：Pinia `useAuthStore`

```text
state: { isAuthenticated: boolean, username: string }
actions: login(username, password), logout()
persist: localStorage key `zbpt-auth`（仅存 isAuthenticated + username）
```

**理由**：路由守卫需全局可读认证态；Pinia 是 Vue 3 官方推荐；持久化满足刷新保持登录验收项。

### 5. Mock 登录规则

**选择**：用户名与密码均非空即通过；可选固定测试账号 `admin/admin` 文档化。

**理由**：满足需求「任意非空凭证」；空值校验覆盖基本表单体验。

### 6. 组件拆分

```
layouts/MainLayout.vue      # 侧栏 + router-view
components/shell/
  AppSidebar.vue            # 品牌区 + 空菜单槽
  BrandLogo.vue             # 渐变 Logo + 文案
views/LoginView.vue         # 全屏登录
views/HomeView.vue          # 占位首页
```

**理由**：布局与壳层组件分离，后续业务页只往 `views/` 和菜单配置扩展。

### 7. 字体加载

**选择**：通过 Google Fonts CDN 引入 Sora + JetBrains Mono（与 Demo 一致），fallback 为系统字体。

**理由**：与 Demo 视觉一致；内网环境可在后续 change 改为本地字体文件。

## 领域设计

### 架构裁剪

**本次选择**：**不适用后端分层**；前端采用 **Presentation 单层 + 状态模块**（视图 / 布局 / store / router），等价于简单需求的「薄壳层」实现。

**理由**：

- 本 change 无领域不变量计算、无聚合持久化、无跨服务编排
- `platform-shell` 与 `identity-access` 在本阶段均为 UI 与客户端会话，不构成复杂领域逻辑
- 过度引入 application/domain 前端分层反而增加目录噪音

### 领域模型影响

| 上下文 | 模型元素 | 本 change 落地方式 |
| --- | --- | --- |
| platform-shell | `AppShell`, `DesignToken`, `BrandIdentity`, `ShellLayout` | `tokens.css`, `MainLayout`, `BrandLogo` |
| platform-shell | `NavigationMenu`（空容器） | `AppSidebar` 预留 `<slot>` |
| identity-access | `AuthSession`, `AuthGuard` | `useAuthStore` + `router.beforeEach` |
| identity-access | `UserLoggedIn` / `UserLoggedOut` | store actions（事件语义，无总线） |

### 关联 docs/domain 条目

- 术语：开发中术语登记在 `docs/domain/developing/ubiquitous-language.md`
- 上下文：开发中上下文登记在 `docs/domain/developing/context-map.md`
- 模型：开发中模型登记在 `docs/domain/developing/domain-model.md`

## Risks / Trade-offs

| 风险 | 缓解 |
| --- | --- |
| Element Plus 默认样式与 Demo 差异大 | 独立 `element-overrides.scss`，登录页/按钮优先覆盖 |
| Mock 会话与后续真实鉴权接口形状不同 | `useAuthStore` 封装 `login/logout`，后续只改 store 内部实现 |
| Google Fonts CDN 内网不可用 | 文档注明 fallback；后续 change 可本地化字体 |
| 侧栏空态体验简陋 | 本 change 接受；菜单 change 再填充 |

## Migration Plan

1. 在 `frontend/` 执行脚手架初始化与依赖安装
2. 本地 `npm run dev` 验证登录 → 首页链路
3. 无生产部署、无 DB 迁移、无 API 变更
4. 回滚：删除 `frontend/` 新增文件即可

## Open Questions

- （无阻塞项）退出登录入口：本 change 在侧栏底部或占位首页提供简单「退出」链接即可，无需用户菜单

## 建议目录结构

```
frontend/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
├── .env.example
└── src/
    ├── main.ts
    ├── App.vue
    ├── assets/styles/
    │   ├── tokens.css
    │   ├── global.scss
    │   └── element-overrides.scss
    ├── components/shell/
    │   ├── BrandLogo.vue
    │   └── AppSidebar.vue
    ├── layouts/
    │   └── MainLayout.vue
    ├── router/
    │   └── index.ts
    ├── stores/
    │   └── auth.ts
    └── views/
        ├── LoginView.vue
        └── HomeView.vue
```
