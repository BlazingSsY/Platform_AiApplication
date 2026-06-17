
-- ----------------------------
-- Table structure for sjjyfx_experience_share
-- 设计经验分享主表
-- ----------------------------
DROP TABLE IF EXISTS "public"."sjjyfx_experience_share";
CREATE TABLE "public"."sjjyfx_experience_share" (
    "id" int8 NOT NULL,
    "title" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
    "content" text COLLATE "pg_catalog"."default",
    "contributor" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
    "organization" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
    "contact" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
    "like_count" int4 NOT NULL DEFAULT 0,
    "is_delete" int2 NOT NULL DEFAULT 0,
    "version" int4 DEFAULT 0,
    "create_date" timestamp(6),
    "update_date" timestamp(6) DEFAULT now(),
    "create_user" int8,
    "update_user" int8
)
;
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."id" IS 'ID（雪花算法）';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."title" IS '标题';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."content" IS '经验分享内容';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."contributor" IS '贡献人';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."organization" IS '单位';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."contact" IS '联系方式';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."like_count" IS '点赞数';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."is_delete" IS '是否删除(1:删除;0:正常)';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."version" IS '版本号';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sjjyfx_experience_share"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table sjjyfx_experience_share
-- ----------------------------
CREATE INDEX IF NOT EXISTS "experience_share_create_date_index" ON "public"."sjjyfx_experience_share" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX IF NOT EXISTS "experience_share_update_date_index" ON "public"."sjjyfx_experience_share" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX IF NOT EXISTS "experience_share_contributor_index" ON "public"."sjjyfx_experience_share" USING btree (
    "contributor" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sjjyfx_experience_share
-- ----------------------------
ALTER TABLE "public"."sjjyfx_experience_share" ADD CONSTRAINT "sjjyfx_experience_share_pkey" PRIMARY KEY ("id");


-- ----------------------------
-- Table structure for sjjyfx_like_record
-- 设计经验分享点赞记录表
-- ----------------------------
DROP TABLE IF EXISTS "public"."sjjyfx_like_record";
CREATE TABLE "public"."sjjyfx_like_record" (
    "id" int8 NOT NULL,
    "experience_id" int8 NOT NULL,
    "user_id" int8 NOT NULL,
    "is_liked" int2 NOT NULL DEFAULT 1,
    "is_delete" int2 NOT NULL DEFAULT 0,
    "version" int4 DEFAULT 0,
    "create_date" timestamp(6),
    "update_date" timestamp(6) DEFAULT now(),
    "create_user" int8,
    "update_user" int8
)
;
COMMENT ON COLUMN "public"."sjjyfx_like_record"."id" IS 'ID（雪花算法）';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."experience_id" IS '经验分享ID';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."user_id" IS '点赞用户ID';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."is_liked" IS '是否点赞(1:已点赞;0:已取消)';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."is_delete" IS '是否删除(1:删除;0:正常)';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."version" IS '版本号';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sjjyfx_like_record"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table sjjyfx_like_record
-- ----------------------------
CREATE INDEX IF NOT EXISTS "like_record_experience_id_index" ON "public"."sjjyfx_like_record" USING btree (
    "experience_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX IF NOT EXISTS "like_record_user_experience_unique" ON "public"."sjjyfx_like_record" USING btree (
    "experience_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
    "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sjjyfx_like_record
-- ----------------------------
ALTER TABLE "public"."sjjyfx_like_record" ADD CONSTRAINT "sjjyfx_like_record_pkey" PRIMARY KEY ("id");
