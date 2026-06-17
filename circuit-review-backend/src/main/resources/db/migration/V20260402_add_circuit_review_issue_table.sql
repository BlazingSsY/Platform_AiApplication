-- ----------------------------
-- Table structure for dlsc_review_issue
-- ----------------------------
CREATE TABLE "public"."dlsc_review_issue" (
                                                   "id" int8 NOT NULL,
                                                   "file_id" int8 NOT NULL,
                                                   "file_version_id" int8 NOT NULL,
                                                   "result_id" int8 NOT NULL,
                                                   "result_detail_id" int8 NOT NULL,
                                                   "rule_id" int8 NOT NULL,
                                                   "device_type" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                   "tag_pin" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                   "review_suggestion" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                   "rule_code" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                   "review_time" timestamp(6) DEFAULT NULL,
                                                   "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                                   "is_delete" int2 NOT NULL DEFAULT 0,
                                                   "version" int4 DEFAULT 0,
                                                   "create_date" timestamp(6),
                                                   "update_date" timestamp(6) DEFAULT now(),
                                                   "create_user" int8,
                                                   "update_user" int8
)
;
COMMENT ON COLUMN "public"."dlsc_review_issue"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_review_issue".file_id is '外键,关联到文件表';
COMMENT ON COLUMN "public"."dlsc_review_issue".file_version_id is '外键,关联到文件版本表';
COMMENT ON COLUMN "public"."dlsc_review_issue".result_id is '外键,关联到结果表';
COMMENT ON COLUMN "public"."dlsc_review_issue".result_detail_id is '外键,关联到结果详情表';
COMMENT ON COLUMN "public"."dlsc_review_issue".rule_id is '外键,关联到规则表';
COMMENT ON COLUMN "public"."dlsc_review_issue".device_type is '器件型号(可为空)';
COMMENT ON COLUMN "public"."dlsc_review_issue".tag_pin is '位号引脚(单个位号引脚，可为空)';
COMMENT ON COLUMN "public"."dlsc_review_issue"."review_suggestion" IS '审查意见';
COMMENT ON COLUMN "public"."dlsc_review_issue"."rule_code" IS '规则编号';
COMMENT ON COLUMN "public"."dlsc_review_issue"."review_time" IS '审查时间';
COMMENT ON COLUMN "public"."dlsc_review_issue"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_review_issue"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_review_issue"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_review_issue"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_review_issue"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_review_issue"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_review_issue"."update_user" IS '最后更新人';

