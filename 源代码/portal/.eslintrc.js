/* eslint-disable */
/*
 * @Description:
 * @Author: hui
 * @Date: 2022-02-17 10:18:17
 * @LastEditTime: 2022-03-28 18:24:23
 */
module.exports = {
	'env': {
		'node': true
	},
	'globals': {
		'console': `writable`
	},
	'extends': [
		`plugin:vue/vue3-recommended`,
		// `standard`,
		// `plugin:@typescript-eslint/recommended`, // 使用来自 @typescript-eslint/eslint-plugin 的推荐规则
		// `plugin:prettier/recommended`, // 解决eslint prettierrc冲突
		`./.eslintrc-auto-import.json`
	], // 位置靠后的包将覆盖前面的
	'parser': `vue-eslint-parser`, // 解析 .vue 文件
	'parserOptions': {
		'parser': `@typescript-eslint/parser`, // 解析vue script 部分
		'ecmaVersion': 2021,
		'sourceType': `module`
	},
	'plugins': [`vue`, `@typescript-eslint`],
	'rules': {
		// off 0  warn 1 error 2
		'no-console': process.env.NODE_ENV === `production` ? 2 : 0,
		'no-debugger': process.env.NODE_ENV === `production` ? 2 : 0,
		'radix': [0],
		'quotes': [0, `backtick`],
		'no-underscore-dangle': 0, // 禁止标识符中有悬空下划线
		'consistent-return': 0,
		'max-len': [1, { 'code': 400 }],
		'no-restricted-imports': 0, // 禁用特定的 import
		'no-param-reassign': 0, // 禁止对函数参数再赋值
		'no-void': 0, // 禁用void操作符
		'no-multi-spaces': 1, // 不能用多余的空格
		'no-empty-function': 0, // 不允许空函数。
		'eqeqeq': [`off`],
		'no-irregular-whitespace': 0,
		'no-unused-expressions': [
			2,
			{
				// 禁止无用的表达式
				'allowShortCircuit': true,
				'allowTernary': true
			}
		], // 禁止未使用过的表达式
		'wrap-regex': 0, // 正则表达式字面量用括号括起来
		'camelcase': 0, // 强制驼峰法命名
		'consistent-this': [0, `that`], // this别名
		'quote-props': [0, `always`],
		'import/no-unresolved': [0], // 确保导入的模块可以解析为本地文件系统上的模块
		'import/extensions': [0], // 确保在导入路径中一致地使用文件扩展名
		'no-use-before-define': 0, // 定义前不使用
		'linebreak-style': 0, // 以什么结尾 CRLF 、LF
		'lines-between-class-members': 0, // 类属性之间间隔空行
		'class-methods-use-this': 0, // 类非静态方法必须调用this
		'object-curly-newline': [0, { 'multiline': true }], // import{...} from 是否可以写成一行
		//* *****
		// vue
		//* *****
		'vue/no-multiple-template-root': 0, // 不允许向模板添加多个根节点
		'vue/no-v-html': 0, // 不允许使 v-html 来防止 XSS 攻击
		'vue/no-v-model-argument': 0, // vue3 v-modle:xxx新写法适配
		'vue/component-definition-name-casing': [2, `PascalCase` || `kebab-case`], // 强制对组件定义名称强制实施特定大小写
		'vue/first-attribute-linebreak': [
			2,
			{
				'singleline': `ignore`,
				'multiline': `ignore`
			}
		], // 实施第一个属性的位置
		'vue/html-closing-bracket-newline': [
			2,
			{
				'singleline': `never`,
				'multiline': `always`
			}
		], // 要求或不允许在标记的右括号前换行符
		'vue/html-closing-bracket-spacing': [0], // 要求或不允许在标记的右括号前加上空格
		'vue/html-indent': [
			0,
			`tab`,
			{
				'attribute': 1,
				'baseIndent': 1,
				'closeBracket': 0,
				'alignAttributesVertically': true,
				'ignores': []
			}
		], // 在 中强制实施一致的缩进
		'vue/html-quotes': [2, `double` || `single`, { 'avoidEscape': false }], // 强制实施 HTML 属性的引号样式
		'vue/html-end-tags': 2, // 强制执行结束标记样式
		'vue/one-component-per-file': 0, // 强制每个组件应位于其自己的文件中
		'vue/comment-directive': 0, // 只能在模板里面注释，不能再标签里面注释
		'vue/max-attributes-per-line': [
			2,
			{
				'singleline': {
					'max': 10
				},
				'multiline': {
					'max': 10
				}
			}
		], // 强制要求每行的最大属性数
		'vue/multiline-html-element-content-newline': [
			2,
			{
				'ignoreWhenEmpty': true,
				'ignores': [`pre`, `textarea`],
				'allowEmptyLines': false
			}
		], // 多行元素的内容前后换行符
		'vue/singleline-html-element-content-newline': [
			0,
			{
				'ignoreWhenNoAttributes': true,
				'ignoreWhenEmpty': true,
				'ignores': [`pre`, `textarea`]
			}
		], // 单行元素的内容前后换行符
		'vue/mustache-interpolation-spacing': [2, `always`], // | `never`
		'vue/no-multi-spaces': [
			2,
			{
				'ignoreProperties': false
			}
		], // 不允许多个空格
		'vue/prop-name-casing': [2, `camelCase`], // 强制执行 Prop 名称的特定大小写
		'vue/no-spaces-around-equal-signs-in-attribute': [2], // 不允许在属性中的等号周围留空格
		'vue/attribute-hyphenation': [2], // 在模板中的自定义组件上强制实施属性命名样式
		'vue/no-template-shadow': 1, // 禁止从外部作用域中声明的阴影变量中声明变量
		'vue/require-default-prop': [2], // 需要属性的默认值
		'vue/script-setup-uses-vars': 0, // 使用的变量被标记为未使用
		'vue/html-self-closing': [
			2,
			{
				'html': {
					'void': `any`,
					'normal': `always`,
					'component': `always`
				},
				'svg': `always`,
				'math': `always`
			}
		], // 强制实施自闭合样式 "always"要求在没有内容的元素上自闭合。 "never"不允许自闭合。 "any"不要强制执行自闭合样式
		'vue/require-prop-types': 0, // Require type definitions in props
		'vue/order-in-components': [
			2,
			{
				'order': [
					`el`,
					`name`,
					`key`,
					`parent`,
					`functional`,
					[`delimiters`, `comments`],
					[`components`, `directives`, `filters`],
					`extends`,
					`mixins`,
					[`provide`, `inject`],
					`layout`,
					`middleware`,
					`validate`,
					`scrollToTop`,
					`transition`,
					`loading`,
					`inheritAttrs`,
					`model`,
					[`props`, `propsData`],
					`emits`,
					`setup`,
					`asyncData`,
					`data`,
					`fetch`,
					`head`,
					`computed`,
					`watch`,
					`watchQuery`,
					`methods`,
					[`template`, `render`],
					`renderError`,
					`LIFECYCLE_HOOKS`,
					`ROUTER_GUARDS`
				]
			}
		], // 组件中属性的顺序
		'vue/require-explicit-emits': [
			2,
			{
				'allowProps': false
			}
		], // 需要名称由 触发的选项emits$emit()
		'vue/multi-word-component-names': 0, // 要求组件名称始终为多词
		'vue/require-direct-export': [
			0,
			{
				'disallowFunctionalComponentFunction': false
			}
		], // 要求直接导出组件
		'vue/v-on-event-hyphenation': [0], // 在模板中的自定义组件上强制实施 v-on 事件命名样式
		'vue/require-prop-type-constructor':0,
		//* *****
		// TS
		//* *****
		'@typescript-eslint/no-use-before-define': 0, // 在定义变量之前不允许使用变量
		'@typescript-eslint/no-explicit-any': 0,
		'@typescript-eslint/no-unused-vars': [0, { 'varsIgnorePattern': `.*`, 'args': `none` }], // 禁止未使用的变量
		'@typescript-eslint/no-empty-function': [0], // 不允许空函数。
		'@typescript-eslint/no-non-null-assertion': 0, // 使用非空断言会取消严格空值检查模式的好处。
		'*': 0 // 使用非空断言会取消严格空值检查模式的好处。
	}
};
