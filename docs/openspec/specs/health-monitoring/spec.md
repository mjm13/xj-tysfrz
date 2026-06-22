# health-monitoring Specification

## Purpose

后端健康检查与 API 文档暴露能力，供部署探活与前后端契约对齐。

## Requirements

### Requirement: 健康检查端点

后端 SHALL 通过 Spring Boot Actuator 暴露健康检查端点，供部署与探活使用。

#### Scenario: 健康端点可用

- **WHEN** 调用 `GET /actuator/health`
- **THEN** 在应用与依赖正常时 MUST 返回状态 `UP`

### Requirement: API 文档可访问

后端 SHALL 通过 springdoc-openapi 暴露 OpenAPI 文档，作为前后端契约来源。

#### Scenario: 文档端点

- **WHEN** 访问 `/v3/api-docs` 或 `/swagger-ui.html`
- **THEN** MUST 返回当前服务的 OpenAPI 描述 / 可交互文档页面
