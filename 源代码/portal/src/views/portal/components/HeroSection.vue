<template>
	<section class="heroSection">
		<!-- <SearchBar /> -->
		<MenuColumn />
	</section>
</template>
<script setup>
	import SearchBar from './SearchBar.vue';
	import MenuColumn from './MenuColumn.vue';
</script>
<style scoped>
	.heroSection {
		position: relative;
		height: 300px;
		background: url('@/assets/images/banner.png') center no-repeat;
		display: flex;
		width: 100%;
		background-size: 100% 100%;
	}
</style>
