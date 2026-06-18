# Domain Model (Established)

## 说明

本文件仅记录已归档、已落地的领域模型。

## 模型清单

### identity-access-mock

> 壳层阶段为 UI + Mock，模型极薄；仅记录会话概念，后端真实鉴权落地后再扩展。

- **Session（会话，值对象）**：表示当前登录态。
  - 字段：`username`、`isAuthenticated`、（预留）`token`
  - 不变量：未认证态不得访问受保护路由
  - 持久化：localStorage/sessionStorage 抽象，Mock 阶段不存真实密钥
  - 落地：`frontend/src/stores/auth.ts`

### platform-shell

> 纯 UI 壳层，无业务聚合；模型为视觉与布局约定，详见 `ubiquitous-language` 与 `openspec/specs/platform-shell`。
