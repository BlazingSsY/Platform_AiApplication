<script setup>
import { informationTypeOpt ,informationTypeOptlabel} from '@/hooks/informationTypeOpt'
import { informationStatusOpt,informationStatusLabel } from '@/hooks/informationStatusOpt'
import { informationsPages,informationsStatus ,informationsDel,informationsEdit,
  informationsAdd,informationsDelAttachments,informationAddAttachments,informationAttachments,
  informationsConfig,
  informationsConfigUpdate,
} from '@/ajax/informations';
import { uploadFile } from '@/ajax/app';
import dayjs from 'dayjs'
const appListData = reactive([])
const pagination = ref({
  "pageNumber": 1,
  "pageSize": 10,
  "total": 0
})
const fileList = ref([])
const appendFileList = ref([])
const addFn = (row)=>{
  console.log('addFn',row);
  if(!row){
    // 新增
    fileList.value = []
    appendFileList.value = []
    appFromData.id = ``;
    appFromData.title = '';
    appFromData.summary = '';
    appFromData.image = '';
    appFromData.type = '';
    appFromData.status = 1;
    appFromData.content = '';    
    appFromData.appendFileList = [];    
    addEditDialogVisible.value = true;
  }else{
    // 编辑
    appFromData.id = row.id;
    appFromData.title = row.title;
    appFromData.summary = row.summary;
    appFromData.image = row.image;
    if(row.image){
      fileList.value = [{
        name: row.image,
        url: `/portal/common/v1/storage/download/${row.image}?fileId=${row.image}`,
      }]
    }else{
      fileList.value = []
    }
    appFromData.type = row.type;
    appFromData.status = row.status;
    appFromData.content = row.content;   
    appendFileList.value = [] 
    appFromData.appendFileList = [] 
    // 获取附件
    informationAttachments({
      infoId : row.id
    }).then(res=>{
      if(res.succ){
        appFromData.appendFileList = res.content
        if(res.content && res.content.length){
          appendFileList.value = res.content.map(item=>({
            name: item.fileName,
            url: `/portal/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}`,
          }))
        }
      }
    })
    addEditDialogVisible.value = true;  
  }
}
const statusChangeFn = (row)=>{
  console.log('statusChangeFn',row);
  const status = row.status === 1 ? 2 : 1;
  ElMessageBox.confirm(`确定${status === 1 ? '发布' : '下线'}该${informationTypeOptlabel(row.type)}？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    informationsStatus({
      status: status,
      id: row.id
    }).then(res=>{
      if(res.succ){
        ElMessage.success("操作成功")
        searchFn()
      }
    })
  })  
}
const delFn = (row)=>{
  console.log('delFn',row);
  ElMessageBox.confirm(`确定删除该${informationTypeOptlabel(row.type)}？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    informationsDel([row]).then(res=>{
      if(res.succ){
        ElMessage.success("删除成功")
        searchFn()
      }
    })
  })
}

// 新增编辑应用弹窗
const addEditDialogVisible = ref(false)
const appFromRef = ref(null)
const appFromData = reactive({
    id:``,
    title: '',
    summary: '',
    image: '',
    type: '',
    status: 1,
    content: '',
    appendFileList:[],
})
const appFromRules = reactive({
    title: [
        { required: true, message: '请输入资讯标题', trigger: 'blur' }
    ],
    // image: [
    //     { required: true, message: '请上传资讯封面', trigger: 'change' }
    // ],
    type: [
        { required: true, message: '请选择资讯分类', trigger: 'change' }
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ],
    content: [
        { required: true, message: '请输入资讯内容', trigger: 'blur' }
    ],
})
// 上传图片
const handleBeforeUpload = (file) => {
  const isJPG = file.type === `image/jpeg` || file.type === `image/png`;
    const isLt10M = file.size / 1024 / 1024 < 10;
    if (!isJPG) {
        proxy.$message.error(`上传图片只能是 JPG后者PNG 格式!`);
    }
    if (isJPG && !isLt10M) {
        proxy.$message.error(`上传图片大小不能超过 10MB!`);
    }
    return isJPG && isLt10M;
}
const onRemove = () => {
  appFromData.image = ``
}
const handleError = () => {
  ElMessage.error("上传失败，请重试")
}
const uploadElement = ref()
const httpRequestAudio = (file) => {
    const formData = new FormData();
    formData.append(`file`, file.file);
    uploadFile(formData)
    .then(res => {
        if (res.succ) {
          appFromData.image = res.content;
        }
    })
    .catch(err => {
        ElMessage.error(`上传图片失败,${err}`);
    });
}
const handleExceed = files => {
  uploadElement.value.clearFiles()
  const file = files[0]
  file.uid = genFileId()
  uploadElement.value.handleStart(file)
  uploadElement.value.submit()
}
// 上传附件
const handleBeforeUploadFiles = (file) => {
    const isLt10M = file.size / 1024 / 1024 < 300;
    if (!isLt10M) {
        proxy.$message.error(`上传附件大小不能超过 300MB!`);
    }
    return isLt10M;
}
const onRemoveFiles = (UploadFile) => {
  console.log('UploadFile',UploadFile);
  const item = appFromData.appendFileList.filter(item=>UploadFile.url.indexOf(item.fileId)>-1)
  console.log('item',item);
  informationsDelAttachments({attachmentId:item[0].id}).then(res=>{
    if(res.succ){
      appFromData.appendFileList = appFromData.appendFileList.filter(item=>UploadFile.url.indexOf(item.fileId)==-1)
      appendFileList.value = appendFileList.value.filter(item=>item.url!==UploadFile.url)
      ElMessage.success("附件删除成功")
    }
  })
}

