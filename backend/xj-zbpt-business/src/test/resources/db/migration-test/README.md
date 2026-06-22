# Flyway 迁移（测试）

- **路径**：`src/test/resources/db/migration-test/`
- **适用 profile**：`test`（H2 in-memory，`MODE=MySQL`）
- **与生产的区别**：
  - 可使用 H2 兼容语法（如 `CREATE INDEX IF NOT EXISTS`）
  - 种子数据为**最小业务集**（如 org_node 仅保留 DataScope 测试所需节点）
  - 结构与生产迁移保持版本号对齐（V1/V2/V3…），语义一致
- **禁止**：在集成测试中 `@MockBean` Mapper 替代数据库；缺数据时在测试类内通过真实 Repository/Mapper 插入符合业务规则的数据
