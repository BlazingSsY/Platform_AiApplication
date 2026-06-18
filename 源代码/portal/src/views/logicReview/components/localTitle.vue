<script setup lang="ts">
const props = defineProps({
  "title": {
    "type": String,
    "required": true,
    "default": ``
  },
  backBtn: {
    "type": Boolean,
    "required": false,
    "default": false
  }
})
const emits = defineEmits([`goBack`])
const goBack = () => {
  emits(`goBack`)
}
</script>

<template>
  <div class="title">
    <div class="left">
      <img src="@/assets/images/arrow.png" />
      {{ props.title }}

      <el-button v-show="props.backBtn" link icon="Back" style="margin-left: 10px" @click="goBack">返回</el-button>
    </div>
    <div class="rightBtn">
      <slot name="rightBtn">
        <!-- 前缀插槽 -->
      </slot>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  height: 65px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .left {
    font-size: 20px;
    color: #0e2036;
    display: flex;
    align-items: center;
    > img {
      width: auto;
      height: 20px;
      margin-right: 6px;
      filter: blur(1px);
    }
  }
  .rightBtn {
    width: 70%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    position: relative;
  }
}
</style>
