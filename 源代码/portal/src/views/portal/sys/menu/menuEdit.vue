<!--
 * @Description: 
 * @Author: 
 * @Date: 2022-03-24 10:34:44
 * @LastEditTime: 2022-03-28 11:23:35
-->
<template>
  <div class="menuEditor">
    <LookBase v-if="type === 2" :info-config="infoConfig" :info-data="formInfo" />
    <div v-else class="formContainer">
      <el-form ref="menusForm" :model="formInfo" :rules="rules[formInfo.menuType || `M`]">
        <el-form-item label="菜单类型" label-width="100px" prop="menuType">
          <el-radio v-model="formInfo.menuType" label="M">目录</el-radio>
          <el-radio v-model="formInfo.menuType" label="C">菜单</el-radio>
          <el-radio v-model="formInfo.menuType" label="F">按钮</el-radio>
        </el-form-item>
        <el-form-item label="菜单名称" label-width="100px" prop="name">
          <el-input v-model="formInfo.name" maxlength="20" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="formInfo.menuType !== `M`" label="权限标识" label-width="100px" prop="alias">
          <el-input v-model="formInfo.alias" maxlength="60" placeholder="请输入权限标识" />
        </el-form-item>
        <template v-if="formInfo.menuType !== `F`">
          <el-form-item label="菜单路径" label-width="100px" prop="path">
            <el-input v-model="formInfo.path" maxlength="60" placeholder="请输入菜单路径" />
          </el-form-item>
          <el-form-item label="组件路径" label-width="100px" prop="component">
            <el-input v-model="formInfo.component" maxlength="60" placeholder="请输入组件路径" />
          </el-form-item>
        </template>
        <el-form-item label="显示顺序" label-width="100px" prop="sequence">
          <el-input v-model="formInfo.sequence" type="number" maxlength="20" placeholder="请输入显示顺序" />
        </el-form-item>
        <template v-if="formInfo.menuType !== `F`">
          <el-form-item label="是否外链" label-width="100px" prop="isFrame">
            <el-radio v-model="formInfo.isFrame" :label="true">是</el-radio>
            <el-radio v-model="formInfo.isFrame" :label="false">否</el-radio>
          </el-form-item>
          <el-form-item label="菜单状态" label-width="100px" prop="enabled">
            <el-radio v-model="formInfo.enabled" :label="true">启用</el-radio>
            <el-radio v-model="formInfo.enabled" :label="false">禁用</el-radio>
          </el-form-item>
          <el-form-item label="菜单图标" label-width="100px" prop="icon">
            <el-input v-model="formInfo.icon" maxlength="20" placeholder="请输入图标名称" />
          </el-form-item>
          <el-form-item label="显示状态" label-width="100px" prop="visible">
            <el-radio v-model="formInfo.visible" :label="true">显示</el-radio>
            <el-radio v-model="formInfo.visible" :label="false">隐藏</el-radio>
          </el-form-item>
        </template>
      </el-form>
      <div class="btns">
        <el-button class="cancel-btn" @click="onCancel">取消</el-button>
        <el-button :loading="isSaveLoading" type="primary" @click="onSave(menusForm)">确定</el-button>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { menuAdd, menuEditor } from "@/ajax/index"
