/*
 * @Author: TIMEFORWARD\yangshenghui
 * @LastEditors: TIMEFORWARD\yangshenghui
 * @LastEditTime: 2024-01-15 14:41:56
 * @FilePath: \commodities-trading-front\src\types\response.d.ts
 * @Description: 描述
 */
declare interface response {
	code?: number;
	msg: string;
	succ: boolean;
	content: pagesContent | userInfo | null | string | any;
	options: options;
}

interface options {
	refreshToken: string;
	token: string;
}

interface commonContent {
	createDate?: string;
	createUser?: string;
	updateDate?: string;
	updateUser?: string;
}

interface userInfo extends commonContent {
	id: string;
	locked: false;
	loginName: string;
	name: string;
	powerAliasTree: Array<prowerType>;
	status: number;
	telephone: string;
	type: number;
	version: number;
}
interface prowerType extends commonContent {
	id: string;
	name: string;
	component?: string | any;
	icon?: string;
	redirect?: string;
	isFrame?: boolean;
	visible: boolean;
	enabled: boolean;
	menuType: string;
	alias?: string;
	fid?: string;
	path?: string;
	sequence?: number;
	version?: number;
	childList?: Array<prowerType>;
	children?: Array<prowerType>;
}

interface pagesContent {
	current: number;
	hitCount?: boolean;
	orders?: Array<any>;
	pages: number;
	searchCount?: boolean;
	size: number;
	total: number;
	records: Array<records>;
}
interface records extends commonContent {
	id?: string;
	name?: string;
	version: number;
	[propName: string]: any;
}

interface CategoryTypes {
	name: string;
	id: number | string;
	type?: number;
	enabled?: boolean;
	parentId?: number | string;
	child?: any[];
}

export { response, userInfo, pagesContent, prowerType, CategoryTypes };
