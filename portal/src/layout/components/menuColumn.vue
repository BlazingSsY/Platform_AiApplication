<script setup>
const route = useRoute()
const router = useRouter()
// import { circuitReview } from "@/routes";
// console.log(`circuitReview`,circuitReview)
import store from "@/store/index"
console.log('user info ', store.state.user?.userInfo)
const isAdmin = computed(() => store.state.user?.userInfo?.role?.name===`管理员`)
const isExpert = computed(() => store.state.user?.userInfo?.role?.name===`专家`)
//
// console.log(`isAdmin`, isAdmin)
const handleOutsideClick = event => {}
const design = ref(true)
const isExpanded = ref(false)
const actPath = ref(route.path)
const isHomeHovered = ref(false)
const toggleMenu = () => {
  isExpanded.value = !isExpanded.value
}
const handleHomeClick = path => {
  store.commit("CLEAR_PAGES_INFO_STROE")
  actPath.value = path
  router.push({ path: path })
  toggleMenu()
}
</script>

<template>
  <div class="menuColumn">
    <div class="menu-system">
      <div class="function-menu" :style="{ left: isExpanded ? '280px' : '0' }" @click="toggleMenu">
        <span>功<br />能<br />菜<br />单</span>
        <svg width="15" height="15" viewBox="0 0 100 100" :style="{ transform: isExpanded ? 'rotate(180deg)' : 'none' }"><polygon points="20,10 80,50 20,90" style="fill: white" /></svg>
      </div>
      <transition name="slide">
        <div v-if="isExpanded" class="menu-content">
          <div :class="{ 'menu-item-home': true, actPath: actPath === `/circuitReviewHome` }" @click="handleHomeClick(`/circuitReviewHome`)">
            <div class="menu-item-wrapper">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </div>
          </div>
          <div class="menu-divider" />

          <div :class="{ 'menu-item-home': true, actPath: actPath === `/documentReview` }" @click="handleHomeClick(`/documentReview`)">
            <div class="menu-item-wrapper">
              <el-icon><Document /></el-icon>
              <span>网表文件审查</span>
            </div>
          </div>
          <div class="menu-divider" />
          
          

          <div :class="{ 'menu-item-home': true, actPath: actPath === `/designNamingConvention` }" @click="handleHomeClick(`/designNamingConvention`)">
            <div class="menu-item-wrapper">
              <el-icon><Setting /></el-icon>
              <span>设计命名规范</span>
            </div>
          </div>
          <div class="menu-divider" />

          <div :class="{ 'menu-item-home': true, actPath: actPath === `/ruleList` }" @click="handleHomeClick(`/ruleList`)">
            <div class="menu-item-wrapper">
              <el-icon><Memo /></el-icon>
              <span>规则列表</span>
            </div>
          </div>
          <div class="menu-divider" />

          <div :class="{ 'menu-item-home': true, actPath: actPath === `/statisticalAnalysis` }" @click="handleHomeClick(`/statisticalAnalysis`)">
            <div class="menu-item-wrapper">
              <el-icon><DataAnalysis /></el-icon>
              <span>统计分析</span>
            </div>
          </div>
          <div class="menu-divider" />

          <!-- <div :class="{ 'menu-item-home': true, actPath: actPath === `/fileRecycleBin` }" @click="handleHomeClick(`/fileRecycleBin`)">
            <div class="menu-item-wrapper">
              <i class="iconfont icon-huishouzhan" />
              <span>网表文件回收站</span>
            </div>
          </div>
          <div class="menu-divider" /> -->

          <div :class="{ 'menu-item-home': true, actPath: actPath === `/toolDownload` }" @click="handleHomeClick(`/toolDownload`)">
            <div class="menu-item-wrapper">
              <el-icon><Suitcase /></el-icon>
              <span>工具下载</span>
            </div>
          </div>
          <div class="menu-divider" />

          <div :class="{ 'menu-item-home': true, actPath: actPath === `/feedback` }" @click="handleHomeClick(`/feedback`)">
            <div class="menu-item-wrapper">
              <el-icon><DataAnalysis /></el-icon>
              <span>意见反馈</span>
            </div>
          </div>

          <div v-if="isAdmin || isExpert" class="menu-divider" />
          <div v-if="isAdmin || isExpert"  :class="{ 'menu-item-home': true, actPath: actPath === `/reviewList` }" @click="handleHomeClick(`/reviewList`)">
            <div class="menu-item-wrapper">
              <el-icon><Tickets /></el-icon>
              <span>问题复核</span>
            </div>
          </div>

          <div v-if="isAdmin" class="menu-divider" />
          <div v-if="isAdmin" :class="{ 'menu-item-home': true, actPath: actPath === `/viewLog` }" @click="handleHomeClick(`/viewLog`)">
            <div class="menu-item-wrapper">
             <el-icon><Document /></el-icon>
              <span>日志查看</span>
            </div>
          </div>

          <div class="menu-divider" />
          <div :class="{ 'menu-item-home': true, actPath: actPath === `/designExperience` }" @click="handleHomeClick(`/designExperience`)">
            <div class="menu-item-wrapper">
              <el-icon><Share /></el-icon>
              <span>设计经验分享</span>
            </div>
          </div>

          <div class="menu-divider" />
          <div :class="{ 'menu-item-home': true, actPath: actPath === `/updateInstructions` }" @click="handleHomeClick(`/updateInstructions`)">
            <div class="menu-item-wrapper">
              <el-icon><DocumentRemove /></el-icon>
              <span>更新说明</span>
            </div>
          </div>

          <!-- <div class="menu-divider" /> -->


          <!-- 系统管理 -->
          <!-- <div class="menu-item" @click="design = !design">
            <div class="menu-item-wrapper">
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </div>
            <svg width="12" height="12" viewBox="0 0 100 100" :style="{ transform: design ? 'rotate(180deg)' : 'none' }">
              <polygon points="10,20 50,80 90,20" style="fill: white" />
            </svg>
          </div>
          <div v-show="design" class="submenu-wrapper">
            <div :class="{ 'submenu-item': true, actPath: actPath === `/role` }" @click="handleHomeClick(`/role`)">角色管理</div>
          </div> -->
        </div>
      </transition>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.function-menu {
  width: 43px;
  height: 111px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 14px;
  position: absolute;
  left: 0;
  top: 24px;
  z-index: 2;
  cursor: pointer;
  z-index: 100;

  box-shadow: 3px 4px 18px 0px rgba(38, 99, 241, 0.35);
  border-radius: 1px 10px 10px 1px;
  transition: left 0.3s ease;
  width: 43px;
  background: linear-gradient(123deg, #018ed9, #0270f6);
  span {
      font-size: 15px;
    line-height: 17px;
    margin-bottom: 5px;
    color: white;
  }
}
</style>

<style scoped>
.menuColumn {
  position: fixed;
  height: 1300px;
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
  height:100vh;
  /* background: rgba(2, 118, 239, 0.9); */
  display: flex;
  flex-direction: column;
  z-index: 1;
  overflow-y: auto;
  background: linear-gradient(180deg, #018ed9, #4a91f4);
  box-shadow: 3px 4px 18px 0px rgba(38, 99, 241, 0.35);
  border-radius: 1px;
  opacity: 0.96;
  padding-top: 22px;

  /* transform: translateX(-110%); 
    transition: transform 0.3s ease, opacity 0.3s ease; 
    opacity: 0; */
}

.menu-item-home-wrapper,
.menu-item-wrapper {
  display: flex;
  align-items: center;
  .el-icon,
  i {
    font-size: 20px;
    margin-right: 11px;
  }
  i {
    font-size: 18px;
  }
}

.menu-item-home:hover {
  width: 267px;
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
    font-size: 15px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: background-color 0.2s;
  font-family: Microsoft YaHei;
  line-height: 30px;
  &.actPath {
    width: 267px;
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
}

.menu-item {
  /* margin-left: 7px; */
  font-family: Microsoft YaHei;
  font-weight: bold;
    font-size: 15px;
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
    font-size: 15px;
  line-height: 30px;
}

.submenu-wrapper {
  margin-left: 7px;
}

.submenu-item {
  padding: 8px 50px;
    font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-radius: 10px;
  width: 267px;
  &.actPath {
    background: #ffffff;
    border-radius: 10px;
    color: #018ed9;
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
    font-size: 15px;
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
