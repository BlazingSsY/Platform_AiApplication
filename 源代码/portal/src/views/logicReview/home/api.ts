import { get } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getHomeData = (data: any) => get(`/logicreview/v1/logic-review-statistics/home`, data).then(fnRes);
