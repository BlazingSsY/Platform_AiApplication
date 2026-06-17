<script setup>
import LoginBox from "./LoginBox.vue"
import ChangePasswordDialog from "./ChangePasswordDialog.vue"
import store from "@/store/index"

const route = useRoute()
const router = useRouter()
const userInfo = computed(() => store.state.user.userInfo)
const userAvatar = ref(``)
const profile = computed(() => userInfo.value?.profile)
const showChangePassword = computed(() => store.state.changePasswordShow)
// onMounted(async () => {
// 		// if (userInfo.value && userInfo.value.profile) {
// 		// 	// const res = await commonImgPicture(userInfo.value.profile);
// 		// 	// if (res.succ) {
// 		// 	// 	userAvatar.value = res.content;
// 		// 	// }
// 		// }
//     setTimeout(() => {
//       console.log(`userInfo`,userInfo.value)
//       console.log(`profile`,profile.value)
//     },2000)
// 	});

const loginName = computed(() => userInfo.value?.name)
const loginName1 = computed(() => userInfo.value?.loginName)
const loginFn = () => {
  store.commit(`SET_LOGIN_SHOW`, true)
  store.commit(`SET_LOGIN_TO_PATH`, ``)
}
const loginOut = () => {
  store.dispatch(`user/logout`)
  if (route.path !== `/home`) {
    router.push(`/home`)
  }
  nextTick(() => {
	store.commit(`SET_SCREEN_CHANGE`, true)
  })
}

// 修改密码
const changePassword = () => {
  store.commit('SET_CHANGE_PASSWORD_SHOW', true);
};

const onPasswordChanged = () => {
  store.commit('SET_CHANGE_PASSWORD_SHOW', false);
};
</script>
<template>
  <div class="HeaderUserInfo">
    <span v-if="!loginName" class="userOpt" @click="loginFn">
      <img class="userImg" src="@/assets/images/userDefault.png" alt="头像" />
      <!-- <img v-if="profile" :src="`/circuitreview/common/v1/storage/download/${profile}`" class="userImg" />
      <img v-else src="@/assets/images/userDefault.png" class="userImg" /> -->
      <span>欢迎登录</span>
    </span>
    <el-dropdown v-else class="userOpt" popper-class="userOptDropdown">
      <span class="el-dropdown-link">
        <img v-if="profile" :src="`/circuitreview/common/v1/storage/download/${profile}`" class="userImg" />
        <img v-else src="@/assets/images/userDefault.png" class="userImg" />
        <span>{{ loginName }}</span>
      </span>
      <template #dropdown>
        <el-dropdown-menu style="z-index: 30000">
          <el-dropdown-item @click="changePassword">
            <el-icon><Edit /></el-icon>
							修改密码
						</el-dropdown-item>
          <el-dropdown-item v-if="loginName" @click="loginOut">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
  <LoginBox />
  <!-- 修改密码对话框 - 始终渲染，由 store 统一控制显示 -->
  <ChangePasswordDialog v-model:visible="showChangePassword" @success="onPasswordChanged" @update:visible="(val) => store.commit('SET_CHANGE_PASSWORD_SHOW', val)" />
</template>
<style lang="scss">
.HeaderUserInfo {
  width: 300px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  .userOpt {
    cursor: pointer;
    position: relative;
    * {
      outline: none;
    }
    .userImg {
      width: 40px;
      height: 40px;
      margin-right: 15px;
      border-radius: 50%;
      vertical-align: middle;
    }
  }
}
.userOptDropdown {
  z-index: 10000002 !important;
  > .popper__arrow {
    display: none !important;
  }
}
</style>
