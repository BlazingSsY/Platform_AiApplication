import { computed } from 'vue';
import store from "@/store/index"

// 业务审核状态
export const isAdmin = () => {
  const userInfo = computed(() => store.state.user.userInfo)
  const roleInfo = computed(() => userInfo.value?.role)
  return computed(() => roleInfo.value?.name === '领导' || roleInfo.value?.name === '机载领导' || userInfo.value?.loginName === 'admin')
};
