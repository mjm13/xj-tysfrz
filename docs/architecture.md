# 架构总览（Architecture Overview）

## 系统总览

```mermaid
flowchart LR
    userClient["UserClient"] --> webFrontend["Vue3Frontend"]
    webFrontend --> backendApi["SpringBootApi"]
    backendApi --> mysqlDb["MySQL"]
```

## 平台壳层

`platform-shell` 是前端平台壳层能力，不作为 DDD 限界上下文沉淀到 `docs/domain/established/context-map.md`。

- 职责：登录页、全局路由、MainLayout / ModuleLayout、Design Token 与模块入口承载。
- 规格真相：`docs/openspec/specs/platform-shell/spec.md`。
- 模块追溯：`docs/capability-map.md` 中的 `home` / 前端入口相关条目。
- 与领域的关系：壳层消费 `identity-access` 的登录与权限结果，但不拥有业务聚合、不定义领域不变量。

## 后端分层（DDD 可裁剪）

```mermaid
flowchart TD
    interfacesLayer["InterfacesLayer"] --> applicationLayer["ApplicationLayer"]
    applicationLayer --> domainLayer["DomainLayer"]
    applicationLayer --> infrastructureLayer["InfrastructureLayer"]
    infrastructureLayer --> mysqlDb["MySQL"]
    infrastructureLayer --> externalApi["ExternalApiOrFeign"]
```

## 后端模块划分与横切能力

后端 Maven 多模块按职责拆分；鉴权栈细节见 ADR [`0008-platform-user-access-control.md`](decisions/0008-platform-user-access-control.md)（D1–D8），此处只记录模块边界与横切职责，避免与 ADR 漂移。

| 模块 | 职责 |
| --- | --- |
| `xj-tysfrz-common` | 共享模型（`OperatorContext`、`DataScope` 等跨层 DTO/枚举） |
| `xj-tysfrz-framework` | 横切鉴权：Sa-Token `SaInterceptor` 全局登录校验与白名单、`OperatorContext` Session 读写、`@CurrentOperator` 参数解析；`NotLoginException` / `NotPermissionException` → 401/403 |
| `xj-tysfrz-business` | 业务接入：`AuthAppService` 登录/登出；Controller 使用 `@SaCheckPermission` / `@CurrentOperator`；领域 Service 与 Flyway 迁移 |

```mermaid
flowchart TD
    business["xj-tysfrz-business"] --> framework["xj-tysfrz-framework"]
    framework --> common["xj-tysfrz-common"]
    business --> mysqlDb["MySQL"]
```

## 分层裁剪原则

- 简单需求：`Controller -> Service -> Mapper`
- 复杂需求：`interfaces -> application -> domain -> infrastructure`
- 每次 change 在 `design.md` 明确裁剪决策与理由
