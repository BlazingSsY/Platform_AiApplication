ALTER TABLE "public"."dmsc_review_result_detail"
    ADD COLUMN "recheck_result_status" int4 DEFAULT 0,
  ADD COLUMN "reject_reason" varchar(500) COLLATE "pg_catalog"."default",
  ADD COLUMN "recheck_user_id" varchar(100) COLLATE "pg_catalog"."default";

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."recheck_result_status" IS '复核结果状态(1:通过;2:未通过)';

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."reject_reason" IS '拒绝理由';

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."recheck_user_id" IS '复核处理用户Id';
