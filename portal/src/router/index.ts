/*
 * @Description:
 * @Author: hui
 * @Date: 2022-02-18 11:49:27
 * @LastEditTime: 2024-07-19 14:00:46
 */
import { createRouter, RouteRecordRaw, createWebHashHistory, createWebHistory } from 'vue-router';

const Home = () => import(`@/views/portal/HomePage.vue`);
const layoutPortal = () => import(`@/views/portal/layoutPortal.vue`);
const userList = () => import(`@/views/portal/sys/user/userList.vue`);
const userDetail = () => import(`@/views/portal/sys/user/userDetail.vue`);
const userAddEdit = () => import(`@/views/portal/sys/user/userAddEdit.vue`);
const menu = () => import(`@/views/portal/sys/menu/menuList.vue`);
const menuEdit = () => import(`@/views/portal/sys/menu/menuEdit.vue`);
const depList = () => import(`@/views/portal/sys/department/depList.vue`);
const depAddEdit = () => import(`@/views/portal/sys/department/depAddEdit.vue`);
const depDetail = () => import(`@/views/portal/sys/department/depDetail.vue`);
const appDetail = () => import(`@/views/portal/appDetail/index.vue`);
const Layout = () => import(`@/layout/layout.vue`);

const applicationManage = () => import(`@/views/portal/applicationManage/index.vue`);
const informationManage = () => import(`@/views/portal/informationManage/index.vue`);
const applicationList = () => import(`@/views/portal/informationManage/applicationList.vue`);
const applicationDetail = () => import(`@/views/portal/informationManage/applicationDetail.vue`);

// protal 页面
const portalRouter = [
	{
		'path': `/`,
		'name': `layoutPortal`,
		'component': layoutPortal,
		redirect: `/home`,
		meta:{
			hidden:true
		},
		children:[
			{
				'path': `/home`,
				'name': `home`,
				'component': Home,
				meta:{
					title:`首页`
				}
			},
			{
				'path': `/userList`,
				'name': `userList`,
				'component': userList,
				meta:{
					title:`用户管理`
				}
			},
			{
				'path': `/userAddEdit`,
				'name': `userAddEdit`,
				'component': userAddEdit,
				meta:{
					title:`用户新增`
				}
			},
			{
				'path': `/userDetail`,
				'name': `userDetail`,
				'component': userDetail,
				meta:{
					title:`用户详情`
				}
			},
			{
				'path': `/depList`,
				'name': `depList`,
				'component': depList,
				meta:{
					title:`组织管理`
				}
			},
			{
				'path': `/depAddEdit`,
				'name': `depAddEdit`,
				'component': depAddEdit,
				meta:{
					title:`组织管理`
				}
			},
			{
				'path': `/depDetail`,
				'name': `depDetail`,
				'component': depDetail,
				meta:{
					title:`组织管理`
				}
			},
			{
				'path': `/applicationManage`,
				'name': `applicationManage`,
				'component': applicationManage,
				meta:{
					title:`应用管理`
				}
			},
			{
				'path': `/informationManage`,
				'name': `informationManage`,
				'component': informationManage,
				meta:{
					title:`资讯管理`
				}
			},
			{
				'path': `/applicationList`,
				'name': `applicationList`,
				'component': applicationList,
				meta:{
					title:`资讯列表`
				}
			},
			{
				'path': `/applicationDetail`,
				'name': `applicationDetail`,
				'component': applicationDetail,
				meta:{
					title:`资讯详情`
				}
			},
			{
				'path': `/menu`,
				'name': `menu`,
				'component': menu,
				meta:{
					title:`菜单`
				}
			},
			{
				'path': `/menuEdit`,
				'name': `menuEdit`,
				'component': menuEdit,
				meta:{
					title:`菜单编辑`
				}
			},
			{
				'path': `/appDetail`,
				'name': `appDetail`,
				'component': appDetail,
				meta:{
					title:`应用详情`
				}
			},
		]
	}
];

