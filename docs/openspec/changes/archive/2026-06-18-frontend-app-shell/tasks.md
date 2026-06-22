## 1. 工程初始化

- [x] 1.1 在 `frontend/` 使用 Vite + vue-ts 模板初始化工程
- [x] 1.2 安装依赖：vue-router、pinia、element-plus、sass
- [x] 1.3 配置 `vite.config.ts` 路径别名（`@` → `src`）与开发服务器端口
- [x] 1.4 创建 `.env.example`，预留 `VITE_API_BASE_URL`
- [x] 1.5 更新 `frontend/README.md` 补充本地启动说明

## 2. Design Token 与全局样式

- [x] 2.1 从 Demo 提取 `:root` 变量至 `src/assets/styles/tokens.css`
- [x] 2.2 创建 `global.scss`：字体（Sora + JetBrains Mono）、body 基础样式、滚动条
- [x] 2.3 创建 `element-overrides.scss`：覆盖主色、按钮圆角与 Demo 对齐
- [x] 2.4 在 `main.ts` 全局引入上述样式与 Element Plus

## 3. 认证模块（identity-access-mock）

- [x] 3.1 实现 `stores/auth.ts`：`isAuthenticated`、`username`、`login`、`logout`
- [x] 3.2 实现 localStorage 持久化（key: `tysfrz-auth`，不存密码）
- [x] 3.3 实现 `router/index.ts` 路由表：`/login`、`/`（嵌套 MainLayout）
- [x] 3.4 实现 `beforeEach` 路由守卫（未登录 → `/login`，已登录访问 `/login` → `/`）

## 4. 壳层布局（platform-shell）

- [x] 4.1 实现 `components/shell/BrandLogo.vue`（渐变 Logo + 双行品牌文案）
- [x] 4.2 实现 `components/shell/AppSidebar.vue`（品牌区 + 空菜单槽 + 退出链接）
- [x] 4.3 实现 `layouts/MainLayout.vue`（232px 侧栏 + `<router-view>`）
- [x] 4.4 实现 `views/HomeView.vue` 占位欢迎页
- [x] 4.5 实现 `views/LoginView.vue`（品牌区 + 表单 + Mock 登录按钮）

## 5. 应用入口与联调

- [x] 5.1 配置 `App.vue` 仅渲染 `<router-view>`
- [x] 5.2 在 `main.ts` 挂载 Pinia、Router
- [x] 5.3 手动验证：未登录访问 `/` → 跳转 `/login`
- [x] 5.4 手动验证：Mock 登录 → 进入 `/` 主布局，刷新保持登录态
- [x] 5.5 手动验证：退出登录 → 清除会话并回到 `/login`
- [x] 5.6 确认 `npm run build` 无编译错误
