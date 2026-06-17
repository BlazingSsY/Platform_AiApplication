-- ----------------------------
-- Table structure for dlsc_naming_convention
-- ----------------------------
CREATE TABLE "public"."dlsc_naming_convention" (
                                            "id" int8 NOT NULL,
                                            "title" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "content" text COLLATE "pg_catalog"."default",
                                            "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
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