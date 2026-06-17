// /*
//  * @Author: TIMEFORWARD\yangshenghui
//  * @LastEditors: TIMEFORWARD\yangshenghui
//  * @LastEditTime: 2024-01-09 14:28:41
//  * @FilePath: \commodities-trading-front\src\hocks\publicFieldTyps.ts
//  * @Description: 描述
//  */
// import { ref } from 'vue';
// import { response, CategoryTypes } from '../types/response';
// import { publicFieldList } from '../ajax';
// const publicFieldOpts = (type: string) => {
// 	const categoryOpts = ref<CategoryTypes[]>([]);
// 	// WARE_HOUSING 仓库, GOODS_TYPE 货物类型, TRANSACTIONS_WAY 承担方式, GOODS_SOURCE 资源类型,
// 	// TRADING_DELIVERY 交割方式, INVOICE_TYPE发票方式, CUSTOMER_CAPITAL_TYPE 客户类型, RATING_LEVEL 评级  CONTRACT_TEMPLATE  模板
// 	// TRADING_TYPE, TRADING_METHOD, HOOKUP_SOURCE
// 	publicFieldList(type).then((res: response) => {
// 		if (res.succ) {
// 			categoryOpts.value = res.content;
// 		}
// 	});
// 	return categoryOpts;
// };
// export default publicFieldOpts;
