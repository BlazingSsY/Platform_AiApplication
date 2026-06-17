ALTER TABLE "public"."dlsc_review_result"
    ADD COLUMN "total_fail_check_points" int4 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_review_result"."total_fail_check_points" IS '所有问题数量';