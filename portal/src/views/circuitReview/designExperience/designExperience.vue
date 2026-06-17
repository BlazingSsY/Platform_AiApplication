<script setup lang="ts">
import localTitle from "./components/localTitle.vue"
import DetailsDialog from "./modals/DetailsDialog.vue"
import { ref, reactive, onMounted } from "vue"
import { ElMessage, genFileId, ElMessageBox } from "element-plus"
import { getToken } from '@/utils/auth';
import { createExperienceShare, updateExperienceShare, getExperienceSharePage, likeExperienceShare, deleteExperienceShare } from '@/ajax/circuitreview';
import { getDepartmentSimpleItems,getDepartmentsSimpleItems } from "@/ajax"
import store from "@/store/index"
const isAdmin = computed(() => store.state.user?.userInfo?.role?.name===`管理员`)

const userInfo = computed(() => store.state.user.userInfo)
const departmentName = computed(() => userInfo.value?.departmentName)
const loginName = computed(() => userInfo.value?.name)
const contact = computed(() => userInfo.value?.telephone)
const isContributor = computed(() => tableData.value.some(row => row.contributor === loginName.value))
const roleInfo = computed(() => userInfo.value?.role)
const roleName = computed(() => {
  return roleInfo.value ? roleInfo.value.name : ``
})
const departmentId = computed(() => userInfo.value.departmentId ?? ``)

const optionCb = ref<any>([])
watch(roleName.value, () => {
  if(roleName.value.indexOf(`领导`) > -1){
    getDepartmentsSimpleItems({ departmentId: departmentId.value })
      .then(res => {
        if (res.succ) {
           if(res.content&&res.content.length){
            optionCb.value = res.content.map((item:any)=>{
                return {
                  label: item.name,
                  value: item.id
                }
              })
          }
        }
      })
  }else{
    getDepartmentSimpleItems({}).then((res:any)=>{
      if(res.succ){
        if(res.content&&res.content.length){
          optionCb.value = res.content.map((item:any)=>{
              return {
                label: item.name,
                value: item.id
              }
            })
        }
      }
    })
  }
 
},{immediate:true})
interface ExperienceAttachment {
  id?: number
  fileId: string
  fileName: string
  fid: number
}

interface ExperienceItem {
  id: number
  version: number
  title: string
  content: string
  contributor: string
  organization: string
  contact: string
  appendFileList: ExperienceAttachment[]
  updateDate?: string
  likeCount?: number
  currentUserLiked?: boolean
}

interface ExperienceForm {
  id: number | null
  version: number
  title: string
  content: string
  contributor: string
  organization: string
  contact: string
  appendFileList: ExperienceAttachment[]
  updateDate: string
  likeCount: number
}

const tableData = ref<ExperienceItem[]>([])
const loading = ref(false)
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  title: '',
  contributor: '',
  departmentId: ''
})

const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view'>('add')
const dialogTitle = ref('新增经验')
const formRef = ref()

// 标题 最后更新 贡献人 单位 联系方式 附件文件 点赞数
const form = reactive<ExperienceForm>({
  id: null,
  version: 0,
  title: '',
  content: '',
  contributor: loginName.value || '',
  organization: departmentName.value || '',
  contact: '',
  appendFileList: [],
  likeCount: 0,
  updateDate: ''
})

const rules:any = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  contributor: [
    { required: true, message: '请输入贡献人', trigger: 'blur' },
    { min: 2, max: 50, message: '贡献人长度应在 2-50 字符之间', trigger: 'blur' }
  ],
  organization: [
    { required: true, message: '请输入单位', trigger: 'blur' },
    { min: 2, max: 50, message: '单位长度应在 2-50 字符之间', trigger: 'blur' }

  ],
  contact: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  appendFileList: [{type: 'array', required: true, message: '请上传附件', trigger: 'blur' }]
}

