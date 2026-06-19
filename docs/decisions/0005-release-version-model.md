# ADR 0005：平台发行版与版本模型

## 状态

Accepted（2026-06-19）

## 背景

平台进入真实迭代，需要在开发、运维、现场排障之间建立统一的版本标识与发布物规范。Demo 业务模块不在交付范围内。

## 决策

### 1. 三层版本

| 层级 | 标识 | 来源 |
| --- | --- | --- |
| 平台发行版 | SemVer `x.y.z` | Git tag `vx.y.z` |
| 组件版 | FE / BE 与发行版同步 | `package.json` / `pom.xml` |
| Schema 版 | Flyway `Vn` | `db/migration/Vn__*.sql` |

### 2. 同 tag 强制

一次发行必须 FE + BE **同一版本号**，manifest 中 `frontendRequiresBackend` 与 `backendRequiresFrontend` 均为该版本。

Hotfix 仍打新 tag（如 `v0.2.1`），全量重发前后端制品；不允许 FE/BE 独立 patch tag。

### 3. 运行态 API

`GET /api/system/info` 返回 `release`、`backendVersion`、`buildTime`、`gitCommit`、`flywayVersion`，供前端页脚、运维 smoke 与排障共用。

### 4. 兼容性校验

前端启动后调用 `/api/system/info`，若 `VITE_APP_VERSION !== release` 则显示横幅提示，不阻断登录（首版）。

### 5. 维护窗口

Breaking DB 变更或全量换包前，通过 Nginx 503 静态页进入维护窗口；首版不实现 maintenance API。

### 6. 数据库回滚

Flyway **只前滚**；应用可回滚 JAR/dist，但 DB 已执行迁移后不得假设可逆。Breaking 迁移需维护窗口 + 全量备份。

### 7. 首版部署形态

Docker Compose（nginx + backend + mysql）为标准交付与验证环境；裸 Nginx+JAR 为备选文档路径。

制品库 / CI 分发暂缓（见 release-upgrade-design §13）。

## 后果

-  positive：版本可追溯、发行包可校验（manifest SHA256）、运维文档与 Compose 可复现
- negative：hotfix 必须全 tag 重发，不能仅换 dist 而不 bump tag
- 实现：`SystemInfoController`、`scripts/release/build.sh`、`docker/docker-compose.yml`

## 参考

- `docs/plans/release-upgrade-design.md`
- `docs/operations/upgrade-guide.md`
