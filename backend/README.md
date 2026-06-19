# Backend

高校综合身份数据平台后端工程（Spring Boot 3.3.x + Java 21 + MyBatis-Plus + MySQL + Flyway + springdoc-openapi）。

详见 ADR `docs/decisions/0003-backend-skeleton.md`、`docs/decisions/0004-dev-cors-api-conventions.md` 与 change `springboot-backend-skeleton`、`backend-platform-foundation`。

## 前置要求

- **JDK 21**（LTS）。`mvn test` / `spring-boot:run` 均需 JDK 21；版本不对会报「不支持发行版本 21」。
- **MySQL 8**（dev profile 默认 `localhost:3306`，库名 `xj_zbpt` 可自动创建）

## 技术栈

- Java 21 (LTS) + Spring Boot 3.3.x + Maven
- MyBatis-Plus（分页插件 + 驼峰映射）
- MySQL 8 + Flyway（数据库迁移）
- springdoc-openapi（API 契约 / Swagger UI）
- Spring Boot Actuator（健康检查）

## API 约定

- 业务接口统一前缀 **`/api/`**
- 响应体统一为 `ApiResponse<T>`：`{ "code": 0, "message": "success", "data": ... }`（`code === 0` 表示成功）
- OpenAPI JSON 为前后端契约来源：`GET /v3/api-docs`

## 目录约定

```
src/main/java/com/xj/zbpt/
├── ZbptApplication.java   # 启动类
├── common/                # 统一响应 / 异常等横切能力
├── config/                # MyBatis-Plus、OpenAPI、CORS（dev）等配置
└── <业务模块>/            # 后续业务 change 在此新增
```

## 启动

```bash
# 复制根目录 .env.example 为 .env 并填写 DB_*（可选，默认 root/root）
cd backend
mvn spring-boot:run
```

默认 profile 为 `dev`，数据源/账号可用环境变量 `DB_HOST/DB_PORT/DB_NAME/DB_USERNAME/DB_PASSWORD` 覆盖。生产用 `prod` profile，连接信息必须由环境变量提供。

## 前后端联调（dev）

1. 启动后端（端口 **8080**，profile `dev`）
2. 启动前端：`cd frontend && npm run dev`（端口 **5173**）
3. 确认前端 `.env` 或默认 `VITE_API_BASE_URL=http://localhost:8080`
4. 登录后打开首页，应看到 **「API 已连通 · pong」** 徽章
5. 或手动验证：

```bash
curl -s http://localhost:8080/api/ping
curl -i -H "Origin: http://localhost:5173" http://localhost:8080/api/ping
```

dev profile 下 CORS 允许 `http://localhost:5173`，可通过 `CORS_ALLOWED_ORIGINS` 覆盖（逗号分隔多个源）。

## 关键入口

| 用途 | 地址 |
| --- | --- |
| 健康检查 | `GET /actuator/health` |
| 平台版本 | `GET /api/system/info` |
| 示例接口 | `GET /api/ping` |
| OpenAPI JSON | `GET /v3/api-docs` |
| Swagger UI | `GET /swagger-ui.html` |

## 验收清单

| 检查项 | 命令 / 操作 | 期望 |
| --- | --- | --- |
| 单元测试 | `mvn test` | BUILD SUCCESS |
| 健康 | `GET /actuator/health` | `"status":"UP"` |
| Ping | `GET /api/ping` | `code:0`, `data:"pong"` |
| 版本 | `GET /api/system/info` | 含 `release`、`flywayVersion`、`gitCommit` |
| CORS | curl 带 `Origin: http://localhost:5173` | 响应含 `Access-Control-Allow-Origin` |
| Swagger | 浏览器打开 `/swagger-ui.html` | 标题为「高校综合身份数据平台 API」 |
| 前后端 | 首页 API 徽章 | 显示「API 已连通 · pong」 |

## 迁移约定

Flyway 脚本路径：`src/main/resources/db/migration/`，命名 `V<version>__<description>.sql`。已执行脚本不可修改，变更通过新增版本脚本完成。

## 测试

```bash
mvn test
```

切片测试（`@WebMvcTest`）不连数据库；集成测试使用 H2 + `@ActiveProfiles("test")`（见 `SystemInfoControllerTest`）。

## Docker Compose

见 `docs/operations/upgrade-guide.md` 与 `docker/docker-compose.yml`。
