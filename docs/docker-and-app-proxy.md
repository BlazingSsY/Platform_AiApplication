# Docker 启动与应用反代配置说明

本文档按当前项目代码整理，适用于使用 Docker Compose 启动门户、后端服务、PostgreSQL、Redis、MinIO 和 Nginx 网关。

## 先看结论

- 当前项目使用根目录 `.env` 作为 Docker Compose 环境变量文件，不再使用 `.env.docker`。
- 统一访问入口是 `http://localhost:18080/`，端口由 `.env` 中的 `GATEWAY_PORT` 控制。
- `docker/db/init.sql` 会在 PostgreSQL 数据卷首次创建时自动执行。已有数据卷不会重复执行该脚本。
- 门户、SSO、三个审查应用的页面和后端接口都在当前 Compose 内启动。
- 真正执行审查任务所依赖的外部审查引擎不在当前 Compose 内，需要在 `.env` 中配置正确的 `host:port`。
- 修改 Java、前端或 Nginx 配置后通常需要重新构建对应镜像；只修改 `.env` 通常只需要重新创建容器。

## 服务组成

| 服务 | 容器名 | 作用 | 容器内端口 | 对外访问 |
| --- | --- | --- | --- | --- |
| `gateway` | `daip-gateway` | Nginx，承载前端静态页面和统一反代 | `80` | `${GATEWAY_PORT:-18080}` |
| `portal-backend` | `daip-portal-backend` | 门户、用户、应用卡片、组织等接口 | `8011` | 经 `/portal/` 反代 |
| `circuit-review-backend` | `daip-circuit-review-backend` | 电路审查后端 | `8012` | 经 `/circuitreview/` 反代 |
| `sourcecode-review-backend` | `daip-sourcecode-review-backend` | 软件代码审查后端 | `8013` | 经 `/sourcecodereview/` 反代 |
| `logical-review-backend` | `daip-logical-review-backend` | 逻辑审查后端 | `8014` | 经 `/logicreview/` 反代 |
| `sso-server` | `daip-sso-server` | 单点登录服务 | `9091` | 经 `/sso/` 反代 |
| `postgres` | `daip-postgres` | PostgreSQL 数据库 | `5432` | `localhost:15432` |
| `redis` | `daip-redis` | Redis，主要供 SSO 使用 | `6379` | `localhost:6379` |
| `minio` | `daip-minio` | 对象存储 | `9000/9001` | `localhost:19000/19001` |

## 首次启动

在项目根目录执行：

```powershell
Copy-Item .env.example .env
```

然后编辑 `.env`，至少确认这些配置：

```env
POSTGRES_PASSWORD=change-me
DLSC_DB_PASSWORD=change-me
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin
GATEWAY_PORT=18080

REVIEW_SERVICE_URL=host.docker.internal:38080
CODE_REVIEW_SERVICE_URL=host.docker.internal:38081
LOGIC_REVIEW_SERVICE_URL=host.docker.internal:38082
```

启动全部服务：

```powershell
docker compose up -d --build
```

查看服务状态：

```powershell
docker compose ps
```

浏览器访问：

```text
http://localhost:18080/
```

默认账号：

```text
admin / 111111
```

MinIO 控制台：

```text
http://localhost:19001/
```

## 常用启动和重启命令

首次启动或代码变更后重建：

```powershell
docker compose up -d --build
```

停止并移除当前 Compose 创建的容器和网络，不删除数据库、Redis、MinIO 数据卷：

```powershell
docker compose down
```

下次重新启动：

```powershell
docker compose up -d
```

只修改 `.env` 后，镜像不需要重新构建，但需要重新创建使用了环境变量的容器：

```powershell
docker compose up -d --force-recreate
```

只修改某个后端服务代码：

```powershell
docker compose up -d --build portal-backend
docker compose up -d --build circuit-review-backend
docker compose up -d --build sourcecode-review-backend
docker compose up -d --build logical-review-backend
```

只修改前端代码或 `docker/nginx/default.conf`：

```powershell
docker compose up -d --build gateway
```

如果某个后端容器被重新创建后，网关仍然偶发 `502`，可以重新创建网关，让 Nginx 重新解析上游容器地址：

```powershell
docker compose up -d --force-recreate gateway
```

清空所有 Compose 数据卷并重新初始化数据库：

```powershell
docker compose down -v
docker compose up -d --build
```

注意：`down -v` 会删除 PostgreSQL、Redis、MinIO 的持久化数据，只适合开发环境重置。

## 数据库初始化逻辑

当前 Compose 将 `docker/db/init.sql` 挂载到 PostgreSQL 官方镜像的初始化目录：

```yaml
./docker/db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
```

这意味着：

- 第一次创建 `postgres-data` 数据卷时，PostgreSQL 会自动导入 `docker/db/init.sql`。
- 后续 `docker compose up -d` 不会重复导入该脚本。
- 新机器部署时，只要使用空数据卷启动，应用卡片、账号、基础表结构会自动初始化。
- 如果机器上已有旧的、不完整的数据卷，初始化脚本不会覆盖它，可能出现缺字段、缺表、应用卡片为空等问题。

开发环境可以用以下命令重置为全新数据库：

```powershell
docker compose down -v
docker compose up -d --build
```

生产或需要保留数据的环境，不要直接 `down -v`，应编写迁移 SQL 修复旧数据结构。

## 网关反代路径

当前 `docker/nginx/default.conf` 中已经配置了以下路径：

