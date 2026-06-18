# api-foundation Specification

## Purpose

后端 API 横切基础能力：统一响应体结构与全局异常处理，确保所有 REST 接口返回一致的成功/失败格式。

## Requirements

### Requirement: 统一响应体

后端所有 REST 接口 SHALL 返回统一响应结构 `ApiResponse<T>`，包含 `code`、`message`、`data` 三个字段。

#### Scenario: 成功响应

- **WHEN** 接口正常返回数据
- **THEN** 响应体 MUST 为 `{ "code": 0, "message": "success", "data": <payload> }`

#### Scenario: 失败响应

- **WHEN** 接口处理过程中抛出业务异常
- **THEN** 响应体 MUST 为 `{ "code": <非0错误码>, "message": <错误描述>, "data": null }`

### Requirement: 全局异常处理

后端 SHALL 通过 `@RestControllerAdvice` 统一处理异常，避免堆栈直接暴露给调用方。

#### Scenario: 业务异常

- **WHEN** 代码抛出 `BizException`
- **THEN** 系统 MUST 返回对应错误码与消息的统一失败响应，HTTP 状态可为 200 或约定状态

#### Scenario: 参数校验失败

- **WHEN** 请求参数未通过 Bean Validation 校验
- **THEN** 系统 MUST 返回统一失败响应并包含可读的校验错误信息

#### Scenario: 未预期异常兜底

- **WHEN** 发生未被显式捕获的异常
- **THEN** 系统 MUST 返回统一失败响应（通用错误码），且不泄露内部堆栈细节
