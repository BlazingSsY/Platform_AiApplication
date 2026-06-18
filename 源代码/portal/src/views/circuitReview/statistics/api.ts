import { get, post } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getStatsData = (data: any) => post(`/circuitreview/v1/circuit-statistics`, data).then(fnRes);
export const getStatsDataAffix = (data: any) => get(`/circuitreview/v1/circuit-statistics/rule-type`, data).then(fnRes);

export const getDepartmentsSimpleItems = (data: any) => get(`/portal/v1/departments/child-simple-items`, data).then(fnRes);

export const statisticsExportExcel = (data: any) => post(`/circuitreview/v1/circuit-statistics/export-excel`, data,{ 'responseType': `blob` }); 
export const statisticsExportTableExcel = (data: any) => post(`/circuitreview/v1/circuit-statistics/export-table-excel`, data,{ 'responseType': `blob` }); 
