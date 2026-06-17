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
                                              "is_recycle" int2 NOT NULL DEFAULT 0,
                                              "is_delete" int2 NOT NULL DEFAULT 0,
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
-- Indexes structure for table dlsc_file_version
-- ----------------------------
CREATE INDEX "file_version_create_user_index" ON "public"."dlsc_file_version" USING btree (
    "create_user" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "file_version_department_id_index" ON "public"."dlsc_file_version" USING btree (
    "department_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "file_version_file_id_index" ON "public"."dlsc_file_version" USING btree (
    "file_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE INDEX "file_version_is_recycle_index" ON "public"."dlsc_file_version" USING btree (
    "is_recycle" "pg_catalog"."int2_ops" ASC NULLS LAST
    );
CREATE INDEX "file_version_owner_id_index" ON "public"."dlsc_file_version" USING btree (
    "owner_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Checks structure for table dlsc_file_version
-- ----------------------------
ALTER TABLE "public"."dlsc_file_version" ADD CONSTRAINT "dlsc_file_version_is_recycle_check" CHECK ((is_recycle = ANY (ARRAY[0, 1])));
ALTER TABLE "public"."dlsc_file_version" ADD CONSTRAINT "dlsc_file_version_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));

-- ----------------------------
-- Primary Key structure for table dlsc_file_version
-- ----------------------------
ALTER TABLE "public"."dlsc_file_version" ADD CONSTRAINT "dlsc_file_version_pkey" PRIMARY KEY ("id");


INSERT INTO "public"."dlsc_file_version"(id, file_id, minio_id, file_version, file_name, file_origin_name, secret_level,
                              department_id, owner_id, comments, is_recycle, is_delete, version, create_date,
                              update_date, create_user, update_user)
SELECT id,
       id        AS file_id,
       file_id,
       1 AS file_version,
       CONCAT(
               SUBSTRING(file_name FROM '^(.*?)(\.[^.]*)$'), -- 分组匹配文件名主体和扩展名
               '_v1',
               SUBSTRING(file_name FROM '\.[^.]*$') -- 提取扩展名
           ) AS file_name,
       file_name AS file_origin_name,
       secret_level,
       department_id,
       owner_id,
       comments,
       is_recycle,
       is_delete,
       version,
       create_date,
       update_date,
       create_user,
       update_user
FROM "public"."dlsc_file";


DELETE FROM "public"."dlsc_review_result_detail";

DELETE FROM "public"."dlsc_rule";

DELETE FROM "public"."urm_userlog";

-- ----------------------------
-- recreate dlsc_review_result
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_review_result";
CREATE TABLE "public"."dlsc_review_result" (
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
                                               "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_review_result"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_review_result"."file_id" IS '外键,关联到文件表';
COMMENT ON COLUMN "public"."dlsc_review_result"."file_version_id" IS '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."dlsc_review_result"."check_points" IS '检查点数量';
COMMENT ON COLUMN "public"."dlsc_review_result"."pass_check_points" IS '通过的检查点数量';
COMMENT ON COLUMN "public"."dlsc_review_result"."pass_rate" IS '通过率';
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
-- Indexes structure for table dlsc_review_result
-- ----------------------------
CREATE INDEX "review_result_create_date_index" ON "public"."dlsc_review_result" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX "review_result_file_id_index" ON "public"."dlsc_review_result" USING btree (
    "file_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "review_result_file_version_id_index" ON "public"."dlsc_review_result" USING btree (
    "file_version_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "review_result_review_time_index" ON "public"."dlsc_review_result" USING btree (
    "review_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX "review_result_status_index" ON "public"."dlsc_review_result" USING btree (
    "status" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Checks structure for table dlsc_review_result
-- ----------------------------
ALTER TABLE "public"."dlsc_review_result" ADD CONSTRAINT "dlsc_review_result_new_is_closed_loop_check" CHECK ((is_closed_loop = ANY (ARRAY[0, 1])));
ALTER TABLE "public"."dlsc_review_result" ADD CONSTRAINT "dlsc_review_result_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));

-- ----------------------------
-- Primary Key structure for table dlsc_review_result
-- ----------------------------
ALTER TABLE "public"."dlsc_review_result" ADD CONSTRAINT "dlsc_review_result_pkey" PRIMARY KEY ("id");



ALTER TABLE "public"."dlsc_file"
ALTER COLUMN "is_recycle" TYPE int2 USING "is_recycle"::int2,
  ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "dlsc_file_new_is_recycle_check" CHECK ((is_recycle = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "dlsc_file_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."dlsc_review_result_detail"
ALTER COLUMN "is_passed" TYPE int2 USING "is_passed"::int2,
  ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "dlsc_review_result_detail_new_is_passed_check" CHECK ((is_passed = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "dlsc_review_result_detail_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."dlsc_rule"
ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "dlsc_rule_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."urm_department"
ALTER COLUMN "is_editable" TYPE int2 USING "is_editable"::int2,
  ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "urm_department_new_is_editable_check" CHECK ((is_editable = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_department_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));




ALTER TABLE "public"."urm_power"
ALTER COLUMN "is_frame" TYPE int2 USING "is_frame"::int2,
  ALTER COLUMN "visible" TYPE int2 USING "visible"::int2,
  ALTER COLUMN "enabled" TYPE int2 USING "enabled"::int2,
  ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "urm_power_new_is_frame_check" CHECK ((is_frame = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_power_new_visible_check" CHECK ((visible = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_power_new_enabled_check" CHECK ((enabled = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_power_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."urm_role"
ALTER COLUMN "is_editable" TYPE int2 USING "is_editable"::int2,
  ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "urm_role_new_is_editable_check" CHECK ((is_editable = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_role_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."urm_rolepower"
ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "urm_rolepower_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));




ALTER TABLE "public"."urm_user"
ALTER COLUMN "locked" TYPE int2 USING "locked"::int2,
  ALTER COLUMN "need_change_password" TYPE int2 USING "need_change_password"::int2,
  ALTER COLUMN "is_editable" TYPE int2 USING "is_editable"::int2,
  ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "urm_user_new_locked_check" CHECK ((locked = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_user_new_need_change_password_check" CHECK ((need_change_password = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_user_new_is_editable_check" CHECK ((is_editable = ANY (ARRAY[0, 1]))),
  ADD CONSTRAINT "urm_user_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."urm_userrole"
ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "urm_userrole_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));


ALTER TABLE "public"."dlsc_tool_file"
ALTER COLUMN "is_delete" TYPE int2 USING "is_delete"::int2,
  ADD CONSTRAINT "dlsc_tool_file_new_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));