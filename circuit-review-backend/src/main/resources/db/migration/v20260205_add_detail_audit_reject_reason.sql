ALTER TABLE "public"."dlsc_review_result_detail_audit"
    ADD COLUMN "reject_reason" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying;

COMMENT ON COLUMN "public"."dlsc_review_result_detail_audit"."reject_reason" IS '拒绝原因';