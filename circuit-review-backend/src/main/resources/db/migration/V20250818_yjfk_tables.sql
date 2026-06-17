
-- ----------------------------
-- Table structure for yjfk_answer
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_answer";
CREATE TABLE "public"."yjfk_answer" (
                                        "id" int8 NOT NULL,
                                        "f_id" int8 NOT NULL,
                                        "answer" text COLLATE "pg_catalog"."default" DEFAULT NULL,
                                        "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                        "is_delete" int2 NOT NULL DEFAULT 0,
                                        "version" int4 DEFAULT 0,
                                        "create_date" timestamp(6),
                                        "update_date" timestamp(6) DEFAULT now(),
                                        "create_user" int8,
                                        "update_user" int8
)
;
COMMENT ON COLUMN "public"."yjfk_answer"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_answer"."f_id" IS '父ID(外键,关联到反馈意见主表或当前表)';
COMMENT ON COLUMN "public"."yjfk_answer"."answer" IS '回复内容';
COMMENT ON COLUMN "public"."yjfk_answer"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_answer"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_answer"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_answer"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_answer"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_answer"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_answer"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table yjfk_answer
-- ----------------------------
        
CREATE INDEX IF NOT EXISTS "answer_create_date_index" ON "public"."yjfk_answer" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "answer_fid_index" ON "public"."yjfk_answer" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "answer_update_date_index" ON "public"."yjfk_answer" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_answer
-- ----------------------------
ALTER TABLE "public"."yjfk_answer" ADD CONSTRAINT "yjfk_answer_pkey" PRIMARY KEY ("id");        
        
        
        
        
-- ----------------------------
-- Table structure for yjfk_append_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_append_file";
CREATE TABLE "public"."yjfk_append_file" (
                                             "id" int8 NOT NULL,
                                             "f_id" int8 NOT NULL,
                                             "type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                             "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                             "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                             "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                             "is_delete" int2 NOT NULL DEFAULT 0,
                                             "version" int4 DEFAULT 0,
                                             "create_date" timestamp(6),
                                             "update_date" timestamp(6) DEFAULT now(),
                                             "create_user" int8,
                                             "update_user" int8
)
;
COMMENT ON COLUMN "public"."yjfk_append_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_append_file"."f_id" IS '外键,关联到主记录表';
COMMENT ON COLUMN "public"."yjfk_append_file"."type" IS '附件类型';
COMMENT ON COLUMN "public"."yjfk_append_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."yjfk_append_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."yjfk_append_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_append_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_append_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_append_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_append_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_append_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_append_file"."update_user" IS '最后更新人';


-- ----------------------------
-- Indexes structure for table yjfk_append_file
-- ----------------------------
CREATE INDEX IF NOT EXISTS "append_file_fid_index" ON "public"."yjfk_append_file" USING btree (
    "f_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "append_file_type_index" ON "public"."yjfk_append_file" USING btree (
    "type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_append_file
-- ----------------------------
ALTER TABLE "public"."yjfk_append_file" ADD CONSTRAINT "yjfk_append_file_pkey" PRIMARY KEY ("id");        
        
        
        
        
-- ----------------------------
-- Table structure for yjfk_suggestion
-- ----------------------------
DROP TABLE IF EXISTS "public"."yjfk_suggestion";
CREATE TABLE "public"."yjfk_suggestion" (
                                            "id" int8 NOT NULL,
                                            "title" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "suggestion" text COLLATE "pg_catalog"."default",
                                            "status" int4,
                                            "description" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "is_delete" int2 NOT NULL DEFAULT 0,
                                            "version" int4 DEFAULT 0,
                                            "create_date" timestamp(6),
                                            "update_date" timestamp(6) DEFAULT now(),
                                            "create_user" int8,
                                            "update_user" int8
)
;
COMMENT ON COLUMN "public"."yjfk_suggestion"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."yjfk_suggestion"."title" IS '标题';
COMMENT ON COLUMN "public"."yjfk_suggestion"."suggestion" IS '反馈意见';
COMMENT ON COLUMN "public"."yjfk_suggestion"."status" IS '反馈状态(1:open;2:reopen;3:closed)';
COMMENT ON COLUMN "public"."yjfk_suggestion"."description" IS '意见描述';
COMMENT ON COLUMN "public"."yjfk_suggestion"."comments" IS '备注';
COMMENT ON COLUMN "public"."yjfk_suggestion"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."yjfk_suggestion"."version" IS '版本号';
COMMENT ON COLUMN "public"."yjfk_suggestion"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."yjfk_suggestion"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."yjfk_suggestion"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."yjfk_suggestion"."update_user" IS '最后更新人';

-- ----------------------------
-- Indexes structure for table yjfk_suggestion
-- ----------------------------
CREATE INDEX IF NOT EXISTS "suggestion_create_date_index" ON "public"."yjfk_suggestion" USING btree (
    "create_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );
CREATE INDEX IF NOT EXISTS "suggestion_update_date_index" ON "public"."yjfk_suggestion" USING btree (
    "update_date" "pg_catalog"."timestamp_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table yjfk_suggestion
-- ----------------------------
ALTER TABLE "public"."yjfk_suggestion" ADD CONSTRAINT "yjfk_suggestion_pkey" PRIMARY KEY ("id");
