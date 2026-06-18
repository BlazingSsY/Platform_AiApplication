ALTER TABLE "public"."dlsc_review_result_detail_audit"
    ADD COLUMN "inherited_detail_audit_id" int8 DEFAULT NULL;

COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."inherited_detail_audit_id" IS '继承的结果详情复核记录Id';