
-- ----------------------------
-- Table structure for sjjyfx_experience_share_reply
-- 设计经验分享回复表
-- ----------------------------
DROP TABLE IF EXISTS "public"."sjjyfx_experience_share_reply";
CREATE TABLE "public"."sjjyfx_experience_share_reply" (
    "id" int8 NOT NULL,
    "f_id" int8 NOT NULL,
    "reply" text COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
    "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
    "is_delete" int2 NOT NULL DEFAULT 0,
    "version" int4 DEFAULT 0,
    "create_date" timestamp(6),
    "update_date" timestamp(6) DEFAULT now(),
    "create_user" int8,
    "update_user" int8
)
;
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."id" IS 'ID（雪花算法）';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."f_id" IS '父ID(外键,关联到设计经验分享主表)';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."reply" IS '回复内容';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."comments" IS '备注';


COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."is_delete" IS '是否删除(1:删除;0:正常)';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."version" IS '版本号';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sjjyfx_experience_share_reply"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table sjjyfx_experience_share_reply
-- ----------------------------
CREATE INDEX IF NOT EXISTS "experience_share_reply_create_date_index" ON "public"."sjjyfx_experience_share_reply" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX IF NOT EXISTS "experience_share_reply_fid_index" ON "public"."sjjyfx_experience_share_reply" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX IF NOT EXISTS "experience_share_reply_update_date_index" ON "public"."sjjyfx_experience_share_reply" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sjjyfx_experience_share_reply
-- ----------------------------
ALTER TABLE "public"."sjjyfx_experience_share_reply" ADD CONSTRAINT "sjjyfx_experience_share_reply_pkey" PRIMARY KEY ("id");



