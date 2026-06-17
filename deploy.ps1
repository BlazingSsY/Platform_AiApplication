#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Platform_AiApplication 一键部署脚本。把多步 docker compose 流程收成一条命令。

.DESCRIPTION
    封装了 --env-file、BuildKit 缓存、数据库自动初始化等繁琐细节。常用：

      .\deploy.ps1 up        # 一键：起基础服务 -> 判空导库 -> 构建并启动全部
      .\deploy.ps1 rebuild   # 重新构建并启动全部（改了代码后用，走依赖缓存很快）
      .\deploy.ps1 logs      # 跟踪全部日志（可加服务名：.\deploy.ps1 logs portal-backend）
      .\deploy.ps1 ps        # 查看状态
      .\deploy.ps1 down      # 停止（保留数据）
      .\deploy.ps1 reset     # 停止并删除数据卷（清库，仅测试环境）
      .\deploy.ps1 initdb    # 仅初始化数据库（库为空时才导入；-Force 强制重导）
#>
[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [ValidateSet('up', 'rebuild', 'restart', 'down', 'reset', 'logs', 'ps', 'initdb')]
    [string]$Command = 'up',

    [Parameter(Position = 1, ValueFromRemainingArguments = $true)]
    [string[]]$Rest,

    [switch]$Force
)

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

# 开启 BuildKit，使 Dockerfile 里的 --mount=type=cache 依赖缓存生效。
$env:DOCKER_BUILDKIT = '1'
$env:COMPOSE_DOCKER_CLI_BUILD = '1'

$EnvFile = '.env'
# 所有 compose 调用统一带上 --env-file，用户无需再手敲。
$Dc = @('compose', '--env-file', $EnvFile)

function Ensure-EnvFile {
    if (-not (Test-Path $EnvFile)) {
        if (-not (Test-Path '.env.example')) {
            throw "缺少 $EnvFile 且找不到 .env.example，无法继续。"
        }
        Copy-Item '.env.example' $EnvFile
        Write-Host "已从示例生成 $EnvFile —— 请检查里面的密码/端口后重新运行本脚本。" -ForegroundColor Yellow
        exit 1
    }
}

function Assert-Docker {
    docker info *> $null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "无法连接 Docker。请先启动 Docker Desktop，等它完全就绪（托盘图标显示 running）后重试。" -ForegroundColor Red
        exit 1
    }
}

function Get-EnvValue([string]$Key, [string]$Default) {
    $val = $Default
    foreach ($line in Get-Content $EnvFile) {
        if ($line -match "^\s*$([regex]::Escape($Key))\s*=\s*(.*)$") {
            $val = $Matches[1].Trim()
        }
    }
    return $val
}

function Wait-Postgres {
    $user = Get-EnvValue 'POSTGRES_USER' 'postgres'
    Write-Host "等待 postgres 就绪..." -ForegroundColor Cyan
    for ($i = 0; $i -lt 30; $i++) {
        docker @Dc exec -T postgres pg_isready -U $user *> $null
        if ($LASTEXITCODE -eq 0) { return }
        Start-Sleep -Seconds 2
    }
    throw "postgres 在 60 秒内仍未就绪，请检查 'docker @Dc logs postgres'。"
}

function Initialize-Db {
    $user = Get-EnvValue 'POSTGRES_USER' 'postgres'
    $db = Get-EnvValue 'POSTGRES_DB'   'dlsc'

    Wait-Postgres

    if (-not $Force) {
        # 用 pot_application 表是否存在来判断库是否已初始化。
        $exists = docker @Dc exec -T postgres psql -U $user -d $db -tAc "SELECT to_regclass('public.pot_application')" 2>$null
        if ($exists -match 'pot_application') {
            Write-Host "数据库已初始化，跳过导入（如需强制重导用 -Force）。" -ForegroundColor Green
            return
        }
    }

    # 合并基线：单一文件，含全部 schema + 数据 + 种子应用（由 pg_dump 生成，PG 合法）。
    $sqls = @('docker/db/init.sql')
    Write-Host "导入数据库初始化脚本到 [$db] ..." -ForegroundColor Cyan
    foreach ($s in $sqls) {
        if (-not (Test-Path $s)) {
            Write-Host "  跳过（不存在）: $s" -ForegroundColor DarkYellow
            continue
        }
        Write-Host "  导入 $s" -ForegroundColor DarkCyan
        Get-Content $s -Raw | docker @Dc exec -T postgres psql -U $user -d $db -v ON_ERROR_STOP=1 1> $null
        if ($LASTEXITCODE -ne 0) {
            throw "导入 $s 失败，请检查上面的 psql 报错。"
        }
    }
    Write-Host "数据库初始化完成。" -ForegroundColor Green
}

Assert-Docker

switch ($Command) {
    'up' {
        Ensure-EnvFile
        Write-Host "[1/3] 启动基础服务 (postgres / redis / minio)..." -ForegroundColor Magenta
        docker @Dc up -d postgres redis minio
        if ($LASTEXITCODE -ne 0) { throw "启动基础服务失败，请检查上面的报错。" }
        Write-Host "[2/3] 初始化数据库..." -ForegroundColor Magenta
        Initialize-Db
        Write-Host "[3/3] 构建并启动全部服务..." -ForegroundColor Magenta
        docker @Dc up -d --build
        Write-Host "`n完成。访问网关： http://localhost:$(Get-EnvValue 'GATEWAY_PORT' '18080')" -ForegroundColor Green
    }
    'rebuild' {
        Ensure-EnvFile
        docker @Dc up -d --build @Rest
    }
    'restart' {
        Ensure-EnvFile
        docker @Dc restart @Rest
    }
    'down' {
        docker @Dc down
    }
    'reset' {
        Write-Host "将停止服务并删除 postgres/redis/minio 数据卷（清空数据）。" -ForegroundColor Yellow
        docker @Dc down -v
    }
    'logs' {
        docker @Dc logs -f --tail=100 @Rest
    }
    'ps' {
        docker @Dc ps
    }
    'initdb' {
        Ensure-EnvFile
        Initialize-Db
    }
}
