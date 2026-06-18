<template>
  <div
    v-loading.fullscreen.lock="loading"
    element-loading-text="数据加载中..."
    class="feedback"
  >
    <localTitle title="意见反馈列表">
      <template #rightBtn>
        <el-button type="primary" style="margin-left: 12px" @click="openModalAdd">新增</el-button>
        <!--        <el-button type="primary" icon="Tickets" style="margin-left: 12px" @click="gzlbFn">规则列表</el-button>-->
        <!-- <el-button type="primary" icon="ChatLineSquare" @click="yjfkFn"> 意见反馈 </el-button> -->
      </template>
    </localTitle>
    <div class="tableBox">
      <el-table
        ref="recentTable"
        class="upload-demo12"
        :data="tableData"
        row-key="id"
        style="width: 100%"
        height="100%"
        max-height="900px"
        border
      >
        <el-table-column
          label="序号"
          align="center"
          width="65"
        >
          <template #default="scope">
            <span>
              {{ (pages.pageNumber - 1) * pages.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          v-for="col in FEEDBACK_TABLE_CONFIG"
          :key="col.key"
          :prop="col.key"
          :label="col.label"
          :width="col.colWidth"
          :align="col.align ? col.align : 'center'"
        >
          <template #header>
            <div class="col-header">
              <span>{{ col.label }}</span>
            </div>
          </template>
          <template #default="scope">
            <span v-if="typeof col.formatter === 'function'">
              {{ col.formatter(scope.row) }}
            </span>
            <span v-else>
              {{ scope.row[col.key] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
            label="操作"
            width="200"
            align="center"
            fixed="right"
          >
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="openModalDetails(scope.row)"
              >
                查看
              </el-button>
              <el-button
                type="danger"
                size="small"
                :disabled="loginName != `admin` && loginName != `jzadmin` && loginUserId != scope.row.createUser"
                @click="deleteRow(scope.row)"
              >
                删除
              </el-button>
            </template>
        </el-table-column>
      </el-table>
      <div class="pagesBox">
        <el-pagination
          v-model:current-page="pages.pageNumber"
          v-model:page-size="pages.pageSize"
          layout="total, prev, pager, next, jumper, sizes"
          :page-sizes="[10, 20, 50, 100]"
          :total="pages.total"
          @current-change="getTableData"
          @size-change="getTableData"
        />
      </div>
    </div>
      <AddDialog
        v-if="showModalAdd"
        v-model:show-modal-add="showModalAdd"
        :add-from="addFrom"
        @refresh-data="refreshDataFn"
        @cancleFn="cancleFn"
      />
      <DetailsDialog
        v-if="showModalDetails"
        v-model:show-modal-details="showModalDetails"
        :current-row="currentRow"
        @refresh-data="getTableData"
      />
  </div>
</template>

<script setup>
  import { reactive } from 'vue'
  import AddDialog from './modals/AddDialog.vue'
  import DetailsDialog from './modals/DetailsDialog.vue'
  import localTitle from "../components/localTitle.vue"
  import { FEEDBACK_TABLE_CONFIG } from './constants.ts'
  import { getFeedbackData, deleteFeedback } from './api.ts'
  import store from "@/store/index"
  const route = useRoute()
  const router = useRouter()
  const currentRow = ref()
  const loading = ref(true)
  const tableData = reactive([])
  const pages = reactive({
    pageNumber: 1,
    pageSize: 50,
    total: 0,
  })

  const userInfo = computed(() => store.state.user.userInfo)
  const loginName = computed(() => userInfo.value?.loginName)
  const loginUserId = computed(() => userInfo.value?.id)

  const getTableData = () => {
    loading.value = true
    getFeedbackData({ ...pages })
    .then((res) => {
      if (res.succ) {
        tableData.splice(0)
        pages.total = res.content.total
        res.content.records.forEach((item) => {
          tableData.push({ ...item })
          if (item.id === currentRow.value?.id) {
            currentRow.value = { ...item }
          }
        })
      }
    })
    .catch((error) => {
      ElMessage.error('获取数据失败', error)
    })
    .finally(() => {
      loading.value = false
    })
  }
  getTableData()
  const queryData = ref(route.query.queryData)

  const refreshDataFn = ()=>{
    if(queryData.value){
      router.replace({query:{}})
      queryData.value = null
      addFrom.value = null
    }
    getTableData()
  }

  // 删除
  const deleteRow = (row) => {
    console.log('loginName', loginName.value)
    ElMessageBox.confirm(`确定删除${row.name ? row.name : ''}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    .then(() => {
      return deleteFeedback([row])
    })
    .then(() => {
      ElMessage.success('删除成功')
      getTableData()
    })
    .catch((err) => {
      if (err !== 'cancel') {
        // eslint-disable-next-line no-console
        console.error(err)
        ElMessage.error('删除失败')
      }
    })
  }
  // 弹窗管理
  const { proxy } = getCurrentInstance()
  const showModalAdd = ref(false)
  const cancleFn = ()=>{
    if(queryData.value){
      const data = proxy.getSession(`reviewResultData`)
      router.push({
          "path": `/sourceCodeReviewResults`,
          "query": {
            "item": encodeURIComponent(data)
          }
        })
    }else{
      showModalAdd.value = false
    }
  }
  const showModalDetails = ref(false)

  const allModalStates = [
    showModalAdd,
    showModalDetails,
  ]

  const closeAllModals = () => allModalStates.forEach(modalState => modalState.value = false)
  // 弹窗-添加
  const openModalAdd = () => {
    closeAllModals()
    currentRow.value = {}
    showModalAdd.value = true
  }

  // 弹窗-查看
  const openModalDetails = (row) => {
    closeAllModals()
    currentRow.value = row
    showModalDetails.value = true
  }

  const addFrom = ref(null)
  if (queryData.value) {
    try {
      // record=241888288291338&version=241863116283913&fileId=241863116271625
      console.log(route.query)
      const {record, version, fileId} = route.query
      addFrom.value = JSON.parse(queryData.value)
      if(record){
        addFrom.value.record = record
        addFrom.value.version = version
        addFrom.value.fileId = fileId
      }
      console.log(addFrom.value)
      closeAllModals()
      nextTick(()=>{
        showModalAdd.value = true
      })
    } catch (error) {
      console.error(error)
    }
   
  }

</script>

<style lang='scss' scoped>
  .feedback {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 0;
    overflow: hidden;
  }

  .controls {
    align-self: flex-end;
  }

  .tableBox {
    width: 100%;
    flex: 1;
    display: flex;
    flex-direction: column;

    :deep(.el-table) {
      background-color: transparent;
      flex: 1;
      min-height: 200px;
    }

    .col-header {
      text-align: center;
    }

    .pagesBox {
      padding: 0 10px;

      .el-pagination {
        margin: 0;
        padding: 7px 10px;
      }
    }
  }
</style>
<style  lang="scss" >
.upload-demo-file{
      .el-upload-list{
        .el-upload-list__item{
          .el-upload-list__item-name{
                width: 700px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }
          }
    }
  }
</style>