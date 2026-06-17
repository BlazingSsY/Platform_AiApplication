<template>
  <div v-loading.fullscreen.lock="loading" element-loading-text="数据加载中..." class="statistics">
    <div class="section">
      <div class="item-container">
        <div class="label">
          <div class="icon" />
          <div class="text">统计分析</div>
        </div>
        <div class="control-panel">
          <div class="controls">
            <div class="control">
              <div class="label">选择单位</div>
              <el-select v-model="departmentFilter" :disabled="roleName === `普通用户`" multiple placeholder="选择单位" style="width: 240px" collapse-tags collapse-tags-tooltip clearable filterable>
                <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
            <div class="control">
              <div class="label">选择审查日期</div>
              <el-date-picker v-model="dateRangeFilter" :disabled-date="disabledDate" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
            </div>
          </div>
          <div class="confirm-buttons">
            <el-button type="primary" @click="fetchData()" :disabled="departmentFilter.length==0"> 开始分析 </el-button>
            <el-button type="warning" @click="onReset()"> 重置 </el-button>
            <el-button v-if="isUserAdminOrJz||is631UserAdmin||isLeaderUserRole" type="primary" :disabled="departmentFilter.length==0" @click="exportData()"> 导出状态汇报表 </el-button>
          </div>
        </div>
      </div>
    </div>
    <div class="section">
      <indicator-panel :file-count="fileCount" :total-review-point-count="totalReviewPointCount" :total-pass-review-point-count="totalPassReviewPointCount" :total-pass-rate="totalPassRate" />
    </div>
    <div class="section">
      <div :class="chartItemLength >= MAX_CHART_ITEM_LENGTH ? 'review-table-full' : 'review-table'">
        <review-table :table-data="tableData" :list-params="listParams"/>
      </div>
      <div :class="chartItemLength >= MAX_CHART_ITEM_LENGTH ? 'stats-chart-full' : 'stats-chart'">
        <stats-chart
          :chart-data="chartData"
          :width="chartItemLength >= MAX_CHART_ITEM_LENGTH ? '100%' : '450px'"
        />
      </div>
    </div>
     <!-- 导出 -->
     <el-dialog
        v-model="dialogVisible"
        title="导出"
        width="500px"
      >
        <el-form
          label-width="120px"
        >
          <el-form-item label="选择审查日期" prop="title">
            <!-- 小于当前日期 -->
            <el-date-picker v-model="expotDateRangeFilter" :disabled-date="disabledDate" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">关闭   </el-button>
            <el-button v-loading="exportLoading" type="primary" @click="exportSend()">确定</el-button>
          </span>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import store from "@/store/index"
import IndicatorPanel from "./IndicatorPanel.vue"
import ReviewTable from "./review-table/ReviewTable.vue"
import StatsChart from "./stats-chart/StatsChart.vue"
import { getStatsData, getDepartmentsSimpleItems,statisticsExportExcel } from "./api"
import { isAdminAndJz } from '@/hooks/userAdminAndJz'
import { is631Admin } from '@/hooks/user631Admin'
import { isLeaderRole } from '@/hooks/userLeader'
const isLeaderUserRole = isLeaderRole()
const is631UserAdmin = is631Admin()
const isUserAdminOrJz = isAdminAndJz()

