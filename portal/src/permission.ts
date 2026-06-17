/*
 * @Description:
 * @Author:hui
 * @Date: 2022-03-24 10:34:44
 * @LastEditTime: 2022-03-31 10:57:45
 */
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { getToken } from '@/utils/auth';
import store from '@/store/index';
import router from './router';

const whiteList = [`/home`,`/appDetail`,`/applicationDetail`,`/applicationList`]; // no redirect whitelist
NProgress.configure({ 'showSpinner': false });

router.beforeEach(async (to, from, next) => {
	NProgress.start();
	const hasToken = getToken(`sys_tokenID`);
	if(hasToken){
		const userInfo: any = store.state;
		if (userInfo.user.userInfo) {
			// userInfo.user.userInfo.role.name: "普通用户"
			//用户 /userList 部门/depList 应用 /applicationManage 咨询 /informationManage
			if(userInfo.user&&userInfo.user.userInfo&&userInfo.user.userInfo.role.name==="普通用户"){
				if(to.path===`/userList`||to.path===`/depList`||to.path===`/applicationManage`||to.path===`/informationManage`){
					next(`/home`);
				}
			}
			next();
		}else{
			await store.dispatch(`user/getUserInfoAction`).then(res=>{
				next();
			}).catch((err)=>{
				console.log(err);
				NProgress.done();
				next();
			})
		}
	}else{
		// if(to.path===`/home`||to.path===`/appDetail`){
		if(whiteList.includes(to.path)){
			next();
		}else{
			next(`/home`);
		}
	}
});
router.afterEach(() => {
	NProgress.done();
});
