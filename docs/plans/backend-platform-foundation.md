# Backend Platform Foundation Implementation Plan

> **For agentic workers:** 实现时按任务顺序执行；每步有明确验证命令。  
> **Goal:** 验收已有后端脚手架，补齐 CORS、命名、前端 API 客户端与联调文档，打通 5173→8080 最小链路。  
> **Architecture:** 技术 change，无新领域上下文；后端 dev-only CORS + 元信息更新；前端薄 API client 调 `/api/ping`；文档/ADR 记录 CORS 决策。  
> **Tech Stack:** Spring Boot 3.3 / Java 21、Vue 3 + Vite、现有 `ApiResponse` 契约  
> **Change:** `backend-platform-foundation`  
> **Inbox:** `docs/requirements/shipped/002-backend-scaffold-readiness.md`（已完成）

---

## 文件结构预览

| 文件 | 动作 | 职责 |
| --- | --- | --- |
| `backend/.../config/CorsConfig.java` | 新增 | dev profile CORS，`CORS_ALLOWED_ORIGINS` 可配置 |
| `backend/.../config/OpenApiConfig.java` | 修改 | 身份数据平台标题/描述 |
| `backend/pom.xml` | 修改 | description 文案 |
| `backend/README.md` | 修改 | 联调章节、JDK 21、验收清单 |
| `backend/src/test/.../CorsConfigTest.java` | 新增（可选） | 验证 dev CORS 头 |
| `frontend/src/api/client.ts` | 新增 | baseURL + fetch 封装 |
| `frontend/src/api/ping.ts` | 新增 | `getPing()` |
| `frontend/src/views/HomeView.vue` | 修改 | 轻量 API 连通 badge（不替换 dashboard Mock） |
| `docs/decisions/0004-dev-cors-api-conventions.md` | 新增 | dev CORS + `/api/` 前缀约定 |
| `docs/requirements/shipped/002-*.md` | 修改 | status → shipped |

**Out of scope：** 业务表、鉴权、mvnw（P2 可另开任务）、包名 `com.xj.zbpt` 重构。

---

## Task 1: 后端 CORS（dev）

**Files:**
- Create: `backend/src/main/java/com/xj/zbpt/config/CorsConfig.java`
- Modify: `backend/src/main/resources/application-dev.yml`

- [ ] **Step 1:** 新增 `CorsConfig`，`@Profile("dev")`，读取 `app.cors.allowed-origins`（默认 `http://localhost:5173`）
- [ ] **Step 2:** `application-dev.yml` 增加 `app.cors.allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:5173}`
- [ ] **Step 3:** 根 `.env.example` 增加 `CORS_ALLOWED_ORIGINS` 注释行（可选）
- [ ] **Step 4:** 启动后端，用 curl 带 `Origin: http://localhost:5173` 请求 `/api/ping`，确认 `Access-Control-Allow-Origin`

**Verify:** `curl -i -H "Origin: http://localhost:5173" http://localhost:8080/api/ping`

---

## Task 2: 元信息对齐

**Files:**
- Modify: `OpenApiConfig.java`, `pom.xml`, `backend/README.md`, `application.yml`（`spring.application.name` 可选改为 `xj-tysfrz-backend`）

- [ ] **Step 1:** OpenAPI title →「高校综合身份数据平台 API」，description 更新
- [ ] **Step 2:** pom `description` → 身份数据平台表述
- [ ] **Step 3:** README 首段去掉「指标平台」，改为身份数据平台

**Verify:** 浏览器打开 `http://localhost:8080/swagger-ui.html` 目视标题

---

## Task 3: 前端 API 客户端 + ping

**Files:**
- Create: `frontend/src/api/client.ts`, `frontend/src/api/ping.ts`
- Modify: `frontend/src/views/HomeView.vue`

- [ ] **Step 1:** `client.ts` — `baseURL = import.meta.env.VITE_API_BASE_URL`，`request<T>(path)` 解析 `ApiResponse<T>`
- [ ] **Step 2:** `ping.ts` — `export async function getPing(): Promise<string>`
- [ ] **Step 3:** HomeView 挂载时调用 ping，页脚或 page-head 旁显示「API 连通 / 未连通」（失败不阻塞页面）
- [ ] **Step 4:** 类型定义 `ApiResponse` 与后端结构对齐 `{ code, message, data }`

**Verify:** 前后端同时 dev 启动，首页显示 pong，控制台无 CORS 错误

---

## Task 4: 文档与 ADR

**Files:**
- Modify: `backend/README.md`
- Create: `docs/decisions/0004-dev-cors-api-conventions.md`

- [ ] **Step 1:** README 增加「前后端联调」：启动顺序、env、`/api/ping` 验证
- [ ] **Step 2:** README 增加「验收清单」：mvn test、health、ping、swagger、cors
- [ ] **Step 3:** ADR 0004：dev CORS 仅 dev profile；API 前缀 `/api/`；生产同源/网关

**Verify:** 文档自检 AC 6 条 inbox 验收标准

---

## Task 5: 测试与验证（闸门）

- [ ] **Step 1:** `cd backend && mvn test`（需 JDK 21）
- [ ] **Step 2:** `cd frontend && npm run build`
- [ ] **Step 3:** 对照 inbox 002 验收标准逐项勾选

**Verify:** 命令输出截图或日志作为完成证据

---

## 可选 P2（本 plan 不阻塞）

- Maven Wrapper + `JAVA_HOME` 说明
- `application-test.yml` + `@SpringBootTest` smoke
- 将 inbox 002 升档为 OpenSpec change 并归档（若需 spec 持久化）

---

## 完成后

1. inbox 002 → `status: shipped`
2. 若走 OpenSpec：propose → apply → verify → sync → archive → sync-knowledge（技术 change 以 ADR 为主，跳过 domain 新建）
3. 解锁 backlog P3-01 完整 API 层扩展
