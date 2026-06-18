---
title: 前端应用壳层（登录页 + 主布局）
status: shipped
change: frontend-app-shell (archived 2026-06-18)
owner: team
createdAt: 2026-06-17
referenceDemo: indicator-platform_7.html
---

# 背景

指标平台需要从零搭建前端工程。产品视觉与交互风格已有静态 Demo（`indicator-platform_7.html`，标题「指标平台 / METRIC HUB」）作为参照，但 Demo 仅包含登录后的主界面，未包含登录页。

本需求为**第一个前端需求**：在 `frontend/` 目录初始化 Vue 3 工程，落地与 Demo 一致的设计语言，并提供最小可运行页面链路——**登录页 → 进入系统后的主布局（左侧菜单 + 右侧内容区）**。菜单与内容区在本阶段可为空占位，不要求实现具体业务页面。

# 业务目标

- 建立可扩展的前端工程基线（Vue 3 + TypeScript + Element Plus，见 ADR 0001）
- 视觉风格与 Demo 保持一致，形成可复用的 Design Token 与布局组件
- 提供登录页与主布局两条路由，完成「未登录 / 已登录」的基础导航闭环
- 为后续指标业务页面预留侧边栏菜单与主内容区插槽

# 用例 / 用户故事

1. 作为**访客**，我希望打开系统时看到风格统一的登录页，以便识别产品品牌并输入凭证（本阶段凭证校验可 Mock）。
2. 作为**已登录用户**，我希望登录成功后进入系统首页，左侧为菜单区域、右侧为内容展示区域，以便后续访问各业务模块。
3. 作为**前端开发者**，我希望 Design Token、布局组件与路由结构清晰分离，以便按 OpenSpec change 渐进填充业务页面。

# 涉及限界上下文

- `platform-shell`（平台壳层，新建）：登录、路由、全局布局、设计系统
- `identity-access`（身份与访问，后续）：真实鉴权与用户信息（本阶段不实现后端对接）

# 关键领域概念（统一语言）

| term | definition |
| --- | --- |
| 应用壳层（App Shell） | 登录页 + 主布局（侧栏 + 内容区）+ 全局路由，不包含具体指标业务 |
| 主布局（Main Layout） | 登录后的页面框架：左侧 `Sidebar`、右侧 `Main Content` |
| 设计令牌（Design Token） | 从 Demo 提取的颜色、字体、圆角、阴影等 CSS 变量 |
| 占位视图（Placeholder View） | 菜单项或内容区暂无业务实现时的空态展示 |

# 参照 Demo 的设计规范

> 来源：`indicator-platform_7.html` 内联样式与 DOM 结构。实现时抽取为全局 SCSS/CSS 变量，**不必**逐像素还原 Demo 中全部业务组件（KPI 卡片、图表、表格等）。

## 色彩

| Token | 值 | 用途 |
| --- | --- | --- |
| `--ink-900` ~ `--ink-600` | `#0A2540` ~ `#2C5282` | 品牌深色、按钮 hover |
| `--blue-600` / `--blue-500` | `#2563EB` / `#3B82F6` | 主色、激活态 |
| `--blue-50` | `#EFF6FF` | 菜单选中背景 |
| `--gray-50` ~ `--gray-900` | 见 Demo `:root` | 背景、边框、文字层级 |
| `--teal-500` / `--cyan-400` | 辅助渐变 | Logo、头像渐变 |
| `--rose` / `--green` / `--amber` | 语义色 | 告警、成功、警告（预留） |

## 字体

- 界面正文：`Sora`，fallback `PingFang SC`、`Microsoft YaHei`、`system-ui`
- 等宽/数字：`JetBrains Mono`
- 基础字号：`13px`，行高 `1.5`

## 圆角与阴影

- 圆角：`6px`（sm）/ `10px`（default）/ `14px`（lg）
- 阴影：`--shadow-sm`、`--shadow`、`--shadow-md`、`--shadow-lg`（见 Demo）

## 布局结构（主界面）

```
┌─────────────────────────────────────────────────────────┐
│ Sidebar (232px)          │ Main (flex:1, padding 20/24) │
│ ┌──────────────────────┐ │ ┌──────────────────────────┐ │
│ │ Brand Logo + 指标平台 │ │ │ Page Head（可选，本阶段可空）│ │
│ │ METRIC HUB           │ │ └──────────────────────────┘ │
│ ├──────────────────────┤ │ ┌──────────────────────────┐ │
│ │ Side Nav（本阶段可空） │ │ │ Content Slot（本阶段可空） │ │
│ ├──────────────────────┤ │ └──────────────────────────┘ │
│ │ Side Footer（可选）   │ │                              │
│ └──────────────────────┘ │                              │
└─────────────────────────────────────────────────────────┘
```

- 侧栏：白底、`border-right: 1px solid gray-200`、固定宽 `232px`、`100vh`
- 主区：浅灰背景 `gray-50`，内容区白卡片风格在后续业务页使用
- 菜单项样式（后续填充时复用）：`.side-group` / `.side-head` / `.side-item` / `.side-item.active`

