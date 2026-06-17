/*
 * @Description:
 * @Author:
 * @Date: 2022-03-31 11:12:54
 * @LastEditTime: 2022-04-01 09:15:31
 */

interface infoConfigItem {
	title: string;
	propsConfig: propsConfig[];
}
interface propsConfig {
	label: string;
	prop: string;
	type?: string;
	slot?: string;
	cbHidden?: any;
	contentCallback?: any;
	[key: string]: unknown;
}

interface configItem {
	title?: string;
	slot?: string;
	propsConfig: propsConfig[][];
}

export { infoConfigItem, configItem };
