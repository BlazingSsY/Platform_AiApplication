<script setup lang="ts">
  import localTitle from "./components/localTitle.vue"
  import { sourceCodeReviewRuleAll ,sourceCodeReviewRuleSelect, sourceCodeReviewRuleDetails,sourceCodeReviewRuleMetadata} from "@/ajax/sourcecodereview"
  import table2excel from "js-table2excel"
  import { ref, reactive, nextTick } from "vue"
  const loading = ref(false)
  const router = useRouter()
  const checkAll = ref(false)
  const checkList = ref([])
  const handleCheckAllChange = (val) => {
    checkList.value = val ? rulesListDataLocal.value.map(r=>r.id) : []
    selectSize.value = checkList.value.length
  }
  const handleCheckedCitiesChange = (value) => {
    const checkedCount = value.length
    checkAll.value = checkedCount === rulesListDataLocal.value.length
    selectSize.value = checkedCount
  }
  
  const handleCheckedChange = (flag,item) => {
    if (flag) {
      selectActListData.value.forEach(r=>{
        if(r.id === item.id){
          r.selectStatus = 1
        }
      })
    } else {
      selectActListData.value.forEach(r=>{
        if(r.id === item.id){
          r.selectStatus = 0
        }
      })
    }
  }
  // 获取审查规则列表
  const pages = reactive({
    pageNum:1,
    pagesize:15,
    total: 0
  })
  const filter = ref({
    "language": [],
    "selectStatus": [],
    "ruleType": [],
    "ruleSource": [],
    "desc": ""
  })

  const getCurrentTimestampStr = () => {
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, `0`)
    const day = String(now.getDate()).padStart(2, `0`)
    const hours = String(now.getHours()).padStart(2, `0`)
    const minutes = String(now.getMinutes()).padStart(2, `0`)
    const seconds = String(now.getSeconds()).padStart(2, `0`)
    return `${year}${month}${day}${hours}${minutes}${seconds}`
  }
  
  
  
  const selectSize = ref(0)
  const selectActListData = ref([])
  const getSelectActListData = ()=>{
    sourceCodeReviewRuleAll({})
      .then(res => {
        if (res.succ) {
          selectActListData.value = res.content.rules
        }
      })
  }
  
  const rulesListDataLocal = ref([])
  const descOverflowMap = ref<Record<string | number, boolean>>({})
  const langOverflowMap = ref<Record<string | number, boolean>>({})
  const markDescOverflow = () => {
    nextTick(() => {
      const map: Record<string | number, boolean> = {}
      // 计算规则描述是否溢出（保持原实现）
      const descNodes = document.querySelectorAll('.desc-text')
      const descMap: Record<string | number, boolean> = {}
      descNodes.forEach(node => {
        const el = node as HTMLElement
        const id = el.dataset.id || el.dataset.idx
        if (id !== undefined) {
          descMap[id] = el.scrollWidth > el.clientWidth
        }
      })
      descOverflowMap.value = descMap
      // 计算编程语言字段是否溢出
      const langNodes = document.querySelectorAll('.lang-text')
      const langMap: Record<string | number, boolean> = {}
      langNodes.forEach(node => {
        const el = node as HTMLElement
        const id = el.dataset.id || el.dataset.idx
        if (id !== undefined) {
          langMap[id] = el.scrollWidth > el.clientWidth
        }
      })
      langOverflowMap.value = langMap
    })
  }
  const sourceCodeReviewRuleAllFn = ()=>{
    loading.value = true
    sourceCodeReviewRuleAll({pageSize:pages.pagesize,pageNum:pages.pageNum,filter:filter.value})
      .then(res => {
        loading.value = false
        if (res.succ) {
          rulesListDataLocal.value = res.content.rules
          markDescOverflow()
          // selectSize.value = res.content.rules.filter(r=>r.selectStatus==1).length
          checkList.value = res.content.rules.filter(r=>r.selectStatus==1||r.mustSelect==1).map(r=>r.id)
          selectSize.value = checkList.value.length
          checkAll.value = checkList.value.length > 0 && checkList.value.length === rulesListDataLocal.value.length
          pages.total = res.content.rules.length
        }
      })
      .catch(() => {
        loading.value = false
      })
  }
  getSelectActListData()
  sourceCodeReviewRuleAllFn()
  
  const metadata = ref({
    language:[],
    ruleType:[],
    selectStatus:[],
    ruleSource:[],
  })
  const sourceCodeReviewRuleMetadataFn = ()=>{
    sourceCodeReviewRuleMetadata().then(res=>{
      if(res.succ){
        metadata.value.language=res.content.language.filter(r=>r!=`不限`)
        metadata.value.ruleType=res.content.ruleType.filter(r=>r!=`不限`)
        metadata.value.ruleSource=res.content.ruleSource.filter(r=>r!=`不限`)
      }
    })  
  }
  sourceCodeReviewRuleMetadataFn()
  
  const goBack = () => {
    router.go(-1)
  }
  
  const choseFn = ()=>{
    const selectedIds = checkList.value.slice()
    if(selectedIds.length === 0){
      ElMessage.warning('请选择规则')
      return
    }
    // 校验必选项是否都选中了
    const requiredList = rulesListDataLocal.value.filter(item => item.mustSelect === 1)
    const unselectedRequired = requiredList.filter(item => !selectedIds.includes(item.id))
    if (unselectedRequired.length > 0) {
      ElMessage.warning(`有 ${unselectedRequired.length} 条必选规则未选中，请先选中`)
      return
    }
    sourceCodeReviewRuleSelect({ids:selectedIds}).then(res=>{
      if(res.succ){
        ElMessage.success('选中成功')
        pages.pageNum = 1
        pages.total = 0
        rulesListDataLocal.value.splice(0)
        checkList.value = []
        getSelectActListData()
        sourceCodeReviewRuleAllFn()
      }
    })
  }
  const resetFn = ()=>{
    filter.value = {
      "language": [],
      "selectStatus": [],
      "ruleType":[],
      "ruleSource":[],
      "desc": ""
    } 
    searchFn()
  }

  // 调试：注入一条超长描述以验证 tooltip 触发
  const injectTestLongDesc = () => {
    if (!rulesListDataLocal.value.length) return
    const longDesc = '这是一个很长的规则描述，用于测试 tooltip 是否在文字被截断时出现。'.repeat(3)
    rulesListDataLocal.value[0] = { ...rulesListDataLocal.value[0], desc: longDesc }
    markDescOverflow()
  }
  const searchFn = ()=>{
    pages.pageNum = 1
    pages.total = 0
    rulesListDataLocal.value.splice(0)
    checkList.value = []
    sourceCodeReviewRuleAllFn()
  }
  const exportFn = ()=>{
    const columns = [
      { title: `序号`, key: `index`, type: `text` },
      { title: `编程语言`, key: `language`, type: `text` },
      { title: `是否选中`, key: `selectStatus`, type: `text` },
      { title: `是否必选`, key: `mustSelect`, type: `text` },
      { title: `规则类型`, key: `ruleType`, type: `text` },
      { title: `规则来源`, key: `ruleSource`, type: `text` },
      { title: `规则描述`, key: `desc`, type: `text` }
    ]

    sourceCodeReviewRuleAll({pageSize:999,pageNum:1})
      .then(res => {
        if (res.succ) {
          const records = res.content.rules
            .filter(r => r.selectStatus)
            .map((r, i) => ({
              index: i + 1,
              language: Array.isArray(r.language) ? r.language.join(', ') : (r.language===`C_PLUS`?`C++`:r.language),
              selectStatus: r.selectStatus === 1 ? '是' : '否',
              mustSelect: r.mustSelect === 1 ? '是' : '否',
              ruleType: r.ruleType || '',
              ruleSource: r.ruleSource || '',
              desc: r.desc || ''
            }))

          if (!records.length) {
            ElMessage.error('没有数据可导出！')
            return
          }

          table2excel(columns, records, `规则列表_${getCurrentTimestampStr()}.xls`)
        }
      })
  }
  
  const detailShow = ref(false)
  const detailDetail = ref(null)
  const detailFn = (item)=>{
    sourceCodeReviewRuleDetails({ids:[item.id]}).then(res=>{
      if(res.succ){
        console.log(JSON.parse(JSON.stringify(res.content)))
        detailDetail.value = res.content.rules[0]
        // detailDetail.value.errorExample = detailDetail.value.errorExample?.replace(/\/n/g, '<br>')
        // detailDetail.value.fixExample = detailDetail.value.fixExample?.replace(/\/n/g, '<br>')
        if (detailDetail.value.errorExample) {
          detailDetail.value.errorExample = detailDetail.value.errorExample.replace(/\n/g, '<br>')
        }
        if (detailDetail.value.fixExample) {
          detailDetail.value.fixExample = detailDetail.value.fixExample.replace(/\n/g, '<br>')
        }
        if (detailDetail.value.correctExample) {
          detailDetail.value.correctExample = detailDetail.value.correctExample.replace(/\n/g, '<br>')
        }
        console.log(`---detailDetail`,detailDetail.value)
        detailShow.value = true
      }
    })
  }
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="ruleList" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
      <localTitle title="规则列表" >
        <template #rightBtn>
          <el-button type="primary" style="margin-left: 12px" :disabled="checkList.length===0" @click="choseFn">选中提交</el-button>
          <el-button type="primary" @click="exportFn">导出</el-button>
          <!-- <el-button type="success" plain size="small" @click="injectTestLongDesc">测试长描述</el-button> -->
          <div class="descs">
            <span>已选中: {{ selectSize }} 条</span>
            <span>共: {{ pages.total }} 条</span>
          </div>
        </template>
      </localTitle>
      <div class="tableBox">
        <div class="searchBox">
          <el-form :model="filter" label-width="auto" inline>
            <el-form-item label="编程语言">
              <el-select v-model="filter.language" placeholder="请选择" clearable multiple style="width: 180px;" collapse-tags collapse-tags-tooltip :max-collapse-tags="2">
                <el-option
                  v-for="item in metadata.language"
                  :key="item"
                  :label="item===`C_PLUS`?`C++`:item "
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="规则类型">
              <el-select v-model="filter.ruleType" placeholder="请选择" clearable multiple style="width: 180px;" collapse-tags collapse-tags-tooltip :max-collapse-tags="2">
                <el-option
                  v-for="item in metadata.ruleType"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="是否选中">
              <el-select v-model="filter.selectStatus" placeholder="请选择" clearable multiple style="width: 180px;" collapse-tags collapse-tags-tooltip :max-collapse-tags="2">
                <el-option
                  label="已选择"
                  value="1"
                />
                <el-option
                  label="未选择"
                  value="0"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="规则来源">
              <el-select v-model="filter.ruleSource" placeholder="请选择" clearable multiple style="width: 180px;" collapse-tags collapse-tags-tooltip :max-collapse-tags="2">
                <el-option
                  v-for="item in metadata.ruleSource"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="规则描述">
              <el-input v-model="filter.desc" placeholder="请输入规则描述" clearable style="width: 180px;"/> 
            </el-form-item>
            <el-form-item class="search-btns">
              <el-button type="primary" @click="searchFn">查询</el-button>
              <el-button @click="resetFn">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <ul class="infinite-list infinite-list1">
            <li class="header">
            <div class="row w60" >
              <el-checkbox
                v-model="checkAll"
                @change="handleCheckAllChange"
              />
            </div>
            <div class="row w100 tableHead" >
              序号
            </div>
            <div class="row w250 tableHead">编程语言</div>
            <div class="row w100 tableHead">是否选中</div>
            <div class="row w100 tableHead">是否必选</div>
            <div class="row w150 tableHead">规则类型</div>
            <div class="row w150 tableHead">规则来源</div>
            <div class="row tableHead">规则描述</div>
  <!--          <div class="row w100">操作</div>-->
          </li>
          </ul>
        <el-checkbox-group v-if="rulesListDataLocal.length" v-model="checkList" @change="handleCheckedCitiesChange">
          <ul class="infinite-list" style="overflow-y: auto">
            <li v-for="(item,index) in rulesListDataLocal" :key="index" class="infinite-list-item">
              <div class="row w60">
                <el-checkbox :value="item.id" :disabled="item.mustSelect===1" @change="($event)=>handleCheckedChange($event,item)"/>
              </div>
              <div class="row w100">{{ index+1 }}</div>
              <div class="row w250">
                <el-tooltip
                  effect="light"
                  :content="Array.isArray(item.language) ? item.language.join(', ') : (item.language===`C_PLUS`?`C++`:item.language)"
                  placement="top"
                  :disabled="!langOverflowMap[item.id]"
                >
                  <el-text class="lang-text" :data-id="item.id" truncated>
                    {{ Array.isArray(item.language) ? item.language.join(', ') : (item.language===`C_PLUS`?`C++`:item.language) }}
                  </el-text>
                </el-tooltip>
              </div>
              <div class="row w100">{{ item.selectStatus===1?'是':'否' }}</div>
              <div class="row w100">{{ item.mustSelect===1?'是':'否' }}</div>
              <div class="row w150">{{ item.ruleType }}</div>
              <div class="row w150">{{ item.ruleSource }}</div>
              <div class="row">
                <el-tooltip
                  effect="light"
                  :content="item.desc || ''"
                  placement="top"
                  :disabled="!descOverflowMap[item.id]"
                >
                  <el-text class="desc-text" :data-id="item.id" :title="descOverflowMap[item.id] ? '' : null" truncated>{{ item.desc }}</el-text>
                </el-tooltip>
              </div>
              <!-- <div class="row w100">
                <el-button type="primary" link @click="detailFn(item)">详情</el-button>
              </div> -->
            </li>
          </ul>
        </el-checkbox-group>
      </div>
  
      <!-- 详情 -->
      <el-dialog v-model="detailShow" draggable class="customDialog customDialogPT0" title="查看" top="10vh" :destroy-on-close="true" :close-on-click-modal="false" :close-on-press-escape="false" width="60%">
        <div class="detailView">
          <div class="title">正确示例</div>
          <div class="content" style="padding-top: 20px;" v-html="detailDetail.correctExample" />
          <div class="title">错误示例</div>
          <div class="content" style="padding-top: 20px;" v-html="detailDetail.errorExample"/>
          <div class="title">解释</div>
          <div class="content content1">
            {{ detailDetail.explain }}
          </div>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button class="curtromPrimary" @click="detailShow = false"> 关 闭 </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <style lang="scss" scoped>
  .tableHead {
    font-weight: 600; 
    color: #333; 
    font-family: "黑体", "Microsoft YaHei", sans-serif;
  }
  .ruleList {
    display: flex;
    flex-direction: column;
    height: 100%;
    font-size: 15px;
    :deep(.el-form-item__label),
    :deep(.el-input__inner),
    :deep(.el-select__selected-item),
    :deep(.el-select-dropdown__item),
    :deep(.el-checkbox__label),
    :deep(.el-button),
    :deep(.el-text) {
      font-size: 15px;
    }
    :deep(.descs){
        position: absolute;
        top: 0;
        left: -30%;
        height: 30px;
        display: flex;
        align-items: center;
        span{
          margin: 0 10px;
        }
      }
      .searchBox{
        display: flex;
        padding: 0;
        :deep(.el-form){
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          width: 100%;
          gap: 0;
        }
        :deep(.el-form-item){
          margin-bottom: 0;
          margin-right: 12px;
        }
        :deep(.el-form-item.search-btns){
          margin-left: auto; /* 将按钮推到右侧 */
          margin-right: 0;
        }
        :deep(.el-form-item.search-btns .el-button:last-child){
          margin-right: 0;
        }
      }
    .tableBox {
      flex: 1;
      padding-bottom: 20px;
    
      .infinite-list1{
          height: 36px;
          li{
            display: flex;
            &.header{
              background-color: #f5f5f5;
              >div{
                min-height: 36px;
                font-size: 15px;
              }
            }
            >div{
              flex: 1;
              flex-shrink: 0;
              display: flex;
              align-items: center;
              justify-content: center;
              min-height: 36px;
              font-size: 15px;
            }
            .w100{
              width: 100px;
              flex: 0 0 100px;
            }
            .w250{
          width: 250px;
          flex: 0 0 250px;
          text-align: center;
        }
            .w200{
          width: 200px;
          flex: 0 0 200px;
          text-align: center;
        }
            .w150{
          width: 150px;
          flex: 0 0 150px;
          text-align: center;
        }
          
            .w60{
              width: 60px;
              flex: 0 0 60px;
              .el-checkbox{
                .el-checkbox__label{
                  padding-left: 0 !important;
                }
  
              }
            }
          }
        }
      :deep(.el-checkbox-group){
        height: 100%;
        .infinite-list{
          height: 100%;
          max-height: 900px;
          &.infinite-list1{
            height: 36px;
          }
          li{
            display: flex;
            &.header{
              background-color: #f5f5f5;
              >div{
                min-height: 36px;
                font-size: 15px;
              }
            }
            >div{
              flex: 1;
              flex-shrink: 0;
              display: flex;
              align-items: center;
              justify-content: center;
              min-height: 36px;
              width: 0;
              font-size: 15px;
            }
            .w100{
              width: 100px;
              flex: 0 0 100px;
            }
            .w250{
          width: 250px;
          flex: 0 0 250px;
          text-align: center;
        }
            .w200{
          width: 200px;
          flex: 0 0 200px;
          text-align: center;
        }
        .w150{
          width: 150px;
          flex: 0 0 150px;
          text-align: center;
        }
            .w60{
              width: 60px;
              flex: 0 0 60px;
              .el-checkbox{
                .el-checkbox__label{
                  padding-left: 0 !important;
                }
  
              }
            }
            .el-text{
              color: #333;
              width: 100%;
              height: 36px;
              line-height: 36px;
            }
            .desc-text {
              width: 100%;
            }
          }
          .infinite-list-item{
            background-color: #fffdfd;
            border-bottom: 1px solid #eee;
            &:hover{
              opacity: 0.8;
            }
          }
        }
      }
     
      // :deep(.el-table) {
      //   background-color: transparent;
      //   height: 100%;
      //   max-height: 600px;
      //   .opt {
      //     .btn {
      //       height: 28px;
      //       line-height: 28px;
      //       padding: 0 4px;
      //       min-width: 70px;
      //       text-align: center;
  
      //       .el-loading-mask {
      //         .el-loading-spinner {
      //           height: 28px;
      //           margin: 0;
      //           top: 0;
  
      //           .circular {
      //             width: 30px;
      //             height: 30px;
      //           }
      //         }
      //       }
      //     }
      //   }
      // }
    }
  
    :deep(.el-dialog){
      .detailView{
        .title{
          line-height: 32px;
          font-weight: 600;
          font-size: 16px;
        }
        .content{
          padding:0 20px 24px;
          line-height: 24px;
          background-color: #f5f5f5;
          border-radius: 6px;
        }
        .content1{
          padding-top: 24px;
        }
      }
    }
  }
  </style>
