<!--
 * @Description: 
 * @Author: 
 * @Date: 2022-03-24 10:34:44
 * @LastEditTime: 2022-03-31 11:19:26
-->
<template>
  <div class="userListEdit">
    <LookBase v-if="groupForm.id" :info-config="infoConfig" :info-data="groupForm" />
  </div>
</template>
<script setup lang="ts">
import { getGroupInfo } from "@/ajax"
import { infoConfigItem } from "@/types/components/LookBase"
const { proxy } = getCurrentInstance() as any
const deptId = proxy.$route.query.id
const groupForm = ref({
  "name": ``,
  "type": `系统单位`,
  "comments": ``,
  fidName: ``,
  id: ``,
  "isOffice": false
} as any)
const infoConfig = ref<any>([
  {
    "title": `基本信息`,
    "propsConfig": [
      {
        "label": `组织名称`,
        "prop": `name`
      },
      {
        "label": `组织类型`,
        "prop": `isOffice`,
        "contentCallback": (data: any) => data.isOffice ? '部门' : '单位'
      },
      {
        "label": `创建人`,
        "prop": `createUser`
      },
      {
        "label": `更新时间`,
        "prop": `updateDate`
      },
      {
        "label": `创建时间`,
        "prop": `createDate`
      },
      {
        "label": `组织描述`,
        "prop": `comments`
      }
    ]
  }
])

const created = async () => {
  if (proxy.$route.query.id) {
    const res = await getGroupInfo({ id: deptId })
    if (res && res.succ) {
      if (res.content.fid) {
        await getGroupInfo({ id: res.content.fid }).then(ress => {
          if (ress.succ) {
            infoConfig.value[0].propsConfig.unshift({
              "label": `上级组织`,
              "prop": `fidName`
            })
            groupForm.value = { ...res.content, fidName: ress.content.name }
          }
        })
      } else {
        groupForm.value = { ...res.content }
      }
    }
  }
}
created()
</script>
<style scoped lang="scss">
.userListEdit {
  background: #fff;
  height: 100%;
  overflow-y: auto;
  .formContainer {
    padding: 20px 20%;
    height: 100%;
  }
  .btns {
    text-align: center;
    padding-bottom: 20px;
  }
  .avatar {
    padding: 10px;
    width: 60px;
    height: 60px;
  }
  .avatar2 {
    height: 40px;
    width: 40px;
    margin: 1px 10px;
  }
}
</style>
