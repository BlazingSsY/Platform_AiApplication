<script setup lang="ts">
  import localTitle from "./components/localTitle.vue"
  import { getToolPages, toolFileUploadFile, deleteToolFile } from "@/ajax/logicreview"
  import { downloadFile } from "@/ajax/index"
  import store from "@/store/index"
  const userInfo = computed(() => store.state.user.userInfo)
  const roleInfo = computed(() => userInfo.value?.role)
  const { proxy } = getCurrentInstance() as any
  // 工具列表数据
  const toolList = ref<any[]>([])
  const loading = ref(false)
  const loadingText = ref(`数据加载中...`)
  
  // 获取工具列表（模拟）
  const loadingObj = ref({})
  
  const fetchToolList = async () => {
    loadingText.value = `数据加载中...`
    loading.value = true
    const res = await getToolPages({ pageNumber: 1, pageSize: 999 })
        .then(res => {
          loading.value = false
          loadingObj.value = {}
          if (res.succ) {
            toolList.value = res.content.records
          }
        })
        .catch(() => {
          loading.value = false
        })
  }
  fetchToolList()
  
  // 上传相关
  const uploadShow = ref(false)
  const uploadFormRef = ref()
  const uploadForm = reactive({
    toolName: "",
    comments: "",
    file: null
  })
  const fileList = ref<any[]>([])
  const validateFile = (rule: any, value: any, callback: any) => {
    if (!value) {
      callback(new Error("请上传工具文件"))
    } else {
      callback()
    }
  }
  const uploadRules = reactive({
    toolName: [{ required: true, message: "请输入工具名称", trigger: "blur" }],
    "file": [{ "required": true, validator: validateFile, trigger: "change" }]
  })
  const handleBeforeUpload = (file: any) => {
    return false
  }
  const onRemove = () => {
    uploadForm.file = null
    fileList.value = []
  }
  const handleChange = (file: any, fileList: any) => {
    uploadForm.file = file.raw
    if (fileList.length > 1) {
      fileList.splice(0, 1)
    }
  }
  const handleError = () => {
    ElMessage.error("上传失败，请重试")
  }
  const uploadFn = () => {
    uploadForm.toolName = ""
    uploadForm.comments = ""
    uploadForm.file = null
    fileList.value = []
    uploadShow.value = true
  }
  const uploadSubmit = async (formEl: any) => {
    if (!formEl) return
    await formEl.validate((valid: boolean) => {
      if (valid) {
        loadingText.value = `文件上传中...`
        loading.value = true
        const fd = new FormData()
        if (uploadForm.file) {
          fd.append("file", uploadForm.file)
        }
        toolFileUploadFile({ ...uploadForm, file: fd })
            .then(res => {
              loading.value = false
              if (res.succ) {
                uploadShow.value = false
                fetchToolList()
                ElMessage.success("上传成功")
              }
            })
            .catch(() => {
              loading.value = false
            })
      }
    })
  }
  
  // 下载
  const downloadFn = (item: any) => {
    console.log(` item.fileName`, item.fileName)
    // const a = document.createElement('a');
    // a.href = `/circuitreview/common/v1/storage/download/${item.fileId}`;
    // a.download = item.fileName; // 设置下载的文件名
    // document.body.appendChild(a);
    // a.click();
    // document.body.removeChild(a);
    // loadingText.value = `文件下载中...`
    // loading.value = true
    if(item.fileId){
      window.open(`/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(item.fileName)}`)
    }
    //
    // downloadFile({ fileId: item.fileId }).then((res: any) => {
    //   loading.value = false
    //   proxy.downLoadFile(res, item.fileName)
    // }).catch(() => {
    //   loading.value = false
    // })
  }
  // 删除
  const delFn = (item: any) => {
    ElMessageBox.confirm("确定删除该工具？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }).then(() => {
      deleteToolFile([item]).then(res => {
        if (res.succ) {
          ElMessage.success("删除成功")
          fetchToolList()
        }
      })
    })
  }
  

  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="toolDownload" element-loading-background="rgba(122, 122, 122, 0.8)" :element-loading-text="loadingText">
      <localTitle title="工具下载列表">
        <template #rightBtn>
          <el-button type="primary" icon="Upload" style="margin-left: 12px" @click="uploadFn" v-if="roleInfo?.name === `管理员`">上传工具</el-button>
        </template>
      </localTitle>
      <div class="tableBox">
        <el-table :data="toolList" border tooltip-effect="light">
          <el-table-column label="序号" align="center" width="80">
            <template #default="scope">
              {{ scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="toolName" label="工具名称" align="center" min-width="200px" />
          <el-table-column prop="comments" label="描述" align="center" min-width="200px" />
          <el-table-column prop="fileName" label="文件名" align="center" min-width="200px" />
          <el-table-column prop="createDate" label="上传时间" align="center" min-width="120px" />
          <el-table-column label="操作" align="center" width="220px">
            <template #default="scope">
              <el-button type="primary" size="small" @click="downloadFn(scope.row)">下载</el-button>
              <el-button type="danger" size="small" @click="delFn(scope.row)" v-if="roleInfo?.name === `管理员`">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 上传工具弹窗 -->
      <el-dialog v-if="uploadShow" v-model="uploadShow" class="dialogCont dialogCont3" title="" :show-close="false" width="600px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">上传工具</div>
          </div>
        </template>
        <div class="contBox">
          <el-form ref="uploadFormRef" style="max-width: 600px" :model="uploadForm" :rules="uploadRules" label-width="auto" class="demo-ruleForm" status-icon>
            <el-form-item label="工具名称" prop="toolName">
              <el-input v-model="uploadForm.toolName" placeholder="请输入工具名称" style="width: 400px" />
            </el-form-item>
            <el-form-item label="描述" prop="comments">
              <el-input v-model="uploadForm.comments" placeholder="请输入描述" style="width: 400px" />
            </el-form-item>
            <el-form-item label="工具文件" prop="file">
              <el-upload
                  class="upload-demo"
                  action="#"
                  :show-file-list="true"
                  :before-upload="handleBeforeUpload"
                  :on-change="handleChange"
                  :on-remove="onRemove"
                  :on-error="handleError"
                  :file-list="fileList"
                  :auto-upload="false"
              >
                <el-button type="primary" icon="Upload">上传文件</el-button>
              </el-upload>
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right">
              <el-button @click="uploadShow = false">关闭</el-button>
              <el-button type="primary" @click="uploadSubmit(uploadFormRef)">确定</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <style lang="scss" scoped>
  .toolDownload {
    display: flex;
    flex-direction: column;
    .title-bar {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      .title {
        font-size: 22px;
        font-weight: bold;
        color: #2d3a4b;
      }
    }
    .tableBox {
      flex: 1;
      height: 100%;
      max-height: 900px;
      .pagination-container {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        background-color: transparent;
        border-top: 1px solid #ebeef5;
      }
    }
    :deep(.dialogCont3) {
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
  }
  </style>
  