import { Ref } from "vue"
import { isNameRegular } from "@/utils/regularCommon"
import { ElForm, ElMessage } from "element-plus"
type ElFormInstance = InstanceType<typeof ElForm>
const menusForm = ref<ElFormInstance>()
// 路由
const router = useRouter()
const isSaveLoading = ref(false)
const rules = {
  "M": {
    "name": [{ "required": true, "message": `请输入菜单名称`, "trigger": `blur` }, { "validator": isNameRegular }],
    "sequence": [{ "required": true, "message": `请输入同级别显示顺序`, "trigger": `blur` }],
    "path": [{ "required": true, "message": `请输入菜单路径`, "trigger": `blur` }],
    "component": [{ "required": true, "message": `请输入组件路径`, "trigger": `blur` }]
  },
  "C": {
    "name": [{ "required": true, "message": `请输入菜单名称`, "trigger": `blur` }, { "validator": isNameRegular }],
    "sequence": [{ "required": true, "message": `请输入同级别显示顺序`, "trigger": `blur` }],
    "path": [{ "required": true, "message": `请输入菜单路径`, "trigger": `blur` }],
    "component": [{ "required": true, "message": `请输入组件路径`, "trigger": `blur` }]
    // 'alias': [{ 'required': true, 'message': `请输入权限标识`, 'trigger': `blur` }]
  },
  "F": {
    "name": [{ "required": true, "message": `请输入菜单名称`, "trigger": `blur` }, { "validator": isNameRegular }],
    "sequence": [{ "required": true, "message": `请输入同级别显示顺序`, "trigger": `blur` }]
    // 'alias': [{ 'required': true, 'message': `请输入权限标识`, 'trigger': `blur` }]
  }
}
const type: Ref<number> = ref(1)
let formInfo = reactive({
  "name": ``,
  "alias": ``,
  "path": ``,
  "component": ``,
  "icon": ``,
  "menuType": `M`,
  "visible": true,
  "isFrame": false,
  "enabled": true,
  "sequence": 0
})
const infoConfig = reactive([
  {
    "title": `基本信息`,
    "propsConfig": [
      {
        "label": `菜单类型`,
        "prop": `menuType`,
        "contentCallback": (item: any) => {
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
        "label": `菜单名称`,
        "prop": `name`
      },
      {
        "label": `组件路径`,
        "prop": `component`,
        "cbHidden": (item: any) => item.menuType === `F`
      },
      {
        "label": `菜单路径`,
        "prop": `path`,
        "cbHidden": (item: any) => item.menuType === `F`
      },
      {
        "label": `权限标识`,
        "prop": `alias`,
        "cbHidden": (item: any) => item.menuType === `M`
      },
      {
        "label": `显示顺序`,
        "prop": `sequence`
      },
      {
        "label": `菜单图标`,
        "type": `icon`,
        "prop": `icon`,
        "cbHidden": (item: any) => item.menuType === `F`
      },
      {
        "label": `是否外链`,
        "prop": `isFrame`,
        "cbHidden": (item: any) => item.menuType === `F`,
        "contentCallback": (item: any) => (item.isFrame ? `是` : `否`)
      },
      {
        "label": `菜单状态`,
        "prop": `enabled`,
        "cbHidden": (item: any) => item.menuType === `F`,
        "contentCallback": (item: any) => (item.enabled ? `启用` : `禁用`)
      },

      {
        "label": `显示状态`,
        "prop": `visible`,
        "cbHidden": (item: any) => item.menuType === `F`,
        "contentCallback": (item: any) => (item.visible ? `显示` : `隐藏`)
      }
    ]
  }
])

const onCancel = () => {
  router.go(-1)
}
const onSave = (formEl: ElFormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async valid => {
    if (valid) {
      isSaveLoading.value = true
      let result: any = null
      if (type.value) {
        result = await menuEditor(formInfo)
      } else {
        const params = JSON.parse(JSON.stringify(formInfo))
        router.currentRoute.value.query.fid && (params.fid = router.currentRoute.value.query.fid)
        result = await menuAdd(params)
      }
      isSaveLoading.value = false
      result && result.succ && ElMessage.success(`${type.value ? `编辑` : `新增`}成功`)
      router.go(-1)
    }
  })
}
onMounted(() => {
  console.log(router.currentRoute.value.query.menuObj)
  type.value = Number(router.currentRoute.value.query.type)
  const menuObj = router.currentRoute.value.query.menuObj ? JSON.parse(router.currentRoute.value.query.menuObj as string) : null
  if (type.value) {
    formInfo = Object.assign(formInfo, { ...menuObj })
  } else if (menuObj && menuObj.menuType === `M`) {
    formInfo.menuType = `C`
    formInfo.sequence = menuObj.childList ? menuObj.childList.length + 1 : 1
  } else if (menuObj && menuObj.menuType === `C`) {
    formInfo.menuType = `F`
    formInfo.sequence = menuObj.childList ? menuObj.childList.length + 1 : 1
  } else {
    formInfo.menuType = `M`
    formInfo.sequence = router.currentRoute.value.query.total ? Number(router.currentRoute.value.query.total) + 1 : 1
  }
})
</script>
<style scoped lang="scss">
.menuEditor {
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
  .cancel-btn {
    background-color: #ecf5ff;
  }
}
</style>
