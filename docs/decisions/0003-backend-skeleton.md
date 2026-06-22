# ADR 0003 - 后端工程骨架（Spring Boot）

- status: accepted
- date: 2026-06-18
- relates: ADR 0001（技术栈）、ADR 0002（务实 DDD）
- change: springboot-backend-skeleton

## Context

后端此前仅有 README，无可运行工程。需要确定一套后端基线（运行时、框架、数据访问、迁移、文档、监控）与分层约定，统一后续所有业务 change 的工程结构，避免重复造轮子与风格漂移。

## Decision

- **运行时 / 框架**：Java 21 (LTS) + Spring Boot 3.3.x + Maven，基础包 `com.xj.zbpt`
- **数据访问**：MyBatis-Plus（分页插件 + BaseMapper 约定）
- **数据库 / 迁移**：MySQL 8 + Flyway（脚本 `backend/xj-zbpt-business/src/main/resources/db/migration`，真实执行）
- **API 文档**：springdoc-openapi（作为前后端契约来源）
- **监控 / 日志**：Spring Boot Actuator（health）+ logback 分级日志
- **测试**：JUnit 5 + Spring Boot Test，切片测试用 H2，不依赖真库
- **分层基线**：骨架采用简化分层（Controller → Service → Mapper），不预设完整 DDD 四层；业务 change 按 ADR 0002 按复杂度再决定是否升级
- **配置/密钥**：连接信息用 `${ENV}` 占位，真实值放本地 `.env`（git 忽略），不入库

## Consequences

- 后续业务 change 直接复用统一响应、全局异常、分页、迁移、文档等横切能力
- 简化分层降低骨架与简单业务的实现成本；复杂业务仍可升级到完整 DDD
- 依赖本机/部署环境提供 MySQL 与 JDK 21；切片测试用 H2 规避真库依赖
- 引入关键外部依赖（Spring Boot、MyBatis-Plus、Flyway、springdoc、MySQL 驱动），已通过 Approval Gate 确认
