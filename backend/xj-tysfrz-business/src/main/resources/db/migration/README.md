# Flyway 迁移（生产 / 开发）

- **路径**：`src/main/resources/db/migration/`
- **适用 profile**：`dev`、`prod`（MySQL）
- **约定**：已执行脚本不可修改；结构变更只新增 `V<n>__*.sql`
- **测试环境**使用独立目录 `src/test/resources/db/migration-test/`，见 `application-test.yml`
