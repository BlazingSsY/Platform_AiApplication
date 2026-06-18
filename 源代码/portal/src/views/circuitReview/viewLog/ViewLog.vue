<template>
  <div class="log-view">
    <div class="section">
      <div class="item-container">
        <div class="label">
          <div class="icon" />
          <div class="text">日志查看</div>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeTab" class="log-tabs">
      <el-tab-pane label="中间服务层日志" name="service">
        <LogContent ref="serviceLogRef" :fetchLog="fetchServiceLog" :downloadLog="downloadServiceLog" :defaultNum="1000" />
      </el-tab-pane>
      <el-tab-pane label="后端调试日志" name="debug">
        <LogContent ref="debugLogRef" :fetchLog="(num: number) => fetchBackendLog('debug', num)" :downloadLog="(num: number) => downloadBackendLog('debug', num)" :defaultNum="5000" />
      </el-tab-pane>
      <el-tab-pane label="模糊匹配日志" name="fuzzy">
        <LogContent ref="fuzzyLogRef" :fetchLog="(num: number) => fetchBackendLog('fuzzy', num)" :downloadLog="(num: number) => downloadBackendLog('fuzzy', num)" :defaultNum="5000" />
      </el-tab-pane>
      <el-tab-pane label="审查结果日志" name="result">
        <LogContent ref="resultLogRef" :fetchLog="(num: number) => fetchBackendLog('result', num)" :downloadLog="(num: number) => downloadBackendLog('result', num)" :defaultNum="5000" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import LogContent from './LogContent.vue'
import { getCircuitLog, getCircuitServiceLog, downloadCircuitLog, downloadCircuitServiceLog } from '@/ajax/circuitreview'

const activeTab = ref('service')
const serviceLogRef = ref<InstanceType<typeof LogContent> | null>(null)
const debugLogRef = ref<InstanceType<typeof LogContent> | null>(null)
const fuzzyLogRef = ref<InstanceType<typeof LogContent> | null>(null)
const resultLogRef = ref<InstanceType<typeof LogContent> | null>(null)

const fetchServiceLog = (num: number) => getCircuitServiceLog({ num })
const fetchBackendLog = (logName: string, num: number) => getCircuitLog({ logName, num })
const downloadServiceLog = (num: number) => downloadCircuitServiceLog({ num })
const downloadBackendLog = (logName: string, num: number) => downloadCircuitLog({ logName, num })

onMounted(() => {
  // 默认加载 service log
  if (serviceLogRef.value) {
    serviceLogRef.value.getLog()
  }
})

// 切换tab时自动查询对应日志
const tabRefMap: Record<string, any> = {
  service: serviceLogRef,
  debug: debugLogRef,
  fuzzy: fuzzyLogRef,
  result: resultLogRef,
}
watch(activeTab, (tab) => {
  nextTick(() => {
    const refInstance = tabRefMap[tab]
    if (refInstance?.value) {
      refInstance.value.getLog()
    }
  })
})
</script>

<style lang="scss" scoped>
.log-view {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 30px 0px;
  gap: 20px;
  overflow-y: auto;
}

.log-tabs {
  width: 100%;

  :deep(.el-tabs__header) {
    margin-bottom: 20px;
  }
}

.section {
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.item-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex-shrink: 1;
  flex-grow: 1;

  .label {
    display: flex;
    flex-direction: row;
    gap: 8px;
    align-items: center;

    .icon {
      width: 12px;
      height: 20px;
      background: linear-gradient(90deg, rgba(32, 133, 255, 0.73), rgba(255, 255, 255, 0));
      box-shadow: 0px 0px 10px 0px rgba(38, 99, 241, 0.35);
    }

    .text {
      font-family: YouSheBiaoTiHei;
      font-weight: 400;
      font-size: 20px;
      color: #0e2036;
      line-height: 36px;
    }
  }
}
</style>
