<!--
 * @Description: 页面表格组件
 * @Author: ym
 * @Date: 2021-03-05 09:41:16
 * @LastEditTime: 2022-04-06 14:33:42
-->
<template>
  <div class="customTable">
    <div v-if="tableConfig.searchConfig && tableConfig.searchConfig.searchForms" v-hasPermi="tableConfig.searchConfig && tableConfig.searchConfig.hasPermi" class="search">
      <el-form inline :model="searchFromData" class="searchFrom">
        <el-form-item v-for="(formItem, fIndex) in tableConfig.searchConfig.searchForms" :key="(formItem.prop || fIndex) + `_search_form`" :label="formItem.label">
          <el-input v-if="formItem.type === `input`" v-model.trim="searchFromData[formItem.prop]" :placeholder="`请输入` + formItem.label" @input="(val: string) => onSearchInput(val, formItem)" />
          <el-select v-else-if="formItem.type === `select`" v-model="searchFromData[formItem.prop]" :placeholder="`请选择` + formItem.label">
            <el-option v-for="(op, oIndex) in formItem.optionCb ? formItem.optionCb() : []" :key="formItem.prop + oIndex + `_select_option`" :label="op.label" :value="op.value" />
          </el-select>
          <el-date-picker v-else-if="formItem.type === `dateRange`" v-model="timerRange[formItem.prop]" type="daterange" end-placeholder="结束日期" range-separator="至" start-placeholder="开始日期" />
          <el-date-picker v-else-if="formItem.type === `date`" v-model="searchFromData[formItem.prop]" type="date" placeholder="选择日期" />
          <el-date-picker v-else-if="formItem.type === `month`" v-model="searchFromData[formItem.prop]" type="month" placeholder="选择月份" />
          <template v-if="fIndex + 1 === tableConfig.searchConfig.searchForms.length">
            <el-button class="saveBtn" type="primary" @click="onSearchClicked">查询</el-button>
            <el-button @click="onReset">重置</el-button>
          </template>
        </el-form-item>
      </el-form>
    </div>
    <div v-if="tableConfig.actionBtns && tableConfig.actionBtns.length" class="top">
      <div class="btns">
        <el-button
          v-for="btn in tableConfig.actionBtns"
          :key="btn.label"
          v-hasPermi="btn.hasPermi"
          :type="btn.type"
          :icon="btn.icon"
          size="default"
          :loading="isLoading[btn.label + btn.icon]"
          :disabled="btn.cbDisabled ? btn.cbDisabled() : false"
          @click="onActionsClicked(btn)"
        >
          {{ btn.label }}
        </el-button>
      </div>
      <el-input v-if="tableConfig.search" v-model="searchValue" v-hasPermi="tableConfig.search.hasPermi" :placeholder="`请输入${tableConfig.search.label}查询`" class="searchInput">
        <template #suffix>
          <el-icon @click="onSearchClicked"><Search /></el-icon>
        </template>
      </el-input>

      <slot name="searchBtnList" />
    </div>
    <div class="tableCon">
      <el-table
        :ref="id"
        v-loading="isTableLoading"
        :data="tableData"
        :default-expand-all="defaultExpandAll"
        row-key="id"
        stripe
        height="100%"
        :highlight-current-row="isHightlightRow"
        :tree-props="treeConfig"
        @select-all="selectAll"
        @select="selectionChange"
        @row-click="rowClicked"
      >
        <el-table-column v-if="isShowSelect" type="selection" width="55" :selectable="disRowSelected as any" align="center" />
        <el-table-column v-if="!isHiddenIndex" label="序号" type="index" :width="isShowSelect ? 55 : 70" align="center" />
        <el-table-column
          v-for="(header, index) in tableConfig.tableHeader"
          :key="index + `_header`"
          :min-width="header.minwidth || `auto`"
          :width="header.width || `auto`"
          :prop="header.prop"
          :label="header.label"
          show-overflow-tooltip
          :align="header.align || `left`"
        >
          <!-- <slot slot="header">
            <span>{{header.label}}</span>
            <slot v-if="header.headerSolt" :name="header.headerSolt"></slot>
          </slot> -->
          <!-- <template v-slot:header>
            <el-tooltip class="title" effect="dark" :content="header.label" placement="top" >
              <div v-auto="true">
                <span class="t-header-text">{{header.label}}</span>
              </div>
            </el-tooltip>
          </template> -->
          <template #default="scope">
            <template v-if="header.iconCallback">
              <i class="iconfont" :class="header.iconCallback(scope.row)" :style="`color: ${header.iconColorCallback ? header.iconColorCallback(scope.row) : `#444`}`" />
            </template>
            <template v-else-if="header.btns">
              <template v-for="(btn, bIndex) in header.btns">
                <el-button
                  v-if="btn.hide ? !btn.hide(scope.row) : true"
                  :key="bIndex"
                  type="text"
                  :disabled="btn.cbDisabled ? btn.cbDisabled(scope.row) : false"
                  :loading="isLoading[scope.row.id + btn.label]"
                  @click.stop="onTablebtnClicked(btn, scope.row)"
                >
                  {{ (btn.textCallback && btn.textCallback(scope.row)) || btn.label }}
                </el-button>
              </template>
            </template>
            <template v-else-if="header.innerHTML">
              <div class="my-html" v-html="header.innerHTML(scope.row)" />
            </template>
            <template v-else>
              <span v-if="header.dotColorCallabck" class="statusCircle" :style="`background: ${header.dotColorCallabck(scope.row)}`" />
              {{ header.textCallback ? header.textCallback(scope.row) : scope.row[header.prop] }}
            </template>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="isShowPagination"
        v-model:current-page="props.pagationConfig.pageNumber"
        v-model:page-size="props.pagationConfig.pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper, sizes"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="onSearchClicked"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
