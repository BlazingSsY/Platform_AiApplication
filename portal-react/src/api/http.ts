import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { message } from 'antd';
import qs from 'qs';
import { getToken, useAuthStore } from '@/store/auth';
import type { ApiResult, PageResult } from '@/types';

export const http = axios.create({
  baseURL: import.meta.env.VITE_APP_API_BASE_URL || '',
  headers: {
    'content-type': 'application/json',
  },
});

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

http.interceptors.response.use(
  (response) => {
    const contentType = String(response.headers['content-type'] || '');
    if (contentType.includes('application/octet-stream')) {
      return response;
    }
    const data = response.data as ApiResult;
    if (data && data.succ === false && data.code !== 1001) {
      message.error(data.msg || '请求失败');
    }
    return response;
  },
  (error: AxiosError<any>) => {
    const status = error.response?.status;
    const data = error.response?.data || {};
    if (status === 401) {
      useAuthStore.getState().logout();
      useAuthStore.getState().openLogin();
      message.error(data.msg || '登录信息失效');
    } else if (status) {
      message.error(data.msg || data.message || `请求失败：${status}`);
    } else {
      message.error(error.message || '网络请求异常');
    }
    return Promise.reject(error);
  },
);

function addTimestamp(url: string) {
  const joiner = url.includes('?') ? '&' : '?';
  return `${url}${joiner}tem=${Date.now()}`;
}

export async function request<T = unknown>(config: AxiosRequestConfig): Promise<ApiResult<T>> {
  const response = await http.request<ApiResult<T>>(config);
  return response.data;
}

export function get<T = unknown>(url: string, params?: Record<string, any>, config?: AxiosRequestConfig) {
  return request<T>({
    url: addTimestamp(url),
    method: 'get',
    params,
    paramsSerializer: (value) => qs.stringify(value, { arrayFormat: 'repeat' }),
    ...config,
  });
}

export function post<T = unknown>(url: string, data?: any, config?: AxiosRequestConfig) {
  return request<T>({ url, method: 'post', data, ...config });
}

export function put<T = unknown>(url: string, data?: any, config?: AxiosRequestConfig) {
  return request<T>({ url, method: 'put', data, ...config });
}

export function patch<T = unknown>(url: string, data?: any, config?: AxiosRequestConfig) {
  return request<T>({ url, method: 'patch', data, ...config });
}

export function del<T = unknown>(url: string, params?: Record<string, any>, config?: AxiosRequestConfig) {
  return request<T>({ url, method: 'delete', params, ...config });
}

export function extractRows<T = any>(result?: ApiResult<PageResult<T> | T[]>): { rows: T[]; total: number } {
  const content = result?.content;
  if (Array.isArray(content)) {
    return { rows: content, total: content.length };
  }
  const page = (content || {}) as PageResult<T>;
  const rows = page.records || page.list || page.rows || page.content || [];
  const total = page.total ?? page.totalElements ?? rows.length;
  return { rows, total };
}