const formatNow = () => {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const hh = String(now.getHours()).padStart(2, '0')
  const mm = String(now.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${hh}:${mm}`
}

const resetForm = () => {
  form.id = null
  form.version = 0
  form.title = ''
  form.content = ''
  form.contributor = loginName.value || ''
  form.organization = departmentName.value || ''
  form.contact = ''
  form.appendFileList = []
  form.likeCount = 0
  form.updateDate = formatNow()
  formRef.value?.clearValidate()
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  dialogTitle.value = '新增经验'
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row: ExperienceItem) => {
  dialogMode.value = 'edit'
  dialogTitle.value = '编辑经验'
  form.id = row.id
  form.version = row.version
  form.title = row.title
  form.content = row.content
  form.contributor = row.contributor
  form.organization = row.organization
  form.contact = row.contact
  form.appendFileList = row.appendFileList ? JSON.parse(JSON.stringify(row.appendFileList)) : []
  form.likeCount = row.likeCount || 0
  form.updateDate = row.updateDate || formatNow()
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

// 查看弹窗
const showViewDialog = ref(false)
const viewRowData = ref<ExperienceItem>({} as ExperienceItem)

const openViewDialog = (row: ExperienceItem) => {
  viewRowData.value = { ...row }
  showViewDialog.value = true
}

const saveForm = () => {
  if (!formRef.value) return
  formRef.value.validate((valid: boolean) => {
    if (!valid) return
    const payload:any = {
      id: form.id ?? ``,
      version: form.version,
      title: form.title,
      content: form.content,
      contributor: form.contributor,
      organization: form.organization,
      contact: form.contact,
      appendFileList: form.appendFileList
    }
    if (dialogMode.value === 'add') {
      delete payload.id
      createExperienceShare(payload).then(res => {
        if (res.succ) {
           ElMessage.success('新增成功')
            loadExperienceShares()
        }
      }).catch(() => {
        ElMessage.error('新增失败')
      })
    } else if (dialogMode.value === 'edit' && form.id !== null) {
      updateExperienceShare(form.id, payload).then(res => {
        if (res.succ) {
          ElMessage.success('编辑成功')
          loadExperienceShares()
        }
       
      }).catch(() => {
        ElMessage.error('编辑失败')
      })
    }
    dialogVisible.value = false
  })
}

const closeDialog = () => {
  dialogVisible.value = false
}

const handlelikeCount = (row: ExperienceItem) => {

  likeExperienceShare(row.id).then((res) => {
    if (res.succ) {
      ElMessage.success(`${row.currentUserLiked?'取消点赞成功':'点赞成功'}`)
      loadExperienceShares()
    }
  }).catch(() => {
    ElMessage.error(`点赞失败`)
  })
}

const handleDelete = (row: ExperienceItem) => {
  ElMessageBox.confirm(`确定删除《${row.title}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteExperienceShare([{ id: row.id }]).then((res: any) => {
        if (res.succ) {
          ElMessage.success('删除成功')
          loadExperienceShares()
        } else {
          ElMessage.error(res.msg || '删除失败')
        }
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  }).catch(() => {})
}

const headers = {
  "Authorization":getToken(`sys_tokenID`)
}
const handleBefore = file => {
    const fileSize = file.size / 1024 / 1024 < 500

    if (!fileSize) {
        ElMessage.error(`文件必须小于500M`)
    }

    return fileSize
}
const uploadElement = ref()
const handleExceed = files => {
    uploadElement.value!.clearFiles()
    const file = files[0]
    file.uid = genFileId()
    uploadElement.value!.handleStart(file)
    uploadElement.value!.submit()
}
const handleSuccess = (response: any,uploadFile:any) => {
    if (response.succ) {
        ElMessage.success(`上传成功`)
        form.appendFileList = []
        setTimeout(() => {
          form.appendFileList.push({
            fileId: response.content,
            fileName: uploadFile.name,
            fid: uploadFile.uid ?? 0
          })
          formRef.value?.clearValidate()
        },100)
        
    } else {
        ElMessage.error(response.msg || `导入失败, 请重试`)
    }
}
const handleError = error => {
    ElMessage.error(`导入失败, 请重试`)
}

const loadExperienceShares = async () => {
  loading.value = true
  try {
    const params = {
      pageNumber: pagination.value.current,
      pageSize: pagination.value.size,
      title: searchForm.title,
      contributor: searchForm.contributor,
      departmentId: searchForm.departmentId
    }
    const res = await getExperienceSharePage(params)
    if (res.succ) {
      tableData.value = res.content?.records || []
      pagination.value.total = res.content?.total || 0
    } else {
      ElMessage.error('加载经验分享列表失败')
    }
  } catch (err) {
    ElMessage.error('加载经验分享列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  loadExperienceShares()
}

const handleCurrentChange = (current: number) => {
  pagination.value.current = current
  loadExperienceShares()
}

const onSearch = () => {
  pagination.value.current = 1
  loadExperienceShares()
}

const onResetSearch = () => {
  searchForm.title = ''
  searchForm.contributor = ''
  searchForm.departmentId = ''
  pagination.value.current = 1
  loadExperienceShares()
}

const downloadFile = (item: any) => {
  //  window.open(`/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(newFileName)}`)
  window.open(`/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(item.fileName)}`)
}

const handleClose = (index: number) => {
  form.appendFileList.splice(index, 1)
}

onMounted(() => {
  loadExperienceShares()
})
</script>

<template>
  <div class="designExperience">
    <!-- <localTitle title="设计经验分享">
      <template #rightBtn>
        <el-button type="primary" icon="Plus" @click="openAddDialog">新增经验</el-button>
      </template>
    </localTitle>

      <div class="searchBar" style="display:flex;gap:8px;align-items:center;margin:12px 0">
        <el-input v-model="searchForm.title" placeholder="标题" clearable style="width:320px" maxlength="50"/>
        <el-input v-model="searchForm.contributor" placeholder="贡献人" clearable style="width:200px" maxlength="20"/>
        <el-button type="primary" @click="onSearch">查询</el-button>
        <el-button @click="onResetSearch">重置</el-button>
      </div> -->
      <div class="section">
        <div class="item-container">
          <div class="label">
            <div class="icon" />
            <div class="text">设计经验分享</div>
          </div>
          <div class="control-panel">
            <div class="controls">
              <div class="control">
                <div class="label">标题</div>
                <el-input v-model="searchForm.title" placeholder="标题" clearable style="width:320px" maxlength="50"/>
              </div>
              <div class="control">
                <div class="label">贡献人</div>
                <el-input v-model="searchForm.contributor" placeholder="贡献人" clearable style="width:200px" maxlength="20"/>
              </div>
              <div class="control">
                <div class="label">单位</div>
                <el-select v-model="searchForm.departmentId" placeholder="单位" clearable style="width:200px">
                  <el-option v-for="item in optionCb" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
              </div>
              <div class="control-buttons">
                <el-button type="primary" @click="onSearch()"> 查询 </el-button>
                <el-button type="warning" @click="onResetSearch()"> 重置 </el-button>
              </div>
            </div>
            <div class="right-buttons">
                <el-button type="primary" icon="Plus" @click="openAddDialog">新增经验</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="tableBox">
      <el-table v-loading="loading" :data="tableData" border tooltip-effect="light" style="width: 100%">
        <el-table-column label="序号" align="center" width="70">
          <template #default="scope">
            {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip header-align="center"/>
        <el-table-column prop="updateDate" label="最后更新" width="200" align="center" />
        <el-table-column prop="contributor" label="贡献人" width="120" align="center" />
        <el-table-column prop="organization" label="单位" width="120" align="center" show-overflow-tooltip />
        <el-table-column prop="contact" label="联系方式" width="120" align="center" >
          <template #default="{ row }">
            {{ row.contact || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="附件文件" width="200" align="center" >
          <template #default="{ row }">
            <el-tag v-for="item in row.appendFileList" :key="item.fileId" type="info" >
              <el-text type="primary" :title="item.fileName" truncated size="small" style="max-width: 140px;padding: 0 4px;margin:2px 0;cursor: pointer;" @click="downloadFile(item)">{{ item.fileName }}</el-text>
            </el-tag>

            <span v-if="row.appendFileList?.length === 0">-</span>
          </template>
        </el-table-column>
         <el-table-column prop="likeCount" label="点赞数量" width="120" align="center" />
        <el-table-column prop="likeCount" label="点赞" width="90" align="center" >
          <template #default="{ row }">
            <div style="display: flex;align-items: center;justify-content: center;">
              <el-icon :style="`color:${row.currentUserLiked?'darkorange':'#ccc'};cursor: pointer;margin-left: 4px;`" @click="handlelikeCount(row)"><Star /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" :width="isAdmin || isContributor ? 180 : 120" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openViewDialog(row)">查看</el-button>
            <el-button v-if="isAdmin || loginName===row.contributor" type="warning" size="small" style="margin-left: 8px" @click="openEditDialog(row)">编辑</el-button>
            <el-button v-if="isAdmin || loginName===row.contributor" type="danger" size="small" style="margin-left: 8px" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagesBox">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <el-dialog
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="padding-right: 20px;">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="50" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入内容" :rows="8" maxlength="3000" show-word-limit/>
        </el-form-item>
        <el-form-item label="贡献人" prop="contributor">
          <el-input v-model="form.contributor" disabled placeholder="请输入贡献人" />
        </el-form-item>
        <el-form-item label="单位" prop="organization">
          <el-input v-model="form.organization" disabled placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item v-if="form.id" label="最后更新">
          <el-input v-model="form.updateDate" disabled />
        </el-form-item>
        <el-form-item label="附件文件">
          <el-upload
            ref="uploadElement"
            :headers="headers"
            class="upload-demo"
            action="/circuitreview/common/v1/storage/upload"
            :show-file-list="false"
            :before-upload="handleBefore"
            :on-exceed="handleExceed"
            :on-error="handleError"
            :on-success="handleSuccess"
            :limit="1"
          >
            <el-button type="primary" class="export">上传附件</el-button>
          </el-upload>
          <div class="attachment-list" style="width: 100%;">
            <el-tag v-for="(item,index) in form.appendFileList" :key="item.fileId" type="info" style="margin-right: 4px;cursor: pointer;" closable @click="downloadFile(item)" @close="handleClose(index)">
              <el-text type="primary" :title="item.fileName" truncated size="small">{{ item.fileName }}</el-text>
            </el-tag>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </template>
    </el-dialog>


    <!-- 查看弹框 -->
    <DetailsDialog
        v-if="showViewDialog"
        v-model:show-modal-details="showViewDialog"
        :current-row="viewRowData"
      />
  </div>
</template>

<style scoped lang="scss">
.designExperience {
   width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 30px 0px;
    gap: 20px;
    overflow-y: auto;
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
    .pagesBox {
      padding: 0 10px;

      .el-pagination {
        margin: 0;
        padding: 7px 10px;
      }
    }
  }
}
</style>
