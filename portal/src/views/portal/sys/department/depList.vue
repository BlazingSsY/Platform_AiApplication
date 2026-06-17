<template>
  <div class="getGroupsPages">
    <div class="card">
      <div class="title">组织管理</div>
      <CustomTable
        id="groupLeft"
        ref="groupLeftRef"
        class="list"
        :is-show-pagination="false"
        :default-expand-all="true"
        :search-callback="groupSearchCallback"
        :table-config="groupTableConfig"
        :is-hidden-index="true"
        :is-refresh="isGroupRefresh"
        :tree-config="{ children: 'childList', hasChildren: 'hasChildren' }"
        :is-hightlight-row="true"
        :dis-row-selected="
          (row: any) => {
            return row.name !== `中航机载`
          }
        "
        @rowClicked="groupRowClicked"
        @selectionChange="groupSelectionChange"
      >
          <template #searchBtnList>
            <el-button type="primary" v-if="is631User" style="margin:0 10px;" @click="downloadTemplate">下载模板</el-button>
            <el-upload
              ref="uploadElement"
              :headers="headers"
              class="upload-demo"
              action="/portal/v1/departments/import"
              :show-file-list="false"
              :before-upload="handleBefore"
              :on-exceed="handleExceed"
              :on-error="handleError"
              :on-success="handleSuccess"
              :limit="1"
          >
              <el-button v-if="is631User" type="primary" class="export">导入</el-button>
          </el-upload>
          </template>
    </CustomTable>
    </div>
    <div class="card">
      <div class="title">用户管理</div>
      <CustomTable
        id="groupRight"
        class="list"
        :is-hidden-index="true"
        :search-callback="userSearchCallback"
        :table-config="userTableConfig"
        :is-refresh="isRefresh"
        :is-show-pagination="false"
        @selectionChange="userSelectionChange"
      />
    </div>
    <el-dialog v-if="dialogVisible" v-model="dialogVisible" title="添加用户" width="60%" :before-close="() => (dialogVisible = false)">
      <div style="height: 500px;">
        <CustomTable :search-callback="addUserSearchCallback" :table-config="addUserTableConfig" :is-refresh="isRefreshAdd" @selectionChange="addUserSelectionChange" />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="isSaveLoading" @click="onDialogSaved">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { getGroupsTree,getGroupsTreeForLeader, GroupUserList, delDepartmentList, setDeptUser ,getDepartmentsSimpleItems,getDepartmentSimpleItems} from "@/ajax/index"
import type { Department, User } from "@/types/system"
import { ElMessage,genFileId } from "element-plus"
import { getToken } from '@/utils/auth';
import store from "@/store/index"
const userInfo = computed(() => store.state.user.userInfo ?? {})
const roleInfo = computed(() => userInfo.value?.role)
const roleName = computed(() => {
  return roleInfo.value ? roleInfo.value.name : ``
})
const departmentId = computed(() => userInfo.value.departmentId ?? ``)
const isAdmin = computed(() => roleInfo.value?.name === `管理员`)
const is631User = computed(() => !store.state.isJiZaiUser)
// isJiZaiUser === true：表示是"记载"部署环境 ； isJiZaiUser === false：表示是"631"部署环境

const { proxy } = getCurrentInstance() as any
const isRefresh = ref(false)
const isGroupRefresh = ref(false)
const currentGroup = ref<any>({ "id": ``, name: `` })
const isSaveLoading = ref(false)
const isRefreshAdd = ref(false)
const dialogVisible = ref(false)
let selectionGroups = ref<any>([])
let selectionUser: User[] = []
let msg: any = null
let addUserSelects: User[] = []
// <el-icon><Plus /></el-icon>
const downloadTemplate = ()=>{
  window.open(`/portal/v1/departments/export`)
}
// 导入
const accept = `.xlsx, .xls`
const headers = {
  "Authorization":getToken(`sys_tokenID`)
}
const handleBefore = file => {
    // eslint-disable-next-line no-useless-escape
    const suffix = accept.replace(/./g, ``).replace(/\,/, `|`)
    const fileType = new RegExp(suffix).test(file.type)
    const fileSize = file.size / 1024 / 1024 < 50

    if (!fileType) {
        ElMessage.error(`只支持 ${accept} 类型文件`)
    } else if (!fileSize) {
        ElMessage.error(`文件必须小于50M`)
    }

    return !!fileType && fileSize
}
const uploadElement = ref()
const handleExceed = files => {
    uploadElement.value!.clearFiles()
    const file = files[0]
    file.uid = genFileId()
    uploadElement.value!.handleStart(file)
    uploadElement.value!.submit()
}
const handleSuccess = (response: any) => {
    if (response.succ) {
        ElMessage.success(`导入成功`)
        setTimeout(() => {
           isRefresh.value = !isRefresh.value
           isGroupRefresh.value = !isGroupRefresh.value
        }, 1000)
    } else {
        ElMessage.error(response.msg || `导入失败, 请重试`)
    }
}

