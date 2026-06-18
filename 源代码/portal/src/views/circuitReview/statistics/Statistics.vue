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
              <el-select v-model="departmentFilter" multiple placeholder="选择单位" style="width: 240px" collapse-tags collapse-tags-tooltip clearable filterable :disabled="roleName === `普通用户` || is631UserAdmin">
                <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
            <div v-if="is631UserAdmin" class="control">
              <div class="label">选择用户</div>
              <el-select v-model="userFilter" multiple placeholder="选择用户" style="width: 240px" collapse-tags collapse-tags-tooltip clearable filterable :disabled="roleName === `普通用户`">
                <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
            <div class="control">
              <div class="label">选择审查日期</div>
              <el-date-picker v-model="dateRangeFilter" :disabled-date="disabledDate" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
            </div>
          </div>
          <div class="confirm-buttons">
            <el-button type="primary" @click="fetchData()" :disabled="departmentFilter.length === 0"> 开始分析 </el-button>
            <el-button type="warning" @click="onReset()"> 重置 </el-button>
            <el-button v-if="isUserAdminOrJz||is631UserAdmin||isLeaderUserRole" :disabled="departmentFilter.length === 0" type="primary" @click="exportData()"> 导出状态汇报表 </el-button>
          </div>
        </div>
      </div>
    </div>
    <div class="section">
      <indicator-panel :file-count="fileCount" :total-review-point-count="totalReviewPointCount" :total-pass-review-point-count="totalPassReviewPointCount" :total-fail-review-point-count="totalFailReviewPointCount" :total-pass-rate="totalPassRate" />
    </div>
    <div class="section">
      <div :class="'review-table-full'">
        <review-table :table-data="tableData" :list-params="listParams"/>
      </div>
      <div :class="'stats-chart-full'">
        <stats-chart
          :chart-data="chartData"
          :width="'100%'"
          :is-single-department="departmentFilter.length === 1"
          />
      </div>
    </div>
    <!-- 备份动态分2部分的样式 
     <div class="section">
      <div :class="chartItemLength >= MAX_CHART_ITEM_LENGTH ? 'review-table-full' : 'review-table'">
        <review-table :table-data="tableData" />
      </div>
      <div :class="chartItemLength >= MAX_CHART_ITEM_LENGTH ? 'stats-chart-full' : 'stats-chart'">
        <stats-chart
          :chart-data="chartData"
          :width="chartItemLength >= MAX_CHART_ITEM_LENGTH ? '100%' : '450px'"
          :is-single-department="departmentFilter.length === 1"
          />
      </div>
    </div>    
    -->
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
import { reactive, ref, computed, watch, onUnmounted } from "vue"
import store from "@/store/index"
import IndicatorPanel from "./IndicatorPanel.vue"
import ReviewTable from "./review-table/ReviewTable.vue"
import StatsChart from "./stats-chart/StatsChart.vue"
import { getStatsData, getStatsDataAffix, getDepartmentsSimpleItems, statisticsExportExcel } from "./api"
import { GroupUserList } from "@/ajax/index"
import { is631Admin } from '@/hooks/user631Admin'
import { isAdminAndJz } from '@/hooks/userAdminAndJz'
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
const userFilter = ref([])
const dateRangeFilter = ref([])

const fileCount = ref(0)
const totalReviewPointCount = ref(0)
const totalPassReviewPointCount = ref(0)
const totalFailReviewPointCount = ref(0)
const totalPassRate = ref(0)
const tableData = reactive([])
const chartData = ref({})
const departmentsData = reactive([])
const userList = reactive([])

// 用于存储轮询定时器引用，组件卸载时清理
let affixPollTimer = null

onUnmounted(() => {
  console.log(`onUnmounted`)
  if (affixPollTimer) {
    console.log(`onUnmounted affixPollTimer`,affixPollTimer)
    clearTimeout(affixPollTimer)
    affixPollTimer = null
  }
})

const resetFilters = () => {
  departmentFilter.value = departmentsData.map(data => data.id)
  if (is631UserAdmin.value) {
    userFilter.value = userList.map(data => data.id)
  }
  dateRangeFilter.value = []
}
const listParams = ref({})
const fetchData = () => {
  const params = {}
  if (departmentFilter.value) {
    params.departmentIds = departmentFilter.value
  }
  if (is631UserAdmin.value && userFilter.value) {
    params.userIds = userFilter.value
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
        totalFailReviewPointCount.value = res.content.totalFailReviewPointCount
        totalPassRate.value = res.content.totalPassRate

        tableData.splice(0)
        res.content.fileDetailVOList.forEach(item => {
          tableData.push({ ...item })
        })
        chartData.value = res.content.chartDataMap
        console.log('chartData.value before affix:', chartData.value)
        let resultId = res.content.resultId
        // 20秒后再调用getStatsDataAffix补充慢的数据，用来补充 res.content.chartDataMap的按规则类型的value
        // 延迟5秒后开始轮询补充数据
        let affixPollCount = 0;
        const maxAffixPollCount = 12;
        const affixPollInterval = 5000;
        // 注意：affixPollTimer 是组件级别的变量，用于在组件卸载时清理定时器

        const pollAffixData = () => {
          affixPollCount++
          if (affixPollCount > maxAffixPollCount) {
            // 超过最大轮询次数，停止轮询
            if (affixPollTimer) {
              clearTimeout(affixPollTimer)
              affixPollTimer = null
            }
            return
          }

          getStatsDataAffix({ resultId })
            .then(affixRes => {
              if (affixRes.succ && affixRes.content) {
                // 将按规则类型的数据合并到chartData中
                if (affixRes.content) {
                  Object.assign(chartData.value.按规则类型, affixRes.content)
                  console.log('chartData.value after affix:', chartData.value)
                }
                // 轮询到数据后停止
                if (affixPollTimer) {
                  clearTimeout(affixPollTimer)
                  affixPollTimer = null
                }
              } else {
                // 未获取到数据，继续轮询
                affixPollTimer = setTimeout(pollAffixData, affixPollInterval)
              }
            })
            .catch(error => {
              console.error('获取规则类型数据失败:', error)
              // 出错后继续轮询
              if (affixPollCount < maxAffixPollCount) {
                affixPollTimer = setTimeout(pollAffixData, affixPollInterval)
              }
            })
        }

        affixPollTimer = setTimeout(pollAffixData, 5000)
      }
    })
    .catch(error => {
      ElMessage.error("获取数据失败", error)
    })
    .finally(() => {
      loading.value = false
    })
}

const getUserList = (departmentId) => {
  GroupUserList({
    departmentId,
    pageSize: 100000
  }).then(res => {
    if (res.succ) {
      if (res.content && res.content.records) {
        userList.splice(0)
        res.content.records.forEach(user => userList.push(user))
      }
    }
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
    if (!is631UserAdmin.value) {
      fetchData()
    }
  },
  { deep: true }
)

watch(
  [() => departmentFilter],
  ([newDepartmentFilter]) => {
    if (is631UserAdmin.value && newDepartmentFilter.value && newDepartmentFilter.value[0]) {
      getUserList(newDepartmentFilter.value[0])
    }
  },
  { deep: true }
)

watch(
  [() => userList],
  ([newUserList]) => {
    if (is631UserAdmin.value) {
      userFilter.value = newUserList.map(data => data.id)
      fetchData()
    }
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
    flex-wrap: wrap;
    gap: 20px;
    justify-content: center;
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
