<template>
  <div class="userListEdit">
    <LookBase :info-config="infoConfig" :info-data="ruleForm">
      <template #profile>
        <div>
          <img v-if="ruleForm.profile" :src="`/circuitreview/common/v1/storage/download/${ruleForm.profile}`" class="avatar2" />
          <img v-else src="@/assets/images/userDefault.png" class="avatar2" />
        </div>
      </template>
    </LookBase>
  </div>
</template>
<script setup lang="ts">
import { getUserInfo, storageDownload } from "@/ajax"
import LookBase from "@/components/LookBase.vue"
import { User } from "@/types/system"

const { proxy } = getCurrentInstance() as any
const showImages = ref(``)
const typeList = [
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
let ruleForm = reactive({
  "loginName": ``,
  "departmentId": ``,
  "name": ``,
  "telephone": ``,
  "email": ``,
  "address": ``,
  // "sex": `女`,
  "type": 0,
  "comments": ``,
  "profile": ``
} as any)

const infoConfig = [
  {
    "title": `基本信息`,
    "propsConfig": [
      {
        "label": `头像`,
        "slot": `profile`
      },
      {
        "label": `用户名`,
        "prop": `name`
      },
      {
        "label": `用户类型`,
        "prop": `type`,
        "contentCallback": (item: User) => typeList?.find((e: any) => e.value === item.type)?.label
      },
      {
        "label": `电话`,
        "prop": `telephone`
      },
      {
        "label": `邮箱`,
        "prop": `email`
      },
      // {
      //   label: `角色名称`,
      //   prop: `roleId`,
      //   contentCallback: item => Array.isArray(item.roleId) ? item.roleId.map(e => e.name).join() : ``
      // },
      // {
      //   "label": `详细地址`,
      //   "prop": `address`
      // },
      {
        "label": `登录名`,
        "prop": `loginName`
      },
      // {
      //   "label": `性别`,
      //   "prop": `sex`
      // },
      {
        "label": `状态`,
        "prop": `locked`,
        "contentCallback": (item: User) => (item.locked ? `禁用` : `激活`)
      },
      {
        "label": `所属单位`,
        "prop": `departmentName`
      },
      {
        "label": `创建时间`,
        "prop": `createDate`
      },
      {
        "label": `描述`,
        "prop": `comments`
      }
    ]
  }
]

// 获取图片
const commonImgShowFn = async (id: string) => {
  await storageDownload(id).then(res => {
    if (res.succ) {
      showImages.value = res.content
    } else {
      showImages.value = ``
    }
  })
}
const created = () => {
  if (proxy.$route.query.id) {
    getUserInfo({ "userID": proxy.$route.query.id }).then(res => {
      if (res.succ) {
        ruleForm = Object.assign(ruleForm, res.content)
        // ruleForm.sex = ruleForm.sex ? ruleForm.sex : `女`
        ruleForm.profile && commonImgShowFn(ruleForm.profile)
      }
    })
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