const handleError = error => {
    ElMessage.error(`导入失败, 请重试`, error)
}
const importDepartment = ()=>{}
const groupLeftRef = ref()
const groupTableConfig = {
  "actionBtns": [
    // {
    //   "icon": `Plus`,
    //   "type": `primary`, // 按钮类型
    //   "label": `新增根节点`, // 按钮展示的文字
    //   // "hasPermi": `dpm:add`,
    //   "btnCallback": () => btnClicked(`/depAddEdit`, { "type": 0 }) // 事件回调
    // },
    {
      "icon": `DocumentAdd`,
      "type": `primary`, // 按钮类型
      "label": `新增子节点`, // 按钮展示的文字
      // "hasPermi": `dpm:add`,
      "btnCallback": () => {
        console.log(`currentGroup.value`, currentGroup.value)
        console.log(groupLeftRef.value.tableData)
        
        if (currentGroup.value?.id) {
          // 检查当前节点的层级，限制只到2级
          // 通过检查是否有父级节点来判断层级
          // if ("fid" in currentGroup.value && currentGroup.value.fid && currentGroup.value.fid !== null) {
          //   // 如果当前节点有父级，说明已经是2级，不能再添加子节点
          //   ElMessage.warning(`子节点层级不能超过2级！`)
          //   return
          // }
          const findDepartmentLevel = (data, targetId, currentLevel = 1) => {
            for (const item of data) {
              if (item.id === targetId) {
                return currentLevel;
              }
              if (item.childList && item.childList.length > 0) {
                const level = findDepartmentLevel(item.childList, targetId, currentLevel + 1);
                if (level) {
                  return level;
                }
              }
            }
            return null;
          };
          // 获取当前节点的层级
          const currentLevel = findDepartmentLevel(groupLeftRef.value.tableData, currentGroup.value.id);
          console.log(`currentLevel`,currentLevel)
          console.log(`currentGroup.value`,currentGroup.value)

          // 根据部署类型设置不同的层级限制
          const maxLevel = is631User.value ? 2 : 3;
          if (currentLevel >= maxLevel) {
            ElMessage.warning(`子节点层级不能超过${maxLevel}级！`)
            return
          }
          btnClicked(`/depAddEdit`, { "type": 0, "fid": currentGroup.value.id, fidName: currentGroup.value.name,level:currentLevel,isOffice:currentGroup.value.isOffice })
        } else {
          ElMessage.warning(`请点选一个父级单位！`)
        }
      }
    },
    {
      "icon": `Delete`,
      "type": `danger`, // 按钮类型
      "label": `删除`, // 按钮展示的文字
      // "hasPermi": `dpm:del`,
      "isRefresh": true, // 执行完事件是否刷新页面
      "btnCallback": onDeleted // 事件回调
    },
  ],
  "search": {
    "prop": `name`,
    "label": `组织名称`
  },
  "tableHeader": [
    {
      "label": `组织名称`,
      "prop": `name`
    },
    // {
    //   "label": `单位类型`,
    //   "prop": `type`,
    //   "textCallback": (rowData: Department) => rowData.type
    // },
    {
      "label": `组织类型`,
      "prop": `isOffice`,
      "textCallback": (rowData: Department) => rowData.isOffice ? '部门' : '单位'
    },
    {
      "label": `组织描述`,
      "prop": `comments`
    },
    {
      "label": `创建人`,
      "prop": `createUserName`
    },
    {
      "label": `更新时间`,
      "prop": `updateDate`
    },
    {
      "label": `操作`,
      "prop": `id`,
      "minwidth": 80,
      "btns": [
        {
          "label": `查看`,
          // "hasPermi": `dpm:detail`,
          "btnCallback": (item: Department) => btnClicked(`/depDetail`, { "id": item.id })
        },
        {
          "label": `编辑`,
          // "hasPermi": `dpm:edit`,
          "btnCallback": (item: Department) => btnClicked(`/depAddEdit`, { "type": 1, "id": item.id })
        }
      ]
    }
  ]
}

