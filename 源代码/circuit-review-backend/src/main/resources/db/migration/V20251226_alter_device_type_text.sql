ALTER TABLE "public"."dlsc_review_result_detail"
ALTER COLUMN "device_type" TYPE text COLLATE "pg_catalog"."default" USING "device_type"::text;