<script setup>
  import localTitle from "./components/localTitle.vue"
  import { getCircuitReviewRules } from "@/ajax/circuitreview"
  const loading = ref(false)
  const rulesListDataLocal = ref([])
  const filter = reactive({
    ruleTypeEnum: "",
    ruleName: ""
  })
  const ruleTypeMapping = {
    "PROPOSITIONAL_RULE": `建议规则`,
    "NEEDFUL_RULE": `必要规则`,
    "COERCIVE_RULE": `强制规则`
  }
  const initData = ()=>{
    loading.value = true
      // 获取审查规则列表
    getCircuitReviewRules(filter)
    .then(res => {
      loading.value = false
      if (res.succ) {
        rulesListDataLocal.value = res.content.map(r => {
          return {
            "label": r.name.replace(/^\s+|\s+$|[.；;!！?？:：,，、\[\]【】\{\}""']+$/gu, ``), // 去掉前后空格和结尾标点符号
            "ruleTypeStr": r.typeStr
          }
        })
      }
    })
    .catch(() => {
      loading.value = false
    })
  }
  initData()
  const resetFn = ()=>{
    filter.ruleTypeEnum = ""
    filter.ruleName = ""
    searchFn()
  }
  const searchFn = ()=>{
    initData()
  }

  
  const router = useRouter()
  const goBack = () => {
    router.go(-1)
  }
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="ruleList" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
      <localTitle title="规则列表" >
        <template #rightBtn>
          <el-button class="align-right" @click="goBack">返回</el-button>
        </template>
      </localTitle>
      
      <div class="tableBox">
        <div class="searchBox">
          <el-form :model="filter" label-width="auto" inline>
            <el-form-item label="规则内容">
              <el-input v-model="filter.ruleName" placeholder="请输入规则内容" clearable style="width: 200px;"/> 
            </el-form-item>
            <el-form-item label="规则类型">
              <el-select v-model="filter.ruleTypeEnum" placeholder="请选择" clearable style="width: 200px;">
                <el-option
                  v-for="item in Object.keys(ruleTypeMapping)"
                  :key="item"
                  :label="ruleTypeMapping[item]"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchFn">查询</el-button>
              <el-button class="align-right" @click="resetFn">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <el-table ref="rulesListRef" border class="upload-demo12" tooltip-effect="light" :data="rulesListDataLocal">
          <el-table-column type="index" label="序号" align="center" width="100" />
          <el-table-column prop="ruleTypeStr" label="规则类型" align="center" min-width="100px" />
          <el-table-column prop="label" label="规则内容" align="left" min-width="400px" show-overflow-tooltip />
        </el-table>
      </div>
    </div>
  </template>
  
  <style lang="scss" scoped>
  .ruleList {
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
    padding-left: 16px;
    padding-right: 0;
    /* 统一右侧间距变量（用于两个按钮到竖线的距离） */
    --right-gap: 12px;

    :deep(.title) {
      width: 100%;
      box-sizing: border-box;
      padding-left: 0;
      padding-right: 0 !important;
    }

    :deep(.title .rightBtn) {
      width: auto !important;
      padding-right: 0 !important;
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }

    .searchBox{
      /* follow ruleList copy.vue: right-aligned form */
      padding-right: 0;
      height: 100px;
      display: flex;
      justify-content: flex-end; /* ensure children are anchored right */
      align-items: center;
      background-color: #fff;

      /* 将内部 form 变为横向 flex，并用 margin-left:auto 将表单推到右侧 */
      :deep(.el-form){
        display: flex;
        align-items: center;
        gap: 8px;
        margin-left: auto;
      }

      /* 消除表单项默认右侧外边距 */
      :deep(.el-form-item){
        margin-right: 0;
        margin-bottom: 0;
        .el-form-item__label { padding-right: 10px; }
        .el-input,
        .el-input__inner { height: 32px; line-height: 32px; }
      }

      :deep(.el-select){
        .el-select__wrapper{
          height: 36px;
          line-height: 36px;
        }
      }
    }

    /* 给 header 的返回按钮和搜索区的重置按钮统一右侧间距 */
    /* 优先使用明确类确保样式生效 */
    :global(.align-right) {
      margin-right: var(--right-gap) !important;
    }

    .tableBox {
      flex: 1;
      padding-bottom: 20px;
      :deep(.el-table) {
        background-color: transparent;
        //height: calc(100vh - 40px);
        .opt {
          .btn {
            height: 28px;
            line-height: 28px;
            padding: 0 4px;
            min-width: 70px;
            text-align: center;

            .el-loading-mask {
              .el-loading-spinner {
                height: 28px;
                margin: 0;
                top: 0;

                .circular {
                  width: 30px;
                  height: 30px;
                }
              }
            }
          }
        }
      }
    }

    /* 已移除临时辅助线，保留布局和间距变量 */
  }
  </style>
  