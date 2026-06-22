-- Flyway baseline 迁移（技术骨架阶段，无业务表）。
-- 约定：后续业务 change 在此目录新增 V<版本>__<描述>.sql，例如 V2__create_metric.sql。
-- 命名遵循 docs/conventions.md 的 Flyway 约定；不得修改已执行过的脚本。

SELECT 1;
