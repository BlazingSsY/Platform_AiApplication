<script setup>
import menuColumn from "./menuColumn.vue"
const route = useRoute()
const isCodeReview = computed(() => route.path.indexOf(`/circuitReviewHome`) !== -1)
</script>

<template>
  <div class="wrapBanner" :style="{height: isCodeReview ? '260px' : '0'}">
    <menuColumn />
  </div>
</template>

<style lang="scss" scoped>
.wrapBanner {
  height: 260px;
  background-color: aqua;
  position: relative;
  background: url(@/assets/images/circuitReview/bannerV2.png) no-repeat center center;
  background-size: 100% 100%;
}
</style>
