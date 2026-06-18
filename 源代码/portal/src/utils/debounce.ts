let timer: any = null;
/*
 * @callback 防抖的执行的函数
 * @callback 函数带的参数
 * @delay 毫秒数
 * */
export default function debounce(callback, paramsApplys, delay) {
	clearTimeout(timer);
	timer = setTimeout(() => {
		callback(paramsApplys);
	}, 1000);
}
