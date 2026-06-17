import { get, post, patch, put } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

// 查看回复详情
export const getReplyDetail = (id: string) => get(`/circuitreview/v1/experience-share-replies/${id}`).then(fnRes);

// 修改回复
export const updateReply = (id: string, data: any) => put(`/circuitreview/v1/experience-share-replies/${id}`, data).then(fnRes);

// 创建回复
export const addReply = (data: any) => post(`/circuitreview/v1/experience-share-replies`, data).then(fnRes);

// 删除回复
export const delReply = (data: any) => patch(`/circuitreview/v1/experience-share-replies`, data).then(fnRes);

// 分页查询回复
export const getReplies = (data: any) => get(`/circuitreview/v1/experience-share-replies/page`, data).then(fnRes);

export const storageUpload = (data: any) => post(`/circuitreview/common/v1/storage/upload`, data).then(fnRes); // 文件上传
