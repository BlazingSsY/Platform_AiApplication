/*
 Navicat Premium Data Transfer

 Source Server         : 100.64.0.3
 Source Server Type    : PostgreSQL
 Source Server Version : 90204
 Source Host           : 100.64.0.3:15432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90204
 File Encoding         : 65001

 Date: 06/09/2025 14:13:58
*/
ALTER TABLE "public"."urm_user"
    ADD COLUMN "email" varchar(500);

COMMENT ON COLUMN "public"."urm_user"."email" IS '邮箱';