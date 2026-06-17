<template>
  <div class="feature-cards-container">
    <div class="left-column">
      <!-- 设计功能卡片 -->
      <div class="tabDesign" :style="{marginTop: !VITE_APP_IS_JIZAI ? '80px' : '40px'}">
        <!-- <div class="title">
          <img src="@/assets/images/arrow.png" alt="">
          <span class="name">设计研发</span>
          <span class="line">/</span>
          <span class="elName">Design</span>
        </div> -->
        <img src="@/assets/images/sjyf.png" alt="设计" class="cardTitleDesign" />
        <div class="feature-card">
          <div
            v-for="(item, index) in designFeatures"
            :key="'design-' + index"
            :class="`card-item ${item.status != 1 ? 'noActive' : ''}`"
            @click="handleCardClick(item)"
          >
            <div class="card-item-container">
              <div :class="{'item-icon':true,'item-icon-jz':item.icon.indexOf('.png') == -1}">
                <img v-if="item.icon.indexOf('.png') == -1" :src="getLocalImg(item.icon)" :alt="item.title" style="border-radius: 35%"/>
                <img v-else :src="item.icon?`/portal/common/v1/storage/download/${item.icon}?fileId=${item.icon}`:bglr" :alt="item.title" style="border-radius: 35%"/>
              </div>
              <div class="item-text">
                <h2>{{ item.name }}</h2>
                <!-- <p>{{ item.engName }}</p> -->
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 生产功能卡片 -->
      <div class="tabProduction">
        <img src="@/assets/images/sczc.png" alt="生产" class="cardTitleProduction" />
        <!-- <div class="title">
          <img src="@/assets/images/arrow.png" alt="">
          <span class="name">生产制造</span>
          <span class="line">/</span>
          <span class="elName">Production</span>
        </div> -->
        <div class="feature-card">
          <div
            v-for="(item, index) in productionFeatures"
            :key="'production-' + index"
            :class="`card-item ${item.status != 1 ? 'noActive' : ''}`"
            @click="handleCardClick(item)"
          >
            <div class="card-item-container">
              <div :class="{'item-icon':true,'item-icon-jz':item.icon.indexOf('.png') == -1}">
                <img v-if="item.icon.indexOf('.png') == -1" :src="getLocalImg(item.icon)" :alt="item.title" style="border-radius: 35%"/>
                <img v-else :src="item.icon?`/portal/common/v1/storage/download/${item.icon}?fileId=${item.icon}`:bglr" :alt="item.title" style="border-radius: 35%"/>
              </div>
              <div class="item-text">
                <h3>{{ item.name }}</h3>
                <!-- <p>{{ item.engName }}</p> -->
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="right-column">
      <!-- 管理功能卡片 -->
      <div class="tabManagement" :style="{marginTop: !VITE_APP_IS_JIZAI ? '80px' : '40px'}">
        <img src="@/assets/images/yygl.png" alt="管理" class="cardTitleManagement" />
          <!-- <div class="title">
          <span class="elName">Operation</span>
          <span class="line">/</span>
          <span class="name">运营管理</span>
          <img src="@/assets/images/arrow.png" alt="">
        </div> -->
        <div class="feature-card right-align">
          <div
            v-for="(item, index) in managementFeatures"
            :key="'management-' + index"
           :class="`card-item ${item.status != 1 ? 'noActive' : ''}`"
            @click="handleCardClick(item)"
          >
            <div class="card-item-container right-align-container">
              <div class="item-text">
                <h3>{{ item.name }}</h3>
                <!-- <p>{{ item.engName }}</p> -->
              </div>
              <div :class="{'item-icon':true,'item-icon-jz':item.icon.indexOf('.png') == -1}">
                 <img v-if="item.icon.indexOf('.png') == -1" :src="getLocalImg(item.icon)" :alt="item.title" style="border-radius: 35%"/>
                <img v-else :src="item.icon?`/portal/common/v1/storage/download/${item.icon}?fileId=${item.icon}`:bglr" :alt="item.title" style="border-radius: 35%"/>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 算力功能卡片 -->
      <div class="tabComputing">
        <img src="@/assets/images/slzyV2.png" alt="算力" class="cardTitleComputing" />
        <!-- <div class="title">
          <span class="elName">Computing Power</span>
          <span class="line">/</span>
          <span class="name">算力资源</span>
          <img src="@/assets/images/arrow.png" alt="">
        </div> -->
        <div class="feature-card right-align">
          <div
            v-for="(item, index) in computingFeatures"
            :key="'computing-' + index"
           :class="`card-item ${item.status != 1 ? 'noActive' : ''}`"
            @click="handleCardClick(item)"
          >
            <div class="card-item-container right-align-container">
              <div class="item-text">
                <h3>{{ item.name }}</h3>
                <!-- <p>{{ item.engName }}</p> -->
              </div>
              <div :class="{'item-icon':true,'item-icon-jz':item.icon.indexOf('.png') == -1}">
                <img v-if="item.icon.indexOf('.png') == -1" :src="getLocalImg(item.icon)" :alt="item.title" style="border-radius: 35%"/>
                <img v-else :src="item.icon?`/portal/common/v1/storage/download/${item.icon}?fileId=${item.icon}`:bglr" :alt="item.title" style="border-radius: 35%"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { designFeatures as designData, productionFeatures as productionData, managementFeatures as managementData, computingFeatures as computingData } from "@/Data/featureConstants"
