<!--
 * @Description:
 * @Author: hui
 * @Date: 2022-02-17 10:06:44
 * @LastEditTime: 2024-06-12 11:32:41
-->
<template>
	<el-config-provider :locale="locale">
		<div id="app-container" :style="containerStyle">
			<router-view />
		</div>
	</el-config-provider>
</template>
<script setup>
	import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
	import { ElConfigProvider } from 'element-plus';
	import store from "@/store/index"
	import zhCn from 'element-plus/es/locale/lang/zh-cn';
	import { deploymentType } from '@/ajax/circuitreview'
	const locale = zhCn
	// 获取是否为记载
	const getIsJiZaiUser = ()=>{
		// 要替换成接口获取
		deploymentType().then(res=>{
			if(res.succ){
				console.log(`0是jizai，1是631 --- res.content`,res.content)
				// 0是jizai，1是631
				store.commit(`SET_IS_JI_ZAI_USER`, res.content==0)
			}
		})
	}
	getIsJiZaiUser()

	// 监听isScreenChange变化
	const isScreenChange = computed(() => store.state.isScreenChange)
	console.log(`isScreenChange`,isScreenChange.value)
	const containerStyle = ref({
		height: '100%',
		width: '100%'
	});
	// 计算缩放比例并应用到容器
	const calculateScale = () => {
		const currentWidth = window.innerWidth;
		if(currentWidth<=1740){
			const standardWidth = 1920;
			// 保持比例，但可以添加最小值或最大值限制
			const scaleValue = Math.min(currentWidth / standardWidth, 1); // 不放大，只缩小
			containerStyle.value = {
				transform: `scale(${scaleValue})`,
				transformOrigin: 'top left',
				width: `${100 / scaleValue}%`,
				height: `${100 / scaleValue}%`,
				transition: 'transform 0.3s ease'
			};
			store.commit(`SET_SCREEN_CHANGE`, false)
		}else{
			containerStyle.value = {
				height: '100%',
				width: '100%'
			};
		}
	};

	watch(isScreenChange.value, (newVal, oldVal) => {
		if(newVal){
			calculateScale()
		}
	},{deep:true})
	// 监听窗口大小变化
	onMounted(() => {
		calculateScale(); // 初始化
		window.addEventListener('resize', calculateScale);
	});

	// 组件卸载时移除监听
	onUnmounted(() => {
		window.removeEventListener('resize', calculateScale);
		containerStyle.value = {
			height: '100%',
			width: '100%'
		};
	});
</script>
<!-- <script lang="ts">
	import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
	import { ElConfigProvider } from 'element-plus';
	import store from "@/store/index"
	import zhCn from 'element-plus/es/locale/lang/zh-cn';

	export default defineComponent({
		'components': {
			ElConfigProvider
		},
		setup() {
			const isScreenChange = computed(() => store.state.isScreenChange)
			console.log(`isScreenChange`,isScreenChange.value)
			const containerStyle = ref({
				height: '100%',
				width: '100%'
			});

			// 计算缩放比例并应用到容器
			const calculateScale = () => {
				const currentWidth = window.innerWidth;
				if(currentWidth<=1700){
					const standardWidth = 1920;
					// 保持比例，但可以添加最小值或最大值限制
					const scaleValue = Math.min(currentWidth / standardWidth, 1); // 不放大，只缩小
					containerStyle.value = {
						transform: `scale(${scaleValue})`,
						transformOrigin: 'top left',
						width: `${100 / scaleValue}%`,
						height: `${100 / scaleValue}%`,
						transition: 'transform 0.3s ease'
					};
					store.commit(`SET_SCREEN_CHANGE`, false)
				}else{
					containerStyle.value = {
						height: '100%',
						width: '100%'
					};
				}
			};

			watch(isScreenChange.value, (newVal, oldVal) => {
				console.log(`isScreenChange`,isScreenChange.value)
				if(newVal){
					calculateScale()
				}
			},{deep:true})

			// 监听窗口大小变化
			onMounted(() => {
				calculateScale(); // 初始化
				window.addEventListener('resize', calculateScale);
			});

			// 组件卸载时移除监听
			onUnmounted(() => {
				window.removeEventListener('resize', calculateScale);
				containerStyle.value = {
					height: '100%',
					width: '100%'
				};
			});

			return {
				'locale': zhCn,
				containerStyle
			};
		}
	});
</script> -->
<style lang="scss">
	html, body {
		// scrollbar-gutter: stable; /* 为滚动条预留固定空间 */
		width: 100%;
		height: 100%;
		margin: 0;
		padding: 0;
		overflow: hidden;
	}
	#app-container {
		overflow: hidden;
	}
	.minus5 {
		margin-left: -12px;
		margin-right: -12px;
	}
	.w-e-text-container {
		strong {
			font-weight: bold !important;
		}
		em {
			font-style: italic !important;
		}
	}
</style>