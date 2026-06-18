import { get, post, postFromData, put, del, patch } from './ajax';

const fnResNew = (res: any) => (res ? res.data : {});

// 硬件逻辑智能审查 - 文件与审查
export const logicUploadCheckFile = (fileName: any) => post(`/logicreview/v1/logic-file/check-exists`, { fileName }).then(fnResNew);
export const logicUploadFile = (data: any) => postFromData(`/logicreview/v1/logic-file/upload?fileSecretLevelEnum=${data.fileSecretLevelEnum}`, data.file).then(fnResNew);
export const getLogicFilePage = (data?: any) => get(`/logicreview/v1/logic-file/page`, data).then(fnResNew);
export const createLogicReview = (data: any) => post(`/logicreview/v1/logic-review`, data, { timeout: 300000 }).then(fnResNew);
export const logicReviewResultFileId = (data: any) => patch(`/logicreview/v1/logic-review-result/fileId`, data).then(fnResNew);
// export const deleteLogicFile = (data: any) => patch(`/logicreview/v1/logic-file`, data).then(fnResNew);
export const deleteLogicFile = (data: any) => patch(`/logicreview/v1/logic-file`, data).then(fnResNew);
export const logicFileIsRecycle = (data: any) => put(`/logicreview/v1/logic-file/is-recycle`, data).then(fnResNew);
export const getLogicReviewRules = (data?: any) => get(`/logicreview/v1/logic-review-rule`, data).then(fnResNew);
export const getLogicReviewResultDetailPage = (data?: any) => get(`/logicreview/v1/logic-review-result-detail/page`, data).then(fnResNew);
export const getLogicReviewResultDetailList = (data?: any) => get(`/logicreview/v1/logic-review-result-detail`, data).then(fnResNew);
export const getLogicRecyclePage = (data?: any) => get(`/logicreview/v1/logic-file/recycle/page`, data).then(fnResNew);
export const getLogicReviewResultFilters = (data?: any) => get(`/logicreview/v1/logic-review-result/${data.id}/filters`, data).then(fnResNew);
export const getFileLink = (data?: any) => get(`/logicreview/v1/logic-file-version/file-link/${data.fileId}`, data).then(fnResNew);

// 审查结果与规则摘要
export const getLogicReviewResult = (id: number) => get(`/logicreview/v1/logic-review-result/${id}`).then(fnResNew);
export const getLogicReviewRuleSummary = (id: number) => get(`/logicreview/v1/logic-review-result/rule-summary`, { id }).then(fnResNew);

// 文件版本管理
export const uploadLogicVersionFile = (data: any) => postFromData(`/logicreview/v1/logic-file-version/upload?fileId=${data.fileId}&fileSecretLevelEnum=${data.fileSecretLevelEnum}`, data.file).then(fnResNew);
export const logicFileVersion = (data: any) => get(`/logicreview/v1/logic-file/${data.fileId}/version`, data).then(fnResNew);
export const logicFileResult = (data: any) => get(`/logicreview/v1/logic-file-version/${data.versionId}/result`, data).then(fnResNew);
export const logicFileVersionResult = (data: any) => get(`/logicreview/v1/logic-file/${data.fileId}/version-result`, data).then(fnResNew);
export const delLogicReviewResult = (data: any) => del(`/logicreview/v1/logic-review-result/${data.id}`, data).then(fnResNew);
export const logicFileVersionFs = (data: any) => patch(`/logicreview/v1/logic-file-version`, data).then(fnResNew);
export const logicFileFs = (data: any) => patch(`/logicreview/v1/logic-file`, data).then(fnResNew);

// 工具文件管理
export const toolFileUploadFile = (data: any) => postFromData(`/logicreview/v1/tool-file/upload?toolName=${data.toolName}&comments=${data.comments}`, data.file).then(fnResNew); // 上传文件
export const deleteToolFile = (data: any) => patch(`/logicreview/v1/tool-file`, data).then(fnResNew); // 删除文件
export const getToolFile = (data: any) => get(`/logicreview/v1/tool-file/${data.id}`,data).then(fnResNew); //
export const getToolPages = (data: any) => get(`/logicreview/v1/tool-file/page`,data).then(fnResNew); //

// 通过三方接口获取所有规则
export const logicReviewRuleAll = (data: any) => post(`/logicreview/v1/logic-review-rule/all`,data).then(fnResNew); //
// 通过三方接口获取规则详情
export const logicReviewRuleDetails = (data: any) => post(`/logicreview/v1/logic-review-rule/details`,data).then(fnResNew); //
// 通过三方接口选中规则
export const logicReviewRuleSelect = (data: any) => post(`/logicreview/v1/logic-review-rule/select`,data).then(fnResNew); //
export const logicReviewRuleMetadata = (data: any) => get(`/logicreview/v1/logic-review-rule/metadata`,data).then(fnResNew); //