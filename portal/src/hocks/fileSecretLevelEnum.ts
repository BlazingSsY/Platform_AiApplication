/*
 * @Author: TIMEFORWARD\yangshenghui
 * @LastEditors: TIMEFORWARD\yangshenghui
 * @LastEditTime: 2023-12-28 15:16:29
 * @FilePath: \commodities-trading-front\src\hocks\bizStatus.ts
 * @Description: 描述
 */
import { ref } from 'vue';
const options:any = [
	{ 'name': `绝密`, 'id': `TOP_SECRET` },
	{ 'name': `机密`, 'id': `CONFIDENTIAL` },
	{ 'name': `秘密`, 'id': `SECRET` },
	{ 'name': `公开`, 'id': `PUBLIC` }
]
// 业务审核状态
const fileSecretLevelEnum = () => {
	const Opts = ref<any[]>(options);
	return Opts;
};

export const getFileSecretLevelEnumStr = (id)=>{
	if(options.find(r=>r.id===id)){
		return options.find(r=>r.id===id).name
	}else{
		return ``
	}
}
export default fileSecretLevelEnum;
