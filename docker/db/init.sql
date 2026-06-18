--
-- PostgreSQL database dump
--

\restrict CunaKHXLrjl5uayrmVDgZggEFG35t8jDuVxBA1v79pu2mH2R5xBxhLfzzjqAlMV

-- Dumped from database version 14.23
-- Dumped by pg_dump version 14.23

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: code_yjfk_answer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.code_yjfk_answer (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    answer text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    ref_id bigint
);


--
-- Name: COLUMN code_yjfk_answer.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.id IS 'ID（UUID）';


--
-- Name: COLUMN code_yjfk_answer.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.f_id IS '父ID(外键,关联到反馈意见主表或当前表)';


--
-- Name: COLUMN code_yjfk_answer.answer; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.answer IS '回复内容';


--
-- Name: COLUMN code_yjfk_answer.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.comments IS '备注';


--
-- Name: COLUMN code_yjfk_answer.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN code_yjfk_answer.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.version IS '版本号';


--
-- Name: COLUMN code_yjfk_answer.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.create_date IS '创建时间';


--
-- Name: COLUMN code_yjfk_answer.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.update_date IS '最后更新时间';


--
-- Name: COLUMN code_yjfk_answer.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.create_user IS '创建人';


--
-- Name: COLUMN code_yjfk_answer.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.update_user IS '最后更新人';


--
-- Name: COLUMN code_yjfk_answer.ref_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_answer.ref_id IS '参照的回复ID(外键,关联到当前表)';


