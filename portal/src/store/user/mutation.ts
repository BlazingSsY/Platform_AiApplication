import { SET_USER_INFO, SET_TOKEN_INFO, SET_SYS_PATH, SET_PROWER_ALIAS_TREE, SET_ROTURE_LIST, SET_PROWER_LIST } from '../mutation-types';

export default {
	[SET_USER_INFO](state: any, obj: any) {
		state.userInfo = obj;
	}, // 储存登录信息
	[SET_TOKEN_INFO](state: any, obj: any) {
		state.token = obj.token;
		state.refreshToken = obj.refreshToken;
	}, // 储存token
	[SET_SYS_PATH](state: any, id: string) {
		state.sysId = id;
	},
	[SET_PROWER_ALIAS_TREE](state: any, list: Array<any>) {
		state.powerAliasTree = list;
	}, // 储存路由列表
	[SET_ROTURE_LIST](state: any, list: Array<any>) {
		state.routerList = list;
	}, // 储存路由列表
	[SET_PROWER_LIST](state: any, list: Array<any>) {
		state.powerList = list;
	} // 储存权限列表
};
