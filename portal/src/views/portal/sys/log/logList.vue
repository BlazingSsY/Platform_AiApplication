<!--
 * @Description: 日志管理
 * @Author: ym
 * @Date: 2021-03-09 16:20:27
 * @LastEditTime: 2024-02-26 09:51:19
-->
<template>
	<div class="logsList">
		<div class="title">日志列表</div>
		<CustomTable class="list" :search-callback="searchCallback" :table-config="tableConfig" />
	</div>
</template>
<script lang="ts" setup>
	import { reactive } from 'vue';
	import { useRouter } from 'vue-router';
	import { getLogPages } from '@/ajax';
	// 路由
	const router = useRouter();
	const tableConfig = reactive({
		'search': {
			'prop': `parameter`,
			'label': `关键字`,
			'hasPermi': `logs:btn:search`
		},
		'tableHeader': [
			{
				'label': `模块名称`,
				'prop': `modelName`
			},
			{
				'label': `操作内容`,
				'prop': `content`
			},
			{
				'label': `操作人昵称`,
				'prop': `userName`
			},
			{
				'label': `登录名称`,
				'prop': `loginName`
			},
			{
				'label': `操作时间`,
				'prop': `operateDate`
			},
			{
				'label': `操作`,
				'prop': `id`,
				'btns': [
					{
						'label': `查看`,
						// hasPermi: `logs:btn:look`,
						'btnCallback': (item: any) => {
							router.push({
								'path': `/logDetail`,
								'query': {
									'rowData': JSON.stringify(item)
								}
							});
						}
					}
				]
			}
		]
	});
	// 搜索事件
	const searchCallback = (params: any) => getLogPages(params);
</script>
<style scoped lang="scss">
	.logsList {
		background: #fff;
		position: relative;
		.list {
			position: absolute;
			top: 72px;
			left: 0;
			right: 0;
			bottom: 0;
			height: auto !important;
		}
		.title {
			font-size: 20px;
			font-weight: bold;
			height: 70px;
			line-height: 70px;
			padding: 0 20px;
			border-bottom: 1px solid #e2e2e2;
		}
	}
</style>
