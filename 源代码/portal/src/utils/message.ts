export const message = (message: string, type?: boolean | string = `success`) => {
	ElMessage.closeAll();
	ElMessage({ message, 'type': typeof type == `string` ? type : type ? `success` : `error` });
};
