<!--
 * @Description: 用户列表
 * @Author: ym
 * @Date: 2021-03-05 09:32:17
 * @LastEditTime: 2022-03-17 17:14:18
-->
<template>
  <div class="userList">
    <div class="card">
      <div class="title">用户管理</div>
      <CustomTable
        class="list"
        :search-callback="searchCallback"
        :table-config="tableConfig"
        :is-hidden-index="true"
        :is-hightlight-row="true"
        :pagation-config="pagationConfig"
        :dis-row-selected="disRowSelected"
        :is-refresh="isRefresh1"
        @selectionChange="selectionChange"
        @rowClicked="userRowClicked"
      >
        <template #searchBtnList>
            <el-button type="primary" v-if="is631User" style="margin:0 10px;" @click="downloadTemplate">下载模板</el-button>
            <!-- <el-button type="primary" @click="importDepartment">导入</el-button> -->
            <el-upload
              ref="uploadElement"
              :headers="headers"
              class="upload-demo"
              action="/portal/v1/users/import"
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
      <div class="title">角色管理</div>
      <div class="list right">
        <div class="btns">
          <el-button type="primary" :disabled="currentRow && !currentRow.isEditable" :loading="isSaveLoading" @click="onRoleSave"> 保存 </el-button>
        </div>
        <div class="tabCon">
          <el-table key="id" ref="rightRolesTable" border tooltip-effect="light" :loading="isTableLoading" :height="`100%`" :data="roleList" @selection-change="handleRightSelectionChange">
            <el-table-column type="selection" width="55" :selectable="selectable" />
            <el-table-column prop="name" label="角色名称" />
            <el-table-column prop="comments" label="描述" />
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { GroupUserList, usersPasswordReset, delUserList, editUserLock, selectRoleList, roleUserEditor,getDepartmentSimpleItems,getDepartmentsSimpleItems,UserListRole } from "@/ajax"
import type { User, Role } from "@/types/system"
import { getFileSecretLevelEnumStr } from "@/hocks/fileSecretLevelEnum"
import md5 from "md5"
import { ElMessage,genFileId } from "element-plus"
import { getToken } from '@/utils/auth';

const is631User = computed(() => !store.state.isJiZaiUser)
console.log('is631User',is631User.value)
const router = useRouter()
const { proxy } = getCurrentInstance() as any
const USER_TYPE_CONFIG = [
  {
    "label": `普通用户`,
    "value": 0
  },
  {
    "label": `安全审计员`,
    "value": 1
  },
  {
    "label": `安全保密员`,
    "value": 2
  },
  {
    "label": `系统管理员`,
    "value": 3
  }
]
import store from "@/store/index"
const userInfo = computed(() => store.state.user.userInfo ?? {})
const roleInfo = computed(() => userInfo.value?.role)
const roleName = computed(() => {
  return roleInfo.value ? roleInfo.value.name : ``
})
const departmentId = computed(() => userInfo.value.departmentId ?? ``)

const downloadTemplate = ()=>{
  window.open(`/portal/v1/users/export`)
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
           isRefresh1.value = !isRefresh1.value
        }, 1000)
    } else {
        ElMessage.error(response.msg || `导入失败, 请重试`)
    }
}

const handleError = error => {
    ElMessage.error(`导入失败, 请重试`, error)
}
// departmentId
const selectable = (row: User) => {
  if (roleName.value.indexOf(`领导`)>-1) {
    if (row.name === `普通用户`) {
      return true
    } else {
      return false
    }
  } else if (roleName.value === `管理员`) {
    if (row.name === `管理员`) {
      return false
    } else {
      return true
    }
  } else {
    return true
  }
}

const rightRolesTable = ref()
const currentRow = ref<any>({ "id": ``, "loginName": `` })
const isTableLoading = ref(false)
const isSaveLoading = ref(false)
const isRefresh = ref(false)
const isRefresh1 = ref(false)
const roleList = ref([])
let roleDefaultSelected: Role | null = null
let roleSelect: Role | null = null
let msg: any = null
let select = ref<any>([])
const pagationConfig = reactive({
    "pageSize": 50,
    "pageNumber": 1
  })

// 角色列表
const roleOptions = ref<any[]>([])
const getRoleList = async () => {
  const res: any = await UserListRole({})
  console.log('角色列表数据:', res)
  const list = Array.isArray(res) ? res : (res?.content ?? res?.data ?? [])
  if (Array.isArray(list) && list.length) {
    roleOptions.value = list.map((item: any) => ({
      label: item.name,
      value: item.id
    }))
    console.log('roleOptions:', roleOptions.value)
  }
}
getRoleList()
  const disRowSelected = (row:any)=>{
    return row.id !== userInfo.value.id
  }

