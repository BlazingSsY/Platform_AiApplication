# circuit-review-backend - 数智平台后端服务

## 项目简介

Circuit Review Backend 是一个基于 Spring Boot 的数智平台后端服务。

### 核心功能模块

- **电路审查后台服务** - 提供电路文件上传、管理、多版本管理、电路审查、审查结果查询及审查结果统计功能

### 技术架构

- **框架**: Spring Boot 2.7.5
- **数据库**: OpenGauss数据库
- **ORM**: PostgreSQL
- **文件存储**: MinIO 对象存储
- **API文档**: Swagger/OpenAPI 3
- **认证**: JWT Token
- **构建工具**: Maven
- **容器化**: Docker

## 环境要求

### 开发环境

- **JDK**: 17 或更高版本
- **Maven**: 3.6+
- **IDE**: IntelliJ IDEA 或 Eclipse (推荐 IntelliJ IDEA)

### 运行环境

- **Java Runtime**: OpenJDK 17+
- **数据库**: OpenGauss数据库
- **对象存储**: MinIO 服务
- **操作系统**: Linux/Windows/macOS
