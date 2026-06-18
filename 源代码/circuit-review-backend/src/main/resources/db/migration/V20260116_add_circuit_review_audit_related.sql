ALTER TABLE "public"."dlsc_review_result"
    ADD COLUMN "is_in_audit" SMALLINT NOT NULL CHECK (is_in_audit IN (0, 1)) DEFAULT 0;

COMMENT ON COLUMN "public"."dlsc_review_result"."is_in_audit" IS '问题审核状态(0:未审核;1:审核中)';

ALTER TABLE "public"."dlsc_review_result_detail"
    ADD COLUMN "audit_type" int4;

COMMENT ON COLUMN "public"."dlsc_review_result_detail"."audit_type" IS '审核类型';

ALTER TABLE "public"."dlsc_review_result_detail"
    ADD COLUMN "approved_audit_type" int4;

COMMENT ON COLUMN "public"."dlsc_review_result_detail"."approved_audit_type" IS '批准的审核类型';

ALTER TABLE "public"."dlsc_review_result_detail"
    ADD COLUMN "issue_feedback" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying;

COMMENT ON COLUMN "public"."dlsc_review_result_detail"."issue_feedback" IS '问题反馈';

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "closed_loop_file_version_id" int8 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_file"."closed_loop_file_version_id" IS '问题闭环文件版本id';

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "closed_loop_result_id" int8 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_file"."closed_loop_result_id" IS '问题闭环审查结果id';


ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "closed_loop_file_version" int4 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_file"."closed_loop_file_version" IS '问题闭环文件版本序号';

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "latest_file_version_id" int8 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_file"."latest_file_version_id" IS '最新文件版本id';


ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "latest_file_version" int4 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_file"."latest_file_version" IS '最新文件版本序号';


ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "is_closed_loop" SMALLINT NOT NULL CHECK (is_closed_loop IN (0, 1)) DEFAULT 0;

COMMENT ON COLUMN "public"."dlsc_file"."is_closed_loop" IS '问题闭环状态(0:未闭环;1:已闭环)';

CREATE TABLE "public"."dlsc_review_result_audit" (
                                               "id" int8 NOT NULL,
                                               "file_id" int8 NOT NULL,
                                               "file_version_id" int8 NOT NULL,
                                               "result_id" int8 NOT NULL,
                                               "is_audit_finished" SMALLINT NOT NULL CHECK (is_audit_finished IN (0, 1)) DEFAULT 0,
                                               "audit_time" timestamp(6) DEFAULT NULL::character varying,
                                               "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                               "version" int4 DEFAULT 0,
                                               "create_date" timestamp(6),
                                               "update_date" timestamp(6) DEFAULT now(),
                                               "create_user" int8,
                                               "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."file_id" IS '外键,关联到文件表';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."file_version_id" IS '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."result_id" IS '外键,关联到审查结果表';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."is_audit_finished" IS '问题审核状态(0:待审核;1:已审核)';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."audit_time" IS '开始审核的时间';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_review_result_audit"."update_user" IS '最后更新人';

CREATE TABLE "public"."dlsc_review_result_detail_audit" (
                                                      "id" int8 NOT NULL,
                                                      "file_id" int8 NOT NULL,
                                                      "file_version_id" int8 NOT NULL,
                                                      "result_id" int8 NOT NULL,
                                                      "result_detail_id" int8 NOT NULL,
                                                      "result_audit_id" int8 NOT NULL,
                                                      "audit_type" int4,
                                                      "issue_feedback" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                      "status" int4 NOT NULL,
                                                      "audit_submit_time" timestamp(6) DEFAULT NULL::character varying,
                                                      "audit_close_time" timestamp(6) DEFAULT NULL::character varying,
                                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                                      "version" int4 DEFAULT 0,
                                                      "create_date" timestamp(6),
                                                      "update_date" timestamp(6) DEFAULT now(),
                                                      "create_user" int8,
                                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."file_id" IS '外键,关联到文件表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."file_version_id" IS '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."result_id" IS '外键,关联到结果表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."result_detail_id" IS '外键,关联到审查结果详情表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."result_audit_id" IS '外键,关联到电路审查结果审核表';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."audit_type" IS '审核类型';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."issue_feedback" IS '问题反馈';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."status" IS '问题审核状态(1:审核中;2:审核通过;3:审核拒绝)';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."audit_submit_time" IS '提交审核的时间';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."audit_close_time" IS '审核完成的时间';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."update_user" IS '最后更新人';
