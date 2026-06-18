## ADDED Requirements

### Requirement: Design Token 全局样式

系统 SHALL 从 Demo（`indicator-platform_7.html`）提取 Design Token，并在全局 CSS/SCSS 变量中定义色彩、字体、圆角与阴影，供所有壳层页面复用。

#### Scenario: Token 变量可用

- **WHEN** 开发者引用 `--blue-600`、`--gray-50` 等变量
- **THEN** 渲染结果与 Demo `:root` 定义一致

#### Scenario: 禁止散落硬编码

- **WHEN** 实现壳层组件样式
- **THEN** 主色、背景色、边框色 MUST 通过 Design Token 引用，不得直接写死色值（语义色例外需在 tokens 中登记）

---

### Requirement: 主布局结构

系统 SHALL 在已登录态提供主布局（Main Layout）：左侧固定 232px Sidebar + 右侧 flex 主内容区，整体最小高度 100vh。

#### Scenario: 侧栏固定宽度

- **WHEN** 用户在桌面端（≥1280px）查看主布局
- **THEN** 侧栏宽度 MUST 为 232px，主内容区占据剩余空间

#### Scenario: 侧栏可为空

- **WHEN** 本 change 未配置菜单项
- **THEN** 侧栏仍渲染品牌区与导航容器，菜单区域可为空白或简短占位提示

#### Scenario: 内容区路由出口

- **WHEN** 主布局加载
- **THEN** 右侧 MUST 包含 `<router-view>` 以承载子页面

---

### Requirement: 品牌标识展示

系统 SHALL 在侧栏顶部展示品牌区：渐变 Logo 方块 + 中文「指标平台」+ 英文「METRIC HUB」，视觉风格与 Demo `.side-brand` 一致。

#### Scenario: 品牌区渲染

- **WHEN** 用户进入主布局
- **THEN** 侧栏顶部显示 Logo 与双行品牌文案

---

### Requirement: 占位首页

系统 SHALL 提供占位视图（Placeholder View）作为默认首页内容，显示欢迎类文案，不要求业务数据。

#### Scenario: 首页占位

- **WHEN** 用户登录成功进入 `/`
- **THEN** 主内容区显示占位欢迎文案（如「欢迎使用指标平台」）

---

### Requirement: 登录页视觉统一

系统 SHALL 提供独立登录页 `/login`，使用同一套 Design Token，包含品牌区、用户名/密码表单与主操作按钮。

#### Scenario: 登录页独立路由

- **WHEN** 用户访问 `/login`
- **THEN** 页面以全屏登录布局渲染，不显示主布局侧栏

#### Scenario: 表单字段

- **WHEN** 登录页加载
- **THEN** 页面 MUST 包含用户名与密码输入框及「登录」主按钮