const uploadElementFiles = ref()
const handleErrorFiles = () => {
  ElMessage.error("上传失败，请重试")
}
const httpRequestFiles = (file) => {
  console.log('file',file);
    const formData = new FormData();
    formData.append(`file`, file.file);
    uploadFile(formData)
    .then(res => {
      console.log('res',res);
      console.log('appFromData.appendFileList',appFromData.appendFileList);
      console.log('appendFileList.value',appendFileList.value);
        if (res.succ) {
            appFromData.appendFileList.push({
              fileName: file.file.name,
              fileId: res.content
            })
            if(appFromData.id){
              informationAddAttachments({
                infoId: appFromData.id,
                data:{
                  fileId: res.content,
                  fileName: file.file.name
                }
              }).then(res=>{
                if(res.succ){
                  ElMessage.success("附件新增成功")
                }
              })
            }
        }else{
          appendFileList.value = appendFileList.value.filter(item=>item.uid!==file.file.uid)
        }
    })
    .catch(err => {
        ElMessage.error(`上传附件失败,${err}`);
    });
}
const handleExceedFiles = files => {
  uploadElementFiles.value.clearFiles()
  const file = files[0]
  file.uid = genFileId()
  uploadElementFiles.value.handleStart(file)
  uploadElementFiles.value.submit()
}

const loading = ref(false)
const uploadSubmit = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      // 提交表单
      const sentData = JSON.parse(JSON.stringify(appFromData))
      console.log('提交表单',sentData);
      loading.value = true
      if(sentData.id){
        informationsEdit(sentData).then(res=>{
          loading.value = false
          if(res.succ){
            addEditDialogVisible.value = false;
            searchFn()
            ElMessage.success("编辑成功")
          }
        }).catch(err=>{
          loading.value = false
        })
      }else{
        delete sentData.id
        informationsAdd(sentData).then(res=>{
          loading.value = false
          if(res.succ){
            addEditDialogVisible.value = false;
            searchFn()
            ElMessage.success("新增成功") 
          }
        }).catch(err=>{
          loading.value = false
        })
      }
    }
  })
}
const title = ref('')
const searchFn = async () => {
  const res = await informationsPages({
    pageNumber: pagination.value.pageNumber,
    pageSize: pagination.value.pageSize,
    title:title.value
  })
  if (res.succ) {
    appListData.splice(0)
    res.content.records.forEach(r=>{
      appListData.push(r)
    })
    pagination.value.total = res.content.total || 0
  }
}
searchFn()


