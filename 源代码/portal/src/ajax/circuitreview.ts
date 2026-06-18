import axios from 'axios';
import { get, post,postFromData, put, del, patch } from './ajax';
import { getToken } from '@/utils/auth';
import { ElMessage } from 'element-plus';

const fnResNew = (res: any) => (res ? res.data : {});
const VITE_APP_API_BASE_URL: string = import.meta.env.VITE_APP_API_BASE_URL;

// 新的业务接口
export const uploadCheckFile = (data : any) => post(`/circuitreview/v1/circuit-file/check-exists`,data).then(fnResNew); // 检查当前用户下的同名文件是否存在(直接上传文件)
export const checkInAudit = (fileName : any) => get(`/circuitreview/v1/circuit-file-version/check-in-audit`,fileName).then(fnResNew); // 检查该文件是否存在正在复核的版本(直接上传文件)

export const uploadFile = (data: any) => postFromData(`/circuitreview/v1/circuit-file/upload?fileSecretLevelEnum=${data.fileSecretLevelEnum}&compatibleModels=${data.compatibleModels}&productModel=${data.productModel}&productName=${data.productName}&diagramNumber=${data.diagramNumber}&diagramVersion=${data.diagramVersion}&isTestFile=${data.isTestFile}`, data.file).then(fnResNew); // 上传文件
export const getCircuitFilePage = (data?: any) => get(`/circuitreview/v1/circuit-file/page`, data).then(fnResNew); // 分页查询电路图文件
export const createCircuitReview = (data: any) => post(`/circuitreview/v1/circuit-review`, data, { 'timeout': 300000 }).then(fnResNew); // 创建电路审查
export const circuitReviewResultFileId = (data: any) => patch(`/circuitreview/v1/circuit-review-result/fileId`, data).then(fnResNew); // 根据文件ID删除所有审查结果
export const deleteCircuitFile = (data: any) => patch(`/circuitreview/v1/circuit-file`, data).then(fnResNew); // 删除指定的文件
export const circuitFileIsRecycle = (data: any) => put(`/circuitreview/v1/circuit-file/is-recycle`,data).then(fnResNew); // 删除指定的文件
export const getCircuitReviewRules = (data?: any) => get(`/circuitreview/v1/circuit-review-rule`, data).then(fnResNew); // 获取审查规则列表
export const getCircuitReviewResultDetailPage = (data?: any) => get(`/circuitreview/v1/circuit-review-result-detail/page`, data).then(fnResNew); // 分页查询电路审查结果详情
export const getCircuitReviewResultDetailList = (data?: any) => get(`/circuitreview/v1/circuit-review-result-detail`, data).then(fnResNew); // 电路审查结果详情
export const recyclePage = (data?: any) => get(`/circuitreview/v1/circuit-file/recycle/page`, data).then(fnResNew); // 分页查询回收站中的电路图文件列表
export const getCircuitReviewResultFilters= (data?: any) => get(`circuitreview/v1/circuit-review-result/${data.id}/filters`, data).then(fnResNew); // 

// 新增接口
export const getCircuitReviewResult = (id: number) => get(`/circuitreview/v1/circuit-review-result/${id}`).then(fnResNew); // 获取审查结果状态
export const getCircuitReviewRuleSummary = (id: number) => get(`/circuitreview/v1/circuit-review-result/rule-summary`, { id }).then(fnResNew); // 获取审查规则情况

// 工具文件管理
export const toolFileUploadFile = (data: any) => postFromData(`/circuitreview/v1/tool-file/upload?toolName=${data.toolName}&comments=${data.comments}`, data.file).then(fnResNew); // 上传文件
export const deleteToolFile = (data: any) => patch(`/circuitreview/v1/tool-file`, data).then(fnResNew); // 删除文件
export const getToolFile = (data: any) => get(`/circuitreview/v1/tool-file/${data.id}`,data).then(fnResNew); // 
export const getToolPages = (data: any) => get(`/circuitreview/v1/tool-file/page`,data).then(fnResNew); // 


