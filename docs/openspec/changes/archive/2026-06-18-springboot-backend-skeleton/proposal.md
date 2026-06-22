# Proposal: springboot-backend-skeleton

## Change Type

**technical**（技术 change）

判定依据（`.cursor/rules/00-workflow.mdc`）：交付物是可运行的后端工程骨架与横切能力（统一响应、全局异常、健康检查、迁移与 API 文档），**不引入任何业务规则或新的业务术语**，因此跳过领域建模，不新建业务限界上下文。

## Why

后端目前只有 `backend/README.md`，没有可运行工程。需要先建立 Spring Boot 工程基线，统一后续所有业务 change 的目录结构、响应契约、异常处理、数据访问、数据库迁移与 API 文档约定，避免每个业务 change 各自发明轮子。

## What

在 `backend/` 初始化一个 Spring Boot 3.3.x（Java 21，Maven）工程，落地以下横切能力：

- **工程骨架**：Maven 多 profile（dev/prod），基础包 `com.xj.tysfrz`，分层目录约定
- **统一响应体**：`ApiResponse<T>` 统一成功/失败结构
- **全局异常处理**：`@RestControllerAdvice` 统一兜底，业务异常 `BizException` + 错误码
- **MyBatis-Plus**：分页插件、`BaseMapper` 基类约定、驼峰映射
- **Flyway**：迁移脚本目录 `backend/xj-tysfrz-business/src/main/resources/db/migration`，连接真实 MySQL（localhost:3306，库 `xj-tysfrz`）
- **springdoc-openapi**：自动生成 OpenAPI 文档与 Swagger UI
- **日志 + Actuator**：分级日志、`/actuator/health` 健康端点

## Scope

### In Scope
- 可运行的 Spring Boot 应用（能启动、health 通过、build 通过）
- 上述横切能力的最小可用实现 + 一个示例 `ping`/`health` 控制器用于打通链路
- 首个 Flyway baseline 迁移（建库后可执行，仅含约定示例或空 baseline）

### Out of Scope
- 任何业务表、业务接口、业务规则
- 鉴权与权限模型（后续 `identity-access` 后端 change）
- 前端对接（前端已有壳层，本 change 不动前端）
- CI/CD 流水线

## Approval Gate

本 change 触发「新增关键外部依赖」闸门——已由用户确认引入：Spring Boot、MyBatis-Plus、Flyway、springdoc-openapi、MySQL 驱动。数据库连接（localhost:3306 / root）由用户提供，凭证仅写入本地 `.env`（git 忽略），不入库。

## Impacted Capabilities (cross-cutting)

- `api-foundation`（新增）：统一响应 + 全局异常
- `health-monitoring`（新增）：Actuator 健康检查

## Risks

- 本机 MySQL 未就绪会导致启动/迁移失败 → 缓解：数据源延迟连接 + 测试用 H2，启动验证前确认 MySQL 可达
- Java 21 需本机 JDK 21 → 缓解：propose 阶段已确认
