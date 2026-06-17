<template>
  <div class="recent-table">
    <div class="btnList">
      <div style="display: flex; align-items: center; margin:0 12px; gap: 8px;">
        <span style="font-size: 15px; color: #606266;">显示所有列</span>
        <el-switch v-model="showOwnerNameColumn" style="--el-switch-on-color: #67c23a; --el-switch-off-color: #dcdfe6; --el-switch-size: 20px;" size="large" />
      </div>
      <el-button v-if="isUserAdminOrJz||is631UserAdmin||isLeaderUserRole" type="primary" @click="exportData()"> 导出统计分析表 </el-button>
    </div>
    <el-table
      ref="recentTable"
      :data="tableData"
      row-key="id"
      style="width: 100%"
      border
    >
      <el-table-column
        label="序号"
        align="center"
        width="65"
      >
        <template #default="scope">
          <span>
            {{ scope.$index + 1 }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        v-for="col in tableConfig"
        :key="col.key"
        :prop="col.key"
        :label="col.label"
        :min-width="col.colWidth"
        :show-overflow-tooltip="col.label===`网表文件`"
        :align="col.label===`网表文件`?'left':'center'"
      >
        <template #header>
          <div class="col-header">
            <span>{{ col.label }}</span>
          </div>
        </template>
        <template #default="scope">
          <span v-if="col.key === 'passRate'">
            {{ formatPercent(scope.row[col.key]) }}
          </span>
          <span v-else-if="col.key === 'isClosedLoop'" style="height: 23px;" :class="scope.row[col.key]===1?'ybh':'wbh'">
            {{ scope.row[col.key]===1?`问题已关闭`:`问题未关闭` }}
          </span>
          <span v-else-if="col.key === 'exceedHalfMonthNotClosed'">
            {{ scope.row[col.key]===1?`是`:`否` }}
          </span>
          <span v-else-if="col.key===`compatibleModels`||col.key===`productModel`||col.key===`productName`||col.key===`diagramNumber`||col.key===`diagramVersion`">
            {{ scope.row[col.key]??`-` }}
          </span>
          <span v-else-if="col.key === 'failCheckPoints'||col.key === 'closedFailCheckPoints'">
            {{ scope.row[col.key]??`0` }}
          </span>
          <span v-else>{{ scope.row[col.key] }}</span>
        </template>
      </el-table-column>
    </el-table>

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
  import store from "@/store/index"
  import { formatPercent } from '@/utils/formatter'
  import { is631Admin } from '@/hooks/user631Admin'
  import { REVIEW_TABLE_CONFIG, REVIEW_TABLE_CONFIG_BY_USER } from './constants';
  import { statisticsExportTableExcel } from "../api"
  import { isAdminAndJz } from '@/hooks/userAdminAndJz'
import { isLeaderRole } from '@/hooks/userLeader'
const isLeaderUserRole = isLeaderRole()
  const isUserAdminOrJz = isAdminAndJz()
  const is631UserAdmin = is631Admin()
  const userInfo = computed(() => store.state.user.userInfo)
  const roleInfo = computed(() => userInfo.value?.role)
  const roleName = computed(() => {
      return roleInfo.value ? roleInfo.value.name : ''
  })



  const isByUser = computed(() => is631UserAdmin.value || roleName.value === '普通用户')

  const ICON_MAP = {
    'IN_PROGRESS': 'icon-jinhangzhong',
    'FINISHED': 'icon-chenggong green'
  }
   // 控制用户名列显示状态
  const recentTable = ref()
  const showOwnerNameColumn = ref(false)

  // 监听用户名列显示状态变化，强制表格重新渲染以修复背景色问题
  watch(() => showOwnerNameColumn.value, () => {
    nextTick(() => {
      recentTable.value?.doLayout()
    })
  })

  const listArr = [
    {
      key: 'compatibleModels',
      label: '配套机型',
      colWidth: 120,
    },
    {
      key: 'productModel',
      label: '产品型号',
      colWidth: 120,
    },
    {
      key: 'productName',
      label: '产品名称',
      colWidth: 120,
    },
    {
      key: 'diagramNumber',
      label: '电路原理图号',
      colWidth: 140,
    },
    {
      key: 'diagramVersion',
      label: '版本',
      colWidth: 100,
    },
  ]

  /**
   * 管理员视角：将"单位"列的插入移到 if(showOwnerNameColumn.value) 外层，现在无论 showOwnerNameColumn 的值如何，"单位"列都会一直显示在第 2 列位置
   * 普通用户视角：保持不变，不显示"单位"列
   */
  const tableConfig = computed(() => {
    let list = []
    if(isUserAdminOrJz.value){
      list = [...REVIEW_TABLE_CONFIG_BY_USER]
      list.splice(1,0,{
        key: 'departmentName',
        label: '单位',
        colWidth: 130,
      })
      if(showOwnerNameColumn.value){
        list.splice(2,0,{
          key: 'ownerName',
          label: '用户',
          colWidth: 130,
        })
        list.splice(3,0,...listArr)
      }
    }else{
      list = [...REVIEW_TABLE_CONFIG]
      if(showOwnerNameColumn.value){
        list.splice(1,0,{
          key: 'ownerName',
          label: '用户',
          colWidth: 130,
        })
        list.splice(2,0,...listArr)
      }
    }
    return list
  })


  const props = defineProps({
    tableData: {
      type: Array,
      default: () => []
    },
    listParams: {
      type: Object,
      default: () => ({})
    }
  })

 const dialogVisible = ref(false)
const expotDateRangeFilter = ref([])
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}
const exportData = ()=>{
  statisticsExportTableExcel(props.listParams).then(res=>{
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
const exportLoading = ref(false)
const exportSend = ()=>{
  exportLoading.value = true
  statisticsExportTableExcel({
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
  
</script>

<style lang='scss' scoped>
  .recent-table {
    min-width: 600px;
    height: 100%;

    .btnList{
      padding-bottom: 10px;
      display: flex;
      justify-content: flex-end;
    }

    :deep(.el-table) {
      border-radius: 20px;
      height: calc(100% - 50px);
      .wbh{
        color: #E39E25;
        font-size: 13px;
      }
      .ybh{
        padding: 0;
        color: #22C9A8;
        font-size: 13px;
      }
    }

    :deep(.el-table__header .el-table__cell) {
      background: #D7E8FB;
      .col-header {
        font-family: Microsoft YaHei;
        font-weight: 600;
        font-size: 15px;
        color: #333333;
      }
    }

    :deep(.el-table__body .el-table__cell) {
      height: 38px;
      background: #FFF;
      // opacity: 0.6;
    }

    :deep(.cell) {
      white-space: nowrap;
      overflow: hidden;
      overflow-wrap: break-word;
      text-overflow: ellipsis;
    }

    :deep(.el-table__empty-block) {
      height: 200px !important;
    }
  }

  .green {
    color: rgb(51, 169, 84)
  }
</style>
