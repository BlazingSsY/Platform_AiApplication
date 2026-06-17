ALTER TABLE "public"."dmsc_rule"
ALTER COLUMN "type" TYPE varchar(500) USING "type"::varchar(500);