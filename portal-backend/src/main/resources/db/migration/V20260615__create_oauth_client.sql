-- ----------------------------
-- Table structure for oauth_client
-- ----------------------------
DROP TABLE IF EXISTS "public"."oauth_client";
CREATE TABLE "public"."oauth_client" (
    "id" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
    "application_id" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
    "client_name" varchar(35) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id" varchar(35) COLLATE "pg_catalog"."default" NOT NULL,
    "client_secret" varchar(35) COLLATE "pg_catalog"."default" NOT NULL,
    "client_url" varchar(200) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
    "client_desc" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL,
    "logo_url" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL,
    "ranking" int2 NOT NULL DEFAULT 100,
    "remark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL,
    "state_enum" int2 NOT NULL DEFAULT 1,
    "create_date" timestamp(6) NOT NULL DEFAULT now(),
    "update_date" timestamp(6) DEFAULT now(),
    "create_user" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
    "update_user" varchar(22) COLLATE "pg_catalog"."default" DEFAULT NULL,
    "version" int4 DEFAULT 0,
    "is_delete" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "public"."oauth_client"."id" IS '主键ID';
COMMENT ON COLUMN "public"."oauth_client"."application_id" IS '应用ID，关联到应用表';
COMMENT ON COLUMN "public"."oauth_client"."client_name" IS '账号名称';
COMMENT ON COLUMN "public"."oauth_client"."client_id" IS '账号ID';
COMMENT ON COLUMN "public"."oauth_client"."client_secret" IS '账号密钥';
COMMENT ON COLUMN "public"."oauth_client"."client_url" IS '账号匹配的网站，支持正则符号';
COMMENT ON COLUMN "public"."oauth_client"."client_desc" IS '账号描述';
COMMENT ON COLUMN "public"."oauth_client"."logo_url" IS 'logo 的链接地址';
COMMENT ON COLUMN "public"."oauth_client"."ranking" IS '排序，默认值100，值越小越靠前';
COMMENT ON COLUMN "public"."oauth_client"."remark" IS '备注';
COMMENT ON COLUMN "public"."oauth_client"."state_enum" IS '是否启动, 1正常，2禁用';
COMMENT ON COLUMN "public"."oauth_client"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."oauth_client"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."oauth_client"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."oauth_client"."update_user" IS '最后更新人';
COMMENT ON COLUMN "public"."oauth_client"."version" IS '版本号';
COMMENT ON COLUMN "public"."oauth_client"."is_delete" IS '是否删除(1: 删除;0:正常)';

ALTER TABLE "public"."oauth_client" ADD CONSTRAINT "oauth_client_pkey" PRIMARY KEY ("id");