import { links, demoHost } from "@/Data/constants"
import store from "@/store/index"
import { openApplication } from '@/utils/openApplication'
import bglr from '../../../assets/images/bglr.png';
import circuitHover from '@/assets/images/circuit-hover.png';
import codeHover from '@/assets/images/code-review-hover.png';
import elgHover from '@/assets/images/elg-hover.png';
import elg from '@/assets/images/elg.png';
import erds from '@/assets/images/erds.png';
import pcb from '@/assets/images/pcb.png';
import pcba from '@/assets/images/pcba.png';
import dsr from '@/assets/images/dsr.png';
import crr from '@/assets/images/crr.png';
import asc from '@/assets/images/asc.png';
import pdkb from '@/assets/images/pdkb.png';
import cn from '@/assets/images/cn.png';
import icn from '@/assets/images/icn.png';
const isJiZaiUser = computed(() => store.state.isJiZaiUser)
// 可以添加页脚数据或方法
const VITE_APP_IS_JIZAI = isJiZaiUser.value
const userInfo = computed(() => store.state.user.userInfo)
const loginName = computed(() => userInfo.value?.name)
const router = useRouter()

const getLocalImgMap = {
  'circuitHover': circuitHover,
  'codeHover': codeHover,
  'elgHover': elgHover,
  'elg': elg,
  'erds': erds,
  'pcb': pcb,
  'pcba': pcba,
  'dsr': dsr,
  'crr': crr,
  'asc': asc,
  'pdkb': pdkb,
  'cn': cn,
  'icn': icn,
}
const getLocalImg = (iconName)=>{
  if(getLocalImgMap[iconName]){
    return getLocalImgMap[iconName] 
  }else{
    return bglr
  }
}
// 获取应用列表
const applications = ref([])
// 设计功能卡片
const designFeatures = ref([])
// 生产功能卡片
const productionFeatures = ref([])
// 管理功能卡片
const managementFeatures = ref([])
// 算力功能卡片
const computingFeatures = ref([])
const applicationsListData = computed(()=>store.state.applicationsListData)

watch(()=>applicationsListData.value,val=>{
  if(val){
    designFeatures.value = val[`设计研发`]??[]
    productionFeatures.value = val[`生产制造`]??[]
    managementFeatures.value =val[`运营管理`]??[]
    computingFeatures.value = val[`算力资源`]??[]

    console.log(`[DEBUG] designFeatures`,designFeatures.value)
    

  }
},{deep:true,immediate:true})
// const loading = ref(false)
// const getApplicationsList = ()=>{
//   loading.value = true
//   applicationsList().then(res=>{
//     loading.value = false
//     if(res.succ){
//       designFeatures.value = res.content[`设计研发`]
//       productionFeatures.value = res.content[`生产制造`]
//       managementFeatures.value = res.content[`运营管理`]
//       computingFeatures.value = res.content[`算力资源`]
//     }
//   }).catch(err=>{
//     loading.value = false
//   })
// }
// getApplicationsList()


