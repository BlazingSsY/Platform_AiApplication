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

 Date: 12/14/2025 14:13:58
*/
ALTER TABLE "public"."dmsc_review_result"
    ADD COLUMN "files_size" int4,
  ADD COLUMN "files_line" int4,
  ADD COLUMN "use_rule_size" int4,
  ADD COLUMN "questions" int4;

COMMENT ON COLUMN "public"."dmsc_review_result"."files_size" IS '文件数';

COMMENT ON COLUMN "public"."dmsc_review_result"."files_line" IS '文件行数';

COMMENT ON COLUMN "public"."dmsc_review_result"."use_rule_size" IS '使用规则数';

COMMENT ON COLUMN "public"."dmsc_review_result"."questions" IS '问题数量';

