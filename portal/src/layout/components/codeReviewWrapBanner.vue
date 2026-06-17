<script setup>
import codeReviewMenuColumn from "./codeReviewMenuColumn.vue"
const route = useRoute()
const isCodeReview = computed(() => route.path.indexOf(`/codeReviewHome`) !== -1)
</script>

<template>
  <div class="wrapBanner" :style="{height: isCodeReview ? '260px' : '0'}">
    <codeReviewMenuColumn />
  </div>
</template>

<style lang="scss" scoped>
.wrapBanner {
  height: 260px;
  background-color: aqua;
  position: relative;
  background: url(@/assets/images/codeReview/bannerV2.png) no-repeat center center;
  background-size: 100% 100%;
}
</style>
