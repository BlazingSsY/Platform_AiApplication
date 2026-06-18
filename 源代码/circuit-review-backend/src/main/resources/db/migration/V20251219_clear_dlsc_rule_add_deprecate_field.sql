ALTER TABLE "public"."dlsc_rule"
    ADD COLUMN "is_deprecate" SMALLINT NOT NULL CHECK (is_deprecate IN (0, 1)) DEFAULT 0;

COMMENT ON COLUMN "public"."dlsc_rule"."is_deprecate" IS '是否废弃';