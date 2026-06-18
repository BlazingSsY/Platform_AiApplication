/*
 * @Description:
 * @Author:
 * @Date: 2022-03-24 10:34:44
 * @LastEditTime: 2023-12-25 14:09:56
 */
/// <reference types="vite/client" />

// eslint-disable-next-line quotes
declare module '*.vue' {
	import type { DefineComponent } from 'vue';
	// eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
	const component: DefineComponent<{}, {}, any>;
	export default component;
}

interface ImportMetaEnv {
	readonly NODE_ENV: string;
	readonly BASE_URL: string;
	readonly DEV: boolean;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}

declare interface Window {
	OSS: any;
}
