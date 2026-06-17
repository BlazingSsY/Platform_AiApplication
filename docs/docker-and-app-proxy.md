# Docker 启动与应用跳转配置说明

本文说明三件事：

1. 如何用 Docker Compose 启动当前平台。
2. 如何判断一个外部应用是否支持 `/xxx/` 子路径部署。
3. 后续新增应用跳转时，不同方案分别改哪些配置。

## 零、一键部署（推荐）

不想记多步命令的，直接用仓库根目录的一键脚本：

```powershell
.\deploy.ps1 up      # Windows PowerShell
```

```bash
chmod +x deploy.sh   # 首次赋予执行权限
./deploy.sh up       # Linux / macOS（需已安装 docker compose 插件）
```

两个脚本功能完全一致，子命令同名。

它会自动完成：

1. 没有 `.env` 就从示例复制一份并提示你改配置；
2. 先启动 `postgres / redis / minio`；
3. 判断数据库是否为空，空库才导入各模块 `init.sql`（已有数据自动跳过，不会误删）；
4. 用 BuildKit 依赖缓存构建并启动全部服务。

常用子命令：

```powershell
.\deploy.ps1 up        # 全流程一键部署 / 增量更新
.\deploy.ps1 rebuild   # 改完代码重新构建并启动（依赖走缓存，很快）
.\deploy.ps1 logs portal-backend   # 跟踪某个服务日志
.\deploy.ps1 ps        # 查看状态
.\deploy.ps1 down      # 停止（保留数据）
.\deploy.ps1 reset     # 停止并清空数据卷（仅测试环境）
.\deploy.ps1 initdb -Force   # 强制重导数据库脚本
```

> `--env-file .env`、BuildKit 开关等参数都封装在脚本里，无需手动带。
> 下面「一/二/三」是手动分步流程，作为原理参考；日常用上面的 `deploy.ps1` 即可。

## 一、当前平台的 Docker 结构

本项目容器化后包含这些服务：

| 服务 | 作用 | 容器内地址 |
| --- | --- | --- |
| `gateway` | Nginx 统一入口，托管前端并反代后端 | `http://gateway` |
| `portal-backend` | 门户后端 | `http://portal-backend:8011/portal` |
| `circuit-review-backend` | 电路审查后端 | `http://circuit-review-backend:8012/circuitreview` |
| `sourcecode-review-backend` | 源码审查后端 | `http://sourcecode-review-backend:8013/sourcecodereview` |
| `logical-review-backend` | 逻辑审查后端 | `http://logical-review-backend:8014/logicreview` |
| `sso-server` | SSO 服务 | `http://sso-server:9091/sso` |
| `postgres` | PostgreSQL | `postgres:5432` |
| `redis` | Redis | `redis:6379` |
| `minio` | MinIO | `minio:9000` |

宿主机默认访问入口：

```text
http://localhost:18080/
```

## 二、首次启动

### 1. 准备环境变量

复制模板：

```bash
cp .env.example .env
```

Windows PowerShell：

```powershell
Copy-Item .env.example .env
```

编辑 `.env`，至少确认这些值：

```env
POSTGRES_DB=dlsc
POSTGRES_USER=postgres
POSTGRES_PASSWORD=change-me

DLSC_DB_HOST=postgres:5432
DLSC_DB_NAME=dlsc
DLSC_DB_USERNAME=postgres
DLSC_DB_PASSWORD=change-me

MINIO_SERVER=minio:9000
MINIO_ACCOUNT=minioadmin
MINIO_SECURITY=minioadmin

SSO_REDIS_HOST=redis
SSO_MANAGEMENT_URL=http://portal-backend:8011/portal/v1

REVIEW_SERVICE_URL=host.docker.internal:38080
CODE_REVIEW_SERVICE_URL=host.docker.internal:38081
LOGIC_REVIEW_SERVICE_URL=host.docker.internal:38082
```

`REVIEW_SERVICE_URL`、`CODE_REVIEW_SERVICE_URL`、`LOGIC_REVIEW_SERVICE_URL` 是外部审查引擎地址。应用配置中会自动拼接 `http://`，所以这里只填 `host:port`。

如果外部审查引擎也在 Docker Compose 网络中，填服务名和端口，例如：

