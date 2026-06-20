# platform-shell Specification

## Purpose

平台壳层（App Shell）：登录页、全局路由、双布局（MainLayout / ModuleLayout）与设计系统。为业务模块提供可扩展框架，不包含具体业务功能。

## Requirements

### Requirement: Design Token 全局样式

系统 SHALL 从 Demo 提取 Design Token，并在全局 CSS 变量中定义色彩、字体、圆角与阴影，供所有壳层页面复用。

#### Scenario: Token 变量可用

- **WHEN** 开发者引用 `--color-primary`、`--text-primary` 等变量
- **THEN** 渲染结果与 Demo `:root` / `tokens.css` 定义一致

#### Scenario: 禁止散落硬编码

- **WHEN** 实现壳层组件样式
- **THEN** 主色、背景色、边框色 MUST 通过 Design Token 引用，不得直接写死色值（语义色例外需在 tokens 中登记）

### Requirement: 主布局结构

系统 SHALL 在已登录态区分两类布局：（1）首页 MainLayout——顶栏 + 无侧栏主内容区；（2）业务 ModuleLayout——顶栏 + 左侧模块侧栏 + 主内容区，整体最小高度 100vh。

#### Scenario: 首页无侧栏

- **WHEN** 用户访问 `/` 平台总览
- **THEN** 页面 MUST 使用 MainLayout，不显示模块侧栏

#### Scenario: 业务页模块侧栏

- **WHEN** 用户访问 `/identity/*` 或 `/services/*` 等业务路由
- **THEN** 页面 MUST 使用 ModuleLayout，左侧显示配置驱动的模块侧栏（约 220px），主内容区占据剩余空间

#### Scenario: 内容区路由出口

- **WHEN** 任一布局加载
- **THEN** 主内容区 MUST 包含 `<router-view>` 以承载子页面

### Requirement: 品牌标识展示

系统 SHALL 在顶栏展示品牌区（BrandLogo），视觉风格与 Demo 顶栏一致。

#### Scenario: 顶栏品牌

- **WHEN** 用户进入任意已登录页面
- **THEN** 顶栏 MUST 显示品牌 Logo 与平台名称

### Requirement: 占位首页

系统 SHALL 提供首页视图作为默认内容，展示平台总览与功能导航（可为 Mock 数据）。

#### Scenario: 首页渲染

- **WHEN** 用户登录成功进入 `/`
- **THEN** 主内容区显示平台总览与功能导航卡片

### Requirement: 登录页视觉统一

系统 SHALL 提供独立登录页 `/login`，使用同一套 Design Token，包含品牌区、用户名/密码表单与主操作按钮。

#### Scenario: 登录页独立路由

- **WHEN** 用户访问 `/login`
- **THEN** 页面以全屏登录布局渲染，不显示模块侧栏

#### Scenario: 表单字段

- **WHEN** 登录页加载
- **THEN** 页面 MUST 包含用户名与密码输入框及「登录」主按钮
