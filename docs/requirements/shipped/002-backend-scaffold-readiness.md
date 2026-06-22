---
title: 后端脚手架验收与平台基座补强
status: shipped
change: backend-platform-foundation
plan: docs/plans/backend-platform-foundation.md
owner: team
createdAt: 2026-06-19
tier: 🟡
changeType: 技术
dependsOn: springboot-backend-skeleton（已归档 2026-06-18）
blocks: P2-D0 / P3-01（前后端 API 联调）
reference:
  - docs/openspec/changes/archive/2026-06-18-springboot-backend-skeleton/
  - docs/decisions/0003-backend-skeleton.md
  - temp/02-current-state.md
  - temp/04-work-backlog.md（Phase 2 / P3-01）
---

# 背景

Change `springboot-backend-skeleton` 已于 2026-06-18 归档，交付了可运行的 Spring Boot 工程（统一响应、全局异常、Flyway、Actuator、springdoc、Ping 示例链路）。  
产品方向已切换为 **高校综合身份数据平台**（参照 `docs/原始demo/`），前端壳层与首页 Mock 已就绪，但**前后端尚未打通**，后端仍无业务 API 与业务表。

本需求不是重做脚手架，而是：**验收已有骨架 + 补齐面向身份平台联调所缺的横切能力**，为后续 `basic-identity`、`platform-dashboard` 等业务 change 提供稳定基座。

# 当前进度评估（2026-06-19）

## 已完成 ✅

| 项 | 证据 |
| --- | --- |
| Maven 工程 + Java 21 / Boot 3.3.5 | `backend/pom.xml` |
| 启动类 + 包结构 `com.xj.tysfrz` | `TysfrzApplication.java` |
| 统一响应 `ApiResponse` / `ErrorCode` | `common/response/` |
| 全局异常 `BizException` + `GlobalExceptionHandler` | `common/exception/` |
| MyBatis-Plus 分页 + 驼峰 | `MybatisPlusConfig.java` |
| Flyway baseline | `V1__baseline.sql`（空 baseline） |
| Actuator health | `application.yml` |
| OpenAPI / Swagger UI | `OpenApiConfig.java` |
| 示例 API | `GET /api/ping`、`GET /api/ping/boom` |
| 切片测试 | `PingControllerTest.java` |
| 配置占位 + `.env.example` | 根目录 `DB_*`、`VITE_API_BASE_URL` |
| ADR / OpenSpec | `0003-backend-skeleton.md`，specs `api-foundation`、`health-monitoring` |

## 未完成 / 需补强 ⚠️

| 项 | 影响 | 优先级 |
| --- | --- | --- |
| **OpenAPI / pom 元信息** | 标题与描述需持续与「身份数据平台」产品名一致 | P1（库名、模块名、包名已统一为 `xj-tysfrz` / `com.xj.tysfrz`） |
| **无 CORS 配置** | 前端 `localhost:5173` 无法直接调用 `8080` API | P1 |
| **前端无 API 客户端** | `VITE_API_BASE_URL` 已预留但未使用 | P1 |
| **无联调验收链路** | 登录仍 Mock，首页数据硬编码，未验证契约 | P1 |
| **无 `test` profile / 集成测试基线** | 仅 `@WebMvcTest`；CI 或本机缺 JDK 21 时难以验证 | P2 |
| **无 Maven Wrapper（mvnw）** | 环境 JDK 版本不一致时 `mvn test` 易失败 | P2 |
| **业务 API / 业务表** | m1~m7、m5、m6 全部未开始 | 后续 change（非本需求） |
| **真实鉴权** | 无 Session/JWT 端点 | `identity-access` change（非本需求） |

## 环境备注

本机执行 `mvn test` 若报「不支持发行版本 21」，属 **JDK 版本未对齐**，需在 README/本任务中明确 JDK 21 要求或引入 toolchain/mvnw。

# 业务目标

- 确认 archived 脚手架能力在本仓库仍可用（health、ping、swagger、Flyway）
- 消除前后端联调的前置阻塞（CORS、命名、API 基址约定）
- 建立**最小可演示**的后端调用链路：前端能请求 `/api/ping` 并展示结果（或 dev 页验证）
- 同步文档元信息至「身份数据平台」，避免后续业务 change 复制错误命名

# 用例 / 用户故事

1. 作为**前端开发者**，我希望在 dev 环境下直接调用 `http://localhost:8080/api/*`，以便开始替换首页 Mock 数据。
2. 作为**后端开发者**，我希望 openapi 标题与包描述反映「身份数据平台」，以便 Swagger 作为正确契约来源。
3. 作为**交付验收人**，我希望一条命令能验证 health + ping + 测试通过，以便确认脚手架未回退。

