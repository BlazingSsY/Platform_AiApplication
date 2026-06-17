<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import localTitle from "./components/localTitle.vue"
  
  const router = useRouter()
  const loading = ref(false)
  const tableList = reactive<any[]>([])
  const multipleSelection = ref<any[]>([])
  
  // 分页相关参数
  const pagination = ref({
    current: 1,
    size: 10,
    total: 0
  })
  
  // 获取回收站文件列表
  const initTableList = async () => {
    loading.value = true
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 模拟数据
      tableList.splice(0)
      const mockData = [
        {
          id: 1,
          fileName: "main.js",
          fileType: "JavaScript",
          fileSize: "2.5KB",
          deleteTime: "2024-01-15 10:30:00",
          deleteBy: "张三"
        },
        {
          id: 2,
          fileName: "utils.py",
          fileType: "Python",
          fileSize: "1.8KB",
          deleteTime: "2024-01-14 14:20:00",
          deleteBy: "李四"
        },
        {
          id: 3,
          fileName: "config.json",
          fileType: "JSON",
          fileSize: "0.5KB",
          deleteTime: "2024-01-13 09:15:00",
          deleteBy: "王五"
        }
      ]
      
      mockData.forEach(item => tableList.push(item))
      pagination.value.total = mockData.length
    } catch (error) {
      ElMessage.error("获取文件列表失败")
    } finally {
      loading.value = false
    }
  }
  
  // 选择变化
  const handleSelectionChange = (val: any[]) => {
    multipleSelection.value = val
  }
  
  // 删除文件
  const delFn = (row?: any) => {
    ElMessageBox.confirm(`确定删除?`, `提示`, {
      confirmButtonText: `确定`,
      cancelButtonText: `取消`,
      type: `warning`
    }).then(() => {
      let delData = []
      if (row) {
        delData.push(row)
      } else {
        delData = multipleSelection.value
      }
      
      // 模拟删除
      ElMessage.success(`删除文件成功！`)
      initTableList()
    })
  }
  
  // 恢复文件
  const restoreFn = (row?: any) => {
    ElMessageBox.confirm(`确定恢复?`, `提示`, {
      confirmButtonText: `确定`,
      cancelButtonText: `取消`,
      type: `warning`
    }).then(() => {
      let restoreData = []
      if (row) {
        restoreData.push(row.id)
      } else {
        restoreData = multipleSelection.value.map(r => r.id)
      }
      
      // 模拟恢复
      ElMessage.success(`恢复文件成功！`)
      initTableList()
    })
  }
  
  // 批量删除
  const batchDel = () => {
    if (multipleSelection.value.length === 0) {
      ElMessage.warning(`请勾选想要删除的文件！`)
      return
    }
    delFn()
  }
  
  // 批量恢复
  const batchRestore = () => {
    if (multipleSelection.value.length === 0) {
      ElMessage.warning(`请勾选想要恢复的文件！`)
      return
    }
    restoreFn()
  }
  
  // 分页变化
  const handleCurrentChange = (page: number) => {
    pagination.value.current = page
    initTableList()
  }
  
  // 返回首页
  const goToHome = () => {
    router.push('/codeReviewHome')
  }
  
  // 开始审查
  const goToReview = () => {
    router.push('/sourceCodeDocumentReview')
  }
  
  onMounted(() => {
    initTableList()
  })
  </script>
  <template>
    <div
        v-loading.fullscreen.lock="loading"
        class="source-code-file-recycle-bin"
        element-loading-background="rgba(122, 122, 122, 0.8)"
        element-loading-text="数据加载中..."
    >
      <localTitle title="代码文件回收站">
        <template #rightBtn>
          <el-button
              type="primary"
              icon="Refresh"
              :disabled="multipleSelection.length === 0"
              @click="batchRestore()"
          >
            批量恢复
          </el-button>
          <el-button
              type="danger"
              icon="Delete"
              :disabled="multipleSelection.length === 0"
              @click="batchDel()"
          >
            批量删除
          </el-button>
        </template>
      </localTitle>
  
      <div class="content">
        <div class="header">
          <h2>代码文件回收站</h2>
          <p>这里显示已删除的代码文件，您可以恢复或永久删除这些文件。</p>
        </div>
  
        <div class="table-container">
          <el-table
            class="custom-table-size"
              :data="tableList"
              @selection-change="handleSelectionChange"
              style="width: 100%"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="fileName" label="文件名" min-width="200" />
            <el-table-column prop="fileType" label="文件类型" width="120" />
            <el-table-column prop="fileSize" label="文件大小" width="120" />
            <el-table-column prop="deleteTime" label="删除时间" width="180" />
            <el-table-column prop="deleteBy" label="删除者" width="120" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button
                    type="primary"
                    size="small"
                    @click="restoreFn(scope.row)"
                >
                  恢复
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    @click="delFn(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
  
          <div class="pagination">
            <el-pagination
                v-model:current-page="pagination.current"
                v-model:page-size="pagination.size"
                :page-sizes="[10, 20, 50, 100]"
                :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="handleCurrentChange"
            />
          </div>
        </div>
  
        <div class="actions">
          <el-button type="primary" @click="goToHome">返回首页</el-button>
          <el-button type="success" @click="goToReview">开始审查</el-button>
        </div>
      </div>
    </div>
  </template>
  <style lang="scss" scoped>
  .source-code-file-recycle-bin {
    display: flex;
    flex-direction: column;
    height: 100vh;
  
    .content {
      flex: 1;
      padding: 20px;
  
      .header {
        text-align: center;
        margin-bottom: 30px;
  
        h2 {
          color: #303133;
          margin-bottom: 15px;
        }
  
        p {
          color: #606266;
          font-size: 16px;
        }
      }
  
      .table-container {
        background: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        padding: 20px;
        margin-bottom: 30px;
  
        .pagination {
          margin-top: 20px;
          text-align: right;
        }
      }
  
      .actions {
        display: flex;
        gap: 20px;
        justify-content: center;
      }
    }
  }
  </style>
  