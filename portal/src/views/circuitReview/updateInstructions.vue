<script setup lang="ts">
import localTitle from "./components/localTitle.vue"
import { ref, reactive, onMounted } from "vue"
import { ElMessage, ElMessageBox,dayjs } from "element-plus"
import { getUpdateNotePage, delUpdateNote, createUpdateNote, updateUpdateNote } from '@/ajax/circuitreview';
import store from "@/store/index"
const isAdmin = computed(() => store.state.user?.userInfo?.role?.name===`管理员`)
interface UpdateItem {
  id: number
  version?: number
  updateTime: string
  content: string
}

interface UpdateForm {
  id: number | null
  updateTime: Date | string
  content: string
}

const tableData = ref<UpdateItem[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const disabledDate = (time) => {
  // 小于今天零点全部禁用
  return time.getTime() < new Date().setHours(0, 0, 0, 0)
}
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view'>('add')
const dialogTitle = ref('添加说明')
const formRef = ref()

const form = reactive<UpdateForm>({
  id: null,
  updateTime: '',
  content: ''
})

const rules = {
  updateTime: [{ required: true, message: '请输入更新时间', trigger: 'blur' }],
  content: [{ required: true, message: '请输入更新内容', trigger: 'blur' }]
}


const resetForm = () => {
  form.id = null
  form.updateTime = dayjs().format(`YYYY-MM-DD HH:mm:ss`)
  form.content = ''
  formRef.value?.clearValidate()
}

const loadUpdateNotes = () => {
  // 这里对接后端：发送 current 为 0-based（后端示例使用 0 起始），模板中保留 1-based
  //  pageNumber: pagination.value.current,
      // pageSize: pagination.value.size,
  getUpdateNotePage({ pageNumber: currentPage.value, pageSize: pageSize.value }).then((res: any) => {
    // 兼容后端包装：{ succ, code, msg, content: { records, total, current, size }}
      if (res.succ) {
        const data = res.content || res
        const records = (data && (data.records || data.list)) || []
        tableData.value = Array.isArray(records) ? records : []
        total.value = (data && (data.total ?? data.totalCount ?? 0)) || tableData.value.length
      } else {
        ElMessage.error(res.msg || '加载更新说明失败')
      }
   
  }).catch(() => {
    ElMessage.error('加载更新说明失败')
  })
}

onMounted(() => {
  loadUpdateNotes()
})

const openAddDialog = () => {
  dialogMode.value = 'add'
  dialogTitle.value = '添加说明'
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row: UpdateItem) => {
  dialogMode.value = 'edit'
  dialogTitle.value = '编辑说明'
  form.id = row.id
  form.updateTime = row.updateTime ?? dayjs().format(`YYYY-MM-DD HH:mm:ss`)
  form.content = row.content
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const openViewDialog = (row: UpdateItem) => {
  dialogMode.value = 'view'
  dialogTitle.value = '查看说明'
  form.id = row.id
  form.updateTime = row.updateTime ?? dayjs().format(`YYYY-MM-DD HH:mm:ss`)
  form.content = row.content
  dialogVisible.value = true
}

const saveForm = () => {
  if (dialogMode.value === 'view') {
    dialogVisible.value = false
    return
  }
  if (!formRef.value) return
  formRef.value.validate((valid: boolean) => {
    if (!valid) return
    if (dialogMode.value === 'add') {
      const payload = {
        updateTime: form.updateTime,
        content: form.content
      }
      createUpdateNote(payload).then((res: any) => {
          if (res.succ) {
            ElMessage.success('添加成功')
            loadUpdateNotes()
          } else {
            ElMessage.error(res.msg || '添加失败')
          }
       
      }).catch(() => {
        ElMessage.error('添加失败')
      })
    } else if (dialogMode.value === 'edit' && form.id !== null) {
      updateUpdateNote(form.id, { updateTime: form.updateTime, content: form.content }).then((res: any) => {
          if (res.succ) {
            ElMessage.success('编辑成功')
            loadUpdateNotes()
          } else {
            ElMessage.error(res.msg || '编辑失败')
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

const handleDelete = (row: UpdateItem) => {
  ElMessageBox.confirm(`确定删除这条更新说明吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    delUpdateNote(row.id).then((res: any) => {
        if (res.succ) {
          ElMessage.success('删除成功')
          loadUpdateNotes()
        } else {
          ElMessage.error(res.msg || '删除失败')
        }
     
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  })
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadUpdateNotes()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadUpdateNotes()
}
</script>

<template>
  <div class="updateInstructions">
    <localTitle title="更新说明">
      <template #rightBtn>
        <el-button v-if="isAdmin" type="primary" icon="Plus" @click="openAddDialog">添加说明</el-button>
      </template>
    </localTitle>

    <div class="tableBox">
      <el-table :data="tableData" border tooltip-effect="light" style="width: 100%">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="更新内容" min-width="360" header-align="center">
          <template #default="{ row }">
            <div class="content-cell">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center"/>
        <el-table-column label="操作" :width="isAdmin?180:100" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openViewDialog(row)">查看</el-button>
            <el-button v-if="isAdmin" type="warning" size="small" style="margin-left: 8px" @click="openEditDialog(row)">编辑</el-button>
            <el-button v-if="isAdmin" type="danger" size="small" style="margin-left: 8px" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div style="text-align: right; margin-top: 12px;">
      <el-pagination
        background
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="sizes, prev, pager, next, total"
        :page-sizes="[10,20,50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" status-icon style="padding-right: 20px;">
        <el-form-item label="更新内容" prop="content">
          <el-input v-model="form.content" :rows="10" type="textarea" :disabled="dialogMode === 'view'" placeholder="请输入更新内容" maxlength="500" show-word-limit />
        </el-form-item>
         <el-form-item label="更新时间" prop="updateTime">
          <el-date-picker
            v-model="form.updateTime"
            type="datetime"
            placeholder="请选择更新时间"
            :disabled="dialogMode === 'view'"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button v-if="dialogMode !== 'view'" @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogMode !== 'view'" type="primary" @click="saveForm">确定</el-button>
        <el-button v-else type="primary" @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.updateInstructions {
  .tableBox {
    padding: 10px 0;
    .el-table {
      background: #ffffff;
    }
  }
}
.content-cell {
  white-space: pre-wrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>