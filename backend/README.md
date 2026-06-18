# Backend

指标平台后端工程（Spring Boot 3.3.x + Java 21 + MyBatis-Plus + MySQL + Flyway + springdoc-openapi）。

详见 ADR `docs/decisions/0003-backend-skeleton.md` 与 change `springboot-backend-skeleton`。

## 技术栈

- Java 21 (LTS) + Spring Boot 3.3.x + Maven
- MyBatis-Plus（分页插件 + 驼峰映射）
- MySQL 8 + Flyway（数据库迁移）
- springdoc-openapi（API 契约 / Swagger UI）
- Spring Boot Actuator（健康检查）

## 目录约定

```
src/main/java/com/xj/zbpt/
├── ZbptApplication.java   # 启动类
├── common/                # 统一响应 / 异常等横切能力
├── config/                # MyBatis-Plus、OpenAPI 等配置
└── <业务模块>/            # 后续业务 change 在此新增
```

## 启动

```bash
# 需 JDK 21 与可用的 MySQL（localhost:3306）
# 数据库 xj_zbpt 会在首次连接时自动创建（createDatabaseIfNotExist=true）
cd backend
mvn spring-boot:run
```

默认 profile 为 `dev`，数据源/账号可用环境变量 `DB_HOST/DB_PORT/DB_NAME/DB_USERNAME/DB_PASSWORD` 覆盖（见根目录 `.env.example`）。生产用 `prod` profile，连接信息必须由环境变量提供。

## 关键入口

| 用途 | 地址 |
| --- | --- |
| 健康检查 | `GET /actuator/health` |
| 示例接口 | `GET /api/ping` |
| OpenAPI JSON | `GET /v3/api-docs` |
| Swagger UI | `GET /swagger-ui.html` |

## 迁移约定

Flyway 脚本路径：`src/main/resources/db/migration/`，命名 `V<version>__<description>.sql`。已执行脚本不可修改，变更通过新增版本脚本完成。

## 测试

```bash
mvn test
```

切片测试（`@WebMvcTest`）不连数据库；如需集成测试可使用 H2（test scope 已引入）。
