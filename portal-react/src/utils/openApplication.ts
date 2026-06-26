import type { NavigateFunction } from 'react-router-dom';
import { useAuthStore } from '@/store/auth';
import { internalPaths } from '@/routes/reviewRoutes';
import type { ApplicationItem } from '@/types';

export type OpenResult = 'external' | 'internal' | 'login' | 'inactive';

function normalizePath(url: string) {
  return url.replace(/\/+$/, '') || '/';
}

function isInternalPath(url: string) {
  if (!url.startsWith('/')) {
    return false;
  }
  return internalPaths.has(normalizePath(url));
}

export function openApplication(item: ApplicationItem, navigate: NavigateFunction): OpenResult {
  if (!item || Number(item.status) !== 1 || !item.url) {
    return 'inactive';
  }

  const url = item.url;
  if (/^https?:\/\//i.test(url) || (url.startsWith('/') && !isInternalPath(url))) {
    window.open(url, '_blank');
    return 'external';
  }

  const userInfo = useAuthStore.getState().userInfo;
  if (userInfo?.name || userInfo?.loginName || userInfo?.username) {
    navigate(url);
    return 'internal';
  }

  useAuthStore.getState().openLogin(url);
  return 'login';
}