import { reactive, defineProps, computed } from "vue"

const { proxy } = getCurrentInstance() as any
const props = defineProps({
  "id": {
    "type": String,
    "required": false,
    "default": `customTable`
  },
  "defaultExpandAll": {
    "type": Boolean,
    "required": false,
    "default": false
  },
  "treeConfig": {
    "type": Object,
    "required": false,
    "default": () => ({ "children": `children`, "hasChildren": `hasChildren` })
  },
  "tableConfig": {
    "type": Object,
    "required": false,
    "default": () => ({
      // 路由传参需要和筛选条件联动时使用(重置会清空)
      "routerParams": {},
      // 表格顶部的操作按钮
      "actionBtns": [
        {
          "type": `error`, // 按钮类型
          "icon": `el-icon-plus`, // 按钮上的图标
          "label": `删除`, // 按钮展示的文字
          "isRefresh": true, // 执行完事件是否刷新页面
          "btnCallback": () => {} // 事件回调
        }
      ],
      // 存在多个过滤条件时使用
      "searchConfig": {
        "searchForms": [
          {
            "type": `input`,
            "label": `用户名`,
            "prop": `name`
          },
          {
            "type": `select`,
            "label": `用户类型`,
            "prop": `type`,
            // 默认展示值(重置不会清空)
            "default": null,
            "optionCb": () => [
              {
                "label": `xinyongy`,
                "value": 0
              }
            ]
          },
          {
            "type": `dateRange`,
            "label": `创建时间`,
            "prop": `data`, // 唯一标识：存在多个dateRange时做区分
            "propStart": `starData`,
            "propEnd": `endData`
          },
          {
            "type": `date`,
            "label": `创建s时间`,
            "prop": `datas`
          }
        ],
        "hasPermi": `xxx` // 权限
      },
      // 只存在一个文本过滤条件时使用
      "search": {
        "prop": `name`,
        "label": `用户名`
      },
      // 表格展示配置
      "tableHeader": [
        {
          "label": `手机`,
          "prop": `mobile`,
          "iconCallback": () => `el-icon-dish`
        },
        {
          "label": `操作`,
          "prop": `id`,
          "btns": [
            {
              "label": `删除`,
              "btnCallback": () => {}
            },
            {
              "label": `查看`,
              "btnCallback": () => {}
            }
          ]
        }
      ]
    })
  } as any,
  // 禁用行勾选的回调，默认可勾选
  "disRowSelected": {
    "type": Function,
    "required": false,
    "default": (row: any, index: number) => true
  },
  // 是否显示分页，默认显示
  "isShowPagination": {
    "type": Boolean,
    "required": false,
    "default": true
  },
  // 分页的配置
  "pagationConfig": {
    "type": Object,
    "default": () => ({
      "pageSize": 20,
      "pageNumber": 1
    })
  },
  // 是否高亮当前选中行，默认false
  "isHightlightRow": {
    "type": Boolean,
    "required": false,
    "default": false
  },
  // 搜索回调
  "searchCallback": {
    "type": Function,
    "required": true
  },
  // 表格数据过滤的回调
  "filterCallback": {
    "type": Function,
    "required": false,
    "default": () => null
  },
  // 是否展示勾选框 默认展示
  "isShowSelect": {
    "type": Boolean,
    "required": false,
    "default": true
  },
  // 是否隐藏序号列
  "isHiddenIndex": {
    "type": Boolean,
    "required": false,
    "default": false
  },
  // 是否刷新
  "isRefresh": {
    "type": Boolean,
    "required": false,
    "default": false
  }
})

