<template>
  <div class="roleList">
    <div class="left">
      <CustomTable
        :search-callback="searchCallback"
        :table-config="tableConfig"
        :is-hightlight-row="true"
        :dis-row-selected="disRowSelected"
        @selectionChange="selectionChange"
        @rowClicked="rowClicked"
      />
    </div>
    <div class="right">
      <div class="title">权限管理</div>
      <div class="tree">
        <el-button type="primary" class="btn" :disabled="currentRow && `isEditable` in currentRow ? !currentRow.isEditable : false" :loading="isBtnLoading" @click="onPowerSaved"> 保存 </el-button>
        <el-tree
          ref="treeRef"
          v-loading="treeLoading"
          default-expand-all
          show-checkbox
          node-key="id"
          :indent="20"
          highlight-current
          :data="powerList"
          :props="{ children: `childList`, label: `name` }"
          @check="powerTreeChange"
        />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from "vue-router"
import { ElMessage, ElMessageBox, ElTree } from "element-plus"
import { getUserRoleList, delRoleList, getUserPermissions, setUserRolePermissions } from "@/ajax"
import type { Role } from "@/types/system"

const { proxy } = getCurrentInstance() as any

const treeRef = ref<InstanceType<typeof ElTree>>()
// 路由
const router = useRouter()
let selects = reactive([] as Role[])
let msg: any = null
let currentRow: Role | null = reactive({} as unknown as any)
let powerList: any[] = reactive([])
const isBtnLoading = ref(false)
const treeLoading = ref(false)
let defaultCheckedKeys: any[] = reactive([])
let currentCheckedKeys: any[] = reactive([])
const tableConfig = reactive({
  "actionBtns": [
    {
      "icon": `Plus`,
      "type": `primary`, // 按钮类型
      "label": `新增`, // 按钮展示的文字
      // hasPermi: `role:btn:add`,
      "btnCallback": () => btnClicked(`/roleAddEdit`, { "type": 0 }) // 事件回调
    },
    {
      "icon": `Delete`,
      "type": `danger`, // 按钮类型
      "label": `删除`, // 按钮展示的文字
      // hasPermi: `role:btn:delete`,
      "isRefresh": true, // 执行完事件是否刷新页面
      "btnCallback": onDeleted // 事件回调
    }
  ],
  "search": {
    "prop": `name`,
    "label": `角色名称`,
    "hasPermi": `role:btn:search`
  },
  "tableHeader": [
    {
      "label": `角色名称`,
      "prop": `name`
    },
    {
      "label": `角色类型`,
      "prop": `type`
    },
    {
      "label": `更新时间`,
      "prop": `updateDate`
    },
    {
      "label": `描述`,
      "prop": `comments`
    },
    {
      "label": `操作`,
      "prop": `id`,
      "minwidth": 120,
      "btns": [
        {
          "label": `查看`,
          // hasPermi: `role:btn:look`,
          "btnCallback": (item: Role) => btnClicked(`/roleDetail`, { "id": item.id })
        },
        {
          "label": `编辑`,
          // hasPermi: `role:btn:edit`,
          "btnCallback": (item: Role) => btnClicked(`/roleAddEdit`, { "type": 1, "id": item.id }),
          "cbDisabled": (item: Role) => !item.isEditable
        },
        {
          // hasPermi: `role:btn:people`,
          "label": `查看人员`,
          "btnCallback": (item: Role) => btnClicked(`/roleUser`, { "type": 2, "id": item.id })
        }
      ]
    }
  ]
})
const searchCallback = (params: any) => getUserRoleList(params)
const selectionChange = (select: Role[]) => {
  selects = select
}
// 操作类型 0：新建 1：编辑 2：查看
const btnClicked = (path: string, params: object) => {
  router.push({
    "path": path,
    "query": { ...params }
  })
}
const disRowSelected = (row: Role) => row.isEditable
async function onDeleted() {
  if (selects.length === 0) {
    msg?.close()
    msg = proxy.$message.warning(`请勾选需要删除的用户`)
    return Promise.reject(new Error(`cancel`))
  }
  try {
    await ElMessageBox.confirm(`此操作将删除数据, 是否继续?`, `提示`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
    const res = await delRoleList(selects.map((e: any) => ({ "id": e.id })))
    res.succ && ElMessage.success(`删除成功`)
  } catch (error) {
    return Promise.reject(new Error(`cancel`))
  }
}
const rowClicked = async (row: Role) => {
  currentCheckedKeys = []
  currentRow = row
  treeLoading.value = true
  const res = await getUserPermissions({
    "id": row.id
  })
  if (res.succ) {
    powerList = Object.assign(powerList, res.content)
    // 递归获取已经选中的节点
    defaultCheckedKeys = getDeault(res.content) as never[]
    currentCheckedKeys = [...defaultCheckedKeys]
    nextTick(() => {
      treeRef.value && treeRef.value.setCheckedNodes && treeRef.value.setCheckedNodes(defaultCheckedKeys)
    })
  }
  treeLoading.value = false
}
const getDeault = (values: any[]) => {
  const result: any[] = []
  values.forEach(e => {
    if (e.childList && e.childList.length > 0) {
      const defaults_ = getDeault(e.childList)
      result.push(...defaults_)
    }
    if (e.selected) {
      result.push(e)
    }
  })
  return result
}
const powerTreeChange = (tree: any, checked: any) => {
  currentCheckedKeys = checked.checkedNodes
}
const comparisonArray = () => {
  const selectedIds = currentCheckedKeys
    .map((e: any) => e.id)
    .sort()
    .toString()
  const defaultSelected_ = defaultCheckedKeys
    .map((e: any) => e.id)
    .sort()
    .toString()
  return selectedIds === defaultSelected_
}
const onPowerSaved = async () => {
  // 选中行是否有改动
  msg?.close()
  if (!(currentRow && currentRow.id)) {
    msg = ElMessage.warning(`请点选需要编辑的角色`)
  } else if (comparisonArray()) {
    msg = ElMessage.warning(`没有改动无需保存`)
  } else {
    const checkedKeys: any[] = []
    // 需要取消的数据
    defaultCheckedKeys.forEach((e: any) => {
      if (!currentCheckedKeys.find((el: any) => el.id === e.id)) {
        e.id &&
          checkedKeys.push({
            "powerId": e.id,
            "powerName": e.name,
            "roleId": currentRow?.id,
            "roleName": currentRow?.name,
            "selected": false
          })
      }
    })
    // 需要选中的数据（过滤掉之前已保存的数据)
    currentCheckedKeys
      .filter((e: any) => !defaultCheckedKeys.find((el: any) => el.id === e.id))
      .forEach((ele: any) => {
        ele.id &&
          checkedKeys.push({
            "powerId": ele.id,
            "powerName": ele.name,
            "roleId": currentRow?.id,
            "roleName": currentRow?.name,
            "selected": true
          })
      })
    if (checkedKeys.length === 0) {
      msg = ElMessage.warning(`没有改动无需保存`)
    } else {
      isBtnLoading.value = true
      const result_ = await setUserRolePermissions(checkedKeys)
      result_.succ && ElMessage.success(`保存成功`)
      result_.succ && (defaultCheckedKeys = [...currentCheckedKeys])
      isBtnLoading.value = false
    }
  }
}
</script>
<style scoped lang="scss">
.roleList {
  height: 100%;
  width: 100%;
  display: flex;
  .right {
    background: #fff;
    width: 20%;
    min-width: 200px;
    max-width: 300px;
    margin-left: 4px;
    display: flex;
    flex-direction: column;
    max-height: 600px;
    .tree {
      overflow-y: auto;
      flex: 1;
    }
    .title {
      padding: 20px;
      border-bottom: 1px solid #eee;
    }
    .btn {
      margin: 10px 20px;
    }
  }
  .left {
    background: #fff;
    flex: 1;
  }
}
</style>