const userTableConfig = {
  "actionBtns": [
    {
      "icon": `Plus`,
      "type": `primary`, // 按钮类型
      "label": `添加`, // 按钮展示的文字
      // "hasPermi": `dpm:edit`,
      "btnCallback": () => {
        if (currentGroup.value?.id) {
          if(roleName.value.indexOf(`领导`) > -1){
            addUserTableConfig.searchConfig.searchForms[3].default = departmentId.value
          }else{
            addUserTableConfig.searchConfig.searchForms[3].default = ``
          }
          dialogVisible.value = true
          isRefreshAdd.value = !isRefreshAdd.value
        } else {
          msg?.close()
          msg = proxy.$message.warning(`请点选需要编辑的单位！`)
        }
      } // 事件回调
    },
    {
      "icon": `Delete`,
      "type": `danger`, // 按钮类型
      "label": `移出`, // 按钮展示的文字
      // "hasPermi": `dpm:edit`,
      "isRefresh": true, // 执行完事件是否刷新页面
      "btnCallback": onRemove // 事件回调
    }
  ],
  // "search": {
  //   "prop": `loginName`,
  //   "label": `登录用户名`
  // },
  "tableHeader": [
    {
      "label": `用户名`,
      "prop": `name`
    },
    {
      "label": `登录名`,
      "prop": `loginName`
    },
    {
      "label": `邮箱`,
      "prop": `email`
    },
    {
      "label": `所属单位`,
      "prop": `departmentName`,
      // "iconCallback": (item: User) => (item.sex === `女` ? `icon-nv` : `icon-nan`),
      // "iconColorCallback": (item: User) => (item.sex === `女` ? `red` : `blue`)
    },
    {
      "label": `电话`,
      "prop": `telephone`
    }
  ]
}

const addUserTableConfig = reactive({
  // "search": {
  //   "prop": `loginName`,
  //   "label": `登录用户名`
  // },
  searchConfig:{
    searchForms:[
      {
        prop: `userName`,
        label: `用户名`,
        type: `input`,
        default:``,
      },
      {
        prop: `loginName`,
        label: `登录名`,
        type: `input`,
        default:``,
      },
      {
        prop: `email`,
        label: `邮箱`,
        type: `input`,
        default:``,
      },
      {
        prop: `departmentId`,
        label: `所属单位`,
        type: `select`,
        optionCb:  ()=>[],
        default:``,
      }
    ],
  },
  "tableHeader": [
    {
      "label": `用户名`,
      "prop": `name`
    },
    {
      "label": `登录名`,
      "prop": `loginName`
    },
    {
      "label": `邮箱`,
      "prop": `email`
    },
    {
      "label": `所属单位`,
      "prop": `departmentName`,
      // "label": `性别`,
      // "prop": `sex`,
      // "iconCallback": (item: User) => (item.sex === `女` ? `icon-nv` : `icon-nan`),
      // "iconColorCallback": (item: User) => (item.sex === `女` ? `red` : `blue`)
    },
    {
      "label": `电话`,
      "prop": `telephone`
    },
    {
      "label": `状态`,
      "prop": `locked`,
      "align": `center`,
      "dotColorCallabck": (item: User) => (item.locked === true ? `red` : `green`),
      "textCallback": (item: User) => (item.locked === true ? `禁用` : `激活`)
    },
    {
      "label": `描述`,
      "prop": `comments`
    },
    {
      "label": `创建时间`,
      "prop": `createDate`,
      "minwidth": 120,
    },
    // {
    //   "label": `详细地址`,
    //   "prop": `address`
    // }
  ]
})

watch(currentGroup, (value: any) => {
  if (value.id) {
    isRefresh.value = !isRefresh.value
  }
})
watch(roleName.value, () => {
  if(roleName.value.indexOf(`领导`) > -1){
    getDepartmentsSimpleItems({ departmentId: departmentId.value })
      .then(res => {
        if (res.succ) {
           if(res.content&&res.content.length){
            addUserTableConfig.searchConfig.searchForms[3].optionCb = () => {
              return res.content.map((item:any)=>{
                return {
                  label: item.name,
                  value: item.id
                }
              })
            }
            addUserTableConfig.searchConfig.searchForms[3].default = departmentId.value
          }
        }
      })
  }else{
    getDepartmentSimpleItems({}).then((res:any)=>{
      if(res.succ){
        if(res.content&&res.content.length){
          addUserTableConfig.searchConfig.searchForms[3].optionCb = () => {
            return res.content.map((item:any)=>{
              return {
                label: item.name,
                value: item.id
              }
            })
          }
        }
      }
    })
  }
 
},{immediate:true})

