ALTER TABLE "public"."dmsc_file"
    ADD COLUMN "compatible_models" varchar(500);

ALTER TABLE "public"."dmsc_file"
    ADD COLUMN "product_model" varchar(500);

ALTER TABLE "public"."dmsc_file"
    ADD COLUMN "product_name" varchar(500);

ALTER TABLE "public"."dmsc_file"
    ADD COLUMN "config_name" varchar(500);

ALTER TABLE "public"."dmsc_file"
    ADD COLUMN "code_file_version" varchar(500);

COMMENT ON COLUMN "public"."dmsc_file"."compatible_models" IS '配套机型';
COMMENT ON COLUMN "public"."dmsc_file"."product_model" IS '产品型号';
COMMENT ON COLUMN "public"."dmsc_file"."product_name" IS '产品名称';
COMMENT ON COLUMN "public"."dmsc_file"."config_name" IS '配置项名称';
COMMENT ON COLUMN "public"."dmsc_file"."code_file_version" IS '代码版本';