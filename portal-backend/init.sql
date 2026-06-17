/*
 Navicat Premium Data Transfer

 Source Server         : 100.64.0.3
 Source Server Type    : PostgreSQL
 Source Server Version : 90204
 Source Host           : 100.64.0.3:15432
 Source Catalog        : dlsc
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90204
 File Encoding         : 65001

 Date: 15/07/2025 16:47:14
*/


-- ----------------------------
-- Table structure for pot_application
-- ----------------------------
DROP TABLE IF EXISTS "public"."pot_application";
CREATE TABLE "public"."pot_application" (
                                            "id" int8 NOT NULL,
                                            "name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
                                            "eng_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
                                            "module" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                            "icon" text COLLATE "pg_catalog"."default",
                                            "hover_icon" text COLLATE "pg_catalog"."default",
                                            "image" text COLLATE "pg_catalog"."default",
                                            "description" text COLLATE "pg_catalog"."default",
                                            "sequence" int4 DEFAULT 0,
                                            "status" int2 NOT NULL DEFAULT 0,
                                            "url" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                            "comments" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
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




-- ----------------------------
-- Table structure for pot_info_append_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."pot_info_append_file";
CREATE TABLE "public"."pot_info_append_file" (
                                                 "id" int8 NOT NULL,
                                                 "f_id" int8 NOT NULL,
                                                 "file_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                                 "file_name" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                                 "comments" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
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
                                            "url" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                            "comments" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
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
-- Table structure for urm_department
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_department";
CREATE TABLE "public"."urm_department" (
                                           "id" int8 NOT NULL,
                                           "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                           "type" varchar(20) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "is_editable" SMALLINT NOT NULL CHECK (is_editable IN (0, 1)) DEFAULT 1,
                                           "sequence" int4 DEFAULT 0,
                                           "f_id" int8,
                                           "comments" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                           "version" int4 NOT NULL DEFAULT 0,
                                           "create_date" timestamp(6),
                                           "update_date" timestamp(6) DEFAULT now(),
                                           "create_user" int8,
                                           "update_user" int8
)
;
COMMENT ON COLUMN "public"."urm_department"."id" IS '部门ID（UUID）';
COMMENT ON COLUMN "public"."urm_department"."name" IS '用户创建时输入的部门名称';
COMMENT ON COLUMN "public"."urm_department"."type" IS '机构类型';
COMMENT ON COLUMN "public"."urm_department"."is_editable" IS '是否可删除 0-不可删除 1-可删除';
COMMENT ON COLUMN "public"."urm_department"."sequence" IS '部门序号';
COMMENT ON COLUMN "public"."urm_department"."f_id" IS '父级机构ID（UUID），为null时为根级机构';
COMMENT ON COLUMN "public"."urm_department"."comments" IS '备注';
COMMENT ON COLUMN "public"."urm_department"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."urm_department"."version" IS '版本号';
COMMENT ON COLUMN "public"."urm_department"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."urm_department"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."urm_department"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."urm_department"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of urm_department
-- ----------------------------
INSERT INTO "public"."urm_department" VALUES (110000000000001, '中航机载', '业务管理部门', '0', 0, NULL, NULL, '0', 0, '2025-07-02 09:05:34', '2025-07-02 09:05:34', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_department" VALUES (110000000000002, '631', '业务管理部门', '0', 1, 110000000000001, NULL, '0', 0, '2025-07-02 09:05:34', '2025-07-02 09:05:34', 100000000000001, 100000000000001);

-- ----------------------------
-- Table structure for urm_power
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_power";
CREATE TABLE "public"."urm_power" (
                                      "id" int8 NOT NULL,
                                      "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                      "alias" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "menu_type" varchar(4) COLLATE "pg_catalog"."default" NOT NULL,
                                      "path" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "component" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "icon" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "is_frame" SMALLINT NOT NULL CHECK (is_frame IN (0, 1)) DEFAULT 0,
                                      "visible" SMALLINT NOT NULL CHECK (visible IN (0, 1)) DEFAULT 1,
                                      "enabled" SMALLINT NOT NULL CHECK (enabled IN (0, 1)) DEFAULT 1,
                                      "sequence" int4 DEFAULT 0,
                                      "f_id" varchar(22) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                      "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                      "version" int4 DEFAULT 0,
                                      "create_date" timestamp(6),
                                      "update_date" timestamp(6) DEFAULT now(),
                                      "create_user" int8,
                                      "update_user" int8
)
;
COMMENT ON COLUMN "public"."urm_power"."id" IS '权限ID（UUID）';
COMMENT ON COLUMN "public"."urm_power"."name" IS '权限名';
COMMENT ON COLUMN "public"."urm_power"."alias" IS '权限别名';
COMMENT ON COLUMN "public"."urm_power"."menu_type" IS '菜单类型(M:目录;C:菜单;F:按钮)';
COMMENT ON COLUMN "public"."urm_power"."path" IS '菜单路径';
COMMENT ON COLUMN "public"."urm_power"."component" IS '组件名称';
COMMENT ON COLUMN "public"."urm_power"."icon" IS '菜单图标';
COMMENT ON COLUMN "public"."urm_power"."is_frame" IS '是否外联(1:是 ;0:否)';
COMMENT ON COLUMN "public"."urm_power"."visible" IS '是否显示(1:是 ;0:否)';
COMMENT ON COLUMN "public"."urm_power"."enabled" IS '是否启用(1:是 ;0:否)';
COMMENT ON COLUMN "public"."urm_power"."sequence" IS '同级别显示顺序';
COMMENT ON COLUMN "public"."urm_power"."f_id" IS '父id';
COMMENT ON COLUMN "public"."urm_power"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."urm_power"."version" IS '版本号';
COMMENT ON COLUMN "public"."urm_power"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."urm_power"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."urm_power"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."urm_power"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of urm_power
-- ----------------------------
INSERT INTO "public"."urm_power" VALUES (237208795044873, '测试', '12', 'C', '12', '21', NULL, '0', '1', '1', 11, '925644053573655', '1', 0, '2025-07-10 15:43:22.480675', '2025-07-10 15:44:40.416887', 100000000000002, 100000000000002);
INSERT INTO "public"."urm_power" VALUES (925644053573182, '日志列表', ':sysManage:logManage:list', 'C', '/logsList', '/log/logList', NULL, '0', '1', '1', 161, '925644053573653', '1', 0, '2022-01-07 13:42:02', '2025-07-10 15:45:13.419189', 100000000000001, 100000000000002);
INSERT INTO "public"."urm_power" VALUES (925644053573163, '查看(按钮)', ':sysManage:userManage:list:btnDetail', 'F', NULL, NULL, NULL, '0', '1', '1', 1312, '925644053573687', '0', 0, '2021-10-08 14:20:27', '2024-03-27 11:03:28', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573634, '新增(按钮)', ':sysManage:departmentManage:list:btnAdd', 'F', NULL, NULL, NULL, '0', '1', '1', 1111, '925644053573698', '0', 0, '2021-10-08 14:34:56', '2024-03-27 10:58:38', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573635, '搜索(按钮)', ':sysManage:userManage:list:btnSearch', 'F', NULL, NULL, NULL, '0', '1', '1', 1313, '925644053573687', '0', 0, '2021-10-08 14:20:45', '2024-03-27 11:03:29', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573638, '编辑(按钮)', ':sysManage:userManage:list:btnEdit', 'F', NULL, NULL, NULL, '0', '1', '1', 1314, '925644053573687', '0', 0, '2021-10-08 14:21:06', '2024-03-27 11:03:31', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573641, '删除(按钮)', ':sysManage:userManage:list:btnDel', 'F', NULL, NULL, NULL, '0', '1', '1', 1315, '925644053573687', '0', 2, '2021-10-08 14:21:19', '2024-03-27 11:03:34', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573650, '部门编辑', ':sysManage:departmentManage:edit', 'C', '/depAddEdit', '/department/depAddEdit', NULL, '0', '0', '1', 112, '925644053573670', '0', 1, '2022-01-08 13:14:57', '2024-03-27 10:57:49', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573651, '部门详情', ':sysManage:departmentManage:detail', 'C', '/depDetail', '/department/depDetail', NULL, '0', '0', '1', 113, '925644053573670', '0', 1, '2022-01-08 13:14:57', '2024-03-27 10:57:58', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573654, '用户编辑', ':sysManage:userManage:edit', 'C', '/userAddEdit', '/user/userAddEdit', NULL, '0', '0', '1', 132, '925644053573662', '0', 0, '2021-10-08 16:47:31', '2024-03-27 11:02:34', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573662, '用户管理', ':sysManage:userManage', 'C', '/user', 'Layout', 'icon-yonghuguanli', '0', '1', '1', 13, '925644053573655', '0', 0, '2021-10-08 16:45:53', '2024-03-27 10:52:17', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573664, '角色编辑', ':sysManage:roleManage:edit', 'C', '/roleAddEdit', '/role/roleAddEdit', NULL, '0', '0', '1', 122, '925644053573683', '0', 1, '2021-10-08 16:51:37', '2024-03-27 11:00:25', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573665, '角色详情', ':sysManage:roleManage:detail', 'C', '/roleDetail', '/role/roleDetail', NULL, '0', '0', '1', 123, '925644053573683', '0', 1, '2021-10-08 16:51:37', '2024-03-27 11:00:29', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573666, '角色列表', ':sysManage:roleManage:list', 'C', '/roleList', '/role/roleList', NULL, '0', '1', '1', 121, '925644053573683', '0', 1, '2022-01-07 13:49:11', '2024-03-27 11:00:19', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573668, '编辑(按钮)', ':sysManage:departmentManage:list:btnEdit', 'F', NULL, NULL, NULL, '0', '1', '1', 1112, '925644053573698', '0', 0, '2021-10-08 14:37:15', '2024-03-27 10:58:40', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573670, '部门管理', ':sysManage:departmentManage', 'C', '/department', 'Layout', 'icon-bumenguanli', '0', '1', '1', 11, '925644053573655', '0', 0, '2022-01-08 13:14:45', '2024-03-27 10:52:12', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573673, '查看(按钮)', ':sysManage:departmentManage:list:btnDetail', 'F', NULL, NULL, NULL, '0', '1', '1', 1113, '925644053573698', '0', 0, '2021-10-08 14:37:31', '2024-03-27 10:58:45', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573676, '新增(按钮)', ':sysManage:roleManage:list:btnAdd', 'F', NULL, NULL, NULL, '0', '1', '1', 1211, '925644053573666', '0', 1, '2021-10-08 14:26:21', '2024-03-27 11:01:44', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573678, '菜单列表', ':sysManage:menuManage:list', 'C', '/menuList', '/menu/menuList', NULL, '0', '1', '1', 151, '925644053573694', '0', 2, '2022-01-21 12:59:13', '2024-03-27 11:05:30', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573653, '日志管理', ':sysManage:logManage', 'C', '/logs', 'Layout', 'icon-rizhiguanli', '0', '1', '1', 16, '925644053573655', '1', 0, '2022-01-07 13:42:06', '2025-07-10 15:45:23.108013', 100000000000001, 100000000000002);
INSERT INTO "public"."urm_power" VALUES (925644053573655, '系统设置', ':sysManage', 'M', '/sysManage', 'Layout', 'icon-xitongshezhi', '0', '1', '1', 1, NULL, '0', 3, '2021-09-26 11:30:16', '2025-07-10 15:59:47.949563', 100000000000001, 100000000000002);
INSERT INTO "public"."urm_power" VALUES (925644053573679, '角色人员', ':sysManage:roleManage:roleUser', 'C', '/roleUser', '/role/roleUser', NULL, '0', '0', '1', 124, '925644053573683', '0', 1, '2021-10-08 16:51:44', '2024-03-27 11:00:34', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573680, '删除(按钮)', ':sysManage:departmentManage:list:btnDel', 'F', NULL, NULL, NULL, '0', '1', '1', 1114, '925644053573698', '0', 0, '2021-10-08 14:37:59', '2024-03-27 10:58:47', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573683, '角色管理', ':sysManage:roleManage', 'C', '/role', 'Layout', 'icon-jiaoseguanli', '0', '1', '1', 12, '925644053573655', '0', 0, '2021-10-08 17:59:42', '2024-03-27 10:52:15', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573686, '查看(按钮)', ':sysManage:roleManage:list:btnDetail', 'F', NULL, NULL, NULL, '0', '1', '1', 1212, '925644053573666', '0', 1, '2021-10-08 14:26:34', '2024-03-27 11:01:49', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573687, '用户列表', ':sysManage:userManage:list', 'C', '/userList', '/user/userList', NULL, '0', '1', '1', 131, '925644053573662', '0', 2, '2022-01-07 13:51:59', '2024-03-27 11:02:32', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573689, '搜索(按钮)', ':sysManage:roleManage:list:btnSearch', 'F', NULL, NULL, NULL, '0', '1', '1', 1213, '925644053573666', '0', 1, '2021-10-08 14:26:48', '2024-03-27 11:01:51', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573691, '菜单编辑', ':sysManage:menuManage:edit', 'C', '/menuEdit', '/menu/menuEdit', NULL, '0', '0', '1', 152, '925644053573694', '0', 1, '2022-01-21 12:59:37', '2024-03-27 11:05:36', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573693, '搜索-按钮', ':sysManage:departmentManage:list:btnSearch', 'F', NULL, NULL, NULL, '0', '1', '1', 1115, '925644053573698', '0', 0, '2021-10-09 09:43:49', '2024-03-27 10:58:50', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573694, '菜单管理', ':sysManage:menuManage', 'C', '/menu', 'Layout', 'icon-caidanguanli', '0', '1', '1', 15, '925644053573655', '0', 1, '2022-01-21 13:06:36', '2024-03-27 10:52:28', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573695, '编辑(按钮)', ':sysManage:roleManage:list:btnEdit', 'F', NULL, NULL, NULL, '0', '1', '1', 1214, '925644053573666', '0', 1, '2021-10-08 14:27:05', '2024-03-27 11:01:53', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573697, '新增(按钮)', ':sysManage:userManage:list:btnAdd', 'F', NULL, NULL, NULL, '0', '1', '1', 1311, '925644053573687', '0', 0, '2021-10-08 14:20:05', '2024-03-27 11:03:26', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573698, '部门列表', ':sysManage:departmentManage:list', 'C', '/depList', '/department/depList', NULL, '0', '1', '1', 111, '925644053573670', '0', 1, '2022-01-08 13:14:51', '2024-06-19 08:41:14', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573699, '删除(按钮)', ':sysManage:roleManage:list:btnDel', 'F', NULL, NULL, NULL, '0', '1', '1', 1215, '925644053573666', '0', 1, '2021-10-08 14:27:24', '2024-03-27 11:01:55', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053574654, '用户详情', ':sysManage:userManage:detail', 'C', '/userDetail', '/user/userDetail', NULL, '0', '0', '1', 132, '925644053573662', '0', 0, '2021-10-08 16:47:31', '2024-03-27 11:02:38', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_power" VALUES (925644053573105, '日志详情', ':sysManage:logManage:detail', 'C', '/logDetail', '/log/logDetail', NULL, '0', '0', '1', 162, '925644053573653', '0', 1, '2021-10-08 16:56:13', '2024-03-27 11:06:01', 100000000000001, 100000000000001);

-- ----------------------------
-- Table structure for urm_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_role";
CREATE TABLE "public"."urm_role" (
                                     "id" int8 NOT NULL,
                                     "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                     "type" varchar(20) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                     "comments" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                     "is_editable" SMALLINT NOT NULL CHECK (is_editable IN (0, 1)) DEFAULT 1,
                                     "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                     "version" int4 DEFAULT 0,
                                     "create_date" timestamp(6),
                                     "update_date" timestamp(6) DEFAULT now(),
                                     "create_user" int8,
                                     "update_user" int8
)
;
COMMENT ON COLUMN "public"."urm_role"."id" IS '角色ID（UUID）';
COMMENT ON COLUMN "public"."urm_role"."name" IS '用户创建时输入的角色名称';
COMMENT ON COLUMN "public"."urm_role"."type" IS '角色类型';
COMMENT ON COLUMN "public"."urm_role"."comments" IS '备注';
COMMENT ON COLUMN "public"."urm_role"."is_editable" IS '可编辑状态(1: 可编辑;0:不可编辑)。系统预设角色不可编辑，其余角色均可编辑';
COMMENT ON COLUMN "public"."urm_role"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."urm_role"."version" IS '版本号';
COMMENT ON COLUMN "public"."urm_role"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."urm_role"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."urm_role"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."urm_role"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of urm_role
-- ----------------------------
INSERT INTO "public"."urm_role" VALUES (100000000000003, '领导', '系统角色', '可查看和管理所有部门用户的业务数据的角色', '0', '0', 0, '2025-07-03 11:04:29', '2025-07-03 11:04:32', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_role" VALUES (100000000000001, '管理员', '系统角色', '可管理整个系统的角色，如: 管理用户，管理权限等', '0', '0', 0, '2025-07-03 11:03:29', '2025-07-03 11:03:32', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_role" VALUES (100000000000002, '机载领导', '系统角色', '可查看和管理所有用户的业务数据的角色', '0', '0', 0, '2025-07-03 11:04:29', '2025-07-07 08:47:02', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_role" VALUES (100000000000004, '普通用户', '系统角色', '普通用户角色', '0', '0', 0, '2025-07-03 11:04:29', '2025-07-03 11:04:32', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_role" VALUES (100000000000005, '专家', '系统角色', '专家角色，进行审查结果审核', '0', '0', 0, '2026-01-30 11:04:29', '2026-01-30 11:04:32', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_role" VALUES (237378018947593, '测试01', '普通用户', '测试01', '1', '1', 0, '2025-07-14 11:31:57.916489', '2025-07-14 11:32:01.575194', 100000000000002, 100000000000002);

-- ----------------------------
-- Table structure for urm_rolepower
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_rolepower";
CREATE TABLE "public"."urm_rolepower" (
                                          "id" int8 NOT NULL,
                                          "role_id" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
                                          "power_id" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
                                          "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                          "version" int4 DEFAULT 0,
                                          "create_date" timestamp(6),
                                          "update_date" timestamp(6) DEFAULT now(),
                                          "create_user" int8,
                                          "update_user" int8
)
;
COMMENT ON COLUMN "public"."urm_rolepower"."id" IS '角色权限ID（UUID）';
COMMENT ON COLUMN "public"."urm_rolepower"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."urm_rolepower"."power_id" IS '权限ID';
COMMENT ON COLUMN "public"."urm_rolepower"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."urm_rolepower"."version" IS '版本号';
COMMENT ON COLUMN "public"."urm_rolepower"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."urm_rolepower"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."urm_rolepower"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."urm_rolepower"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of urm_rolepower
-- ----------------------------
INSERT INTO "public"."urm_rolepower" VALUES (428057672192003, '100000000000001', '925644053573651', '0', 0, '2022-01-29 22:10:59', '2022-01-29 22:10:59', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (428057672192004, '100000000000001', '925644053573665', '0', 0, '2022-01-29 22:10:59', '2022-01-29 22:10:59', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (428057672192022, '100000000000001', '925644053574654', '0', 0, '2022-01-29 22:10:59', '2022-01-29 22:10:59', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (428057672192023, '100000000000001', '925644053573694', '0', 0, '2022-01-29 22:10:59', '2022-01-29 22:10:59', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (428057672192024, '100000000000001', '925644053573678', '0', 0, '2022-01-29 22:10:59', '2022-01-29 22:10:59', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (428057672192025, '100000000000001', '925644053573691', '0', 0, '2022-01-29 22:10:59', '2022-01-29 22:10:59', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573110, '100000000000001', '925644053573655', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573113, '100000000000001', '925644053573182', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573117, '100000000000001', '925644053573653', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573119, '100000000000001', '925644053573634', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573121, '100000000000001', '925644053573670', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573123, '100000000000001', '925644053573698', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573124, '100000000000001', '925644053573105', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573125, '100000000000001', '925644053573666', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573127, '100000000000001', '925644053573673', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573128, '100000000000001', '925644053573650', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573129, '100000000000001', '925644053573680', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573130, '100000000000001', '925644053573683', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573131, '100000000000001', '925644053573693', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573132, '100000000000001', '925644053573664', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573134, '100000000000001', '925644053573689', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573135, '100000000000001', '925644053573676', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573136, '100000000000001', '925644053573695', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573137, '100000000000001', '925644053573686', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573138, '100000000000001', '925644053573699', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573139, '100000000000001', '925644053573635', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573140, '100000000000001', '925644053573638', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573141, '100000000000001', '925644053573687', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573142, '100000000000001', '925644053573679', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573143, '100000000000001', '925644053573697', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573144, '100000000000001', '925644053573662', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573145, '100000000000001', '925644053573163', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573146, '100000000000001', '925644053573641', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573147, '100000000000001', '925644053573654', '0', 0, '2021-12-06 12:00:24', '2021-12-06 12:00:24', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573148, '100000000000001', '825644053573656', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573149, '100000000000001', '825644053573657', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573150, '100000000000001', '825644053573658', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573151, '100000000000001', '825644053573659', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573152, '100000000000001', '825644053573660', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573153, '100000000000001', '825644053573661', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573154, '100000000000001', '825644053573662', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573157, '100000000000001', '725644053573655', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573158, '100000000000001', '725644053573656', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573159, '100000000000001', '725644053573657', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573160, '100000000000001', '725644053573658', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573161, '100000000000001', '725644053573659', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573162, '100000000000001', '825644053573665', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573163, '100000000000001', '825644053573666', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573164, '100000000000001', '825644053573667', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573165, '100000000000001', '825644053573668', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573166, '100000000000001', '825644053573669', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573167, '100000000000001', '825644053573670', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573168, '100000000000001', '825644053573671', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573169, '100000000000001', '725644053573666', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573261, '100000000000001', '825664053573691', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573268, '100000000000001', '725644053573665', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053573270, '100000000000001', '825644053573672', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574162, '100000000000001', '825664053573792', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574163, '100000000000001', '825664053573793', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574164, '100000000000001', '825664053573794', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574165, '100000000000001', '825664053573795', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574166, '100000000000001', '825664053573796', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574167, '100000000000001', '825664053573797', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_rolepower" VALUES (925644053574261, '100000000000001', '825664053573791', '0', 0, '2022-01-21 20:44:54', '2022-01-21 20:44:54', 100000000000001, 100000000000001);

-- ----------------------------
-- Table structure for urm_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_user";
CREATE TABLE "public"."urm_user" (
                                     "id" int8 NOT NULL,
                                     "department_id" int8 DEFAULT 0,
                                     "login_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                     "password" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                     "name" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                     "telephone" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                     "profile" text COLLATE "pg_catalog"."default",
                                     "work_no" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                     "locked" SMALLINT NOT NULL CHECK (locked IN (0, 1)) DEFAULT 1,
                                     "need_change_password" SMALLINT NOT NULL CHECK (need_change_password IN (0, 1)) DEFAULT 0,
                                     "is_editable" SMALLINT NOT NULL CHECK (is_editable IN (0, 1)) DEFAULT 1,
                                     "secret_level" int2 NOT NULL DEFAULT 4,
                                     "comments" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                     "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                     "version" int4 DEFAULT 0,
                                     "create_date" timestamp(6),
                                     "update_date" timestamp(6) DEFAULT now(),
                                     "create_user" int8,
                                     "update_user" int8
)
;
COMMENT ON COLUMN "public"."urm_user"."id" IS '用户ID（UUID）';
COMMENT ON COLUMN "public"."urm_user"."department_id" IS '机构id';
COMMENT ON COLUMN "public"."urm_user"."login_name" IS '登录名';
COMMENT ON COLUMN "public"."urm_user"."password" IS '密码';
COMMENT ON COLUMN "public"."urm_user"."name" IS '用户姓名';
COMMENT ON COLUMN "public"."urm_user"."telephone" IS '电话号码';
COMMENT ON COLUMN "public"."urm_user"."profile" IS '头像';
COMMENT ON COLUMN "public"."urm_user"."work_no" IS '工号';
COMMENT ON COLUMN "public"."urm_user"."locked" IS '是否被锁((0:正常;1:被锁)';
COMMENT ON COLUMN "public"."urm_user"."need_change_password" IS '下次登录是否需要修改密码(0:否;1:是)';
COMMENT ON COLUMN "public"."urm_user"."is_editable" IS '是否可编辑(0:不可编辑;1:可编辑)';
COMMENT ON COLUMN "public"."urm_user"."secret_level" IS '密级(1:核心涉密;2:重要涉密;3:一般涉密;4:非密)';
COMMENT ON COLUMN "public"."urm_user"."comments" IS '描述';
COMMENT ON COLUMN "public"."urm_user"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."urm_user"."version" IS '版本号';
COMMENT ON COLUMN "public"."urm_user"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."urm_user"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."urm_user"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."urm_user"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of urm_user
-- ----------------------------
INSERT INTO public.urm_user VALUES (100000000000001, 110000000000001, 'admin', 'bCf3351E4368cF0d11dEbb5ecAdfB6fB3eA05F823f897c5b', 'admin', '1135689644413', null, null, 0, 0, 0, 4, null, 0, 7, '2025-07-03 11:02:02.000000', '2025-07-03 11:02:04.000000', 100000000000001, 100000000000001);
INSERT INTO public.urm_user VALUES (100000000000002, 110000000000001, 'jzadmin', '29c467346634435382cAec7bbFdbF68312162D874a48cc04', '机载管理员', '1335689644413', null, null, 0, 1, 0, 4, null, 0, 5, '2025-07-03 11:02:02.000000', '2025-07-03 11:02:04.000000', 100000000000001, 0);
INSERT INTO public.urm_user VALUES (100000000000003, 110000000000002, '631admin', 'aC2f6db5268ed53e9fb95832a47aEa30d035dEf17cc1ef7e', '631管理员', '1345689644413', null, null, 0, 1, 0, 4, null, 0, 2, '2025-07-03 11:02:02.000000', '2025-07-03 11:02:04.000000', 100000000000001, 0);
INSERT INTO public.urm_user VALUES (100000000000004, 110000000000002, '631expert', 'aC2f6db5268ed53e9fb95832a47aEa30d035dEf17cc1ef7e', '631专家', '1345689644413', null, null, 0, 1, 0, 4, null, 0, 2, '2026-01-30 11:02:02.000000', '2026-01-30 11:02:04.000000', 100000000000001, 0);

-- ----------------------------
-- Table structure for urm_userlog
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_userlog";
CREATE TABLE "public"."urm_userlog" (
                                        "id" int8 NOT NULL,
                                        "content" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                        "model_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                        "operate_date" timestamp(6) NOT NULL DEFAULT now(),
                                        "user_id" varchar(22) COLLATE "pg_catalog"."default",
                                        "user_name" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                        "login_name" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."urm_userlog"."id" IS '日志ID';
COMMENT ON COLUMN "public"."urm_userlog"."content" IS '操作具体内容描述';
COMMENT ON COLUMN "public"."urm_userlog"."model_name" IS '操作模块';
COMMENT ON COLUMN "public"."urm_userlog"."operate_date" IS '操作时间';
COMMENT ON COLUMN "public"."urm_userlog"."user_id" IS '操作者id';
COMMENT ON COLUMN "public"."urm_userlog"."user_name" IS '用户昵称';
COMMENT ON COLUMN "public"."urm_userlog"."login_name" IS '登录名称';

-- ----------------------------
-- Table structure for urm_userrole
-- ----------------------------
DROP TABLE IF EXISTS "public"."urm_userrole";
CREATE TABLE "public"."urm_userrole" (
                                         "id" int8 NOT NULL,
                                         "user_id" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
                                         "role_id" varchar(22) COLLATE "pg_catalog"."default" NOT NULL,
                                         "is_delete" SMALLINT NOT NULL CHECK (is_delete IN (0, 1)) DEFAULT 0,
                                         "version" int4 DEFAULT 0,
                                         "create_date" timestamp(6),
                                         "update_date" timestamp(6) DEFAULT now(),
                                         "create_user" int8,
                                         "update_user" int8
)
;
COMMENT ON COLUMN "public"."urm_userrole"."id" IS '用户角色ID（UUID）';
COMMENT ON COLUMN "public"."urm_userrole"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."urm_userrole"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."urm_userrole"."is_delete" IS '是否删除(1: 删除;0:正常)';
COMMENT ON COLUMN "public"."urm_userrole"."version" IS '版本号';
COMMENT ON COLUMN "public"."urm_userrole"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."urm_userrole"."update_date" IS '最后更新时间';
COMMENT ON COLUMN "public"."urm_userrole"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."urm_userrole"."update_user" IS '最后更新人';

-- ----------------------------
-- Records of urm_userrole
-- ----------------------------
INSERT INTO "public"."urm_userrole" VALUES (237087256261664, '100000000000001', '100000000000001', '0', 0, '2025-07-07 21:47:02.054434', '2025-07-07 21:47:02.054434', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256261665, '100000000000002', '100000000000002', '0', 0, '2025-07-07 21:47:02.054434', '2025-07-07 21:47:02.054434', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256269346, '100000000000003', '100000000000003', '0', 0, '2025-07-07 21:47:02.066467', '2025-07-07 21:47:02.066467', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256277025, '100000000000004', '100000000000005', '0', 0, '2026-01-30 21:47:02.075939', '2026-01-30 21:47:02.075939', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256261641, '100000000000013', '100000000000004', '0', 0, '2025-07-07 21:47:02.054434', '2025-07-07 21:47:02.054434', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256261642, '100000000000023', '100000000000004', '0', 0, '2025-07-07 21:47:02.054434', '2025-07-07 21:47:02.054434', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256261643, '100000000000014', '100000000000004', '0', 0, '2025-07-07 21:47:02.054434', '2025-07-07 21:47:02.054434', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237087256261644, '100000000000024', '100000000000004', '0', 0, '2025-07-07 21:47:02.054434', '2025-07-07 21:47:02.054434', 0, 0);
INSERT INTO "public"."urm_userrole" VALUES (237375857069065, '237375282809865', '100000000000003', '0', 0, '2025-07-14 10:21:35.499801', '2025-07-14 10:21:35.499858', 100000000000001, 100000000000001);
INSERT INTO "public"."urm_userrole" VALUES (237383220739081, '237383212200457', '100000000000004', '0', 0, '2025-07-14 14:21:17.665297', '2025-07-14 14:21:17.665369', 100000000000001, 100000000000001);

-- ----------------------------
-- Primary Key structure for table pot_application
-- ----------------------------
ALTER TABLE "public"."pot_application" ADD CONSTRAINT "pot_application_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table pot_information
-- ----------------------------
ALTER TABLE "public"."pot_information" ADD CONSTRAINT "pot_information_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_department
-- ----------------------------
ALTER TABLE "public"."urm_department" ADD CONSTRAINT "urm_department_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_power
-- ----------------------------
ALTER TABLE "public"."urm_power" ADD CONSTRAINT "urm_power_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_role
-- ----------------------------
ALTER TABLE "public"."urm_role" ADD CONSTRAINT "urm_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_rolepower
-- ----------------------------
ALTER TABLE "public"."urm_rolepower" ADD CONSTRAINT "urm_rolepower_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_user
-- ----------------------------
ALTER TABLE "public"."urm_user" ADD CONSTRAINT "urm_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_userlog
-- ----------------------------
ALTER TABLE "public"."urm_userlog" ADD CONSTRAINT "urm_userlog_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table urm_userrole
-- ----------------------------
ALTER TABLE "public"."urm_userrole" ADD CONSTRAINT "urm_userrole_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- 以下索引用于优化查询,再性能BUG复现后添加
-- ----------------------------

CREATE INDEX IF NOT EXISTS "userrole_user_id_index" ON "urm_userrole" USING btree (
    "user_id"
    );


CREATE INDEX IF NOT EXISTS "department_f_id_index" ON "urm_department" USING btree (
    "f_id"
    );

CREATE INDEX IF NOT EXISTS "department_name_index" ON "urm_department" USING btree (
    "name"
    );

