# 计划文档（Plans）

Plan Mode 方案的持久化位置（🟢 轻量 / 🟡 中等 需求）。

## 为什么需要

Plan Mode 默认随 session 失效、不可审计、多人无法对齐。把值得保留的计划落盘到这里，弥补这一短板，同时避免 OpenSpec 的重量级管道。

## 约定

- 文件名：`YYYY-MM-DD-<short-name>.md`（kebab-case）
- 仅在计划值得保留时落盘（一次性琐碎改动可不落）
- 🔴 核心需求不放这里，走 `docs/openspec/changes/`

## 模板

```markdown
---
title: <plan-title>
tier: green | yellow
date: <yyyy-mm-dd>
status: planned | done | dropped
---

## 目标
<要解决什么>

## 方案
<AI 出的方案，人审后定稿>

## 验证
<如何确认完成：命令 / 手动步骤>

## 验收记录（收尾必填，便于人工检查）
- 涉及菜单 / 模块：<moduleKey + 路径，或「后端 API / 无界面」>
- 改了什么功能：<一句话>
- 验收场景：1. GIVEN ... WHEN ... THEN ...
- 手动验证步骤：<点哪里、看到什么 / 接口示例与预期响应>
- 自动化覆盖：<测试名 / 命令>；需人工点验：<...>
- 本次范围外 / Deferred：<...>
```

> 🟢/🟡 需求若无独立需求文件，「人工验收说明」就追加到本计划文档的「验收记录」段（见 `.cursor/rules/00-workflow.mdc`）。