```env
REVIEW_SERVICE_URL=circuit-engine:38080
```

如果外部审查引擎跑在宿主机 Windows 上，Docker Desktop 通常可以用：

```env
REVIEW_SERVICE_URL=host.docker.internal:38080
```

### 2. 启动基础服务

```bash
docker compose up -d postgres redis minio
```

### 3. 初始化数据库

当前代码里的 DDL 自动执行配置是注释状态，且各模块根目录的 `init.sql` 包含 `DROP TABLE IF EXISTS`。因此不建议在 Compose 中自动挂载执行，避免误删已有数据。

首次空库可以手工导入：

```bash
docker compose exec -T postgres psql -U postgres -d dlsc < portal-backend/init.sql
docker compose exec -T postgres psql -U postgres -d dlsc < circuit-review-backend/init.sql
docker compose exec -T postgres psql -U postgres -d dlsc < sourcecode-review-backend/init.sql
docker compose exec -T postgres psql -U postgres -d dlsc < logical-review-backend/init.sql
```

Windows PowerShell 中 `< file.sql` 对外部命令重定向有时不稳定，可以用：

```powershell
Get-Content .\portal-backend\init.sql | docker compose exec -T postgres psql -U postgres -d dlsc
```

其余 SQL 同理。

已有生产数据时，不要直接执行这些 `init.sql`。应先备份数据库，再只执行确认需要的增量 SQL。

### 4. 构建并启动全部服务

```bash
docker compose up -d --build
```

查看状态：

```bash
docker compose ps
```

查看日志：

```bash
docker compose logs -f gateway
docker compose logs -f portal-backend
```

停止：

```bash
docker compose down
```

停止并删除数据库、Redis、MinIO 数据卷：

```bash
docker compose down -v
```

`down -v` 会删除数据，仅用于测试环境重置。

## 三、当前平台反代路径

配置文件：

```text
docker/nginx/default.conf
```

当前路径关系：

| 平台路径 | 反代到 |
| --- | --- |
| `/` | 前端 `portal/dist` |
| `/portal/` | `portal-backend:8011` |
| `/circuitreview/` | `circuit-review-backend:8012` |
| `/sourcecodereview/` | `sourcecode-review-backend:8013` |
| `/logicreview/` | `logical-review-backend:8014` |
| `/sso/` | `sso-server:9091` |

这些 `proxy_pass` 后面不要加路径，也不要加尾部 `/`：

```nginx
proxy_pass http://portal_backend;
```

不要写成：

```nginx
proxy_pass http://portal_backend/;
```

原因是后端服务自己已经配置了 context-path，例如 `/portal`、`/circuitreview`。

## 四、如何判断外部应用是否支持 `/xxx/` 子路径

假设外部应用真实地址是：

```text
http://192.168.10.21:9000/
```

你希望平台路径是：

```text
http://平台地址:8080/myapp/
```

按下面顺序检查。

### 方法 1：看应用前端构建配置

常见框架的配置项：

| 框架 | 常见配置 |
| --- | --- |
| Vite | `base: '/myapp/'` |
| Vue CLI | `publicPath: '/myapp/'` |
| React CRA | `homepage` 或 `PUBLIC_URL=/myapp` |
| Next.js | `basePath: '/myapp'` |
| Angular | `--base-href /myapp/`、`--deploy-url /myapp/` |

如果应用明确配置了类似 `base/publicPath/basePath`，通常支持子路径。

### 方法 2：用浏览器开发者工具看资源路径

打开真实地址：

```text
http://192.168.10.21:9000/
```

按 F12 看 Network。重点看 JS、CSS、图片请求。

支持子路径的应用，资源通常能按 `/myapp/...` 加载，例如：

```text
/myapp/assets/index.js
/myapp/static/css/app.css
```

不支持子路径的应用经常请求根路径资源：

```text
/assets/index.js
/static/css/app.css
/api/user
```

如果它在 `/myapp/` 下仍然请求 `/assets/...` 或 `/api/...`，通常会和平台根路径冲突。

### 方法 3：临时加 Nginx location 测试

在 `docker/nginx/default.conf` 里临时加：

