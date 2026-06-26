export interface ApiResult<T = unknown> {
  succ?: boolean;
  code?: number | string;
  msg?: string;
  content?: T;
  options?: Record<string, any>;
  total?: number;
  data?: T;
}

export interface UserInfo {
  id?: number | string;
  name?: string;
  loginName?: string;
  username?: string;
  profile?: string;
  role?: {
    id?: number | string;
    name?: string;
  };
  [key: string]: any;
}

export interface ApplicationItem {
  id?: number | string;
  name: string;
  module?: string;
  url?: string;
  icon?: string;
  status?: number | string | boolean;
  remark?: string;
  [key: string]: any;
}

export type ApplicationGroups = Record<string, ApplicationItem[]>;

export interface ReviewModuleConfig {
  key: 'circuit' | 'code' | 'logic';
  title: string;
  shortTitle: string;
  basePath: string;
  homePath: string;
  uploadPath: string;
  listPath: string;
  detailPath: string;
  statisticsPath: string;
  toolPath: string;
  resultPath: string;
  rulePath: string;
  feedbackPath: string;
  logPath: string;
  homeApi: string;
  apiPrefix: string;
  banner: string;
  logo: string;
}

export interface PageResult<T = unknown> {
  records?: T[];
  list?: T[];
  rows?: T[];
  content?: T[];
  total?: number;
  totalElements?: number;
  [key: string]: any;
}
