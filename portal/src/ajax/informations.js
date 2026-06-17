import { get, post,postFromData, put, del, patch } from './ajax';

const fnRes = (res) => (res ? res.data : {});

export const informationsDetail = (data) => get(`/portal/v1/informations/${data.id}`, data).then(fnRes) // 根据资讯id获取资讯详情
export const informationsEdit = (data) => put(`/portal/v1/informations/${data.id}`, data).then(fnRes) // 根据id修改资讯
export const informationsStatus= (data) => put(`/portal/v1/informations/status`, data).then(fnRes) // 修改资讯状态（发布/下线）
export const informationsList = (data) => get(`/portal/v1/informations`, data).then(fnRes) // 获取所有资讯列表
export const informationsAdd = (data) => post(`/portal/v1/informations`, data).then(fnRes) // 创建资讯
export const informationsDel = (data) => patch(`/portal/v1/informations`, data).then(fnRes) // 删除资讯(安全删除)
export const informationAttachments = (data) => get(`/portal/v1/informations/${data.infoId}/attachments`, data).then(fnRes) // 根据资讯id获取附件列表
export const informationAddAttachments = (data) => post(`/portal/v1/informations/${data.infoId}/attachments`, data.data).then(fnRes) // 添加资讯附件
export const informationsPages = (data) => get(`/portal/v1/informations/pages`, data).then(fnRes) // 分页查询资讯
export const informationsDelAttachments = (data) => del(`/portal/v1/informations/attachments/${data.attachmentId }`, data).then(fnRes) // 删除资讯附件


// 资讯设置
export const informationsConfig = (data) => get(`/portal/v1/informations/config`, data).then(fnRes) // 获取资讯配置
export const informationsPortalHome= (data) => get(`/portal/v1/informations/portal-home`, data).then(fnRes) // 获取门户首页资讯滚动列表
export const informationsConfigUpdate= (data) => put(`/portal/v1/informations/config`, data).then(fnRes) // 更新资讯配置
