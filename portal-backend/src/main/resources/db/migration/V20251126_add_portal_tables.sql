-- ----------------------------
-- Table structure for pot_application
-- ----------------------------
DROP TABLE IF EXISTS "public"."pot_application";
CREATE TABLE "public"."pot_application" (
                                            "id" int8 NOT NULL,
                                            "name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
                                            "eng_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
                                            "module" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "icon" text COLLATE "pg_catalog"."default",
                                            "hover_icon" text COLLATE "pg_catalog"."default",
                                            "image" text COLLATE "pg_catalog"."default",
                                            "description" text COLLATE "pg_catalog"."default",
                                            "sequence" int4 DEFAULT 0,
                                            "status" int2 NOT NULL DEFAULT 0,
                                            "url" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "comments" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "is_delete" int2 NOT NULL DEFAULT 0,
                                            "version" int4 NOT NULL DEFAULT 0,
                                            "create_date" timestamp(6),
                                            "update_date" timestamp(6) DEFAULT now(),
                                            "create_user" int8,
                                            "update_user" int8
)
;
COMMENT ON COLUMN "public"."pot_application"."id" IS '应用ID（UUID）';
COMMENT ON COLUMN "public"."pot_application"."name" IS '应用名称';
COMMENT ON COLUMN "public"."pot_application"."eng_name" IS '应用英文名称';
COMMENT ON COLUMN "public"."pot_application"."module" IS '所属板块';
COMMENT ON COLUMN "public"."pot_application"."icon" IS '应用图标';
COMMENT ON COLUMN "public"."pot_application"."hover_icon" IS '应用高亮图标';
COMMENT ON COLUMN "public"."pot_application"."image" IS '应用图片';
COMMENT ON COLUMN "public"."pot_application"."description" IS '应用说明';
COMMENT ON COLUMN "public"."pot_application"."sequence" IS '序号';
COMMENT ON COLUMN "public"."pot_application"."status" IS '状态(0: 未上线;1: 已上线;2: 已下线;)';
COMMENT ON COLUMN "public"."pot_application"."url" IS '应用入口地址';
COMMENT ON COLUMN "public"."pot_application"."comments" IS '备注';
COMMENT ON COLUMN "public"."pot_application"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."pot_application"."version" IS '版本号';
COMMENT ON COLUMN "public"."pot_application"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."pot_application"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."pot_application"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."pot_application"."update_user" IS '最后更新人';

ALTER TABLE "public"."pot_application" ADD CONSTRAINT "pot_application_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));
ALTER TABLE "public"."pot_application" ADD CONSTRAINT "pot_application_status_check" CHECK ((status = ANY (ARRAY[0, 1, 2])));
ALTER TABLE "public"."pot_application" ADD CONSTRAINT "pot_application_pkey" PRIMARY KEY ("id");




-- ----------------------------
-- Table structure for pot_info_append_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."pot_info_append_file";
CREATE TABLE "public"."pot_info_append_file" (
                                                 "id" int8 NOT NULL,
                                                 "f_id" int8 NOT NULL,
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
COMMENT ON COLUMN "public"."pot_info_append_file"."id" IS 'ID（UUID）';
COMMENT ON COLUMN "public"."pot_info_append_file"."f_id" IS '外键,关联到主记录表(pot_information)';
COMMENT ON COLUMN "public"."pot_info_append_file"."file_id" IS '文件在Minio上的ID';
COMMENT ON COLUMN "public"."pot_info_append_file"."file_name" IS '文件的原始名称';
COMMENT ON COLUMN "public"."pot_info_append_file"."comments" IS '备注';
COMMENT ON COLUMN "public"."pot_info_append_file"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."pot_info_append_file"."version" IS '版本号';
COMMENT ON COLUMN "public"."pot_info_append_file"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."pot_info_append_file"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."pot_info_append_file"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."pot_info_append_file"."update_user" IS '最后更新人';

ALTER TABLE "public"."pot_info_append_file" ADD CONSTRAINT "pot_info_append_file_pkey" PRIMARY KEY ("id");


-- ----------------------------
-- Table structure for pot_information
-- ----------------------------
DROP TABLE IF EXISTS "public"."pot_information";
CREATE TABLE "public"."pot_information" (
                                            "id" int8 NOT NULL,
                                            "title" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                            "summary" varchar(500) COLLATE "pg_catalog"."default",
                                            "type" int2 NOT NULL DEFAULT 1,
                                            "image" text COLLATE "pg_catalog"."default",
                                            "content" text COLLATE "pg_catalog"."default",
                                            "status" int2 NOT NULL DEFAULT 0,
                                            "url" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "comments" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL,
                                            "is_delete" int2 NOT NULL DEFAULT 0,
                                            "version" int4 NOT NULL DEFAULT 0,
                                            "create_date" timestamp(6),
                                            "update_date" timestamp(6) DEFAULT now(),
                                            "create_user" int8,
                                            "update_user" int8
)
;
COMMENT ON COLUMN "public"."pot_information"."id" IS '资讯ID（UUID）';
COMMENT ON COLUMN "public"."pot_information"."title" IS '资讯摘要';
COMMENT ON COLUMN "public"."pot_information"."summary" IS '资讯摘要';
COMMENT ON COLUMN "public"."pot_information"."type" IS '资讯类型(1:新闻;2:通知;3:资讯)';
COMMENT ON COLUMN "public"."pot_information"."image" IS '资讯图片';
COMMENT ON COLUMN "public"."pot_information"."content" IS '应用内容';
COMMENT ON COLUMN "public"."pot_information"."status" IS '状态(0: 未发布;1: 已发布;2: 已下线;)';
COMMENT ON COLUMN "public"."pot_information"."comments" IS '备注';
COMMENT ON COLUMN "public"."pot_information"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."pot_information"."version" IS '版本号';
COMMENT ON COLUMN "public"."pot_information"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."pot_information"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."pot_information"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."pot_information"."update_user" IS '最后更新人';


ALTER TABLE "public"."pot_information" ADD CONSTRAINT "pot_information_type_check" CHECK ((type = ANY (ARRAY[1, 2, 3])));
ALTER TABLE "public"."pot_information" ADD CONSTRAINT "pot_information_status_check" CHECK ((status = ANY (ARRAY[0, 1, 2])));
ALTER TABLE "public"."pot_information" ADD CONSTRAINT "pot_information_is_delete_check" CHECK ((is_delete = ANY (ARRAY[0, 1])));

-- ----------------------------
-- Primary Key structure for table pot_information
-- ----------------------------
ALTER TABLE "public"."pot_information" ADD CONSTRAINT "pot_information_pkey" PRIMARY KEY ("id");