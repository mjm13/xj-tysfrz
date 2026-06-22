

# xj_zbpt运维升级指南

> 适用：高校综合身份数据平台 `xj-tysfrz` 单机 / Docker Compose 部署  
> 关联：`docs/plans/release-upgrade-design.md`、ADR 0005

---

## 1. 前置条件


| 项      | 要求                              |
| ------ | ------------------------------- |
| JDK    | 21+（裸 JAR 部署时）                  |
| Docker | 24+（Compose 部署时）                |
| MySQL  | 8.0+                            |
| 网络     | 生产 Nginx 同源反代 `/api`，无需浏览器 CORS |


---

## 2. 构建发行包

```bash
# Git Bash / Linux / macOS
chmod +x scripts/release/build.sh
./scripts/release/build.sh 0.2.0
```

产物位于 `dist/xj-tysfrz-0.2.0/`：

```
xj-tysfrz-0.2.0/
├── manifest.json
├── backend/xj-tysfrz-backend-0.2.0.jar
├── frontend/dist/
└── config/nginx.conf.example
```

**同 tag 规则：** 前后端必须使用同一 `release` 版本；manifest 中 `frontendRequiresBackend` 与 `backendRequiresFrontend` 均为该版本号。

---

## 3. Docker Compose 部署（推荐）

### 3.1 首次启动

```bash
# 1. 构建前端静态资源
cd frontend
npm ci
npm run build
cd ..

# 2. 启动栈
cd docker
docker compose up -d --build
```

默认访问：`http://localhost:8088`（可通过 `HTTP_PORT` 环境变量修改）。

### 3.2 验证（smoke）

```bash
curl -s http://localhost:8088/api/ping
curl -s http://localhost:8088/api/system/info
curl -s http://localhost:8088/actuator/health
```

期望：

- `/api/ping` → `{"code":0,"data":"pong",...}`
- `/api/system/info` → 含 `release`、`flywayVersion`、`gitCommit`
- `/actuator/health` → `{"status":"UP"}`

### 3.3 环境变量

在 `docker/` 目录创建 `.env`（可选）：

```env
DB_PASSWORD=your-secure-password
DB_NAME=xj-tysfrz
HTTP_PORT=8088
```

---

## 4. 维护窗口升级流程

1. **公告**：通知用户维护时段（如 22:00–22:30）
2. **进入维护**：Nginx 返回 503 静态页（见 `docker/nginx/503.html`）
3. **备份数据库**：`mysqldump` 全库
4. **替换制品**：新 JAR + 新 `frontend/dist`
5. **启动后端**：Flyway 自动迁移；失败则中止，不恢复流量
6. **reload Nginx**：切换回正常 upstream
7. **smoke**：执行 §3.2 三项检查
8. **失败回滚**：恢复上一版 JAR + dist（**数据库不回滚**，见 ADR 0005）

### Nginx 503 切换示例

维护期间将 `docker/nginx/nginx.conf` 中 `location /` 临时改为：

```nginx
location / {
    return 503;
}
error_page 503 /503.html;
location = /503.html {
    root /usr/share/nginx/html;
    internal;
}
```

升级完成后改回 `try_files` 配置并 `docker compose exec nginx nginx -s reload`。

---

## 5. 裸机 Nginx + JAR（备选）

1. 将 `config/nginx.conf.example` 复制到 `/etc/nginx/conf.d/xj-tysfrz.conf`，修改 upstream 为实际后端地址
2. 运行 JAR：`java -jar xj-tysfrz-backend-{version}.jar --spring.profiles.active=prod`
3. 将 `frontend/dist` 部署到 Nginx `root` 目录
4. smoke 同 §3.2

---

## 6. 版本核对


| 位置                     | 说明                      |
| ---------------------- | ----------------------- |
| 页脚                     | 展示前端 / 后端版本与 git commit |
| `GET /api/system/info` | 运维脚本与监控探针               |
| `manifest.json`        | 发行包 SHA256 校验           |


前后端版本不一致时，页面顶部显示黄色横幅（不阻断浏览，提示重新部署匹配包）。

---

## 7. 后续能力（未实现）

- `scripts/release/upgrade.sh` / `rollback-app.sh` 自动化（R5）
- 内网制品库 / CI 自动部署（R6–R7）
- `POST /api/system/maintenance` 管理接口

