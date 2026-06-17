ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "max_check_points" int4 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_file"."max_check_points" IS '最大审查点数';