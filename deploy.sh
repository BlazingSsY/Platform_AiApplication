#!/usr/bin/env bash
#
# Platform_AiApplication 一键部署脚本（Linux / macOS）。
# 与 deploy.ps1 等价，把多步 docker compose 流程收成一条命令。
#
#   ./deploy.sh up        # 一键：起基础服务 -> 判空导库 -> 构建并启动全部
#   ./deploy.sh rebuild   # 改完代码重新构建并启动（依赖走缓存，很快）
#   ./deploy.sh logs portal-backend   # 跟踪某个服务日志
#   ./deploy.sh ps        # 查看状态
#   ./deploy.sh down      # 停止（保留数据）
#   ./deploy.sh reset     # 停止并删除数据卷（清库，仅测试环境）
#   ./deploy.sh initdb [--force]   # 仅初始化数据库（库为空才导入；--force 强制重导）
#
set -euo pipefail

cd "$(dirname "$0")"

# 开启 BuildKit，使 Dockerfile 里的 --mount=type=cache 依赖缓存生效。
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

ENV_FILE=".env"
# 所有 compose 调用统一带上 --env-file，用户无需再手敲。
DC=(docker compose --env-file "$ENV_FILE")

FORCE=0

ensure_env() {
  if [ ! -f "$ENV_FILE" ]; then
    if [ ! -f ".env.example" ]; then
      echo "缺少 $ENV_FILE 且找不到 .env.example，无法继续。" >&2
      exit 1
    fi
    cp ".env.example" "$ENV_FILE"
    echo "已从示例生成 $ENV_FILE —— 请检查里面的密码/端口后重新运行本脚本。"
    exit 1
  fi
}

assert_docker() {
  if ! docker info >/dev/null 2>&1; then
    echo "无法连接 Docker。请先启动 Docker 服务（或 Docker Desktop）后重试。" >&2
    exit 1
  fi
}

# 读取 .env 中某个键的值（取最后一次出现；顺带去掉可能的 CRLF）。
get_env_value() {
  local key="$1" default="$2" val
  val=$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$ENV_FILE" 2>/dev/null \
        | tail -n1 \
        | sed -E "s/^[[:space:]]*${key}[[:space:]]*=[[:space:]]*//" \
        | tr -d '\r')
  echo "${val:-$default}"
}

wait_postgres() {
  local user; user=$(get_env_value POSTGRES_USER postgres)
  echo "等待 postgres 就绪..."
  for _ in $(seq 1 30); do
    if "${DC[@]}" exec -T postgres pg_isready -U "$user" >/dev/null 2>&1; then
      return 0
    fi
    sleep 2
  done
  echo "postgres 在 60 秒内仍未就绪，请检查 '${DC[*]} logs postgres'。" >&2
  exit 1
}

init_db() {
  local user db exists
  user=$(get_env_value POSTGRES_USER postgres)
  db=$(get_env_value POSTGRES_DB dlsc)

  wait_postgres

  if [ "$FORCE" != "1" ]; then
    # 用 pot_application 表是否存在来判断库是否已初始化。
    exists=$("${DC[@]}" exec -T postgres psql -U "$user" -d "$db" \
             -tAc "SELECT to_regclass('public.pot_application')" 2>/dev/null | tr -d '[:space:]')
    if [ "$exists" = "pot_application" ]; then
      echo "数据库已初始化，跳过导入（--force 强制重导）。"
      return 0
    fi
  fi

  echo "导入数据库初始化脚本到 [$db] ..."
  local s
  # 合并基线：单一文件，含全部 schema + 数据 + 种子应用（pg_dump 生成，PG 合法）。
  for s in docker/db/init.sql; do
    if [ ! -f "$s" ]; then
      echo "  跳过（不存在）: $s"
      continue
    fi
    echo "  导入 $s"
    "${DC[@]}" exec -T postgres psql -U "$user" -d "$db" -v ON_ERROR_STOP=1 <"$s" >/dev/null
  done
  echo "数据库初始化完成。"
}

# ---- 解析参数：第一个是命令，其余透传；--force 可出现在任意位置 ----
COMMAND="up"
REST=()
first=1
for arg in "$@"; do
  case "$arg" in
    --force|-Force) FORCE=1 ;;
    *)
      if [ "$first" = "1" ]; then COMMAND="$arg"; first=0; else REST+=("$arg"); fi
      ;;
  esac
done

assert_docker

case "$COMMAND" in
  up)
    ensure_env
    echo "[1/3] 启动基础服务 (postgres / redis / minio)..."
    "${DC[@]}" up -d postgres redis minio
    echo "[2/3] 初始化数据库..."
    init_db
    echo "[3/3] 构建并启动全部服务..."
    "${DC[@]}" up -d --build
    echo
    echo "完成。访问网关： http://localhost:$(get_env_value GATEWAY_PORT 18080)"
    ;;
  rebuild)
    ensure_env
    "${DC[@]}" up -d --build "${REST[@]}"
    ;;
  restart)
    ensure_env
    "${DC[@]}" restart "${REST[@]}"
    ;;
  down)
    "${DC[@]}" down
    ;;
  reset)
    echo "将停止服务并删除 postgres/redis/minio 数据卷（清空数据）。"
    "${DC[@]}" down -v
    ;;
  logs)
    "${DC[@]}" logs -f --tail=100 "${REST[@]}"
    ;;
  ps)
    "${DC[@]}" ps
    ;;
  initdb)
    ensure_env
    init_db
    ;;
  *)
    echo "未知命令: $COMMAND" >&2
    echo "可用: up | rebuild | restart | down | reset | logs | ps | initdb" >&2
    exit 1
    ;;
esac
