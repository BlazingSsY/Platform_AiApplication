import { get, post } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getStatsData = (data: any) => post(`/logicreview/v1/logic-review-statistics`, data).then(fnRes);

export const getDepartmentsSimpleItems = (data: any) => get(`/portal/v1/departments/child-simple-items`, data).then(fnRes);

export const statisticsExportExcel = (data: any) => post(`/logicreview/v1/logic-review-statistics/export-excel`, data,{ 'responseType': `blob` }); 
export const statisticsExportTableExcel = (data: any) => post(`/logicreview/v1/logic-review-statistics/export-table-excel`, data,{ 'responseType': `blob` }); 
