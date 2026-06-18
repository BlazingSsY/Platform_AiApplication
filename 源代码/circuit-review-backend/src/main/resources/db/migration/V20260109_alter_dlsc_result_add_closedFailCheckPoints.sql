ALTER TABLE "public"."dlsc_review_result"
    ADD COLUMN "closed_fail_check_points" int4 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_review_result"."closed_fail_check_points" IS '关闭的不通过审查点数量';