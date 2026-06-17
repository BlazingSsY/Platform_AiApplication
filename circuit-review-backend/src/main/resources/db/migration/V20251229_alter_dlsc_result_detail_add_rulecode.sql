ALTER TABLE "public"."dlsc_review_result_detail"
    ADD COLUMN "rule_code" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying;

COMMENT ON COLUMN "public"."dlsc_review_result_detail"."rule_code" IS '规则编号';