--
-- Name: code_yjfk_append_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.code_yjfk_append_file (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    type character varying(100) NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN code_yjfk_append_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.id IS 'ID（UUID）';


--
-- Name: COLUMN code_yjfk_append_file.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.f_id IS '外键,关联到主记录表';


--
-- Name: COLUMN code_yjfk_append_file.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.type IS '附件类型';


--
-- Name: COLUMN code_yjfk_append_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN code_yjfk_append_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN code_yjfk_append_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.comments IS '备注';


--
-- Name: COLUMN code_yjfk_append_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN code_yjfk_append_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.version IS '版本号';


--
-- Name: COLUMN code_yjfk_append_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.create_date IS '创建时间';


--
-- Name: COLUMN code_yjfk_append_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.update_date IS '最后更新时间';


--
-- Name: COLUMN code_yjfk_append_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.create_user IS '创建人';


--
-- Name: COLUMN code_yjfk_append_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_append_file.update_user IS '最后更新人';


--
-- Name: code_yjfk_suggestion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.code_yjfk_suggestion (
    id bigint NOT NULL,
    title character varying(500) DEFAULT NULL::character varying,
    suggestion text,
    status integer,
    description character varying(1000) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    file_id bigint,
    file_version_id bigint,
    result_id bigint
);


--
-- Name: COLUMN code_yjfk_suggestion.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.id IS 'ID（UUID）';


--
-- Name: COLUMN code_yjfk_suggestion.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.title IS '标题';


--
-- Name: COLUMN code_yjfk_suggestion.suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.suggestion IS '反馈意见';


--
-- Name: COLUMN code_yjfk_suggestion.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.status IS '反馈状态(1:open;2:reopen;3:closed)';


--
-- Name: COLUMN code_yjfk_suggestion.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.description IS '意见描述';


--
-- Name: COLUMN code_yjfk_suggestion.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.comments IS '备注';


--
-- Name: COLUMN code_yjfk_suggestion.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN code_yjfk_suggestion.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.version IS '版本号';


--
-- Name: COLUMN code_yjfk_suggestion.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.create_date IS '创建时间';


--
-- Name: COLUMN code_yjfk_suggestion.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.update_date IS '最后更新时间';


--
-- Name: COLUMN code_yjfk_suggestion.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.create_user IS '创建人';


--
-- Name: COLUMN code_yjfk_suggestion.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.update_user IS '最后更新人';


--
-- Name: COLUMN code_yjfk_suggestion.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.file_id IS '文件ID';


--
-- Name: COLUMN code_yjfk_suggestion.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.file_version_id IS '文件版本ID';


--
-- Name: COLUMN code_yjfk_suggestion.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion.result_id IS '审查结果ID';


--
-- Name: code_yjfk_suggestion_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.code_yjfk_suggestion_status (
    id bigint NOT NULL,
    suggestion_id bigint,
    status integer,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN code_yjfk_suggestion_status.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.id IS 'ID（UUID）';


--
-- Name: COLUMN code_yjfk_suggestion_status.suggestion_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.suggestion_id IS '父ID(外键,关联到反馈意见主表或当前表)';


--
-- Name: COLUMN code_yjfk_suggestion_status.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.status IS '反馈状态(0:new;1:open;2:reopen;3:closed)';


--
-- Name: COLUMN code_yjfk_suggestion_status.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.comments IS '备注';


--
-- Name: COLUMN code_yjfk_suggestion_status.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN code_yjfk_suggestion_status.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.version IS '版本号';


--
-- Name: COLUMN code_yjfk_suggestion_status.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.create_date IS '创建时间';


--
-- Name: COLUMN code_yjfk_suggestion_status.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.update_date IS '最后更新时间';


--
-- Name: COLUMN code_yjfk_suggestion_status.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.create_user IS '创建人';


--
-- Name: COLUMN code_yjfk_suggestion_status.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.code_yjfk_suggestion_status.update_user IS '最后更新人';


--
-- Name: dlsc_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_file (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    file_path character varying(500) DEFAULT NULL::character varying,
    file_save_name character varying(500) DEFAULT NULL::character varying,
    secret_level smallint DEFAULT 4 NOT NULL,
    department_id bigint,
    owner_id bigint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_recycle smallint DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    closed_loop_file_version_id bigint,
    closed_loop_result_id bigint,
    closed_loop_file_version integer,
    latest_file_version_id bigint,
    latest_file_version integer,
    is_closed_loop smallint DEFAULT 0 NOT NULL,
    compatible_models character varying(500),
    product_model character varying(500),
    product_name character varying(500),
    diagram_number character varying(500),
    diagram_version character varying(500),
    max_check_points integer,
    CONSTRAINT dlsc_file_is_closed_loop_check CHECK ((is_closed_loop = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_file_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_file_is_delete_check1 CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_file_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_file_new_is_recycle_check CHECK ((is_recycle = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN dlsc_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN dlsc_file.file_path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.file_path IS '服务器上文件保存的路径';


--
-- Name: COLUMN dlsc_file.file_save_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.file_save_name IS '服务器上文件保存的名称';


--
-- Name: COLUMN dlsc_file.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.secret_level IS '密级(1:内部;2:受控;3:商密;4:公开)';


--
-- Name: COLUMN dlsc_file.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.department_id IS '文件隶属部门Id';


--
-- Name: COLUMN dlsc_file.owner_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.owner_id IS '文件用户Id';


--
-- Name: COLUMN dlsc_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.comments IS '备注';


--
-- Name: COLUMN dlsc_file.is_recycle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.is_recycle IS '是否移入文件回收站(1:是; 0:否)';


--
-- Name: COLUMN dlsc_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.version IS '版本号';


--
-- Name: COLUMN dlsc_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.create_user IS '创建人';


--
-- Name: COLUMN dlsc_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.update_user IS '最后更新人';


--
-- Name: COLUMN dlsc_file.closed_loop_file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.closed_loop_file_version_id IS '问题闭环文件版本id';


--
-- Name: COLUMN dlsc_file.closed_loop_result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.closed_loop_result_id IS '问题闭环审查结果id';


--
-- Name: COLUMN dlsc_file.closed_loop_file_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.closed_loop_file_version IS '问题闭环文件版本序号';


--
-- Name: COLUMN dlsc_file.latest_file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.latest_file_version_id IS '最新文件版本id';


--
-- Name: COLUMN dlsc_file.latest_file_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.latest_file_version IS '最新文件版本序号';


--
-- Name: COLUMN dlsc_file.is_closed_loop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.is_closed_loop IS '问题闭环状态(0:未闭环;1:已闭环)';


--
-- Name: COLUMN dlsc_file.compatible_models; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.compatible_models IS '配套机型';


--
-- Name: COLUMN dlsc_file.product_model; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.product_model IS '产品型号';


--
-- Name: COLUMN dlsc_file.product_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.product_name IS '产品名称';


--
-- Name: COLUMN dlsc_file.diagram_number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.diagram_number IS '电路原理图号';


--
-- Name: COLUMN dlsc_file.diagram_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.diagram_version IS '电路原理图版本';


--
-- Name: COLUMN dlsc_file.max_check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file.max_check_points IS '最大审查点数';


--
-- Name: dlsc_file_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_file_version (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    minio_id character varying(100) NOT NULL,
    file_version integer DEFAULT 1,
    file_name character varying(500) DEFAULT NULL::character varying,
    file_origin_name character varying(500) DEFAULT NULL::character varying,
    secret_level smallint DEFAULT 4 NOT NULL,
    department_id bigint,
    owner_id bigint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_recycle smallint DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT dlsc_file_version_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_file_version_is_recycle_check CHECK ((is_recycle = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_file_version.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.id IS '角色ID（UUID）';


--
-- Name: COLUMN dlsc_file_version.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.file_id IS '所属文件ID,关联到文件表';


--
-- Name: COLUMN dlsc_file_version.minio_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.minio_id IS '文件在minio的ID';


--
-- Name: COLUMN dlsc_file_version.file_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.file_version IS '文件版本序号';


--
-- Name: COLUMN dlsc_file_version.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.file_name IS '文件版本名称';


--
-- Name: COLUMN dlsc_file_version.file_origin_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.file_origin_name IS '文件上传时的原始名称';


--
-- Name: COLUMN dlsc_file_version.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.secret_level IS '密级(1:内部;2:受控;3:商密;4:公开)';


--
-- Name: COLUMN dlsc_file_version.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.department_id IS '文件隶属部门Id';


--
-- Name: COLUMN dlsc_file_version.owner_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.owner_id IS '文件用户Id';


--
-- Name: COLUMN dlsc_file_version.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.comments IS '备注';


--
-- Name: COLUMN dlsc_file_version.is_recycle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.is_recycle IS '是否移入文件回收站(1:是; 0:否)';


--
-- Name: COLUMN dlsc_file_version.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_file_version.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.version IS '版本号';


--
-- Name: COLUMN dlsc_file_version.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_file_version.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_file_version.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.create_user IS '创建人';


--
-- Name: COLUMN dlsc_file_version.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_file_version.update_user IS '最后更新人';


--
-- Name: dlsc_naming_convention; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_naming_convention (
    id bigint NOT NULL,
    title character varying(500) DEFAULT NULL::character varying,
    content text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN dlsc_naming_convention.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_naming_convention.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.title IS '标题';


--
-- Name: COLUMN dlsc_naming_convention.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.content IS '内容';


--
-- Name: COLUMN dlsc_naming_convention.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.comments IS '备注';


--
-- Name: COLUMN dlsc_naming_convention.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_naming_convention.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.version IS '版本号';


--
-- Name: COLUMN dlsc_naming_convention.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_naming_convention.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_naming_convention.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.create_user IS '创建人';


--
-- Name: COLUMN dlsc_naming_convention.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_naming_convention.update_user IS '最后更新人';


--
-- Name: dlsc_review_issue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_review_issue (
    id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_version_id bigint NOT NULL,
    result_id bigint NOT NULL,
    result_detail_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    device_type text,
    tag_pin text,
    review_suggestion text,
    rule_code character varying(500) DEFAULT NULL::character varying,
    review_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN dlsc_review_issue.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_review_issue.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.file_id IS '外键,关联到文件表';


--
-- Name: COLUMN dlsc_review_issue.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.file_version_id IS '外键,关联到文件版本表';


--
-- Name: COLUMN dlsc_review_issue.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.result_id IS '外键,关联到结果表';


--
-- Name: COLUMN dlsc_review_issue.result_detail_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.result_detail_id IS '外键,关联到结果详情表';


--
-- Name: COLUMN dlsc_review_issue.rule_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.rule_id IS '外键,关联到规则表';


--
-- Name: COLUMN dlsc_review_issue.device_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.device_type IS '器件型号(可为空)';


--
-- Name: COLUMN dlsc_review_issue.tag_pin; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.tag_pin IS '位号引脚(单个位号引脚，可为空)';


--
-- Name: COLUMN dlsc_review_issue.review_suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.review_suggestion IS '审查意见';


--
-- Name: COLUMN dlsc_review_issue.rule_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.rule_code IS '规则编号';


--
-- Name: COLUMN dlsc_review_issue.review_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.review_time IS '审查时间';


--
-- Name: COLUMN dlsc_review_issue.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.comments IS '备注';


--
-- Name: COLUMN dlsc_review_issue.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_review_issue.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.version IS '版本号';


--
-- Name: COLUMN dlsc_review_issue.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_review_issue.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_review_issue.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.create_user IS '创建人';


--
-- Name: COLUMN dlsc_review_issue.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_issue.update_user IS '最后更新人';


--
-- Name: dlsc_review_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_review_result (
    id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_version_id bigint NOT NULL,
    check_points integer,
    pass_check_points integer,
    pass_rate numeric(15,5) DEFAULT NULL::numeric,
    is_closed_loop smallint DEFAULT 0 NOT NULL,
    review_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    status integer,
    error_message text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    closed_fail_check_points integer,
    is_in_audit smallint DEFAULT 0 NOT NULL,
    total_fail_check_points integer,
    CONSTRAINT dlsc_review_result_is_in_audit_check CHECK ((is_in_audit = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_new_is_closed_loop_check CHECK ((is_closed_loop = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_review_result.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_review_result.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.file_id IS '外键,关联到文件表';


--
-- Name: COLUMN dlsc_review_result.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.file_version_id IS '外键,关联到文件版本表';


--
-- Name: COLUMN dlsc_review_result.check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.check_points IS '检查点数量';


--
-- Name: COLUMN dlsc_review_result.pass_check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.pass_check_points IS '通过的检查点数量';


--
-- Name: COLUMN dlsc_review_result.pass_rate; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.pass_rate IS '通过率';


--
-- Name: COLUMN dlsc_review_result.review_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.review_time IS '开始审查的时间';


--
-- Name: COLUMN dlsc_review_result.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.status IS '状态(1:正在审查;2:审查完成)';


--
-- Name: COLUMN dlsc_review_result.error_message; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.error_message IS 'Python API错误信息';


--
-- Name: COLUMN dlsc_review_result.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.comments IS '备注';


--
-- Name: COLUMN dlsc_review_result.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_review_result.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.version IS '版本号';


--
-- Name: COLUMN dlsc_review_result.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_review_result.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_review_result.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.create_user IS '创建人';


--
-- Name: COLUMN dlsc_review_result.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.update_user IS '最后更新人';


--
-- Name: COLUMN dlsc_review_result.closed_fail_check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.closed_fail_check_points IS '关闭的不通过审查点数量';


--
-- Name: COLUMN dlsc_review_result.is_in_audit; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.is_in_audit IS '问题审核状态(0:未审核;1:审核中)';


--
-- Name: COLUMN dlsc_review_result.total_fail_check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result.total_fail_check_points IS '所有问题数量';


--
-- Name: dlsc_review_result_audit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_review_result_audit (
    id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_version_id bigint NOT NULL,
    result_id bigint NOT NULL,
    is_audit_finished smallint DEFAULT 0 NOT NULL,
    audit_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    is_admin_audit_finished smallint DEFAULT 0 NOT NULL,
    is_expert_audit_finished smallint DEFAULT 0 NOT NULL,
    CONSTRAINT dlsc_review_result_audit_is_admin_audit_finished_check CHECK ((is_admin_audit_finished = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_audit_is_audit_finished_check CHECK ((is_audit_finished = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_audit_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_audit_is_expert_audit_finished_check CHECK ((is_expert_audit_finished = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_review_result_audit.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_review_result_audit.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.file_id IS '外键,关联到文件表';


--
-- Name: COLUMN dlsc_review_result_audit.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.file_version_id IS '外键,关联到文件版本表';


--
-- Name: COLUMN dlsc_review_result_audit.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.result_id IS '外键,关联到审查结果表';


--
-- Name: COLUMN dlsc_review_result_audit.is_audit_finished; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.is_audit_finished IS '问题审核状态(0:待审核;1:已审核)';


--
-- Name: COLUMN dlsc_review_result_audit.audit_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.audit_time IS '开始审核的时间';


--
-- Name: COLUMN dlsc_review_result_audit.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_review_result_audit.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.version IS '版本号';


--
-- Name: COLUMN dlsc_review_result_audit.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_review_result_audit.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_review_result_audit.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.create_user IS '创建人';


--
-- Name: COLUMN dlsc_review_result_audit.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.update_user IS '最后更新人';


--
-- Name: COLUMN dlsc_review_result_audit.is_admin_audit_finished; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.is_admin_audit_finished IS '管理员问题审核状态(0:待审核;1:已审核)';


--
-- Name: COLUMN dlsc_review_result_audit.is_expert_audit_finished; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_audit.is_expert_audit_finished IS '专家问题审核状态(0:待审核;1:已审核)';


--
-- Name: dlsc_review_result_detail; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_review_result_detail (
    id bigint NOT NULL,
    result_id bigint,
    rule_id bigint,
    device_type text DEFAULT NULL::character varying,
    tag_pin text,
    review_suggestion text,
    is_passed smallint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    rule_code character varying(500) DEFAULT NULL::character varying,
    audit_type integer,
    approved_audit_type integer,
    issue_feedback text DEFAULT NULL::character varying,
    CONSTRAINT dlsc_review_result_detail_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_detail_is_passed_check CHECK ((is_passed = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_detail_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_review_result_detail_new_is_passed_check CHECK ((is_passed = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_review_result_detail.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_review_result_detail.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.result_id IS '外键,关联到结果表';


--
-- Name: COLUMN dlsc_review_result_detail.rule_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.rule_id IS '外键,关联到规则表';


--
-- Name: COLUMN dlsc_review_result_detail.device_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.device_type IS '器件型号';


--
-- Name: COLUMN dlsc_review_result_detail.tag_pin; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.tag_pin IS '位号引脚';


--
-- Name: COLUMN dlsc_review_result_detail.review_suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.review_suggestion IS '审查意见';


--
-- Name: COLUMN dlsc_review_result_detail.is_passed; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.is_passed IS '是否通过(0:未通过;1:通过)';


--
-- Name: COLUMN dlsc_review_result_detail.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.comments IS '备注';


--
-- Name: COLUMN dlsc_review_result_detail.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_review_result_detail.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.version IS '版本号';


--
-- Name: COLUMN dlsc_review_result_detail.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_review_result_detail.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_review_result_detail.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.create_user IS '创建人';


--
-- Name: COLUMN dlsc_review_result_detail.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.update_user IS '最后更新人';


--
-- Name: COLUMN dlsc_review_result_detail.rule_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.rule_code IS '规则编号';


--
-- Name: COLUMN dlsc_review_result_detail.audit_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.audit_type IS '审核类型';


--
-- Name: COLUMN dlsc_review_result_detail.approved_audit_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.approved_audit_type IS '批准的审核类型';


--
-- Name: COLUMN dlsc_review_result_detail.issue_feedback; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail.issue_feedback IS '问题反馈';


--
-- Name: dlsc_review_result_detail_audit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_review_result_detail_audit (
    id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_version_id bigint NOT NULL,
    result_id bigint NOT NULL,
    result_detail_id bigint NOT NULL,
    result_audit_id bigint NOT NULL,
    audit_type integer,
    issue_feedback text DEFAULT NULL::character varying,
    status integer NOT NULL,
    audit_submit_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    audit_close_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    reject_reason text DEFAULT NULL::character varying,
    inherited_detail_audit_id bigint,
    CONSTRAINT dlsc_review_result_detail_audit_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_review_result_detail_audit.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_review_result_detail_audit.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.file_id IS '外键,关联到文件表';


--
-- Name: COLUMN dlsc_review_result_detail_audit.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.file_version_id IS '外键,关联到文件版本表';


--
-- Name: COLUMN dlsc_review_result_detail_audit.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.result_id IS '外键,关联到结果表';


--
-- Name: COLUMN dlsc_review_result_detail_audit.result_detail_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.result_detail_id IS '外键,关联到审查结果详情表';


--
-- Name: COLUMN dlsc_review_result_detail_audit.result_audit_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.result_audit_id IS '外键,关联到电路审查结果审核表';


--
-- Name: COLUMN dlsc_review_result_detail_audit.audit_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.audit_type IS '审核类型';


--
-- Name: COLUMN dlsc_review_result_detail_audit.issue_feedback; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.issue_feedback IS '问题反馈';


--
-- Name: COLUMN dlsc_review_result_detail_audit.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.status IS '问题审核状态(1:审核中;2:审核通过;3:审核拒绝)';


--
-- Name: COLUMN dlsc_review_result_detail_audit.audit_submit_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.audit_submit_time IS '提交审核的时间';


--
-- Name: COLUMN dlsc_review_result_detail_audit.audit_close_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.audit_close_time IS '审核完成的时间';


--
-- Name: COLUMN dlsc_review_result_detail_audit.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_review_result_detail_audit.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.version IS '版本号';


--
-- Name: COLUMN dlsc_review_result_detail_audit.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_review_result_detail_audit.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_review_result_detail_audit.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.create_user IS '创建人';


--
-- Name: COLUMN dlsc_review_result_detail_audit.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.update_user IS '最后更新人';


--
-- Name: COLUMN dlsc_review_result_detail_audit.reject_reason; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.reject_reason IS '拒绝原因';


--
-- Name: COLUMN dlsc_review_result_detail_audit.inherited_detail_audit_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_review_result_detail_audit.inherited_detail_audit_id IS '继承的结果详情复核记录Id';


--
-- Name: dlsc_rule; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_rule (
    id bigint NOT NULL,
    type integer,
    name character varying(1000) DEFAULT NULL::character varying NOT NULL,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    is_deprecate smallint DEFAULT 0 NOT NULL,
    code character varying(500) DEFAULT NULL::character varying NOT NULL,
    CONSTRAINT dlsc_rule_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_rule_is_deprecate_check CHECK ((is_deprecate = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_rule_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_rule.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_rule.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.type IS '规则类型(1:通用规则;2:元器件手册规则;3:技术规则)';


--
-- Name: COLUMN dlsc_rule.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.name IS '规则名称';


--
-- Name: COLUMN dlsc_rule.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.comments IS '备注';


--
-- Name: COLUMN dlsc_rule.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_rule.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.version IS '版本号';


--
-- Name: COLUMN dlsc_rule.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_rule.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_rule.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.create_user IS '创建人';


--
-- Name: COLUMN dlsc_rule.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.update_user IS '最后更新人';


--
-- Name: COLUMN dlsc_rule.is_deprecate; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.is_deprecate IS '是否废弃';


--
-- Name: COLUMN dlsc_rule.code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_rule.code IS '规则编号';


--
-- Name: dlsc_tool_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_tool_file (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    tool_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT dlsc_tool_file_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dlsc_tool_file_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dlsc_tool_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.id IS '工具文件ID（UUID）';


--
-- Name: COLUMN dlsc_tool_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN dlsc_tool_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN dlsc_tool_file.tool_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.tool_name IS '工具名称';


--
-- Name: COLUMN dlsc_tool_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.comments IS '备注';


--
-- Name: COLUMN dlsc_tool_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_tool_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.version IS '版本号';


--
-- Name: COLUMN dlsc_tool_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_tool_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_tool_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.create_user IS '创建人';


--
-- Name: COLUMN dlsc_tool_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_tool_file.update_user IS '最后更新人';


--
-- Name: dlsc_update_note; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dlsc_update_note (
    id bigint NOT NULL,
    update_time timestamp(6) without time zone,
    content text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN dlsc_update_note.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.id IS 'ID（UUID）';


--
-- Name: COLUMN dlsc_update_note.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.update_time IS '更新时间';


--
-- Name: COLUMN dlsc_update_note.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.content IS '更新内容';


--
-- Name: COLUMN dlsc_update_note.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.comments IS '备注';


--
-- Name: COLUMN dlsc_update_note.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dlsc_update_note.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.version IS '版本号';


--
-- Name: COLUMN dlsc_update_note.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.create_date IS '创建时间';


--
-- Name: COLUMN dlsc_update_note.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.update_date IS '最后更新时间';


--
-- Name: COLUMN dlsc_update_note.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.create_user IS '创建人';


--
-- Name: COLUMN dlsc_update_note.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dlsc_update_note.update_user IS '最后更新人';


--
-- Name: dmsc_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dmsc_file (
    id bigint DEFAULT 4 NOT NULL,
    minio_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    file_path character varying(500) DEFAULT NULL::character varying,
    file_save_name character varying(500) DEFAULT NULL::character varying,
    secret_level smallint DEFAULT 4 NOT NULL,
    department_id bigint,
    owner_id bigint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_recycle smallint DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT dmsc_file_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dmsc_file_is_delete_check1 CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dmsc_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.id IS 'ID（UUID）';


--
-- Name: COLUMN dmsc_file.minio_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.minio_id IS '文件在Minio上的ID';


--
-- Name: COLUMN dmsc_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN dmsc_file.file_path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.file_path IS '服务器上文件保存的路径';


--
-- Name: COLUMN dmsc_file.file_save_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.file_save_name IS '服务器上文件保存的名称';


--
-- Name: COLUMN dmsc_file.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.secret_level IS '密级(1:内部;2:受控;3:商密;4:公开)';


--
-- Name: COLUMN dmsc_file.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.department_id IS '文件隶属部门Id';


--
-- Name: COLUMN dmsc_file.owner_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.owner_id IS '文件用户Id';


--
-- Name: COLUMN dmsc_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.comments IS '备注';


--
-- Name: COLUMN dmsc_file.is_recycle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.is_recycle IS '是否移入文件回收站(1:是; 0:否)';


--
-- Name: COLUMN dmsc_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dmsc_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.version IS '版本号';


--
-- Name: COLUMN dmsc_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.create_date IS '创建时间';


--
-- Name: COLUMN dmsc_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.update_date IS '最后更新时间';


--
-- Name: COLUMN dmsc_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.create_user IS '创建人';


--
-- Name: COLUMN dmsc_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file.update_user IS '最后更新人';


--
-- Name: dmsc_file_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dmsc_file_version (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    minio_id character varying(100) NOT NULL,
    file_version integer DEFAULT 1,
    file_name character varying(500) DEFAULT NULL::character varying,
    file_origin_name character varying(500) DEFAULT NULL::character varying,
    secret_level smallint DEFAULT 4 NOT NULL,
    department_id bigint,
    owner_id bigint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_recycle smallint DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT dmsc_file_version_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dmsc_file_version_is_delete_check1 CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dmsc_file_version.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.id IS '角色ID（UUID）';


--
-- Name: COLUMN dmsc_file_version.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.file_id IS '所属文件ID,关联到文件表';


--
-- Name: COLUMN dmsc_file_version.minio_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.minio_id IS '文件在minio的ID';


--
-- Name: COLUMN dmsc_file_version.file_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.file_version IS '文件版本序号';


--
-- Name: COLUMN dmsc_file_version.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.file_name IS '文件版本名称';


--
-- Name: COLUMN dmsc_file_version.file_origin_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.file_origin_name IS '文件上传时的原始名称';


--
-- Name: COLUMN dmsc_file_version.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.secret_level IS '密级(1:内部;2:受控;3:商密;4:公开)';


--
-- Name: COLUMN dmsc_file_version.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.department_id IS '文件隶属部门Id';


--
-- Name: COLUMN dmsc_file_version.owner_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.owner_id IS '文件用户Id';


--
-- Name: COLUMN dmsc_file_version.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.comments IS '备注';


--
-- Name: COLUMN dmsc_file_version.is_recycle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.is_recycle IS '是否移入文件回收站(1:是; 0:否)';


--
-- Name: COLUMN dmsc_file_version.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dmsc_file_version.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.version IS '版本号';


--
-- Name: COLUMN dmsc_file_version.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.create_date IS '创建时间';


--
-- Name: COLUMN dmsc_file_version.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.update_date IS '最后更新时间';


--
-- Name: COLUMN dmsc_file_version.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.create_user IS '创建人';


--
-- Name: COLUMN dmsc_file_version.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_file_version.update_user IS '最后更新人';


--
-- Name: dmsc_review_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dmsc_review_result (
    id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_version_id bigint NOT NULL,
    check_points integer,
    pass_check_points integer,
    pass_rate numeric(15,5) DEFAULT NULL::numeric,
    is_closed_loop smallint DEFAULT 0 NOT NULL,
    review_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    status integer,
    error_message text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    duration integer,
    files_size integer,
    files_line integer,
    use_rule_size integer,
    questions integer,
    CONSTRAINT dmsc_review_result_is_closed_loop_check CHECK ((is_closed_loop = ANY (ARRAY[0, 1]))),
    CONSTRAINT dmsc_review_result_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dmsc_review_result.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.id IS 'ID（UUID）';


--
-- Name: COLUMN dmsc_review_result.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.file_id IS '外键,关联到文件表';


--
-- Name: COLUMN dmsc_review_result.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.file_version_id IS '外键,关联到文件版本表';


--
-- Name: COLUMN dmsc_review_result.check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.check_points IS '检查点数量';


--
-- Name: COLUMN dmsc_review_result.pass_check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.pass_check_points IS '通过的检查点数量';


--
-- Name: COLUMN dmsc_review_result.pass_rate; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.pass_rate IS '通过率';


--
-- Name: COLUMN dmsc_review_result.is_closed_loop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.is_closed_loop IS '闭环状态';


--
-- Name: COLUMN dmsc_review_result.review_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.review_time IS '开始审查的时间';


--
-- Name: COLUMN dmsc_review_result.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.status IS '状态(1:正在审查;2:审查完成)';


--
-- Name: COLUMN dmsc_review_result.error_message; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.error_message IS '底层API错误信息';


--
-- Name: COLUMN dmsc_review_result.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.comments IS '备注';


--
-- Name: COLUMN dmsc_review_result.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dmsc_review_result.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.version IS '版本号';


--
-- Name: COLUMN dmsc_review_result.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.create_date IS '创建时间';


--
-- Name: COLUMN dmsc_review_result.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.update_date IS '最后更新时间';


--
-- Name: COLUMN dmsc_review_result.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.create_user IS '创建人';


--
-- Name: COLUMN dmsc_review_result.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.update_user IS '最后更新人';


--
-- Name: COLUMN dmsc_review_result.duration; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.duration IS '耗时(秒)';


--
-- Name: COLUMN dmsc_review_result.files_size; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.files_size IS '文件数';


--
-- Name: COLUMN dmsc_review_result.files_line; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.files_line IS '文件行数';


--
-- Name: COLUMN dmsc_review_result.use_rule_size; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.use_rule_size IS '使用规则数';


--
-- Name: COLUMN dmsc_review_result.questions; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result.questions IS '问题数量';


--
-- Name: dmsc_review_result_detail; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dmsc_review_result_detail (
    id bigint NOT NULL,
    result_id bigint,
    rule_id bigint,
    source_file_name character varying(500) DEFAULT NULL::character varying,
    language character varying(100) DEFAULT NULL::character varying,
    error_code text,
    line_number character varying(100) DEFAULT NULL::character varying,
    error_reason text,
    review_suggestion text,
    is_passed smallint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    rule_code character varying(500) DEFAULT NULL::character varying,
    question_id bigint,
    recheck_conclusion character varying(500),
    question_desc text,
    recheck_status integer DEFAULT 0,
    recheck_result_status integer DEFAULT 0,
    reject_reason character varying(500),
    recheck_user_id bigint,
    CONSTRAINT dmsc_review_result_detail_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT dmsc_review_result_detail_is_passed_check CHECK ((is_passed = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dmsc_review_result_detail.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.id IS 'ID（UUID）';


--
-- Name: COLUMN dmsc_review_result_detail.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.result_id IS '外键,关联到结果表';


--
-- Name: COLUMN dmsc_review_result_detail.rule_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.rule_id IS '外键,关联到规则表';


--
-- Name: COLUMN dmsc_review_result_detail.source_file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.source_file_name IS '源代码文件名';


--
-- Name: COLUMN dmsc_review_result_detail.language; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.language IS '代码语言';


--
-- Name: COLUMN dmsc_review_result_detail.error_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.error_code IS '错误原因';


--
-- Name: COLUMN dmsc_review_result_detail.line_number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.line_number IS '代码行号';


--
-- Name: COLUMN dmsc_review_result_detail.review_suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.review_suggestion IS '审查意见';


--
-- Name: COLUMN dmsc_review_result_detail.is_passed; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.is_passed IS '是否通过(0:未通过;1:通过)';


--
-- Name: COLUMN dmsc_review_result_detail.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.comments IS '备注';


--
-- Name: COLUMN dmsc_review_result_detail.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dmsc_review_result_detail.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.version IS '版本号';


--
-- Name: COLUMN dmsc_review_result_detail.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.create_date IS '创建时间';


--
-- Name: COLUMN dmsc_review_result_detail.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.update_date IS '最后更新时间';


--
-- Name: COLUMN dmsc_review_result_detail.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.create_user IS '创建人';


--
-- Name: COLUMN dmsc_review_result_detail.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.update_user IS '最后更新人';


--
-- Name: COLUMN dmsc_review_result_detail.rule_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.rule_code IS '规则编号';


--
-- Name: COLUMN dmsc_review_result_detail.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.question_id IS '问题ID';


--
-- Name: COLUMN dmsc_review_result_detail.recheck_conclusion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.recheck_conclusion IS '审查结论';


--
-- Name: COLUMN dmsc_review_result_detail.question_desc; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.question_desc IS '问题描述';


--
-- Name: COLUMN dmsc_review_result_detail.recheck_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.recheck_status IS '复核状态(1:未复核;2:复核中;3:复核完成)';


--
-- Name: COLUMN dmsc_review_result_detail.recheck_result_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.recheck_result_status IS '复核结果状态(1:通过;2:未通过)';


--
-- Name: COLUMN dmsc_review_result_detail.reject_reason; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.reject_reason IS '拒绝理由';


--
-- Name: COLUMN dmsc_review_result_detail.recheck_user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_review_result_detail.recheck_user_id IS '复核处理用户Id';


--
-- Name: dmsc_rule; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dmsc_rule (
    id bigint NOT NULL,
    type character varying(500),
    name character varying(1000) DEFAULT NULL::character varying NOT NULL,
    code character varying(500) DEFAULT NULL::character varying NOT NULL,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    explain character varying(1000) DEFAULT NULL::character varying,
    CONSTRAINT dmsc_rule_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN dmsc_rule.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.id IS 'ID（UUID）';


--
-- Name: COLUMN dmsc_rule.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.type IS '规则类型';


--
-- Name: COLUMN dmsc_rule.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.name IS '规则编号';


--
-- Name: COLUMN dmsc_rule.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.comments IS '备注';


--
-- Name: COLUMN dmsc_rule.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dmsc_rule.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.version IS '版本号';


--
-- Name: COLUMN dmsc_rule.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.create_date IS '创建时间';


--
-- Name: COLUMN dmsc_rule.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.update_date IS '最后更新时间';


--
-- Name: COLUMN dmsc_rule.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.create_user IS '创建人';


--
-- Name: COLUMN dmsc_rule.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.update_user IS '最后更新人';


--
-- Name: COLUMN dmsc_rule.explain; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_rule.explain IS '机理说明';


--
-- Name: dmsc_tool_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dmsc_tool_file (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    tool_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN dmsc_tool_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.id IS '角色ID（UUID）';


--
-- Name: COLUMN dmsc_tool_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN dmsc_tool_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN dmsc_tool_file.tool_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.tool_name IS '工具名称';


--
-- Name: COLUMN dmsc_tool_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.comments IS '备注';


--
-- Name: COLUMN dmsc_tool_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN dmsc_tool_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.version IS '版本号';


--
-- Name: COLUMN dmsc_tool_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.create_date IS '创建时间';


--
-- Name: COLUMN dmsc_tool_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.update_date IS '最后更新时间';


--
-- Name: COLUMN dmsc_tool_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.create_user IS '创建人';


--
-- Name: COLUMN dmsc_tool_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dmsc_tool_file.update_user IS '最后更新人';


--
-- Name: ljsc_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ljsc_file (
    id bigint DEFAULT 4 NOT NULL,
    minio_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    file_path character varying(500) DEFAULT NULL::character varying,
    file_save_name character varying(500) DEFAULT NULL::character varying,
    secret_level smallint DEFAULT 4 NOT NULL,
    department_id bigint,
    owner_id bigint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_recycle smallint DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    compatible_models character varying(500),
    product_model character varying(500),
    product_name character varying(500),
    config_name character varying(500),
    code_file_version character varying(500)
);


--
-- Name: COLUMN ljsc_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.id IS 'ID（UUID）';


--
-- Name: COLUMN ljsc_file.minio_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.minio_id IS '文件在Minio上的ID';


--
-- Name: COLUMN ljsc_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN ljsc_file.file_path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.file_path IS '服务器上文件保存的路径';


--
-- Name: COLUMN ljsc_file.file_save_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.file_save_name IS '服务器上文件保存的名称';


--
-- Name: COLUMN ljsc_file.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.secret_level IS '密级(1:内部;2:受控;3:商密;4:公开)';


--
-- Name: COLUMN ljsc_file.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.department_id IS '文件隶属部门Id';


--
-- Name: COLUMN ljsc_file.owner_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.owner_id IS '文件用户Id';


--
-- Name: COLUMN ljsc_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.comments IS '备注';


--
-- Name: COLUMN ljsc_file.is_recycle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.is_recycle IS '是否移入文件回收站(1:是; 0:否)';


--
-- Name: COLUMN ljsc_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN ljsc_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.version IS '版本号';


--
-- Name: COLUMN ljsc_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.create_date IS '创建时间';


--
-- Name: COLUMN ljsc_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.update_date IS '最后更新时间';


--
-- Name: COLUMN ljsc_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.create_user IS '创建人';


--
-- Name: COLUMN ljsc_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.update_user IS '最后更新人';


--
-- Name: COLUMN ljsc_file.compatible_models; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.compatible_models IS '配套机型';


--
-- Name: COLUMN ljsc_file.product_model; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.product_model IS '产品型号';


--
-- Name: COLUMN ljsc_file.product_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.product_name IS '产品名称';


--
-- Name: COLUMN ljsc_file.config_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.config_name IS '配置项名称';


--
-- Name: COLUMN ljsc_file.code_file_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file.code_file_version IS '代码版本';


--
-- Name: ljsc_file_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ljsc_file_version (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    minio_id character varying(100) NOT NULL,
    file_version integer DEFAULT 1,
    file_name character varying(500) DEFAULT NULL::character varying,
    file_origin_name character varying(500) DEFAULT NULL::character varying,
    secret_level smallint DEFAULT 4 NOT NULL,
    department_id bigint,
    owner_id bigint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_recycle smallint DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN ljsc_file_version.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.id IS '角色ID（UUID）';


--
-- Name: COLUMN ljsc_file_version.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.file_id IS '所属文件ID,关联到文件表';


--
-- Name: COLUMN ljsc_file_version.minio_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.minio_id IS '文件在minio的ID';


--
-- Name: COLUMN ljsc_file_version.file_version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.file_version IS '文件版本序号';


--
-- Name: COLUMN ljsc_file_version.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.file_name IS '文件版本名称';


--
-- Name: COLUMN ljsc_file_version.file_origin_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.file_origin_name IS '文件上传时的原始名称';


--
-- Name: COLUMN ljsc_file_version.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.secret_level IS '密级(1:内部;2:受控;3:商密;4:公开)';


--
-- Name: COLUMN ljsc_file_version.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.department_id IS '文件隶属部门Id';


--
-- Name: COLUMN ljsc_file_version.owner_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.owner_id IS '文件用户Id';


--
-- Name: COLUMN ljsc_file_version.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.comments IS '备注';


--
-- Name: COLUMN ljsc_file_version.is_recycle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.is_recycle IS '是否移入文件回收站(1:是; 0:否)';


--
-- Name: COLUMN ljsc_file_version.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN ljsc_file_version.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.version IS '版本号';


--
-- Name: COLUMN ljsc_file_version.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.create_date IS '创建时间';


--
-- Name: COLUMN ljsc_file_version.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.update_date IS '最后更新时间';


--
-- Name: COLUMN ljsc_file_version.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.create_user IS '创建人';


--
-- Name: COLUMN ljsc_file_version.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_file_version.update_user IS '最后更新人';


--
-- Name: ljsc_review_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ljsc_review_result (
    id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_version_id bigint NOT NULL,
    check_points integer,
    pass_check_points integer,
    pass_rate numeric(15,5) DEFAULT NULL::numeric,
    is_closed_loop smallint DEFAULT 0 NOT NULL,
    review_time timestamp(6) without time zone DEFAULT NULL::timestamp without time zone,
    status integer,
    error_message text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    duration integer,
    files_size integer,
    files_line integer,
    use_rule_size integer,
    questions integer
);


--
-- Name: COLUMN ljsc_review_result.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.id IS 'ID（UUID）';


--
-- Name: COLUMN ljsc_review_result.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.file_id IS '外键,关联到文件表';


--
-- Name: COLUMN ljsc_review_result.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.file_version_id IS '外键,关联到文件版本表';


--
-- Name: COLUMN ljsc_review_result.check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.check_points IS '检查点数量';


--
-- Name: COLUMN ljsc_review_result.pass_check_points; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.pass_check_points IS '通过的检查点数量';


--
-- Name: COLUMN ljsc_review_result.pass_rate; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.pass_rate IS '通过率';


--
-- Name: COLUMN ljsc_review_result.is_closed_loop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.is_closed_loop IS '闭环状态';


--
-- Name: COLUMN ljsc_review_result.review_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.review_time IS '开始审查的时间';


--
-- Name: COLUMN ljsc_review_result.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.status IS '状态(1:正在审查;2:审查完成)';


--
-- Name: COLUMN ljsc_review_result.error_message; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.error_message IS '底层API错误信息';


--
-- Name: COLUMN ljsc_review_result.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.comments IS '备注';


--
-- Name: COLUMN ljsc_review_result.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN ljsc_review_result.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.version IS '版本号';


--
-- Name: COLUMN ljsc_review_result.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.create_date IS '创建时间';


--
-- Name: COLUMN ljsc_review_result.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.update_date IS '最后更新时间';


--
-- Name: COLUMN ljsc_review_result.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.create_user IS '创建人';


--
-- Name: COLUMN ljsc_review_result.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.update_user IS '最后更新人';


--
-- Name: COLUMN ljsc_review_result.duration; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.duration IS '耗时(秒)';


--
-- Name: COLUMN ljsc_review_result.files_size; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.files_size IS '文件数';


--
-- Name: COLUMN ljsc_review_result.files_line; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.files_line IS '文件行数';


--
-- Name: COLUMN ljsc_review_result.use_rule_size; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.use_rule_size IS '使用规则数';


--
-- Name: COLUMN ljsc_review_result.questions; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result.questions IS '问题数量';


--
-- Name: ljsc_review_result_detail; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ljsc_review_result_detail (
    id bigint NOT NULL,
    result_id bigint,
    rule_id bigint,
    source_file_name character varying(500) DEFAULT NULL::character varying,
    language character varying(100) DEFAULT NULL::character varying,
    error_code text,
    line_number character varying(100) DEFAULT NULL::character varying,
    error_reason text,
    review_suggestion text,
    is_passed smallint,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone NOT NULL,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    rule_code character varying(500) DEFAULT NULL::character varying,
    question_id bigint,
    recheck_conclusion character varying(500),
    question_desc text,
    recheck_status integer DEFAULT 0,
    recheck_result_status integer DEFAULT 0,
    reject_reason character varying(500),
    recheck_user_id bigint
);


--
-- Name: COLUMN ljsc_review_result_detail.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.id IS 'ID（UUID）';


--
-- Name: COLUMN ljsc_review_result_detail.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.result_id IS '外键,关联到结果表';


--
-- Name: COLUMN ljsc_review_result_detail.rule_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.rule_id IS '外键,关联到规则表';


--
-- Name: COLUMN ljsc_review_result_detail.source_file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.source_file_name IS '源代码文件名';


--
-- Name: COLUMN ljsc_review_result_detail.language; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.language IS '代码语言';


--
-- Name: COLUMN ljsc_review_result_detail.error_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.error_code IS '错误原因';


--
-- Name: COLUMN ljsc_review_result_detail.line_number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.line_number IS '代码行号';


--
-- Name: COLUMN ljsc_review_result_detail.review_suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.review_suggestion IS '审查意见';


--
-- Name: COLUMN ljsc_review_result_detail.is_passed; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.is_passed IS '是否通过(0:未通过;1:通过)';


--
-- Name: COLUMN ljsc_review_result_detail.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.comments IS '备注';


--
-- Name: COLUMN ljsc_review_result_detail.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN ljsc_review_result_detail.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.version IS '版本号';


--
-- Name: COLUMN ljsc_review_result_detail.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.create_date IS '创建时间';


--
-- Name: COLUMN ljsc_review_result_detail.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.update_date IS '最后更新时间';


--
-- Name: COLUMN ljsc_review_result_detail.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.create_user IS '创建人';


--
-- Name: COLUMN ljsc_review_result_detail.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.update_user IS '最后更新人';


--
-- Name: COLUMN ljsc_review_result_detail.rule_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.rule_code IS '规则编号';


--
-- Name: COLUMN ljsc_review_result_detail.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.question_id IS '问题ID';


--
-- Name: COLUMN ljsc_review_result_detail.recheck_conclusion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.recheck_conclusion IS '审查结论';


--
-- Name: COLUMN ljsc_review_result_detail.question_desc; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.question_desc IS '问题描述';


--
-- Name: COLUMN ljsc_review_result_detail.recheck_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.recheck_status IS '复核状态(1:未复核;2:复核中;3:复核完成)';


--
-- Name: COLUMN ljsc_review_result_detail.recheck_result_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.recheck_result_status IS '复核结果状态(1:通过;2:未通过)';


--
-- Name: COLUMN ljsc_review_result_detail.reject_reason; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.reject_reason IS '拒绝理由';


--
-- Name: COLUMN ljsc_review_result_detail.recheck_user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_review_result_detail.recheck_user_id IS '复核处理用户Id';


--
-- Name: ljsc_rule; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ljsc_rule (
    id bigint NOT NULL,
    type character varying(500),
    name character varying(1000) DEFAULT NULL::character varying NOT NULL,
    code character varying(500) DEFAULT NULL::character varying NOT NULL,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    explain character varying(1000) DEFAULT NULL::character varying
);


--
-- Name: COLUMN ljsc_rule.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.id IS 'ID（UUID）';


--
-- Name: COLUMN ljsc_rule.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.type IS '规则类型';


--
-- Name: COLUMN ljsc_rule.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.name IS '规则名称';


--
-- Name: COLUMN ljsc_rule.code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.code IS '规则编号';


--
-- Name: COLUMN ljsc_rule.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.comments IS '备注';


--
-- Name: COLUMN ljsc_rule.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN ljsc_rule.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.version IS '版本号';


--
-- Name: COLUMN ljsc_rule.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.create_date IS '创建时间';


--
-- Name: COLUMN ljsc_rule.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.update_date IS '最后更新时间';


--
-- Name: COLUMN ljsc_rule.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.create_user IS '创建人';


--
-- Name: COLUMN ljsc_rule.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.update_user IS '最后更新人';


--
-- Name: COLUMN ljsc_rule.explain; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_rule.explain IS '机理说明';


--
-- Name: ljsc_tool_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ljsc_tool_file (
    id bigint DEFAULT 4 NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    tool_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN ljsc_tool_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.id IS '角色ID（UUID）';


--
-- Name: COLUMN ljsc_tool_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN ljsc_tool_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN ljsc_tool_file.tool_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.tool_name IS '工具名称';


--
-- Name: COLUMN ljsc_tool_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.comments IS '备注';


--
-- Name: COLUMN ljsc_tool_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN ljsc_tool_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.version IS '版本号';


--
-- Name: COLUMN ljsc_tool_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.create_date IS '创建时间';


--
-- Name: COLUMN ljsc_tool_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.update_date IS '最后更新时间';


--
-- Name: COLUMN ljsc_tool_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.create_user IS '创建人';


--
-- Name: COLUMN ljsc_tool_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ljsc_tool_file.update_user IS '最后更新人';


--
-- Name: logic_yjfk_answer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.logic_yjfk_answer (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    answer text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    ref_id bigint
);


--
-- Name: COLUMN logic_yjfk_answer.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.id IS 'ID（UUID）';


--
-- Name: COLUMN logic_yjfk_answer.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.f_id IS '父ID(外键,关联到反馈意见主表或当前表)';


--
-- Name: COLUMN logic_yjfk_answer.answer; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.answer IS '回复内容';


--
-- Name: COLUMN logic_yjfk_answer.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.comments IS '备注';


--
-- Name: COLUMN logic_yjfk_answer.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN logic_yjfk_answer.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.version IS '版本号';


--
-- Name: COLUMN logic_yjfk_answer.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.create_date IS '创建时间';


--
-- Name: COLUMN logic_yjfk_answer.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.update_date IS '最后更新时间';


--
-- Name: COLUMN logic_yjfk_answer.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.create_user IS '创建人';


--
-- Name: COLUMN logic_yjfk_answer.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.update_user IS '最后更新人';


--
-- Name: COLUMN logic_yjfk_answer.ref_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_answer.ref_id IS '参照的回复ID(外键,关联到当前表)';


--
-- Name: logic_yjfk_append_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.logic_yjfk_append_file (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    type character varying(100) NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN logic_yjfk_append_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.id IS 'ID（UUID）';


--
-- Name: COLUMN logic_yjfk_append_file.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.f_id IS '外键,关联到主记录表';


--
-- Name: COLUMN logic_yjfk_append_file.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.type IS '附件类型';


--
-- Name: COLUMN logic_yjfk_append_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN logic_yjfk_append_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN logic_yjfk_append_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.comments IS '备注';


--
-- Name: COLUMN logic_yjfk_append_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN logic_yjfk_append_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.version IS '版本号';


--
-- Name: COLUMN logic_yjfk_append_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.create_date IS '创建时间';


--
-- Name: COLUMN logic_yjfk_append_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.update_date IS '最后更新时间';


--
-- Name: COLUMN logic_yjfk_append_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.create_user IS '创建人';


--
-- Name: COLUMN logic_yjfk_append_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_append_file.update_user IS '最后更新人';


--
-- Name: logic_yjfk_suggestion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.logic_yjfk_suggestion (
    id bigint NOT NULL,
    title character varying(500) DEFAULT NULL::character varying,
    suggestion text,
    status integer,
    description character varying(1000) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    file_id bigint,
    file_version_id bigint,
    result_id bigint
);


--
-- Name: COLUMN logic_yjfk_suggestion.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.id IS 'ID（UUID）';


--
-- Name: COLUMN logic_yjfk_suggestion.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.title IS '标题';


--
-- Name: COLUMN logic_yjfk_suggestion.suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.suggestion IS '反馈意见';


--
-- Name: COLUMN logic_yjfk_suggestion.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.status IS '反馈状态(1:open;2:reopen;3:closed)';


--
-- Name: COLUMN logic_yjfk_suggestion.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.description IS '意见描述';


--
-- Name: COLUMN logic_yjfk_suggestion.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.comments IS '备注';


--
-- Name: COLUMN logic_yjfk_suggestion.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN logic_yjfk_suggestion.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.version IS '版本号';


--
-- Name: COLUMN logic_yjfk_suggestion.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.create_date IS '创建时间';


--
-- Name: COLUMN logic_yjfk_suggestion.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.update_date IS '最后更新时间';


--
-- Name: COLUMN logic_yjfk_suggestion.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.create_user IS '创建人';


--
-- Name: COLUMN logic_yjfk_suggestion.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.update_user IS '最后更新人';


--
-- Name: COLUMN logic_yjfk_suggestion.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.file_id IS '文件ID';


--
-- Name: COLUMN logic_yjfk_suggestion.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.file_version_id IS '文件版本ID';


--
-- Name: COLUMN logic_yjfk_suggestion.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion.result_id IS '审查结果ID';


--
-- Name: logic_yjfk_suggestion_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.logic_yjfk_suggestion_status (
    id bigint NOT NULL,
    suggestion_id bigint,
    status integer,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN logic_yjfk_suggestion_status.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.id IS 'ID（UUID）';


--
-- Name: COLUMN logic_yjfk_suggestion_status.suggestion_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.suggestion_id IS '父ID(外键,关联到反馈意见主表或当前表)';


--
-- Name: COLUMN logic_yjfk_suggestion_status.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.status IS '反馈状态(0:new;1:open;2:reopen;3:closed)';


--
-- Name: COLUMN logic_yjfk_suggestion_status.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.comments IS '备注';


--
-- Name: COLUMN logic_yjfk_suggestion_status.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN logic_yjfk_suggestion_status.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.version IS '版本号';


--
-- Name: COLUMN logic_yjfk_suggestion_status.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.create_date IS '创建时间';


--
-- Name: COLUMN logic_yjfk_suggestion_status.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.update_date IS '最后更新时间';


--
-- Name: COLUMN logic_yjfk_suggestion_status.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.create_user IS '创建人';


--
-- Name: COLUMN logic_yjfk_suggestion_status.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.logic_yjfk_suggestion_status.update_user IS '最后更新人';


--
-- Name: oauth_client; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauth_client (
    id character varying(22) NOT NULL,
    application_id character varying(22) NOT NULL,
    client_name character varying(35) NOT NULL,
    client_id character varying(35) NOT NULL,
    client_secret character varying(35) NOT NULL,
    client_url character varying(200) DEFAULT ''::character varying NOT NULL,
    client_desc character varying(50) DEFAULT NULL::character varying,
    logo_url character varying(200) DEFAULT NULL::character varying,
    ranking smallint DEFAULT 100 NOT NULL,
    remark character varying(255) DEFAULT NULL::character varying,
    state_enum smallint DEFAULT 1 NOT NULL,
    create_date timestamp(6) without time zone DEFAULT now() NOT NULL,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user character varying(22) NOT NULL,
    update_user character varying(22) DEFAULT NULL::character varying,
    version integer DEFAULT 0,
    is_delete smallint DEFAULT 0 NOT NULL
);


--
-- Name: COLUMN oauth_client.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.id IS '主键ID';


--
-- Name: COLUMN oauth_client.application_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.application_id IS '应用ID，关联到应用表';


--
-- Name: COLUMN oauth_client.client_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.client_name IS '账号名称';


--
-- Name: COLUMN oauth_client.client_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.client_id IS '账号ID';


--
-- Name: COLUMN oauth_client.client_secret; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.client_secret IS '账号密钥';


--
-- Name: COLUMN oauth_client.client_url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.client_url IS '账号匹配的网站，支持正则符号';


--
-- Name: COLUMN oauth_client.client_desc; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.client_desc IS '账号描述';


--
-- Name: COLUMN oauth_client.logo_url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.logo_url IS 'logo 的链接地址';


--
-- Name: COLUMN oauth_client.ranking; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.ranking IS '排序，默认值100，值越小越靠前';


--
-- Name: COLUMN oauth_client.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.remark IS '备注';


--
-- Name: COLUMN oauth_client.state_enum; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.state_enum IS '是否启动, 1正常，2禁用';


--
-- Name: COLUMN oauth_client.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.create_date IS '创建时间';


--
-- Name: COLUMN oauth_client.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.update_date IS '最后更新时间';


--
-- Name: COLUMN oauth_client.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.create_user IS '创建人';


--
-- Name: COLUMN oauth_client.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.update_user IS '最后更新人';


--
-- Name: COLUMN oauth_client.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.version IS '版本号';


--
-- Name: COLUMN oauth_client.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.oauth_client.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: pot_application; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pot_application (
    id bigint NOT NULL,
    name character varying(500) NOT NULL,
    eng_name character varying(500) NOT NULL,
    module character varying(500) DEFAULT NULL::character varying,
    icon text,
    hover_icon text,
    image text,
    description text,
    sequence integer DEFAULT 0,
    status smallint DEFAULT 0 NOT NULL,
    url character varying(500) DEFAULT NULL::character varying,
    comments character varying(500) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT pot_application_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT pot_application_status_check CHECK ((status = ANY (ARRAY[0, 1, 2])))
);


--
-- Name: COLUMN pot_application.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.id IS '应用ID（UUID）';


--
-- Name: COLUMN pot_application.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.name IS '应用名称';


--
-- Name: COLUMN pot_application.eng_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.eng_name IS '应用英文名称';


--
-- Name: COLUMN pot_application.module; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.module IS '所属板块';


--
-- Name: COLUMN pot_application.icon; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.icon IS '应用图标';


--
-- Name: COLUMN pot_application.hover_icon; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.hover_icon IS '应用高亮图标';


--
-- Name: COLUMN pot_application.image; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.image IS '应用图片';


--
-- Name: COLUMN pot_application.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.description IS '应用说明';


--
-- Name: COLUMN pot_application.sequence; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.sequence IS '序号';


--
-- Name: COLUMN pot_application.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.status IS '状态(0: 未上线;1: 已上线;2: 已下线;)';


--
-- Name: COLUMN pot_application.url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.url IS '应用入口地址';


--
-- Name: COLUMN pot_application.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.comments IS '备注';


--
-- Name: COLUMN pot_application.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN pot_application.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.version IS '版本号';


--
-- Name: COLUMN pot_application.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.create_date IS '创建时间';


--
-- Name: COLUMN pot_application.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.update_date IS '最后更新时间';


--
-- Name: COLUMN pot_application.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.create_user IS '创建人';


--
-- Name: COLUMN pot_application.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_application.update_user IS '最后更新人';


--
-- Name: pot_info_append_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pot_info_append_file (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN pot_info_append_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.id IS 'ID（UUID）';


--
-- Name: COLUMN pot_info_append_file.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.f_id IS '外键,关联到主记录表(pot_information)';


--
-- Name: COLUMN pot_info_append_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN pot_info_append_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN pot_info_append_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.comments IS '备注';


--
-- Name: COLUMN pot_info_append_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN pot_info_append_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.version IS '版本号';


--
-- Name: COLUMN pot_info_append_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.create_date IS '创建时间';


--
-- Name: COLUMN pot_info_append_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.update_date IS '最后更新时间';


--
-- Name: COLUMN pot_info_append_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.create_user IS '创建人';


--
-- Name: COLUMN pot_info_append_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_info_append_file.update_user IS '最后更新人';


--
-- Name: pot_information; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pot_information (
    id bigint NOT NULL,
    title character varying(100) NOT NULL,
    summary character varying(500),
    type smallint DEFAULT 1 NOT NULL,
    image text,
    content text,
    status smallint DEFAULT 0 NOT NULL,
    url character varying(200) DEFAULT NULL::character varying,
    comments character varying(200) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT pot_information_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT pot_information_status_check CHECK ((status = ANY (ARRAY[0, 1, 2]))),
    CONSTRAINT pot_information_type_check CHECK ((type = ANY (ARRAY[1, 2, 3])))
);


--
-- Name: COLUMN pot_information.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.id IS '资讯ID（UUID）';


--
-- Name: COLUMN pot_information.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.title IS '资讯摘要';


--
-- Name: COLUMN pot_information.summary; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.summary IS '资讯摘要';


--
-- Name: COLUMN pot_information.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.type IS '资讯类型(1:新闻;2:通知;3:资讯)';


--
-- Name: COLUMN pot_information.image; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.image IS '资讯图片';


--
-- Name: COLUMN pot_information.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.content IS '应用内容';


--
-- Name: COLUMN pot_information.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.status IS '状态(0: 未发布;1: 已发布;2: 已下线;)';


--
-- Name: COLUMN pot_information.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.comments IS '备注';


--
-- Name: COLUMN pot_information.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN pot_information.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.version IS '版本号';


--
-- Name: COLUMN pot_information.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.create_date IS '创建时间';


--
-- Name: COLUMN pot_information.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.update_date IS '最后更新时间';


--
-- Name: COLUMN pot_information.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.create_user IS '创建人';


--
-- Name: COLUMN pot_information.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pot_information.update_user IS '最后更新人';


--
-- Name: sjjyfx_experience_share; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sjjyfx_experience_share (
    id bigint NOT NULL,
    title character varying(500) NOT NULL,
    content text,
    contributor character varying(200) DEFAULT NULL::character varying,
    organization character varying(500) DEFAULT NULL::character varying,
    contact character varying(500) DEFAULT NULL::character varying,
    like_count integer DEFAULT 0 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN sjjyfx_experience_share.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.id IS 'ID（雪花算法）';


--
-- Name: COLUMN sjjyfx_experience_share.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.title IS '标题';


--
-- Name: COLUMN sjjyfx_experience_share.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.content IS '经验分享内容';


--
-- Name: COLUMN sjjyfx_experience_share.contributor; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.contributor IS '贡献人';


--
-- Name: COLUMN sjjyfx_experience_share.organization; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.organization IS '单位';


--
-- Name: COLUMN sjjyfx_experience_share.contact; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.contact IS '联系方式';


--
-- Name: COLUMN sjjyfx_experience_share.like_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.like_count IS '点赞数';


--
-- Name: COLUMN sjjyfx_experience_share.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.is_delete IS '是否删除(1:删除;0:正常)';


--
-- Name: COLUMN sjjyfx_experience_share.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.version IS '版本号';


--
-- Name: COLUMN sjjyfx_experience_share.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.create_date IS '创建时间';


--
-- Name: COLUMN sjjyfx_experience_share.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.update_date IS '最后更新时间';


--
-- Name: COLUMN sjjyfx_experience_share.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.create_user IS '创建人';


--
-- Name: COLUMN sjjyfx_experience_share.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share.update_user IS '最后更新人';


--
-- Name: sjjyfx_experience_share_reply; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sjjyfx_experience_share_reply (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    reply text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN sjjyfx_experience_share_reply.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.id IS 'ID（雪花算法）';


--
-- Name: COLUMN sjjyfx_experience_share_reply.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.f_id IS '父ID(外键,关联到设计经验分享主表)';


--
-- Name: COLUMN sjjyfx_experience_share_reply.reply; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.reply IS '回复内容';


--
-- Name: COLUMN sjjyfx_experience_share_reply.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.comments IS '备注';


--
-- Name: COLUMN sjjyfx_experience_share_reply.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.is_delete IS '是否删除(1:删除;0:正常)';


--
-- Name: COLUMN sjjyfx_experience_share_reply.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.version IS '版本号';


--
-- Name: COLUMN sjjyfx_experience_share_reply.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.create_date IS '创建时间';


--
-- Name: COLUMN sjjyfx_experience_share_reply.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.update_date IS '最后更新时间';


--
-- Name: COLUMN sjjyfx_experience_share_reply.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.create_user IS '创建人';


--
-- Name: COLUMN sjjyfx_experience_share_reply.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_experience_share_reply.update_user IS '最后更新人';


--
-- Name: sjjyfx_like_record; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sjjyfx_like_record (
    id bigint NOT NULL,
    experience_id bigint NOT NULL,
    user_id bigint NOT NULL,
    is_liked smallint DEFAULT 1 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN sjjyfx_like_record.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.id IS 'ID（雪花算法）';


--
-- Name: COLUMN sjjyfx_like_record.experience_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.experience_id IS '经验分享ID';


--
-- Name: COLUMN sjjyfx_like_record.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.user_id IS '点赞用户ID';


--
-- Name: COLUMN sjjyfx_like_record.is_liked; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.is_liked IS '是否点赞(1:已点赞;0:已取消)';


--
-- Name: COLUMN sjjyfx_like_record.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.is_delete IS '是否删除(1:删除;0:正常)';


--
-- Name: COLUMN sjjyfx_like_record.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.version IS '版本号';


--
-- Name: COLUMN sjjyfx_like_record.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.create_date IS '创建时间';


--
-- Name: COLUMN sjjyfx_like_record.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.update_date IS '最后更新时间';


--
-- Name: COLUMN sjjyfx_like_record.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.create_user IS '创建人';


--
-- Name: COLUMN sjjyfx_like_record.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sjjyfx_like_record.update_user IS '最后更新人';


--
-- Name: urm_department; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_department (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(20) DEFAULT NULL::character varying,
    is_editable smallint DEFAULT 1 NOT NULL,
    sequence integer DEFAULT 0,
    f_id bigint,
    comments character varying(200) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    is_office smallint DEFAULT 0 NOT NULL,
    CONSTRAINT urm_department_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_department_is_editable_check CHECK ((is_editable = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_department_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_department_new_is_editable_check CHECK ((is_editable = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN urm_department.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.id IS '部门ID（UUID）';


--
-- Name: COLUMN urm_department.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.name IS '用户创建时输入的部门名称';


--
-- Name: COLUMN urm_department.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.type IS '机构类型';


--
-- Name: COLUMN urm_department.is_editable; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.is_editable IS '是否可删除 0-不可删除 1-可删除';


--
-- Name: COLUMN urm_department.sequence; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.sequence IS '部门序号';


--
-- Name: COLUMN urm_department.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.f_id IS '父级机构ID（UUID），为null时为根级机构';


--
-- Name: COLUMN urm_department.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.comments IS '备注';


--
-- Name: COLUMN urm_department.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN urm_department.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.version IS '版本号';


--
-- Name: COLUMN urm_department.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.create_date IS '创建时间';


--
-- Name: COLUMN urm_department.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.update_date IS '最后更新时间';


--
-- Name: COLUMN urm_department.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.create_user IS '创建人';


--
-- Name: COLUMN urm_department.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.update_user IS '最后更新人';


--
-- Name: COLUMN urm_department.is_office; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_department.is_office IS '是否科室 0-不是科室 1-是科室';


--
-- Name: urm_power; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_power (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    alias character varying(200) DEFAULT NULL::character varying,
    menu_type character varying(4) NOT NULL,
    path character varying(200) DEFAULT NULL::character varying,
    component character varying(100) DEFAULT NULL::character varying,
    icon character varying(100) DEFAULT NULL::character varying,
    is_frame smallint DEFAULT 0 NOT NULL,
    visible smallint DEFAULT 1 NOT NULL,
    enabled smallint DEFAULT 1 NOT NULL,
    sequence integer DEFAULT 0,
    f_id bigint,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT urm_power_enabled_check CHECK ((enabled = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_is_frame_check CHECK ((is_frame = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_new_enabled_check CHECK ((enabled = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_new_is_frame_check CHECK ((is_frame = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_new_visible_check CHECK ((visible = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_power_visible_check CHECK ((visible = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN urm_power.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.id IS '权限ID（UUID）';


--
-- Name: COLUMN urm_power.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.name IS '权限名';


--
-- Name: COLUMN urm_power.alias; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.alias IS '权限别名';


--
-- Name: COLUMN urm_power.menu_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.menu_type IS '菜单类型(M:目录;C:菜单;F:按钮)';


--
-- Name: COLUMN urm_power.path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.path IS '菜单路径';


--
-- Name: COLUMN urm_power.component; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.component IS '组件名称';


--
-- Name: COLUMN urm_power.icon; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.icon IS '菜单图标';


--
-- Name: COLUMN urm_power.is_frame; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.is_frame IS '是否外联(1:是 ;0:否)';


--
-- Name: COLUMN urm_power.visible; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.visible IS '是否显示(1:是 ;0:否)';


--
-- Name: COLUMN urm_power.enabled; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.enabled IS '是否启用(1:是 ;0:否)';


--
-- Name: COLUMN urm_power.sequence; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.sequence IS '同级别显示顺序';


--
-- Name: COLUMN urm_power.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.f_id IS '父id';


--
-- Name: COLUMN urm_power.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN urm_power.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.version IS '版本号';


--
-- Name: COLUMN urm_power.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.create_date IS '创建时间';


--
-- Name: COLUMN urm_power.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.update_date IS '最后更新时间';


--
-- Name: COLUMN urm_power.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.create_user IS '创建人';


--
-- Name: COLUMN urm_power.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_power.update_user IS '最后更新人';


--
-- Name: urm_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_role (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(20) DEFAULT NULL::character varying,
    comments character varying(200) DEFAULT NULL::character varying,
    is_editable smallint DEFAULT 1 NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT urm_role_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_role_is_editable_check CHECK ((is_editable = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_role_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_role_new_is_editable_check CHECK ((is_editable = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN urm_role.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.id IS '角色ID（UUID）';


--
-- Name: COLUMN urm_role.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.name IS '用户创建时输入的角色名称';


--
-- Name: COLUMN urm_role.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.type IS '角色类型';


--
-- Name: COLUMN urm_role.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.comments IS '备注';


--
-- Name: COLUMN urm_role.is_editable; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.is_editable IS '可编辑状态(1: 可编辑;0:不可编辑)。系统预设角色不可编辑，其余角色均可编辑';


--
-- Name: COLUMN urm_role.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN urm_role.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.version IS '版本号';


--
-- Name: COLUMN urm_role.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.create_date IS '创建时间';


--
-- Name: COLUMN urm_role.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.update_date IS '最后更新时间';


--
-- Name: COLUMN urm_role.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.create_user IS '创建人';


--
-- Name: COLUMN urm_role.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_role.update_user IS '最后更新人';


--
-- Name: urm_rolepower; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_rolepower (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    power_id bigint NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT urm_rolepower_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_rolepower_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN urm_rolepower.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.id IS '角色权限ID（UUID）';


--
-- Name: COLUMN urm_rolepower.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.role_id IS '角色ID';


--
-- Name: COLUMN urm_rolepower.power_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.power_id IS '权限ID';


--
-- Name: COLUMN urm_rolepower.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN urm_rolepower.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.version IS '版本号';


--
-- Name: COLUMN urm_rolepower.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.create_date IS '创建时间';


--
-- Name: COLUMN urm_rolepower.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.update_date IS '最后更新时间';


--
-- Name: COLUMN urm_rolepower.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.create_user IS '创建人';


--
-- Name: COLUMN urm_rolepower.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_rolepower.update_user IS '最后更新人';


--
-- Name: urm_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_user (
    id bigint NOT NULL,
    department_id bigint DEFAULT 0,
    login_name character varying(100) NOT NULL,
    password character varying(100) NOT NULL,
    name character varying(100) DEFAULT NULL::character varying,
    telephone character varying(100),
    profile text,
    work_no character varying(50) DEFAULT NULL::character varying,
    locked smallint DEFAULT 1 NOT NULL,
    need_change_password smallint DEFAULT 0 NOT NULL,
    is_editable smallint DEFAULT 1 NOT NULL,
    secret_level smallint DEFAULT 4 NOT NULL,
    comments character varying(200) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    email character varying(500),
    CONSTRAINT urm_user_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_is_editable_check CHECK ((is_editable = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_locked_check CHECK ((locked = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_need_change_password_check CHECK ((need_change_password = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_new_is_editable_check CHECK ((is_editable = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_new_locked_check CHECK ((locked = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_user_new_need_change_password_check CHECK ((need_change_password = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN urm_user.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.id IS '用户ID（UUID）';


--
-- Name: COLUMN urm_user.department_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.department_id IS '机构id';


--
-- Name: COLUMN urm_user.login_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.login_name IS '登录名';


--
-- Name: COLUMN urm_user.password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.password IS '密码';


--
-- Name: COLUMN urm_user.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.name IS '用户姓名';


--
-- Name: COLUMN urm_user.telephone; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.telephone IS '电话号码';


--
-- Name: COLUMN urm_user.profile; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.profile IS '头像';


--
-- Name: COLUMN urm_user.work_no; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.work_no IS '工号';


--
-- Name: COLUMN urm_user.locked; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.locked IS '是否被锁((0:正常;1:被锁)';


--
-- Name: COLUMN urm_user.need_change_password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.need_change_password IS '下次登录是否需要修改密码(0:否;1:是)';


--
-- Name: COLUMN urm_user.is_editable; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.is_editable IS '是否可编辑(0:不可编辑;1:可编辑)';


--
-- Name: COLUMN urm_user.secret_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.secret_level IS '密级(1:核心涉密;2:重要涉密;3:一般涉密;4:非密)';


--
-- Name: COLUMN urm_user.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.comments IS '描述';


--
-- Name: COLUMN urm_user.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN urm_user.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.version IS '版本号';


--
-- Name: COLUMN urm_user.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.create_date IS '创建时间';


--
-- Name: COLUMN urm_user.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.update_date IS '最后更新时间';


--
-- Name: COLUMN urm_user.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.create_user IS '创建人';


--
-- Name: COLUMN urm_user.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.update_user IS '最后更新人';


--
-- Name: COLUMN urm_user.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_user.email IS '邮箱';


--
-- Name: urm_userlog; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_userlog (
    id bigint NOT NULL,
    content character varying(255) DEFAULT NULL::character varying,
    model_name character varying(255) NOT NULL,
    operate_date timestamp(6) without time zone DEFAULT now() NOT NULL,
    user_id bigint,
    user_name character varying(255) DEFAULT NULL::character varying,
    login_name character varying(255)
);


--
-- Name: COLUMN urm_userlog.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.id IS '日志ID';


--
-- Name: COLUMN urm_userlog.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.content IS '操作具体内容描述';


--
-- Name: COLUMN urm_userlog.model_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.model_name IS '操作模块';


--
-- Name: COLUMN urm_userlog.operate_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.operate_date IS '操作时间';


--
-- Name: COLUMN urm_userlog.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.user_id IS '操作者id';


--
-- Name: COLUMN urm_userlog.user_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.user_name IS '用户昵称';


--
-- Name: COLUMN urm_userlog.login_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userlog.login_name IS '登录名称';


--
-- Name: urm_userrole; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.urm_userrole (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    CONSTRAINT urm_userrole_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1]))),
    CONSTRAINT urm_userrole_new_is_delete_check CHECK ((is_delete = ANY (ARRAY[0, 1])))
);


--
-- Name: COLUMN urm_userrole.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.id IS '用户角色ID（UUID）';


--
-- Name: COLUMN urm_userrole.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.user_id IS '用户ID';


--
-- Name: COLUMN urm_userrole.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.role_id IS '角色ID';


--
-- Name: COLUMN urm_userrole.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN urm_userrole.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.version IS '版本号';


--
-- Name: COLUMN urm_userrole.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.create_date IS '创建时间';


--
-- Name: COLUMN urm_userrole.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.update_date IS '最后更新时间';


--
-- Name: COLUMN urm_userrole.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.create_user IS '创建人';


--
-- Name: COLUMN urm_userrole.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.urm_userrole.update_user IS '最后更新人';


--
-- Name: yjfk_answer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yjfk_answer (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    answer text,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    ref_id bigint
);


--
-- Name: COLUMN yjfk_answer.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.id IS 'ID（UUID）';


--
-- Name: COLUMN yjfk_answer.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.f_id IS '父ID(外键,关联到反馈意见主表或当前表)';


--
-- Name: COLUMN yjfk_answer.answer; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.answer IS '回复内容';


--
-- Name: COLUMN yjfk_answer.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.comments IS '备注';


--
-- Name: COLUMN yjfk_answer.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN yjfk_answer.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.version IS '版本号';


--
-- Name: COLUMN yjfk_answer.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.create_date IS '创建时间';


--
-- Name: COLUMN yjfk_answer.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.update_date IS '最后更新时间';


--
-- Name: COLUMN yjfk_answer.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.create_user IS '创建人';


--
-- Name: COLUMN yjfk_answer.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.update_user IS '最后更新人';


--
-- Name: COLUMN yjfk_answer.ref_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_answer.ref_id IS '参照的回复ID(外键,关联到当前表)';


--
-- Name: yjfk_append_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yjfk_append_file (
    id bigint NOT NULL,
    f_id bigint NOT NULL,
    type character varying(100) NOT NULL,
    file_id character varying(100) NOT NULL,
    file_name character varying(500) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN yjfk_append_file.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.id IS 'ID（UUID）';


--
-- Name: COLUMN yjfk_append_file.f_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.f_id IS '外键,关联到主记录表';


--
-- Name: COLUMN yjfk_append_file.type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.type IS '附件类型';


--
-- Name: COLUMN yjfk_append_file.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.file_id IS '文件在Minio上的ID';


--
-- Name: COLUMN yjfk_append_file.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.file_name IS '文件的原始名称';


--
-- Name: COLUMN yjfk_append_file.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.comments IS '备注';


--
-- Name: COLUMN yjfk_append_file.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN yjfk_append_file.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.version IS '版本号';


--
-- Name: COLUMN yjfk_append_file.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.create_date IS '创建时间';


--
-- Name: COLUMN yjfk_append_file.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.update_date IS '最后更新时间';


--
-- Name: COLUMN yjfk_append_file.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.create_user IS '创建人';


--
-- Name: COLUMN yjfk_append_file.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_append_file.update_user IS '最后更新人';


--
-- Name: yjfk_suggestion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yjfk_suggestion (
    id bigint NOT NULL,
    title character varying(500) DEFAULT NULL::character varying,
    suggestion text,
    status integer,
    description character varying(1000) DEFAULT NULL::character varying,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint,
    file_id bigint,
    file_version_id bigint,
    result_id bigint
);


--
-- Name: COLUMN yjfk_suggestion.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.id IS 'ID（UUID）';


--
-- Name: COLUMN yjfk_suggestion.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.title IS '标题';


--
-- Name: COLUMN yjfk_suggestion.suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.suggestion IS '反馈意见';


--
-- Name: COLUMN yjfk_suggestion.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.status IS '反馈状态(1:open;2:reopen;3:closed)';


--
-- Name: COLUMN yjfk_suggestion.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.description IS '意见描述';


--
-- Name: COLUMN yjfk_suggestion.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.comments IS '备注';


--
-- Name: COLUMN yjfk_suggestion.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN yjfk_suggestion.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.version IS '版本号';


--
-- Name: COLUMN yjfk_suggestion.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.create_date IS '创建时间';


--
-- Name: COLUMN yjfk_suggestion.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.update_date IS '最后更新时间';


--
-- Name: COLUMN yjfk_suggestion.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.create_user IS '创建人';


--
-- Name: COLUMN yjfk_suggestion.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.update_user IS '最后更新人';


--
-- Name: COLUMN yjfk_suggestion.file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.file_id IS '文件ID';


--
-- Name: COLUMN yjfk_suggestion.file_version_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.file_version_id IS '文件版本ID';


--
-- Name: COLUMN yjfk_suggestion.result_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion.result_id IS '审查结果ID';


--
-- Name: yjfk_suggestion_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yjfk_suggestion_status (
    id bigint NOT NULL,
    suggestion_id bigint,
    status integer,
    comments character varying(1000) DEFAULT NULL::character varying,
    is_delete smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: COLUMN yjfk_suggestion_status.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.id IS 'ID（UUID）';


--
-- Name: COLUMN yjfk_suggestion_status.suggestion_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.suggestion_id IS '父ID(外键,关联到反馈意见主表或当前表)';


--
-- Name: COLUMN yjfk_suggestion_status.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.status IS '反馈状态(0:new;1:open;2:reopen;3:closed)';


--
-- Name: COLUMN yjfk_suggestion_status.comments; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.comments IS '备注';


--
-- Name: COLUMN yjfk_suggestion_status.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.is_delete IS '是否删除(1: 删除;0:正常)';


--
-- Name: COLUMN yjfk_suggestion_status.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.version IS '版本号';


--
-- Name: COLUMN yjfk_suggestion_status.create_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.create_date IS '创建时间';


--
-- Name: COLUMN yjfk_suggestion_status.update_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.update_date IS '最后更新时间';


--
-- Name: COLUMN yjfk_suggestion_status.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.create_user IS '创建人';


--
-- Name: COLUMN yjfk_suggestion_status.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.yjfk_suggestion_status.update_user IS '最后更新人';


--
-- Data for Name: code_yjfk_answer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.code_yjfk_answer (id, f_id, answer, comments, is_delete, version, create_date, update_date, create_user, update_user, ref_id) FROM stdin;
\.


--
-- Data for Name: code_yjfk_append_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.code_yjfk_append_file (id, f_id, type, file_id, file_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: code_yjfk_suggestion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.code_yjfk_suggestion (id, title, suggestion, status, description, comments, is_delete, version, create_date, update_date, create_user, update_user, file_id, file_version_id, result_id) FROM stdin;
\.


--
-- Data for Name: code_yjfk_suggestion_status; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.code_yjfk_suggestion_status (id, suggestion_id, status, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dlsc_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_file (id, file_id, file_name, file_path, file_save_name, secret_level, department_id, owner_id, comments, is_recycle, is_delete, version, create_date, update_date, create_user, update_user, closed_loop_file_version_id, closed_loop_result_id, closed_loop_file_version, latest_file_version_id, latest_file_version, is_closed_loop, compatible_models, product_model, product_name, diagram_number, diagram_version, max_check_points) FROM stdin;
\.


--
-- Data for Name: dlsc_file_version; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_file_version (id, file_id, minio_id, file_version, file_name, file_origin_name, secret_level, department_id, owner_id, comments, is_recycle, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dlsc_naming_convention; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_naming_convention (id, title, content, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dlsc_review_issue; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_review_issue (id, file_id, file_version_id, result_id, result_detail_id, rule_id, device_type, tag_pin, review_suggestion, rule_code, review_time, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dlsc_review_result; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_review_result (id, file_id, file_version_id, check_points, pass_check_points, pass_rate, is_closed_loop, review_time, status, error_message, comments, is_delete, version, create_date, update_date, create_user, update_user, closed_fail_check_points, is_in_audit, total_fail_check_points) FROM stdin;
\.


--
-- Data for Name: dlsc_review_result_audit; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_review_result_audit (id, file_id, file_version_id, result_id, is_audit_finished, audit_time, is_delete, version, create_date, update_date, create_user, update_user, is_admin_audit_finished, is_expert_audit_finished) FROM stdin;
\.


--
-- Data for Name: dlsc_review_result_detail; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_review_result_detail (id, result_id, rule_id, device_type, tag_pin, review_suggestion, is_passed, comments, is_delete, version, create_date, update_date, create_user, update_user, rule_code, audit_type, approved_audit_type, issue_feedback) FROM stdin;
\.


--
-- Data for Name: dlsc_review_result_detail_audit; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_review_result_detail_audit (id, file_id, file_version_id, result_id, result_detail_id, result_audit_id, audit_type, issue_feedback, status, audit_submit_time, audit_close_time, is_delete, version, create_date, update_date, create_user, update_user, reject_reason, inherited_detail_audit_id) FROM stdin;
\.


--
-- Data for Name: dlsc_rule; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_rule (id, type, name, comments, is_delete, version, create_date, update_date, create_user, update_user, is_deprecate, code) FROM stdin;
\.


--
-- Data for Name: dlsc_tool_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_tool_file (id, file_id, file_name, tool_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dlsc_update_note; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dlsc_update_note (id, update_time, content, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dmsc_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dmsc_file (id, minio_id, file_name, file_path, file_save_name, secret_level, department_id, owner_id, comments, is_recycle, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dmsc_file_version; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dmsc_file_version (id, file_id, minio_id, file_version, file_name, file_origin_name, secret_level, department_id, owner_id, comments, is_recycle, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: dmsc_review_result; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dmsc_review_result (id, file_id, file_version_id, check_points, pass_check_points, pass_rate, is_closed_loop, review_time, status, error_message, comments, is_delete, version, create_date, update_date, create_user, update_user, duration, files_size, files_line, use_rule_size, questions) FROM stdin;
\.


--
-- Data for Name: dmsc_review_result_detail; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dmsc_review_result_detail (id, result_id, rule_id, source_file_name, language, error_code, line_number, error_reason, review_suggestion, is_passed, comments, is_delete, version, create_date, update_date, create_user, update_user, rule_code, question_id, recheck_conclusion, question_desc, recheck_status, recheck_result_status, reject_reason, recheck_user_id) FROM stdin;
\.


--
-- Data for Name: dmsc_rule; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dmsc_rule (id, type, name, code, comments, is_delete, version, create_date, update_date, create_user, update_user, explain) FROM stdin;
\.


--
-- Data for Name: dmsc_tool_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dmsc_tool_file (id, file_id, file_name, tool_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: ljsc_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ljsc_file (id, minio_id, file_name, file_path, file_save_name, secret_level, department_id, owner_id, comments, is_recycle, is_delete, version, create_date, update_date, create_user, update_user, compatible_models, product_model, product_name, config_name, code_file_version) FROM stdin;
\.


--
-- Data for Name: ljsc_file_version; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ljsc_file_version (id, file_id, minio_id, file_version, file_name, file_origin_name, secret_level, department_id, owner_id, comments, is_recycle, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: ljsc_review_result; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ljsc_review_result (id, file_id, file_version_id, check_points, pass_check_points, pass_rate, is_closed_loop, review_time, status, error_message, comments, is_delete, version, create_date, update_date, create_user, update_user, duration, files_size, files_line, use_rule_size, questions) FROM stdin;
\.


--
-- Data for Name: ljsc_review_result_detail; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ljsc_review_result_detail (id, result_id, rule_id, source_file_name, language, error_code, line_number, error_reason, review_suggestion, is_passed, comments, is_delete, version, create_date, update_date, create_user, update_user, rule_code, question_id, recheck_conclusion, question_desc, recheck_status, recheck_result_status, reject_reason, recheck_user_id) FROM stdin;
\.


--
-- Data for Name: ljsc_rule; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ljsc_rule (id, type, name, code, comments, is_delete, version, create_date, update_date, create_user, update_user, explain) FROM stdin;
\.


--
-- Data for Name: ljsc_tool_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ljsc_tool_file (id, file_id, file_name, tool_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: logic_yjfk_answer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.logic_yjfk_answer (id, f_id, answer, comments, is_delete, version, create_date, update_date, create_user, update_user, ref_id) FROM stdin;
\.


--
-- Data for Name: logic_yjfk_append_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.logic_yjfk_append_file (id, f_id, type, file_id, file_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: logic_yjfk_suggestion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.logic_yjfk_suggestion (id, title, suggestion, status, description, comments, is_delete, version, create_date, update_date, create_user, update_user, file_id, file_version_id, result_id) FROM stdin;
\.


--
-- Data for Name: logic_yjfk_suggestion_status; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.logic_yjfk_suggestion_status (id, suggestion_id, status, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: oauth_client; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.oauth_client (id, application_id, client_name, client_id, client_secret, client_url, client_desc, logo_url, ranking, remark, state_enum, create_date, update_date, create_user, update_user, version, is_delete) FROM stdin;
\.


--
-- Data for Name: pot_application; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pot_application (id, name, eng_name, module, icon, hover_icon, image, description, sequence, status, url, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
900000000000001	电路智能审查	Circuit Review	设计研发	crr	\N	\N	\N	1	1	/circuitReviewHome	\N	0	0	2026-06-17 16:40:04.866167	2026-06-17 16:40:04.866167	100000000000001	100000000000001
900000000000002	软件代码智能审查	Source Code Review	设计研发	dsr	\N	\N	\N	2	1	/codeReviewHome	\N	0	0	2026-06-17 16:40:04.866167	2026-06-17 16:40:04.866167	100000000000001	100000000000001
900000000000003	硬件逻辑智能审查	Logic Review	设计研发	elg	\N	\N	\N	3	1	/logicReviewHome	\N	0	0	2026-06-17 16:40:04.866167	2026-06-17 16:40:04.866167	100000000000001	100000000000001
900000000000101	DeepSeek	DeepSeek	运营管理	crr	\N	\N	DeepSeek 官网入口	1	1	https://www.deepseek.com/	\N	0	0	2026-06-18 07:22:10	2026-06-18 07:22:10	100000000000001	100000000000001
900000000000102	试验大纲审查应用	Test Outline Review	生产制造	crr	\N	\N	试验大纲审查应用反代入口	1	1	/documentReview/	\N	0	0	2026-06-18 07:30:00	2026-06-18 07:30:00	100000000000001	100000000000001
\.


--
-- Data for Name: pot_info_append_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pot_info_append_file (id, f_id, file_id, file_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: pot_information; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pot_information (id, title, summary, type, image, content, status, url, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: sjjyfx_experience_share; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sjjyfx_experience_share (id, title, content, contributor, organization, contact, like_count, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: sjjyfx_experience_share_reply; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sjjyfx_experience_share_reply (id, f_id, reply, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: sjjyfx_like_record; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sjjyfx_like_record (id, experience_id, user_id, is_liked, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: urm_department; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_department (id, name, type, is_editable, sequence, f_id, comments, is_delete, version, create_date, update_date, create_user, update_user, is_office) FROM stdin;
110000000000001	中航机载	业务管理部门	0	0	\N	\N	0	0	2025-07-02 09:05:34	2025-07-02 09:05:34	100000000000001	100000000000001	0
110000000000002	631	业务管理部门	0	1	110000000000001	\N	0	0	2025-07-02 09:05:34	2025-07-02 09:05:34	100000000000001	100000000000001	0
\.


--
-- Data for Name: urm_power; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_power (id, name, alias, menu_type, path, component, icon, is_frame, visible, enabled, sequence, f_id, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
237208795044873	测试	12	C	12	21	\N	0	1	1	11	925644053573655	1	0	2025-07-10 15:43:22.480675	2025-07-10 15:44:40.416887	100000000000002	100000000000002
925644053573182	日志列表	:sysManage:logManage:list	C	/logsList	/log/logList	\N	0	1	1	161	925644053573653	1	0	2022-01-07 13:42:02	2025-07-10 15:45:13.419189	100000000000001	100000000000002
925644053573163	查看(按钮)	:sysManage:userManage:list:btnDetail	F	\N	\N	\N	0	1	1	1312	925644053573687	0	0	2021-10-08 14:20:27	2024-03-27 11:03:28	100000000000001	100000000000001
925644053573634	新增(按钮)	:sysManage:departmentManage:list:btnAdd	F	\N	\N	\N	0	1	1	1111	925644053573698	0	0	2021-10-08 14:34:56	2024-03-27 10:58:38	100000000000001	100000000000001
925644053573635	搜索(按钮)	:sysManage:userManage:list:btnSearch	F	\N	\N	\N	0	1	1	1313	925644053573687	0	0	2021-10-08 14:20:45	2024-03-27 11:03:29	100000000000001	100000000000001
925644053573638	编辑(按钮)	:sysManage:userManage:list:btnEdit	F	\N	\N	\N	0	1	1	1314	925644053573687	0	0	2021-10-08 14:21:06	2024-03-27 11:03:31	100000000000001	100000000000001
925644053573641	删除(按钮)	:sysManage:userManage:list:btnDel	F	\N	\N	\N	0	1	1	1315	925644053573687	0	2	2021-10-08 14:21:19	2024-03-27 11:03:34	100000000000001	100000000000001
925644053573650	部门编辑	:sysManage:departmentManage:edit	C	/depAddEdit	/department/depAddEdit	\N	0	0	1	112	925644053573670	0	1	2022-01-08 13:14:57	2024-03-27 10:57:49	100000000000001	100000000000001
925644053573651	部门详情	:sysManage:departmentManage:detail	C	/depDetail	/department/depDetail	\N	0	0	1	113	925644053573670	0	1	2022-01-08 13:14:57	2024-03-27 10:57:58	100000000000001	100000000000001
925644053573654	用户编辑	:sysManage:userManage:edit	C	/userAddEdit	/user/userAddEdit	\N	0	0	1	132	925644053573662	0	0	2021-10-08 16:47:31	2024-03-27 11:02:34	100000000000001	100000000000001
925644053573662	用户管理	:sysManage:userManage	C	/user	Layout	icon-yonghuguanli	0	1	1	13	925644053573655	0	0	2021-10-08 16:45:53	2024-03-27 10:52:17	100000000000001	100000000000001
925644053573664	角色编辑	:sysManage:roleManage:edit	C	/roleAddEdit	/role/roleAddEdit	\N	0	0	1	122	925644053573683	0	1	2021-10-08 16:51:37	2024-03-27 11:00:25	100000000000001	100000000000001
925644053573665	角色详情	:sysManage:roleManage:detail	C	/roleDetail	/role/roleDetail	\N	0	0	1	123	925644053573683	0	1	2021-10-08 16:51:37	2024-03-27 11:00:29	100000000000001	100000000000001
925644053573666	角色列表	:sysManage:roleManage:list	C	/roleList	/role/roleList	\N	0	1	1	121	925644053573683	0	1	2022-01-07 13:49:11	2024-03-27 11:00:19	100000000000001	100000000000001
925644053573668	编辑(按钮)	:sysManage:departmentManage:list:btnEdit	F	\N	\N	\N	0	1	1	1112	925644053573698	0	0	2021-10-08 14:37:15	2024-03-27 10:58:40	100000000000001	100000000000001
925644053573670	部门管理	:sysManage:departmentManage	C	/department	Layout	icon-bumenguanli	0	1	1	11	925644053573655	0	0	2022-01-08 13:14:45	2024-03-27 10:52:12	100000000000001	100000000000001
925644053573673	查看(按钮)	:sysManage:departmentManage:list:btnDetail	F	\N	\N	\N	0	1	1	1113	925644053573698	0	0	2021-10-08 14:37:31	2024-03-27 10:58:45	100000000000001	100000000000001
925644053573676	新增(按钮)	:sysManage:roleManage:list:btnAdd	F	\N	\N	\N	0	1	1	1211	925644053573666	0	1	2021-10-08 14:26:21	2024-03-27 11:01:44	100000000000001	100000000000001
925644053573678	菜单列表	:sysManage:menuManage:list	C	/menuList	/menu/menuList	\N	0	1	1	151	925644053573694	0	2	2022-01-21 12:59:13	2024-03-27 11:05:30	100000000000001	100000000000001
925644053573653	日志管理	:sysManage:logManage	C	/logs	Layout	icon-rizhiguanli	0	1	1	16	925644053573655	1	0	2022-01-07 13:42:06	2025-07-10 15:45:23.108013	100000000000001	100000000000002
925644053573655	系统设置	:sysManage	M	/sysManage	Layout	icon-xitongshezhi	0	1	1	1	\N	0	3	2021-09-26 11:30:16	2025-07-10 15:59:47.949563	100000000000001	100000000000002
925644053573679	角色人员	:sysManage:roleManage:roleUser	C	/roleUser	/role/roleUser	\N	0	0	1	124	925644053573683	0	1	2021-10-08 16:51:44	2024-03-27 11:00:34	100000000000001	100000000000001
925644053573680	删除(按钮)	:sysManage:departmentManage:list:btnDel	F	\N	\N	\N	0	1	1	1114	925644053573698	0	0	2021-10-08 14:37:59	2024-03-27 10:58:47	100000000000001	100000000000001
925644053573683	角色管理	:sysManage:roleManage	C	/role	Layout	icon-jiaoseguanli	0	1	1	12	925644053573655	0	0	2021-10-08 17:59:42	2024-03-27 10:52:15	100000000000001	100000000000001
925644053573686	查看(按钮)	:sysManage:roleManage:list:btnDetail	F	\N	\N	\N	0	1	1	1212	925644053573666	0	1	2021-10-08 14:26:34	2024-03-27 11:01:49	100000000000001	100000000000001
925644053573687	用户列表	:sysManage:userManage:list	C	/userList	/user/userList	\N	0	1	1	131	925644053573662	0	2	2022-01-07 13:51:59	2024-03-27 11:02:32	100000000000001	100000000000001
925644053573689	搜索(按钮)	:sysManage:roleManage:list:btnSearch	F	\N	\N	\N	0	1	1	1213	925644053573666	0	1	2021-10-08 14:26:48	2024-03-27 11:01:51	100000000000001	100000000000001
925644053573691	菜单编辑	:sysManage:menuManage:edit	C	/menuEdit	/menu/menuEdit	\N	0	0	1	152	925644053573694	0	1	2022-01-21 12:59:37	2024-03-27 11:05:36	100000000000001	100000000000001
925644053573693	搜索-按钮	:sysManage:departmentManage:list:btnSearch	F	\N	\N	\N	0	1	1	1115	925644053573698	0	0	2021-10-09 09:43:49	2024-03-27 10:58:50	100000000000001	100000000000001
925644053573694	菜单管理	:sysManage:menuManage	C	/menu	Layout	icon-caidanguanli	0	1	1	15	925644053573655	0	1	2022-01-21 13:06:36	2024-03-27 10:52:28	100000000000001	100000000000001
925644053573695	编辑(按钮)	:sysManage:roleManage:list:btnEdit	F	\N	\N	\N	0	1	1	1214	925644053573666	0	1	2021-10-08 14:27:05	2024-03-27 11:01:53	100000000000001	100000000000001
925644053573697	新增(按钮)	:sysManage:userManage:list:btnAdd	F	\N	\N	\N	0	1	1	1311	925644053573687	0	0	2021-10-08 14:20:05	2024-03-27 11:03:26	100000000000001	100000000000001
925644053573698	部门列表	:sysManage:departmentManage:list	C	/depList	/department/depList	\N	0	1	1	111	925644053573670	0	1	2022-01-08 13:14:51	2024-06-19 08:41:14	100000000000001	100000000000001
925644053573699	删除(按钮)	:sysManage:roleManage:list:btnDel	F	\N	\N	\N	0	1	1	1215	925644053573666	0	1	2021-10-08 14:27:24	2024-03-27 11:01:55	100000000000001	100000000000001
925644053574654	用户详情	:sysManage:userManage:detail	C	/userDetail	/user/userDetail	\N	0	0	1	132	925644053573662	0	0	2021-10-08 16:47:31	2024-03-27 11:02:38	100000000000001	100000000000001
925644053573105	日志详情	:sysManage:logManage:detail	C	/logDetail	/log/logDetail	\N	0	0	1	162	925644053573653	0	1	2021-10-08 16:56:13	2024-03-27 11:06:01	100000000000001	100000000000001
\.


--
-- Data for Name: urm_role; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_role (id, name, type, comments, is_editable, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
100000000000003	领导	系统角色	可查看和管理所有部门用户的业务数据的角色	0	0	0	2025-07-03 11:04:29	2025-07-03 11:04:32	100000000000001	100000000000001
100000000000001	管理员	系统角色	可管理整个系统的角色，如: 管理用户，管理权限等	0	0	0	2025-07-03 11:03:29	2025-07-03 11:03:32	100000000000001	100000000000001
100000000000002	机载领导	系统角色	可查看和管理所有用户的业务数据的角色	0	0	0	2025-07-03 11:04:29	2025-07-07 08:47:02	100000000000001	100000000000001
100000000000005	专家	系统角色	专家角色，进行审查结果审核	0	0	0	2026-01-30 11:04:29	2026-01-30 11:04:32	100000000000001	100000000000001
237378018947593	测试01	普通用户	测试01	1	1	0	2025-07-14 11:31:57.916489	2025-07-14 11:32:01.575194	100000000000002	100000000000002
100000000000004	普通用户	系统角色	普通用户角色	0	0	0	2025-07-03 11:04:29	2025-07-03 11:04:32	100000000000001	100000000000001
\.


--
-- Data for Name: urm_rolepower; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_rolepower (id, role_id, power_id, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
428057672192003	100000000000001	925644053573651	0	0	2022-01-29 22:10:59	2022-01-29 22:10:59	100000000000001	100000000000001
428057672192004	100000000000001	925644053573665	0	0	2022-01-29 22:10:59	2022-01-29 22:10:59	100000000000001	100000000000001
428057672192022	100000000000001	925644053574654	0	0	2022-01-29 22:10:59	2022-01-29 22:10:59	100000000000001	100000000000001
428057672192023	100000000000001	925644053573694	0	0	2022-01-29 22:10:59	2022-01-29 22:10:59	100000000000001	100000000000001
428057672192024	100000000000001	925644053573678	0	0	2022-01-29 22:10:59	2022-01-29 22:10:59	100000000000001	100000000000001
428057672192025	100000000000001	925644053573691	0	0	2022-01-29 22:10:59	2022-01-29 22:10:59	100000000000001	100000000000001
925644053573110	100000000000001	925644053573655	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573113	100000000000001	925644053573182	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573117	100000000000001	925644053573653	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573119	100000000000001	925644053573634	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573121	100000000000001	925644053573670	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573123	100000000000001	925644053573698	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573124	100000000000001	925644053573105	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573125	100000000000001	925644053573666	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573127	100000000000001	925644053573673	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573128	100000000000001	925644053573650	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573129	100000000000001	925644053573680	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573130	100000000000001	925644053573683	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573131	100000000000001	925644053573693	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573132	100000000000001	925644053573664	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573134	100000000000001	925644053573689	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573135	100000000000001	925644053573676	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573136	100000000000001	925644053573695	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573137	100000000000001	925644053573686	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573138	100000000000001	925644053573699	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573139	100000000000001	925644053573635	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573140	100000000000001	925644053573638	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573141	100000000000001	925644053573687	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573142	100000000000001	925644053573679	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573143	100000000000001	925644053573697	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573144	100000000000001	925644053573662	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573145	100000000000001	925644053573163	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573146	100000000000001	925644053573641	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573147	100000000000001	925644053573654	0	0	2021-12-06 12:00:24	2021-12-06 12:00:24	100000000000001	100000000000001
925644053573148	100000000000001	825644053573656	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573149	100000000000001	825644053573657	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573150	100000000000001	825644053573658	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573151	100000000000001	825644053573659	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573152	100000000000001	825644053573660	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573153	100000000000001	825644053573661	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573154	100000000000001	825644053573662	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573157	100000000000001	725644053573655	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573158	100000000000001	725644053573656	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573159	100000000000001	725644053573657	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573160	100000000000001	725644053573658	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573161	100000000000001	725644053573659	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573162	100000000000001	825644053573665	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573163	100000000000001	825644053573666	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573164	100000000000001	825644053573667	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573165	100000000000001	825644053573668	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573166	100000000000001	825644053573669	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573167	100000000000001	825644053573670	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573168	100000000000001	825644053573671	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573169	100000000000001	725644053573666	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573261	100000000000001	825664053573691	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573268	100000000000001	725644053573665	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053573270	100000000000001	825644053573672	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574162	100000000000001	825664053573792	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574163	100000000000001	825664053573793	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574164	100000000000001	825664053573794	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574165	100000000000001	825664053573795	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574166	100000000000001	825664053573796	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574167	100000000000001	825664053573797	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
925644053574261	100000000000001	825664053573791	0	0	2022-01-21 20:44:54	2022-01-21 20:44:54	100000000000001	100000000000001
\.


--
-- Data for Name: urm_user; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_user (id, department_id, login_name, password, name, telephone, profile, work_no, locked, need_change_password, is_editable, secret_level, comments, is_delete, version, create_date, update_date, create_user, update_user, email) FROM stdin;
100000000000001	110000000000001	admin	bCf3351E4368cF0d11dEbb5ecAdfB6fB3eA05F823f897c5b	admin	1135689644413	\N	\N	0	0	0	4	\N	0	7	2025-07-03 11:02:02	2025-07-03 11:02:04	100000000000001	100000000000001	\N
100000000000002	110000000000001	jzadmin	29c467346634435382cAec7bbFdbF68312162D874a48cc04	机载管理员	1335689644413	\N	\N	0	1	0	4	\N	0	5	2025-07-03 11:02:02	2025-07-03 11:02:04	100000000000001	0	\N
100000000000003	110000000000002	631admin	aC2f6db5268ed53e9fb95832a47aEa30d035dEf17cc1ef7e	631管理员	1345689644413	\N	\N	0	1	0	4	\N	0	2	2025-07-03 11:02:02	2025-07-03 11:02:04	100000000000001	0	\N
100000000000004	110000000000002	631expert	aC2f6db5268ed53e9fb95832a47aEa30d035dEf17cc1ef7e	631专家	1345689644413	\N	\N	0	1	0	4	\N	0	2	2026-01-30 11:02:02	2026-01-30 11:02:04	100000000000001	0	\N
\.


--
-- Data for Name: urm_userlog; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_userlog (id, content, model_name, operate_date, user_id, user_name, login_name) FROM stdin;
\.


--
-- Data for Name: urm_userrole; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.urm_userrole (id, user_id, role_id, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
237087256261664	100000000000001	100000000000001	0	0	2025-07-07 21:47:02.054434	2025-07-07 21:47:02.054434	0	0
237087256261665	100000000000002	100000000000002	0	0	2025-07-07 21:47:02.054434	2025-07-07 21:47:02.054434	0	0
237087256269346	100000000000003	100000000000003	0	0	2025-07-07 21:47:02.066467	2025-07-07 21:47:02.066467	0	0
237087256277025	100000000000004	100000000000005	0	0	2026-01-30 21:47:02.075939	2026-01-30 21:47:02.075939	0	0
237087256261641	100000000000013	100000000000004	0	0	2025-07-07 21:47:02.054434	2025-07-07 21:47:02.054434	0	0
237087256261642	100000000000023	100000000000004	0	0	2025-07-07 21:47:02.054434	2025-07-07 21:47:02.054434	0	0
237087256261643	100000000000014	100000000000004	0	0	2025-07-07 21:47:02.054434	2025-07-07 21:47:02.054434	0	0
237087256261644	100000000000024	100000000000004	0	0	2025-07-07 21:47:02.054434	2025-07-07 21:47:02.054434	0	0
237375857069065	237375282809865	100000000000003	0	0	2025-07-14 10:21:35.499801	2025-07-14 10:21:35.499858	100000000000001	100000000000001
237383220739081	237383212200457	100000000000004	0	0	2025-07-14 14:21:17.665297	2025-07-14 14:21:17.665369	100000000000001	100000000000001
\.


--
-- Data for Name: yjfk_answer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.yjfk_answer (id, f_id, answer, comments, is_delete, version, create_date, update_date, create_user, update_user, ref_id) FROM stdin;
\.


--
-- Data for Name: yjfk_append_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.yjfk_append_file (id, f_id, type, file_id, file_name, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Data for Name: yjfk_suggestion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.yjfk_suggestion (id, title, suggestion, status, description, comments, is_delete, version, create_date, update_date, create_user, update_user, file_id, file_version_id, result_id) FROM stdin;
\.


--
-- Data for Name: yjfk_suggestion_status; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.yjfk_suggestion_status (id, suggestion_id, status, comments, is_delete, version, create_date, update_date, create_user, update_user) FROM stdin;
\.


--
-- Name: logic_yjfk_answer code_yjfk_answer_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logic_yjfk_answer
    ADD CONSTRAINT code_yjfk_answer_copy1_pkey PRIMARY KEY (id);


--
-- Name: code_yjfk_answer code_yjfk_answer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_yjfk_answer
    ADD CONSTRAINT code_yjfk_answer_pkey PRIMARY KEY (id);


--
-- Name: logic_yjfk_append_file code_yjfk_append_file_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logic_yjfk_append_file
    ADD CONSTRAINT code_yjfk_append_file_copy1_pkey PRIMARY KEY (id);


--
-- Name: code_yjfk_append_file code_yjfk_append_file_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_yjfk_append_file
    ADD CONSTRAINT code_yjfk_append_file_pkey PRIMARY KEY (id);


--
-- Name: logic_yjfk_suggestion code_yjfk_suggestion_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logic_yjfk_suggestion
    ADD CONSTRAINT code_yjfk_suggestion_copy1_pkey PRIMARY KEY (id);


--
-- Name: code_yjfk_suggestion code_yjfk_suggestion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_yjfk_suggestion
    ADD CONSTRAINT code_yjfk_suggestion_pkey PRIMARY KEY (id);


--
-- Name: logic_yjfk_suggestion_status code_yjfk_suggestion_status_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logic_yjfk_suggestion_status
    ADD CONSTRAINT code_yjfk_suggestion_status_copy1_pkey PRIMARY KEY (id);


--
-- Name: code_yjfk_suggestion_status code_yjfk_suggestion_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.code_yjfk_suggestion_status
    ADD CONSTRAINT code_yjfk_suggestion_status_pkey PRIMARY KEY (id);


--
-- Name: dlsc_tool_file dlsc_file_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_tool_file
    ADD CONSTRAINT dlsc_file_copy1_pkey PRIMARY KEY (id);


--
-- Name: dlsc_file dlsc_file_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_file
    ADD CONSTRAINT dlsc_file_pkey PRIMARY KEY (id);


--
-- Name: dlsc_file_version dlsc_file_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_file_version
    ADD CONSTRAINT dlsc_file_version_pkey PRIMARY KEY (id);


--
-- Name: dlsc_naming_convention dlsc_naming_convention_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_naming_convention
    ADD CONSTRAINT dlsc_naming_convention_pkey PRIMARY KEY (id);


--
-- Name: dlsc_review_result_detail dlsc_review_result_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_review_result_detail
    ADD CONSTRAINT dlsc_review_result_detail_pkey PRIMARY KEY (id);


--
-- Name: dlsc_review_result dlsc_review_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_review_result
    ADD CONSTRAINT dlsc_review_result_pkey PRIMARY KEY (id);


--
-- Name: dlsc_rule dlsc_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_rule
    ADD CONSTRAINT dlsc_rule_pkey PRIMARY KEY (id);


--
-- Name: dmsc_tool_file dlsc_tool_file_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dmsc_tool_file
    ADD CONSTRAINT dlsc_tool_file_copy1_pkey PRIMARY KEY (id);


--
-- Name: dlsc_update_note dlsc_update_note_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dlsc_update_note
    ADD CONSTRAINT dlsc_update_note_pkey PRIMARY KEY (id);


--
-- Name: ljsc_file dmsc_file_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ljsc_file
    ADD CONSTRAINT dmsc_file_copy1_pkey PRIMARY KEY (id);


--
-- Name: dmsc_file dmsc_file_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dmsc_file
    ADD CONSTRAINT dmsc_file_pkey PRIMARY KEY (id);


--
-- Name: ljsc_file_version dmsc_file_version_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ljsc_file_version
    ADD CONSTRAINT dmsc_file_version_copy1_pkey PRIMARY KEY (id);


--
-- Name: dmsc_file_version dmsc_file_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dmsc_file_version
    ADD CONSTRAINT dmsc_file_version_pkey PRIMARY KEY (id);


--
-- Name: ljsc_review_result dmsc_review_result_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ljsc_review_result
    ADD CONSTRAINT dmsc_review_result_copy1_pkey PRIMARY KEY (id);


--
-- Name: ljsc_review_result_detail dmsc_review_result_detail_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ljsc_review_result_detail
    ADD CONSTRAINT dmsc_review_result_detail_copy1_pkey PRIMARY KEY (id);


--
-- Name: dmsc_review_result_detail dmsc_review_result_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dmsc_review_result_detail
    ADD CONSTRAINT dmsc_review_result_detail_pkey PRIMARY KEY (id);


--
-- Name: dmsc_review_result dmsc_review_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dmsc_review_result
    ADD CONSTRAINT dmsc_review_result_pkey PRIMARY KEY (id);


--
-- Name: ljsc_rule dmsc_rule_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ljsc_rule
    ADD CONSTRAINT dmsc_rule_copy1_pkey PRIMARY KEY (id);


--
-- Name: dmsc_rule dmsc_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dmsc_rule
    ADD CONSTRAINT dmsc_rule_pkey PRIMARY KEY (id);


--
-- Name: ljsc_tool_file dmsc_tool_file_copy1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ljsc_tool_file
    ADD CONSTRAINT dmsc_tool_file_copy1_pkey PRIMARY KEY (id);


--
-- Name: oauth_client oauth_client_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth_client
    ADD CONSTRAINT oauth_client_pkey PRIMARY KEY (id);


--
-- Name: pot_application pot_application_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pot_application
    ADD CONSTRAINT pot_application_pkey PRIMARY KEY (id);


--
-- Name: pot_info_append_file pot_info_append_file_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pot_info_append_file
    ADD CONSTRAINT pot_info_append_file_pkey PRIMARY KEY (id);


--
-- Name: pot_information pot_information_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pot_information
    ADD CONSTRAINT pot_information_pkey PRIMARY KEY (id);


--
-- Name: sjjyfx_experience_share sjjyfx_experience_share_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sjjyfx_experience_share
    ADD CONSTRAINT sjjyfx_experience_share_pkey PRIMARY KEY (id);


--
-- Name: sjjyfx_experience_share_reply sjjyfx_experience_share_reply_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sjjyfx_experience_share_reply
    ADD CONSTRAINT sjjyfx_experience_share_reply_pkey PRIMARY KEY (id);


--
-- Name: sjjyfx_like_record sjjyfx_like_record_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sjjyfx_like_record
    ADD CONSTRAINT sjjyfx_like_record_pkey PRIMARY KEY (id);


--
-- Name: urm_department urm_department_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_department
    ADD CONSTRAINT urm_department_pkey PRIMARY KEY (id);


--
-- Name: urm_power urm_power_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_power
    ADD CONSTRAINT urm_power_pkey PRIMARY KEY (id);


--
-- Name: urm_role urm_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_role
    ADD CONSTRAINT urm_role_pkey PRIMARY KEY (id);


--
-- Name: urm_rolepower urm_rolepower_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_rolepower
    ADD CONSTRAINT urm_rolepower_pkey PRIMARY KEY (id);


--
-- Name: urm_user urm_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_user
    ADD CONSTRAINT urm_user_pkey PRIMARY KEY (id);


--
-- Name: urm_userlog urm_userlog_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_userlog
    ADD CONSTRAINT urm_userlog_pkey PRIMARY KEY (id);


--
-- Name: urm_userrole urm_userrole_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.urm_userrole
    ADD CONSTRAINT urm_userrole_pkey PRIMARY KEY (id);


--
-- Name: yjfk_answer yjfk_answer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yjfk_answer
    ADD CONSTRAINT yjfk_answer_pkey PRIMARY KEY (id);


--
-- Name: yjfk_append_file yjfk_append_file_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yjfk_append_file
    ADD CONSTRAINT yjfk_append_file_pkey PRIMARY KEY (id);


--
-- Name: yjfk_suggestion yjfk_suggestion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yjfk_suggestion
    ADD CONSTRAINT yjfk_suggestion_pkey PRIMARY KEY (id);


--
-- Name: yjfk_suggestion_status yjfk_suggestion_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yjfk_suggestion_status
    ADD CONSTRAINT yjfk_suggestion_status_pkey PRIMARY KEY (id);


--
-- Name: answer_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX answer_create_date_index ON public.yjfk_answer USING btree (create_date);


--
-- Name: answer_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX answer_fid_index ON public.yjfk_answer USING btree (f_id);


--
-- Name: answer_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX answer_update_date_index ON public.yjfk_answer USING btree (update_date);


--
-- Name: append_file_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX append_file_fid_index ON public.yjfk_append_file USING btree (f_id);


--
-- Name: append_file_type_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX append_file_type_index ON public.yjfk_append_file USING btree (type);


--
-- Name: code_answer_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_answer_create_date_index ON public.code_yjfk_answer USING btree (create_date);


--
-- Name: code_answer_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_answer_fid_index ON public.code_yjfk_answer USING btree (f_id);


--
-- Name: code_answer_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_answer_update_date_index ON public.code_yjfk_answer USING btree (update_date);


--
-- Name: code_append_file_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_append_file_fid_index ON public.code_yjfk_append_file USING btree (f_id);


--
-- Name: code_append_file_type_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_append_file_type_index ON public.code_yjfk_append_file USING btree (type);


--
-- Name: code_suggestion_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_suggestion_create_date_index ON public.code_yjfk_suggestion USING btree (create_date);


--
-- Name: code_suggestion_status_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_suggestion_status_create_date_index ON public.code_yjfk_suggestion_status USING btree (create_date);


--
-- Name: code_suggestion_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX code_suggestion_update_date_index ON public.code_yjfk_suggestion USING btree (update_date);


--
-- Name: department_f_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX department_f_id_index ON public.urm_department USING btree (f_id);


--
-- Name: department_name_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX department_name_index ON public.urm_department USING btree (name);


--
-- Name: experience_share_contributor_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX experience_share_contributor_index ON public.sjjyfx_experience_share USING btree (contributor);


--
-- Name: experience_share_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX experience_share_create_date_index ON public.sjjyfx_experience_share USING btree (create_date);


--
-- Name: experience_share_reply_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX experience_share_reply_create_date_index ON public.sjjyfx_experience_share_reply USING btree (create_date);


--
-- Name: experience_share_reply_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX experience_share_reply_fid_index ON public.sjjyfx_experience_share_reply USING btree (f_id);


--
-- Name: experience_share_reply_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX experience_share_reply_update_date_index ON public.sjjyfx_experience_share_reply USING btree (update_date);


--
-- Name: experience_share_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX experience_share_update_date_index ON public.sjjyfx_experience_share USING btree (update_date);


--
-- Name: file_create_user_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_create_user_index ON public.dlsc_file USING btree (create_user);


--
-- Name: file_department_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_department_id_index ON public.dlsc_file USING btree (department_id);


--
-- Name: file_file_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_file_id_index ON public.dlsc_file USING btree (file_id);


--
-- Name: file_is_recycle_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_is_recycle_index ON public.dlsc_file USING btree (is_recycle);


--
-- Name: file_minio_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_minio_id_index ON public.dmsc_file USING btree (minio_id);


--
-- Name: file_owner_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_owner_id_index ON public.dlsc_file USING btree (owner_id);


--
-- Name: file_version_create_user_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_version_create_user_index ON public.dlsc_file_version USING btree (create_user);


--
-- Name: file_version_department_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_version_department_id_index ON public.dlsc_file_version USING btree (department_id);


--
-- Name: file_version_file_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_version_file_id_index ON public.dlsc_file_version USING btree (file_id);


--
-- Name: file_version_is_recycle_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_version_is_recycle_index ON public.dlsc_file_version USING btree (is_recycle);


--
-- Name: file_version_owner_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX file_version_owner_id_index ON public.dlsc_file_version USING btree (owner_id);


--
-- Name: like_record_experience_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX like_record_experience_id_index ON public.sjjyfx_like_record USING btree (experience_id);


--
-- Name: like_record_user_experience_unique; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX like_record_user_experience_unique ON public.sjjyfx_like_record USING btree (experience_id, user_id);


--
-- Name: logic_answer_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_answer_create_date_index ON public.logic_yjfk_answer USING btree (create_date);


--
-- Name: logic_answer_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_answer_fid_index ON public.logic_yjfk_answer USING btree (f_id);


--
-- Name: logic_answer_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_answer_update_date_index ON public.logic_yjfk_answer USING btree (update_date);


--
-- Name: logic_append_file_fid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_append_file_fid_index ON public.logic_yjfk_append_file USING btree (f_id);


--
-- Name: logic_append_file_type_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_append_file_type_index ON public.logic_yjfk_append_file USING btree (type);


--
-- Name: logic_file_minio_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_file_minio_id_index ON public.ljsc_file USING btree (minio_id);


--
-- Name: logic_suggestion_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_suggestion_create_date_index ON public.logic_yjfk_suggestion USING btree (create_date);


--
-- Name: logic_suggestion_status_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_suggestion_status_create_date_index ON public.logic_yjfk_suggestion_status USING btree (create_date);


--
-- Name: logic_suggestion_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX logic_suggestion_update_date_index ON public.logic_yjfk_suggestion USING btree (update_date);


--
-- Name: naming_convention_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX naming_convention_create_date_index ON public.dlsc_naming_convention USING btree (create_date);


--
-- Name: naming_convention_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX naming_convention_update_date_index ON public.dlsc_naming_convention USING btree (update_date);


--
-- Name: review_result_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_create_date_index ON public.dlsc_review_result USING btree (create_date);


--
-- Name: review_result_detail_result_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_detail_result_create_date_index ON public.dlsc_review_result_detail USING btree (create_date);


--
-- Name: review_result_detail_result_device_type_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_detail_result_device_type_index ON public.dlsc_review_result_detail USING btree (device_type);


--
-- Name: review_result_detail_result_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_detail_result_id_index ON public.dlsc_review_result_detail USING btree (result_id);


--
-- Name: review_result_detail_result_is_passed_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_detail_result_is_passed_index ON public.dlsc_review_result_detail USING btree (is_passed);


--
-- Name: review_result_detail_result_rule_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_detail_result_rule_id_index ON public.dlsc_review_result_detail USING btree (rule_id);


--
-- Name: review_result_file_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_file_id_index ON public.dlsc_review_result USING btree (file_id);


--
-- Name: review_result_file_version_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_file_version_id_index ON public.dlsc_review_result USING btree (file_version_id);


--
-- Name: review_result_review_time_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_review_time_index ON public.dlsc_review_result USING btree (review_time);


--
-- Name: review_result_status_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX review_result_status_index ON public.dlsc_review_result USING btree (status);


--
-- Name: rule_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rule_create_date_index ON public.dlsc_rule USING btree (create_date);


--
-- Name: rule_type_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rule_type_index ON public.dlsc_rule USING btree (type);


--
-- Name: suggestion_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX suggestion_create_date_index ON public.yjfk_suggestion USING btree (create_date);


--
-- Name: suggestion_status_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX suggestion_status_create_date_index ON public.yjfk_suggestion_status USING btree (create_date);


--
-- Name: suggestion_update_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX suggestion_update_date_index ON public.yjfk_suggestion USING btree (update_date);


--
-- Name: update_note_create_date_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX update_note_create_date_index ON public.dlsc_update_note USING btree (create_date);


--
-- Name: update_note_update_time_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX update_note_update_time_index ON public.dlsc_update_note USING btree (update_time);


--
-- Name: userrole_user_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX userrole_user_id_index ON public.urm_userrole USING btree (user_id);


--
-- PostgreSQL database dump complete
--

\unrestrict CunaKHXLrjl5uayrmVDgZggEFG35t8jDuVxBA1v79pu2mH2R5xBxhLfzzjqAlMV

