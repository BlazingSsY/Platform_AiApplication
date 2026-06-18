import { get } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getHomeData = (data: any) => get(`/circuitreview/v1/circuit-statistics/home`, data).then(fnRes);