```nginx
location ^~ /myapp/ {
    proxy_pass http://192.168.10.21:9000/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

重建或重启 gateway：

```bash
docker compose up -d --build gateway
```

访问：

```text
http://localhost:18080/myapp/
```

如果页面能打开、刷新不 404、控制台没有大量资源 404、登录/接口请求正常，基本支持。

### 方法 4：直接访问子路径

有些应用本身就能处理子路径。可以直接试：

```text
http://192.168.10.21:9000/myapp/
```

如果真实服务直接支持这个路径，反代会更简单。

## 五、新增应用跳转的三种方案

### 方案 A：当前 portal 内部已有页面

适用场景：应用页面已经写在当前 `portal/src/views` 里，只是需要新增入口。

修改位置：

1. 门户后台“应用管理”新增应用，或直接改数据库表 `pot_application`。
2. `url` 填 Vue Router 相对路径。

示例：

```text
/circuitReviewHome
/codeReviewHome
/logicReviewHome
/documentReview
/sourceCodeDocumentReview
/logicDocumentReview
```

不需要改 Nginx。

浏览器地址示例：

```text
http://平台地址:8080/#/codeReviewHome
```

### 方案 B：外部应用支持 `/xxx/` 子路径

适用场景：新应用独立部署、独立数据库，但可以运行在 `/myapp/` 这种路径下。

修改 `docker/nginx/default.conf`：

```nginx
location = /myapp {
    return 308 /myapp/;
}

location ^~ /myapp/ {
    proxy_pass http://192.168.10.21:9000/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

修改门户应用配置：

```text
pot_application.url = /myapp/
```

重启 gateway：

```bash
docker compose up -d --build gateway
```

访问效果：

```text
http://平台地址:8080/myapp/
```

地址栏仍然是平台地址。

### 方案 C：外部应用不支持 `/xxx/` 子路径

适用场景：老系统或第三方系统只支持部署在 `/`，静态资源、接口都写死为根路径。

推荐用子域名：

```text
http://myapp.platform.local/
```

新增一个 Nginx server：

```nginx
server {
    listen 80;
    server_name myapp.platform.local;

    client_max_body_size 1024m;

    location / {
        proxy_pass http://192.168.10.21:9000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

门户应用配置：

```text
pot_application.url = http://myapp.platform.local/
```

这种方式会打开新域名，但仍在统一域名体系里。对不支持子路径的应用最稳。

如果必须让地址栏保持 `平台地址/myapp/`，需要对该应用的 HTML、JS、CSS、接口路径做重写，维护成本高，不建议作为默认方案。

## 六、门户应用入口在哪里配置

门户前端应用列表来自：

```text
GET /portal/v1/applications
```

对应数据库表：

```text
pot_application
```

关键字段：

| 字段 | 说明 |
| --- | --- |
| `name` | 应用名称 |
| `module` | 所属板块 |
| `icon` | 应用图标 |
| `status` | 状态，通常 `1` 表示可用 |
| `url` | 应用入口地址 |

前端点击逻辑：

```text
url 以 http:// 或 https:// 开头 -> window.open 打开外部地址
url 是 /xxx 这种相对路径 -> Vue Router 在当前平台内跳转
```

因此：

| 目标 | `pot_application.url` 应填 |
| --- | --- |
| 当前 portal 内部页面 | `/codeReviewHome` |
| 通过平台 Nginx 子路径反代 | `/myapp/` |
| 外部系统直接打开 | `http://192.168.10.21:9000/` |
| 子域名反代 | `http://myapp.platform.local/` |

## 七、常见问题

### 1. 为什么不自动导入 init.sql

因为当前 `init.sql` 中有 `DROP TABLE IF EXISTS`。自动挂载到 PostgreSQL 初始化目录适合全新空库，但对已有数据有风险。

### 2. 为什么前端生产环境不写后端 IP

`portal/.env.production` 中：

```env
VITE_APP_API_BASE_URL=''
```

表示接口请求使用相对路径，例如 `/portal/...`、`/circuitreview/...`。这样才能通过同一个 Nginx 入口转发，保持浏览器地址一致。

### 3. 修改 Nginx 配置后怎么生效

如果只改了 `docker/nginx/default.conf`：

```bash
docker compose up -d --build gateway
```

或者：

```bash
docker compose restart gateway
```

如果 Nginx 配置被 bake 进镜像，推荐用 `--build gateway`。