//------ 文件版本管理 新增接口
// 创建电路图文件版本(直接上传文件)-用于给电路图文件添加一个版本，参数的fileName是电路图文件的名称
export const uploadVersionFile = (data: any) => postFromData(`/circuitreview/v1/circuit-file-version/upload?fileId=${data.fileId}&fileSecretLevelEnum=${data.fileSecretLevelEnum}`, data.file).then(fnResNew);
// 获取电路图文件所有版本-用于获取文件版本下拉框的数据，路径中的id是电路图文件的id
export const circuitFileVersion = (data: any) => get(`/circuitreview/v1/circuit-file/${data.fileId}/version`,data).then(fnResNew); 
// 查询电路审查结果-用于所选版本的审查记录下拉框的数据，路径中的id是文件版本的id
export const circuitFileResult = (data: any) => get(`/circuitreview/v1/circuit-file-version/${data.versionId}/result`,data).then(fnResNew); 
// 获取电路图文件所有版本和审查结果-用于获取查看版本弹窗的数据，路径中的id是电路图文件的id
export const circuitFileVersionResult = (data: any) => get(`/circuitreview/v1/circuit-file/${data.fileId}/version-result`,data).then(fnResNew); 
// 删除审查记录-用于删除当前的审查记录，路径中的id是需要删除的审查记录的id
export const delCircuitReviewResult = (data: any) => del(`/circuitreview/v1/circuit-review-result/${data.id}`,data).then(fnResNew); 

//  粉碎电路图文件某个版本,Body中的ID为电路图文件ID
export const circuitFileVersionFs = (data: any) => patch(`/circuitreview/v1/circuit-file-version`,data).then(fnResNew); 
//  粉碎电路图文件(粉碎所有版本的文件) ,Body中的为文件版本ID
export const circuitFileFs = (data: any) => patch(`/circuitreview/v1/circuit-file`,data).then(fnResNew); 
export const getCircuitFile = (data: any) => get(`/circuitreview/v1/circuit-file/${data.id}`,data).then(fnResNew); 
export const getCircuitFileVersion = (data: any) => get(`/circuitreview/v1/circuit-file-version/${data.id}`,data).then(fnResNew); 


// 提交审核结论
export const submitAuditResult = (detailId: number, auditType: string, issueFeedback?: string) => put(`/circuitreview/v1/circuit-review-result-detail/${detailId}/audit`, { auditType, issueFeedback }).then(fnResNew);

// 设计命名规范
export const addNamingConvention = (data: any) => post(`/circuitreview/v1/naming-convention`,data).then(fnResNew); 
export const namingConventionPages = (data: any) => get(`/circuitreview/v1/naming-convention/page`,data).then(fnResNew); 
export const namingConventionEdit = (data: any) => put(`/circuitreview/v1/naming-convention/${data.id}?id=${data.id}`,data).then(fnResNew); 
export const namingConventionDel = (data: any) => del(`/circuitreview/v1/naming-convention/${data.id}`,data).then(fnResNew); 

// 分页查询回收站中的电路图文件列表
export const circuitFileRecyclePage = (data: any) => get(`/circuitreview/v1/circuit-file/recycle/page`,data).then(fnResNew); 
export const circuitFileResultDel = (data: any) => patch(`/circuitreview/v1/circuit-file/result`,data).then(fnResNew); 

// 0是jizai，1是631
export const deploymentType = (data: any) => get(`/circuitreview/v1/circuit-review/deployment-type`,data).then(fnResNew); 

// 日志查看
export const getCircuitLog = (data: { logName: string, num: number }) => get(`/circuitreview/v1/log`, data).then(fnResNew);
export const getCircuitServiceLog = (data: { num: number }) => get(`/circuitreview/v1/log/service`, data).then(fnResNew);

// 日志下载 - 通过 axios blob 方式下载，自动携带 token
const downloadFileFromUrl = (url: string, filename: string) => {
  axios.get(url, {
    responseType: 'blob',
    headers: { Authorization: getToken('sys_tokenID') || '' },
  }).then(response => {
    const blob = new Blob([response.data]);
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
  }).catch(() => {
    ElMessage.error('下载失败');
  });
};

// 下载中间层服务日志
export const downloadCircuitServiceLog = (params: { num: number }) => {
  const url = `${VITE_APP_API_BASE_URL}/circuitreview/v1/log/service/download?num=${params.num}`;
  downloadFileFromUrl(url, '中间服务层日志.log');
};

