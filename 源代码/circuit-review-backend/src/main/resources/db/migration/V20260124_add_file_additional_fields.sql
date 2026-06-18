ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "compatible_models" varchar(500);

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "product_model" varchar(500);

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "product_name" varchar(500);

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "diagram_number" varchar(500);

ALTER TABLE "public"."dlsc_file"
    ADD COLUMN "diagram_version" varchar(500);

COMMENT ON COLUMN "public"."dlsc_file"."compatible_models" IS '配套机型';
COMMENT ON COLUMN "public"."dlsc_file"."product_model" IS '产品型号';
COMMENT ON COLUMN "public"."dlsc_file"."product_name" IS '产品名称';
COMMENT ON COLUMN "public"."dlsc_file"."diagram_number" IS '电路原理图号';
COMMENT ON COLUMN "public"."dlsc_file"."diagram_version" IS '电路原理图版本';