# platform-shell Specification

## Purpose

平台壳层（App Shell）：登录页、全局路由、主布局与设计系统。为业务模块提供可扩展框架。

## MODIFIED Requirements

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

系统 SHALL 在顶栏展示品牌区（BrandLogo），视觉风格与 Demo 顶栏一致；MUST NOT 在侧栏展示旧版「指标平台 / METRIC HUB」侧栏品牌区作为唯一品牌入口。

#### Scenario: 顶栏品牌

- **WHEN** 用户进入任意已登录页面
- **THEN** 顶栏 MUST 显示品牌 Logo 与平台名称

## REMOVED Requirements

### Requirement: 主布局侧栏固定 232px（指标平台）

**Reason**: 004 platform-module-layout 已改为顶栏 + ModuleLayout；侧栏仅出现在业务模块页，宽度约 220px，且由 module-nav 配置驱动。

**Migration**: 业务导航改由 AppTopbar 下拉与 ModuleSidebar 承担；established 术语「232px Sidebar 指标平台」在 sync-knowledge 时更新。

### Requirement: 品牌标识展示（侧栏顶部）

**Reason**: 品牌区已迁移至顶栏 AppTopbar / BrandLogo。

**Migration**: 删除侧栏 `.side-brand` 要求；引用顶栏品牌 Requirement。
