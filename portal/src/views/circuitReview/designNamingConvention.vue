<script setup lang='ts'>
  import localTitle from "./components/localTitle.vue"
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { addNamingConvention, namingConventionPages, namingConventionEdit, namingConventionDel } from '@/ajax/circuitreview'
  import store from "@/store/index"
  
  const isAdmin = computed(() => store.state.user?.userInfo?.role?.name===`管理员`)
  
  console.log(`isAdmin`, isAdmin)
  
  // 表单数据
  const namingConventionForm = reactive({
    title: '',
    content: '',
    id: '',
  })
  
  // 分页数据
  const pagination = ref({
    current: 1,
    size: 50,
    total: 0
  })
  
  // 列表数据
  const list = ref([])
  
  // 对话框状态
  const dialogVisible = ref(false)
  const dialogTitle = ref('添加命名规范')
  
  // 加载状态
  const loading = ref(false)
  
  // 表单验证规则
  const rules = {
    title: [
      { required: true, message: '请输入标题', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    content: [
      { required: true, message: '请输入内容', trigger: 'blur' },
      { min: 5, message: '内容长度不能少于 5 个字符', trigger: 'blur' }
    ]
  }
  
  // 表单引用
  const formRef = ref()
  
  // 获取列表数据
  const getList = async () => {
    loading.value = true
    try {
      const params = {
        page: pagination.value.current,
        size: pagination.value.size,
        title: '', // 可根据需要添加搜索条件
        content: ''
      }
      const res = await namingConventionPages(params)
      if (res.succ) {
        list.value = res.content?.records || []
        pagination.value.total = res.content?.total || 0
      } else {
        ElMessage.error('获取列表失败')
      }
    } catch (error) {
      ElMessage.error('获取列表失败')
    } finally {
      loading.value = false
    }
  }
  
  // 重置表单
  const resetForm = () => {
    namingConventionForm.title = ''
    namingConventionForm.content = ''
    namingConventionForm.id = ''
    if (formRef.value) {
      formRef.value.resetFields()
    }
  }
  
  // 打开添加对话框
  const openAddDialog = () => {
    resetForm()
    dialogTitle.value = '添加命名规范'
    dialogVisible.value = true
  }
  
  // 打开编辑对话框
  const openEditDialog = (row) => {
    namingConventionForm.title = row.title
    namingConventionForm.content = row.content
    namingConventionForm.id = row.id
    dialogTitle.value = '编辑命名规范'
    dialogVisible.value = true
  }
  
  // 保存数据
  const saveData = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          let res
          if (namingConventionForm.id) {
            // 编辑
            res = await namingConventionEdit(namingConventionForm)
          } else {
            // 添加
            res = await addNamingConvention(namingConventionForm)
          }
          if (res.succ) {
            ElMessage.success(namingConventionForm.id ? '编辑成功' : '添加成功')
            dialogVisible.value = false
            getList() // 重新获取列表
          } else {
            ElMessage.error(namingConventionForm.id ? '编辑失败' : '添加失败')
          }
        } catch (error) {
          ElMessage.error(namingConventionForm.id ? '编辑失败' : '添加失败')
        }
      }
    })
  }
  
  // 删除数据
  const deleteData = async (id) => {
    try {
      await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      const res = await namingConventionDel({ id })
      if (res.succ) {
        ElMessage.success('删除成功')
        getList() // 重新获取列表
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }
  
  // 分页变化
  const handleSizeChange = (size) => {
    pagination.value.size = size
    pagination.value.current = 1
    getList()
  }
  
  const handleCurrentChange = (current) => {
    pagination.value.current = current
    getList()
  }
  
  // 初始化时获取列表数据
  getList()
  </script>
  
  <template>
    <div class="designNamingConvention">
      <localTitle title="设计命名规范管理">
        <template #rightBtn>
          <el-button v-if="isAdmin" type="primary" @click="openAddDialog">添加规范</el-button>
        </template>
      </localTitle>
      <div class="tableBox">
          <!-- 列表表格 -->
          <el-table v-loading="loading" border class="custom-table-size" tooltip-effect="light" :data="list" style="width: 100%">
              <el-table-column label="序号" align="center" width="64">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="title" label="标题" min-width="50" header-align="center" />
              <el-table-column prop="content" label="内容" min-width="360" header-align="center" />
              <el-table-column v-if="isAdmin" label="操作" width="170" align="center">
                  <template #default="scope">
                  <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="deleteData(scope.row.id)">删除</el-button>
                  </template>
              </el-table-column>
          </el-table>
      </div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
  
      <!-- 添加/编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
      >
        <el-form
          ref="formRef"
          :model="namingConventionForm"
          :rules="rules"
          label-width="80px"
        >
          <el-form-item label="标题" prop="title">
            <el-input v-model="namingConventionForm.title" placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input
              v-model="namingConventionForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入内容"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveData">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <style lang='scss' scoped>
  .designNamingConvention {
    padding:0 20px;
    display: flex;
    flex-direction: column;
    .content-text {
      display: inline-block;
      width: 100%;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .tableBox{
      flex: 1;
      height: calc(100vh - 40px);
    }
  
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
  </style>