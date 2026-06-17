import axios, { AxiosRequestConfig, AxiosResponse, AxiosError, AxiosInstance } from 'axios';
import { ElMessage } from 'element-plus'
import { getToken, removeToken, setToken } from '@/utils/auth';
import qs from 'qs';
const VITE_APP_API_BASE_URL: string = import.meta.env.VITE_APP_API_BASE_URL;
// 创建一个axios实例
// const VITE_BASE_URL: any = import.meta.env.VITE_BASE_URL;
const instance: AxiosInstance = axios.create({
	'baseURL': VITE_APP_API_BASE_URL,
	'headers': {
		'content-type': `application/json`
	}
});
// 传参序列化  增加token  暂无
instance.interceptors.request.use((config: AxiosRequestConfig) => {
	const token = getToken(`sys_tokenID`);
	if (token) {
		config.headers!.Authorization = token || ``; // 请求头加上token
	}
	return config;
});
// 返回状态判断（添加响应拦截器）
instance.interceptors.response.use(
	(res: AxiosResponse) => {
		// ElMessage.closeAll();
		if (res.headers[`content-type`] === `application/octet-stream`) {
			return Promise.resolve(res);
		}
		// code: 1001 是"请修改密码后重新登录"，由 ChangePasswordDialog 处理，拦截器不再重复弹提示
		if (res.data.succ === false && res.data.code !== 1001) {
			ElMessage.error(res.data.msg);
		}
		return Promise.resolve(res);
	},
	// eslint-disable-next-line consistent-return
	async (error: AxiosError) => {
		// ElMessage.closeAll();
		const { config } = error;
		console.log('error:', error);
		// 根据返回的状态码来获取相应的提示
		if (error.response && error.response.status) {
			switch (error.response.status) {
				case 400:
					ElMessage.error(`请求出错`);
					break;
				case 401:
					// token 过期
					ElMessage.error(`登录信息失效`);
					removeToken(`sys_tokenID`);
					removeToken(`sys_tokenID_ref`);
					import('@/store/index').then((storeModule) => {
						const store = storeModule.default;
						store.commit('SET_LOGIN_SHOW', true);
						store.commit('SET_LOGIN_TO_PATH', '');
					});
					import('@/router/index').then((routerModule) => {
						const router = routerModule.default;
						if (router.currentRoute.value.path !== '/home') {
							router.push('/home');
						}
					});
					break;
				case 403:
					ElMessage.error({
						'message': `拒绝访问`
					});
					break;
				case 404:
					ElMessage.error({
						'message': `请求错误,未找到该资源`
					});
					break;
				case 500:
					ElMessage.error({
						'message': error.response.data.message
					});
					break;
				default:
					ElMessage.error({
						'message': error.response.data.msg
					});
			}
		}
	}
);

// 返回一个Promise（发送post请求）
export function post(url: string, data: any, params?: any) {
	return new Promise((resolve, reject) => {
		instance
			.post(url, data, params)
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}
export function postFromData(url: string, params: any) {
	return new Promise((resolve, reject) => {
		instance
			.post(url, params, { 'headers': { 'Content-Type': `multipart/form-data` } })
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

export function postData(url: string, params: any) {
	return new Promise((resolve, reject) => {
		instance
			.post(url, { 'data': params })
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

// 返回一个Promise(发送get请求)
export function get(url: string, params?: any, config?: any) {
	let urlPath = url;
	const getTimestamp = new Date().getTime();
	if (urlPath.indexOf(`?`) !== -1) {
		urlPath = `${urlPath}&tem=${getTimestamp}`;
	} else {
		urlPath = `${urlPath}?tem=${getTimestamp}`;
	}
	for (const key in params) {
		if (params[key] === ``) {
			delete params[key];
		}
	}
	return new Promise((resolve, reject) => {
		instance
			.get(urlPath, {
				params,
				...config,
				'paramsSerializer': function (params) {
					return qs.stringify(params, { 'arrayFormat': `repeat` });
				}
			})
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

// 返回一个Promise（发送delete请求）
export function del(url: string, params: any) {
	return new Promise((resolve, reject) => {
		instance
			.delete(url, { params })
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

// 返回一个Promise（发送delete请求）
export function delData(url: string, params: any) {
	return new Promise((resolve, reject) => {
		instance
			.delete(url, { 'data': params })
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

// 返回一个Promise（发送delete请求）
export function delIds(url: string) {
	return new Promise((resolve, reject) => {
		instance
			.delete(url)
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

// 返回一个Promise（发送put请求）
export function put(url: string, params: any) {
	return new Promise((resolve, reject) => {
		instance
			.put(url, params)
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}
// 返回一个Promise（发送put请求）
export function put1(url: string, params: any) {
	return new Promise((resolve, reject) => {
		instance
			.put(url, null, { params })
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}

// 返回一个Promise（发送patch请求）
export function patch(url: string, params?: any) {
	return new Promise((resolve, reject) => {
		instance
			.patch(url, params)
			.then(response => {
				resolve(response);
			})
			.catch(error => {
				reject(error);
			});
	});
}
