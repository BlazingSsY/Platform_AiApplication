import { get, post, patch, put } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getFeedbackData = (data: any) => get(`/sourcecodereview/v1/suggestions/page`, data).then(fnRes);

export const addFeedback = (data: any) => post(`/sourcecodereview/v1/suggestions`, data).then(fnRes);

export const deleteFeedback = (data: any) => patch(`/sourcecodereview/v1/suggestions`, data).then(fnRes)

export const changeFeedbackStatus = (data: any) => put(`/sourcecodereview/v1/suggestions/status/${data.id}`, data).then(fnRes)

export const getReplies = (data: any) => get(`/sourcecodereview/v1/answers/page`, data).then(fnRes)

export const addReply = (data: any) => post(`/sourcecodereview/v1/answers`, data).then(fnRes)
export const delReply = (data: any) => patch(`/sourcecodereview/v1/answers`, data).then(fnRes)

export const storageUpload = (data: any) => post(`/sourcecodereview/common/v1/storage/upload`, data).then(fnRes); // 文件上传

export const getFileUrl = (fileid: any) => get(`/sourcecodereview/common/v1/storage/download/${fileid}`, fileid).then(fnRes); 
export const suggestionStatus = (data: any) => get(`/sourcecodereview/v1/suggestion-status`, data).then(fnRes); 