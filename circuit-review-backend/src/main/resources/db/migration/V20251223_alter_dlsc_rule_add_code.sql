ALTER TABLE "public"."dlsc_rule"
    ADD COLUMN "code" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_rule"."code" IS '规则编号';