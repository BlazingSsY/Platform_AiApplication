<template>
  <div class="userListEdit">
    <div class="title">用户{{ ruleForm.id ? `编辑` : `新增` }}</div>
    <div class="formContainer">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
        <el-form-item label="登录名称" prop="loginName">
          <el-input v-model="ruleForm.loginName" maxlength="20" placeholder="请输入登录名称" :disabled="ruleForm.id"/>
        </el-form-item>
        <el-form-item label="用户名称" prop="name">
          <el-input v-model="ruleForm.name" maxlength="20" placeholder="请输入用户名" />
        </el-form-item>
        <!-- <el-form-item label="涉密级别" prop="secretLevel">
          <el-select v-model="ruleForm.secretLevel" filterable allow-create default-first-option placeholder="请选择涉密级别" style="width: 400px">
            <el-option v-for="item in fileSecretLevel" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="用户类型" prop="type">
          <el-select v-model="ruleForm.type" filterable allow-create default-first-option placeholder="请选择用户类型">
            <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="所属单位" prop="departmentId" class="department">
          <el-cascader v-model="ruleForm.departmentId" :disabled="!!departmentId" :options="departmentList" :props="{ checkStrictly: true, emitPath: false, label: `name`, value: `id`, children: `childList` }" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="telephone">
          <el-input v-model="ruleForm.telephone" maxlength="11" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="ruleForm.email" maxlength="50" placeholder="请输入邮箱" />
        </el-form-item>
         <el-form-item v-if="!type" label="角色" class="last-form">
          <el-select v-model="selectedRoleId" clearable placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="性别" prop="sex" class="sex">
          <el-button :type="ruleForm.sex === '女' ? 'danger' : 'info'" round size="small" @click.prevent="chooesSex('女')">
            女
           
          </el-button>
          <el-button :type="ruleForm.sex === '男' ? 'primary' : 'info'" round size="small" @click.prevent="chooesSex(`男`)">
            男
          
          </el-button>
        </el-form-item> -->
        <el-form-item label="头像" :hide-required-asterisk="false" prop="temporaryProfile">
          <el-upload class="avatar-uploader" action="string" :show-file-list="false" :multiple="false" :http-request="uploadImg" :before-upload="beforeAvatarUpload">
            <img v-if="ruleForm.profile" :src="`/circuitreview/common/v1/storage/download/${ruleForm.profile}`" class="avatar2" />
            <img v-else-if="type === 2 && ruleForm.profile === ''" src="@/assets/images/userDefault.png" class="avatar2" />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
          <p>建议尺寸为90*90像素,大小小于2M</p>
        </el-form-item>
        <!-- <el-form-item label="详细地址" prop="address">
          <el-input v-model="ruleForm.address" placeholder="请输入详细地址" maxlength="50" />
        </el-form-item> -->
        <el-form-item label="描述" prop="comments" class="last-form">
          <el-input v-model="ruleForm.comments" :rows="4" type="textarea" maxlength="150" show-word-limit placeholder="请输入用户描述" />
        </el-form-item>
       
      </el-form>
      <div class="btns">
        <el-button class="cancel-btn" @click="$router.back()"> 取消 </el-button>
        <el-button :loading="isSaveLoading" type="primary" @click="onSave"> 确定 </el-button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import md5 from "md5"
import { storageUpload, getGroupsTree, addUserList, getUserInfo, editUserList, storageDownload, UserListRole, roleUserEditor } from "@/ajax"
import { isEmailRegular, isLoginRegular, isMoblePhoneRegular, isNameRegular } from "@/utils/regularCommon"
import fileSecretLevelEnum from "@/hocks/fileSecretLevelEnum"
const route = useRoute()
const fileSecretLevel = fileSecretLevelEnum()
const { proxy } = getCurrentInstance() as any
const type = ref(2)
const showImages = ref(``)
let departmentList = ref([]) // 单位列表
const isSaveLoading = ref(false)
const departmentId = route.query.departmentId
console.log(`departmentId`,departmentId)
const roleList = ref<any[]>([])
const selectedRoleId = ref<string>('')

// 获取角色列表（仅新增时加载）
const getRoleList = async () => {
  const res: any = await UserListRole({})
  if (res.succ) {
    roleList.value = res.content || []
  }
}
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
  "departmentId": departmentId,
  "name": ``,
  "telephone": ``,
  "email": ``,
  "address": ``,
  // "sex": `女`,
  "type": 0,
  // roleId:100000000000004,
  "comments": ``,
  "secretLevel": `PUBLIC`,
  "profile": ``
} as any)

