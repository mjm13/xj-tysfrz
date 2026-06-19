---
title: 升级发布能力（版本基座 + 打包与部署）
status: shipped
change: release-version-foundation
owner: team
createdAt: 2026-06-19
tier: 🟡
changeType: 技术
dependsOn: backend-platform-foundation（shipped）
design: docs/plans/release-upgrade-design.md
plan: docs/plans/release-upgrade-design.md
adr: docs/decisions/0005-release-version-model.md
---

# 背景

Demo 业务模块（m1~m7 等）仅为原型，**不是**交付范围。平台进入真实迭代后，需要可重复的 **版本标识、构建产物、部署升级、前后端兼容** 能力，支撑高校内网/on-prem 发版。

完整设计见 [`docs/plans/release-upgrade-design.md`](../../plans/release-upgrade-design.md)。

# 业务目标

- 运维与开发能明确「当前运行的是哪一版」（FE / BE / DB schema）
- 一次发行产出标准 tarball（manifest + jar + dist + 脚本占位）
- 生产默认 Nginx 同源部署，dev 保持 CORS 联调
- 升级流程有文档：备份 → 替换 → Flyway → smoke → 可选应用回滚

# 范围（MVP / R1–R4）

## In Scope

1. `GET /api/system/info`：release、backendVersion、buildTime、gitCommit、flywayVersion
2. 后端构建注入 `git.properties` / `build-info`
3. 前端 `VITE_APP_VERSION` 注入 + 页脚/AppFooter 展示
4. 可选：启动时比对 FE/BE 版本，不匹配显示横幅
5. `scripts/release/build.sh` 生成 `xj-tysfrz-{version}/` 目录与 `manifest.json`
6. `docs/operations/upgrade-guide.md` + `config/nginx.conf.example`

## Out of Scope（后续 change）

- Web 管理端上传安装包、远程重启（三期）
- demo 业务 API
- K8s 编排（二期 Docker Compose 优先）
- Flyway 自动回滚

# 涉及限界上下文

- 无业务 context；更新 `api-foundation` / `health-monitoring` spec 增量（版本端点）
- ADR：增补 `0005-release-version-model.md`（SemVer + manifest + 兼容性）

# 验收标准

- [x] GIVEN 后端启动 WHEN `GET /api/system/info` THEN 返回 release、flywayVersion、gitCommit
- [x] GIVEN `npm run build` WHEN 查看 dist THEN 页脚可见前端版本号
- [x] GIVEN `./scripts/release/build.sh 0.2.0` WHEN 解压产物 THEN manifest 含 FE/BE 版本与 SHA256
- [x] GIVEN upgrade-guide WHEN 运维按文档操作 THEN 可在单机完成 Docker Compose 部署与 smoke
- [x] GIVEN FE 版本高于 manifest 允许范围 WHEN 打开首页 THEN 显示版本不匹配提示（若实现 R2 横幅）

# 建议任务

| ID | 任务 |
| --- | --- |
| R1 | SystemInfoController + build 元数据 |
| R2 | 前端版本展示与兼容性检查 |
| R3 | release/build.sh + manifest 规范 |
| R4 | operations 文档 + nginx 示例 |

# 流程

- 🟡 Plan Mode；设计已落 `docs/plans/release-upgrade-design.md`
- 实现完成后 ADR 0005 accepted；无需 `docs/domain` 新 context

# 评审待决（见设计文档 §13）

- [x] 默认部署：Docker Compose 首版（§13 已确认）
- [x] FE/BE 强制同 tag 发行
- [x] 制品存放位置：暂不处理
