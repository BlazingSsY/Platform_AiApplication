-- ----------------------------
-- Table structure for dmsc_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."dmsc_file";
CREATE TABLE "public"."dmsc_file" (
                                      "id" int8 NOT NULL DEFAULT 4,
                                      "minio_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                      "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                      "file_path" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                      "file_save_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                      "secret_level" int2 NOT NULL DEFAULT 4,
                                      "department_id" int8 DEFAULT NULL,
                                      "owner_id" int8 DEFAULT NULL,
                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                      "is_recycle" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dmsc_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dmsc_file"."minio_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."dmsc_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."dmsc_file"."file_path" IS '服务器上文件保存的路径';
COMMENT ON COLUMN "public"."dmsc_file"."file_save_name" IS '服务器上文件保存的名称';
COMMENT ON COLUMN "public"."dmsc_file"."secret_level" IS '密级(1:内部;2:受控;3:商密;4:公开)';
COMMENT ON COLUMN "public"."dmsc_file"."department_id" IS '文件隶属部门Id';
COMMENT ON COLUMN "public"."dmsc_file"."owner_id" IS '文件用户Id';
COMMENT ON COLUMN "public"."dmsc_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."dmsc_file"."is_recycle" IS '是否移入文件回收站(1:是; 0:否)';
COMMENT ON COLUMN "public"."dmsc_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dmsc_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."dmsc_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dmsc_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dmsc_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dmsc_file"."update_user" IS '最后更新人';

-- ----------------------------
-- Primary Key structure for table dmsc_file
-- ----------------------------
ALTER TABLE "public"."dmsc_file" ADD CONSTRAINT "dmsc_file_pkey" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "file_minio_id_index" ON "dmsc_file" USING btree ( "minio_id");

CREATE INDEX IF NOT EXISTS "file_department_id_index" ON "dmsc_file" USING btree ( "department_id");

CREATE INDEX IF NOT EXISTS "file_owner_id_index" ON "dmsc_file" USING btree ( "owner_id");

CREATE INDEX IF NOT EXISTS "file_is_recycle_index" ON "dmsc_file" USING btree ( "is_recycle");

CREATE INDEX IF NOT EXISTS "file_create_user_index" ON "dmsc_file" USING btree ( "create_user");

-- ----------------------------
-- Table structure for dmsc_file_version
-- ----------------------------
DROP TABLE IF EXISTS "public"."dmsc_file_version";
CREATE TABLE "public"."dmsc_file_version" (
                                              "id" int8 NOT NULL DEFAULT 4,
                                              "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                              "minio_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                              "file_version" int4 DEFAULT 1,
                                              "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                              "file_origin_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                              "secret_level" int2 NOT NULL DEFAULT 4,
                                              "department_id" int8 DEFAULT NULL,
                                              "owner_id" int8 DEFAULT NULL,
                                              "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                              "is_recycle" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                              "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                              "version" int4 DEFAULT 0,
                                              "create_date" timestamp(6),
                                              "update_date" timestamp(6) DEFAULT now(),
                                              "create_user" int8,
                                              "update_user" int8
)
;
COMMENT ON COLUMN "public"."dmsc_file_version"."id" IS '角色ID（UUID）';
COMMENT ON COLUMN "public"."dmsc_file_version"."file_id" IS '所属文件ID,关联到文件表';
COMMENT ON COLUMN "public"."dmsc_file_version"."minio_id" IS '文件在minio的ID';
COMMENT ON COLUMN "public"."dmsc_file_version"."file_version" IS '文件版本序号';
COMMENT ON COLUMN "public"."dmsc_file_version"."file_name" IS '文件版本名称';
COMMENT ON COLUMN "public"."dmsc_file_version"."file_origin_name" IS '文件上传时的原始名称';
COMMENT ON COLUMN "public"."dmsc_file_version"."secret_level" IS '密级(1:内部;2:受控;3:商密;4:公开)';
COMMENT ON COLUMN "public"."dmsc_file_version"."department_id" IS '文件隶属部门Id';
COMMENT ON COLUMN "public"."dmsc_file_version"."owner_id" IS '文件用户Id';
COMMENT ON COLUMN "public"."dmsc_file_version"."comments" IS '备注';
COMMENT ON COLUMN "public"."dmsc_file_version"."is_recycle" IS '是否移入文件回收站(1:是; 0:否)';
COMMENT ON COLUMN "public"."dmsc_file_version"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dmsc_file_version"."version" IS '版本号';
COMMENT ON COLUMN "public"."dmsc_file_version"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dmsc_file_version"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dmsc_file_version"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dmsc_file_version"."update_user" IS '最后更新人';

-- ----------------------------
-- Primary Key structure for table dmsc_file_version
-- ----------------------------
ALTER TABLE "public"."dmsc_file_version" ADD CONSTRAINT "dmsc_file_version_pkey" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "file_version_file_id_index" ON "dmsc_file_version" USING btree ( "file_id");

CREATE INDEX IF NOT EXISTS "file_version_department_id_index" ON "dmsc_file_version" USING btree ( "department_id");

CREATE INDEX IF NOT EXISTS "file_version_owner_id_index" ON "dmsc_file_version" USING btree ( "owner_id");

CREATE INDEX IF NOT EXISTS "file_version_is_recycle_index" ON "dmsc_file_version" USING btree ( "is_recycle");

CREATE INDEX IF NOT EXISTS "file_version_create_user_index" ON "dmsc_file_version" USING btree ( "create_user");

-- ----------------------------
-- Table structure for dmsc_review_result
-- ----------------------------
DROP TABLE IF EXISTS "public"."dmsc_review_result";
CREATE TABLE "public"."dmsc_review_result" (
                                               "id" int8 NOT NULL,
                                               "file_id" int8 NOT NULL,
                                               "file_version_id" int8 NOT NULL,
                                               "check_points" int4 DEFAULT NULL,
                                               "pass_check_points" int4 DEFAULT NULL,
                                               "pass_rate" numeric(15,5) DEFAULT NULL,
                                               "is_closed_loop" SMALLINT NOT NULL CHECK (is_closed_loop IN (0, 1)) DEFAULT 0,
                                               "review_time" timestamp(6) DEFAULT NULL,
                                               "status" int4,
                                               "error_message" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                               "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                               "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                               "version" int4 DEFAULT 0,
                                               "create_date" timestamp(6),
                                               "update_date" timestamp(6) DEFAULT now(),
                                               "create_user" int8,
                                               "update_user" int8
)
;
COMMENT ON COLUMN "public"."dmsc_review_result"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dmsc_review_result"."file_id" IS '外键,关联到文件表';
COMMENT ON COLUMN "public"."dmsc_review_result"."file_version_id" IS '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."dmsc_review_result"."check_points" IS '检查点数量';
COMMENT ON COLUMN "public"."dmsc_review_result"."pass_check_points" IS '通过的检查点数量';
COMMENT ON COLUMN "public"."dmsc_review_result"."pass_rate" IS '通过率';
COMMENT ON COLUMN "public"."dmsc_review_result"."is_closed_loop" IS '闭环状态';
COMMENT ON COLUMN "public"."dmsc_review_result"."review_time" IS '开始审查的时间';
COMMENT ON COLUMN "public"."dmsc_review_result"."status" IS '状态(1:正在审查;2:审查完成)';
COMMENT ON COLUMN "public"."dmsc_review_result"."error_message" IS '底层API错误信息';
COMMENT ON COLUMN "public"."dmsc_review_result"."comments" IS '备注';
COMMENT ON COLUMN "public"."dmsc_review_result"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dmsc_review_result"."version" IS '版本号';
COMMENT ON COLUMN "public"."dmsc_review_result"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dmsc_review_result"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dmsc_review_result"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dmsc_review_result"."update_user" IS '最后更新人';

-- ----------------------------
-- Primary Key structure for table dmsc_review_result
-- ----------------------------
ALTER TABLE "public"."dmsc_review_result" ADD CONSTRAINT "dmsc_review_result_pkey" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "review_result_file_id_index" ON "dmsc_review_result" USING btree ( "file_id");

CREATE INDEX IF NOT EXISTS "review_result_review_time_index" ON "dmsc_review_result" USING btree ( "review_time");

CREATE INDEX IF NOT EXISTS "review_result_status_index" ON "dmsc_review_result" USING btree ( "status");

CREATE INDEX IF NOT EXISTS "review_result_create_date_index" ON "dmsc_review_result" ( "create_date");

-- ----------------------------
-- Table structure for dmsc_review_result_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."dmsc_review_result_detail";
CREATE TABLE "public"."dmsc_review_result_detail" (
                                                      "id" int8 NOT NULL,
                                                      "result_id" int8,
                                                      "rule_id" int8,
                                                      "source_file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "language" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "error_code" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "line_number" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "error_reason" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "review_suggestion" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "is_passed" SMALLINT CHECK (is_passed IN (0, 1)),
                                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                                      "version" int4 DEFAULT 0,
                                                      "create_date" timestamp(6),
                                                      "update_date" timestamp(6) DEFAULT now(),
                                                      "create_user" int8,
                                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."result_id" IS '外键,关联到结果表';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."rule_id" IS '外键,关联到规则表';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."source_file_name" IS '源代码文件名';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."language" IS '代码语言';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."error_code" IS '错误代码';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."line_number" IS '代码行号';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."error_code" IS '错误原因';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."review_suggestion" IS '审查意见';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."is_passed" IS '是否通过(0:未通过;1:通过)';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."comments" IS '备注';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."version" IS '版本号';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dmsc_review_result_detail"."update_user" IS '最后更新人';

-- ----------------------------
-- Primary Key structure for table dmsc_review_result_detail
-- ----------------------------
ALTER TABLE "public"."dmsc_review_result_detail" ADD CONSTRAINT "dmsc_review_result_detail_pkey" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_id_index" ON "dmsc_review_result_detail" ( "result_id");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_rule_id_index" ON "dmsc_review_result_detail" ( "rule_id");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_is_passed_index" ON "dmsc_review_result_detail" ( "is_passed");

CREATE INDEX IF NOT EXISTS "review_result_detail_result_create_date_index" ON "dmsc_review_result_detail" ( "create_date");

-- ----------------------------
-- Table structure for dmsc_rule
-- ----------------------------
DROP TABLE IF EXISTS "public"."dmsc_rule";
CREATE TABLE "public"."dmsc_rule" (
                                      "id" int8 NOT NULL,
                                      "type" int4,
                                      "name" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL,
                                      "code" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL,
                                      "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dmsc_rule"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dmsc_rule"."type" IS '规则类型';
COMMENT ON COLUMN "public"."dmsc_rule"."name" IS '规则名称';
COMMENT ON COLUMN "public"."dmsc_rule"."name" IS '规则编号';
COMMENT ON COLUMN "public"."dmsc_rule"."comments" IS '备注';
COMMENT ON COLUMN "public"."dmsc_rule"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dmsc_rule"."version" IS '版本号';
COMMENT ON COLUMN "public"."dmsc_rule"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dmsc_rule"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dmsc_rule"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dmsc_rule"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of dmsc_rule
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table dmsc_rule
-- ----------------------------
ALTER TABLE "public"."dmsc_rule" ADD CONSTRAINT "dmsc_rule_pkey" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "rule_type_index" ON "dmsc_rule" USING btree ( "type");

CREATE INDEX IF NOT EXISTS "rule_create_date_index" ON "dmsc_rule" USING btree ( "create_date");

