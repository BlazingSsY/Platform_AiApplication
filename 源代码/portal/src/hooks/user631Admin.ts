import { ref,computed } from 'vue';
import store from "@/store/index"

// 业务审核状态
export const is631Admin = () => {
    const userInfo = computed(() => store.state.user.userInfo)
    const roleInfo = computed(() => userInfo.value?.role)
	return ref(roleInfo.value.name===`领导`&&userInfo.value?.departmentName===`631`)
};
