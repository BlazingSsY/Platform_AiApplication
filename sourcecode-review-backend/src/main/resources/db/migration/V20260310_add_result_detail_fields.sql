ALTER TABLE "public"."dmsc_review_result_detail"
    ADD COLUMN "question_id" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL,
  ADD COLUMN "recheck_conclusion" varchar(500) COLLATE "pg_catalog"."default",
  ADD COLUMN "question_desc" text COLLATE "pg_catalog"."default",
  ADD COLUMN "recheck_status" int4 DEFAULT 0;

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."question_id" IS '问题ID';

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."recheck_conclusion" IS '审查结论';

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."question_desc" IS '问题描述';

COMMENT ON COLUMN "public"."dmsc_review_result_detail"."recheck_status" IS '复核状态(1:未复核;2:复核中;3:复核完成)';