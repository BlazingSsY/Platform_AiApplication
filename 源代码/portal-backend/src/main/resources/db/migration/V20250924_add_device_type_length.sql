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
ALTER TABLE "public"."dlsc_review_result_detail"
ALTER COLUMN "device_type" TYPE varchar(2000) COLLATE "pg_catalog"."default";