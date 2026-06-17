<template>
  <div class="departmentList">
    <CustomTable
      :table-config="tableConfig"
      :is-hightlight-row="true"
      :search-callback="searchCallback"
      :is-hidden-index="true"
      :tree-config="{ children: 'childList', hasChildren: 'hasChildren' }"
      :is-show-pagination="false"
      @selectionChange="selectionChange"
      @rowClicked="rowClicked"
      @cbCurrentTotal="cbCurrentTotal"
    />
  </div>
</template>
<script lang="ts" setup>
import { menuList, menuDelete } from "@/ajax/index"
import { ElMessage } from "element-plus"
const { proxy } = getCurrentInstance() as any
// 路由
const router = useRouter()
let msg = ref(null as any)
let currentRow = ref(null as any)
const total = ref(0)
let selects = reactive([])
const onDeleted = async () => {
  console.log(`selects`, selects)
  if (selects.length === 0) {
    msg.value?.close()
    msg.value = ElMessage.warning(`请勾选需要删除的节点`)
    return Promise.reject(new Error(`cancel`))
  }
  try {
    await ElMessageBox.confirm(`此操作将删除选中的${selects.length}条数据, 是否继续?`, `提示`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
    const res = await menuDelete(selects)
    res && res.succ && ElMessage.success(`已删除`)
  } catch (error) {
    return Promise.reject(new Error(`cancel`))
  }
}
const tableConfig = reactive({
  "actionBtns": [
    {
      "icon": `Plus`,
      "type": `primary`, // 按钮类型
      "label": `新增根节点`, // 按钮展示的文字
      // hasPermi: `menu:btn:add`,
      "btnCallback": () => btnClicked(`/menuEdit`, { "type": 0, "total": total }) // 事件回调
    },
    {
      "icon": `Plus`,
      "type": `primary`, // 按钮类型
      // hasPermi: `menu:btn:add`,
      "label": `新增子节点`, // 按钮展示的文字
      "btnCallback": () => {
        if (currentRow && currentRow.value.id) {
          btnClicked(`/menuEdit`, {
            "type": 0,
            "menuObj": JSON.stringify(currentRow),
            "total": total,
            "fid": currentRow.value.id
          })
        } else {
          msg.value?.close()
          msg.value = ElMessage.warning(`请点选一个父级节点！`)
        }
      }
    },
    {
      "icon": `Delete`,
      "type": `danger`, // 按钮类型
      // hasPermi: `menu:btn:delete`,
      "label": `删除`, // 按钮展示的文字
      "isRefresh": true, // 执行完事件是否刷新页面
      "btnCallback": onDeleted // 事件回调
    }
  ],
  // search: {
  //   prop: `name`,
  //   label: `菜单名称`
  // },
  "tableHeader": [
    {
      "label": `菜单名称`,
      "prop": `name`,
      "minwidth": 120
    },
    // {
    //   label: `所属用户`,
    //   prop: `type`
    // },
    {
      "label": `菜单类型`,
      "prop": `menuType`,
      "textCallback": (item: any) => {
        if (item.menuType === `M`) {
          return `目录`
        }
        if (item.menuType === `C`) {
          return `菜单`
        }
        return `按钮`
      }
    },
    {
      "label": `排序`,
      "prop": `sequence`
    },
    {
      "label": `图标`,
      "prop": `icon`,
      "iconCallback": (item: any) => item.icon
    },
    {
      "label": `权限标识`,
      "prop": `alias`,
      "minwidth": 120
    },
    {
      "label": `状态`,
      "prop": `enabled`,
      "textCallback": (item: any) => (item.enabled ? `启用` : `禁用`)
    },
    {
      "label": `显示状态`,
      "prop": `visible`,
      "textCallback": (item: any) => (item.visible ? `显示` : `隐藏`)
    },
    {
      "label": `操作`,
      "prop": `id`,
      "minwidth": 80,
      "btns": [
        {
          "label": `查看`,
          // hasPermi: `menu:btn:look`,
          "btnCallback": (item: any) => btnClicked(`/menuEdit`, { "type": 2, "menuObj": JSON.stringify(item) })
        },
        {
          "label": `编辑`,
          // hasPermi: `menu:btn:edit`,
          "btnCallback": (item: any) => btnClicked(`/menuEdit`, { "type": 1, "menuObj": JSON.stringify(item) })
        }
      ]
    }
  ]
})
const searchCallback = (params: any) => menuList(params)
// 操作类型 0：新建 1：编辑 2：查看
const btnClicked = (path: string, params: any) => {
  router.push({
    "path": path,
    "query": { ...params }
  })
}
const cbCurrentTotal = (tot: number) => {
  total.value = tot
}
const selectionChange = (select: any) => {
  selects = select
}
const rowClicked = (row: any) => {
  currentRow.value = row
}
</script>
<style scoped>
.departmentList {
  height: 100%;
  background: #fff;
}
</style>
