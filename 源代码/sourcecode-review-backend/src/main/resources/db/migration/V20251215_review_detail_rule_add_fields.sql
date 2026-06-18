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

 Date: 12/15/2025 14:13:58
*/

ALTER TABLE "public"."dmsc_rule"
    ADD COLUMN "explain" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying;

COMMENT ON COLUMN "public"."dmsc_rule"."explain" IS '机理说明';

ALTER TABLE "public"."dmsc_review_result_detail"
    ADD COLUMN "rule_code" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying;

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."rule_code" IS '规则编号';

