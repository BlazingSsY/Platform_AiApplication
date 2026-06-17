<!-- eslint-disable -->
<template>
  <div class="menu-container">
    <div class="menu-system" @click="handleOutsideClick">
      <transition name="slide">
        <div v-if="isExpanded" class="menu-content">
          <div class="menu-item-home" @click="handleCardClick({ url: `/home`,status:1 })" @mouseover="isHomeHovered = true" @mouseout="isHomeHovered = false">
            <div class="menu-item-home-wrapper">
              <img :src="isHomeHovered ? homeB : homeW" alt="home" style="width: 20px; height: 17px; margin-right: 11px" />
              <span>首页</span>
            </div>
          </div>

          <div class="menu-divider" />
          <div class="menu-item" @click="toggleSubmenu('design')">
            <div class="menu-item-wrapper">
              <img src="@/assets/images/design.png" alt="design" style="width: 20px; height: 17px; margin-right: 11px" />
              <span>设计</span>
            </div>
            <svg width="12" height="12" viewBox="0 0 100 100" :style="{ transform: expandedSubmenus.design ? 'rotate(180deg)' : 'none' }">
              <polygon points="10,20 50,80 90,20" style="fill: white" />
            </svg>
          </div>
          <div v-show="expandedSubmenus.design" class="submenu-wrapper">
            <div v-for="(item, index) in designFeatures" :key="'design-submenu-' + index" :class="`submenu-item ${!item.status ? 'disabled' : ''}`" @click="handleCardClick(item)">
              {{ item.name }}
            </div>
          </div>

          <div class="menu-divider" />
          <div class="menu-item" @click="toggleSubmenu('production')">
            <div class="menu-item-wrapper">
              <img src="@/assets/images/production.png" alt="production" style="width: 20px; height: 17px; margin-right: 11px" />
              <span>生产</span>
            </div>
            <!-- <span>production</span> -->
            <svg width="12" height="12" viewBox="0 0 100 100" :style="{ transform: expandedSubmenus.production ? 'rotate(180deg)' : 'none' }">
              <polygon points="10,20 50,80 90,20" style="fill: white" />
            </svg>
          </div>
          <div v-show="expandedSubmenus.production" class="submenu-wrapper">
            <div v-for="(item, index) in productionFeatures" :key="'production-submenu-' + index" :class="`submenu-item ${!item.status ? 'disabled' : ''}`" @click="handleCardClick(item)">
              {{ item.name }}
            </div>
          </div>

          <div class="menu-divider" />
          <div class="menu-item" @click="toggleSubmenu('management')">
            <div class="menu-item-wrapper">
              <img src="@/assets/images/management.png" alt="management" style="width: 20px; height: 17px; margin-right: 11px" />
              <span>管理</span>
            </div>
            <!-- <span>management</span> -->
            <svg width="12" height="12" viewBox="0 0 100 100" :style="{ transform: expandedSubmenus.management ? 'rotate(180deg)' : 'none' }">
              <polygon points="10,20 50,80 90,20" style="fill: white" />
            </svg>
          </div>
          <div v-show="expandedSubmenus.management" class="submenu-wrapper">
            <div v-for="(item, index) in managementFeatures" :key="'management-submenu-' + index" :class="`submenu-item ${!item.status ? 'disabled' : ''}`" @click="handleCardClick(item)">
              {{ item.name }}
            </div>
          </div>

          <div class="menu-divider" />
          <div class="menu-item" @click="toggleSubmenu('computing')">
            <div class="menu-item-wrapper">
              <img src="@/assets/images/computing.png" alt="computing" style="width: 20px; height: 17px; margin-right: 11px" />
              <span>算力</span>
            </div>
            <!-- <span>computing</span> -->
            <svg width="12" height="12" viewBox="0 0 100 100" :style="{ transform: expandedSubmenus.computing ? 'rotate(180deg)' : 'none' }">
              <polygon points="10,20 50,80 90,20" style="fill: white" />
            </svg>
          </div>
          <div v-show="expandedSubmenus.computing" class="submenu-wrapper">
            <div v-for="(item, index) in computingFeatures" :key="'computing-submenu-' + index" :class="`submenu-item ${!item.status ? 'disabled' : ''}`" @click="handleCardClick(item)">
              {{ item.name }}
            </div>
          </div>

          <div v-if="roleInfo && (roleInfo.name === `管理员`||roleInfo.name === `领导`||roleInfo.name === `机载领导`)" class="menu-divider" />
          <div v-if="roleInfo && (roleInfo.name === `管理员`||roleInfo.name === `领导`||roleInfo.name === `机载领导`)" class="menu-item" @click="toggleSubmenu('system')">
            <div class="menu-item-wrapper">
              <img src="@/assets/images/computing.png" alt="computing" style="width: 20px; height: 17px; margin-right: 11px" />
              <span>系统</span>
            </div>
            <svg width="12" height="12" viewBox="0 0 100 100" :style="{ transform: expandedSubmenus.system ? 'rotate(180deg)' : 'none' }">
              <polygon points="10,20 50,80 90,20" style="fill: white" />
            </svg>
          </div>
          <div v-show="expandedSubmenus.system" v-if="roleInfo && (roleInfo.name === `管理员`||roleInfo.name === `领导`||roleInfo.name === `机载领导`)" class="submenu-wrapper">
            <template v-for="(item, index) in systemFeatures" :key="'computing-submenu-' + index" >
              <div v-if="((roleInfo.name === `领导`||roleInfo.name === `机载领导`)&&item.name !== '应用管理'&&item.name !== '资讯管理')||(roleInfo.name === `管理员`)" :class="`submenu-item ${!item.status ? 'disabled' : ''}`" @click="handleCardClick(item)">
                {{ item.name }}
              </div>
            </template>
          </div>
        </div>
      </transition>
      <div class="function-menu" :style="{ left: isExpanded ? '280px' : '0' }" @click="toggleMenu">
        <span>功<br />能<br />菜<br />单</span>
        <svg width="15" height="15" viewBox="0 0 100 100" :style="{ transform: isExpanded ? 'rotate(180deg)' : 'none' }">
          <polygon points="20,10 80,50 20,90" style="fill: white" />
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
// import { designFeatures as designData, productionFeatures as productionData, managementFeatures as managementData, computingFeatures as computingData, systemFeatures } from "@/Data/featureConstants"
import { links, demoHost } from "@/Data/constants"
import homeW from "@/assets/images/homeW.png"
import homeB from "@/assets/images/homeB.png"
import store from "@/store/index"
import { openApplication } from '@/utils/openApplication'
const roleInfo = computed(() => userInfo.value?.role)
const userInfo = computed(() => store.state.user.userInfo)
const loginName = computed(() => userInfo.value?.name)
const router = useRouter()
const isJiZaiUser = computed(() => store.state.isJiZaiUser)
console.log(`isJiZaiUser`,isJiZaiUser.value)
const designFeatures = ref([])
const productionFeatures = ref([])
const managementFeatures = ref([])
const computingFeatures = ref([])
const systemFeatures = ref([
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
]);
watch(()=>isJiZaiUser.value,val=>{
  if(isJiZaiUser.value){
    systemFeatures.value = systemFeatures.value.filter(item=>item.name !== '资讯管理'&&item.name !== '应用管理')
    console.log(`systemFeatures`,systemFeatures.value)
  }
},{immediate:true,deep:true})

