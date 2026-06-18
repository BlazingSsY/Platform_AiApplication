import { get, post, postFromData, put, del, patch } from './ajax';

const fnResNew = (res: any) => (res ? res.data : {});

// 代码审查 - 文件与审查
export const sourceCodeUploadCheckFile = (fileName: any) => post(`/sourcecodereview/v1/source-code-file/check-exists`, fileName).then(fnResNew);
export const sourceCodeUploadFile = (data: any) => postFromData(`/sourcecodereview/v1/source-code-file/upload?fileSecretLevelEnum=${data.fileSecretLevelEnum}&compatibleModels=${data.compatibleModels}&productModel=${data.productModel}&productName=${data.productName}&configName=${data.configName}&codeFileVersion=${data.codeFileVersion}`, data.file).then(fnResNew);
export const getSourceCodeFilePage = (data?: any) => get(`/sourcecodereview/v1/source-code-file/page`, data).then(fnResNew);
export const createSourceCodeReview = (data: any) => post(`/sourcecodereview/v1/source-code-review`, data, { timeout: 300000 }).then(fnResNew);
// export const getSourceCodeReviewCode = (data: any) => post(`/sourcecodereview/v1/source-code-review-result/source/code`, data, { timeout: 300000 }).then(fnResNew);
export const getSourceCodeReviewCode = (data: any) => get(`/sourcecodereview/v1/source-code-review-result/source/code`, data, { timeout: 300000 }).then(fnResNew);
export const sourceCodeReviewResultFileId = (data: any) => patch(`/sourcecodereview/v1/source-code-review-result/fileId`, data).then(fnResNew);
// export const deleteSourceCodeFile = (data: any) => patch(`/sourcecodereview/v1/source-code-file`, data).then(fnResNew);
export const deleteSourceCodeFile = (data: any) => patch(`/sourcecodereview/v1/source-code-file`, data).then(fnResNew);
export const sourceCodeFileIsRecycle = (data: any) => put(`/sourcecodereview/v1/source-code-file/is-recycle`, data).then(fnResNew);
export const getSourceCodeReviewRules = (data?: any) => get(`/sourcecodereview/v1/source-code-review-rule`, data).then(fnResNew);
export const getSourceCodeReviewResultDetailPage = (data?: any) => get(`/sourcecodereview/v1/source-code-review-result-detail/page`, data).then(fnResNew);
export const getSourceCodeReviewResultDetailList = (data?: any) => get(`/sourcecodereview/v1/source-code-review-result-detail`, data).then(fnResNew);
export const getSourceCodeRecyclePage = (data?: any) => get(`/sourcecodereview/v1/source-code-file/recycle/page`, data).then(fnResNew);
export const getSourceCodeReviewResultFilters = (data?: any) => get(`/sourcecodereview/v1/source-code-review-result/${data.id}/filters`, data).then(fnResNew);
export const getFileLink = (data?: any) => get(`/sourcecodereview/v1/source-code-file-version/file-link/${data.fileId}`, data).then(fnResNew);
export const sourceCodeReviewResultDetailList = (data?: any) => get(`/sourcecodereview/v1/source-code-review-result/detail/list`, data).then(fnResNew);
export const getSourceCodeReviewResultVersion = (data?: any) => get(`/sourcecodereview/v1/source-code-review-result/detail/version`, data).then(fnResNew);

// 新增api
//代码停止审查
export const stopSourceCodeReview = (data: any) => post(`/sourcecodereview/v1/source-code-review/stop`, data).then(fnResNew);
// 获取任务等待情况
export const waitTaskInfo = (data: any) => get(`/sourcecodereview/v1/source-code-review-result/wait-task`, data).then(fnResNew);
// 4. 查询审查进度
export const getSourceCodeReviewProgress = (data: any) => get(`/sourcecodereview/v1/source-code-review-result/process/${data.id}`,data).then(fnResNew);