## 登录页（Demo 未提供，需新建）

登录页应在同一 Design Token 下设计，建议要素：

- 居中卡片或分栏布局，保留品牌 Logo（渐变方块 +「指标平台 / METRIC HUB」）
- 表单字段：用户名、密码（Element Plus 表单组件，样式覆盖为 Demo 风格）
- 主按钮使用 `--blue-600`，圆角与 Demo `.btn-primary` 一致
- 背景可使用 `ink` 渐变或 `gray-50` 简洁底，与主界面视觉连贯

# 功能范围

## 本阶段包含（In Scope）

| 模块 | 说明 |
| --- | --- |
| 工程初始化 | Vite + Vue 3 + TypeScript + Vue Router + Pinia（路由守卫用） |
| Design Token | 全局 CSS 变量 / SCSS，对齐 Demo `:root` |
| 登录页 `/login` | 静态表单 + Mock 登录（任意非空或固定账号均可进入系统） |
| 主布局 `/` 或 `/home` | 左侧 Sidebar 容器 + 右侧 `<router-view>` |
| 路由守卫 | 未登录跳转 `/login`；已登录访问 `/login` 重定向至首页 |
| 空态占位 | 侧栏无菜单项时可显示空白或简短提示；内容区可显示「欢迎使用指标平台」类占位文案 |
| 响应式基线 | 保证桌面端（≥1280px）布局正常；移动端不做硬性要求 |

## 本阶段不包含（Out of Scope）

- 后端登录 API 对接、Token 刷新、权限模型
- Demo 中的搜索框、通知、帮助、用户菜单等侧栏增强功能
- 具体菜单项与业务页面（总览大屏、指标目录等）
- KPI 卡片、图表、表格等业务组件
- 国际化、主题切换、单元测试（可在后续 change 补充）

# 领域规则与不变量

- 主布局仅在「已登录」状态下可访问；未登录用户只能访问登录页
- 侧栏宽度固定 `232px`，不随内容伸缩
- 全局样式以 Design Token 为准，避免硬编码散落色值
- Element Plus 仅作为基础组件库；视觉以 Demo 风格覆盖为主，不采用 Element 默认主题色

# 非功能需求

- **性能**：首屏加载使用按需路由，登录页与主布局分包
- **安全**：Mock 阶段不在前端存储真实密钥；为后续 Token 方案预留 `localStorage` / `sessionStorage` 抽象
- **可维护性**：布局组件（`AppLayout`、`AppSidebar`、`LoginView`）与业务视图分离
- **契约**：本阶段无后端 API 依赖

# 技术约束

- 技术栈遵循 ADR 0001：Vue 3 + TypeScript + Element Plus
- 代码目录：`frontend/`
- 路由命名：kebab-case 路径
- 环境变量：`.env` 预留 `VITE_API_BASE_URL`（本阶段可不使用）

# 建议目录结构

```
frontend/
├── src/
│   ├── assets/styles/        # tokens、global、element overrides
│   ├── layouts/
│   │   └── MainLayout.vue    # 侧栏 + 主内容
│   ├── router/
│   ├── stores/               # auth mock store
│   ├── views/
│   │   ├── LoginView.vue
│   │   └── HomeView.vue      # 空占位首页
│   └── components/shell/     # Sidebar、BrandLogo 等
├── .env.example
└── package.json
```

# 验收标准

- [ ] GIVEN 用户未登录 WHEN 访问 `/` 或 `/home` THEN 重定向至 `/login`
- [ ] GIVEN 用户在登录页输入任意有效凭证（Mock 规则）WHEN 点击登录 THEN 进入主布局首页，URL 为 `/` 或 `/home`
- [ ] GIVEN 用户已登录 WHEN 访问 `/login` THEN 重定向至首页
- [ ] GIVEN 用户处于主布局 WHEN 查看页面 THEN 左侧为固定 232px 侧栏、右侧为内容区，两侧均可为空占位
- [ ] GIVEN 主布局渲染完成 WHEN 对比 Demo THEN 品牌区（Logo 渐变 +「指标平台 / METRIC HUB」）、主色、字体、圆角与 Demo Design Token 一致
- [ ] GIVEN 登录页渲染完成 WHEN 对比主布局 THEN 视觉风格统一（同一套 Token）
- [ ] GIVEN 执行 `npm run dev` WHEN 本地启动 THEN 无编译错误，登录 → 首页链路可手动走通
- [ ] GIVEN 退出登录（若本阶段提供入口）WHEN 确认退出 THEN 回到登录页并清除 Mock 登录态

# 备注

- **Demo 路径**：`c:\Users\mjm13\Desktop\indicator-platform_7.html`（开发时可复制关键 CSS 片段至 `frontend/src/assets/styles/` 作对照）
- **后续衔接**：真实鉴权、菜单配置、总览大屏等应作为独立 OpenSpec change，在本壳层之上迭代
- **风险**：Element Plus 默认主题与 Demo 差异较大，需预留 `element-plus` 样式覆盖层，避免后期返工
