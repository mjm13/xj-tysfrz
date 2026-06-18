# ADR 0001 - Technology Stack

- status: accepted
- date: 2026-06-17

## Context

项目需要支持前后端分离、领域建模和渐进式演进。

## Decision

- Backend: Spring Boot + MyBatis-Plus + MySQL
- Frontend: Vue 3 + Element Plus
- DB migration: Flyway
- API contract: springdoc-openapi
- Planning workflow: OpenSpec
- Execution support: Superpowers skills/rules

## Consequences

- 具备稳定技术栈与契约基础
- 可按需求复杂度裁剪 DDD，不强制重架构
