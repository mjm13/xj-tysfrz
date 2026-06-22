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
```