const tableConfig = reactive({
  "actionBtns": [
    {
      "icon": `Plus`,
      "type": `primary`, // 按钮类型
      "label": `新增`, // 按钮展示的文字
      // hasPermi: `user:btn:add`,
      "btnCallback": () => {
        router.push({
          "path": `/userAddEdit`,
          "query": {
            "type": 0,
            departmentId: departmentId.value
          }
        })
      } // 事件回调
    },
    {
      "icon": `Delete`,
      "type": `danger`, // 按钮类型
      // hasPermi: `user:btn:delete`,
      "label": `删除`, // 按钮展示的文字
      "isRefresh": true, // 执行完事件是否刷新页面
      "btnCallback": onDeleted // 事件回调
    }
  ],
  searchConfig:{
    searchForms:[
      {
        prop: `userName`,
        label: `用户名`,
        type: `input`,
        default:``,
        stripLike: true,
      },
      {
        prop: `loginName`,
        label: `登录名`,
        type: `input`,
        default:``,
        stripLike: true,
      },
      {
        prop: `roleId`,
        label: `角色`,
        type: `select`,
        optionCb: () => roleOptions.value,
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
  // "search": {
  //   "prop": `loginName`,
  //   "label": `登录用户名`,
  // },
  "tableHeader": [
    {
      "label": `登录名`,
      "prop": `loginName`
    },
    {
      "label": `用户名`,
      "prop": `name`
    },
    {
      "label": `所属单位`,
      "prop": `departmentName`
    },
    {
      "label": `角色`,
      "prop": `defaultRoleName`,
      "width": 140
      // textCallback:(item:any)=>{
      //   if(item.roleId&&item.roleId.length){
      //     return item.roleId.map((item: any) => item.name).join(`,`)
      //   }else{
      //     return ` `
      //   }
      // }
    },
    // {
    //   "label": `涉密级别`,
    //   "prop": `secretLevel`,
    //   "minwidth": 50,
    //   "textCallback": (item: User) => {
    //     console.log(`item`, item)
    //     return getFileSecretLevelEnumStr(item.secretLevel)
    //   }
    // },
    {
      "label": `状态`,
      "prop": `locked`,
      "align": `center`,
      "dotColorCallabck": (item: User) => (item.locked ? `red` : `green`),
      "textCallback": (item: User) => (item.locked ? `禁用` : `激活`)
    },
    {
      "label": `操作`,
      "prop": `id`,
      "width": 250,
      "btns": [
        {
          "label": `查看`,
          "hasPermi": `user:btn:look`,
          "btnCallback": (item: User) => {
            router.push({
              "path": `/userDetail`,
              "query": {
                "type": 2,
                "id": item.id
              }
            })
          }
        },
        //
        {
          "label": `编辑`,
          cbDisabled: row => !row.isEditable,
          "hasPermi": `user:btn:edit`,
          "btnCallback": (item: User) => {
            router.push({
              "path": `/userAddEdit`,
              "query": {
                "type": 1,
                "id": item.id,
                departmentId: departmentId.value
              }
            })
          }
        },

        {
          "label": `启用`,
          "isRefresh": true, // 执行完回调是否刷新页面
          cbDisabled: row => !row.isEditable,
          "hasPermi": `user:btn:edit`,
          "textCallback": (item: User) => (item.locked ? `激活` : `禁用`),
          "btnCallback": async (item: User) => {
            const confirm = await ElMessageBox.confirm(`此操作将${item.locked ? `激活` : `禁用`}用户, 是否继续?`, `提示？`, {
              "confirmButtonText": `确定`,
              "cancelButtonText": `取消`,
              "type": `warning`
            })
            if (confirm === `confirm`) {
              item.locked = !item.locked
              const res = await editUserLock({ ...item, newStatus: item.locked })

              setTimeout(() => {
                res.succ && ElMessage.success(`${!item.locked ? `激活` : `禁用`}成功!`)
              }, 400)
            }
          }
        },
        {
          "label": `重置密码`,
          "isRefresh": true, // 执行完回调是否刷新页面
          cbDisabled: row => !row.isEditable,
          "hasPermi": `user:btn:edit`,
          "btnCallback": async (item: User) => {
            const confirm = await ElMessageBox.confirm(`此操作将重置密码, 是否继续?`, `提示？`, {
              "confirmButtonText": `确定`,
              "cancelButtonText": `取消`,
              "type": `warning`
            })
            if (confirm === `confirm`) {
              const res = await usersPasswordReset({ "loginName": item.loginName, password: md5(`123456`) })
              setTimeout(() => {
                res.succ && proxy.$message.success(`重置成功!`)
              }, 400)
            }
            // if (
            //   await proxy.$confirm(`此操作将重置密码, 是否继续?`, `提示`, {
            //     "confirmButtonText": `确定`,
            //     "cancelButtonText": `取消`,
            //     "type": `warning`
            //   })
            // ) {
            //   const res = await usersPasswordReset({ "loginName": item.loginName })
            //   res.succ && proxy.$message.success(`重置成功!`)
            // }
          }
        }
      ]
    }
  ]
})



watch(currentRow as any, val => {
  if (val.id) {
    isRefresh.value = !isRefresh.value
  }
})

watch(roleName.value, () => {
  if(roleName.value.indexOf(`领导`) > -1){
    getDepartmentsSimpleItems({ departmentId: departmentId.value })
      .then(res => {
        if (res.succ) {
           if(res.content&&res.content.length){
            tableConfig.searchConfig.searchForms[3].optionCb = () => {
              return res.content.map((item:any)=>{
                return {
                  label: item.name,
                  value: item.id
                }
              })
            }
            tableConfig.searchConfig.searchForms[3].default = departmentId.value
          }
        }
      })
  }else{
    getDepartmentSimpleItems({}).then((res:any)=>{
      if(res.succ){
        if(res.content&&res.content.length){
          tableConfig.searchConfig.searchForms[3].optionCb = () => {
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

const searchCallback = (params: any) => GroupUserList({ departmentId: roleName.value.indexOf(`领导`) > -1 ? departmentId.value : ``, includeDepartmentName: true ,...params})
const selectionChange = (val: any) => {
  select.value = val
}
const handleRightSelectionChange = (selects: any[]) => {
  // 实现单选逻辑：如果选择了多个，只保留最后一个
  if (selects.length > 1) {
    // 取消之前选中的行
    const lastSelected = selects[selects.length - 1]
    rightRolesTable.value.clearSelection()
    // 只选中最后一个
    rightRolesTable.value.toggleRowSelection(lastSelected, true)
    roleSelect = lastSelected
  } else {
    roleSelect = selects.length > 0 ? selects[0] : null
  }
}

const userRowClicked = (row: any) => {
  roleDefaultSelected = null
  roleSelect = null
  currentRow.value = row
  getRoleListByUser()
}

const getRoleListByUser = async () => {
  isTableLoading.value = true
  const params = {
    "pageNumber": 1,
    "pageSize": 1000,
    "userId": currentRow.value.id
  }

  const res = await selectRoleList(params)
  if (res && res.succ) {
    roleList.value = res.content.records
    roleDefaultSelected = roleList.value.find((e: any) => e.selected) || null
    proxy.$nextTick(() => {
      if (roleDefaultSelected) {
        rightRolesTable.value.toggleRowSelection(roleDefaultSelected, true)
        roleSelect = roleDefaultSelected
      }
    })
  }
  isTableLoading.value = false
}

const getUserRolesParams = (role: any, selected: boolean) => {
  if (!role) return null
  return {
    "roleId": role.id,
    "roleName": role.name,
    "selected": selected,
    "userId": currentRow.value?.id,
    "userName": currentRow.value?.loginName
  }
}

const comparisonArray = () => {
  if (!roleSelect && !roleDefaultSelected) return true
  if (!roleSelect || !roleDefaultSelected) return false
  return roleSelect.id === roleDefaultSelected.id
}

const onRoleSave = async () => {
  msg?.close()
  if (comparisonArray()) {
    msg = proxy.$message.warning(currentRow?.value?.id ? `请先修改角色再保存！` : `请点选需要编辑的用户！`)
  } else {
    isSaveLoading.value = true
    const params: any[] = []

    // 如果之前有选中的角色，先取消选中
    if (roleDefaultSelected) {
      const cancelParam = getUserRolesParams(roleDefaultSelected, false)
      if (cancelParam) params.push(cancelParam)
    }

    // 如果当前选中了角色，添加选中
    if (roleSelect) {
      const selectParam = getUserRolesParams(roleSelect, true)
      if (selectParam) params.push(selectParam)
    }

    const res = await roleUserEditor(params)
    if (res.succ) {
      proxy.$message.success(`保存成功`)
      nextTick(()=>{
        isRefresh1.value = !isRefresh1.value
      })
      getRoleListByUser()
    }
    isSaveLoading.value = false
  }
}

async function onDeleted() {
  if (select.value.length) {
    const arr = select.value.map(r => {
      return {
        "id": r.id,
        "label": r.name,
        "version": r.version
      }
    })
    const confirm = await ElMessageBox.confirm(`此操作将删除选中的${arr.length}个用户, 是否继续?`, `提示？`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
    if (confirm === `confirm`) {
      const res = await delUserList(arr)
      res.succ && proxy.$message.success(`删除成功`)
    }
  } else {
    ElMessage.warning(`请勾选需要删除的用户`)
  }
}
defineExpose(USER_TYPE_CONFIG)
</script>
<style scoped lang="scss">
.userList {
  display: flex;
  .card {
    display: flex;
    flex-direction: column;
    background: #fff;
    &:first-child {
      margin-right: 4px;
      flex: 2;
    }
    &:last-child {
      flex: 1;
    }
    .title {
      font-size: 16px;
      padding: 10px 20px;
      background-color: #fafafa;
    }
    .list {
      flex: 1;
      :deep(.el-table){
        max-height: 670px;
      }
      &:first-child {
        margin-right: 4px;
      }
    }
    .right {
      padding: 10px 20px 20px 20px;
      display: flex;
      flex-direction: column;
      .btns {
        padding-bottom: 10px;
      }
      .tabCon {
        flex: 1;
        :deep(.el-table) {
          th.el-table__cell {
            background-color: #f5f5ff;
          }
          .el-table__header {
            .el-checkbox {
              display: none;
            }
          }
        }
      }
    }
  }
}
</style>
