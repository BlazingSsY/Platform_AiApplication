import { get, post, postFromData, put, del, patch } from './ajax';

const fnResNew = (res: any) => (res ? res.data : {});

// 代码审查 - 文件与审查
export const logicUploadCheckFile = (fileName: any) => post(`/logicreview/v1/logic-file/check-exists`, fileName).then(fnResNew);
export const logicUploadFile = (data: any) => postFromData(`/logicreview/v1/logic-file/upload?fileSecretLevelEnum=${data.fileSecretLevelEnum}&compatibleModels=${data.compatibleModels}&productModel=${data.productModel}&productName=${data.productName}&configName=${data.configName}&codeFileVersion=${data.codeFileVersion}`, data.file).then(fnResNew);
export const getSourceCodeFilePage = (data?: any) => get(`/logicreview/v1/logic-file/page`, data).then(fnResNew);
export const createSourceCodeReview = (data: any) => post(`/logicreview/v1/logic-review`, data, { timeout: 300000 }).then(fnResNew);
// export const getSourceCodeReviewCode = (data: any) => post(`/logicreview/v1/logic-review-result/source/code`, data, { timeout: 300000 }).then(fnResNew);
export const getSourceCodeReviewCode = (data: any) => get(`/logicreview/v1/logic-review-result/source/code`, data, { timeout: 300000 }).then(fnResNew);
export const logicReviewResultFileId = (data: any) => patch(`/logicreview/v1/logic-review-result/fileId`, data).then(fnResNew);
// export const deleteSourceCodeFile = (data: any) => patch(`/logicreview/v1/logic-file`, data).then(fnResNew);
export const deleteSourceCodeFile = (data: any) => patch(`/logicreview/v1/logic-file`, data).then(fnResNew);
export const logicFileIsRecycle = (data: any) => put(`/logicreview/v1/logic-file/is-recycle`, data).then(fnResNew);
export const getSourceCodeReviewRules = (data?: any) => get(`/logicreview/v1/logic-review-rule`, data).then(fnResNew);
export const getSourceCodeReviewResultDetailPage = (data?: any) => get(`/logicreview/v1/logic-review-result-detail/page`, data).then(fnResNew);
export const getSourceCodeReviewResultDetailList = (data?: any) => get(`/logicreview/v1/logic-review-result-detail`, data).then(fnResNew);
export const getSourceCodeRecyclePage = (data?: any) => get(`/logicreview/v1/logic-file/recycle/page`, data).then(fnResNew);
export const getSourceCodeReviewResultFilters = (data?: any) => get(`/logicreview/v1/logic-review-result/${data.id}/filters`, data).then(fnResNew);
export const getFileLink = (data?: any) => get(`/logicreview/v1/logic-file-version/file-link/${data.fileId}`, data).then(fnResNew);
export const logicReviewResultDetailList = (data?: any) => get(`/logicreview/v1/logic-review-result/detail/list`, data).then(fnResNew);
export const getSourceCodeReviewResultVersion = (data?: any) => get(`/logicreview/v1/logic-review-result/detail/version`, data).then(fnResNew);

// 新增api
//代码停止审查
export const stopSourceCodeReview = (data: any) => post(`/logicreview/v1/logic-review/stop`, data).then(fnResNew);
// 获取任务等待情况
export const waitTaskInfo = (data: any) => get(`/logicreview/v1/logic-review-result/wait-task`, data).then(fnResNew);
// 4. 查询审查进度
export const getSourceCodeReviewProgress = (data: any) => get(`/logicreview/v1/logic-review-result/process/${data.id}`,data).then(fnResNew);

// 审查结果与规则摘要
export const getSourceCodeReviewResult = (id: number) => get(`/logicreview/v1/logic-review-result/${id}`).then(fnResNew);
export const getSourceCodeReviewRuleSummary = (id: number) => get(`/logicreview/v1/logic-review-result/rule-summary`, { id }).then(fnResNew);

// 文件版本管理
export const uploadSourceCodeVersionFile = (data: any) => postFromData(`/logicreview/v1/logic-file-version/upload?fileId=${data.fileId}&fileSecretLevelEnum=${data.fileSecretLevelEnum}`, data.file).then(fnResNew);
export const logicFileVersion = (data: any) => get(`/logicreview/v1/logic-file/${data.fileId}/version`, data).then(fnResNew);
export const logicFileResult = (data: any) => get(`/logicreview/v1/logic-file-version/${data.versionId}/result`, data).then(fnResNew);
export const logicFileVersionResult = (data: any) => get(`/logicreview/v1/logic-file/${data.fileId}/version-result`, data).then(fnResNew);
export const delSourceCodeReviewResult = (data: any) => del(`/logicreview/v1/logic-review-result/${data.id}`, data).then(fnResNew);
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
export const logicReviewRuleMetadata = (data: any) => get(`/logicreview/v1/logic-review-rule/metadata`,data).then(fnResNew);

// 日志查询
export const getLog = () => get(`/logicreview/v1/log`).then(fnResNew);
export const getServiceLog = (num: number = 5000) => get(`/logicreview/v1/log/service`, { num }).then(fnResNew);
//

// 获取代码版本列表
export const getSourceCodeReviewResultSourceVersions = (date) => get(`/logicreview/v1/logic-review-result/source/versions`, date).then(fnResNew);

// 源代码复审
export const logicReviewRecheck = (data: any) => post(`/logicreview/v1/logic-review-recheck`, data).then(fnResNew);

// 审核列表
// 分页查询代码审查复核核
export const logicReviewRecheckPage = (data: any) => get(`/logicreview/v1/logic-review-recheck/page`,data).then(fnResNew); 
// 根据文件版本ID和版本号查看复核详情
export const logicReviewRecheckDetail = (data: any) => get(`/logicreview/v1/logic-review-recheck/detail`,data).then(fnResNew); 
export const logicReviewRecheckResultSubmit = (data: any) => post(`/logicreview/v1/logic-review-recheck/result/submit`,data).then(fnResNew); 