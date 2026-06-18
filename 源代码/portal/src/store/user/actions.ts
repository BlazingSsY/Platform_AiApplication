import { usersLogin, getUserLoginInfo } from '@/ajax/index';
import { response, prowerType } from '@/types/response';
import { setToken, removeToken } from '@/utils/auth';
import { getPermissionList } from '@/utils/getRouterPerms';
import menu from '../../utils/menu';
export default {
	async usersLoginAction(context: any, data: any) {
		return new Promise(async(resolve, reject) => {
			await usersLogin(data)
				.then(async (res: response) => {
					if (res.succ) {
						context.commit(`SET_USER_INFO`, res.content);
						context.commit(`SET_TOKEN_INFO`, res.options);
						setToken(`sys_tokenID`, res.options.token);
						setToken(`sys_tokenID_ref`, res.options.refreshToken);
					}
					setTimeout(()=>{
						resolve(res);
					},100)
				})
				.catch(err => {
					reject(err);
				});
		});
	},
	async getUserInfoAction(context: any, data: any) {
		return new Promise((resolve, reject) => {
			getUserLoginInfo(data)
				.then((res: response) => {
					if (res.succ) {
						const userInfo = JSON.parse(JSON.stringify(res.content));
						delete userInfo.powerAliasTree;
						// const { powerAliasTree } = res.content;
						// menuType  M 系统 C 路由  F 按钮
						// const powerAliasTree: Array<prowerType> = [
						// 	{
						// 		'id': `QcnBWgFOEeuG8gJCrBwAAg`,
						// 		'version': 3,
						// 		'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 		'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 		'createDate': `2020-09-28 05:48:39`,
						// 		'updateDate': `2020-09-28 06:25:14`,
						// 		'name': `系统管理`,
						// 		'alias': ``,
						// 		'menuType': `M`,
						// 		'path': `/system`,
						// 		'component': ``,
						// 		'icon': `iconxitongshezhi`,
						// 		'isFrame': false,
						// 		'visible': true,
						// 		'enabled': true,
						// 		'sequence': 1,
						// 		'childList': [
						// 			{
						// 				'id': `nbNEDwFOEeuG8gJCrBwAAg`,
						// 				'version': 2,
						// 				'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'createDate': `2020-09-28 05:51:13`,
						// 				'updateDate': `2020-09-28 06:26:16`,
						// 				'name': `用户管理`,
						// 				'alias': ``,
						// 				'menuType': `C`,
						// 				'path': `/user`,
						// 				'component': `Layout`,
						// 				'icon': `icon-yonghuguanli`,
						// 				'isFrame': false,
						// 				'visible': true,
						// 				'enabled': true,
						// 				'sequence': 1,
						// 				'fid': `QcnBWgFOEeuG8gJCrBwAAg`,
						// 				'childList': [
						// 					{
						// 						'id': `Jgbc8QFPEeuG8gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `用户列表`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/userList`,
						// 						'component': `/user/userList`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': true,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8gJCrBwAAg`,
						// 						'childList': [
						// 							{
						// 								'alias': `:user:btn:add`,
						// 								'createDate': `2021-10-08 14:20:05`,
						// 								'createUser': `4uoe9hznEey_LwJCrBYABQ`,
						// 								'enabled': true,
						// 								'fid': `s4ofkCf_EeypRAJCrBYAAw`,
						// 								'id': `xuQxwif_EeypRAJCrBYAAw`,
						// 								'isFrame': false,
						// 								'menuType': `F`,
						// 								'name': `新增-按钮`,
						// 								'sequence': 1,
						// 								'updateDate': `2021-10-08 14:20:05`,
						// 								'updateUser': `4uoe9hznEey_LwJCrBYABQ`,
						// 								'version': 0,
						// 								'visible': true
						// 							}
						// 						]
						// 					},
						// 					{
						// 						'id': `Jgbc8QFPEeuS8gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `用户编辑`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/userAddEdit`,
						// 						'component': `/user/userAddEdit`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8gJCrBwAAg`
						// 					},
						// 					{
						// 						'id': `Jgbc8QF90euS8gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `用户详情`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/userDetail`,
						// 						'component': `/user/userDetail`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8gJCrBwAAg`
						// 					}
						// 				]
						// 			},
						// 			{
						// 				'id': `nbNEDwFOEeuG8KJCrBwAAg`,
						// 				'version': 2,
						// 				'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'createDate': `2020-09-28 05:51:13`,
						// 				'updateDate': `2020-09-28 06:26:16`,
						// 				'name': `角色管理`,
						// 				'alias': ``,
						// 				'menuType': `C`,
						// 				'path': `/role`,
						// 				'component': `Layout`,
						// 				'icon': `icon-yonghuguanli`,
						// 				'isFrame': false,
						// 				'visible': true,
						// 				'enabled': true,
						// 				'sequence': 1,
						// 				'fid': `QcnBWgFOEeuG8gJCrBwAAg`,
						// 				'childList': [
						// 					{
						// 						'id': `Jgbc8QFPEeuG8gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `角色列表`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/roleList`,
						// 						'component': `/role/roleList`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': true,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8KJCrBwAAg`
						// 					},
						// 					{
						// 						'id': `Jgbc8QFPEeu45gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `角色编辑`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/roleAddEdit`,
						// 						'component': `/role/roleAddEdit`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8KJCrBwAAg`
						// 					},
						// 					{
						// 						'id': `Jgbc8QF22PEeu45gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `角色详情`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/roleDetail`,
						// 						'component': `/role/roleDetail`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8KJCrBwAAg`
						// 					},
						// 					{
						// 						'id': `Jgbc8QF2222PEeu45gJCrBwAAg`,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`,
						// 						'name': `角色人员`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/roleUser`,
						// 						'component': `/role/roleUser`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'fid': `nbNEDwFOEeuG8KJCrBwAAg`
						// 					}
						// 				]
						// 			},
						// 			{
						// 				'id': `nbNEDwFOEeuG8K89rGHAAg`,
						// 				'fid': `QcnBWgFOEeuG8gJCrBwAAg`,
						// 				'name': `日志管理`,
						// 				'alias': ``,
						// 				'menuType': `C`,
						// 				'path': `/log`,
						// 				'component': `Layout`,
						// 				'icon': `icon-yonghuguanli`,
						// 				'isFrame': false,
						// 				'visible': true,
						// 				'enabled': true,
						// 				'sequence': 1,
						// 				'version': 2,
						// 				'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'createDate': `2020-09-28 05:51:13`,
						// 				'updateDate': `2020-09-28 06:26:16`,
						// 				'childList': [
						// 					{
						// 						'id': `JgbcGU7PEeuG8gJ19BwAAg`,
						// 						'fid': `nbNEDwFOEeuG8K89rGHAAg`,
						// 						'name': `日志列表`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/logList`,
						// 						'component': `/log/logList`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': true,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					},
						// 					{
						// 						'id': `JgbcGU7PEDRG8gJ19BwAAg`,
						// 						'fid': `nbNEDwFOEeuG8K89rGHAAg`,
						// 						'name': `日志详情`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/logDetail`,
						// 						'component': `/log/logDetail`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					}
						// 				]
						// 			},
						// 			{
						// 				'id': `nbNEDwFOEGYG8K89rGHAAg`,
						// 				'fid': `QcnBWgFOEeuG8gJCrBwAAg`,
						// 				'name': `部门管理`,
						// 				'alias': ``,
						// 				'menuType': `C`,
						// 				'path': `/department`,
						// 				'component': `Layout`,
						// 				'icon': `icon-yonghuguanli`,
						// 				'isFrame': false,
						// 				'visible': true,
						// 				'enabled': true,
						// 				'sequence': 1,
						// 				'version': 2,
						// 				'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'createDate': `2020-09-28 05:51:13`,
						// 				'updateDate': `2020-09-28 06:26:16`,
						// 				'childList': [
						// 					{
						// 						'id': `JgbcGU7PE78G8gJ19BwAAg`,
						// 						'fid': `nbNEDwFOEeuG8K89rGHAAg`,
						// 						'name': `部门列表`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/depList`,
						// 						'component': `/department/depList`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': true,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					},
						// 					{
						// 						'id': `JgbcGU7PEeuG8gJ19BwAAg`,
						// 						'fid': `nbNEDwFOEeuG8K89rGHAAg`,
						// 						'name': `部门编辑`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/depAddEdit`,
						// 						'component': `/department/depAddEdit`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					},
						// 					{
						// 						'id': `JgbcGU722PEeuG8gJ19BwAAg`,
						// 						'fid': `nbNEDwFOEeuG8K89rGHAAg`,
						// 						'name': `部门详情`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/depDetail`,
						// 						'component': `/department/depDetail`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					}
						// 				]
						// 			},
						// 			{
						// 				'id': `nbNEDwFUIGYG8K89rGHAAg`,
						// 				'fid': `QcnBWgFOEeuG8gJCrBwAAg`,
						// 				'name': `用户组管理`,
						// 				'alias': ``,
						// 				'menuType': `C`,
						// 				'path': `/group`,
						// 				'component': `Layout`,
						// 				'icon': `icon-yonghuguanli`,
						// 				'isFrame': false,
						// 				'visible': true,
						// 				'enabled': true,
						// 				'sequence': 1,
						// 				'version': 2,
						// 				'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 				'createDate': `2020-09-28 05:51:13`,
						// 				'updateDate': `2020-09-28 06:26:16`,
						// 				'childList': [
						// 					{
						// 						'id': `JgbcGU7PE78G8g779BwAAg`,
						// 						'fid': `nbNEDwFUIGYG8K89rGHAAg`,
						// 						'name': `用户组列表`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/groupList`,
						// 						'component': `/groupManage/groupList`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': true,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					},
						// 					{
						// 						'id': `Jgb88U7PE78G8g779BwAAg`,
						// 						'fid': `nbNEDwFUIGYG8K89rGHAAg`,
						// 						'name': `用户组编辑`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/groupAddEdit`,
						// 						'component': `/groupManage/groupAddEdit`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					},
						// 					{
						// 						'id': `Jgb88U7PE7228G8g779BwAAg`,
						// 						'fid': `nbNEDwFUIGYG8K89rGHAAg`,
						// 						'name': `用户组详情`,
						// 						'alias': ``,
						// 						'menuType': `C`,
						// 						'path': `/groupDetail`,
						// 						'component': `/groupManage/groupDetail`,
						// 						'icon': ``,
						// 						'isFrame': false,
						// 						'visible': false,
						// 						'enabled': true,
						// 						'sequence': 1,
						// 						'version': 1,
						// 						'createUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'updateUser': `GLNY-t6ZEemnogBQVsAACA`,
						// 						'createDate': `2020-09-28 05:55:02`,
						// 						'updateDate': `2020-09-28 06:26:27`
						// 					}
						// 				]
						// 			}
						// 		]
						// 	}
						// ];
						// 判断开发环境并且是管理员的时候加入菜单管理
						// if (import.meta.env.DEV && userInfo.type === 3) {
						// 	for (const i of powerAliasTree) {
						// 		if (i.name === `系统管理` && i.childList) {
						// 			i.childList.push(menu);
						// 		}
						// 	}
						// }
						// // 判断刷新页面 记录系统
						// let sysId: string | null = ``;
						// if (window.sessionStorage.getItem(`sysId`)) {
						// 	sysId = window.sessionStorage.getItem(`sysId`);
						// } else {
						// 	sysId = powerAliasTree.length ? powerAliasTree[0].id : ``;
						// }
						// const { permsArr, routerList } = getPermissionList(powerAliasTree);
						// context.commit(`SET_SYS_PATH`, sysId);
						// context.commit(`SET_PROWER_ALIAS_TREE`, powerAliasTree);
						context.commit(`SET_USER_INFO`, userInfo);
						// context.commit(`SET_ROTURE_LIST`, routerList);
						// context.commit(`SET_PROWER_LIST`, permsArr);
					}
					resolve(res);
				})
				.catch(err => {
					reject(err);
				});
		});
	},
	logout(context: any) {
		removeToken(`sys_tokenID`);
		removeToken(`sys_tokenID_ref`);
		context.commit(`SET_USER_INFO`, null);
		context.commit(`SET_TOKEN_INFO`, { 'token': ``, 'refreshToken': `` });
	}
};
