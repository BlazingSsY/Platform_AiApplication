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

 Date: 10/21/2025 14:13:58
*/
ALTER TABLE "public"."yjfk_suggestion"
    ADD COLUMN "file_id" int8,
  ADD COLUMN "file_version_id" int8,
  ADD COLUMN "result_id" int8;

COMMENT ON COLUMN "public"."yjfk_suggestion"."file_id" IS '文件ID';

COMMENT ON COLUMN "public"."yjfk_suggestion"."file_version_id" IS '文件版本ID';

COMMENT ON COLUMN "public"."yjfk_suggestion"."result_id" IS '审查结果ID';