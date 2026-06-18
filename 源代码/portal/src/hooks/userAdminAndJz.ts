import { ref,computed } from 'vue';
import store from "@/store/index"

// 业务审核状态
export const isAdminAndJz = () => {
  const userInfo = computed(() => store.state.user.userInfo)
  const roleInfo = computed(() => userInfo.value?.role)
  return ref(roleInfo.value.name===`机载领导` || roleInfo.value.name===`管理员`)
};
