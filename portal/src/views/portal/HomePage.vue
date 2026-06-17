<!-- HomePage.vue -->
<template>
	<div class="homepPage">
		<Announcement v-if="!VITE_APP_IS_JIZAI"/>
		<FeatureCards/>
	</div>
</template>

<script setup>
	import Announcement from './components/Announcement.vue';
	import FeatureCards from './components/FeatureCards.vue';
	import store from "@/store/index"
	const isJiZaiUser = computed(() => store.state.isJiZaiUser)
	// 可以添加页脚数据或方法
	const VITE_APP_IS_JIZAI = isJiZaiUser.value
</script>

<style lang="scss">
	.homepPage {
		width: 100%;
		height: 100%;
		position: relative;
	}
</style>
