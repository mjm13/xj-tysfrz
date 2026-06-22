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

### Requirement: 后端认证集成

登录页 SHALL 调用 identity-access 后端登录 API 获取 Sa-Token AccessToken；MUST NOT 使用前端 Mock 本地校验作为生产路径。

#### Scenario: 登录成功导航

- **WHEN** 用户在登录页输入有效凭证并提交
- **THEN** 系统 MUST 调用 POST `/api/auth/login`，存储 AccessToken，并导航至 `/`

#### Scenario: 登录失败提示

- **WHEN** 后端返回 401
- **THEN** 登录页 MUST 显示错误提示且 MUST NOT 建立会话

### Requirement: Token 路由守卫

路由守卫 SHALL 依据有效 AccessToken（Sa-Token，或经 `/api/auth/me` 校验）保护非 public 路由；无效或缺失 Token MUST 重定向至 `/login`。

#### Scenario: 未登录访问受保护路由

- **WHEN** 无有效 Token 的用户访问 `/` 或业务模块路由
- **THEN** 系统 MUST 重定向至 `/login`

#### Scenario: 已登录访问登录页

- **WHEN** 已持有有效 Token 的用户访问 `/login`
- **THEN** 系统 MUST 重定向至 `/`

### Requirement: 按权限过滤导航

顶栏与模块导航 SHALL 根据当前用户 permissions 隐藏或禁用未授权模块入口。

#### Scenario: 无权限模块不可见

- **WHEN** 用户无某 module 的 read 权限
- **THEN** 对应导航入口 MUST 不可见或 disabled，且 direct URL 访问 MUST 被守卫或后端 403 拦截

### Requirement: 会话持久化 Token

前端 SHALL 持久化 AccessToken（localStorage 或 sessionStorage）；刷新页面后 MUST 恢复登录态（Token 未过期时）。

#### Scenario: 刷新保持登录

- **WHEN** 用户已登录且 Token 未过期并刷新页面
- **THEN** 会话 MUST 从存储恢复，用户仍处于已登录状态

#### Scenario: 不存储密码

- **WHEN** 写入持久化存储
- **THEN** 系统 MUST NOT 存储明文密码

### Requirement: 平台管理顶栏入口

系统 SHALL 在顶栏提供「平台管理」导航入口；仅当用户持有 `admin:users:read`（或后续 admin 子权限）时 MUST 显示。

#### Scenario: 管理员可见平台管理

- **WHEN** 用户 permissions 含 `admin:users:read`
- **THEN** 顶栏 MUST 显示「平台管理」且可进入「平台用户」

#### Scenario: 无权限不可见

- **WHEN** 用户 permissions 不含任何 admin 用户读权限
- **THEN** 顶栏 MUST NOT 显示「平台管理」

### Requirement: 平台管理模块布局

`/admin/users` SHALL 使用 ModuleLayout，moduleKey 为 `platform-admin`，侧栏展示平台管理子菜单（首期至少「平台用户」）。

#### Scenario: 平台用户页布局

- **WHEN** 用户访问 `/admin/users`
- **THEN** 页面 MUST 使用 ModuleLayout 与 platform-admin 侧栏配置

### Requirement: 平台管理路由守卫

无 `admin:users:read` 的用户 direct 访问 `/admin/*` MUST 被路由守卫拒绝（重定向首页或 403 页）。

#### Scenario: 越权 URL

- **WHEN** 无 admin 权限用户访问 `/admin/users`
- **THEN** 系统 MUST 重定向至 `/` 或等效拒绝
