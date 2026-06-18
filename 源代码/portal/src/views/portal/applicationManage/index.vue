<script setup>
import { appTypeOpt } from '@/hooks/appTypeOpt'
import { appStatusOpt,appStatusLabel } from '@/hooks/appStatusOpt'
import { uploadFile,applicationsPages,applicationsDel,changeStatus,applicationsAdd,applicationsEdit } from '@/ajax/app.js';
const appListData = reactive([])
const pagination = ref({
  "pageNumber": 1,
  "pageSize": 10,
  "total": 0
})
const searchFn = ()=>{
    console.log('searchFn');
    applicationsPages(pagination.value).then(res=>{
      if(res.succ){
        appListData.splice(0)
        res.content.records.forEach(r=>{
          appListData.push(r)
        })
        pagination.value.total = res.content.total
      }
    })
}
searchFn()

const fileList = ref([])
const addFn = (row)=>{
  console.log('addFn',row);
  if(!row){
    // 新增
    fileList.value = []
    appFromData.id = ``;
    appFromData.name = '';
    appFromData.engName = '';
    appFromData.icon = '';
    appFromData.module = '';
    appFromData.url = '';
    appFromData.description = '';
    appFromData.status = 0;
    appFromData.contents = '';    
    addEditDialogVisible.value = true;
  }else{
    // 编辑
    appFromData.id = row.id;
    appFromData.name = row.name;
    appFromData.engName = row.engName;
    appFromData.icon = row.icon;
    appFromData.module = row.module;
    appFromData.url = row.url;
    fileList.value = [{
      name: row.icon,
      url: `/portal/common/v1/storage/download/${row.icon}?fileId=${row.icon}`,
    }]
    appFromData.description = row.description;
    appFromData.status = row.status;
    appFromData.contents = row.contents;    
    addEditDialogVisible.value = true;  
  }
}
const statusChangeFn = (row)=>{
  const status = row.status === 1 ? 2 : 1;
  ElMessageBox.confirm(`确定${status === 1 ? '上线' : '下线'}该应用？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    changeStatus({
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
  ElMessageBox.confirm("确定删除该应用？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    console.log('删除应用');
    applicationsDel([row]).then(res=>{
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
    name: '',
    engName: '',
    icon: '',
    module: '',
    url: '',
    description: '',
    status: 0,
    contents: '' //备注
})
const appFromRules = reactive({
    name: [
        { required: true, message: '请输入应用名称', trigger: 'blur' }
    ],
    engName: [
        { required: true, message: '请输入英文应用名称', trigger: 'blur' }
    ],
    icon: [
        { required: true, message: '请上传应用图标', trigger: 'change' }
    ],
    module: [
        { required: true, message: '请选择所属板块', trigger: 'change' }
    ],
    url: [
        { required: true, message: '请输入应用地址', trigger: 'blur' },
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ]
})
const handleBeforeUpload = (file) => {
  console.log('handleBeforeUpload',file);
  const isJPG = file.type === `image/jpeg` || file.type === `image/png`;
    const isLt10M = file.size / 1024 / 1024 < 10;
    if (!isJPG) {
        ElMessage.error(`上传图片只能是 JPG后者PNG 格式!`);
    }
    if (isJPG && !isLt10M) {
        ElMessage.error(`上传图片大小不能超过 10MB!`);
    }
    return isJPG && isLt10M;
}
const onRemove = () => {
  appFromData.icon = ``
}
const handleError = () => {
  ElMessage.error("上传失败，请重试")
}
const httpRequestAudio = (file) => {
  console.log(`file`,file)
    const formData = new FormData();
    formData.append(`file`, file.file);
    uploadFile(formData)
    .then(res => {
        if (res.succ) {
            appFromData.icon = res.content;
        }
    })
    .catch(err => {
        ElMessage.error(`上传附件失败,${err}`);
    });
}

const uploadSubmit = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      // 提交表单
      console.log('提交表单',appFromData);
      const sentData = JSON.parse(JSON.stringify(appFromData))
      console.log('sentData',sentData);
      if(appFromData.id){
        applicationsEdit(appFromData).then(res=>{
          if(res.succ){
            addEditDialogVisible.value = false;
            searchFn()
            ElMessage.success("编辑成功")
          }
        })
      }else{
        delete sentData.id
        applicationsAdd(sentData).then(res=>{
          if(res.succ){
            addEditDialogVisible.value = false;
              searchFn()
              ElMessage.success("新增成功") 
          }
        })
      }
    }
  })
}

</script>

<template>
  <div class="applicationManage">
    <div class="title">
      应用管理
      <el-button type="primary" @click="addFn()">新增应用</el-button>
    </div>
    <div class="tableBox">
         <el-table
            ref="tableRef"
            :data="appListData"
            :tooltip-options="{ placement: 'bottom' }"
            class="custom-table"
        >
            <el-table-column label="序号" prop="sequence" align="center" width="64" >
                <template #default="scope">
                  <!-- {{ scope.row.sequence + 1 }} -->
                    {{ scope.$index + 1 + pagination.pageSize*(pagination.pageNumber - 1) }}
                </template> 
            </el-table-column>
            <el-table-column prop="name" label="名称" align="center" min-width="200" />
            <el-table-column prop="engName" label="英文名称" align="center" min-width="200" />
            <el-table-column prop="icon" label="图标" align="center" min-width="50" >
              <template #default="scope">
                <div style="padding: 4px;background: rgba(0,0,0,0.05);display: flex;align-items: center;justify-content: center;">
                  <img :src="`/portal/common/v1/storage/download/${scope.row.icon}?fileId=${scope.row.icon}`" alt="" style="height: 40px;width: auto;">
                </div>
                </template>
            </el-table-column>
            <el-table-column prop="module" label="所属板块" align="center" width="120" />
            <el-table-column prop="url" label="地址" align="center" min-width="200" />
            <el-table-column prop="status" label="状态" align="center" width="140" >
                <template #default="scope">
                    <span :class="scope.row.status === 0 ? 'notOnline' :scope.row.status === 1 ? 'online':`offline`">
                        {{ appStatusLabel(scope.row.status) }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200" class-name="opt" fixed="right">
              <template #default="scope">
                  <el-button type="primary" size="small" class="btn" @click="addFn(scope.row)"> 编辑 </el-button>
                  <el-button :type="scope.row.status !== 1?`primary`:`danger`" size="small" class="btn" @click="statusChangeFn(scope.row)"> 
                    {{ scope.row.status !== 1?`上线`:`下线` }}  
                  </el-button>
                  <el-button type="danger" size="small" class="btn" :disabled="scope.row.status === 1" @click="delFn(scope.row)"> 删除 </el-button>
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
    <el-dialog v-model="addEditDialogVisible" class="dialogCont dialogCont3" title="" width="900px" :close-on-click-modal="false" :close-on-press-escape="false">
      <template #header>
        <div class="my-header">
          {{ appFromData.id ? '编辑' : '新增' }}应用
        </div>
      </template>
      <div class="contBox">
        <el-form ref="appFromRef" :model="appFromData" :rules="appFromRules" label-width="auto" class="demo-ruleForm" status-icon>
            <el-form-item label="应用名称" prop="name">
                <el-input v-model="appFromData.name" placeholder="请输入应用名称" style="width: 80%;"/>
            </el-form-item>
            <el-form-item label="英文名称" prop="engName">
                <el-input v-model="appFromData.engName" placeholder="请输入英文名称" style="width: 80%;"/>
            </el-form-item>
            <el-form-item label="应用图标" prop="icon">
                <el-upload
                  v-model:file-list="fileList"
                  class="upload-demo"
                  action="#"
                  accept=".jpg,.png,.jpeg,.gif"
                  :http-request="httpRequestAudio"
                  :show-file-list="true"
                  :limit="1"
                  :before-upload="handleBeforeUpload"
                  :on-remove="onRemove"
                  :on-error="handleError"
                  >
                  <el-button type="primary" icon="Upload">上传文件</el-button>
                </el-upload>
            </el-form-item>
            <el-form-item label="应用分类" prop="module">
                <el-select v-model="appFromData.module" placeholder="请选择应用分类" style="width: 80%;">
                  <el-option
                      v-for="item in appTypeOpt"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                  />
                </el-select>
            </el-form-item>
            <el-form-item label="应用地址" prop="url">
                <el-input v-model="appFromData.url" placeholder="请输入应用地址" style="width: 80%;"/>
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="appFromData.status">
                    <el-radio :label="0">未上线</el-radio>
                    <el-radio :label="1">上线</el-radio>
                    <el-radio :label="2">下线</el-radio>
                </el-radio-group>
            </el-form-item>
            <!-- <el-form-item label="应用描述" prop="description">
                <el-input v-model="appFromData.description" type="textarea" placeholder="请输入应用描述" style="width: 80%;" :rows="4"/>
            </el-form-item> -->
            <!-- 应用详情页面 -->
            <el-form-item label="应用详情" prop="description">
                <WangEditor v-model="appFromData.description" />
            </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="my-footer">
          <div class="right">
            <el-button @click="addEditDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="uploadSubmit(appFromRef)">确定</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang='scss' scoped>
.applicationManage{
    height: 100%;
    height: 600px;
    .title{
        font-size: 16px;
        padding: 0 20px;
        height: 50px;
        background-color: #fafafa;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    .tableBox {
        height: calc(100% - 110px);
       .el-table {
            background-color: transparent;
            flex: 1;
            height: 100%;
            // #67CA38
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
            }
        }
    }
    .pagination-container {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        background-color: #fff;
        border-top: 1px solid #ebeef5;
    }
    :deep(.dialogCont3) {
      .el-dialog__body {
        padding: 0;
        .el-form {
          padding-top: 20px;
          .upload-demo {
            width: 80%;
          }
        }
      }
    }
}
</style>
