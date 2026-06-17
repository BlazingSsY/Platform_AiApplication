<template>
	<div class="search-bar-container">
		<div class="search-bar">
			<input v-model="searchQuery" type="text" placeholder="请输入产品名称、缩写、功能名称进行查询" @keyup.enter="handleSearch" />
			<button class="search-btn" @click="handleSearch">
				<img src="@/assets/images/searchIcon.png" alt="搜索" />
				<span>功能检索</span>
			</button>
		</div>
	</div>
</template>

<script setup>
	import { ref } from 'vue';

	const searchQuery = ref(``);

	const handleSearch = () => {
		if (!searchQuery.value.trim()) {
			alert(`请输入搜索内容`);
			return;
		}
		// 这里添加实际的搜索逻辑
		console.log(`搜索内容:`, searchQuery.value);
		// 可以emit事件或调用API
		// emit('search', searchQuery.value);
	};
</script>

<style scoped>
	.search-bar-container {
		width: 50%;
		height: 68px;
		position: absolute;
		bottom: 82px;
		left: 0;
		right: 0;
		margin: auto;
	}

	.search-bar {
		display: flex;
		align-items: center;
		background-color: white;
		border-radius: 40px;
		border: 1px solid #9bbbe0;
		height: 100%;
		padding: 0 4px 0 39px;
	}

	.search-bar input {
		flex: 1;
		border: none;
		outline: none;
		font-size: 15px;
		color: #333;
		/* 修改为更明显的文字颜色 */
	}

	.search-btn {
		width: 140px;
		height: 48px;
		background: linear-gradient(90deg, #018FD7, #026FF7);
		border-radius: 24px;
		border: none;
		color: white;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 16px;
		cursor: pointer;

		span {
			font-size: 15px;
		}
	}

	.search-btn img {
		width: 18px;
		height: 18px;
		margin-right: 10px;
	}
</style>
