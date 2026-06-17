<!-- HomePage.vue -->
<template>
  <div v-loading="loading" class="layoutPortal">
    <Header />
    <HeroSection />
    <div class="contentWrapper">
      <div class="contenPage">
        <router-view />
      </div>
      <Footer />
    </div>
  </div>
</template>

<script setup>
import Header from "./components/Header.vue"
import Footer from "./components/Footer.vue"
import HeroSection from "./components/HeroSection.vue"
import { applicationsList } from '@/ajax/app.js'
import store from "@/store/index"
	const loading = ref(false)
	const getApplicationsList = ()=>{
		loading.value = true
		applicationsList().then(res=>{
		loading.value = false
		if(res.succ){
      store.commit('SET_APP_LIST_DATA',res.content)

      console.log(`store.state.applicationsListData`,store.state)
			}
		}).catch(err=>{
			loading.value = false
		})
	}
	getApplicationsList()
</script>

<style lang="scss">
.layoutPortal {
  width: 100%;
  height: 100%;
  overflow-x: auto;
  padding-top: 80px;
  .header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100001;
    min-width: 1600px;
  }
  .heroSection{
    min-width: 1600px;
  }
  .contentWrapper {
    background: url("@/assets/images/pageBj.png") center no-repeat;
    background-size: 100% 100%;
    position: relative;
    min-width: 1600px;

    .contenPage {
      margin: 0 auto;
      max-width: 1700px;
      min-height: 500px;
    }
  }
}
</style>