// 审查结果与规则摘要
export const getSourceCodeReviewResult = (id: number) => get(`/sourcecodereview/v1/source-code-review-result/${id}`).then(fnResNew);
export const getSourceCodeReviewRuleSummary = (id: number) => get(`/sourcecodereview/v1/source-code-review-result/rule-summary`, { id }).then(fnResNew);

// 文件版本管理
export const uploadSourceCodeVersionFile = (data: any) => postFromData(`/sourcecodereview/v1/source-code-file-version/upload?fileId=${data.fileId}&fileSecretLevelEnum=${data.fileSecretLevelEnum}`, data.file).then(fnResNew);
export const sourceCodeFileVersion = (data: any) => get(`/sourcecodereview/v1/source-code-file/${data.fileId}/version`, data).then(fnResNew);
export const sourceCodeFileResult = (data: any) => get(`/sourcecodereview/v1/source-code-file-version/${data.versionId}/result`, data).then(fnResNew);
export const sourceCodeFileVersionResult = (data: any) => get(`/sourcecodereview/v1/source-code-file/${data.fileId}/version-result`, data).then(fnResNew);
export const delSourceCodeReviewResult = (data: any) => del(`/sourcecodereview/v1/source-code-review-result/${data.id}`, data).then(fnResNew);
export const sourceCodeFileVersionFs = (data: any) => patch(`/sourcecodereview/v1/source-code-file-version`, data).then(fnResNew);
export const sourceCodeFileFs = (data: any) => patch(`/sourcecodereview/v1/source-code-file`, data).then(fnResNew);

// 工具文件管理
export const toolFileUploadFile = (data: any) => postFromData(`/sourcecodereview/v1/tool-file/upload?toolName=${data.toolName}&comments=${data.comments}`, data.file).then(fnResNew); // 上传文件
export const deleteToolFile = (data: any) => patch(`/sourcecodereview/v1/tool-file`, data).then(fnResNew); // 删除文件
export const getToolFile = (data: any) => get(`/sourcecodereview/v1/tool-file/${data.id}`,data).then(fnResNew); //
export const getToolPages = (data: any) => get(`/sourcecodereview/v1/tool-file/page`,data).then(fnResNew); //

// 通过三方接口获取所有规则
export const sourceCodeReviewRuleAll = (data: any) => post(`/sourcecodereview/v1/source-code-review-rule/all`,data).then(fnResNew); //
// 通过三方接口获取规则详情
export const sourceCodeReviewRuleDetails = (data: any) => post(`/sourcecodereview/v1/source-code-review-rule/details`,data).then(fnResNew); //
// 通过三方接口选中规则
export const sourceCodeReviewRuleSelect = (data: any) => post(`/sourcecodereview/v1/source-code-review-rule/select`,data).then(fnResNew); //
export const sourceCodeReviewRuleMetadata = (data: any) => get(`/sourcecodereview/v1/source-code-review-rule/metadata`,data).then(fnResNew);

// 日志查询
export const getLog = () => get(`/sourcecodereview/v1/log`).then(fnResNew);
export const getServiceLog = (num: number = 5000) => get(`/sourcecodereview/v1/log/service`, { num }).then(fnResNew);
//

// 获取代码版本列表
export const getSourceCodeReviewResultSourceVersions = (date) => get(`/sourcecodereview/v1/source-code-review-result/source/versions`, date).then(fnResNew);

// 源代码复审
export const sourceCodeReviewRecheck = (data: any) => post(`/sourcecodereview/v1/source-code-review-recheck`, data).then(fnResNew);

// 审核列表
// 分页查询代码审查复核核
export const sourceCodeReviewRecheckPage = (data: any) => get(`/sourcecodereview/v1/source-code-review-recheck/page`,data).then(fnResNew); 
// 根据文件版本ID和版本号查看复核详情
export const sourceCodeReviewRecheckDetail = (data: any) => get(`/sourcecodereview/v1/source-code-review-recheck/detail`,data).then(fnResNew); 
export const sourceCodeReviewRecheckResultSubmit = (data: any) => post(`/sourcecodereview/v1/source-code-review-recheck/result/submit`,data).then(fnResNew); 