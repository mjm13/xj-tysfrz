# Tasks: springboot-backend-skeleton

> 技术 change：验证以「可编译 / 可启动 / health 通过」为主，仅对统一响应与全局异常写最小切片测试。

## 1. 工程骨架

- [x] 1.1 在 `backend/` 创建 `pom.xml`：Java 21、Spring Boot 3.3.x parent，依赖 web、validation、actuator、mybatis-plus-spring-boot3-starter、flyway-core + flyway-mysql、mysql-connector-j、springdoc-openapi-starter-webmvc-ui、test（含 H2）
  - 验证：`mvn -q -DskipTests compile` 通过
- [x] 1.2 创建启动类 `com.xj.tysfrz.TysfrzApplication`
  - 验证：`mvn -q spring-boot:run` 能进入启动流程

## 2. 横切能力

- [x] 2.1 统一响应体 `common/response/ApiResponse.java` + `ErrorCode.java`
  - 验证：`ApiResponse.ok(x)` / `ApiResponse.fail(code,msg)` 结构符合 spec
- [x] 2.2 业务异常 `common/exception/BizException.java` + 全局处理 `GlobalExceptionHandler.java`（业务异常 / 参数校验 / 兜底）
  - 验证：切片测试覆盖业务异常分支；参数校验/兜底由 GlobalExceptionHandler 实现
- [x] 2.3 MyBatis-Plus 配置 `config/MybatisPlusConfig.java`（分页插件 + 驼峰映射）
  - 验证：上下文加载无误
- [x] 2.4 springdoc 配置 `config/OpenApiConfig.java`（标题/版本元信息）
  - 验证：`/v3/api-docs` 返回 JSON
- [x] 2.5 日志 `logback-spring.xml`（控制台 + 文件，分级）
  - 验证：启动日志按级别输出

## 3. 配置与数据库

- [x] 3.1 `application.yml` / `application-dev.yml` / `application-prod.yml`：数据源用 `${DB_*}` 占位，dev 连 `xj-tysfrz`
- [x] 3.2 切片测试 `@WebMvcTest` 不连数据库（H2 已引入 test scope，供后续集成测试）
- [x] 3.3 根 `.env`（git 忽略）写入本地 MySQL 连接（localhost:3306 / root / root / xj-tysfrz）；`createDatabaseIfNotExist=true` 自动建库
  - 验证：本机能连上 MySQL，应用启动成功
- [x] 3.4 Flyway baseline `db/migration/V1__baseline.sql`（空 baseline 或建表约定示例注释）
  - 验证：应用启动后生成 `flyway_schema_history` 表

## 4. 示例链路

- [x] 4.1 `ping/PingController.java`：`GET /api/ping` 返回 `ApiResponse<String>`（非业务，仅打通链路）
  - 验证：`PingControllerTest`（H2 切片）断言统一响应结构

## 5. 集成验证（AC 闸门）

- [x] 5.1 `GET /actuator/health` → `UP`（对应 health-monitoring spec）
- [x] 5.2 `GET /api/ping` → 统一响应结构（对应 api-foundation 成功场景）
- [x] 5.3 抛 `BizException` → 统一失败结构（对应 api-foundation 失败场景）
- [x] 5.4 `/swagger-ui.html` 可访问（对应 health-monitoring 文档场景）
- [x] 5.5 `mvn -q test` 全绿

## 6. 文档

- [x] 6.1 更新 `backend/README.md`：启动方式、profile、Flyway、OpenAPI 入口
- [x] 6.2 写 ADR `docs/decisions/0003-backend-skeleton.md`（accepted）
