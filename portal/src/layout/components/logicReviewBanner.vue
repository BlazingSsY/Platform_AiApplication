<script setup>
import logicReviewMenuColumn from "./logicReviewMenuColumn.vue"
const route = useRoute()
const isCodeReview = computed(() => route.path.indexOf(`/logicReviewHome`) !== -1)
</script>

<template>
  <div class="wrapBanner" :style="{height: isCodeReview ? '260px' : '0'}">
    <logicReviewMenuColumn />
  </div>
</template>

<style lang="scss" scoped>
.wrapBanner {
  height: 260px;
  background-color: aqua;
  position: relative;
  background: url(@/assets/images/logicReview/ljscBanner.png) no-repeat center center;
  background-size: 100% 100%;
}
</style>