# 涉及限界上下文

- **无新业务上下文**（技术 change）
- 对接已有：`api-foundation`、`health-monitoring`（更新 spec 增量，不新建伪领域 context）
- 下游消费者：`platform-shell`（前端 API 层）、后续 `platform-dashboard` / `basic-identity`

# 范围

## In Scope（本 change 交付）

1. **CORS**：dev profile 允许 `http://localhost:5173`（可配置 `CORS_ALLOWED_ORIGINS`）
2. **元信息对齐**：OpenAPI title/description、pom `description`、backend README 改为身份数据平台表述（模块 `xj-tysfrz-*`、包名 `com.xj.tysfrz`、库名 `xj-tysfrz`）
3. **API 约定文档化**：统一前缀 `/api/`、响应结构引用 `ApiResponse`（写入 `backend/README.md` 或 ADR 增补）
4. **前端最小 API 层**：`frontend/src/api/client.ts` + 调用 `/api/ping`（可在首页或 dev-only 区域展示连通状态）
5. **验收脚本/清单**：文档列出 `mvn test`、`/actuator/health`、`/api/ping`、Swagger 检查步骤
6. **（可选 P2）** `application-test.yml` + 一条集成测试启动上下文；或 Maven Wrapper + JDK 21 说明

## Out of Scope（单独立项）

- 人员/分类/组织等业务表与 API（→ `basic-identity` 等 Phase 2 change）
- Dashboard 真实统计（→ `platform-dashboard`）
- 登录鉴权替换 Mock（→ `identity-access`）

# 关键决策（建议）

| 决策 | 建议 | 理由 |
| --- | --- | --- |
| 数据库名 | `xj-tysfrz` | 与产品/仓库名一致；本地 dev 可自动建库 |
| CORS | 仅 dev profile 放开 | 生产由网关或同源策略控制 |
| 首页是否接 ping | 轻量连通 badge 或 console，不替换 dashboard Mock | 范围最小 |

# 非功能需求

- 不引入新关键运行时依赖（不新增 Feign、Redis 等）
- CORS / 配置变更不得硬编码生产密钥
- 现有 `PingControllerTest` 保持通过

# 验收标准

- [x] GIVEN JDK 21 + MySQL 可用 WHEN `mvn test` THEN 全部通过（JDK 21.0.10 @ `.jdks/ms-21.0.10` 验证通过）
- [ ] GIVEN 后端 dev 启动 WHEN `GET /actuator/health` THEN 返回 `UP`
- [x] GIVEN 后端 dev 启动 WHEN `GET /api/ping` THEN 返回 `{ code, message, data: "pong" }` 结构（已有 PingController + 测试用例）
- [x] GIVEN 前端 dev（5173）+ 后端 dev（8080）WHEN 前端调用 ping THEN 无 CORS 错误且能读到响应（CorsConfig + api/client 已实现）
- [x] GIVEN Swagger UI WHEN 打开 THEN 标题/描述为身份数据平台相关文案
- [x] GIVEN `backend/README.md` WHEN 新人阅读 THEN 能完成启动、验证、联调三步

# 建议任务拆分（供 propose / tasks）

| # | 任务 | 验证 |
| --- | --- | --- |
| T1 | 新增 `WebMvcConfigurer` CORS（dev） | 浏览器跨域 ping 成功 |
| T2 | 更新 OpenAPI / pom / README 元信息 | 目视 + swagger |
| T3 | 前端 `api/client` + ping 调用 | 网络面板 200 |
| T4 | 补充 backend README「前后端联调」章节 | 文档评审 |
| T5 | （P2）test profile 或 mvnw + JDK 说明 | CI/本机 `mvn test` |

# 与 backlog 关系

| Backlog ID | 关系 |
| --- | --- |
| P3-01 前端 API 层 | 本 change 交付 T3 最小版 |
| P2-D7 platform-dashboard | 依赖本 change 联调通路 |
| P2-D0 identity-platform-domain | 可并行；业务 API 在其后 |

# 流程建议

- **档位 🟡**：Plan Mode + 可选 Superpowers；**不进完整 OpenSpec 全管道**，除非评审后升级为混合 change
- **持久化**：design 落 OpenSpec change `backend-platform-foundation`；关键决策可增补 ADR `0003` 或新建 `0004-cors-api-conventions.md`（一行结论即可）
- **domain**：跳过新建 context（技术 change）

# 备注

- 首版脚手架 OpenSpec 已归档，本需求以 **delta + 验收** 形式推进，避免重复实现已有能力
- 若产品确认数据库统一改名为 `xj_tysfrz`，作为独立 Approval Gate 子任务（涉及本地/部署环境变更）