// 电路审查
export const circuitReview = [
	{
		'path': `/warp`,
		'name': `warp`,
		'component': Layout,
		redirect: `/circuitReviewHome`,
		meta:{
			hidden:true
		},
		children:[
			{
				'path': `/circuitReviewHome`,
				'name': `circuitReviewHome`,
				'component': () => import(`@/views/circuitReview/home/home.vue`),
				meta:{
					title:`首页`
				}
			},
			{
				'path': `/documentReview`,
				'name': `documentReview`,
				'component': import(`@/views/circuitReview/documentReview.vue`),
				meta:{
					title:`文件审查`
				}
			},
			{
				'path': `/reviewList`,
				'name': `reviewList`,
				'component': import(`@/views/circuitReview/reviewManage/reviewList.vue`),
				meta:{
					title:`问题复核`
				}
			},
			{
				'path': `/reviewDetail`,
				'name': `reviewDetail`,
				'component': import(`@/views/circuitReview/reviewManage/reviewDetail.vue`),
				meta:{
					title:`复核详情`
				}
			},
			{
				'path': `/statisticalAnalysis`,
				'name': `statisticalAnalysis`,'component': () => import(`@/views/circuitReview/statistics/Statistics.vue`),
				meta:{
					title:`统计分析`
				}
			},
			// {
			// 	'path': `/fileRecycleBin`,
			// 	'name': `fileRecycleBin`,'component': () => import(`@/views/circuitReview/fileRecycleBin.vue`),
			// 	meta:{
			// 		title:`网表文件回收站`
			// 	}
			// },
			{
				'path': `/toolDownload`,
				'name': `toolDownload`,'component': () => import(`@/views/circuitReview/toolDownload.vue`),
				meta:{
					title:`工具下载`
				}
			},
			{
				'path': `/reviewResults`,
				'name': `reviewResults`,
				'component': import(`@/views/circuitReview/reviewResults.vue`),
				meta:{
					title:`审查结果`
				}
			},
			{
				'path': `/ruleList`,
				'name': `ruleList`,
				'component': import(`@/views/circuitReview/ruleList.vue`),
				meta:{
					title:`规则列表`
				}
			},
			{
				'path': `/feedback`,
				'name': `feedback`,
				'component': () => import(`@/views/circuitReview/feedback/Feedback.vue`),
				meta:{
					title:`意见反馈`
				}
			},
			{
				'path': `/designNamingConvention`,
				'name': `designNamingConvention`,
				'component': () => import(`@/views/circuitReview/designNamingConvention.vue`),
				meta:{
					title:`设计命名规范`
				}
			},
			{

				'path': `/viewLog`,
				'name': `viewLog`,
				'component': () => import(`@/views/circuitReview/viewLog/ViewLog.vue`),
				meta:{
					title:`日志查看`
				}
			},
			{
				'path': `/designExperience`,
				'name': `designExperience`,
				'component': () => import(`@/views/circuitReview/designExperience/designExperience.vue`),
				meta:{
					title:`设计经验分享`
				}
			},
			{
				'path': `/updateInstructions`,
				'name': `updateInstructions`,
				'component': () => import(`@/views/circuitReview/updateInstructions.vue`),
				meta:{
					title:`更新说明`
				}
			}

		]
	},
	{
		'path': `/sys`,
		'name': `sys`,
		'component': Layout,
		redirect: `/role`,
		children:[
			{
				'path': `/role`,
				'name': `role`,
				'component': import(`@/views/circuitReview/sys/role/roleList.vue`),
				meta:{
					title:`角色`
				}
			},
			{
				'path': `/roleAddEdit`,
				'name': `roleAddEdit`,
				'component': import(`@/views/circuitReview/sys/role/roleAddEdit.vue`),
				meta:{
					title:`角色编辑`
				}
			},
			{
				'path': `/roleDetail`,
				'name': `roleDetail`,
				'component': import(`@/views/circuitReview/sys/role/roleDetail.vue`),
				meta:{
					title:`角色详情`
				}
			},
			{
				'path': `/roleUser`,
				'name': `roleUser`,
				'component': import(`@/views/circuitReview/sys/role/roleUser.vue`),
				meta:{
					title:`角色人员`
				}
			},
		]
	}
];

