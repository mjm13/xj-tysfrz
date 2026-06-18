# Frontend — 指标平台

Vue 3 + TypeScript + Element Plus 前端工程。

## 本地启动

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173

## Mock 登录

本阶段不调用后端 API。任意**非空**用户名和密码即可登录；也可使用 `admin` / `admin`。

## 环境变量

复制 `.env.example` 为 `.env` 并按需修改：

```
VITE_API_BASE_URL=http://localhost:8080
```

## 脚本

| 命令 | 说明 |
| --- | --- |
| `npm run dev` | 开发服务器 |
| `npm run build` | 生产构建 |
| `npm run preview` | 预览构建产物 |
| `npm test` | 运行单元测试（auth Mock 逻辑） |

## 契约约定

- API 模块按 capability 组织
- 契约来源为后端 OpenAPI（`/v3/api-docs`）
- 通过 `.env` 配置 API Base URL