const checkAll = {} as any
const isLoading = reactive({}) as any
let timerRange = ref({}) as any
const searchValue = ref(``)
let defaultValue = {} as any
let searchFromData = reactive({})
const tableData = ref([])
const isTableLoading = ref(false)
const total = ref(0)
let checked = [] as Array<any> // 已勾选的数据
const currentPage = ref(props.pagationConfig.pageNumber)
const pageSize = ref(props.pagationConfig.pageSize)
watch(
  () => props.pagationConfig,
  () => {
    onSearchClicked()
  },
  { "deep": true }
)

watch(
  () => props.tableConfig.routerParams,
  (val: { [x: string]: any }) => {
    // 设置路由默认值（可重置）
    val &&
      Object.keys(val).forEach(e => {
        searchFromData[e] = val[e]
      })
  },
  { "deep": true }
)

watch(
  () => props.isRefresh,
  () => {
    onSearchClicked()
  },
  { "deep": true }
)

watch(
  timerRange,
  () => {
    // 转换时间格式
    const timerConfig = (props.tableConfig?.searchConfig?.searchForms || []).filter((e: any) => e.type === `dateRange`)
    timerConfig.forEach((e: any) => {
      if (!timerRange[e.prop]) {
        searchFromData[e.propStart] = ``
        searchFromData[e.propEnd] = ``
        return
      }
      searchFromData[e.propStart] = timerRange[e.prop].length > 0 ? proxy.conversionTime(+new Date(timerRange[e.prop][0]))(`yyyy-MM-dd hh:mm:ss`) : ``
      searchFromData[e.propEnd] = timerRange[e.prop].length > 0 ? proxy.conversionTime(+new Date(timerRange[e.prop][1]))(`yyyy-MM-dd hh:mm:ss`) : ``
    })
  },
  { "deep": true }
)

// 操作按钮 新增/删除等
const onActionsClicked = async (btn: any) => {
  try {
    isLoading[btn.label + btn.icon] = true
    // this.$set(this.isLoading, btn.label + btn.icon, true)
    await btn.btnCallback(searchFromData)
    if (btn.isRefresh) {
      await onSearchClicked()
    }
  } catch (error: any) {
    error.message !== `cancel` && console.error(error)
  } finally {
    // this.$set(this.isLoading, btn.label + btn.icon, false)
    isLoading[btn.label + btn.icon] = false
  }
}

// 搜索事件
const onSearchClicked = async () => {
  const params = { ...props.pagationConfig }
  isTableLoading.value = true
  if (props.tableConfig.search && props.tableConfig.search.prop && searchValue.value.trim()) {
    params[props.tableConfig.search.prop] = searchValue.value.trim()
  }
  // 对于搜索条件去空处理
  Object.keys(searchFromData).forEach(e => {
    if (e && (searchFromData[e] || searchFromData[e] === 0 || searchFromData[e] === false)) {
      params[e] = searchFromData[e]
    }
  })
  console.log(`params`,params)
  const res = await props.searchCallback(params)
  isTableLoading.value = false
  if (res && res.succ) {
    total.value = res.content.total || 0
    tableData.value = (props.filterCallback && props.filterCallback(res.content.records || res.content)) || res.content.records || res.content
    proxy.$emit(`cbCurrentTotal`, tableData.value.length) // 当前页总条数
  }
  selectAll([]) // 清除勾选
}

// 表格中的操作按钮点击事件
const onTablebtnClicked = async (btn: any, row: any) => {
  try {
    // this.$set(this.isLoading, row.id + btn.label, true)
    isLoading[row.id + btn.label] = true
    await btn.btnCallback(row, btn.label)
    if (btn.isRefresh) {
      onSearchClicked()
    }
  } catch (error: any) {
    error.message !== `cancel` && console.error(error)
  } finally {
    // this.$set(this.isLoading, row.id + btn.label, false)
    isLoading[row.id + btn.label] = false
  }
}

// 点击全选按钮
const selectAll = (selections: any[]) => {
  checked = []
  if (!checkAll[props.id]) {
    checkAll[props.id] = true
    isAllChecked(selections)
  } else {
    checkAll[props.id] = false
    proxy.$refs[props.id]?.clearSelection()
  }
  proxy.$emit(`selectionChange`, checked)
}

