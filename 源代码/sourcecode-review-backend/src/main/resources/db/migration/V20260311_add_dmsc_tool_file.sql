/*
 Navicat Premium Data Transfer

 Source Server         : 100.64.0.3
 Source Server Type    : PostgreSQL
 Source Server Version : 90204
 Source Host           : 100.64.0.3:15432
 Source Catalog        : dlsc
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90204
 File Encoding         : 65001

 Date: 11/03/2026 11:26:47
*/


-- ----------------------------
-- Table structure for dmsc_tool_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."dmsc_tool_file";
CREATE TABLE "public"."dmsc_tool_file" (
                                           "id" int8 NOT NULL DEFAULT 4,
                                           "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                           "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "tool_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "is_delete" int2 NOT NULL DEFAULT 0,
                                           "version" int4 DEFAULT 0,
                                           "create_date" timestamp(6),
                                           "update_date" timestamp(6) DEFAULT now(),
                                           "create_user" int8,
                                           "update_user" int8
)
;
COMMENT ON COLUMN "public"."dmsc_tool_file"."id" IS '角色ID（UUID）';
COMMENT ON COLUMN "public"."dmsc_tool_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."dmsc_tool_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."dmsc_tool_file"."tool_name" IS '工具名称';
COMMENT ON COLUMN "public"."dmsc_tool_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."dmsc_tool_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dmsc_tool_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."dmsc_tool_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dmsc_tool_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dmsc_tool_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dmsc_tool_file"."update_user" IS '最后更新人';

-- ----------------------------
-- Primary Key structure for table dmsc_tool_file
-- ----------------------------
ALTER TABLE "public"."dmsc_tool_file" ADD CONSTRAINT "dlsc_tool_file_copy1_pkey" PRIMARY KEY ("id");