const config = reactive({
  count:{
    "refreshInterval": 3,
    "rangeType": "count",
    "count": 3
  },
  today:{
    "refreshInterval": 3,
    "rangeType": "today"
  },
  dateRange:{
    "refreshInterval": 3,
    "rangeType": "range",
    "startDate": "2025-12-01",
    "endDate": "2025-12-02"
  }
})
const configFrom = reactive({
  rangeType:`count`,
  count:3,
  refreshInterval:3,
  dateRange:[dayjs().subtract(7, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')] // 直接设置初始值
})
  const changeRangeType = ()=>{
    configFrom.refreshInterval = 3
    if(configFrom.rangeType === `count`){
      configFrom.count = 3
    }else if(configFrom.rangeType === `today`){
      
    }else if(configFrom.rangeType === `range`){
      configFrom.dateRange = [dayjs().subtract(7, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
    }
    valueChange()
  }

  const valueChange = ()=>{
    const seatData = JSON.parse(JSON.stringify(configFrom))
    if(configFrom.rangeType === `count`){
      delete seatData.dateRange
    }else if(configFrom.rangeType === `today`){
      delete seatData.count
      delete seatData.dateRange
    }else if(configFrom.rangeType === `range`){
      delete seatData.count
      delete seatData.dateRange
      seatData.startDate =configFrom.dateRange[0]
      seatData.endDate = configFrom.dateRange[1]
    }
    informationsConfigUpdate(seatData).then(res=>{
      if(res.succ){
        informationsConfigFn()
      }
    })
  }

  const informationsConfigFn = ()=>{
    informationsConfig().then(res=>{
      if(res.succ){
        configFrom.rangeType = res.content.rangeType || `count`
        configFrom.refreshInterval = res.content.refreshInterval || 3
        if(configFrom.rangeType === `count`){
          configFrom.count = res.content.count || 3
        }else if(configFrom.rangeType === `today`){
        }else if(configFrom.rangeType === `range`){
          configFrom.dateRange = [res.content.startDate || dayjs().subtract(7, 'day').format('YYYY-MM-DD'), res.content.endDate || dayjs().format('YYYY-MM-DD')]
        }
     }
    })
  }
  informationsConfigFn()


</script>

<template>
  <div class="informationManage">
    <div class="title">资讯管理</div>
    <div class="search">
      <div class="config">
        <el-form ref="appFromRef" :model="configFrom" label-width="auto" class="demo-ruleForm" status-icon inline>
            <el-form-item label="滚动资讯配置" prop="rangeType">
                <el-select v-model="configFrom.rangeType" placeholder="请选择配置类型" style="width: 120px;" @change="changeRangeType">
                    <el-option label="资讯数量" value="count" />
                    <el-option label="今日资讯" value="today" />
                    <el-option label="日期范围" value="range" />
                </el-select>
            </el-form-item>

            <!-- 资讯数量 -->
            <el-form-item v-if="configFrom.rangeType === `count`" label="资讯数量" prop="count" >
                <el-input-number v-model="configFrom.count" :min="3" :max="50" :step="1" @change="valueChange"/>
            </el-form-item>
            <!-- 日期范围 -->
            <el-form-item v-if="configFrom.rangeType === `range`" label="日期范围" prop="dateRange" >
                <el-date-picker
                    v-model="configFrom.dateRange"
                    type="daterange"
                    format="YYYY/MM/DD"
                    value-format="YYYY-MM-DD"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    @change="valueChange"
                />
            </el-form-item>
            <!-- 刷新时间 -->
            <el-form-item label="刷新时间" prop="refreshInterval">
                <el-input-number v-model="configFrom.refreshInterval" :min="3" :max="10" :step="1" @change="valueChange"/>
                <span style="margin-left: 8px;color: #909399;">秒</span>
            </el-form-item>
          </el-form>
      </div>
      <div class="searchCont">
        <el-input v-model="title" placeholder="请输入资讯标题" clearable @keyup.enter="searchFn" style="width: 300px"/>
        <el-button type="primary" @click="searchFn()" style="margin-left: 20px;">查询</el-button>
        <el-button type="primary" @click="addFn()">新增资讯</el-button>
      </div>
    </div>
    <div class="tableBox">
         <el-table
            ref="tableRef"
            :data="appListData"
            :tooltip-options="{ placement: 'bottom' }"
            class="custom-table"
        >
            <el-table-column label="序号" type="index" align="center" width="64" />
            <el-table-column prop="title" label="资讯标题" align="left" min-width="200" />
            <el-table-column prop="createDate" label="创建时间" align="center" width="160" />
            <el-table-column prop="type" label="分类" align="center" width="120" >
                <template #default="scope">
                    {{ informationTypeOptlabel(scope.row.type) }}
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" align="center" width="140" >
                <template #default="scope">
                    <span :class="scope.row.status === 0 ? 'notOnline' :scope.row.status === 1 ? 'online':`offline`">
                        {{ informationStatusLabel(scope.row.status) }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="image" label="封面图" align="center" width="140" >
                <template #default="scope">
                    <div style="padding: 4px;background: rgba(0,0,0,0.05);display: flex;align-items: center;justify-content: center;">
                      <img v-if="scope.row.image" :src="`/portal/common/v1/storage/download/${scope.row.image}?fileId=${scope.row.image}`" alt="" style="height: 40px;width: auto;">
                      <img v-if="!scope.row.image" src="@/assets/images/fengmian.png" alt="" style="height: 40px;width: auto;">
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200" class-name="opt" fixed="right">
              <template #default="scope">
                  <el-button type="primary" size="small" class="btn" @click="addFn(scope.row)"> 编辑 </el-button>
                  <el-button :type="scope.row.status !== 1?`primary`:`danger`" size="small" class="btn" @click="statusChangeFn(scope.row)"> 
                    {{ scope.row.status !== 1?`发布`:`下线` }}  
                  </el-button>
                  <el-button type="danger" size="small" class="btn" @click="delFn(scope.row)"> 删除 </el-button>
              </template>
            </el-table-column>
        </el-table>
    </div>
    <div class="pagination-container">
        <el-pagination
            v-model:current-page="pagination.pageNumber"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="searchFn"
            @size-change="searchFn"
        />
    </div>

    <!-- 新增、编辑应用 -->
    <el-dialog v-if="addEditDialogVisible" v-model="addEditDialogVisible" class="dialogCont dialogCont3" title="" :show-close="false" width="900px" :close-on-click-modal="false" :close-on-press-escape="false" top="3vh">
      <template #header>
        <div class="my-header">
          {{ appFromData.id ? '编辑' : '新增' }}应用
        </div>
      </template>
      <div class="contBox">
        <el-form ref="appFromRef" :model="appFromData" :rules="appFromRules" label-width="auto" class="demo-ruleForm" status-icon>
            <el-form-item label="资讯标题" prop="title">
                <el-input v-model="appFromData.title" placeholder="请输入资讯标题" style="width: 80%"/>
            </el-form-item>
            <el-form-item label="资讯封面" prop="image" style="width: 100%">
                <el-upload
                  ref="uploadElement"
                  v-model:file-list="fileList"
                  class="upload-demo"
                  action="#"
                  accept=".jpg,.png,.jpeg,.gif"
                  :http-request="httpRequestAudio"
                  :show-file-list="true"
                  :limit="1"
                  :on-exceed="handleExceed"
                  :before-upload="handleBeforeUpload"
                  :on-remove="onRemove"
                  :on-error="handleError"
                  >
                <el-button type="primary" icon="Upload">上传文件</el-button>
                </el-upload>
            </el-form-item>
            <el-form-item label="资讯分类" prop="type">
                <el-select v-model="appFromData.type" placeholder="请选择资讯分类" style="width: 80%">
                  <el-option
                      v-for="item in informationTypeOpt"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                  />
                </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="appFromData.status">
                    <el-radio :label="1">已发布</el-radio>
                    <el-radio :label="0">未发布</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="资讯摘要" prop="summary">
                <el-input v-model="appFromData.summary" type="textarea" placeholder="请输入资讯摘要" style="width: 100%" rows="3" />
            </el-form-item>
            <el-form-item label="资讯内容" prop="content">
                <WangEditor v-model="appFromData.content" :height="220"/>
            </el-form-item>
            <el-form-item label="附件" prop="appendFileList">
                <el-upload
                ref="uploadElementFiles"
                v-model:file-list="appendFileList"
                class="upload-demo"
                action="#"
                :http-request="httpRequestFiles"
                :show-file-list="true"
                :limit="5"
                :on-exceed="handleExceedFiles"
                :before-upload="handleBeforeUploadFiles"
                :on-remove="onRemoveFiles"
                :on-error="handleErrorFiles"
                >
                <el-button type="primary" icon="Upload">上传文件</el-button>
                </el-upload>
            </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="my-footer">
          <div class="right">
            <el-button @click="addEditDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="uploadSubmit(appFromRef)" v-loading="loading">确定</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang='scss' scoped>
.informationManage{
    height: 100%;
    height: 600px;
    .title{
        font-size: 16px;
        padding: 0 20px;
        height: 44px;
        line-height: 44px;
        background-color: #fafafa;
    }
    .search {
        height: 46px;
        padding: 0 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #fff;
        .config{
          flex: 1;
          height: 100%;
        }
        .searchCont{

        }
    }
    .tableBox {
        height: calc(100% - 150px);
       .el-table {
            background-color: transparent;
            height: 100%;
            .notOnline,.offline{
              &::before{
                display: inline-block;
                content: " ";
                width: 6px;
                height: 6px;
                background-color: #A5A5A5;
                border-radius: 50%;
                vertical-align: middle;
                margin-right: 4px;
              }
              
            }
            .online{
              &::before{
                display: inline-block;
                content: " ";
                width: 6px;
                height: 6px;
                background-color: #67CA38;
                border-radius: 50%;
                margin-right: 4px;
              }
            }
            .opt {
                .btn {
                height: 26px;
                line-height: 26px;
                padding: 0 4px;
                min-width: 50px;
                text-align: center;
                span{
                  font-size: 13px;
                }

                .el-loading-mask {
                    .el-loading-spinner {
                    height: 26px;
                    margin: 0;
                    top: 0;

                    .circular {
                        width: 28px;
                        height: 28px;
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
                  position: absolute;
                  right: 4px;
                  top: 15px;
                  color: #8b8b8b;
                }
                .upload-demo12 {
                  width: 80px;
                  display: inline-block;
                  margin-left: 12px;
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
    :deep(.dialogCont3) {
      .el-dialog__body {
        padding: 0;
        .el-form {
          .upload-demo {
            width: 80%;
          }
        }
      }
    }
}
</style>