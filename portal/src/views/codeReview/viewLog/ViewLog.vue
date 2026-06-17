<template>
  <div class="log-content">
    <div class="section">
      <div class="item-container">
        <div class="label">
          <div class="icon" />
          <div class="text">日志查看</div>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeTab" class="log-tabs">
      <el-tab-pane label="后端log" name="backend">
        <BackendLog ref="backendLogRef" />
      </el-tab-pane>
      <el-tab-pane label="中间服务层log" name="service">
        <ServiceLog ref="serviceLogRef" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import BackendLog from './BackendLog.vue'
import ServiceLog from './ServiceLog.vue'

const activeTab = ref('backend')
const backendLogRef = ref<InstanceType<typeof BackendLog> | null>(null)
const serviceLogRef = ref<InstanceType<typeof ServiceLog> | null>(null)

onMounted(() => {
  if (backendLogRef.value) {
    backendLogRef.value.getLog()
  }
  if (serviceLogRef.value) {
    serviceLogRef.value.getLog()
  }
})
</script>

<style lang="scss" scoped>
.log-content {
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
