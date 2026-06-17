<template>
  <div class="userListEdit">
    <!-- <div class="title">{{ level===2?`部门`:`单位` }}{{ groupForm.id ? `编辑` : `新增` }}</div> -->
    <div class="title">{{ `组织` }}{{ groupForm.id ? `编辑` : `新增` }}</div>
    <div class="formContainer">
      <el-form ref="groupFormRef" :model="groupForm" :rules="rules" label-width="100px" class="demo-groupForm">
        <el-form-item v-if="groupForm.fidName" :label="`上级${isOffice?`部门`:`单位`}`">
          <el-input v-model="groupForm.fidName" readonly disabled/>
        </el-form-item>
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="groupForm.name" maxlength="20" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织类型" prop="isOffice">
          <el-select v-model="groupForm.isOffice" placeholder="请选择组织类型" :disabled="type==1">
            <el-option :label="'部门'" :value="true" />
            <el-option v-if="isAdmin&&level!=2" :label="'单位'" :value="false" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="单位类型" prop="type">
          <el-input v-model="groupForm.type" maxlength="20" placeholder="请输入单位类型" />
        </el-form-item> -->
        <el-form-item label="组织描述" prop="comments">
          <el-input v-model="groupForm.comments" :rows="3" type="textarea" maxlength="150" show-word-limit resize="none" placeholder="请输入组织描述" />
        </el-form-item>
      </el-form>
      <div class="btns">
        <el-button class="cancel-btn" @click="$router.back()">取消</el-button>
        <el-button :loading="isSaveLoading" type="primary" @click="onSave">确定</el-button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { addDepartmentList, editDepartmentList, getGroupInfo } from "@/ajax"
import { isNameRegular } from "@/utils/regularCommon"
import store from "@/store/index"

const { proxy } = getCurrentInstance() as any
const deptId = proxy.$route.query.id
const fid = proxy.$route.query.fid
const roleInfo = computed(() => store.state.user.userInfo?.role)
const isAdmin = computed(() => roleInfo.value?.name === `管理员`)
const level = ref(0)
const isOffice = ref(false) //部门
const groupForm = ref({
  "name": ``,
  "type": `系统单位`,
  "comments": ``,
  fidName: proxy.$route.query.fidName || ``,
  "isOffice": true
} as any)
const getGroupInfoFn = () => {
  getGroupInfo({ id: deptId }).then(res => {
    if (res.succ) {
      groupForm.value = { ...res.content, fidName: groupForm.value.fidName }
      if (groupForm.value.fid) {
        getGroupInfo({ id: groupForm.value.fid }).then(res => {
          if (res.succ) {
            groupForm.value.fidName = res.content.name
          }
        })
      }
    }
  })
}
if (deptId) {
  getGroupInfoFn()
}
const rules = {
  "name": [{ "required": true, "message": `请输入组织名称`, "trigger": `blur` }, { "validator": isNameRegular }]
}
const type = ref(2) // 操作类型 0：新建 1：编辑 2：查看
const isSaveLoading = ref(false)

// 保存
const onSave = () => {
  proxy.$refs.groupFormRef.validate(async (valid: boolean) => {
    if (valid) {
      isSaveLoading.value = true
      const sentData = JSON.parse(JSON.stringify(groupForm.value))
      let result = {} as any
      if (!groupForm.value.id) {
        // 新增
        proxy.$route.query.fid && (sentData.fid = proxy.$route.query.fid)
        result = await addDepartmentList(sentData)
      } else {
        // 编辑
        result = await editDepartmentList(sentData)
      }
      if (result.succ) {
        await proxy.$message({
          "message": `${!type.value ? `新增` : `编辑`}数据成功`,
          "type": `success`
        })
        proxy.$router.go(-1)
      } else {
        isSaveLoading.value = false
      }
    }
  })
}
type.value = Number(proxy.$route.query.type)
level.value = Number(proxy.$route.query.level)
console.log('isOffice',proxy.$route.query.isOffice)
isOffice.value = proxy.$route.query.isOffice == `true`
// const created = async () => {
//   type.value = Number(proxy.$route.query.type)
//   if (proxy.$route.query.id) {
//     const res = await getGroupInfo(deptId)
//     res && res.succ && (groupForm.value = res.content)
//   }
// }
// created()
</script>
<style scoped lang="scss">
.userListEdit {
  background: #fff;
  height: 100%;
  overflow-y: auto;
  .title {
    padding: 10px 20% 20px;
    color: #000;
    text-shadow: 1px 1px 0px #ccd5d6;
  }
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
