-- 门户首页应用卡片种子数据（可选，仅在需要预置内置应用时导入）。
-- 约束（来自 ApplicationServiceImpl.getApplicationList）：
--   * module 必须是以下之一，否则不会显示：设计研发 / 运营管理 / 生产制造 / 算力资源
--   * sequence 不能为 NULL（后端按 sequence 排序，空值会 NPE）
--   * status=1 卡片才可点击（0=未上线，灰显）
--   * icon 不能为空：不含 ".png" 时按前端本地图标键解析
--     （可用键：circuitHover/codeHover/elgHover/elg/erds/pcb/pcba/dsr/crr/asc/pdkb/cn/icn）；
--      含 ".png" 时按 MinIO fileId 下载
--   * url 为站内路由（如 /circuitReviewHome）或 http(s):// 外链
--
-- 复制本段即可新增你自己的应用，注意 id 不要重复。

INSERT INTO "public"."pot_application"
    (id, name, eng_name, module, icon, status, url, sequence, is_delete, version, create_date, update_date, create_user, update_user)
VALUES
    (900000000000001, '电路智能审查',       'Circuit Review',     '设计研发', 'crr', 1, '/circuitReviewHome', 1, 0, 0, now(), now(), 100000000000001, 100000000000001),
    (900000000000002, '软件代码智能审查',   'Source Code Review', '设计研发', 'dsr', 1, '/codeReviewHome',    2, 0, 0, now(), now(), 100000000000001, 100000000000001),
    (900000000000003, '硬件逻辑智能审查',   'Logic Review',       '设计研发', 'elg', 1, '/logicReviewHome',   3, 0, 0, now(), now(), 100000000000001, 100000000000001)
ON CONFLICT (id) DO NOTHING;
