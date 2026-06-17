<template>
  <header class="header">
    <div class="logo-container">
      <img class="logo" src="@/assets/images/circuitReview/logo-hz.png" alt="Logo" />

      <el-button class="customBtn" @click="goBack">
        <i class="iconfont icon-fanhuipingtai" />
        返回平台
      </el-button>
    </div>
    <HeaderUserInfo />
  </header>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { dropdownItems as dropdowns } from "@/Data/featureConstants"
import { links, demoHost } from "@/Data/constants"

const router = useRouter()

const goBack = () => {
  router.push({
    path: `/home`
  })
}

// 定义导航菜单项数组
const dropdownItems = ref(dropdowns)

const activeIndex = ref(0) // 默认选中第一个
const activeDropdown = ref(null) // 当前显示的dropdown
const hoveredItem = ref(null) // 当前hover的dropdown item
const dropdownPosition = ref([])

// 设置当前激活的菜单项
const setActive = index => {
  activeIndex.value = index
  // 这里可以添加路由跳转逻辑
  // 例如: router.push(navRoutes[index]);
}

// 显示下拉菜单
const showDropdown = index => {
  activeDropdown.value = index
}

// 隐藏下拉菜单
const hideDropdown = index => {
  if (activeDropdown.value === index) {
    activeDropdown.value = null
  }
}

// 计算下拉菜单位置
const calculateDropdownPosition = () => {
  const dropdowns = document.querySelectorAll(`.dropdown > a`)
  dropdownPosition.value = Array.from(dropdowns).map(dropdown => {
    const rect = dropdown.getBoundingClientRect()
    return rect.left + rect.width / 2 - 140 // 140是下拉菜单宽度的一半
  })
}

onMounted(() => {
  calculateDropdownPosition()
  window.addEventListener(`resize`, calculateDropdownPosition)
})

const handleFeatureClick = feature => {
  if (feature.link) {
    if (links.includes(feature.link)) {
      const fullUrl = `${demoHost}${feature.link}`
      window.location.href = fullUrl
    } else {
      router.push(feature.link)
    }
  }
  // Close the dropdown after selection
  activeDropdown.value = null
}
</script>

<style scoped lang="scss">
.header {
  background-color: #fff;
  height: 80px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  z-index: 1001;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  .logo-container {
    display: flex;
    align-items: center;
    min-width: 240px;
    .logo {
      height: 48px;
      width: auto;
      object-fit: contain;
      margin-right: 15px;
    }
  }
}
/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  .avatar {
    width: 36px;
    height: 36px;
    background-color: #e0ebf5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 8px;
  }

  .avatar img {
    width: 36px;
    height: 36px;
    cursor: pointer;
  }
  .user-info span {
    font-size: 15px;
    color: #666;
    white-space: nowrap;
  }
}

:deep(.customBtn) {
  padding: 0;
  width: 116px;
  text-align: center;
  height: 40px;
  line-height: 40px;
  background: linear-gradient(123deg, rgba(91, 144, 198, 0.5), rgba(91, 144, 198, 0.7));
  border-radius: 6px;
  font-size: 16px;
  color: #ffffff;
  border: none;
  &:hover {
    opacity: 0.8;
  }
  i {
    font-size: 18px;
    margin-right: 4px;
  }
}
</style>
