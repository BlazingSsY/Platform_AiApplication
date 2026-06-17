// 用户model
export interface User {
	createDate: string;
	createUser: string;
	email: string;
	id: string;
	locked: boolean;
	loginName: string;
	name: string;
	sex: string;
	telephone: string;
	type: number;
	updateDate: string;
	updateUser: string;
	version: number;
	[key: string]: unknown;
}

// 角色model
export interface Role {
	comments: string;
	createDate: string;
	createUser: string;
	id: string;
	isEditable: boolean;
	name: string;
	type: string;
	updateDate: string;
	updateUser: string;
	[key: string]: unknown;
}

// 用户组model
export interface UserGroup {
	comments: string;
	createDate: string;
	createUser: string;
	fid: null | string;
	id: string;
	name: string;
	sequence: number | string;
	updateDate: string;
	updateUser: string;
	[key: string]: unknown;
}

// 部门model
export interface Department {
	comments: string;
	createDate: string;
	createUser: string;
	fid: null | string;
	id: string;
	name: string;
	type: string;
	sequence: number | string;
	updateDate: string;
	updateUser: string;
	[key: string]: unknown;
}
