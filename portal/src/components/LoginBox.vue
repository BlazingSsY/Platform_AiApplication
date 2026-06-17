<script setup>
import store from "@/store/index"
import md5 from "md5"
import { useRouter } from "vue-router"
import { isLoginRegular, isPassRegular } from "@/utils/regularCommon"
// 路由
const router = useRouter()
const loginShow = computed(() => store.state.loginShow)
const lagonToPath = computed(() => store.state.lagonToPath)
const isShow = ref(false)
const timeStr = ref(+new Date())
watch(
  () => loginShow.value,
  () => {
    isShow.value = loginShow.value
    timeStr.value = +new Date()
  },
  {
    immediate: true
  }
)

// 登录
const loginFormRef = ref()
const loginForm = reactive({
  "loginName": ``,
  "password": ``,
  // "loginName": `jzadmin`,
  // "password": `123456`,
  "captcha": ``
})
const imgUpdate = () => {
  timeStr.value = +new Date()
}
const loginRules = {
  "loginName": [
    {
      "required": true,
      "message": `请输入登录名称`,
      "trigger": `blur`
    },
    { "required": true, "validator": isLoginRegular, "trigger": `blur` }
  ],
  "password": [
    {
      "required": true,
      "message": `请输入密码`,
      "trigger": `blur`
    },
    { "required": true, "validator": isPassRegular, "trigger": `blur` }
  ],
  // "captcha": [
  //   {
  //     "required": true,
  //     "message": `请输入验证码`,
  //     "trigger": `blur`
  //   }
  // ]
}
// 登录函数
const submitForm = formEl => {
  if (!formEl) return
  formEl.validate(valid => {
    if (valid) {
      const sendData = JSON.parse(JSON.stringify(loginForm))
      sendData.password = md5(sendData.password)
      store.dispatch(`user/usersLoginAction`, sendData).then(res => {
        if (res.succ) {
          // 不清空登录名，让浏览器可以记住
          loginForm.password = ``
          loginForm.captcha = ``
          if (lagonToPath.value) {
            router.push({ path: lagonToPath.value })
          }
          store.commit(`SET_LOGIN_SHOW`, false)
					store.commit(`SET_SCREEN_CHANGE`, true)
          console.log(`screenChange`,store.state.isScreenChange)
          // 添加刷新逻辑 - 只在跳转到 /home 时刷新
          if (!lagonToPath.value || lagonToPath.value === '/home') {
            location.reload()
          }
        } else {
          // 登录失败，code == 1001 时弹出修改密码对话框
          if (res.code === 1001) {
            // 将登录名存储到 store，供 ChangePasswordDialog 使用
            store.state.changePasswordLoginName = loginForm.loginName
            // 关闭登录弹窗，销毁 LoginBox，移除全局 keydown 监听器，防止修改密码时误触 Enter 反复调用登录接口
            store.commit('SET_LOGIN_SHOW', false)
            store.commit('SET_CHANGE_PASSWORD_SHOW', true)
          } else {
            imgUpdate()
          }
        }
      })
    }
  })
}

const keydown = (e)=>{
  if(e.keyCode==13||e.keyCode==100){
    submitForm(loginFormRef.value)
  }
}

onMounted(()=>{
  window.addEventListener(`keydown`,keydown)
})

onUnmounted(()=>{
  window.removeEventListener(`keydown`,keydown,false)
})

const closeFn = () => {
  store.commit(`SET_LOGIN_SHOW`, false)
  // 不清空登录名，让浏览器可以记住
  loginForm.password = ``
  loginForm.captcha = ``
}
</script>
<template>
  <div v-if="isShow" class="LoginBox">
    <div class="fromBox">
      <div class="left">
        <img src="../assets/images/loginLeft.png" alt="" />
      </div>
      <el-form ref="loginFormRef" :model="loginForm" status-icon :rules="loginRules" label-position="top" label-width="80px" class="demo-ruleForm">
        <h2>欢迎登录</h2>
        <el-form-item label="登录名" prop="loginName">
          <el-input v-model="loginForm.loginName" autocomplete="username" placeholder="请输入登录名" clearable maxlength="20"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" show-password autocomplete="new-password" placeholder="请输入密码" clearable maxlength="20"/>
        </el-form-item>
        <!-- <el-form-item label="验证码" prop="captcha" class="codeBox">
          <el-input v-model="loginForm.captcha" autocomplete="off" placeholder="请输入验证码" :clearable="false" prefix-icon="Postcard" />
          <img class="codeImg" :src="`/circuitreview/v1/captcha?timeStr=${timeStr}`" alt="" @click="imgUpdate" />
        </el-form-item> -->
        <el-form-item>
          <el-button type="primary" @click.prevent="submitForm(loginFormRef)" @keydown.enter="keydown()"> 登录 </el-button>
        </el-form-item>

        <p>忘记密码请联系管理员</p>

        <div class="close" @click="closeFn">
          <el-icon><Close /></el-icon>
        </div>
      </el-form>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.LoginBox {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  z-index: 101;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  .fromBox {
    width: 800px;
    height: 450px;
    background: #ffffff;
    border-radius: 10px;
    display: flex;
    .left {
      width: 400px;
      height: 450px;
      > img {
        width: 100%;
        height: 100%;
        border-radius: 10px 0px 0px 10px;
      }
    }
    :deep(.el-form) {
      flex: 1;
      padding: 40px 46px 0;
      position: relative;
      .close {
        position: absolute;
        right: 20px;
        top: 20px;
        font-size: 20px;
        cursor: pointer;
        &:hover {
          opacity: 0.8;
        }
      }
      .codeBox {
        .el-input {
          width: 174px;
        }
        .codeImg {
          width: 132px;
          height: auto;
          cursor: pointer;
        }
      }
      h2 {
        font-weight: bold;
        font-size: 20px;
        color: #333333;
        line-height: 36px;
        margin-bottom: 26px;
      }
      .el-button {
        width: 100% !important;
        height: 40px !important;
        border-radius: 4px;
        margin-top: 26px;
      }
      p {
        font-weight: 400;
        font-size: 15px;
        color: #b3b9c0;
        line-height: 36px;
        margin-top: 40px;
        text-align: center;
      }
    }
  }
}
</style>
