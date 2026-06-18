<template>
  <div class="roleListEditor">
    <LookBase :info-config="infoConfig" :info-data="detailData" />
  </div>
</template>
<script lang="ts" setup>
import { useRouter } from "vue-router"
import { roleDetail } from "@/ajax"

// 路由
const router = useRouter()
let detailData = reactive({
  "comments": ``,
  "name": ``,
  "type": ``
})
const infoConfig = reactive([
  {
    "title": `基本信息`,
    "propsConfig": [
      {
        "label": `角色名称`,
        "prop": `name`
      },
      {
        "label": `角色类型`,
        "prop": `type`
      },
      {
        "label": `描述`,
        "prop": `comments`
      },
      {
        "label": `更新时间`,
        "prop": `updateDate`
      }
    ]
  }
])

const created = async () => {
  const { id } = router.currentRoute.value.query
  if (id) {
    const roleInfo = await roleDetail({ "id": id })
    if (roleInfo.succ) {
      detailData = Object.assign(detailData, roleInfo.content)
    }
  }
}
created()
</script>
<style scoped lang="scss">
.roleListEditor {
  width: 100%;
  height: 100%;
  background: #fff;
  overflow-y: auto;
  .formContainer {
    padding: 20px 20%;
  }
  .btns {
    text-align: center;
  }
}
</style>
