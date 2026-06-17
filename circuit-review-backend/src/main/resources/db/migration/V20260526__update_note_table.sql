-- ----------------------------
-- Table structure for dlsc_update_note
-- ----------------------------
DROP TABLE IF EXISTS "public"."dlsc_update_note";
CREATE TABLE "public"."dlsc_update_note" (
    "id" int8 NOT NULL,
    "update_time" timestamp(6),
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
COMMENT ON COLUMN "public"."dlsc_update_note"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."dlsc_update_note"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."dlsc_update_note"."content" IS '更新内容';
COMMENT ON COLUMN "public"."dlsc_update_note"."comments" IS '备注';
COMMENT ON COLUMN "public"."dlsc_update_note"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."dlsc_update_note"."version" IS '版本号';
COMMENT ON COLUMN "public"."dlsc_update_note"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."dlsc_update_note"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."dlsc_update_note"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."dlsc_update_note"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table dlsc_update_note
-- ----------------------------
CREATE INDEX IF NOT EXISTS "update_note_update_time_index" ON "public"."dlsc_update_note" USING btree (
    "update_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "update_note_create_date_index" ON "public"."dlsc_update_note" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table dlsc_update_note
-- ----------------------------
ALTER TABLE "public"."dlsc_update_note" ADD CONSTRAINT "dlsc_update_note_pkey" PRIMARY KEY ("id");
