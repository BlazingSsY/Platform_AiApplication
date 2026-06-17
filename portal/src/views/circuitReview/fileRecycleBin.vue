<script setup>
import localTitle from "./components/localTitle.vue"
import { recyclePage, circuitFileIsRecycle, deleteCircuitFile } from "@/ajax/circuitreview"
const loading = ref(false)
const tableList = reactive([])
// 分页相关参数
const pagination = ref({
  "current": 1,
  "size": 10,
  "total": 0
})
const initTableList = () => {
  loading.value = true
  recyclePage({ pageNumber: pagination.value.current, pageSize: pagination.value.pageSize })
    .then(res => {
      loading.value = false
      if (res.succ) {
        tableList.splice(0)
        res.content.records.forEach(r => {
          tableList.push(r)
        })
        pagination.value.total = res.content.total
      }
    })
    .catch(() => {
      loading.value = false
    })
}
initTableList()

const multipleSelection = ref([])
const handleSelectionChange = val => {
  multipleSelection.value = val
}

const delFn = row => {
  ElMessageBox.confirm(`确定删除?`, `提示`, {
    "confirmButtonText": `确定`,
    "cancelButtonText": `取消`,
    "type": `warning`
  }).then(() => {
    let delData = []
    if (row) {
      delData.push(row)
    } else {
      delData = multipleSelection.value
    }
    deleteCircuitFile(delData).then(res => {
      if (res.succ) {
        ElMessage.success(`删除文件成功！`)
        initTableList()
      }
    })
  })
}
const restoreFn = row => {
  ElMessageBox.confirm(`确定恢复?`, `提示`, {
    "confirmButtonText": `确定`,
    "cancelButtonText": `取消`,
    "type": `warning`
  }).then(() => {
    let restoreData = []
    if (row) {
      restoreData.push(row.id)
    } else {
      restoreData = multipleSelection.value.map(r => r.id)
    }
    circuitFileIsRecycle({
      fileIdList: restoreData,
      isRecycle: 0
    }).then(res => {
      if (res.succ) {
        ElMessage.success(`恢复文件成功！`)
        initTableList()
      }
    })
  })
}

const batchDel = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning(`请勾选想要删除的文件！`)
    return
  }
  delFn()
}
const batchRestore = () => {
  console.log(multipleSelection.value)
  if (multipleSelection.value.length === 0) {
    ElMessage.warning(`请勾选想要恢复的文件！`)
    return
  }
  restoreFn()
}
</script>
<template>
  <div v-loading.fullscreen.lock="loading" class="fileRecycleBin" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
    <localTitle title="网表文件回收站">
      <template #rightBtn>
        <el-button type="primary" icon="Refresh" :disabled="multipleSelection.length === 0" @click="batchRestore()">批量恢复</el-button>
        <el-button type="danger" icon="Delete" :disabled="multipleSelection.length === 0" style="margin-left: 12px" @click="batchDel()">批量彻底删除</el-button>
      </template>
    </localTitle>
    <div class="tableBox">
      <el-table :data="tableList" tooltip-effect="light" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="序号" align="center" width="80" type="index" />
        <el-table-column prop="fileName" label="网表文件" align="left" show-overflow-tooltip sortable="custom" />
        <el-table-column prop="updateDate" label="回收时间" align="center" min-width="140px" show-overflow-tooltip sortable="custom" />

        <el-table-column label="操作" align="center" width="200px" class-name="opt">
          <template #default="scope">
            <el-button class="btn" type="primary" size="small" @click="restoreFn(scope.row)"> 恢复 </el-button>
            <el-button type="danger" size="small" class="btn" @click="delFn(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="initTableList"
          @size-change="initTableList"
        />
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.fileRecycleBin {
  display: flex;
  flex-direction: column;
  .tableBox {
    flex: 1;
    .pagination-container {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      background-color: transparent;
      border-top: 1px solid #ebeef5;
    }

    :deep(.el-table) {
      background-color: transparent;
      height: calc(100% - 60px);
      .opt {
        .btn {
          height: 28px;
          line-height: 28px;
          padding: 0 4px;
          min-width: 70px;
          text-align: center;

          .el-loading-mask {
            .el-loading-spinner {
              height: 28px;
              margin: 0;
              top: 0;

              .circular {
                width: 30px;
                height: 30px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