// 查询单位下用户
const userSearchCallback = (params: any) => {
  if (!currentGroup.value?.id) return []
  const params_ = {
    ...params,
    // include: true,
    "departmentId": currentGroup.value.id,
    includeDepartmentName: true
  }
  params_.pageSize = 10000
  return GroupUserList(params_)
}
// 查询游离用户
const addUserSearchCallback = (params: any) => {
  if (!currentGroup.value?.id) return []
  const params_ = {
    // include: ` `,
    // "departmentId": ` `,
    departmentId: roleName.value.indexOf(`领导`) > -1 ? departmentId.value : ``,
    includeDepartmentName: true,
    ...params,
  }
  return GroupUserList(params_)
}

const groupSearchCallback = (params: any) => isAdmin.value ? getGroupsTree(params) : getGroupsTreeForLeader(params)
// 勾选事件
const groupSelectionChange = (values: Department[]) => {
  selectionGroups.value = values
}
// 行点击事件
const groupRowClicked = (value: Department, column: any) => {
  currentGroup.value = value
}
const userSelectionChange = (values: User[]) => {
  selectionUser = values
}
const addUserSelectionChange = (values: any) => {
  addUserSelects = values
}

// 新增|编辑|查看
const btnClicked = (path: string, query: any) => {
  proxy.$router.push({
    "path": path,
    "query": query
  })
}

// 移出
async function onRemove() {
  if (selectionUser?.length > 0) {
    try {
      await ElMessageBox.confirm(`此操作将移除选中的${selectionUser.length}个用户, 是否继续?`, `提示`, {
        "confirmButtonText": `确定`,
        "cancelButtonText": `取消`,
        "type": `warning`
      }).then(async () => {
        const params = selectionUser.map((e: User) => ({
          // ownerId: this.currentGroup.id,
          "ownerId": ``,
          // groupName: this.currentGroup.name,
          // selected: false,
          "id": e.id
          // label: e.loginName
        }))
        console.log(params)
        const res = await setDeptUser(params)
        res.succ && proxy.$message.success(`移出成功`)
      })
    } catch (error) {
      return Promise.reject(new Error(`cancel`))
    }
  } else {
    msg?.close()
    msg = proxy.$message.warning(!currentGroup.value ? `请点选需要编辑的单位！` : `请勾选需要移出的用户`)
    return Promise.reject(new Error(`cancel`))
  }
}

// 批量删除
async function onDeleted() {
  if (selectionGroups.value.length > 0) {
    try {
      await ElMessageBox.confirm(`此操作将删除选中的${selectionGroups.value.length}个单位, 是否继续?`, `提示`, {
        "confirmButtonText": `确定`,
        "cancelButtonText": `取消`,
        "type": `warning`
      }).then(async () => {
        console.log(selectionGroups.value)
        const submitData = selectionGroups.value.map((item: Department) => ({ "id": item.id }))
        const res = await delDepartmentList(submitData)
        if (res.succ) {
          ElMessage.success(`删除成功`)
        }
      })
    } catch (error) {
      return Promise.reject(new Error(`cancel`))
    }
  } else {
    msg?.close()
    msg = proxy.$message.warning(`请勾选需要删除的单位`)
    return Promise.reject(new Error(`cancel`))
  }
}
const onDialogSaved = async () => {
  if (addUserSelects?.length > 0) {
    isSaveLoading.value = true
    const params = addUserSelects.map((e: User) => ({
      "ownerId": currentGroup.value?.id,
      "id": e.id
    }))
    const res = await setDeptUser(params)
    isSaveLoading.value = false
    if (res.succ) {
      ElMessage.success(`添加成功`)
      dialogVisible.value = false
      addUserSelects = []
      isRefresh.value = !isRefresh.value
    }
  } else {
    ElMessage.warning(`请至少勾选一个要加入单位的用户`)
  }
}
</script>
<style lang="scss" scoped>
.getGroupsPages {
  display: flex;
  .card {
    display: flex;
    flex-direction: column;
    background: #fff;
    flex: 1;
    padding-bottom: 20px;
    :deep(.el-table) {
      max-height: 800px;
    }
    &:first-child {
      margin-right: 4px;
    }
    .title {
      font-size: 16px;
      padding: 10px 20px;
      background-color: #fafafa;
    }
    .list {
      flex: 1;
      width: 100%;
      &:first-child {
        margin-right: 4px;
      }
    }
  }
}
:deep(.el-dialog__body) {
  padding: 0px;
}
</style>
