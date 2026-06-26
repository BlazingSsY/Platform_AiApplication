import { get, post } from '@/api/http';
import type { PageResult, ReviewModuleConfig } from '@/types';

export function fetchReviewHome(module: ReviewModuleConfig) {
  return get<Record<string, any>>(module.homeApi);
}

export function fetchReviewPage(module: ReviewModuleConfig, params: Record<string, any>) {
  return get<PageResult | any[]>(`${module.apiPrefix}/v1/review/tasks/pages`, params);
}

export function fetchRules(module: ReviewModuleConfig, params: Record<string, any>) {
  return get<PageResult | any[]>(`${module.apiPrefix}/v1/rules/pages`, params);
}

export function fetchTools(module: ReviewModuleConfig, params: Record<string, any>) {
  return get<PageResult | any[]>(`${module.apiPrefix}/v1/tools/pages`, params);
}

export function fetchLogs(module: ReviewModuleConfig, params: Record<string, any>) {
  return get<PageResult | any[]>(`${module.apiPrefix}/v1/log-records/pages`, params);
}

export function submitFeedback(module: ReviewModuleConfig, data: Record<string, any>) {
  return post(`${module.apiPrefix}/v1/feedbacks`, data);
}
