<template>
  <div class="roleListEditor">
    <div class="title">角色{{ roleForm.id ? `编辑` : `新增` }}</div>
    <div class="formContainer">
      <el-form ref="ruleFormRef" :model="roleForm" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" maxlength="20" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色类型" prop="type">
          <el-select v-model="roleForm.type" filterable allow-create default-first-option placeholder="请选择角色类型">
            <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.label" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="comments" :hide-required-asterisk="false" class="comments">
          <el-input v-model="roleForm.comments" :rows="3" type="textarea" maxlength="150" resize="none" placeholder="请输入用户描述" />
        </el-form-item>
      </el-form>
      <div v-if="type !== 2" class="btns">
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" :loading="isSaveLoading" @click="onSave(ruleFormRef)">确定</el-button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from "vue-router"
import { ElForm, ElMessage } from "element-plus"
import { addUserRole, roleDetail, editRole } from "@/ajax"

type ElFormInstance = InstanceType<typeof ElForm>
// 路由
const router = useRouter()
const ruleFormRef = ref<ElFormInstance>()
let roleForm = reactive({
  "comments": ``,
  "name": ``,
  "type": ``
})
const typeList = reactive([
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
])
const rules = {
  "name": [{ "required": true, "message": `请输入角色名`, "trigger": `blur` }],
  "type": [{ "required": true, "message": `请选择角色类型`, "trigger": `blur` }]
}
const isSaveLoading = ref(false)
let type = 2
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

const onSave = (formEl: ElFormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async valid => {
    if (valid) {
      isSaveLoading.value = true
      const params = JSON.parse(JSON.stringify(roleForm))
      let result = {} as any
      if (!type) {
        // 新增
        result = await addUserRole(params)
      } else if (type === 1) {
        // 编辑
        result = await editRole(params)
      }
      if (result.succ) {
        await ElMessage({
          "message": `${!type ? `新增` : `编辑`}数据成功`,
          "type": `success`
        })
        router.go(-1)
      } else {
        isSaveLoading.value = false
      }
    }
  })
}

const created = async () => {
  type = Number(router.currentRoute.value.query.type)
  const { id } = router.currentRoute.value.query
  if (type && id) {
    const roleInfo = await roleDetail({ "id": id })
    if (roleInfo.succ) {
      roleForm = Object.assign(roleForm, roleInfo.content)
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
  .title {
    padding: 10px 20% 20px;
    color: #000;
    text-shadow: 1px 1px 0px #ccd5d6;
  }
  .formContainer {
    padding: 20px 20%;
  }
  .btns {
    text-align: center;
  }
}
</style>
