# Design: springboot-backend-skeleton

> 技术 change：本文档以**技术方案**为主，不做领域建模。

## 1. 技术方案

### 1.1 栈选型

| 维度 | 选型 | 说明 |
| --- | --- | --- |
| 语言 / 运行时 | Java 21 (LTS) | 用户确认 |
| 框架 | Spring Boot 3.3.x | 与 Java 21 兼容的稳定线 |
| 构建 | Maven | 单模块起步，后续可拆多模块 |
| 基础包 | `com.xj.zbpt` | 与仓库名一致 |
| 数据访问 | MyBatis-Plus 3.5.x | 分页插件 + BaseMapper |
| 数据库 | MySQL 8（localhost:3306，库 `xj_zbpt`） | 用户提供连接 |
| 迁移 | Flyway | 脚本 `db/migration`，真实执行 |
| API 文档 | springdoc-openapi 2.x | Swagger UI + OpenAPI JSON |
| 监控 | Spring Boot Actuator | 暴露 health |
| 测试 | JUnit 5 + Spring Boot Test + H2 | 单测与切片测试不依赖真库 |

### 1.2 目录结构

```
backend/
├── pom.xml
├── src/main/java/com/xj/zbpt/
│   ├── ZbptApplication.java          # 启动类
│   ├── common/
│   │   ├── response/ApiResponse.java # 统一响应体
│   │   ├── response/ErrorCode.java   # 错误码枚举
│   │   ├── exception/BizException.java
│   │   └── exception/GlobalExceptionHandler.java
│   ├── config/
│   │   ├── MybatisPlusConfig.java    # 分页插件
│   │   └── OpenApiConfig.java        # 文档元信息
│   └── ping/PingController.java      # 示例链路（非业务）
├── src/main/resources/
│   ├── application.yml               # 公共配置（占位 ${ENV}）
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── logback-spring.xml            # 分级日志
│   └── db/migration/
│       └── V1__baseline.sql          # baseline（空/约定示例）
└── src/test/java/com/xj/zbpt/
    └── ping/PingControllerTest.java  # 切片测试（H2）
```

### 1.3 横切能力清单

| 能力 | 实现 | 验证 |
| --- | --- | --- |
| 统一响应 | `ApiResponse<T>{code,message,data}`，成功 `ApiResponse.ok(data)` | `PingController` 返回结构正确 |
| 全局异常 | `@RestControllerAdvice` 捕获 `BizException` / 参数校验 / 兜底 | 抛 `BizException` 返回统一错误结构 |
| 分页 | MyBatis-Plus `PaginationInnerInterceptor` | 配置加载无误 |
| 迁移 | Flyway 启动时执行 `db/migration` | 应用启动后 `flyway_schema_history` 生成 |
| API 文档 | springdoc 自动扫描 | `/swagger-ui.html` 与 `/v3/api-docs` 可访问 |
| 健康检查 | Actuator | `/actuator/health` 返回 `UP` |
| 日志 | logback-spring，控制台 + 文件 | 启动日志分级输出 |

### 1.4 配置与密钥

- 公共配置用 `${DB_HOST:localhost}` 等占位，真实值放本地 `.env` / 环境变量，**不入库**
- `application-dev.yml` 连接 `xj_zbpt`；测试 profile 用 H2 内存库

## 2. 分层裁剪决策

- **采用简化分层（Controller → Service → Mapper）**，不引入完整 DDD 四层。
- 理由：本 change 为技术骨架，**无业务聚合/不变量**，完整 DDD（interfaces/application/domain/infrastructure）会造成空壳过度设计。
- 业务 change 接入时，按 `docs/conventions.md` 的「务实分层」按复杂度再决定是否升级到完整 DDD；骨架不预设业务分层目录。

## 3. 关键决策 → ADR

- 栈选型与务实分层基线记入 `docs/decisions/0003-backend-skeleton.md`（accepted）。
- 复用既有 ADR 0001（技术栈）与 0002（务实 DDD），本 ADR 仅补充后端落地细节，不重复。

## 4. 验证策略（非业务 TDD）

技术骨架以「可运行」为验收核心：

1. `mvn -q compile` 通过
2. 应用可启动（连上 MySQL，Flyway 迁移成功）
3. `GET /actuator/health` → `UP`
4. `GET /api/ping` → 统一响应结构
5. `/swagger-ui.html` 可访问
6. `PingControllerTest`（H2 切片）通过

> 示例 `ping` 与全局异常处理写最小切片测试以验证横切能力；不为纯配置类强写测试。
