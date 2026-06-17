<!--
 * @Description:
 * @Author: hui
 * @Date: 2022-02-18 10:44:34
 * @LastEditTime: 2024-10-16 17:07:32
-->
<template>
  <div id="layoutWrap" class="layoutWrap">
    <wrapHead />
    <wrapBanner />
    <div class="contBox">
      <router-view />
    </div>
    <wrapFooter />
  </div>
</template>

<script setup>
import wrapHead from "./components/wrapHead.vue"
import wrapFooter from "./components/wrapFooter.vue"
import wrapBanner from "./components/wrapBanner.vue"
</script>

<style lang="scss" scoped>
.layoutWrap {
  height: 100%;
  width: 100%;
  position: relative;
  padding-top: 80px;
  overflow: auto;
  background: url("@/assets/images/pageBj.png") center no-repeat;
  background-size: 100% 100%;
  // max-height: 900px;
  display: flex;
  flex-direction: column;
  .wrapBanner {
    min-width: 1600px;
    flex-shrink: 0;
  }
  .footer{
    min-width: 1600px;
    flex-shrink: 0;
  }
  .contBox {
    flex: 1;
    width: 100%;
    max-width: calc(100% - 160px);
    margin: 0 auto;
    // min-height: calc(100% - 120px);
   min-width: 1600px;
    // display: grid;
    > div {
      width: 100%;
      height: 100%;
      position: relative;
      :deep(.el-table) {
        th.el-table__cell {
           background: rgba(215, 232, 251, 1);
        }
      }
    }
  }
}
</style>