// 使用ref包装导入的数据以保持响应式
const handleCardClick = item => {

  if(item.status===1){
    // 统一跳转：http(s)/反代路径新标签打开；站内路由 router.push；未登录弹登录框
    openApplication(item)
  }else{
    // 跳转详情
    // router.push({
    //   path:`/appDetail`,
    //   query:{
    //     appId:item.id
    //   }
    // })
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
  //       store.commit(`SET_LOGIN_SHOW`, true, item.router)
  //       store.commit(`SET_LOGIN_TO_PATH`, item.router)
  //     }
  //   } else {
  //     console.log(`item`,item)
  //     router.push(item.router)
  //   }
  // } else {
  //   ElMessage.info(`功能暂未实现!`)
  // }
}
</script>

<style scoped>
.feature-cards-container {
  display: flex;
  justify-content: space-between;
  box-sizing: border-box;
  padding-bottom: 40px;
  background: url("@/assets/images/bg.png") center -50px no-repeat;
  background-size: 120% auto;
  position: relative;
  min-height: 1000px;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
}

.left-column {
  gap: 40px;
  /* position: absolute;
  left: 0;
  top: 0;
  bottom: 0; */
  .title{
    .elName{
      font-size: 32px;
      font-weight: 600;
      color: #AFBED1;
      letter-spacing: 2px;
      font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;
    }
    .name{
      font-size: 30px;
      font-weight: 600;
      letter-spacing: 2px;
    }
    .line{
      font-size: 30px;
      margin: 0 10px;
      color: #AFBED1;
    }
    >img{
      width: 22px;
      height: auto;
      margin-right: 10px;
    }

  }
}

.right-column {
  align-items: flex-end;
  gap: 40px;
  /* position: absolute;
  right: 0;
  top: 0;
  bottom: 0; */
  .title{
    display: flex;
    align-items: center;
    justify-content: flex-end;
    .elName{
      font-size: 32px;
      font-weight: 600;
      color: #AFBED1;
      letter-spacing: 2px;
      font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;
    }
    .name{
      font-size: 30px;
      font-weight: 600;
      letter-spacing: 2px;
    }
    .line{
      font-size: 30px;
      margin: 0 10px;
      color: #AFBED1;
    }
    >img{
      width: 22px;
      height: auto;
      margin-right: 10px;
      transform: rotate(180deg);
    }

  }
}

.cardTitleManagement,
.cardTitleProduction,
.cardTitleDesign {
  height: 40px;
  margin-left: 28px;
}

.cardTitleComputing {
  height: 40px;
  margin-right: 28px;
}

.tabManagement {
  margin-top: 80px;
}
.tabDesign {
  margin-top: 80px;
}

.feature-card {
  box-shadow: 2px 10px 30px 0px rgba(74, 106, 139, 0.16);
  background-color: rgba(246, 248, 250, 0.8);
  border-radius: 12px;
  border: 1px solid white;
  width: 395px;
  margin-top: 15px;
  max-height: 560px;
  overflow-y: auto;
}
.right-align{
  max-height: auto;
}

.feature-card.right-align .card-item {
  flex-direction: row-reverse;
  justify-content: flex-end;
  height: 94px;
}

.right-column .feature-card {
  margin-left: auto; /* 卡片本身靠右 */
}

.card-item {
  display: flex;
  height:94px;
  border-bottom: 1px solid rgba(91, 139, 198, 0.11);
}

.card-item:last-child {
  border-bottom: none;
}

.card-item-container {
  height: 100%;
  width: 393px;
  padding: 0 5px;
  display: flex;
  align-items: center;
  &:hover {
    background: linear-gradient(to right, #d4dfee, #f3f6f9);
    .item-icon {
      background:linear-gradient(to right, #39C7FE, #0979F9) ;
    }
    .item-icon-jz{
      background: #f8f8f8;
    }
  }
}
.noActive{
  .card-item-container {
  cursor: not-allowed;
    &:hover {
      background: none;
      .item-icon {
        background: #D5E1ED;
      }
      .item-icon-jz{
        background: #f8f8f8;
      }
    }
  }
 
}

.item-icon {
  width: 50px;
  height: 50px;
  background-color: #D5E1ED;
  border-radius: 30%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 24px 0 36px;
}
.item-icon-jz{
  width: 50px;
  height: 50px;
  background-color: #f8f8f8;
  border-radius: 30%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 24px 0 36px;
}

.right-align-container {
  justify-content: flex-end;
  /* padding-right: 36px; */
}

/* 调整右对齐时的图标和文本间距 */
.right-align .item-icon {
  margin: 0 36px 0 24px;
}

/* 确保文本在右对齐时也保持对齐 */
.right-align .item-text {
  text-align: right;
}

.item-icon img {
  width: auto;
  height: 30px;
}
.item-icon-jz img {
  width: 55px;
  height: 55px;
}

.item-text h3 {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.item-text p {
  font-size: 14px;
  color: #999;
  width: 264px;
  white-space: nowrap;
  overflow: hidden;
  overflow-wrap: break-word;
  text-overflow: ellipsis;
}

.card-item {
  cursor: pointer; /* 添加手型光标 */
  transition: all 0.2s ease; /* 添加过渡效果 */
}

.card-item:hover {
  transform: translateY(-2px); /* 悬停时轻微上浮 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
}
.noActive:hover {
  transform: translateY(-0); /* 悬停时轻微上浮 */
  box-shadow: none; /* 添加阴影效果 */
}
</style>
