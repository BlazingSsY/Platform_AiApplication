<template>
  <div
    v-loading.fullscreen.lock="loading"
    element-loading-text="数据加载中..."
    class="feedback"
  >
    <div class="section">
      <div class="item-container">
        <div class="label">
          <div class="icon" />
          <div class="text">意见反馈列表</div>
        </div>
        <div class="control-panel">
          <div class="controls">
            <div class="control">
              <div class="label">单位</div>
              <el-select v-model="departmentFilter" placeholder="选择单位" style="width: 240px" clearable filterable :disabled="!isAdmin">
                <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
            <div class="control">
              <div class="label">提出人</div>
              <el-select v-model="userFilter" placeholder="选择提出人" style="width: 240px" clearable filterable :disabled="!departmentFilter">
                <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
            <div class="control">
              <div class="label">状态</div>
              <el-select v-model="statusFilter" placeholder="选择状态" style="width: 240px" clearable>
                <el-option label="新建开启" value="NEW_OPEN" />
                <el-option label="开启" value="OPEN" />
                <el-option label="重新开启" value="REOPEN" />
                <el-option label="关闭" value="CLOSED" />
              </el-select>
            </div>
            <div class="control-buttons">
              <el-button type="primary" @click="getTableData()"> 查询 </el-button>
              <el-button type="warning" @click="onReset()"> 重置 </el-button>
            </div>
          </div>
          <div class="right-buttons">
            <el-button type="primary" @click="openModalAdd"> 新增 </el-button>
          </div>
        </div>
      </div>
    </div>
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
  import { getDepartmentsSimpleItems } from '../statistics/api'
  import { GroupUserList } from '@/ajax/index'
  import store from "@/store/index"

  const route = useRoute()
  const router = useRouter()
  const currentRow = ref()
  const loading = ref(false)
  const loadingDepartments = ref(false)
  const tableData = reactive([])
  const pages = reactive({
    pageNumber: 1,
    pageSize: 50,
    total: 0,
  })

  const userInfo = computed(() => store.state.user.userInfo)
  const loginName = computed(() => userInfo.value?.loginName)
  const loginUserId = computed(() => userInfo.value?.id)
  const userDepartmentId = userInfo.value.departmentId
  const roleInfo = computed(() => userInfo.value?.role)
  const roleName = computed(() => {
    return roleInfo.value ? roleInfo.value.name : ``
  })

  // 筛选条件
  const departmentFilter = ref('')
  const userFilter = ref('')
  const statusFilter = ref('')
  const departmentsData = reactive([])
  const userList = reactive([])

  // 计算是否为管理员
  const isAdmin = computed(() => {
    return loginName.value === 'admin' || loginName.value === 'jzadmin'
  })

  const getTableData = () => {
    loading.value = true
    const params = { ...pages }
    if (departmentFilter.value) {
      params.departmentIds = [departmentFilter.value]
    }
    if (userFilter.value) {
      params.userIds = [userFilter.value]
    }
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    getFeedbackData(params)
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

  // 获取单位列表
  const getDepartments = () => {
    // 只有 admin 和 jzadmin 用户通过接口获取所有单位列表
    if (isAdmin.value) {
      loadingDepartments.value = true
      getDepartmentsSimpleItems({  })
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
    } else {
      // 其他用户直接使用当前登录用户的单位信息，并默认选中
      departmentsData.splice(0)
      const userDeptId = userInfo.value.departmentId
      const userDeptName = userInfo.value.departmentName || '当前单位'
      departmentsData.push({
        id: userDeptId,
        name: userDeptName
      })
      departmentFilter.value = userDeptId
      getUserList(userDeptId)
    }
  }

  // 获取用户列表
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

  // 重置筛选条件
  const onReset = () => {
    userFilter.value = ''
    statusFilter.value = ''
    pages.pageNumber = 1

    // 根据用户类型重置单位筛选
    if (isAdmin.value) {
      // 管理员清空单位筛选
      departmentFilter.value = ''
    } else {
      // 非管理员重置为当前用户单位
      departmentFilter.value = userInfo.value.departmentId
      getUserList(userInfo.value.departmentId)
    }

    getTableData()
  }

  getDepartments()

  // 监听单位筛选变化，更新用户列表
  watch(
    [() => departmentFilter],
    ([newDepartmentFilter]) => {
      // 清空用户筛选
      userFilter.value = ''
      // 如果选择了单位，获取该单位的用户列表
      if (newDepartmentFilter.value) {
        getUserList(newDepartmentFilter.value)
      }
    },
    { deep: true }
  )
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
          "path": `/reviewResults`,
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

  // 初始化查询数据
  onMounted(() => {
    if (queryData.value) {
      // 如果有新增数据，等待数据准备好后再查询
      nextTick(() => {
        getTableData()
      })
    } else {
      // 普通进入，直接查询
      getTableData()
    }
  })

</script>

<style lang='scss' scoped>
  .feedback {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 30px 0px;
    gap: 20px;
    overflow-y: auto;
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
      align-items: center;

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
      }

      .control-buttons {
        display: flex;
        flex-direction: row;
        gap: 12px;
      }
    }

    .right-buttons {
      align-self: flex-end;
    }
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