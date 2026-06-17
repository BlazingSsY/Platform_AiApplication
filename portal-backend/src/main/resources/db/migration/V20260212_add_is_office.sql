ALTER TABLE "public"."urm_department"
    ADD COLUMN "is_office" int2 NOT NULL DEFAULT 0;

COMMENT ON COLUMN "public"."urm_department"."is_office" IS '是否科室 0-不是科室 1-是科室';