<template>
	<div class="announcement-container">
		<VerticalCarousel />
	</div>
</template>

<script setup>
	import VerticalCarousel from './VerticalCarousel.vue'
	import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
	import { news } from '@/Data/stringConstants'; // Assuming you have a JSON file with news data

	const currentDate = ref(new Date());
	let timer = null;

	const formattedMonthYear = computed(() => {
		const year = currentDate.value.getFullYear();
		const month = (currentDate.value.getMonth() + 1).toString().padStart(2, `0`);
		return `${year}.${month}`;
	});

	onMounted(() => {
		// Update time every minute
		timer = setInterval(() => {
			currentDate.value = new Date();
		}, 60000);
	});

	onBeforeUnmount(() => {
		if (timer) {
			clearInterval(timer);
		}
	});
</script>

<style scoped>
	.announcement-container {
		background-color: white;
		border-radius: 12px;
		width: 100%;
		height: 100px;
		display: flex;
		align-items: center;
		padding: 0 30px;
		margin: auto;
		position: absolute;
		top: -50px;
		left: 0;
		right: 0;
		margin: 0 auto;
		z-index: 10;
	}

	.announcement-header {
		display: flex;
		align-items: center;
		width: 100%;
	}

	.title-image {
		height: 60px;
		width: auto;
	}

	.divider {
		width: 2px;
		height: 60px;
		background-color: #eee;
		margin: 0 27px;
	}

	.announcement-content {
		flex: 1;
		margin-left: 22px;
		white-space: nowrap; /* 防止文字换行 */
		overflow: hidden; /* 隐藏溢出内容 */
		text-overflow: ellipsis;
	}

	.announcement-content h3 {
		font-size: 15px;
		font-weight: bold;
		color: #666;
	}

	.announcement-content p {
		font-size: 15px;
		color: #999;
		margin-top: 13px;
	}

	.announcement-content h3,
	.announcement-content p {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.date {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin: 0 20px 0 10px;
	}

	.date .day {
		font-size: 32px;
		font-weight: 900;
		color: #333;
	}

	.date .month-year {
		font-size: 16px;
		color: #666;
		margin-top: 5px;
	}
</style>
