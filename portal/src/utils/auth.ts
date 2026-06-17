/*
 * @Description:
 * @Author: hui
 * @Date: 2021-03-04 10:10:13
 * @LastEditTime: 2022-02-22 09:36:22
 */
import Cookies from 'js-cookie';

export function getToken(token: string) {
	return Cookies.get(token);
}

export function setToken(name: string, token: string) {
	return Cookies.set(name, token);
}

export function removeToken(name: string) {
	return Cookies.remove(name);
}
