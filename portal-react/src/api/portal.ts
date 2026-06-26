import md5 from 'md5';
import { get, post, patch, put } from '@/api/http';
import type { ApplicationGroups, ApplicationItem, UserInfo } from '@/types';

export interface LoginForm {
  loginName: string;
  password: string;
  captcha?: string;
}

export function login(form: LoginForm) {
  return post('/portal/v1/users/login?captcha=', {
    ...form,
    captcha: form.captcha || '',
    password: md5(form.password),
  });
}

export function fetchLoginInfo() {
  return get<UserInfo>('/portal/v1/users/my-login-info');
}

export function fetchApplications() {
  return get<ApplicationGroups>('/portal/v1/applications');
}

export function fetchApplicationPages(params: Record<string, any>) {
  return get('/portal/v1/applications/pages', params);
}

export function createApplication(data: Partial<ApplicationItem>) {
  return post('/portal/v1/applications', data);
}

export function updateApplication(data: Partial<ApplicationItem>) {
  return put(`/portal/v1/applications/${data.id}`, data);
}

export function deleteApplications(data: ApplicationItem[]) {
  return patch('/portal/v1/applications', data);
}

export function fetchUsers(params: Record<string, any>) {
  return get('/portal/v1/users', params);
}

export function fetchDepartments(params: Record<string, any>) {
  return get('/portal/v1/departments/pages', params);
}

export function fetchRoles(params: Record<string, any>) {
  return get('/portal/v1/roles/pages', params);
}

export function fetchMenus(params: Record<string, any>) {
  return get('/portal/v1/powers/select-tree', params);
}

export function fetchInformation(params: Record<string, any>) {
  return get('/portal/v1/informations/pages', params);
}

export function fetchLogs(params: Record<string, any>) {
  return get('/portal/v1/log-records/pages', params);
}
