<!--
 * @Description: 日志详情
 * @Author: ym
 * @Date: 2021-03-09 16:29:08
 * @LastEditTime: 2022-04-01 09:46:45
-->
<template>
	<div class="logsEditor">
		<LookBase :info-config="infoConfig" :info-data="logInfo" />
	</div>
</template>
<script lang="ts" setup>
	import { reactive } from 'vue';
	import { useRouter } from 'vue-router';
	import { infoConfigItem } from '@/types/components/LookBase';
	const router = useRouter();
	let logInfo: any = reactive({});
	const infoConfig: infoConfigItem[] = reactive([
		{
			'title': `基本信息`,
			'propsConfig': [
				{
					'label': `模块名称`,
					'prop': `modelName`
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
					'label': `操作内容`,
					'prop': `content`
				},
				{
					'label': `操作人昵称`,
					'prop': `userName`
				}
			]
		}
	]);
	const rowData = JSON.parse(router.currentRoute.value.query.rowData as string);
	if (rowData.id) {
		logInfo = Object.assign(logInfo, rowData);
	}
</script>
<style scoped lang="scss">
	.logsEditor {
		background: #fff;
		height: 100%;
		overflow-y: auto;
	}
</style>
