<script setup >
  import localTitle from "../components/localTitle.vue"
  import {
    // circuitReviewResultAuditPage,
    circuitReviewResultAuditPageForAdmin,
    circuitReviewResultAuditPageForExpert
  } from "@/ajax/circuitreview"
  import { GroupUserList, getDepartmentsSimpleItems } from "@/ajax/index"
  import store from "@/store/index"
  import { computed } from "vue"
  const isAdmin = computed(() => store.state.user?.userInfo?.role?.name===`管理员`)
  const isExpert = computed(() => store.state.user?.userInfo?.role?.name===`专家`)
  console.log('isAdmin',isAdmin.value, 'isExpert', isExpert.value)
  const userInfo = computed(() => store.state.user.userInfo)
  const roleInfo = computed(() => userInfo.value?.role)
  const roleName = computed(() => {
    // name 普通用户
    return roleInfo.value ? roleInfo.value.name : ``
  })

  const userDepartmentId = computed(() => userInfo.value?.departmentId)

  // 控制用户名列显示状态 - 管理员模式显示
  const showOwnerNameColumn = computed(() => isAdmin.value)

  const fileslistDataAll = ref([])

  // 单位和用户数据
  const departmentsData = reactive([])
  const usersData = ref([])

  const searchObj = reactive({
    fileName:``,
    depId: ``,
    userId: ``
  })
  // 分页相关参数
  const pagination = ref({
    "current": 1,
    "size": 100,
    "total": 0
  })
  

  const route = useRoute()
  if (store.state.pagesInfoStroe && store.state.pagesInfoStroe[route.path]) {
    pagination.value.size = store.state.pagesInfoStroe[route.path].size
    pagination.value.current = store.state.pagesInfoStroe[route.path].current
    if (store.state.pagesInfoStroe[route.path].searchObj) {
      searchObj.fileName = store.state.pagesInfoStroe[route.path].searchObj.fileName ?? ``
      searchObj.depId = store.state.pagesInfoStroe[route.path].searchObj.depId ?? ``
      searchObj.userId = store.state.pagesInfoStroe[route.path].searchObj.userId ?? ``
    }
  }

  // 获取单位列表
  const getDepartments = () => {
    getDepartmentsSimpleItems({ departmentId: userDepartmentId.value })
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
  }
  getDepartments()

  // 单位变化时获取用户列表
  const depChange = () => {
    const params_ = {
      "departmentId": searchObj.depId
    }
    params_.pageSize = 10000
    GroupUserList(params_).then(res => {
      if (res.succ) {
        if (res.content && res.content.records) {
          usersData.value = res.content.records
        }
      }
    })
  }

  const loading = ref(false)
  const loadingType = ref(1)
  
  const sortStroe = reactive({
    sortField: ``,
    sortDirection: ``
  })
  const tableRef = ref()
  // 获取电路图文件列表
  const fetchCircuitFiles = async (page = 1, pageSize = 10) => {
    const params = {
      ...searchObj,
      ...sortStroe,
      "pageNumber": page,
      pageSize
    }
  
    const res = isAdmin.value ? await circuitReviewResultAuditPageForAdmin(params) : await circuitReviewResultAuditPageForExpert(params)
    if (res.succ) {
      // 直接使用新的数据结构
      fileslistDataAll.value = res.content.records
      // 更新分页信息
      pagination.value = {
        "current": res.content.current,
        "size": res.content.size,
        "total": res.content.total
      }
    }
  }
  
  const initData = async () => {
    loadingType.value = 1
    loading.value = true
    await fetchCircuitFiles(pagination.value.current, pagination.value.size)
    loading.value = false
  }
  onMounted(()=>{
    initData()
  })
  
  // 审查记录
  
  const handleCurrentChange = (page) => {
    pagination.value.current = page
    fetchCircuitFiles(page, pagination.value.size)
    nextTick(() => {
      document.getElementById(`layoutWrap`).scrollTo({
        top: 50,
        behavior: "smooth"
      })
    })
  }
  
  const handleSizeChange = (size) => {
    pagination.value.size = size
    pagination.value.current = 1 // 切换每页大小时重置到第一页
    fetchCircuitFiles(1, size)
    nextTick(() => {
      document.getElementById(`layoutWrap`).scrollTo({
        top: 50,
        behavior: "smooth"
      })
    })
  }
  
  const restFn = () => {
    searchObj.fileName = ``
    searchObj.depId = ``
    searchObj.userId = ``
    searchObj.status = ``
    usersData.value = []
    initData()
  }
  /**
   * 
   * FILE_NAME("fileName", "文件名"),
    DEPARTMENT_NAME("departmentName", "部门名称"),
    OWNER_NAME("ownerName", "负责人姓名"),
    STATUS("status", "状态"),
    AUDIT_TIME("auditTime", "复核时间");
   */
  const sortObj = {
    "fileName": `FILE_NAME`,
    "diagramNumber": `DIAGRAM_NUMBER`,
    "departmentName": `DEPARTMENT_NAME`,
    "ownerName": `OWNER_NAME`,
    "status": `STATUS`,
    "auditSubmitTime": `AUDIT_TIME`
  }
  // 表格排序处理
  const handleSortChange = ({ prop, order }) => {
    if (order) {
      sortStroe.sortField = sortObj[prop] ?? ``
      sortStroe.sortDirection = order === `ascending` ? `ASC` : order === `descending` ? `DESC` : ``
    } else {
      sortStroe.sortField = ``
      sortStroe.sortDirection = ``
    }
  
    initData()
  }
    //   查看详情
    const router = useRouter()
    const detailFn = (id,fileName,fileVersionName,auditSubmitTime,fileId,fileVersionId,isRecycle)=>{
        console.log('fileName',fileName, 'fileVersionName',fileVersionName, 'fileId',fileId, 'fileVersionId',fileVersionId,'isRecycle',isRecycle)
        // 通过路由参数传递数据，而不是使用localStorage      
        store.commit("SET_PAGES_INFO_STROE", { path: route.path, info: { ...pagination.value, searchObj } })
        router.push({
          path: `/reviewDetail`,
          query: {
            id: id,
            fileName: fileName,
            fileVersionName: fileVersionName,
            auditSubmitTime: auditSubmitTime,
            fileId: fileId,
            fileVersionId: fileVersionId,
            isRecycle: isRecycle
          }
        })
    }
  // 查看版本
  const rowData = ref(null)
  const viewVersionShow = ref(false)
  const viewVersionListData = ref([])
  const listRecordFn = (row)=>{
    rowData.value = row
    viewVersionListData.value = row.resultAuditVOList
    viewVersionShow.value = true
  }
  
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="documentReview" element-loading-background="rgba(122, 122, 122, 0.8)" :element-loading-text="loadingType === 1 ? `数据加载中...` : `文件上传中...`">
      <localTitle title="问题复核" />
      <div class="tableBox">
        <div class="search">
          <el-form ref="ruleFormRef" :inline="true">
            <el-form-item label="网表文件名">
              <el-input v-model="searchObj.fileName" placeholder="输入网表文件名" style="width: 240px" clearable />
            </el-form-item>
            <el-form-item v-if="roleName && roleName !== `普通用户`" label="单位">
              <el-select v-model="searchObj.depId" placeholder="选择单位" style="width: 240px" clearable @change="depChange">
                <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item v-if="roleName && roleName !== `普通用户`" label="用户">
              <el-select v-model="searchObj.userId" :disabled="searchObj.depId === ``" placeholder="选择用户" style="width: 240px" clearable>
                <el-option v-for="item in usersData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchObj.status" placeholder="请选择状态" style="width: 240px">
                <el-option label="全部状态" value="" />
                <el-option label="待复核" value="0" />
                <el-option label="已复核" value="1" />
              </el-select>
            </el-form-item>
          </el-form>
          <div>
            <el-button type="primary" @click="initData()">搜索</el-button>
            <el-button @click="restFn">重置</el-button>
          </div>
        </div>

        <div class="tableWrapper">
          <el-table
            border
            ref="tableRef"
            class="custom-table-size"
            :data="fileslistDataAll"
            :tooltip-options="{ placement: 'bottom' }"
            :fit="true"
            table-layout="fixed"
            style="width: 100%"
            :style="{ height: roleName && roleName == `普通用户` ? ` calc(100% - 60px)` : ` calc(100% - 160px)`, width: '100%' }"
            :header-cell-style="{ textAlign: 'center' }"
            @sort-change="handleSortChange"
          >
            <el-table-column label="序号" align="center" width="64">
              <template #default="scope">
                {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="fileName" label="网表文件" align="left" show-overflow-tooltip sortable="custom" min-width="260px" />
            <el-table-column prop="diagramNumber" label="电路原理图号" align="center" min-width="120px" sortable="custom" show-overflow-tooltip>
              <template #default="scope">
                {{ scope.row.diagramNumber || `-` }}
              </template>
            </el-table-column>
            <el-table-column prop="departmentName" label="单位名称" align="center" min-width="100px" show-overflow-tooltip sortable="custom" />           
            <el-table-column prop="status" label="状态" align="center" min-width="80px" sortable="custom">
              <template #default="scope">
                {{ (isAdmin ? scope.row.isAdminAuditFinished : scope.row.isExpertAuditFinished ) === 1 ? `已复核` : `待复核` }}
              </template>
            </el-table-column>
            <!-- <el-table-column prop="isAdminAuditFinished" label="admin状态" align="center" min-width="100px" sortable="custom">             
            </el-table-column>
            <el-table-column prop="isExpertAuditFinished" label="expert状态" align="center" min-width="100px" sortable="custom">              
            </el-table-column> -->
            <el-table-column prop="auditSubmitTime" label="复核提交时间" align="center" min-width="160px" sortable="custom">
              <template #default="scope">
                {{ scope.row.auditSubmitTime || `-` }}
              </template>
            </el-table-column>
            <el-table-column prop="ownerName" label="隶属人" align="center" min-width="80px" sortable="custom">
              <template #default="scope">
                {{ scope.row.ownerName || `-` }}
              </template> 
            </el-table-column>
            <el-table-column label="操作" align="center" min-width="100" class-name="opt" fixed="right">
              <template #default="scope">                                          
                <el-button type="primary" size="small" class="btn" @click="detailFn(scope.row.resultAuditId,scope.row.fileName,scope.row.fileVersionName,scope.row.auditSubmitTime,scope.row.fileId,scope.row.fileVersionId,scope.row.isRecycle)"> 查看详情 </el-button>
                <el-button type="primary" size="small" class="btn" @click="listRecordFn(scope.row)"> 所有记录 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
  
        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>

      <!-- 所有记录 -->
      <el-dialog v-model="viewVersionShow" class="dialogCont dialogCont4" title="" :show-close="false" width="1200px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">复核记录</div>
          </div>
        </template>
        <div class="contBox">
          <div class="title">
            电路图文件：<span>{{ rowData.fileName }}</span>
          </div>
          <div class="tableCont">
            <el-table ref="rulesListRef" border tooltip-effect="light" :data="viewVersionListData" height="100%" max-height="500px" >
              <el-table-column prop="fileVersionName" label="版本" align="center" width="400px">
                <template #default="scope">
                  {{ scope.row.fileVersionName }}
                </template>
              </el-table-column>
               <el-table-column prop="auditTime" label="复核时间" align="center" min-width="160px">
                <template #default="scope">
                  {{ scope.row.auditTime || `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="isAuditFinished" label="状态" align="center" min-width="100px" sortable="custom">               
                <template #default="scope">
                  {{ (isAdmin ? scope.row.isAdminAuditFinished : scope.row.isExpertAuditFinished ) === 1 ? `已复核` : `待复核` }}
                </template>           
              </el-table-column>
              <el-table-column label="操作" align="center" width="120" class-name="opt" fixed="right">
                  <template #default="scope">
                      <el-button type="primary" size="small" class="btn" @click="detailFn(scope.row.id,rowData.fileName,scope.row.fileVersionName,scope.row.auditTime,rowData.fileId,rowData.fileVersionId,rowData.isRecycle)"> 查看详情 </el-button>
                  </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right" />
            <div class="btn">
              <el-button @click="viewVersionShow = false"> 关闭</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <style lang="scss" scoped>
  .documentReview {
    display: flex;
    flex-direction: column;
    .tableBox {
      flex: 1;
      .tableWrapper {
        width: 100%;
        overflow-x: auto;
      }
      .tableWrapper :deep(.el-table) {
        width: 100% !important;
        min-width: 1200px;
      }
      .tableWrapper :deep(.el-table__inner-wrapper),
      .tableWrapper :deep(.el-table__header-wrapper table),
      .tableWrapper :deep(.el-table__body-wrapper table) {
        width: 100% !important;
      }
      .search {
        height: 100px;
        padding: 0 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #fff;
        :deep(.el-form) {
          .el-form-item {
            margin-bottom: 0;
            margin-right: 20px;
            .el-form-item__label {
              font-size: 15px;
              padding-right: 10px;
            }
            .el-input,
            .el-input__inner {
              height: 32px;
              line-height: 32px;
            }
          }
        }
      }
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
        height: calc(100vh - 160px);
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
            &.btnNoHover {
              cursor: default;
              &.is-disabled {
                background-color: #28dad7 !important;
                color: #fff !important;
                border: 1px solid #28dad7 !important;
                cursor: not-allowed;
                border: 1px solid rgb(217, 217, 217) !important;
                &:hover {
                  background-color: #28dad7 !important;
                  color: #fff !important;
                  border: 1px solid #28dad7 !important;
                }
              }
            }
          }
          .wranTooltip {
            color: #8b8b8b;
            height: 28px;
            line-height: 28px;
            padding-left: 8px;
          }
  
          .el-popper {
            &.is-light {
              border: 1px solid #e4e7ed;
              background: #fff;
              color: #606266;
            }
          }
          .upload-demo12 {
            width: 85px;
            display: inline-block;
            margin-left: 12px;
          }
        }
        .el-tooltip__trigger{
          margin-right: 0;
          margin-left: 0px;
          vertical-align: middle;
        }
      
        .el-dropdown{
          margin-left: 5px;
          .el-dropdown-link{
            display: flex;
            align-items: center;
            justify-content: center;
            height: 28px;
            line-height: 28px;
            cursor: pointer;
          }
  
        }
        .el-icon{
          margin-right: 0;
        }
      }
      //:deep(.btnXZ){
      //  width: 76px !important;
      //}
    }
  
    :deep(.dialogCont) {
      padding: 0;
      border: 0;
      background: transparent;
      margin-top: 35vh;
  
      .el-dialog__header {
        padding: 0;
        height: 54px;
  
        .my-header {
          padding: 0 20px;
          height: 54px;
          line-height: 54px;
          background: #e0ecfa;
          display: flex;
          align-items: center;
          justify-content: space-between;
          background-size: contain;
          font-size: 16px;
          font-weight: 500;
          .status {
            color: #2487ff;
            text-align: center;
          }
        }
      }
  
      .el-dialog__body {
        padding: 0;
        // height: 184px;  162
        height: 346px;
  
        .contBox {
          height: 100%;
          background-color: #fff;
          position: relative;
          padding: 10px 20px 0;
          .tableCont {
            height: 184px;
            .el-table__inner-wrapper {
              background-color: #f5f5f5;
  
              .el-table__header-wrapper {
                background-color: transparent;
                display: flex;
                flex-direction: column;
                margin-bottom: 0px;
  
                .el-table__header {
                  tr {
                    height: 40px;
  
                    th {
                      font-size: 15px;
                      background: rgba(157, 175, 195, 0.3);
                      line-height: 40px;
                      color: #333;
                    }
                  }
                }
              }
  
              .el-table__body-wrapper {
                background-color: transparent;
  
                .el-table__body {
                  tbody {
                    background-color: #ffffff;
                  }
  
                  .el-table__row {
                    height: 40px;
                    background: #ffffff;
  
                    td {
                      line-height: 40px;
                      color: #333;
                    }
  
                    &:nth-child(2n + 1) {
                      background: rgba(157, 175, 195, 0.1);
                    }
  
                    &:last-child {
                      margin: 0;
  
                      td {
                        border-bottom: none;
                      }
                    }
                  }
                }
  
                .status {
                  .cell {
                    color: #bbbbbb;
                  }
  
                  .dot,
                  .dot > div {
                    position: relative;
                    box-sizing: border-box;
                  }
  
                  .dot {
                    display: flex;
                    align-items: center;
                    font-size: 0;
                    color: #bbbbbb;
                    position: absolute;
                    right: 4px;
                    top: 5px;
                  }
  
                  .dot.la-dark {
                    color: #bbbbbb;
                  }
  
                  .dot > div {
                    display: inline-block;
                    float: none;
                    background-color: currentColor;
                    border: 0 solid currentColor;
                  }
  
                  .dot {
                    width: 30px;
                    height: 18px;
                  }
  
                  .dot > div {
                    width: 4px;
                    height: 4px;
                    margin: 1px;
                    border-radius: 100%;
                    animation: ball-beat 0.7s -0.15s infinite linear;
                  }
  
                  .dot > div:nth-child(2n-1) {
                    animation-delay: -0.5s;
                  }
  
                  @keyframes ball-beat {
                    50% {
                      opacity: 0.2;
                      transform: scale(0.75);
                    }
  
                    100% {
                      opacity: 1;
                      transform: scale(1);
                    }
                  }
                }
  
                .tg {
                  color: #19bb5a;
                }
  
                .notg {
                  color: #de4848;
                }
  
                .bsy {
                  color: #d69d15;
                }
  
                .pszb {
                  position: relative;
                }
              }
            }
          }
          .numInfo {
            display: flex;
            justify-content: center;
            margin-top: 28px;
            > div {
              flex: 1;
              height: 100px;
              background: linear-gradient(123deg, #b5e2f0, #c6e5f6);
              box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
              border-radius: 20px;
              padding: 24px;
              display: flex;
              align-items: center;
              margin-right: 12px;
              .left {
                width: 42px;
                > img {
                  width: 100%;
                  height: auto;
                }
              }
              .value {
                display: flex;
                flex-direction: column;
                padding-left: 20px;
                span {
                  font-size: 26px;
                  color: #45bfe2;
                  line-height: 36px;
                }
                .label {
                  font-size: 15px;
                  color: #3a4c5f;
                  line-height: 23px;
                }
              }
  
               
              &:nth-child(2) {
                background: linear-gradient(123deg, #c7d6f5, #cde4fb);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #6299f5;
                }
              }
              &:nth-child(3) {
                background: linear-gradient(123deg, #c1d9f4, #c7e0f9);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #7e71ff;
                }
              }
              &:nth-child(4) {
                background: linear-gradient(123deg, #f3f1ed, #f1e6ce);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #d9a732;
                }
              }
              &:nth-child(5) {
                margin-right: 0;
                background: linear-gradient(123deg, #eeddbc, #eee2c2);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #d9a732;
                }
              }
            }
          }
        }
      }

      &.progressDialog {
        margin-top: 15vh;
        .el-dialog__body {
          height: 60vh;
        }

        .contBox {
          display: flex;
          flex-direction: column;

          .tableCont {
            flex: 1;
            height: auto;
            min-height: 260px;
          }
        }
      }
  
      &.dialogCont1 {
        margin-top: 4vh;
  
        .el-dialog__body {
          padding: 0;
          height: 750px;
  
          .contBox {
            .tableCont {
              padding-left: 60px;
              padding-top: 10px;
            }
  
            .upload-demo {
              width: 400px;
            }
          }
        }
      }
  
      &.dialogCont2 {
        .el-dialog__body {
          padding: 0;
          height: 750px;
  
          .contBox {
            .tableCont {
              padding-left: 0;
              padding-top: 10px;
              .statusBox {
                .green,
                .red {
                  display: inline-block;
                  width: 10px;
                  height: 10px;
                  border-radius: 50%;
                  background-color: #18c3ad;
                }
                .red {
                  background-color: #f3a41c;
                }
              }
            }
          }
        }
      }
      &.dialogCont3 {
        margin-top: 30vh;
        .el-dialog__body {
          padding: 0;
          height: 200px;
          .el-form {
            padding-top: 20px;
            .upload-demo {
              width: 100%;
            }
          }
        }
      }
      &.dialogCont4 {
        margin-top: 20vh;
        .el-dialog__body {
          padding: 0;
          max-height: 800px;
          .contBox {
            height: 100%;
            background-color: #fff;
            position: relative;
            padding: 0 20px 0;
            display: flex;
            flex-direction: column;
            .title {
              font-weight: 400;
              font-size: 15px;
              color: #333333;
              line-height: 40px;
              position: relative;
              padding-left: 14px;
              > span {
                color: #3186e9;
              }
              &::before {
                content: " ";
                width: 6px;
                height: 14px;
                background: #3186e9;
                position: absolute;
                left: 0;
                top: 13px;
              }
            }
            .tableCont {
              height: calc(100% - 40px);
            }
          }
        }
      }
      &.dialogCont5{
        margin-top: 15vh;
        .el-dialog__body {
          padding: 0;
          max-height: 800px;
          height: 600px;
         }
        .contBox{
          height: 100%;
          display: flex;
          flex-direction: column;
          .search {
            padding: 10px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #fff;
            .el-form {
              .el-form-item {
                margin-bottom: 0;
                margin-right: 20px;
                .el-form-item__label {
                  font-size: 15px;
                  padding-right: 10px;
                }
                .el-input,
                .el-input__inner {
                  height: 32px;
                  line-height: 32px;
                }
              }
            }
          }
          .btnList{
            padding: 7px 0px 14px;
            }
          .tableCont{
            flex: 1;
          }
          .pagination-container {
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: flex-end;
            background-color: transparent;
            border-top: 1px solid #ebeef5;
          }
        }
      }
  
      .el-dialog__footer {
        box-shadow: 0px -1px 27px 0px rgba(42, 50, 57, 0.2);
        padding: 0 20px;
        background-color: #f6f6f6;
  
        .my-footer {
          height: 60px;
          display: flex;
          align-items: center;
          justify-content: center;
  
          > .right {
            flex: 1;
  
            .num {
              width: 100%;
              height: 54px;
              display: flex;
              align-items: center;
  
              > div {
                width: 180px;
                height: 32px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                background-color: #7d807d;
                background: url(../assets/images/btnbj.png) center no-repeat;
                margin-right: 10px;
                padding: 0 40px 0 30px;
                background-size: 100% 100%;
  
                > div {
                  display: flex;
                  align-items: center;
                  font-size: 13px;
                  color: #5491d5;
                  font-weight: 600;
  
                  &:last-child {
                    font-size: 15px;
                    color: #1985fd;
                  }
                }
  
                &:last-child {
                  margin: 0;
                }
              }
            }
          }
  
          .btn {
            .el-button {
              &.look {
                background: url(../assets/images/dan.png) center no-repeat;
                background-size: 100% 100%;
                font-size: 15px;
                color: #ffffff;
              }
  
              &.is-disabled {
                opacity: 0.5;
              }
            }
          }
        }
      }
    }
  }
  
  .rule-list {
    max-height: 400px;
    overflow-y: auto;
    padding: 10px;
  
    .rule-item {
      padding: 8px;
      border-bottom: 1px solid #eee;
      animation: fadeIn 0.3s ease-in-out;
    }
  
    .loading-text {
      text-align: center;
      font-size: 16px;
      color: #409eff;
  
      i {
        margin-left: 8px;
        animation: rotating 2s linear infinite;
      }
    }
  }
  
  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  @keyframes rotating {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  </style>
  