const dialogVisible = ref(false)
const expotDateRangeFilter = ref([])
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}
const exportData = ()=>{
  expotDateRangeFilter.value = []
  dialogVisible.value = true
}
const exportLoading = ref(false)
const exportSend = ()=>{
  exportLoading.value = true
  statisticsExportExcel({
    startDate: expotDateRangeFilter.value[0],
    endDate: expotDateRangeFilter.value[1],
  }).then(res=>{
    const blob = new Blob([res.data], { type: res.headers['content-type']})
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = res.headers['content-disposition'].split(`filename=`)[1].replace(/"/g, '')
    link.click()
    URL.revokeObjectURL(link.href)
    dialogVisible.value = false
    exportLoading.value = false
  }).catch(err=>{
    console.log(`err`,err)
    dialogVisible.value = false
    exportLoading.value = false
  })
}

const userInfo = computed(() => store.state.user.userInfo)
const userDepartmentId = userInfo.value.departmentId
const roleInfo = computed(() => userInfo.value?.role)
const roleName = computed(() => {
  // name 普通用户
  return roleInfo.value ? roleInfo.value.name : ``
})

const loading = ref(true)
const loadingDepartments = ref(false)
const departmentFilter = ref([])
const dateRangeFilter = ref([])

const fileCount = ref(0)
const totalReviewPointCount = ref(0)
const totalPassReviewPointCount = ref(0)
const totalPassRate = ref(0)
const tableData = reactive([])
const chartData = ref({})
const departmentsData = reactive([])

const resetFilters = () => {
  departmentFilter.value = departmentsData.map(data => data.id)
  dateRangeFilter.value = []
}
const listParams = ref({})
const fetchData = () => {
  const params = {}
  if (departmentFilter.value) {
    params.departmentIds = departmentFilter.value
  }
  if (dateRangeFilter.value[0]) {
    params.startDate = dateRangeFilter.value[0]
  }
  if (dateRangeFilter.value[1]) {
    params.endDate = dateRangeFilter.value[1]
  }
  loading.value = true
  listParams.value = params
  getStatsData(params)
    .then(res => {
      if (res.succ) {
        fileCount.value = res.content.fileCount
        totalReviewPointCount.value = res.content.totalReviewPointCount
        totalPassReviewPointCount.value = res.content.totalPassReviewPointCount
        totalPassRate.value = res.content.totalPassRate

        tableData.splice(0)
        res.content.fileDetailVOList.forEach(item => {
          tableData.push({ ...item })
        })
        chartData.value = res.content.chartDataMap
      }
    })
    .catch(error => {
      ElMessage.error("获取数据失败", error)
    })
    .finally(() => {
      loading.value = false
    })
}

const onReset = () => {
  resetFilters()
  fetchData()
}

const getDepartments = () => {
  loadingDepartments.value = true
  getDepartmentsSimpleItems({ departmentId: userDepartmentId })
    .then(res => {
      if (res.succ) {
        departmentsData.splice(0)
        res.content.forEach(item => {
          departmentsData.push({ ...item })
        })
      }
    })
    .catch(error => {
      ElMessage.error("获取单位数据失败", error)
    })
    .finally(() => {
      loadingDepartments.value = false
    })
}
getDepartments()

watch(
  [() => departmentsData],
  ([newDepartmentsData]) => {
    departmentFilter.value = newDepartmentsData.map(data => data.id)
    fetchData()
  },
  { deep: true }
)

watch(
  [() => dateRangeFilter],
  ([newDateRangeFilter]) => {
    if (newDateRangeFilter.value === null) {
      dateRangeFilter.value = []
    }
  },
  { deep: true }
)

const chartItemLength = ref(1000)
const MAX_CHART_ITEM_LENGTH = 12
watch([() => chartData.value],
  ([newChartData]) => {
    if (newChartData) {
      const firstLevel = '按单位'
      const secondLevel = Object.keys(newChartData[firstLevel] || [])?.[0]
      if (newChartData[firstLevel]?.[secondLevel]) {
        chartItemLength.value = Object.keys(newChartData[firstLevel][secondLevel]).length
      }
    }
  }
)
</script>

<style lang="scss" scoped>
.statistics {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0px;
  gap: 30px;
  overflow-y: auto;

  .section {
    width: 100%;
    display: flex;
    flex-direction: row;
    // flex-grow: 1;
    gap: 20px;
    justify-content: center;
    flex-wrap: wrap;
  }
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

.control-panel {
  width: 100%;
  padding: 20px 30px;
  background: #f5f7fa;
  box-shadow: 0px 8px 38px 0px rgba(3, 13, 28, 0.02);
  border-radius: 20px;

  display: flex;
  flex-direction: row;
  justify-content: space-between;

  .controls {
    display: flex;
    flex-direction: row;
    gap: 30px;

    .control {
      display: flex;
      flex-direction: row;
      gap: 12px;

      .label {
        background: #f5f7fa;
        box-shadow: 0px 8px 38px 0px rgba(3, 13, 28, 0.02);
        border-radius: 20px;
      }

      :deep(.el-select__wrapper) {
        height: 32px;
        background: #f5f7fa;
      }

      :deep(.el-tag) {
        background: #eee;
      }

      :deep(.el-date-editor) {
        height: 32px;
        background: #f5f7fa;
      }
    }
  }

  .confirm-buttons {
    align-self: flex-end;
  }
}

.review-table {
  width: 900px;
  max-height: 400px;
  flex-grow: 1;
  flex-shrink: 1;
}

.review-table-full {
  width: 100%;
  max-height: 400px;
}

.stats-chart {
  width: 450px;
  height: 100%;
  min-height: 400px;
}

.stats-chart-full {
  width: 100%;
  height: 400px;
}
</style>