// 下载后端日志（调试/模式匹配/审查结果）
export const downloadCircuitLog = (params: { logName: string, num: number }) => {
  const url = `${VITE_APP_API_BASE_URL}/circuitreview/v1/log/download?num=${params.num}&logName=${params.logName}`;
  downloadFileFromUrl(url, `${params.logName}.log`);
};

// 审核列表
// export const circuitReviewResultAuditPage = (data: any) => get(`/circuitreview/v1/circuit-review-result-audit/page`,data).then(fnResNew); 
export const circuitReviewResultAuditPageForExpert = (data: any) => get(`/circuitreview/v1/circuit-review-result-audit/expert/page`,data).then(fnResNew); 
export const circuitReviewResultAuditPageForAdmin = (data: any) => get(`/circuitreview/v1/circuit-review-result-audit/admin/page`,data).then(fnResNew); 
//export const circuitReviewResultDetailAuditPage = (data: any) => get(`/circuitreview/v1/circuit-review-result-detail-audit/page`,data).then(fnResNew); 
/**
 * 获取电路审查结果的专家审核分页数据
 * @param {any} data - 查询参数对象
 * @returns {Promise} 包含分页数据的Promise对象
 */
export const circuitReviewResultDetailAuditPageForExpert = (data: any) => get(`/circuitreview/v1/circuit-review-result-detail-audit/expert/page`,data).then(fnResNew); 
export const circuitReviewResultDetailAuditPageForAdmin = (data: any) => get(`/circuitreview/v1/circuit-review-result-detail-audit/admin/page`,data).then(fnResNew); 
export const circuitReviewResultDetailAudit = (data: any) => get(`/circuitreview/v1/circuit-review-result-detail-audit`,data).then(fnResNew); 
export const circuitReviewResultEditAudit = (data: any) => {
  let url = `/circuitreview/v1/circuit-review-result-detail-audit/${data.id}`;
  // if (data.rejectReason !== undefined) {
  //   data.rejectReason = `${encodeURIComponent(data.rejectReason)}`;
  // }
  return put(url, data).then(fnResNew);
}; 

// 以下为图片中展示的接口定义（经验分享管理 & 更新说明管理）
// 说明：保持原有代码不变，参数均使用 any 类型，全部追加在文件末尾

// 设计经验分享管理
export const getExperienceShare = (id: any) => get(`/circuitreview/v1/experience-shares/${id}`).then(fnResNew); // 查看设计经验分享详情
export const updateExperienceShare = (id: any, data: any) => put(`/circuitreview/v1/experience-shares/${id}`, data).then(fnResNew); // 修改设计经验分享
export const createExperienceShare = (data: any) => post(`/circuitreview/v1/experience-shares`, data).then(fnResNew); // 创建设计经验分享
export const deleteExperienceShare = (data: any) => patch(`/circuitreview/v1/experience-shares`, data).then(fnResNew); // 删除设计经验分享（使用 body 形式）
export const likeExperienceShare = (id: any) => post(`/circuitreview/v1/experience-shares/${id}/like?id=${id}`, {}).then(fnResNew); // 点赞/取消点赞
export const getExperienceSharePage = (data?: any) => get(`/circuitreview/v1/experience-shares/page`, data).then(fnResNew); // 分页查询设计经验分享

// 更新说明管理
export const getUpdateNote = (id: any) => get(`/circuitreview/v1/update-note/${id}`).then(fnResNew); // 获取更新说明
export const updateUpdateNote = (id: any, data: any) => put(`/circuitreview/v1/update-note/${id}`, data).then(fnResNew); // 更新更新说明
export const delUpdateNote = (id: any) => del(`/circuitreview/v1/update-note/${id}`,{}).then(fnResNew); // 删除更新说明
export const createUpdateNote = (data: any) => post(`/circuitreview/v1/update-note`, data).then(fnResNew); // 创建更新说明
export const getUpdateNotePage = (data?: any) => get(`/circuitreview/v1/update-note/page`, data).then(fnResNew); // 分页查询更新说明
