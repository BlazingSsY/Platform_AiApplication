<template>
  <div class="recent-table">
    <el-table
      ref="recentTable"
      :data="tableData"
      row-key="resultId"
      style="width: 100%"
      height="100%"
      border
      tooltip-effect="light" 
    >
      <el-table-column
        v-for="col in RECENT_TABLE_CONFIG"
        :key="col.key"
        :prop="col.key"
        :label="col.label"
        :min-width="col.colWidth"
        :align="col.label===`文件名称`?'left':'center'"
        show-overflow-tooltip
      >
        <template #header>
          <div class="col-header">
            <span>{{ col.label }}</span>
          </div>
        </template>
        <template #default="scope">
          <span v-if="col.key === 'reviewTime'">
            {{ formatTime(scope.row[col.key]) }}
          </span>
          <span v-else-if="col.key === 'passRate'">
            {{ formatPercent(scope.row[col.key]) }}
          </span>
          <span v-else-if="col.key === 'failCheckPoints'">
            {{ scope.row[col.key] != null && scope.row[col.key] !== '' ? scope.row[col.key] : 0 }}
          </span>
          <span v-else-if="col.key === 'fileName'" style="text-align:left">
            {{ scope.row[col.key] }}
          </span>
          <span v-else-if="col.key === 'isClosedLoop'" style="height: 23px;" :class="scope.row[col.key]===1?'ybh':'wbh'">
            {{ scope.row[col.key]===1?`问题已关闭`:`问题未关闭` }}
          </span>
          <span v-else :title="scope.row[col.key]">
            {{ scope.row[col.key] }}
          </span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
  import { RECENT_TABLE_CONFIG } from './constants';
  import { formatTime, formatPercent } from '@/utils/formatter'

  const ICON_MAP = {
    'IN_PROGRESS': 'icon-jinhangzhong',
    'FINISHED': 'icon-chenggong green'
  }

  const props = defineProps({
    tableData: {
      type: Array,
      default: () => []
    }
  })
</script>

<style lang='scss' scoped>
  .recent-table {
    min-width: 600px;
    height: 100%;

    :deep(.el-table) {
      border-radius: 20px;
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
        display: flex;
        justify-content: center;
        font-family: Microsoft YaHei;
        font-weight: 600;
        font-size: 15px;
        color: #333333;
      }
    }

    :deep(.el-table__body .el-table__cell) {
      height: 38px;
      background: #FFF;
    }

    :deep(.el-table__empty-block) {
      height: 200px !important;
    }
  }

  .green {
    color: rgb(51, 169, 84)
  }
</style>
