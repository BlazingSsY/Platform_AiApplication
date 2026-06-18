/*
 * @Description:
 * @Author:
 * @Date: 2022-03-24 08:46:34
 * @LastEditTime: 2024-06-06 09:19:04
 */
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
export default defineConfig({
	'plugins': [
		vue(),
		AutoImport({
			'resolvers': [ElementPlusResolver()],
			'dts': `./auto-imports.d.ts`, // 生成 `auto-import.d.ts` 全局声明
			'eslintrc': {
				// 自动导入依赖，eslint默认会报错，在此配置
				'enabled': true, // Default `false`
				'filepath': `./.eslintrc-auto-import.json`, // Default `./.eslintrc-auto-import.json`
				'globalsPropValue': true // Default `true`, (true | false | 'readonly' | 'readable' | 'writable' | 'writeable')
			},
			'imports': [`vue`, `vue-router`]
		}),
		Components({
			'dirs': [`src/components/`],
			'resolvers': [ElementPlusResolver()],
			'dts': `./components.d.ts`
		})
	],
	'resolve': {
		'alias': {
			'@': resolve(`./src`),
			'@element-plus/es/components/tabs/src/tab-nav.mjs': resolve(`./src/utils/tab-nav.mjs`)
		}
	},
	'css': {
		'preprocessorOptions': {
			'scss': {
				'additionalData': `@import "@/assets/scss/mixin.scss";`
			}
		}
	},
	'base': `./`,
	'build': {
		'sourcemap': false, // 是否生成 source map 文件
		'chunkSizeWarningLimit': 800, // chunk 大小警告的限制（以 kbs 为单位）。
		'rollupOptions': {
			'output': {
				manualChunks(id) {
					if (id.includes(`node_modules`)) {
						return id.toString().split(`node_modules/`)[1].split(`/`)[0].toString();
					}
				}
			}
		}
	},
	'define': {
		// enable hydration mismatch details in production build
		'__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': `true`
	},
	'server': {
		'host': `0.0.0.0`,
		'open': false,
		'port': 3008,
		proxy: {
		'/circuitreview':'http://api.daip.starmol.com',
		'/sourcecodereview': 'https://daip-portal.starmol.com',
		'/portal': 'https://daip-portal.starmol.com',
		'/logicreview':'https://daip-portal.starmol.com',
		}
		// 'proxy': {
		// 	// 选项写法
		// 	'/circuitreview': {
		// 		'target': `http://api.daip.starmol.com`,
		// 		'changeOrigin': true,
		// 		'secure': false
		// 		// 'headers': {
		// 		// 	// 'Referer': `https://manage.pl.inhoo.net.cn`
		// 		// 	// 'Referer': `https://manage.aitraining.starmol.com`
		// 		// }
		// 	}
		// }
	}
});
