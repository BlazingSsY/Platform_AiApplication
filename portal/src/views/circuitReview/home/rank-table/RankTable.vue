<template>
  <div class="recent-table">
    <el-table
      ref="recentTable"
      :data="tableData"
      row-key="resultId"
      style="width: 100%"
      height="100%"
      border
    >
      <el-table-column
        label="排名"
        align="center"
        width="65"
      >
        <template #default="scope">
          <div
            class="rank-label"
            :style="`background-color: ${getRankBg(scope.$index + 1)}`"
          >
            {{ scope.$index + 1 }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        v-for="col in RANK_TABLE_CONFIG"
        :key="col.key"
        :prop="col.key"
        :label="col.label"
        :width="col.colWidth"
        align="center"
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
          <span v-else :title="scope.row[col.key]">
            {{ scope.row[col.key] }}
          </span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
  import { formatPercent } from '@/utils/formatter';
  import { RANK_TABLE_CONFIG } from './constants';

  const RANK_BG_MAP = {
    1: '#F07609',
    2: '#F09C09',
    3: '#F0B709'
  }

  const getRankBg = (rank) => RANK_BG_MAP[rank] ? RANK_BG_MAP[rank] : '#B4C1D1' 

  const props = defineProps({
    tableData: {
      type: Array,
      default: () => []
    }
  })
</script>

<style lang='scss' scoped>
  .recent-table {
    min-width: 450px;
    height: 100%;

    :deep(.el-table) {
      border-radius: 20px;
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
      height: 41px;
      background: #FFF;
    }

    :deep(.cell) {
      white-space: nowrap;
      overflow: hidden;
      overflow-wrap: break-word;
      text-overflow: ellipsis;
    }

    :deep(.el-table__empty-block) {
      height: 200px !important;;
    }
  }

  .rank-label {
    width: 22px;
    height: 22px;
    border-radius: 2px;
    color: #FFF;

    
    background: #F07609;
  }
</style>
