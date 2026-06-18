<template>
	<div class="unfound-container">
		<div class="content">
			<div class="pic-404">
				<img class="pic-404__parent" src="@/assets/images/meeting.svg" alt="404" />
			</div>
			<div class="bullshit">
				<div class="bullshit__oops">404错误!</div>
				<div class="bullshit__headline">
					{{ message }}
				</div>
				<div class="bullshit__info">对不起，您正在寻找的页面不存在。尝试检查URL的错误，然后按浏览器上的刷新按钮或尝试在我们的应用程序中找到其他内容。</div>
				<el-button type="primary" @click="goBack()"> 返回首页 </el-button>
				<!-- <router-link to="/userList" class="bullshit__return-home"> 返回首页 </router-link> -->
			</div>
		</div>
	</div>
</template>

<script setup>
	const message = computed(() => {
		return `找不到网页！`;
	});
	const router = useRouter();
	const goBack = () => {
		router.go(-1);
	};
</script>

<style lang="scss" scoped>
	.unfound-container {
		transform: translate(-50%, -50%);
		position: absolute;
		top: 40%;
		left: 50%;
	}
	.content {
		display: flex;
		justify-content: center;
		position: relative;
		width: 1200px;
		padding: 0 50px;
		overflow: hidden;
		.pic-404 {
			position: relative;
			float: left;
			width: 600px;
			overflow: hidden;
		}
		.bullshit {
			position: relative;
			float: left;
			width: 300px;
			padding: 30px 30px;
			overflow: hidden;
			&__oops {
				font-size: 32px;
				font-weight: bold;
				line-height: 40px;
				color: #1482f0;
				margin-bottom: 20px;
			}
			&__headline {
				font-size: 20px;
				line-height: 24px;
				color: #222;
				font-weight: bold;
				margin-bottom: 10px;
			}
			&__info {
				font-size: 13px;
				line-height: 21px;
				color: grey;
				margin-bottom: 30px;
			}
			&__return-home {
				display: block;
				float: left;
				width: 110px;
				height: 36px;
				background: #1482f0;
				border-radius: 100px;
				text-align: center;
				color: #ffffff;
				font-size: 16px;
				line-height: 36px;
				cursor: pointer;
			}
		}
	}
</style>
