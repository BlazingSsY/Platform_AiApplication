import Cookies from 'js-cookie';
import { create } from 'zustand';
import type { UserInfo } from '@/types';

const TOKEN_KEY = 'sys_tokenID';
const REFRESH_TOKEN_KEY = 'sys_tokenID_ref';

interface AuthState {
  token: string;
  refreshToken: string;
  userInfo: UserInfo | null;
  loginOpen: boolean;
  loginRedirect: string;
  setTokens: (token?: string, refreshToken?: string) => void;
  setUserInfo: (userInfo: UserInfo | null) => void;
  openLogin: (redirect?: string) => void;
  closeLogin: () => void;
  logout: () => void;
}

export const getToken = () => Cookies.get(TOKEN_KEY) || '';

export const useAuthStore = create<AuthState>((set) => ({
  token: Cookies.get(TOKEN_KEY) || '',
  refreshToken: Cookies.get(REFRESH_TOKEN_KEY) || '',
  userInfo: null,
  loginOpen: false,
  loginRedirect: '',
  setTokens: (token = '', refreshToken = '') => {
    if (token) {
      Cookies.set(TOKEN_KEY, token);
    }
    if (refreshToken) {
      Cookies.set(REFRESH_TOKEN_KEY, refreshToken);
    }
    set({ token, refreshToken });
  },
  setUserInfo: (userInfo) => set({ userInfo }),
  openLogin: (redirect = '') => set({ loginOpen: true, loginRedirect: redirect }),
  closeLogin: () => set({ loginOpen: false }),
  logout: () => {
    Cookies.remove(TOKEN_KEY);
    Cookies.remove(REFRESH_TOKEN_KEY);
    set({
      token: '',
      refreshToken: '',
      userInfo: null,
      loginOpen: false,
      loginRedirect: '',
    });
  },
}));
