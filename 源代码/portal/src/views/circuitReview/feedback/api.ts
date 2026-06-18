import { get, post, patch, put } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getFeedbackData = (data: any) => get(`/circuitreview/v1/suggestions/page`, data).then(fnRes);

export const addFeedback = (data: any) => post(`/circuitreview/v1/suggestions`, data).then(fnRes);

export const deleteFeedback = (data: any) => patch(`/circuitreview/v1/suggestions`, data).then(fnRes)

export const changeFeedbackStatus = (data: any) => put(`/circuitreview/v1/suggestions/status/${data.id}`, data).then(fnRes)

export const getReplies = (data: any) => get(`/circuitreview/v1/answers/page`, data).then(fnRes)

export const addReply = (data: any) => post(`/circuitreview/v1/answers`, data).then(fnRes)
export const delReply = (data: any) => patch(`/circuitreview/v1/answers`, data).then(fnRes)

export const storageUpload = (data: any) => post(`/circuitreview/common/v1/storage/upload`, data).then(fnRes); // 文件上传

export const getFileUrl = (fileid: any) => get(`/circuitreview/common/v1/storage/download/${fileid}`, fileid).then(fnRes); 
export const suggestionStatus = (data: any) => get(`/circuitreview/v1/suggestion-status`, data).then(fnRes); 