/*
 * @Author: TIMEFORWARD\yangshenghui
 * @LastEditors: TIMEFORWARD\yangshenghui
 * @LastEditTime: 2024-02-06 10:47:02
 * @FilePath: \commodities-trading-front\src\utils\previewFile.ts
 * @Description: 描述
 */
import { storagePreviewFile } from '@/ajax/contract';
const blobToBase64 = (blob: any) => {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.readAsBinaryString(blob); // 结果为文件的二进制串
		reader.onloadend = () => {
			resolve(reader.result);
		};
		reader.onerror = reject;
	});
};
export const previewFile = (file: string) => {
	storagePreviewFile({ 'contractFile': file }).then(async (res: any) => {
		if (res.succ) {
			window.open(res.msg);
		}
	});
};
