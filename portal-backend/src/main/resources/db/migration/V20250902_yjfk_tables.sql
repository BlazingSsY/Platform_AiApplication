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

 Date: 28/08/2025 14:13:58
*/


-- ----------------------------
-- Table structure for yjfk_suggestion_status
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_suggestion_status";
CREATE TABLE "public"."yjfk_suggestion_status" (
                                                   "id" int8 NOT NULL,
                                                   "suggestion_id" int8 DEFAULT NULL::character varying,
                                                   "status" int4,
                                                   "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                   "is_delete" int2 NOT NULL DEFAULT 0,
                                                   "version" int4 DEFAULT 0,
                                                   "create_date" timestamp(6),
                                                   "update_date" timestamp(6) DEFAULT now(),
                                                   "create_user" int8,
                                                   "update_user" int8
)
;
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."suggestion_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."status" IS '反馈状态(0:new;1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_suggestion_status"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table yjfk_suggestion_status
-- ----------------------------
CREATE INDEX "suggestion_status_create_date_index" ON "public"."yjfk_suggestion_status" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_suggestion_status
-- ----------------------------
ALTER TABLE "public"."yjfk_suggestion_status" ADD CONSTRAINT "yjfk_suggestion_status_pkey" PRIMARY KEY ("id");


ALTER TABLE "public"."yjfk_answer"
    ADD COLUMN "ref_id" int8;

COMMENT ON COLUMN "public"."yjfk_answer"."ref_id" IS '参照的回复ID(外键,关联到当前表)';