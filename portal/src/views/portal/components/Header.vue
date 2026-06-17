<template>
  <header class="header">
    <div class="header-content">
      <div class="logo-container">
        <img v-if="VITE_APP_IS_JIZAI" class="logo" src="@/assets/images/logoHeader-jz.png" alt="Logo" />
        <img v-else class="logo" src="@/assets/images/logoHeader.png" alt="Logo" />
      </div>

      <nav class="nav-menu">
        <router-link to="/home" :class="{ active: activeIndex === 0 }" @click.prevent="setActive(0)"> 首页 </router-link>

        <div v-for="(item, index) in dropdownItems" :key="index" class="dropdown" @mouseenter="showDropdown(index)" @mouseleave="hideDropdown(index)">
          <router-link to="" :class="{ active: activeIndex === index + 1 }" @click.prevent="setActive(index + 1)">
            {{ item.name }}
            <span class="dropdown-arrow">▼</span>
          </router-link>
          <div class="dropdown-content" :class="{ show: activeDropdown === index }">
            <div
              v-for="(feature, featureIndex) in item.features"
              :key="featureIndex"
              :class="`dropdown-item ${feature.status?'':'disabled'}`"
              @mouseenter="feature.status?hoveredItem = featureIndex:``"
              @mouseleave="hoveredItem = null"
              @click="handleFeatureClick(feature,index)"
            >
              <div class="feature-text">
                <div class="feature-title">
                  {{ feature.name }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <!-- <div class="search-container">
        <input type="text" placeholder="请输入关键词搜索" />
        <button class="search-button">
          <img class="search" src="@/assets/images/searchIcon.png" alt="search" />
          <span>搜索</span>
        </button>
      </div> -->
      <HeaderUserInfo />
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
// import { dropdownItems as dropdowns } from "@/Data/featureConstants"
import { links, demoHost } from "@/Data/constants"
import store from "@/store/index"
import { openApplication } from '@/utils/openApplication'
const isJiZaiUser = computed(() => store.state.isJiZaiUser)
// 可以添加页脚数据或方法
const VITE_APP_IS_JIZAI = isJiZaiUser.value
const roleInfo = computed(() => userInfo.value?.role)
const userInfo = computed(() => store.state.user.userInfo)
const loginName = computed(() => userInfo.value?.name)
const router = useRouter()
const systemFeatures = [
    {
        name: '用户管理',
        subtitle: 'Campus Network',
        url:`/userList`,
        isAuth:true,
        status:1
    },
    {
        name: '组织管理',
        subtitle: 'Campus Network',
        url:`/depList`,
        isAuth:true,
        status:1
    },
    {
        name: '应用管理',
        subtitle: 'Campus Network',
        url:`/applicationManage`,
        isAuth:true,
        status:1
    },
    {
        name: '资讯管理',
        subtitle: 'Campus Network',
        url:`/informationManage`,
        isAuth:true,
        status:1
    }
];


const dropdownItemsData = ref([
    {
        name: '设计',
        features: []
    },
    {
        name: '生产',
        features: []
    },
    {
        name: '管理',
        features: []
    },
    {
        name: '算力',
        features: []
    },
    {
        name: '系统',
        features: []
    },
])
const dropdownItems = ref([])

const applicationsListData = computed(()=>store.state.applicationsListData)

watch(()=>applicationsListData.value,val=>{
  if(val){
    dropdownItemsData.value[0].features = val[`设计研发`]??[]
    dropdownItemsData.value[1].features = val[`生产制造`]??[]
    dropdownItemsData.value[2].features = val[`运营管理`]??[]
    dropdownItemsData.value[3].features = val[`算力资源`]??[]
    dropdownItemsData.value[4].features = systemFeatures?VITE_APP_IS_JIZAI?systemFeatures.filter(r=>r.name!==`资讯管理`&&r.name!==`应用管理`):systemFeatures:[]

    dropdownItems.value = dropdownItemsData.value

    if((roleInfo.value&&(roleInfo.value.name === `管理员`||roleInfo.value.name === `领导`||roleInfo.value.name === `机载领导`))){
    if(roleInfo.value.name === `领导`||roleInfo.value.name === `机载领导`){
      dropdownItems.value = dropdownItemsData.value.map(r=>{
        if(r.name === `系统`){
          const obj = {...r}
          obj.features = obj.features.filter(f=>f.name === "组织管理"||f.name === "用户管理")
          return obj
        }else{
          return r
        }
      })
    }else{
      dropdownItems.value = dropdownItemsData.value
    }

  }else{
    dropdownItems.value = dropdownItemsData.value.filter(r=>r.name !== `系统`)
  }
  }
})

// 定义导航菜单项数组
watch(()=>roleInfo.value,()=>{
  console.log(`roleInfo.value`,roleInfo.value)
  dropdownItems.value = []
  if(roleInfo.value && (roleInfo.value&&(roleInfo.value.name === `管理员`||roleInfo.value.name === `领导`||roleInfo.value.name === `机载领导`))){
    if(roleInfo.value.name === `领导`||roleInfo.value.name === `机载领导`){
      dropdownItems.value = dropdownItemsData.value.map(r=>{
        if(r.name === `系统`){
          const obj = {...r}
          obj.features = obj.features.filter(f=>f.name === "组织管理"||f.name === "用户管理")
          return obj
        }else{
          return r
        }
      })
    }else{
      dropdownItems.value = dropdownItemsData.value
    }
  }else{
    dropdownItems.value = dropdownItemsData.value.filter(r=>r.name !== `系统`)
  }
},{immediate:true,deep:true})
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

const handleFeatureClick = (item,featureIndex) => {
  if(!item.status){
    return 
  }
  setActive(featureIndex + 1)
 
  if(item.status===1){
    // 统一跳转：http(s)/反代路径新标签打开；站内路由 router.push；未登录弹登录框
    openApplication(item)
  }else{
    // 跳转详情
    router.push({
      path:`/appDetail`,
      query:{
        appId:item.id
      }
    })
  }
  // if (item.link) {
  //   // 跳转链接
  //   if (links.includes(item.link)) {
  //     const fullUrl = `${demoHost}${item.link}`
  //     window.location.href = fullUrl
  //   }
  // } else if (item.router) {
  //   // 需要登录权限的
  //   if (item.isAuth) {
  //     if (loginName.value) {
  //       router.push(item.router)
  //     } else {
  //       store.commit(`SET_LOGIN_SHOW`, true)
  //       store.commit(`SET_LOGIN_TO_PATH`, item.router)
  //     }
  //   } else {
  //     router.push(item.router)
  //   }
  // } else {
  //   ElMessage.info(`功能暂未实现!`)
  // }
}
</script>

<style scoped>
/* Previous styles remain unchanged */
.header {
  background-color: #fff;
  height: 80px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1000;
  width: 100%;
  margin: 0 auto;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px 0 36px;
  border-left: 1px solid #f2f7ff;
  border-right: 1px solid #f2f7ff;
  /* max-width: 1920px;
    width: 100%; */
}

.logo-container {
  display: flex;
  align-items: center;
  min-width: 240px;
}

.logo {
  height: 48px;
  width: auto;
  object-fit: contain;
  margin-right: 15px;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  margin: 0 20px;
  flex-grow: 1;
  justify-content: center;
}

.nav-menu a {
  font-size: 15px;
  color: #333;
  text-decoration: none;
  margin: 0 25px;
  position: relative;
  line-height: 38px;
  white-space: nowrap;
  display: flex;
  align-items: center;
}

.nav-menu a:hover,
.nav-menu a.active {
  color: #018ed7;
}

.nav-menu a.active::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 32px;
  height: 2px;
  background-color: #018ed7;
}

