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

 Date: 08/05/2026 14:31:03
*/


-- ----------------------------
-- Table structure for ljsc_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."ljsc_file";
CREATE TABLE "public"."ljsc_file" (
                                      "id" int8 NOT NULL DEFAULT 4,
                                      "minio_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                      "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "file_path" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "file_save_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "secret_level" int2 NOT NULL DEFAULT 4,
                                      "department_id" int8 DEFAULT NULL::character varying,
                                      "owner_id" int8 DEFAULT NULL::character varying,
                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "is_recycle" int2 NOT NULL DEFAULT 0,
                                      "is_delete" int2 NOT NULL DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8,
                                      "compatible_models" varchar(500) COLLATE "pg_catalog"."default",
                                      "product_model" varchar(500) COLLATE "pg_catalog"."default",
                                      "product_name" varchar(500) COLLATE "pg_catalog"."default",
                                      "config_name" varchar(500) COLLATE "pg_catalog"."default",
                                      "code_file_version" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."ljsc_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."ljsc_file"."minio_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."ljsc_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."ljsc_file"."file_path" IS '服务器上文件保存的路径';
COMMENT ON COLUMN "public"."ljsc_file"."file_save_name" IS '服务器上文件保存的名称';
COMMENT ON COLUMN "public"."ljsc_file"."secret_level" IS '密级(1:内部;2:受控;3:商密;4:公开)';
COMMENT ON COLUMN "public"."ljsc_file"."department_id" IS '文件隶属部门Id';
COMMENT ON COLUMN "public"."ljsc_file"."owner_id" IS '文件用户Id';
COMMENT ON COLUMN "public"."ljsc_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."ljsc_file"."is_recycle" IS '是否移入文件回收站(1:是; 0:否)';
COMMENT ON COLUMN "public"."ljsc_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."ljsc_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."ljsc_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."ljsc_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."ljsc_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."ljsc_file"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."ljsc_file"."compatible_models" IS '配套机型';
COMMENT ON COLUMN "public"."ljsc_file"."product_model" IS '产品型号';
COMMENT ON COLUMN "public"."ljsc_file"."product_name" IS '产品名称';
COMMENT ON COLUMN "public"."ljsc_file"."config_name" IS '配置项名称';
COMMENT ON COLUMN "public"."ljsc_file"."code_file_version" IS '代码版本';

-- ----------------------------
-- Table structure for ljsc_file_version
-- ----------------------------
DROP TABLE IF EXISTS "public"."ljsc_file_version";
CREATE TABLE "public"."ljsc_file_version" (
                                              "id" int8 NOT NULL DEFAULT 4,
                                              "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                              "minio_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                              "file_version" int4 DEFAULT 1,
                                              "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                              "file_origin_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                              "secret_level" int2 NOT NULL DEFAULT 4,
                                              "department_id" int8 DEFAULT NULL::character varying,
                                              "owner_id" int8 DEFAULT NULL::character varying,
                                              "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                              "is_recycle" int2 NOT NULL DEFAULT 0,
                                              "is_delete" int2 NOT NULL DEFAULT 0,
                                              "version" int4 DEFAULT 0,
                                              "create_date" timestamp(6),
                                              "update_date" timestamp(6) DEFAULT now(),
                                              "create_user" int8,
                                              "update_user" int8
)
;
COMMENT ON COLUMN "public"."ljsc_file_version"."id" IS '角色ID（UUID）';
COMMENT ON COLUMN "public"."ljsc_file_version"."file_id" IS '所属文件ID,关联到文件表';
COMMENT ON COLUMN "public"."ljsc_file_version"."minio_id" IS '文件在minio的ID';
COMMENT ON COLUMN "public"."ljsc_file_version"."file_version" IS '文件版本序号';
COMMENT ON COLUMN "public"."ljsc_file_version"."file_name" IS '文件版本名称';
COMMENT ON COLUMN "public"."ljsc_file_version"."file_origin_name" IS '文件上传时的原始名称';
COMMENT ON COLUMN "public"."ljsc_file_version"."secret_level" IS '密级(1:内部;2:受控;3:商密;4:公开)';
COMMENT ON COLUMN "public"."ljsc_file_version"."department_id" IS '文件隶属部门Id';
COMMENT ON COLUMN "public"."ljsc_file_version"."owner_id" IS '文件用户Id';
COMMENT ON COLUMN "public"."ljsc_file_version"."comments" IS '备注';
COMMENT ON COLUMN "public"."ljsc_file_version"."is_recycle" IS '是否移入文件回收站(1:是; 0:否)';
COMMENT ON COLUMN "public"."ljsc_file_version"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."ljsc_file_version"."version" IS '版本号';
COMMENT ON COLUMN "public"."ljsc_file_version"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."ljsc_file_version"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."ljsc_file_version"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."ljsc_file_version"."update_user" IS '最后更新人';

-- ----------------------------
-- Table structure for ljsc_review_result
-- ----------------------------
DROP TABLE IF EXISTS "public"."ljsc_review_result";
CREATE TABLE "public"."ljsc_review_result" (
                                               "id" int8 NOT NULL,
                                               "file_id" int8 NOT NULL,
                                               "file_version_id" int8 NOT NULL,
                                               "check_points" int4 DEFAULT NULL::character varying,
                                               "pass_check_points" int4 DEFAULT NULL::character varying,
                                               "pass_rate" numeric(15,5) DEFAULT NULL::character varying,
                                               "is_closed_loop" int2 NOT NULL DEFAULT 0,
                                               "review_time" timestamp(6) DEFAULT NULL::character varying,
                                               "status" int4,
                                               "error_message" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                               "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                               "is_delete" int2 NOT NULL DEFAULT 0,
                                               "version" int4 DEFAULT 0,
                                               "create_date" timestamp(6),
                                               "update_date" timestamp(6) DEFAULT now(),
                                               "create_user" int8,
                                               "update_user" int8,
                                               "duration" int4,
                                               "files_size" int4,
                                               "files_line" int4,
                                               "use_rule_size" int4,
                                               "questions" int4
)
;
COMMENT ON COLUMN "public"."ljsc_review_result"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."ljsc_review_result"."file_id" IS '外键,关联到文件表';
COMMENT ON COLUMN "public"."ljsc_review_result"."file_version_id" IS '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."ljsc_review_result"."check_points" IS '检查点数量';
COMMENT ON COLUMN "public"."ljsc_review_result"."pass_check_points" IS '通过的检查点数量';
COMMENT ON COLUMN "public"."ljsc_review_result"."pass_rate" IS '通过率';
COMMENT ON COLUMN "public"."ljsc_review_result"."is_closed_loop" IS '闭环状态';
COMMENT ON COLUMN "public"."ljsc_review_result"."review_time" IS '开始审查的时间';
COMMENT ON COLUMN "public"."ljsc_review_result"."status" IS '状态(1:正在审查;2:审查完成)';
COMMENT ON COLUMN "public"."ljsc_review_result"."error_message" IS '底层API错误信息';
COMMENT ON COLUMN "public"."ljsc_review_result"."comments" IS '备注';
COMMENT ON COLUMN "public"."ljsc_review_result"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."ljsc_review_result"."version" IS '版本号';
COMMENT ON COLUMN "public"."ljsc_review_result"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."ljsc_review_result"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."ljsc_review_result"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."ljsc_review_result"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."ljsc_review_result"."duration" IS '耗时(秒)';
COMMENT ON COLUMN "public"."ljsc_review_result"."files_size" IS '文件数';
COMMENT ON COLUMN "public"."ljsc_review_result"."files_line" IS '文件行数';
COMMENT ON COLUMN "public"."ljsc_review_result"."use_rule_size" IS '使用规则数';
COMMENT ON COLUMN "public"."ljsc_review_result"."questions" IS '问题数量';

-- ----------------------------
-- Table structure for ljsc_review_result_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."ljsc_review_result_detail";
CREATE TABLE "public"."ljsc_review_result_detail" (
                                                      "id" int8 NOT NULL,
                                                      "result_id" int8,
                                                      "rule_id" int8,
                                                      "source_file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "language" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "error_code" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "line_number" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "error_reason" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "review_suggestion" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "is_passed" int2,
                                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "is_delete" int2 NOT NULL DEFAULT 0,
                                                      "version" int4 DEFAULT 0,
                                                      "create_date" timestamp(6) NOT NULL,
                                                      "update_date" timestamp(6) DEFAULT now(),
                                                      "create_user" int8,
                                                      "update_user" int8,
                                                      "rule_code" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "question_id" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "recheck_conclusion" varchar(500) COLLATE "pg_catalog"."default",
                                                      "question_desc" text COLLATE "pg_catalog"."default",
                                                      "recheck_status" int4 DEFAULT 0,
                                                      "recheck_result_status" int4 DEFAULT 0,
                                                      "reject_reason" varchar(500) COLLATE "pg_catalog"."default",
                                                      "recheck_user_id" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."result_id" IS '外键,关联到结果表';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."rule_id" IS '外键,关联到规则表';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."source_file_name" IS '源代码文件名';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."language" IS '代码语言';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."error_code" IS '错误原因';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."line_number" IS '代码行号';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."review_suggestion" IS '审查意见';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."is_passed" IS '是否通过(0:未通过;1:通过)';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."comments" IS '备注';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."version" IS '版本号';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."rule_code" IS '规则编号';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."question_id" IS '问题ID';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."recheck_conclusion" IS '审查结论';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."question_desc" IS '问题描述';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."recheck_status" IS '复核状态(1:未复核;2:复核中;3:复核完成)';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."recheck_result_status" IS '复核结果状态(1:通过;2:未通过)';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."reject_reason" IS '拒绝理由';
COMMENT ON COLUMN "public"."ljsc_review_result_detail"."recheck_user_id" IS '复核处理用户Id';

-- ----------------------------
-- Table structure for ljsc_rule
-- ----------------------------
DROP TABLE IF EXISTS "public"."ljsc_rule";
CREATE TABLE "public"."ljsc_rule" (
                                      "id" int8 NOT NULL,
                                      "type" varchar(500) COLLATE "pg_catalog"."default",
                                      "name" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL::character varying,
                                      "code" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL::character varying,
                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "is_delete" int2 NOT NULL DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8,
                                      "explain" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying
)
;
COMMENT ON COLUMN "public"."ljsc_rule"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."ljsc_rule"."type" IS '规则类型';
COMMENT ON COLUMN "public"."ljsc_rule"."name" IS '规则名称';
COMMENT ON COLUMN "public"."ljsc_rule"."code" IS '规则编号';
COMMENT ON COLUMN "public"."ljsc_rule"."comments" IS '备注';
COMMENT ON COLUMN "public"."ljsc_rule"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."ljsc_rule"."version" IS '版本号';
COMMENT ON COLUMN "public"."ljsc_rule"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."ljsc_rule"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."ljsc_rule"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."ljsc_rule"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."ljsc_rule"."explain" IS '机理说明';

-- ----------------------------
-- Table structure for ljsc_tool_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."ljsc_tool_file";
CREATE TABLE "public"."ljsc_tool_file" (
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
COMMENT ON COLUMN "public"."ljsc_tool_file"."id" IS '角色ID（UUID）';
COMMENT ON COLUMN "public"."ljsc_tool_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."ljsc_tool_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."ljsc_tool_file"."tool_name" IS '工具名称';
COMMENT ON COLUMN "public"."ljsc_tool_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."ljsc_tool_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."ljsc_tool_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."ljsc_tool_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."ljsc_tool_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."ljsc_tool_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."ljsc_tool_file"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table ljsc_file
-- ----------------------------
CREATE INDEX "logic_file_minio_id_index" ON "public"."ljsc_file" USING btree (
    "minio_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table ljsc_file
-- ----------------------------
ALTER TABLE "public"."ljsc_file" ADD CONSTRAINT "dmsc_file_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ljsc_file_version
-- ----------------------------
ALTER TABLE "public"."ljsc_file_version" ADD CONSTRAINT "dmsc_file_version_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ljsc_review_result
-- ----------------------------
ALTER TABLE "public"."ljsc_review_result" ADD CONSTRAINT "dmsc_review_result_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ljsc_review_result_detail
-- ----------------------------
ALTER TABLE "public"."ljsc_review_result_detail" ADD CONSTRAINT "dmsc_review_result_detail_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ljsc_rule
-- ----------------------------
ALTER TABLE "public"."ljsc_rule" ADD CONSTRAINT "dmsc_rule_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ljsc_tool_file
-- ----------------------------
ALTER TABLE "public"."ljsc_tool_file" ADD CONSTRAINT "dmsc_tool_file_copy1_pkey" PRIMARY KEY ("id");


-- ----------------------------
-- Table structure for logic_yjfk_answer
-- ----------------------------
DROP TABLE IF EXISTS "public"."logic_yjfk_answer";
CREATE TABLE "public"."logic_yjfk_answer" (
                                              "id" int8 NOT NULL,
                                              "f_id" int8 NOT NULL,
                                              "answer" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                              "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                              "is_delete" int2 NOT NULL DEFAULT 0,
                                              "version" int4 DEFAULT 0,
                                              "create_date" timestamp(6),
                                              "update_date" timestamp(6) DEFAULT now(),
                                              "create_user" int8,
                                              "update_user" int8,
                                              "ref_id" int8
)
;
COMMENT ON COLUMN "public"."logic_yjfk_answer"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."f_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."answer" IS '回复内容';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."comments" IS '备注';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."version" IS '版本号';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."logic_yjfk_answer"."ref_id" IS '参照的回复ID(外键,关联到当前表)';

-- ----------------------------
-- Table structure for logic_yjfk_append_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."logic_yjfk_append_file";
CREATE TABLE "public"."logic_yjfk_append_file" (
                                                   "id" int8 NOT NULL,
                                                   "f_id" int8 NOT NULL,
                                                   "type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                                   "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                                   "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                   "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                   "is_delete" int2 NOT NULL DEFAULT 0,
                                                   "version" int4 DEFAULT 0,
                                                   "create_date" timestamp(6),
                                                   "update_date" timestamp(6) DEFAULT now(),
                                                   "create_user" int8,
                                                   "update_user" int8
)
;
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."f_id" IS '外键,关联到主记录表';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."type" IS '附件类型';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."logic_yjfk_append_file"."update_user" IS '最后更新人';

-- ----------------------------
-- Table structure for logic_yjfk_suggestion
-- ----------------------------
DROP TABLE IF EXISTS "public"."logic_yjfk_suggestion";
CREATE TABLE "public"."logic_yjfk_suggestion" (
                                                  "id" int8 NOT NULL,
                                                  "title" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                  "suggestion" text COLLATE "pg_catalog"."default",
                                                  "status" int4,
                                                  "description" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                  "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
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
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."title" IS '标题';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."suggestion" IS '反馈意见';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."status" IS '反馈状态(1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."description" IS '意见描述';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."comments" IS '备注';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."version" IS '版本号';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."file_id" IS '文件ID';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."file_version_id" IS '文件版本ID';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion"."result_id" IS '审查结果ID';

-- ----------------------------
-- Table structure for logic_yjfk_suggestion_status
-- ----------------------------
DROP TABLE IF EXISTS "public"."logic_yjfk_suggestion_status";
CREATE TABLE "public"."logic_yjfk_suggestion_status" (
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
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."suggestion_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."status" IS '反馈状态(0:new;1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."comments" IS '备注';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."version" IS '版本号';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."logic_yjfk_suggestion_status"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table logic_yjfk_answer
-- ----------------------------
CREATE INDEX "logic_answer_create_date_index" ON "public"."logic_yjfk_answer" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX "logic_answer_fid_index" ON "public"."logic_yjfk_answer" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "logic_answer_update_date_index" ON "public"."logic_yjfk_answer" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table logic_yjfk_answer
-- ----------------------------
ALTER TABLE "public"."logic_yjfk_answer" ADD CONSTRAINT "code_yjfk_answer_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table logic_yjfk_append_file
-- ----------------------------
CREATE INDEX "logic_append_file_fid_index" ON "public"."logic_yjfk_append_file" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "logic_append_file_type_index" ON "public"."logic_yjfk_append_file" USING btree (
    "type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table logic_yjfk_append_file
-- ----------------------------
ALTER TABLE "public"."logic_yjfk_append_file" ADD CONSTRAINT "code_yjfk_append_file_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table logic_yjfk_suggestion
-- ----------------------------
CREATE INDEX "logic_suggestion_create_date_index" ON "public"."logic_yjfk_suggestion" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX "logic_suggestion_update_date_index" ON "public"."logic_yjfk_suggestion" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table logic_yjfk_suggestion
-- ----------------------------
ALTER TABLE "public"."logic_yjfk_suggestion" ADD CONSTRAINT "code_yjfk_suggestion_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table logic_yjfk_suggestion_status
-- ----------------------------
CREATE INDEX "logic_suggestion_status_create_date_index" ON "public"."logic_yjfk_suggestion_status" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table logic_yjfk_suggestion_status
-- ----------------------------
ALTER TABLE "public"."logic_yjfk_suggestion_status" ADD CONSTRAINT "code_yjfk_suggestion_status_copy1_pkey" PRIMARY KEY ("id");

