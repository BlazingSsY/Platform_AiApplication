/*
 * @Description:
 * @Author:
 * @Date: 2022-03-25 10:21:17
 * @LastEditTime: 2024-10-16 17:03:05
 */
import 'element-plus/dist/index.css';
import '@/assets/scss/element-variables.scss';
import router from './router/index.js';
import App from './App.vue';
import store from './store/index.js';
import '@/assets/scss/index.scss';
import '@/assets/icon/iconfont.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import './permission.ts'
// 注册指令
import allDirective from '@/directive/index';
// 注册全局方法
import plugins from '@/plugins/index';

const app = createApp(App);
app.use(router);
app.use(store);
app.use(plugins);
app.mount(`#app`);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}

// 注册全局指令
Object.keys(allDirective).forEach(name => {
	const obj: any = allDirective;
	app.directive(name, obj[name]);
});