.dropdown {
  position: relative;
  display: inline-block;
  .router-link-active{
    &::after{
      width: 63px !important;
    }

  }

}

.dropdown-arrow {
  margin-left: 5px;
  font-size: 12px;
  transition: transform 0.3s;
}

.dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-content {
  display: none;
  position: absolute;
  top: 100%;
  left: 130%;
  transform: translateX(-50%);
  background-color: #fff;
  min-width: 220px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  padding: 10px 0;
  z-index: 1;
  border-radius: 10px 10px 10px 10px;
}

.dropdown-content.show {
  display: block;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.2s;
  &.disabled{
    cursor: not-allowed;
    &:hover{
      background:transparent
    }
  }
}

.dropdown-item:hover {
  background: linear-gradient(90deg, #c8e0f3, rgba(227, 240, 250, 0.41));
}

.feature-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
  transition: opacity 0.2s;
}

.feature-text {
  display: flex;
  flex-direction: column;
}

.dropdown-item:hover .feature-title {
  color: #018ed7; /* 使用主题蓝色 */
}

.feature-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 搜索框 */
.search-container {
  display: flex;
  margin-right: 30px;
  flex-shrink: 0;
}

.search-container input {
  width: 300px;
  height: 36px;
  border: 1px solid #9bbbe0;
  border-radius: 4px 0 0 4px;
  padding: 0 15px;
  font-size: 14px;
  color: #333;
}

.search-button {
  width: 80px;
  height: 38px;
  background: linear-gradient(to right, #018fd7, #026ff7);
  border: none;
  border-radius: 0 4px 4px 0;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  margin-left: 2px;
}

.search-button img {
  width: 15px;
  height: 15px;
  margin-right: 5px;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

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
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

/* 新增简单导航项的样式 */
.simple-nav-item {
  font-size: 17px;
  color: #333;
  text-decoration: none;
  margin: 0 30px;
  position: relative;
  line-height: 38px;
  white-space: nowrap;
  display: flex;
  align-items: center;
}

.simple-nav-item.active {
  color: #018ed7;
}

.simple-nav-item.active::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 32px;
  height: 2px;
  background-color: #018ed7;
}
</style>