const rules = {
  // "secretLevel": [{ "required": true, "message": `请选择涉密级别`, "trigger": `change` }],
  "email": [
    { "required": true, "message": `请输入邮箱`, "trigger": `blur` },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
    // { "required": true, "validator": isEmailRegular, "trigger": `blur` }
  ],
  "loginName": [{ "required": true, "message": `请输入登录名称`, "trigger": `blur` }, { "validator": isLoginRegular }],
  "name": [{ "required": true, "message": `请输入用户名称`, "trigger": `blur` }, { "validator": isNameRegular }],
  // "type": [{ "required": true, "message": `请选择用户类型`, "trigger": `blur` }],
  // "sex": [{ "required": true, "message": `请选择性别`, "trigger": `blur` }],
  departmentId: [
    { required: true, message: `请选择所属单位`, trigger: `blur` }
  ],
  // "telephone": [{ "required": true, "message": `请输入手机号`, "trigger": `change` }]
}

// const chooesSex = (sex: any) => {
//   ruleForm.sex = sex
// }

// 上传banner图片
const uploadImg = async (item: any) => {
  showImages.value = URL.createObjectURL(item.file)
  const formData = new FormData()
  const fileItem = item
  formData.append(`file`, fileItem.file)
  await storageUpload(formData).then(async res => {
    if (res.succ) {
      ruleForm.profile = res.content
    } else {
      ruleForm.profile = ``
    }
  })
}

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

// 图片上传
const beforeAvatarUpload = (file: any) => {
  const isJPG = file.type === `image/jpeg` || file.type === `image/png`
  const isLt10M = file.size / 1024 / 1024 < 2
  if (!isJPG) {
    proxy.$message.error(`上传头像图片只能是 JPG后者PNG 格式!`)
  }
  if (isJPG && !isLt10M) {
    proxy.$message.error(`上传头像图片大小不能超过 2MB!`)
  }
  return isJPG && isLt10M
}

// 保存
const onSave = () => {
  proxy.$refs.ruleFormRef.validate(async (valid: boolean) => {
    if (valid) {
      isSaveLoading.value = true
      const sentData = {
        ...ruleForm
      }
      let result: any = {}
      if (!type.value) {
        // 新增
        result = await addUserList(sentData)
        if (result.succ && selectedRoleId.value) {
          // 新增成功后，为用户分配角色
          const userId = result.content?.id || result.content
          const userName = ruleForm.loginName
          const selectedRole = roleList.value.find((r: any) => r.id === selectedRoleId.value)
          if (selectedRole) {
            const params = [{
              roleId: selectedRole.id,
              roleName: selectedRole.name,
              selected: true,
              userId: userId,
              userName: userName
            }]
            const roleRes = await roleUserEditor(params)
            if (!roleRes.succ) {
              proxy.$message.warning(`用户创建成功，但角色分配失败，请到角色管理页面手动分配`)
            }
          }
        }
      } else if (type.value === 1) {
        // 编辑
        result = await editUserList(sentData)
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

const created = () => {
  type.value = Number(proxy.$route.query.type)
  if (!type.value) {
    ruleForm.password = md5(`123456`)
    ruleForm.confirmpassword = md5(`123456`)
    // 新增时加载角色列表
    getRoleList()
  } else if (proxy.$route.query.id) {
    getUserInfo({ "userID": proxy.$route.query.id }).then(res => {
      if (res.succ) {
        ruleForm = Object.assign(ruleForm, res.content)
        // ruleForm.sex = ruleForm.sex ? ruleForm.sex : `女`
        ruleForm.profile && commonImgShowFn(ruleForm.profile)
      }
    })
  }
  if (type.value === 2) return
  // 单位列表
  getGroupsTree(null).then(res => {
    if (res.succ) {
      departmentList.value = res.content
      nextTick(()=>{
        ruleForm.departmentId = proxy.$route.query.id? ruleForm.departmentId:departmentId*1
      })
      console.log(departmentList.value)
    }
  })
}

created()
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
  :deep(.formContainer) {
    padding: 20px 20%;
    height: 100%;
    .el-cascader {
      width: 100%;
    }
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
