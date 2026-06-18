ALTER TABLE "public"."dlsc_review_result_detail_audit"
ALTER COLUMN "reject_reason" TYPE text;

ALTER TABLE "public"."dlsc_review_result_detail"
ALTER COLUMN "issue_feedback" TYPE text;

ALTER TABLE "public"."dlsc_review_result_detail_audit"
ALTER COLUMN "issue_feedback" TYPE text;