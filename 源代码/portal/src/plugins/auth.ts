/*
 * @Author: TIMEFORWARD\yangshenghui
 * @LastEditors: TIMEFORWARD\yangshenghui
 * @LastEditTime: 2024-01-17 11:48:45
 * @FilePath: \commodities-trading-front\src\plugins\auth.ts
 * @Description: 描述
 */
// 下载文件
export const downLoadFile = (res: any, name: string) =>
	new Promise((resolve, reject) => {
		const result = { 'succ': true, 'msg': `` };
		if (res.data.type === `application/json`) {
			const reader = new FileReader();
			reader.onload = (e: any) => {
				const message = JSON.parse(e.target.result).msg;
				result.msg = message;
				result.succ = false;
				resolve(result);
			};
			reader.readAsText(res.data);
		} else {
			const type = res.headers[`content-type`];
			const blob = new Blob([res.data], { type });
			if (`download` in document.createElement(`a`)) {
				// 非IE下载
				const elink = document.createElement(`a`);
				if (name) {
					elink.download = name;
				}
				elink.style.display = `none`;
				elink.href = window.URL.createObjectURL(blob);
				document.body.appendChild(elink);
				elink.click();
				URL.revokeObjectURL(elink.href); // 释放 URL对象
				document.body.removeChild(elink);
			} else {
				const navigatorTs: any = navigator;
				// IE10+下载
				navigatorTs.msSaveBlob(blob, name);
			}
			result.msg = `下载成功`;
			resolve(result);
		}
	});
// 字节单位转换
export const formatFileSize = (value: any) => {
	if (value === null || value === `` || Number(value) === 0) {
		return `0 Bytes`;
	}
	const unitArr = [`Bytes`, `KB`, `MB`, `GB`, `TB`, `PB`, `EB`, `ZB`, `YB`];
	let index = 0;
	const srcsize = parseFloat(value);
	index = Math.floor(Math.log(srcsize) / Math.log(1024));
	let size: any = srcsize / 1024 ** index;
	size = Number(size).toFixed(2); // 保留的小数位数
	return `${size} ${unitArr[index]}`;
};
export const parseFileSizeUnit = (unit: string) => {
	const unitArr = [`Bytes`, `KB`, `MB`, `GB`, `TB`, `PB`, `EB`, `ZB`, `YB`];
	let size = 0;
	if (unitArr.indexOf(unit) !== -1) {
		size = 1024 ** unitArr.indexOf(unit);
	}
	return size;
};