| 浏览器路径 | 转发目标 |
| --- | --- |
| `/` | 前端静态页面，找不到文件时回退到 `index.html` |
| `/portal/` | `portal-backend:8011` |
| `/circuitreview/` | `circuit-review-backend:8012` |
| `/sourcecodereview/` | `sourcecode-review-backend:8013` |
| `/logicreview/` | `logical-review-backend:8014` |
| `/sso/` | `sso-server:9091` |

所以用户在浏览器中看到的域名、IP、端口可以保持一致，例如：

```text
http://localhost:18080/
http://localhost:18080/sourcecodereview/...
http://localhost:18080/circuitreview/...
```

前端页面不需要直接暴露每个 Java 后端端口。

## 外部审查引擎配置

三个审查后端还会调用外部审查引擎，这些引擎不在当前 Compose 内。配置在 `.env`：

```env
REVIEW_SERVICE_URL=host.docker.internal:38080
CODE_REVIEW_SERVICE_URL=host.docker.internal:38081
LOGIC_REVIEW_SERVICE_URL=host.docker.internal:38082
```

填写规则：

- 只写 `host:port`，不要加 `http://`，因为后端配置会自动拼接协议。
- 引擎跑在 Windows 宿主机上时，Docker 容器通常使用 `host.docker.internal:端口` 访问。
- 引擎跑在局域网其他机器上时，填写 `IP:端口`，例如 `192.168.1.20:38080`。
- 引擎也放进同一个 Compose 网络时，填写服务名和端口，例如 `review-engine:38080`。

在 Windows 上可以先验证端口是否可达：

```powershell
Test-NetConnection localhost -Port 38080
Test-NetConnection localhost -Port 38081
Test-NetConnection localhost -Port 38082
```

如果这里不通，审查页面可以打开，但创建或执行审查任务时仍会失败。

## 如何判断外部应用是否支持 `/xxx/` 子路径

把其他应用挂到统一网关下时，优先判断该应用能否在子路径运行，例如 `/myapp/`。

可以按以下方式检查：

1. 查看外部应用前端构建配置，是否支持 `base`、`publicPath`、`homepage`、`baseHref`、`context-path` 等配置。
2. 直接访问临时路径，例如 `http://localhost:18080/myapp/`，打开浏览器开发者工具，看 JS、CSS、图片接口是不是都从 `/myapp/` 下加载。
3. 如果资源请求仍然写死为 `/assets/...`、`/api/...`，说明它大概率不支持直接挂子路径，除非修改该应用构建配置。
4. 如果外部应用有后端，也要确认它的接口路径、Cookie Path、登录回调地址是否能配置子路径。

支持子路径的应用可以挂到当前网关的 `/myapp/`。不支持子路径的应用，建议使用独立域名、独立端口，或只在 `pot_application.url` 中配置完整 URL。

## 新增反代应用的基本步骤

以新增一个应用 `/myapp/` 为例。

第一步，在 `docker/nginx/default.conf` 增加 location：

```nginx
location = /myapp {
    return 308 /myapp/;
}

location ^~ /myapp/ {
    proxy_pass http://host.docker.internal:3000/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

第二步，重建网关：

```powershell
docker compose up -d --build gateway
```

第三步，在 `pot_application` 中新增或修改应用卡片，让 `url` 指向 `/myapp/`。可以通过系统管理页面维护，也可以执行 SQL。

更多细节见 [新增反代应用卡片.md](新增反代应用卡片.md)。

## 常见问题

### 访问 `http://localhost:8080/` 返回 401

`8080` 通常不是当前网关端口，可能访问到了某个后端接口或其他服务。当前统一入口是：

```text
http://localhost:18080/
```

如果修改过 `.env` 的 `GATEWAY_PORT`，以实际值为准。

### 应用卡片或顶部下拉为空

重点检查：

- PostgreSQL 是否使用了旧的、不完整的数据卷。
- `pot_application` 是否有数据。
- `pot_application.module` 是否属于门户代码支持的模块，例如 `设计研发`、`运营管理`、`生产制造`、`算力资源`。
- `status` 是否为 `1`。

开发环境可以通过 `docker compose down -v` 清空旧卷后重新启动，让 `docker/db/init.sql` 自动导入基线数据。

### 页面弹出橙色空报错框

通常是前端请求失败但后端响应体没有可展示的错误信息。先看网关和对应后端日志：

```powershell
docker compose logs gateway --tail 100
docker compose logs sourcecode-review-backend --tail 100
docker compose logs circuit-review-backend --tail 100
docker compose logs logical-review-backend --tail 100
```

如果是 `502`，通常说明目标后端未启动、启动失败、或网关缓存了已重建容器的旧地址。可先确认：

```powershell
docker compose ps
docker compose up -d --force-recreate gateway
```

### 修改 `default.conf` 或 `pot_application` 后要不要重新构建镜像

- 修改 `docker/nginx/default.conf`：需要重建 `gateway` 镜像。
- 修改 `pot_application` 数据：不需要重建镜像，刷新页面或重新登录即可。
- 修改 `.env`：不需要重建镜像，但需要重新创建相关容器。
- 修改 Java 或前端代码：需要重建对应镜像。

### Docker Desktop 里镜像显示 `In use`

`In use` 表示有容器引用了该镜像，不代表容器一定在运行。可以用以下命令看运行中的容器：

```powershell
docker ps
```

查看所有容器，包括已停止容器：

```powershell
docker ps -a
```

如果 `docker compose down` 后仍看到镜像 `In use`，通常是因为存在已停止容器、其他 Compose 项目或手动创建的容器引用了它。
