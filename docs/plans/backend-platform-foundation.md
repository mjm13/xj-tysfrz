# 后端平台基座实现计划（Backend Platform Foundation Implementation Plan）

> **实现者须知：** 实现时按任务顺序执行；每步有明确验证命令。  
> **目标：** 验收已有后端脚手架，补齐 CORS、命名、前端 API 客户端与联调文档，打通 5173→8080 最小链路。  
> **架构：** 技术 change，无新领域上下文；后端 dev-only CORS + 元信息更新；前端薄 API client 调 `/api/ping`；文档/ADR 记录 CORS 决策。  
> **技术栈：** Spring Boot 3.3 / Java 21、Vue 3 + Vite、现有 `ApiResponse` 契约  
> **变更：** `backend-platform-foundation`  
> **需求：** `docs/requirements/shipped/002-backend-scaffold-readiness.md`（已完成）

---

## 文件结构预览

| 文件 | 动作 | 职责 |
| --- | --- | --- |
| `backend/.../config/CorsConfig.java` | 新增 | dev profile CORS，`CORS_ALLOWED_ORIGINS` 可配置 |
| `backend/.../config/OpenApiConfig.java` | 修改 | 身份数据平台标题/描述 |
| `backend/pom.xml` | 修改 | description 文案 |
| `backend/README.md` | 修改 | 联调章节、JDK 21、验收清单 |
| `backend/xj-tysfrz-framework/src/test/.../CorsConfigTest.java` | 新增（可选） | 验证 dev CORS 头 |
| `frontend/src/api/client.ts` | 新增 | baseURL + fetch 封装 |
| `frontend/src/api/ping.ts` | 新增 | `getPing()` |
| `frontend/src/views/HomeView.vue` | 修改 | 轻量 API 连通 badge（不替换 dashboard Mock） |
| `docs/decisions/0004-dev-cors-api-conventions.md` | 新增 | dev CORS + `/api/` 前缀约定 |
| `docs/requirements/shipped/002-*.md` | 修改 | status → shipped |

**范围外：** 业务表、鉴权、mvnw（P2 可另开任务）、包名 `com.xj.tysfrz` 重构。

---

## 任务 1：后端 CORS（dev）

**涉及文件：**
- Create: `backend/xj-tysfrz-framework/src/main/java/com/xj/tysfrz/framework/config/CorsConfig.java`
- Modify: `backend/xj-tysfrz-business/src/main/resources/application-dev.yml`

- [ ] **步骤 1：** 新增 `CorsConfig`，`@Profile("dev")`，读取 `app.cors.allowed-origins`（默认 `http://localhost:5173`）
- [ ] **步骤 2：** `application-dev.yml` 增加 `app.cors.allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:5173}`
- [ ] **步骤 3：** 根 `.env.example` 增加 `CORS_ALLOWED_ORIGINS` 注释行（可选）
- [ ] **步骤 4：** 启动后端，用 curl 带 `Origin: http://localhost:5173` 请求 `/api/ping`，确认 `Access-Control-Allow-Origin`

**验证：** `curl -i -H "Origin: http://localhost:5173" http://localhost:8080/api/ping`

---

## 任务 2：元信息对齐

**涉及文件：**
- Modify: `OpenApiConfig.java`, `pom.xml`, `backend/README.md`, `application.yml`（`spring.application.name` 可选改为 `xj-tysfrz-backend`）

- [ ] **步骤 1：** OpenAPI title →「高校综合身份数据平台 API」，description 更新
- [ ] **步骤 2：** pom `description` → 身份数据平台表述
- [ ] **步骤 3：** README 首段去掉「指标平台」，改为身份数据平台

**验证：** 浏览器打开 `http://localhost:8080/swagger-ui.html` 目视标题

---

## 任务 3：前端 API 客户端 + ping

**涉及文件：**
- Create: `frontend/src/api/client.ts`, `frontend/src/api/ping.ts`
- Modify: `frontend/src/views/HomeView.vue`

- [ ] **步骤 1：** `client.ts` — `baseURL = import.meta.env.VITE_API_BASE_URL`，`request<T>(path)` 解析 `ApiResponse<T>`
- [ ] **步骤 2：** `ping.ts` — `export async function getPing(): Promise<string>`
- [ ] **步骤 3：** HomeView 挂载时调用 ping，页脚或 page-head 旁显示「API 连通 / 未连通」（失败不阻塞页面）
- [ ] **步骤 4：** 类型定义 `ApiResponse` 与后端结构对齐 `{ code, message, data }`

**验证：** 前后端同时 dev 启动，首页显示 pong，控制台无 CORS 错误

---

## 任务 4：文档与 ADR

**涉及文件：**
- Modify: `backend/README.md`
- Create: `docs/decisions/0004-dev-cors-api-conventions.md`

- [ ] **步骤 1：** README 增加「前后端联调」：启动顺序、env、`/api/ping` 验证
- [ ] **步骤 2：** README 增加「验收清单」：mvn test、health、ping、swagger、cors
- [ ] **步骤 3：** ADR 0004：dev CORS 仅 dev profile；API 前缀 `/api/`；生产同源/网关

**验证：** 文档自检 AC 6 条 inbox 验收标准

---

## 任务 5：测试与验证（闸门）

- [ ] **步骤 1：** `cd backend && mvn test`（需 JDK 21）
- [ ] **步骤 2：** `cd frontend && npm run build`
- [ ] **步骤 3：** 对照 inbox 002 验收标准逐项勾选

**验证：** 命令输出截图或日志作为完成证据

---

## 可选 P2（本计划不阻塞）

- Maven Wrapper + `JAVA_HOME` 说明
- `application-test.yml` + `@SpringBootTest` smoke
- 将 inbox 002 升档为 OpenSpec change 并归档（若需 spec 持久化）

---

## 完成后

1. inbox 002 → `status: shipped`
2. 若走 OpenSpec：propose → apply → verify → sync → archive → sync-knowledge（技术 change 以 ADR 为主，跳过 domain 新建）
3. 解锁 backlog P3-01 完整 API 层扩展