const applicationsListData = computed(()=>store.state.applicationsListData)

watch(()=>applicationsListData.value,val=>{
  if(val){
    designFeatures.value = val[`设计研发`]??[]
    productionFeatures.value = val[`生产制造`]??[]
    managementFeatures.value =val[`运营管理`]??[]
    computingFeatures.value = val[`算力资源`]??[]
  }
})

const isExpanded = ref(false)
const expandedSubmenus = ref({
  "design": true,
  "production": true,
  "management": true,
  "computing": true,
  "system": true
})
const isHomeHovered = ref(false)

const toggleMenu = () => {
  isExpanded.value = !isExpanded.value
}

const toggleSubmenu = menu => {
  expandedSubmenus.value[menu] = !expandedSubmenus.value[menu]
}
const handleCardClick = item => {
  if(!item.status){ 
    return 
  }
  if(item.status===1){
    // 统一跳转：http(s)/反代路径新标签打开；站内路由 router.push；未登录弹登录框
    const result = openApplication(item)
    // 内部跳转/弹登录时收起菜单（外部新标签打开则保持菜单）
    if (result === 'internal' || result === 'login') {
      toggleMenu()
    }
  }else{
    // 跳转详情
    router.push({
      path:`/appDetail`,
      query:{
        appId:item.id
      }
    })
    toggleMenu()
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
  //   toggleMenu()
  // } else {
  //   ElMessage.info(`功能暂未实现!`)
  // }
}

const handleOutsideClick = event => {
  if (isExpanded.value && !event.target.closest(`.menu-system`)) {
    isExpanded.value = false
  }
}

onMounted(() => {
  document.addEventListener(`click`, handleOutsideClick)
})

onUnmounted(() => {
  document.removeEventListener(`click`, handleOutsideClick)
})
</script>

<style scoped>
.menu-container {
  position: fixed;
  top: 80px;
  bottom: 0;
  z-index: 100;
}
.menu-system {
  position: absolute;
  left: 0;
  top: 24px;
  z-index: 1000;
  color: white;
  width: 100%;
}

.function-menu {
  width: 39px;
  height: 111px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 14px;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 2;
  cursor: pointer;
  z-index: 100;

  box-shadow: 3px 4px 18px 0px rgba(38, 99, 241, 0.35);
  border-radius: 1px 10px 10px 1px;
  transition: left 0.3s ease;
  width: 43px;
  background: linear-gradient(123deg, #018ed9, #0270f6);
}

.menu-content {
  position: absolute;
  left: 0;
  top: -24px;
  width: 280px;
  height: calc(100vh - 80px);
  /* background: rgba(2, 118, 239, 0.9); */
  display: flex;
  flex-direction: column;
  z-index: 1;
  overflow-y: auto;
  background: linear-gradient(180deg, #018ed9, #4a91f4);
  box-shadow: 3px 4px 18px 0px rgba(38, 99, 241, 0.35);
  border-radius: 1px;
  opacity: 0.96;

  /* transform: translateX(-110%); 
    transition: transform 0.3s ease, opacity 0.3s ease; 
    opacity: 0; */
}

.menu-item-home-wrapper,
.menu-item-wrapper {
  display: flex;
  align-items: center;
}

.menu-item-wrapper {
  margin-left: -7px;
}
/* 
.menu-item:hover {
    margin-left: -7px;
} */

.menu-item-home:hover {
  /* .menu-item-wrapper { */
  width: 267px;
  /* height: 50px; */
  background: #ffffff;
  border-radius: 10px;
  opacity: 0.9;
  display: flex;
  align-items: center;
  padding-left: 20px;
  color: #018ed9; /* Add text color for better visibility on white */
  font-weight: bold;
  margin-left: 7px;
  margin-right: 7px;
}

.menu-item-home,
.menu-item {
  padding: 12px 27px;
  color: white;
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: background-color 0.2s;
  font-family: Microsoft YaHei;
  line-height: 30px;
}

.menu-item {
  /* margin-left: 7px; */
  font-family: Microsoft YaHei;
  font-weight: bold;
  font-size: 14px;
  color: #ffffff;
  line-height: 30px;
  margin-left: 7px;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.menu-item-home {
  font-family: Microsoft YaHei;
  font-size: 14px;
  line-height: 30px;
  margin-top: 22px;
}

.submenu-wrapper {
  margin-left: 7px;
}

.submenu-item {
  padding: 8px 50px;
  font-size: 14px;
  cursor: pointer;
  border-radius: 10px;
  transition: background-color 0.2s;
  &.disabled{
    cursor: not-allowed;
    &:hover{
      background-color: transparent;
      color: #b8b5b5;
    }
  }
}

.submenu-item:hover {
  background: #ffffff;
  border-radius: 10px;
  color: #018ed9;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(-100%);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}

.function-menu span {
  font-size: 14px;
  line-height: 17px;
  margin-bottom: 5px;
  color: white;
}

.menu-divider {
  width: 280px;
  height: 1px;
  border-radius: 1px;
  opacity: 0.13;
  margin: 7px 0;
  border: 1px solid #ffffff;
}
</style>
