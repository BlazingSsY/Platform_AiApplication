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

 Date: 05/01/2026 13:20:47
*/


-- ----------------------------
-- Table structure for code_yjfk_answer
-- ----------------------------
DROP TABLE IF EXISTS "public"."code_yjfk_answer";
CREATE TABLE "public"."code_yjfk_answer" (
                                             "id" int8 NOT NULL,
                                             "f_id" int8 NOT NULL,
                                             "answer" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                             "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                             "is_delete" int2 NOT NULL DEFAULT 0,
                                             "version" int4 DEFAULT 0,
                                             "create_date" timestamp(6),
                                             "update_date" timestamp(6) DEFAULT now(),
                                             "create_user" int8,
                                             "update_user" int8,
                                             "ref_id" int8
)
;
COMMENT ON COLUMN "public"."code_yjfk_answer"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."code_yjfk_answer"."f_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."code_yjfk_answer"."answer" IS '回复内容';
COMMENT ON COLUMN "public"."code_yjfk_answer"."comments" IS '备注';
COMMENT ON COLUMN "public"."code_yjfk_answer"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."code_yjfk_answer"."version" IS '版本号';
COMMENT ON COLUMN "public"."code_yjfk_answer"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."code_yjfk_answer"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."code_yjfk_answer"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."code_yjfk_answer"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."code_yjfk_answer"."ref_id" IS '参照的回复ID(外键,关联到当前表)';

-- ----------------------------
-- Table structure for code_yjfk_append_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."code_yjfk_append_file";
CREATE TABLE "public"."code_yjfk_append_file" (
                                                  "id" int8 NOT NULL,
                                                  "f_id" int8 NOT NULL,
                                                  "type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                                  "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                                  "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                  "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                  "is_delete" int2 NOT NULL DEFAULT 0,
                                                  "version" int4 DEFAULT 0,
                                                  "create_date" timestamp(6),
                                                  "update_date" timestamp(6) DEFAULT now(),
                                                  "create_user" int8,
                                                  "update_user" int8
)
;
COMMENT ON COLUMN "public"."code_yjfk_append_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."f_id" IS '外键,关联到主记录表';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."type" IS '附件类型';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."code_yjfk_append_file"."update_user" IS '最后更新人';

-- ----------------------------
-- Table structure for code_yjfk_suggestion
-- ----------------------------
DROP TABLE IF EXISTS "public"."code_yjfk_suggestion";
CREATE TABLE "public"."code_yjfk_suggestion" (
                                                 "id" int8 NOT NULL,
                                                 "title" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                 "suggestion" text COLLATE "pg_catalog"."default",
                                                 "status" int4,
                                                 "description" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                 "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                 "is_delete" int2 NOT NULL DEFAULT 0,
                                                 "version" int4 DEFAULT 0,
                                                 "create_date" timestamp(6),
                                                 "update_date" timestamp(6) DEFAULT now(),
                                                 "create_user" int8,
                                                 "update_user" int8,
                                                 "file_id" int8,
                                                 "file_version_id" int8,
                                                 "result_id" int8
)
;
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."title" IS '标题';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."suggestion" IS '反馈意见';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."status" IS '反馈状态(1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."description" IS '意见描述';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."comments" IS '备注';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."version" IS '版本号';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."file_id" IS '文件ID';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."file_version_id" IS '文件版本ID';
COMMENT ON COLUMN "public"."code_yjfk_suggestion"."result_id" IS '审查结果ID';

-- ----------------------------
-- Table structure for code_yjfk_suggestion_status
-- ----------------------------
DROP TABLE IF EXISTS "public"."code_yjfk_suggestion_status";
CREATE TABLE "public"."code_yjfk_suggestion_status" (
                                                        "id" int8 NOT NULL,
                                                        "suggestion_id" int8 DEFAULT NULL,
                                                        "status" int4,
                                                        "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                        "is_delete" int2 NOT NULL DEFAULT 0,
                                                        "version" int4 DEFAULT 0,
                                                        "create_date" timestamp(6),
                                                        "update_date" timestamp(6) DEFAULT now(),
                                                        "create_user" int8,
                                                        "update_user" int8
)
;
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."suggestion_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."status" IS '反馈状态(0:new;1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."comments" IS '备注';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."version" IS '版本号';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."code_yjfk_suggestion_status"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table code_yjfk_answer
-- ----------------------------
CREATE INDEX "code_answer_create_date_index" ON "public"."code_yjfk_answer" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX "code_answer_fid_index" ON "public"."code_yjfk_answer" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "code_answer_update_date_index" ON "public"."code_yjfk_answer" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table code_yjfk_answer
-- ----------------------------
ALTER TABLE "public"."code_yjfk_answer" ADD CONSTRAINT "code_yjfk_answer_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table code_yjfk_append_file
-- ----------------------------
CREATE INDEX "code_append_file_fid_index" ON "public"."code_yjfk_append_file" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "code_append_file_type_index" ON "public"."code_yjfk_append_file" USING btree (
    "type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table code_yjfk_append_file
-- ----------------------------
ALTER TABLE "public"."code_yjfk_append_file" ADD CONSTRAINT "code_yjfk_append_file_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table code_yjfk_suggestion
-- ----------------------------
CREATE INDEX "code_suggestion_create_date_index" ON "public"."code_yjfk_suggestion" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX "code_suggestion_update_date_index" ON "public"."code_yjfk_suggestion" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table code_yjfk_suggestion
-- ----------------------------
ALTER TABLE "public"."code_yjfk_suggestion" ADD CONSTRAINT "code_yjfk_suggestion_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table code_yjfk_suggestion_status
-- ----------------------------
CREATE INDEX "code_suggestion_status_create_date_index" ON "public"."code_yjfk_suggestion_status" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table code_yjfk_suggestion_status
-- ----------------------------
ALTER TABLE "public"."code_yjfk_suggestion_status" ADD CONSTRAINT "code_yjfk_suggestion_status_pkey" PRIMARY KEY ("id");