// 软件代码智能审查
export const codeReview = [
	{
		'path': `/codeWarp`,
		'name': `codeWarp`,
		'component': () => import(`@/layout/codeReviewLayout.vue`),
		redirect: `/codeReviewHome`,
		meta:{
			hidden:true
		},
		children:[
			{
				'path': `/codeReviewHome`,
				'name': `codeReviewHome`,
				'component': () => import(`@/views/codeReview/home/home.vue`),
				meta:{
					title:`软件代码智能审查首页`
				}
			},
			{
				'path': `/sourceCodeDocumentReview`,
				'name': `sourceCodeDocumentReview`,
				'component': () => import(`@/views/codeReview/sourceCodeDocumentReview.vue`),
				meta:{
					title:`代码文档审查`
				}
			},
			{
				'path': `/CodeDisplayDialogTest`,
				'name': `CodeDisplayDialogTest`,
				'component': () => import(`@/views/codeReview/home/test/CodeDisplayDialogTest.vue`),
				meta:{
					title:`代码文档审查测试`
				}
			},
			{
				'path': `/sourceCodeStatistics`,
				'name': `sourceCodeStatistics`,
				'component': () => import(`@/views/codeReview/statistics/Statistics.vue`),
				meta:{
					title:`软件代码智能审查统计`
				}
			},
			// {
			// 	'path': `/sourceCodeFileRecycleBin`,
			// 	'name': `sourceCodeFileRecycleBin`,
			// 	'component': () => import(`@/views/codeReview/sourceCodeFileRecycleBin.vue`),
			// 	meta:{
			// 		title:`代码文件回收站`
			// 	}
			// },
			{
				'path': `/sourceCodeToolDownload`,
				'name': `sourceCodeToolDownload`,
				'component': () => import(`@/views/codeReview/sourceCodeToolDownload.vue`),
				meta:{
					title:`软件代码智能审查工具下载`
				}
			},
			{
				'path': `/sourceCodeReviewResults`,
				'name': `sourceCodeReviewResults`,
				'component': () => import(`@/views/codeReview/sourceCodeReviewResults.vue`),
				meta:{
					title:`软件代码智能审查结果`
				}
			},
			{
				'path': `/sourceCodeRuleList`,
				'name': `sourceCodeRuleList`,
				'component': () => import(`@/views/codeReview/sourceCodeRuleList.vue`),
				meta:{
					title:`软件代码智能审查规则列表`
				}
			},
			{
				'path': `/sourceCodeFeedback`,
				'name': `sourceCodeFeedback`,
				'component': () => import(`@/views/codeReview/feedback/Feedback.vue`),
				meta:{
					title:`意见反馈`
				}
			},
			{
				'path': `/sourceCodeViewLog`,
				'name': `sourceCodeViewLog`,
				'component': () => import(`@/views/codeReview/viewLog/ViewLog.vue`),
				meta:{
					title:`日志查看`
				}
			},

			{
				'path': `/sourceCodeReviewList`,
				'name': `sourceCodeReviewList`,
				'component': import(`@/views/codeReview/reviewManage/reviewList.vue`),
				meta:{
					title:`问题复核`
				}
			},
			{
				'path': `/sourceCodeReviewDetail`,
				'name': `sourceCodeReviewDetail`,
				'component': import(`@/views/codeReview/reviewManage/reviewDetail.vue`),
				meta:{
					title:`复核详情`
				}
			},
		]
	},
	{
		'path': `/codeSys`,
		'name': `codeSys`,
		'component': () => import(`@/layout/codeReviewLayout.vue`),
		redirect: `/codeRole`,
		children:[
			{
				'path': `/codeRole`,
				'name': `codeRole`,
				'component': () => import(`@/views/codeReview/sys/role/roleList.vue`),
				meta:{
					title:`软件代码智能审查角色`
				}
			},
			{
				'path': `/codeRoleAddEdit`,
				'name': `codeRoleAddEdit`,
				'component': () => import(`@/views/codeReview/sys/role/roleAddEdit.vue`),
				meta:{
					title:`软件代码智能审查角色编辑`
				}
			},
			{
				'path': `/codeRoleDetail`,
				'name': `codeRoleDetail`,
				'component': () => import(`@/views/codeReview/sys/role/roleDetail.vue`),
				meta:{
					title:`软件代码智能审查角色详情`
				}
			},
			{
				'path': `/codeRoleUser`,
				'name': `codeRoleUser`,
				'component': () => import(`@/views/codeReview/sys/role/roleUser.vue`),
				meta:{
					title:`软件代码智能审查角色人员`
				}
			},
		]
	}
];

