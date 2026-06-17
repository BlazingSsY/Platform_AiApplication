/**
 * 应用卡片点击的统一跳转逻辑（首页卡片 / 顶部菜单 / 侧边菜单共用）。
 *
 * 规则：
 *   1. http(s):// 开头           → window.open 新标签打开（外部应用）
 *   2. 相对路径但匹配不到站内路由 → window.open 新标签打开（经 Nginx 反代的外部应用，如 /myapp/）
 *   3. 相对路径且是站内 Vue 路由   → 已登录 router.push；未登录弹登录框
 *
 * 只处理「已上线(status===1)」应用的导航；未上线/详情跳转等各组件自行处理。
 */
import router from '@/router/index';
import store from '@/store/index';

export interface AppItem {
  status?: number | string;
  url?: string;
  router?: string;
  [key: string]: any;
}

/** 本次点击实际做了什么，方便调用方决定额外副作用（如收起菜单）。 */
export type OpenResult = 'external' | 'internal' | 'login' | 'inactive';

/** 是否外部/反代地址：http(s) 开头，或相对路径但不在已注册的站内路由里。 */
function isExternal(url: string): boolean {
  if (/^https?:\/\//.test(url)) {
    return true;
  }
  if (!url.startsWith('/')) {
    return false;
  }
  // 去掉结尾斜杠后比对已注册路由；匹配不到则视为反代外部地址
  const path = url.replace(/\/+$/, '') || '/';
  return !router.getRoutes().some(r => r.path === path);
}

export function openApplication(item: AppItem): OpenResult {
  if (!item || Number(item.status) !== 1) {
    return 'inactive';
  }
  const url = item.url || '';
  if (!url) {
    return 'inactive';
  }

  if (isExternal(url)) {
    // /myapp/ 由浏览器按当前域名解析 → 经 Nginx 反代到外部应用
    window.open(url, '_blank');
    return 'external';
  }

  // 站内 Vue 路由：需要登录
  const loginName = (store.state as any)?.user?.userInfo?.name;
  if (loginName) {
    router.push({ path: url });
    return 'internal';
  }
  store.commit('SET_LOGIN_SHOW', true);
  store.commit('SET_LOGIN_TO_PATH', item.router);
  return 'login';
}
