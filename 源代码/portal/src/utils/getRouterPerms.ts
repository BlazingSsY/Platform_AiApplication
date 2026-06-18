/*
 * @Description:
 * @Author: hui
 * @Date: 2021-03-08 15:27:46
 * @LastEditTime: 2022-04-02 12:06:41
 */
import { prowerType } from '@/types/response';

const Layout = () => import(`@/layout/layout.vue`);

// const pages = import.meta.globEager(`../views/**/*.vue`);
const pages = import.meta.glob(`../views/**/*.vue`, { 'eager': true, 'import': `default` });
const loadView = (component: string) => {
	const importPage = pages[`../views${component}.vue`];
	if (!importPage) {
		throw new Error(`Unknown page ${component}. Is it located under Pages with a .vue extension?`);
	}
	return importPage;
};
export const getPermissionList = (list: prowerType[]) => {
	const routerList: prowerType[] = [];
	const permsArr: string[] = [];
	getPermsArr(list, routerList, null, permsArr);
	return {
		permsArr,
		routerList
	};
};

const getPermsArr = (list: prowerType[], routerList: prowerType[], routerObj: prowerType | null, permsArr: string[]) => {
	list.forEach((r: prowerType) => {
		// enabled:1 激活 0 禁用
		if (r.enabled && r.menuType !== `M`) {
			// 记录当前用户的所有权限标识 menuType: M 菜单(一级菜单) C 路由(二级菜单) F 按钮
			if (r.menuType !== `M` && r.alias) {
				permsArr.push(r.alias);
			}
			// vue 子路由children
			const o: prowerType = {
				'children': [],
				'name': r.name,
				'component': r.component,
				'icon': r.icon,
				'visible': r.visible,
				'menuType': r.menuType,
				'path': r.path,
				'redirect': ``,
				'enabled': false,
				'id': r.id,
				'fid': r.fid
			};
			if (r.childList && r.childList.length > 0) {
				o.redirect = r.childList[0].path;
			}
			// 判断 是子路由还是一级路由
			if (!routerObj) {
				if (o.component === `Layout`) {
					o.component = Layout;
				}
				routerList.push(o);
			} else if (r.menuType !== `F`) {
				o.component = markRaw(loadView(o.component));
				routerObj.children!.push(o);
			}
			if (r.childList && r.childList.length) {
				getPermsArr(r.childList, routerList, o, permsArr);
			}
		}
		if (r.menuType === `M`) {
			if (r.childList && r.childList.length) {
				getPermsArr(r.childList, routerList, null, permsArr);
			}
		}
	});
};
