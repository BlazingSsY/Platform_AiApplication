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

 Date: 10/18/2025 14:13:58
*/
ALTER TABLE "public"."dmsc_review_result"
    ADD COLUMN "duration" int4;

COMMENT ON COLUMN "public"."dmsc_review_result"."duration" IS '耗时(秒)';