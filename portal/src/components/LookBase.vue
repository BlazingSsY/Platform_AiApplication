<!--
 * @Description: 查看组件
 * @Author: ym
 * @Date: 2021-03-10 10:11:05
 * @LastEditTime: 2022-04-01 14:54:06
-->
<template>
  <div class="lookBasics">
    <div class="infoContainer" :style="`padding: ${padding && padding.length > 0 ? padding[0] + ` ` + (padding[1] || `0`) : `0`}`">
      <div v-for="(item, index) in infoConfig_" :key="item.title + `_base_looK_` + index">
        <div :class="`title ${index > 0 && 'border'}`">{{ item.title }}</div>
        <!-- <slot :name="item.slot" /> -->
        <slot :name="item.slot" />
        <div>
          <div v-for="(card, cardIndex) in item.propsConfig[0]" :key="card.prop + '_card_' + cardIndex" class="card">
            <div>
              <template v-if="card.slot">
                <span class="label">{{ card.label + " :" }}</span>
                <!-- <slot :name="card.slot" /> -->
                <slot :name="card.slot" />
              </template>
              <template v-else>
                <span class="label">{{ card.label + " :" }}</span>
                <span v-if="card.type === `icon`">
                  <i :class="infoData[card.prop] + ` iconfont`" />
                </span>
                <span v-else v-html="(card.contentCallback && card.contentCallback(infoData)) || infoData[card.prop]" />
              </template>
            </div>
            <div v-if="item.propsConfig[1][cardIndex]">
              <template v-if="item.propsConfig[1][cardIndex].slot">
                <!-- <slot :name="item.propsConfig[1][cardIndex].slot" /> -->
                <slot :name="item.propsConfig[1][cardIndex].slot" />
              </template>
              <template v-else>
                <span class="label">{{ item.propsConfig[1][cardIndex].label + " :" }}</span>
                <span v-if="item.propsConfig[1][cardIndex].type === `icon`">
                  <i :class="infoData[item.propsConfig[1][cardIndex].prop] + ` iconfont`" />
                </span>
                <span v-else v-html="(item.propsConfig[1][cardIndex].contentCallback && item.propsConfig[1][cardIndex].contentCallback(infoData)) || infoData[item.propsConfig[1][cardIndex].prop]" />
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="btnShow" class="btn">
      <el-button class="cancel-btn" @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>
<script setup lang="ts">
import { defineProps, reactive } from "vue"
import { configItem } from "@/types/components/LookBase"
const props = defineProps({
  "infoConfig": {
    "type": Array,
    "required": false,
    "default": []
  },
  "infoData": {
    "type": Object,
    "required": false,
    "default": {}
  },
  "padding": {
    "type": Array,
    "required": false,
    "default": () => [`0`, `0`]
  },
  "btnShow": {
    "type": Boolean,
    "required": false,
    "default": true
  }
})
let infoConfig_: configItem[] = reactive([])

const trans = () => {
  infoConfig_ = props.infoConfig.map((e: any) => {
    const childen_ = e.propsConfig ? e.propsConfig.filter((f: any) => !(f.cbHidden && f.cbHidden(props.infoData))) : []
    const index_ = !(childen_.length % 2) ? childen_.length / 2 : parseInt(`${childen_.length / 2}`) + 1
    return {
      "title": e.title,
      "slot": e.slot,
      "propsConfig": [[...childen_.slice(0, index_)], [...childen_.slice(index_, childen_.length + 1)]]
    }
  })
}
trans()
</script>
<style lang="scss" scoped>
.lookBasics {
  display: flex;
  height: 100%;
  flex-direction: column;
  .infoContainer {
    flex: 1;
    height: 0;
    overflow-y: auto;
    overflow-x: hidden;
    .title {
      padding: 10px 20px 20px 20px;
      color: #000;
      text-shadow: 1px 1px 0px #ccd5d6;
    }
    .border {
      border-top: 1px solid #eee;
    }
    .card {
      display: flex;
      padding: 0 16px;
      > div {
        width: 50%;
        display: flex;
        padding: 2px 0 2px 40px;
        position: relative;
        .label {
          // text-align: right;
          // min-width: 100px;
          color: $normal_C;
        }
        > span {
          padding: 14px 6px;
        }
      }
      &:nth-child(2n - 1) {
        background: #f7f9fa;
      }
    }
  }
  .btn {
    background: #fff;
    width: 100%;
    padding: 10px 0 20px 0;
    text-align: center;
  }
  .cancel-btn {
    background-color: #ecf5ff;
  }
  :deep(.el-table) {
    &:before {
      height: 0;
    }
  }
}
</style>