const isAllChecked = (data: any[]) => {
  const status = checkAll[proxy.id]
  if (!Array.isArray(data)) {
    proxy.$refs[proxy.id].toggleRowSelection([data], status)
    checked.push(data)
  } else {
    data.forEach(item => {
      proxy.$refs[props.id].toggleRowSelection(item, status)
      checked.push(item)
      if (item[props.treeConfig.children]) {
        isAllChecked(item[props.treeConfig.children])
      }
    })
  }
}

// 勾选
const selectionChange = async (selection: any[], row: any) => {
  const isHas = selection.filter(r => r.id === row.id)
  // const isHas = selection
  if (isHas.length) {
    // 增加
    await toggleRowSelection([row], 1)
  } else {
    // 取消
    await toggleRowSelection([row], 0)
  }
  proxy.$emit(`selectionChange`, checked)
}

const toggleRowSelection = (list: any[], type: number | boolean) => {
  if (list.length) {
    list.forEach(r => {
      if (type) {
        checked.push(r)
        proxy.$refs[props.id].toggleRowSelection(r, true)
      } else {
        checked = checked.filter(m => m.id !== r.id)
        proxy.$refs[props.id].toggleRowSelection(r, false)
      }
      if (r[props.treeConfig.children] && r[props.treeConfig.children].length) {
        toggleRowSelection(r[props.treeConfig.children], type)
      }
    })
  } else {
    // 取消
    proxy.$refs[props.id]?.clearSelection()
    checked = []
  }
}

// 点选
const rowClicked = (row: any, column: any) => {
  proxy.$emit(`rowClicked`, row, column)
}
// 搜索输入框输入时过滤通配符
const onSearchInput = (val: string, formItem: any) => {
  if (formItem.stripLike && typeof val === 'string') {
    searchFromData[formItem.prop] = val.replace(/%/g, '').replace(/_/g, '')
  }
}
// 重置
const onReset = () => {
  searchFromData = Object.assign(searchFromData, defaultValue)
  timerRange = {}
  searchValue.value = ``
  onSearchClicked()
}

// 初始化操作
const created = () => {
  checkAll[props.id] = true
  // 设置不可重置的默认值
  defaultValue = {}
  props.tableConfig?.searchConfig?.searchForms?.forEach((e: any) => {
    if (`default` in e) {
      defaultValue[e.prop] = e.default
    }
  })
  searchFromData = Object.assign(searchFromData, defaultValue)
  // 设置路由默认值（可重置）
  props.tableConfig.routerParams &&
    Object.keys(props.tableConfig.routerParams).forEach(e => {
      searchFromData[e] = props.tableConfig.routerParams[e]
    })
  proxy.$nextTick(() => {
    proxy.$refs[props.id]?.clearSelection()
  })
  onSearchClicked()
}

created()

defineExpose({
  tableData
})
</script>
<style scoped lang="scss">
.customTable {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 10px;
  .top {
    padding: 10px 20px 0 20px;
    display: flex;
    .btns {
      flex: 1;
    }
    .searchInput {
      width: 25%;
      min-width: 280px;
      max-width: 400px;
      :deep(.el-input__wrapper) {
        background: #fff;
        border: none;
        padding: 2px;
        .el-input__inner {
          height: 33px;
          line-height: 33px;
          background: #fff;
          border: none;
          padding: 10px 0px 10px 10px;
          border-radius: 0;
        }
      }
      .el-input__icon,
      .el-icon {
        width: 38px;
        font-size: 20px;
        cursor: pointer;
      }
      i {
        &:hover {
          color: #0276fa;
        }
      }
    }
  }
  .search {
    padding: 10px 20px 0;
    border-bottom: 1px solid #eee;
    :deep(.el-form) {
      display: flex;
      flex-wrap: wrap;
      .el-form-item {
        display: flex;
        margin-right: 16px;
        margin-bottom: 10px;
        background: #fff;
        align-items: center;
        .el-form-item__content {
          display: flex;
          &:nth-child(2) {
            .saveBtn {
              margin-left: 16px;
            }
            .el-button {
              height: 36px;
            }
          }
          .el-form-item__label{
            padding-right: 4px;
          }
          .el-input,.el-select{
            width: 160px;
          }
        }
        .el-form-item__label {
         padding-right: 4px;
        }
      }
    }
  }
  .tableCon {
    flex: 1;
    height: 0;
    display: flex;
    padding: 10px 20px;
    flex-direction: column;
    .statusCircle {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      transform: translateY(-3px);
      margin-right: 2px;
    }
    :deep(.el-table) {
      td {
        padding: 8px 0;
      }
      .t-header-text {
        display: inline-block;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        vertical-align: middle;
      }
      th.el-table__cell {
        background-color: #f5f5ff;
      }
    }
    // :deep() .el-pagination {
    //   text-align: center;
    // }
  }
}
</style>
