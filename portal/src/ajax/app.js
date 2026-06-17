import { get, post,postFromData, put, del, patch } from './ajax';

const fnRes = (res) => (res ? res.data : {});

export const applicationsDetail = (data) => get(`/portal/v1/applications/${data.id}`, data).then(fnRes) // 根据应用id获取应用详情
export const applicationsEdit = (data) => put(`/portal/v1/applications/${data.id}`, data).then(fnRes) // 修改应用
export const changeStatus = (data) => put(`/portal/v1/applications/status`, data).then(fnRes) // 修改应用
export const applicationsList = (data) => get(`/portal/v1/applications`, data).then(fnRes) // 应用列表
export const applicationsAdd = (data) => post(`/portal/v1/applications`, data).then(fnRes) // 创建应用
export const applicationsDel = (data) => patch(`/portal/v1/applications`, data).then(fnRes) // 删除应用
export const applicationsPages = (data) => get(`/portal/v1/applications/pages`, data).then(fnRes) // 应用分页获取


export const uploadFile = (data) => postFromData(`/portal/common/v1/storage/upload`, data).then(fnRes) // 上传文件
export const downloadFile = (data) => get(`/portal/common/v1/storage/download/${data.fileId}`, data).then(fnRes) // 下载文件


