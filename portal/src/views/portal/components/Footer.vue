  <template>
  <footer class="footer">
    <div class="footer-content">
      <div class="logo-container">
        <img v-if="VITE_APP_IS_JIZAI" class="logo" src="@/assets/images/logo-white-jz.png" alt="Logo" />
        <img v-else class="logo" src="@/assets/images/logo-white.png" alt="Logo" />
      </div>

      
      <div v-if="VITE_APP_IS_JIZAI" class="contact-info">
        <div>
          <span class="contact-us contact-us1">联系我们：</span>
          <span class="phonenum">上海市闵行区紫星路1588号</span>
        </div>
        <div>
          <span class="contact-us contact-us1">系统版权：</span>
          <span class="phone">中航机载系统有限公司</span>
          <span class="contact-us contact-us2">数智平台版本号：</span>
          <span class="phone version-link" @click="openVersionDialog">v1.0.1</span>
        </div>
        <div>
          <span class="contact-us contact-us1">技术支持：</span>
          <span class="phone">中航工业计算所</span>
          <span class="contact-us contact-us1">法律声明</span>
          <span class="contact-us contact-us1">隐私政策</span>
          <span class="contact-us contact-us1">证书下载</span>
        </div>
      </div>

      <div v-else class="contact-info" >
        <div>
          <span class="contact-us">联系我们：</span>
          <span class="phone">西安市锦业二路15号</span>
          <span class="contact-us">航空工业计算所数智平台版本号：</span>
          <span class="phone version-link" @click="openVersionDialog">v1.0.1</span>
        </div>
        <div>
          <span class="contact-us">技术支持：</span>
          <span class="phone">数智技术研究室(电话:029-81167267)</span>
          <span class="contact-us">法律声明</span>
          <span class="contact-us">隐私政策</span>
          <span class="contact-us">证书下载</span>
        </div>
      </div>
    </div>
  </footer>

  <!-- 系统版本弹窗 -->
  <el-dialog v-model="showVersionDialog" draggable title="系统版本" width="460px">
    <div v-loading="versionLoading" class="version-content">
      <div class="version-row">
        <span class="version-label">数智平台前端版本</span>
        <span class="version-colon">：</span>
        <span class="version-value">{{ versionText || '-' }}</span>
      </div>
      <div class="version-row">
        <span class="version-label">{{ serviceLabels.circuit }}</span>
        <span class="version-colon">：</span>
        <span class="version-value">{{ serviceVersions.circuit || '-' }}</span>
      </div>      
      <div class="version-row">
        <span class="version-label">{{ serviceLabels.code }}</span>
        <span class="version-colon">：</span>
        <span class="version-value">{{ serviceVersions.code || '-' }}</span>
      </div>
      <div class="version-row">
        <span class="version-label">{{ serviceLabels.logic }}</span>
        <span class="version-colon">：</span>
        <span class="version-value">{{ serviceVersions.logic || '-' }}</span>
      </div>
      <div class="version-row">
        <span class="version-label">{{ serviceLabels.portal }}</span>
        <span class="version-colon">：</span>
        <span class="version-value">{{ serviceVersions.portal || '-' }}</span>
      </div>
      <br/>
    </div>
  </el-dialog>
</template>

<script  setup>
  import store from "@/store/index"
  import { getToken } from '@/utils/auth'

  const isJiZaiUser = computed(() => store.state.isJiZaiUser)
  // 可以添加页脚数据或方法
  const VITE_APP_IS_JIZAI = isJiZaiUser.value

  // 系统版本弹窗
  const showVersionDialog = ref(false)
  const versionText = ref('')
  const versionLoading = ref(false)

  const serviceLabels = {
    circuit: '电路审查中间件版本',    
    code: '代码审查中间件版本',
    logic: '逻辑审查中间件版本',
    portal: '门户服务版本',
  }

  const serviceVersions = reactive({
    circuit: '',    
    code: '',
    logic: '',
    portal: '',
  })

  const serviceApis = {
    circuit: '/circuitreview/common/v1/version',    
    code: '/sourcecodereview/common/v1/version',
    logic: '/logicreview/common/v1/version',
    portal: '/portal/common/v1/version',
  }

  const openVersionDialog = async () => {
    showVersionDialog.value = true
    versionLoading.value = true

    try {
      // 前端版本
      try {
        const res = await fetch('/version.txt?t=' + Date.now())
        if (res.ok) {
          const text = await res.text()
          // 防止 Vite SPA fallback 返回 index.html
          if (text && !text.trim().startsWith('<!')) {
            versionText.value = text
          } else {
            versionText.value = '未知'
          }
        }
      } catch {
        versionText.value = '未知'
      }

      // 服务端版本（并行请求）— 用 fetch 避免触发 axios 全局错误拦截
      const baseUrl = import.meta.env.VITE_APP_API_BASE_URL
      const token = getToken('sys_tokenID')
      // if (!token) {  // 未登录也可以查询到版本号
      //   Object.keys(serviceApis).forEach((key) => {
      //     serviceVersions[key] = '未知'
      //   })
      // } else {
        const promises = Object.entries(serviceApis).map(async ([key, url]) => {
          try {
            const res = await fetch(`${baseUrl}${url}`, {
              headers: { Authorization: token }
            })
            if (res.ok) {
              const text = await res.text()
              try {
                const data = JSON.parse(text)
                serviceVersions[key] = typeof data === 'string' ? data : (data?.data?.version || data?.data || data || '未知')
              } catch {
                serviceVersions[key] = text || '未知'
              }
            } else {
              serviceVersions[key] = '未知'
            }
          } catch {
            serviceVersions[key] = '未知'
          }
        })

        await Promise.all(promises)
      // }
    } finally {
      versionLoading.value = false
    }
  }
</script>

<style lang="scss" scoped>
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
  /* max-width: 1820px; */
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
  width: 365px;
  height: auto;
  margin-right: 15px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  /* align-items: center; */
  >div{
    &:first-child{
      margin-bottom: 4px;
    }
  }
}

.divider,
.contact-us {
  font-size: 15px;
  color: white;
  margin-right: 20px;
  font-weight: 500;
  width: 200px;
}
.contact-us {
  display: inline-block;
  width: auto;
}
.contact-us1 {
  display: inline-block;
  width: 80px;
}
.contact-us2 {
  display: inline-block;
  width: 120px;
}
.phonenum,
.phone {
  font-size: 15px;
  color: #cdcdd0;
}

.phone {
  margin-right: 30px;
}

.version-link {
  cursor: pointer;
  text-decoration: underline;
  &:hover {
    color: #fff;
  }
}

.phonenum {
  padding-top: 3px;
}

.version-content {
  .version-row {
    display: flex;
    align-items: baseline;
    margin-bottom: 8px;
  }
  .version-label {
    display: inline-block;
    width: 9em;
    text-align: left;
    font-weight: bold;
  }
  .version-colon {
    margin: 0 6px;
  }
  .version-value {
    color: #666;
  }
}

</style>
