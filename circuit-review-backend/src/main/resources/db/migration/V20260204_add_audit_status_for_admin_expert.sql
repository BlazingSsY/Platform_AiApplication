ALTER TABLE "public"."dlsc_review_result_audit"
    ADD COLUMN "is_admin_audit_finished" SMALLINT NOT NULL CHECK (is_admin_audit_finished IN (0, 1)) DEFAULT 0;

COMMENT ON COLUMN "public"."dlsc_review_result_audit"."is_admin_audit_finished" IS '管理员问题审核状态(0:待审核;1:已审核)';

ALTER TABLE "public"."dlsc_review_result_audit"
    ADD COLUMN "is_expert_audit_finished" SMALLINT NOT NULL CHECK (is_expert_audit_finished IN (0, 1)) DEFAULT 0;

COMMENT ON COLUMN "public"."dlsc_review_result_audit"."is_expert_audit_finished" IS '专家问题审核状态(0:待审核;1:已审核)';