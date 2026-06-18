export default {
	userId(state: any) {
		return state.userInfo && state.userInfo.id ? state.userInfo.id : ``;
	},
	userName(state: any) {
		return state.userInfo && state.userInfo.loginName ? state.userInfo.loginName : ``;
	},
	userType(state: any) {
		return state.userInfo && state.userInfo.type ? state.userInfo.type : ``;
	},
	powerList(state: any) {
		return state.powerList ? state.powerList : null;
	},
	agencyId(state: any) {
		return state.userInfo && state.userInfo.agencyId ? state.userInfo.agencyId : null;
	},
	agencyCode(state: any) {
		return state.userInfo && state.userInfo.agencyCode ? state.userInfo.agencyCode : null;
	},
	agencyName(state: any) {
		return state.userInfo && state.userInfo.agencyName ? state.userInfo.agencyName : ``;
	}
};
