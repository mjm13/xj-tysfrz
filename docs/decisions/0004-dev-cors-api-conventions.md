# ADR 0004 - Dev CORS 与 API 路径约定

- status: accepted
- date: 2026-06-19
- relates: ADR 0003（后端骨架）、change `backend-platform-foundation`
- change: backend-platform-foundation

## Context

前端 Vite dev server（`http://localhost:5173`）与后端（`http://localhost:8080`）分端口部署，浏览器跨域请求 `/api/*` 会被拦截。需在开发环境放开 CORS，同时避免生产环境过度暴露。

## Decision

- **API 前缀**：所有 REST 业务接口使用 `/api/` 前缀（示例：`GET /api/ping`）
- **响应契约**：统一 `ApiResponse<T>`，`code === 0` 为成功（见 ADR 0003 / `api-foundation` spec）
- **CORS**：仅在 **`dev` profile** 启用 `CorsConfig`，映射 `/api/**`
- **允许源**：配置项 `app.cors.allowed-origins`，环境变量 `CORS_ALLOWED_ORIGINS`，默认 `http://localhost:5173`（支持逗号分隔多源）
- **生产**：不启用 dev CORS；生产由同源部署或 API 网关统一处理跨域

## Consequences

- 本地前后端联调无需浏览器插件即可调用 API
- prod profile 无 CORS bean，降低误配置风险
- 前端通过 `VITE_API_BASE_URL` 指向后端基址，与 CORS 源分离配置
