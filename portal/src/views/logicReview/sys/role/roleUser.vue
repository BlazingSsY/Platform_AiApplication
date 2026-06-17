<template>
  <div class="roleListUser">
    <div class="title">角色所属用户查看</div>
    <CustomTable class="table" :search-callback="searchCallback" :filter-callback="filterCallback" :table-config="tableConfig" :is-show-select="false" />
    <div class="btn">
      <el-button @click="onBack">返回</el-button>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from "vue"
import { useRouter } from "vue-router"
import { GroupUserList } from "@/ajax"

const USER_TYPE_CONFIG: any[] = [
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
// 路由
const router = useRouter()
const tableConfig = reactive({
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
      "label": `性别`,
      "width": `60`,
      "prop": `sex`
    },
    {
      "label": `状态`,
      "prop": `locked`,
      "width": `100`,
      "align": `center`,
      "dotColorCallabck": (item: any) => (item.locked === 2 ? `red` : `green`),
      "textCallback": (item: any) => (item.locked === 2 ? `禁用` : `激活`)
    },
    {
      "label": `用户类型`,
      "prop": `type`,
      "textCallback": (item: any) => USER_TYPE_CONFIG.find((e: any) => e.value === (item.type || 0)).label
    },
    {
      "label": `电话`,
      "prop": `telephone`
    },
    {
      "label": `邮箱`,
      "prop": `email`
    },
    {
      "label": `创建时间`,
      "prop": `createDate`
    }
  ]
})
const searchCallback = () => {
  const { id } = router.currentRoute.value.query
  return GroupUserList({
    "roleId": id,
    "include": true
  })
}
const filterCallback = (tableData: any) => tableData
const onBack = () => router.go(-1)
</script>
<style lang="scss" scoped>
.roleListUser {
  width: 100%;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  .title {
    padding: 10px 20px 20px;
    color: #000;
    text-shadow: 1px 1px 0px #ccd5d6;
  }
  .table {
    flex: 1;
  }
  .btn {
    padding-bottom: 16px;
    text-align: center;
  }
}
</style>
