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

 Date: 15/07/2025 16:47:14
*/


-- ----------------------------
-- Table structure for dlsc_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_file";
CREATE TABLE "public"."dlsc_file" (
                                      "id" int8 NOT NULL DEFAULT 4,
                                      "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                      "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "file_path" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "file_save_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "secret_level" int2 NOT NULL DEFAULT 4,
                                      "department_id" int8 DEFAULT NULL::character varying,
                                      "owner_id" int8 DEFAULT NULL::character varying,
                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "is_recycle" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."dlsc_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."dlsc_file"."file_path" IS '服务器上文件保存的路径';
COMMENT ON COLUMN "public"."dlsc_file"."file_save_name" IS '服务器上文件保存的名称';
COMMENT ON COLUMN "public"."dlsc_file"."secret_level" IS '密级(1:内部;2:受控;3:商密;4:公开)';
COMMENT ON COLUMN "public"."dlsc_file"."department_id" IS '文件隶属部门Id';
COMMENT ON COLUMN "public"."dlsc_file"."owner_id" IS '文件用户Id';
COMMENT ON COLUMN "public"."dlsc_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_file"."is_recycle" IS '是否移入文件回收站(1:是; 0:否)';
COMMENT ON COLUMN "public"."dlsc_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_file"."update_user" IS '最后更新人';


-- ----------------------------
-- Table structure for dlsc_file_version
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_file_version";
CREATE TABLE "public"."dlsc_file_version" (
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
                                      "is_recycle" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_file_version"."id" IS '角色ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_file_version"."file_id" IS '所属文件ID,关联到文件表';
COMMENT ON COLUMN "public"."dlsc_file_version"."minio_id" IS '文件在minio的ID';
COMMENT ON COLUMN "public"."dlsc_file_version"."file_version" IS '文件版本序号';
COMMENT ON COLUMN "public"."dlsc_file_version"."file_name" IS '文件版本名称';
COMMENT ON COLUMN "public"."dlsc_file_version"."file_origin_name" IS '文件上传时的原始名称';
COMMENT ON COLUMN "public"."dlsc_file_version"."secret_level" IS '密级(1:内部;2:受控;3:商密;4:公开)';
COMMENT ON COLUMN "public"."dlsc_file_version"."department_id" IS '文件隶属部门Id';
COMMENT ON COLUMN "public"."dlsc_file_version"."owner_id" IS '文件用户Id';
COMMENT ON COLUMN "public"."dlsc_file_version"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_file_version"."is_recycle" IS '是否移入文件回收站(1:是; 0:否)';
COMMENT ON COLUMN "public"."dlsc_file_version"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_file_version"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_file_version"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_file_version"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_file_version"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_file_version"."update_user" IS '最后更新人';

-- ----------------------------
-- Table structure for dlsc_review_result
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_review_result";
CREATE TABLE "public"."dlsc_review_result" (
                                               "id" int8 NOT NULL,
                                               "file_id" int8 NOT NULL,
                                               "file_version_id" int8 NOT NULL,
                                               "check_points" int4 DEFAULT NULL::character varying,
                                               "pass_check_points" int4 DEFAULT NULL::character varying,
                                               "pass_rate" numeric(15,5) DEFAULT NULL::character varying,
                                               "is_closed_loop" SMALLINT NOT NULL CHECK (is_closed_loop IN (0, 1)) DEFAULT 0,
                                               "review_time" timestamp(6) DEFAULT NULL::character varying,
                                               "status" int4,
                                               "error_message" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                               "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                               "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                               "version" int4 DEFAULT 0,
                                               "create_date" timestamp(6),
                                               "update_date" timestamp(6) DEFAULT now(),
                                               "create_user" int8,
                                               "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_review_result"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_review_result"."file_id" IS '外键,关联到文件表';
COMMENT ON COLUMN "public"."dlsc_review_result"."file_version_id" IS '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."dlsc_review_result"."check_points" IS '检查点数量';
COMMENT ON COLUMN "public"."dlsc_review_result"."pass_check_points" IS '通过的检查点数量';
COMMENT ON COLUMN "public"."dlsc_review_result"."pass_rate" IS '通过率';
COMMENT ON COLUMN "public"."dlsc_review_result"."is_closed_loop" IS '闭环状态';
COMMENT ON COLUMN "public"."dlsc_review_result"."review_time" IS '开始审查的时间';
COMMENT ON COLUMN "public"."dlsc_review_result"."status" IS '状态(1:正在审查;2:审查完成)';
COMMENT ON COLUMN "public"."dlsc_review_result"."error_message" IS 'Python API错误信息';
COMMENT ON COLUMN "public"."dlsc_review_result"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_review_result"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_review_result"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_review_result"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_review_result"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_review_result"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_review_result"."update_user" IS '最后更新人';

-- ----------------------------
-- Table structure for dlsc_review_result_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_review_result_detail";
CREATE TABLE "public"."dlsc_review_result_detail" (
                                                      "id" int8 NOT NULL,
                                                      "result_id" int8,
                                                      "rule_id" int8,
                                                      "device_type" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "tag_pin" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "review_suggestion" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "is_passed" SMALLINT CHECK (is_passed IN (0, 1)),
                                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                                      "version" int4 DEFAULT 0,
                                                      "create_date" timestamp(6),
                                                      "update_date" timestamp(6) DEFAULT now(),
                                                      "create_user" int8,
                                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."result_id" IS '外键,关联到结果表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."rule_id" IS '外键,关联到规则表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."device_type" IS '器件型号';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."tag_pin" IS '位号引脚';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."review_suggestion" IS '审查意见';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."is_passed" IS '是否通过(0:未通过;1:通过)';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_review_result_detail"."update_user" IS '最后更新人';

-- ----------------------------
-- Table structure for dlsc_rule
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_rule";
CREATE TABLE "public"."dlsc_rule" (
                                      "id" int8 NOT NULL,
                                      "type" int4,
                                      "name" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL::character varying,
                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_rule"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_rule"."type" IS '规则类型(1:通用规则;2:元器件手册规则;3:技术规则)';
COMMENT ON COLUMN "public"."dlsc_rule"."name" IS '规则名称';
COMMENT ON COLUMN "public"."dlsc_rule"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_rule"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_rule"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_rule"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_rule"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_rule"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_rule"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of dlsc_rule
-- ----------------------------

-- ----------------------------
-- Table structure for dlsc_tool_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_tool_file";
CREATE TABLE "public"."dlsc_tool_file" (
                                           "id" int8 NOT NULL DEFAULT 4,
                                           "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                           "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "tool_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                           "version" int4 DEFAULT 0,
                                           "create_date" timestamp(6),
                                           "update_date" timestamp(6) DEFAULT now(),
                                           "create_user" int8,
                                           "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_tool_file"."id" IS '工具文件ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_tool_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."dlsc_tool_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."dlsc_tool_file"."tool_name" IS '工具名称';
COMMENT ON COLUMN "public"."dlsc_tool_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_tool_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_tool_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_tool_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_tool_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_tool_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_tool_file"."update_user" IS '最后更新人';


-- ----------------------------
-- Primary Key structure for table dlsc_file
-- ----------------------------
ALTER TABLE "public"."dlsc_file" ADD CONSTRAINT "dlsc_file_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table dlsc_file_version
-- ----------------------------
ALTER TABLE "public"."dlsc_file_version" ADD CONSTRAINT "dlsc_file_version_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table dlsc_review_result
-- ----------------------------
ALTER TABLE "public"."dlsc_review_result" ADD CONSTRAINT "dlsc_review_result_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table dlsc_review_result_detail
-- ----------------------------
ALTER TABLE "public"."dlsc_review_result_detail" ADD CONSTRAINT "dlsc_review_result_detail_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table dlsc_rule
-- ----------------------------
ALTER TABLE "public"."dlsc_rule" ADD CONSTRAINT "dlsc_rule_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_department
-- ----------------------------
ALTER TABLE "public"."urm_department" ADD CONSTRAINT "urm_department_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_power
-- ----------------------------
ALTER TABLE "public"."urm_power" ADD CONSTRAINT "urm_power_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_role
-- ----------------------------
ALTER TABLE "public"."urm_role" ADD CONSTRAINT "urm_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_rolepower
-- ----------------------------
ALTER TABLE "public"."urm_rolepower" ADD CONSTRAINT "urm_rolepower_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_user
-- ----------------------------
ALTER TABLE "public"."urm_user" ADD CONSTRAINT "urm_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_userlog
-- ----------------------------
ALTER TABLE "public"."urm_userlog" ADD CONSTRAINT "urm_userlog_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_userrole
-- ----------------------------
ALTER TABLE "public"."urm_userrole" ADD CONSTRAINT "urm_userrole_pkey" PRIMARY KEY ("id");


ALTER TABLE "public"."dlsc_tool_file" ADD CONSTRAINT "dlsc_file_copy1_pkey" PRIMARY KEY ("id");



-- ----------------------------
-- 以下索引用于优化查询,再性能BUG复现后添加
-- ----------------------------

CREATE INDEX "userrole_user_id_index" ON "urm_userrole" USING btree (
    "user_id"
    );


CREATE INDEX "department_f_id_index" ON "urm_department" USING btree (
    "f_id"
    );

CREATE INDEX "department_name_index" ON "urm_department" USING btree (
    "name"
    );

CREATE INDEX "file_file_id_index" ON "dlsc_file" USING btree (
    "file_id"
    );

CREATE INDEX "file_department_id_index" ON "dlsc_file" USING btree (
    "department_id"
    );

CREATE INDEX "file_owner_id_index" ON "dlsc_file" USING btree (
    "owner_id"
    );

CREATE INDEX "file_is_recycle_index" ON "dlsc_file" USING btree (
    "is_recycle"
    );

CREATE INDEX "file_create_user_index" ON "dlsc_file" USING btree (
    "create_user"
    );

CREATE INDEX "file_version_file_id_index" ON "dlsc_file_version" USING btree (
    "file_id"
    );

CREATE INDEX "file_version_department_id_index" ON "dlsc_file_version" USING btree (
    "department_id"
    );

CREATE INDEX "file_version_owner_id_index" ON "dlsc_file_version" USING btree (
    "owner_id"
    );

CREATE INDEX "file_version_is_recycle_index" ON "dlsc_file_version" USING btree (
    "is_recycle"
    );

CREATE INDEX "file_version_create_user_index" ON "dlsc_file_version" USING btree (
    "create_user"
    );

CREATE INDEX "review_result_file_id_index" ON "dlsc_review_result" USING btree (
    "file_id"
    );

CREATE INDEX "review_result_file_id_index" ON "dlsc_review_result" USING btree (
    "file_version_id"
    );

CREATE INDEX "review_result_review_time_index" ON "dlsc_review_result" USING btree (
    "review_time"
    );

CREATE INDEX "review_result_status_index" ON "dlsc_review_result" USING btree (
    "status"
    );

CREATE INDEX "review_result_create_date_index" ON "dlsc_review_result" (
                                                                        "create_date"
    );


CREATE INDEX "review_result_detail_result_id_index" ON "dlsc_review_result_detail" (
                                                                                    "result_id"
    );

CREATE INDEX "review_result_detail_result_rule_id_index" ON "dlsc_review_result_detail" (
                                                                                         "rule_id"
    );

CREATE INDEX "review_result_detail_result_device_type_index" ON "dlsc_review_result_detail" (
                                                                                             "device_type"
    );

CREATE INDEX "review_result_detail_result_is_passed_index" ON "dlsc_review_result_detail" (
                                                                                           "is_passed"
    );

CREATE INDEX "review_result_detail_result_create_date_index" ON "dlsc_review_result_detail" (
                                                                                             "create_date"
    );


CREATE INDEX "rule_type_index" ON "dlsc_rule" USING btree (
    "type"
    );

CREATE INDEX "rule_create_date_index" ON "dlsc_rule" USING btree (
    "create_date"
    );


-- ----------------------------
-- Table structure for yjfk_answer
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_answer";
CREATE TABLE "public"."yjfk_answer" (
                                        "id" int8 NOT NULL,
                                        "f_id" int8 NOT NULL,
                                        "answer" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                        "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                        "is_delete" int2 NOT NULL DEFAULT 0,
                                        "version" int4 DEFAULT 0,
                                        "create_date" timestamp(6),
                                        "update_date" timestamp(6) DEFAULT now(),
                                        "create_user" int8,
                                        "update_user" int8
)
;
COMMENT ON COLUMN "public"."yjfk_answer"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_answer"."f_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."yjfk_answer"."answer" IS '回复内容';
COMMENT ON COLUMN "public"."yjfk_answer"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_answer"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_answer"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_answer"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_answer"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_answer"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_answer"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table yjfk_answer
-- ----------------------------

CREATE INDEX IF NOT EXISTS "answer_create_date_index" ON "public"."yjfk_answer" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "answer_fid_index" ON "public"."yjfk_answer" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "answer_update_date_index" ON "public"."yjfk_answer" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_answer
-- ----------------------------
ALTER TABLE "public"."yjfk_answer" ADD CONSTRAINT "yjfk_answer_pkey" PRIMARY KEY ("id");




-- ----------------------------
-- Table structure for yjfk_append_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_append_file";
CREATE TABLE "public"."yjfk_append_file" (
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
COMMENT ON COLUMN "public"."yjfk_append_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_append_file"."f_id" IS '外键,关联到主记录表';
COMMENT ON COLUMN "public"."yjfk_append_file"."type" IS '附件类型';
COMMENT ON COLUMN "public"."yjfk_append_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."yjfk_append_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."yjfk_append_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_append_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_append_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_append_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_append_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_append_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_append_file"."update_user" IS '最后更新人';


-- ----------------------------
-- Indexes structure for table yjfk_append_file
-- ----------------------------
CREATE INDEX IF NOT EXISTS "append_file_fid_index" ON "public"."yjfk_append_file" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "append_file_type_index" ON "public"."yjfk_append_file" USING btree (
    "type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_append_file
-- ----------------------------
ALTER TABLE "public"."yjfk_append_file" ADD CONSTRAINT "yjfk_append_file_pkey" PRIMARY KEY ("id");




-- ----------------------------
-- Table structure for yjfk_suggestion
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_suggestion";
CREATE TABLE "public"."yjfk_suggestion" (
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
                                            "update_user" int8
)
;
COMMENT ON COLUMN "public"."yjfk_suggestion"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_suggestion"."title" IS '标题';
COMMENT ON COLUMN "public"."yjfk_suggestion"."suggestion" IS '反馈意见';
COMMENT ON COLUMN "public"."yjfk_suggestion"."status" IS '反馈状态(1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."yjfk_suggestion"."description" IS '意见描述';
COMMENT ON COLUMN "public"."yjfk_suggestion"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_suggestion"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_suggestion"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_suggestion"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_suggestion"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_suggestion"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_suggestion"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table yjfk_suggestion
-- ----------------------------
CREATE INDEX IF NOT EXISTS "suggestion_create_date_index" ON "public"."yjfk_suggestion" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "suggestion_update_date_index" ON "public"."yjfk_suggestion" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_suggestion
-- ----------------------------
ALTER TABLE "public"."yjfk_suggestion" ADD CONSTRAINT "yjfk_suggestion_pkey" PRIMARY KEY ("id");


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

-- ----------------------------
-- Table structure for dlsc_naming_convention
-- ----------------------------
CREATE TABLE "public"."dlsc_naming_convention" (
                                                   "id" int8 NOT NULL,
                                                   "title" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                   "content" text COLLATE "pg_catalog"."default",
                                                   "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                   "is_delete" int2 NOT NULL DEFAULT 0,
                                                   "version" int4 DEFAULT 0,
                                                   "create_date" timestamp(6),
                                                   "update_date" timestamp(6) DEFAULT now(),
                                                   "create_user" int8,
                                                   "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_naming_convention"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."title" IS '标题';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."content" IS '内容';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_naming_convention"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table dlsc_naming_convention
-- ----------------------------
CREATE INDEX IF NOT EXISTS "naming_convention_create_date_index" ON "public"."dlsc_naming_convention" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "naming_convention_update_date_index" ON "public"."dlsc_naming_convention" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table dlsc_naming_convention
-- ----------------------------
ALTER TABLE "public"."dlsc_naming_convention" ADD CONSTRAINT "dlsc_naming_convention_pkey" PRIMARY KEY ("id");