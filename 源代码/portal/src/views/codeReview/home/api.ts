import { get } from '@/ajax/ajax';

const fnRes = (res: any) => (res ? JSON.parse(res.request.response) : {});

export const getHomeData = (data: any) => get(`/sourcecodereview/v1/source-code-review-statistics/home`, data).then(fnRes);