// 硬件逻辑智能审查
export const logicReview = [
	{
		'path': `/logicWarp`,
		'name': `logicWarp`,
		'component': () => import(`@/layout/logicReviewLayout.vue`),
		redirect: `/logicReviewHome`,
		meta:{
			hidden:true
		},
		children:[
			{
				'path': `/logicReviewHome`,
				'name': `logicReviewHome`,
				'component': () => import(`@/views/logicReview/home/home.vue`),
				meta:{
					title:`硬件逻辑智能审查首页`
				}
			},
			{
				'path': `/logicDocumentReview`,
				'name': `logicDocumentReview`,
				'component': () => import(`@/views/logicReview/logicDocumentReview.vue`),
				meta:{
					title:`逻辑文档审查`
				}
			},
			{
				'path': `/logicStatistics`,
				'name': `logicStatistics`,
				'component': () => import(`@/views/logicReview/statistics/Statistics.vue`),
				meta:{
					title:`硬件逻辑智能审查统计`
				}
			},
			{
				'path': `/logicToolDownload`,
				'name': `logicToolDownload`,
				'component': () => import(`@/views/logicReview/logicToolDownload.vue`),
				meta:{
					title:`硬件逻辑智能审查工具下载`
				}
			},
			{
				'path': `/logicReviewResults`,
				'name': `logicReviewResults`,
				'component': () => import(`@/views/logicReview/logicReviewResults.vue`),
				meta:{
					title:`硬件逻辑智能审查结果`
				}
			},
			{
				'path': `/logicRuleList`,
				'name': `logicRuleList`,
				'component': () => import(`@/views/logicReview/logicRuleList.vue`),
				meta:{
					title:`硬件逻辑智能审查规则列表`
				}
			},
				{
				'path': `/logicFeedback`,
				'name': `logicFeedback`,
				'component': () => import(`@/views/logicReview/feedback/Feedback.vue`),
				meta:{
					title:`意见反馈`
				}
			},
			{
				'path': `/logicViewLog`,
				'name': `logicViewLog`,
				'component': () => import(`@/views/logicReview/viewLog/ViewLog.vue`),
				meta:{
					title:`日志查看`
				}
			},

			{
				'path': `/logicReviewList`,
				'name': `logicReviewList`,
				'component': import(`@/views/logicReview/reviewManage/reviewList.vue`),
				meta:{
					title:`问题复核`
				}
			},
			{
				'path': `/logicReviewDetail`,
				'name': `logicReviewDetail`,
				'component': import(`@/views/logicReview/reviewManage/reviewDetail.vue`),
				meta:{
					title:`复核详情`
				}
			},
		]
	},
	{
		'path': `/logicSys`,
		'name': `logicSys`,
		'component': () => import(`@/layout/codeReviewLayout.vue`),
		redirect: `/logicRole`,
		children:[
			{
				'path': `/logicRole`,
				'name': `logicRole`,
				'component': () => import(`@/views/logicReview/sys/role/roleList.vue`),
				meta:{
					title:`硬件逻辑智能审查角色`
				}
			},
			{
				'path': `/logicRoleAddEdit`,
				'name': `logicRoleAddEdit`,
				'component': () => import(`@/views/logicReview/sys/role/roleAddEdit.vue`),
				meta:{
					title:`硬件逻辑智能审查角色编辑`
				}
			},
			{
				'path': `/logicRoleDetail`,
				'name': `logicRoleDetail`,
				'component': () => import(`@/views/logicReview/sys/role/roleDetail.vue`),
				meta:{
					title:`硬件逻辑智能审查角色详情`
				}
			},
			{
				'path': `/logicRoleUser`,
				'name': `logicRoleUser`,
				'component': () => import(`@/views/logicReview/sys/role/roleUser.vue`),
				meta:{
					title:`硬件逻辑智能审查角色人员`
				}
			},
		]
	}
];

const routes: Array<RouteRecordRaw> = [
	
	...portalRouter,
	...circuitReview,
	...codeReview,
	...logicReview
];

const router = createRouter({
	'history': createWebHashHistory(),
	routes
});

export default router;
