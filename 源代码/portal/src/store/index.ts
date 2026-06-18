/*
 * @Description:
 * @Author: hui
 * @Date: 2022-02-18 11:53:22
 * @LastEditTime: 2022-02-22 12:26:42
 */
import { createStore } from 'vuex';
import { SET_MENU_COLLAPSE ,SET_LOGIN_SHOW,SET_LOGIN_TO_PATH,SET_PAGES_INFO_STROE,CLEAR_PAGES_INFO_STROE,SET_APP_LIST_DATA,SET_SCREEN_CHANGE,SET_IS_JI_ZAI_USER,SET_CHANGE_PASSWORD_SHOW} from './mutation-types';
import user from './user/index';
// Create a new store instance.
export default createStore({
	'state': {
		'menuCollapse': 0,
		loginShow:false,
		lagonToPath:``,
		pagesInfoStroe:{},
		applicationsListData:null,
		isScreenChange:false,
		isJiZaiUser:false,
		changePasswordShow:false,  // 修改密码对话框显示状态
		changePasswordLoginName:'',  // 修改密码时的登录名（登录失败时需要传递）
	},
	'mutations': {
		[SET_IS_JI_ZAI_USER](state: any, flag: boolean) {
			state.isJiZaiUser = flag;
		}, // 储存登录信息
		[SET_MENU_COLLAPSE](state: any, type: number) {
			state.menuCollapse = type;

		}, // 储存登录信息
		[SET_LOGIN_SHOW](state: any, type: boolean) {
			state.loginShow = type;
		},
		[SET_LOGIN_TO_PATH](state: any, goPath?:string) {
			state.lagonToPath = goPath || ``
		},
		[SET_PAGES_INFO_STROE](state: any, pathInfo:any) {
			if(!state.pagesInfoStroe){
				state.pagesInfoStroe = {}
			}
			state.pagesInfoStroe[pathInfo.path] = pathInfo.info
		},
		[CLEAR_PAGES_INFO_STROE](state: any) {
			state.pagesInfoStroe = null
		},
		[SET_APP_LIST_DATA](state: any,data:any) {
			state.applicationsListData = data
		},
		[SET_SCREEN_CHANGE](state: any,data:any) {
			state.isScreenChange = data
		},
		[SET_CHANGE_PASSWORD_SHOW](state: any, show: boolean) {
			state.changePasswordShow = show;
		},
	},
	'modules': {
		user
	}
});
