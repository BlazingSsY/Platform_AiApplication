<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="logo-container">
        <img class="logo" src="@/assets/images/circuitReview/logo-bz.png" alt="Logo" />
      </div>

      <div class="contact-info">
        <template v-if="VITE_APP_IS_JIZAI">
          <div>
            <span class="contact-us">联系我们：</span>
            <span class="phonenum">上海市闵行区紫星路1588号</span>
          </div>
          <div>
            <span class="contact-us">系统版权：</span>
            <span class="phone">中航机载系统有限公司</span>
            <span class="contact-us">法律声明</span>
            <span class="contact-us">隐私政策</span>
            <span class="contact-us">证书下载</span>
          </div>
          <div>
            <span class="contact-us">技术支持：</span>
            <span class="phone">中航工业计算所</span>
            <span class="contact-us">硬件规则：</span>
            <span class="phone">029-81167292</span>
            <span class="contact-us">软件使用：</span>
            <span class="phone">029-81167267</span>
          </div>
        </template>
      </div>
    </div>
  </footer>
</template>

<script setup>
  import store from "@/store/index"
	const isJiZaiUser = computed(() => store.state.isJiZaiUser)
  // 可以添加页脚数据或方法
  const VITE_APP_IS_JIZAI = isJiZaiUser.value
</script>


<style scoped>
  .footer {
    background-color: #242d3f;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    margin: 0 auto;
  }
  
  .footer-content {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 250px;
  }
  
  .logo-container {
    display: flex;
    align-items: center;
  }
  
  .logo {
    width: 300px;
    height: auto;
    margin-right: 15px;
  }
  
  .contact-info {
    display: flex;
    flex-direction: column;
    /* align-items: center; */
  }
  
  .divider,
  .contact-us {
    font-size: 15px;
    color: white;
    /* margin-right: 20px; */
    font-weight: 500;
    width: 200px;
  }
  .contact-us {
    display: inline-block;
    width: 80px;
  }
  
  .phonenum,
  .phone {
    font-size: 15px;
    color: #cdcdd0;
  }
  
  .phone {
    margin-right: 30px;
  }
  
  .phonenum {
    padding-top: 3px;
  }
  </style>
  