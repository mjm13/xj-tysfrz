# Ubiquitous Language (Established)

## 说明

本文件仅记录已归档、已稳定复用的统一语言术语。

## 术语表

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| 应用壳层（App Shell） | platform-shell | 登录页 + 主布局（侧栏 + 内容区）+ 全局路由，不含具体业务 | `App.vue` / `router/` |
| 主布局（Main Layout） | platform-shell | 登录后的页面框架：左侧 Sidebar（固定 232px）、右侧内容区 router-view | `layouts/MainLayout.vue` |
| 设计令牌（Design Token） | platform-shell | 从 Demo 提取的颜色、字体、圆角、阴影等全局 CSS 变量 | `assets/styles/tokens.css` |
| 占位视图（Placeholder View） | platform-shell | 菜单或内容区暂无业务实现时的空态展示 | `views/HomeView.vue` |
| Mock 登录 | identity-access-mock | 不调后端、前端本地校验凭证并建立会话的登录方式 | `stores/auth.ts` |
| 路由守卫（AuthGuard） | identity-access-mock | 依据会话状态控制路由访问、重定向未登录用户 | `router/index.ts` |
| 会话存储（SessionStore） | identity-access-mock | Pinia 管理的认证态，预留 localStorage/sessionStorage 持久化 | `stores/auth.ts` |
