<script setup lang="ts">
  import zscs from "@/assets/images/circuitReview/zscs.png"
  import hs from "@/assets/images/circuitReview/zonghaoshishichang.png"
  import ztg from "@/assets/images/circuitReview/ztg.png"
  import bhzt from "@/assets/images/circuitReview/bhzt.png"
  import imgWtgs from "@/assets/images/codeReview/questionnumber.png"
  import imgDmhs from "@/assets/images/codeReview/linenumber.png"
  import imgGzgs from "@/assets/images/codeReview/rulenumber.png"
  import localTitle from "../components/localTitle.vue"
  import CodeDisplayDialog from '@/views/codeReview/components/CodeDisplay/CodeDisplayDialog.vue';
    import {
    logicReviewRecheckDetail,
    logicReviewRecheckResultSubmit
  } from "@/ajax/logicreview"
  import * as sourceApi from "@/ajax/logicreview"
  // 对话框状态
  const dialogVisible = ref(false);
  const dialogTitle = ref('代码显示对话框测试');
  const dialogWidth = ref('1200px');
  const footerText = ref(`软件检测实验室在建立软件测试体系或申请cnas/cma相关资质时，需要依据相关标准，使用有效的方法开展检验检测活动，GJB-8114是一部嵌入式软件安全测试相关的国家标准，本系列文章我们就针对GJB-8114《C/C++语言编程安全子集》的具体内容进行分析解读。`)
  
  // 代码显示属性
  const language = ref('c');
  const showLineNumbers = ref(true);
  const highlightedLinesInput = ref('1,3,5-8,10');
  const highlightedLines = ref<number[]>([1, 3, 5, 6, 7, 8, 10]);
  
  // 当前要显示的代码内容
  const currentCode = ref('')
  
  // 解析高亮行号输入
  const updateHighlightedLines = () => {
    const lines: number[] = [];
    const parts = highlightedLinesInput.value.split(',');
  
    for (const part of parts) {
      const trimmed = part.trim();
      if (!trimmed) continue;
  
      if (trimmed.includes('-')) {
        const [startStr, endStr] = trimmed.split('-').map(s => s.trim());
        const start = parseInt(startStr, 10);
        const end = parseInt(endStr, 10);
  
        if (!isNaN(start) && !isNaN(end) && start <= end) {
          for (let i = start; i <= end; i++) {
            lines.push(i);
          }
        }
      } else {
        const lineNum = parseInt(trimmed, 10);
        if (!isNaN(lineNum)) {
          lines.push(lineNum);
        }
      }
    }
  
    highlightedLines.value = lines;
  };
  
  // 计算属性：代码显示属性
  const codeDisplayProps = computed(() => ({
    code: currentCode.value,
    highlightedLines: highlightedLines.value,
    showLineNumbers: showLineNumbers.value,
    language: language.value,
    theme: 'light' as const,
    maxHeight: '400px'
  }));
  
  // 打开代码显示对话框（用于点击行号）
  const openCodeDialog = async (row: any) => {
    const param = {
      resultId:  rowItem.value.reviewResultId, // versionFilters.record, //
      fileName: row.fileName, // offset version
      offset: row.lineNumber,
      version: fileVersionName.value
    }
  
    // 先清空并显示 loading 状态（如果需要可替换为更优 UI）
    currentCode.value = ''
    try {
      const res: any = await sourceApi.getSourceCodeReviewCode(param)
      // 后端可能返回不同字段名，优先使用 content.sourceCode ，再兼容 sourceCode
      if (res && (res.succ || res.content || res.sourceCode)) {
        currentCode.value = (res.content && res.content.sourceCode) // || res.sourceCode || ''
      } else {
        currentCode.value = row && row.errorCode ? String(row.errorCode) : ''
      }
    } catch (e) {
      console.error('获取代码失败', e)
      currentCode.value = row && row.errorCode ? String(row.errorCode) : ''
    }
  
    // 设置高亮行为：尝试使用行号
    highlightedLinesInput.value = row && row.lineNumber ? String(row.lineNumber) : ''
    updateHighlightedLines()
    console.log('highlightedLines.value --> ', highlightedLines.value)
    // errorReason
    footerText.value = (row && row.modifySuggest ? String(row.modifySuggest) : '')
    // footerText.value = ' 机理说明：'+(row && row.explain ? String(row.explain) : '')
    // footerText.value += '\n 修改建议：'+ (row && row.reviewSuggestion ? String(row.reviewSuggestion) : '')
    // const ln = row && row.lineNumber ? row.lineNumber : NaN  
    // highlightedLines.value = !isNaN(ln) ? [ln] : []
    dialogTitle.value = (row && row.fileName ? `${row.fileName} ` : '') + (row && row.lineNumber ? `- 行 ${row.lineNumber}` : '')
    dialogVisible.value = true
  }
  
  // CodeDisplayDialog 事件处理占位
  const handleDialogOk = () => {
    dialogVisible.value = false
  }
  const handleDialogCancel = () => {
    dialogVisible.value = false
  }
  const handleLineClick = (line: number) => {
    // 如果需要基于行号做其他处理，可以在这里扩展
    // highlightedLines.value = [line]
  }
  const handleCodeCopy = () => {
    ElMessage.success('已复制')
  }

  const loading = ref(false)
  const route = useRoute()
  const { row }: any = route.query
  const rowItem = ref<any>({})
  // const distinctValues = ref<any>({})
  // 定义表格列映射 - 按照显示顺序定义
  // 规则类型、审查规则、
  // 机理说明、复核结论、错误原因、审查意见、代码文件、代码行号、错误代码
  // const columnOrder = [`ruleName`, `ruleType`,`isPassed`, `sourceFileName`, `lineNumber`, `reviewSuggestion`]
  // const columnMapping = {
  //   "ruleType": `规则类型`,
  //   "ruleName": `审查规则`,
  //   "sourceFileName": `代码文件`,
  //   "lineNumber": `行号`,
  //   // "explain": `机理说明`,
  //   // "errorReason": `错误原因`,
  //   "reviewSuggestion": `审查意见`,
  //   // "errorCode": `错误代码`,
  //   "isPassed": `复核结论`,
  // }
  const columnMapping = {
    "ruleSource": `规则类型`,
    "rule": `审查规则`,
    "fileName": `代码文件`,
    "lineNumber": `行号`,
    "modifySuggest": `审查意见`,

    "recheckConclusion": `复核结论`, 
    "recheckResultStatus": `复核结果`, 
    "questionDesc": `问题描述`,
  }
  // 是否通过选项配置
  const isPassedOptions = [
    { "label": `全部`, "value": ``, "display": `全部` },
    { "label": `通过`, "value": 1, "display": `通过` },
    { "label": `未通过`, "value": 0, "display": `未通过` }
  ]
  // 计算过滤后的数据
  const filteredData = ref<any>([])
  const filteredDataAll = ref<any>([])
  const pages = ref({
    "pageSize": 10,
    "pageNumber": 1,
    "total": 1
  })

  const getPageDataList = ()=>{
    let filteredList = JSON.parse(JSON.stringify(filteredDataAll.value))
    
    // 规则类型筛选
    if (filterRuleSource.value) {
      filteredList = filteredList.filter((item: any) => item.ruleSource === filterRuleSource.value)
    }
    
    // 审查规则筛选
    if (filterRule.value) {
      filteredList = filteredList.filter((item: any) => item.rule === filterRule.value)
    }

    // 更新总数，确保分页统计与过滤后数据一致
    pages.value.total = filteredList.length
    
    // 分页处理
    const { pageNumber, pageSize } = pages.value
    const startIndex = (pageNumber - 1) * pageSize
    const endIndex = startIndex + pageSize
    filteredData.value = filteredList.slice(startIndex, endIndex)
  }
  const recheckTime = ref('')
  
  // 筛选条件
  const filterRuleSource = ref('')  // 规则类型筛选
  const filterRule = ref('')        // 审查规则筛选
  
  // 筛选选项列表
  const ruleSourceOptions = ref<string[]>([])
  const ruleOptions = ref<string[]>([])
  
  // 生成筛选选项
  const generateFilterOptions = () => {
    if (!filteredDataAll.value || filteredDataAll.value.length === 0) {
      ruleSourceOptions.value = []
      ruleOptions.value = []
      return
    }
    // 规则类型选项
    const sourceSet = new Set<string>()
    // 审查规则选项
    const ruleSet = new Set<string>()
    
    filteredDataAll.value.forEach((item: any) => {
      if (item.ruleSource) {
        sourceSet.add(item.ruleSource)
      }
      if (item.rule) {
        ruleSet.add(item.rule)
      }
    })
    
    ruleSourceOptions.value = Array.from(sourceSet).sort()
    ruleOptions.value = Array.from(ruleSet).sort()
  }
  
  // 重置筛选
  const resetFilters = () => {
    filterRuleSource.value = ''
    filterRule.value = ''
    pages.value.pageNumber = 1
    getPageDataList()
  }
  
  // 获取审查结果详情数据
  const fetchReviewResultDetails = async (page = 1, pageSize = 10, noLoading?) => {
    if (!noLoading) {
      loading.value = true
    }
    try {
      const res = await logicReviewRecheckDetail({"fileVersionId": rowItem.value.fileVersionId})
      if (res.succ) {
        const resultData = res.content
        filteredDataAll.value = resultData.detailVOList??[]
        recheckTime.value = resultData.recheckTime || ''
        pages.value.total = resultData.detailVOList.length
        fileVersionName.value = resultData.version ||rowItem.value.version|| ''


        if(resultData&&resultData.detailVOList&&resultData.detailVOList.length){
            // 生成筛选选项
            generateFilterOptions()
            getPageDataList()
            // 记录初始化过滤选项
            // if (distinctValues.value && Object.keys(distinctValues.value).length === 0&& filteredDataAll.value.length > 0) {
            //   // 为每个字段生成过滤选项
            //   columnOrder.forEach(key => {
            //     if (key === `isPassed`) {
            //       distinctValues.value[key] = isPassedOptions.map(option => option.display)
            //     } else if (key === `ruleType`) {
            //       const unique = filteredDataAll.value
            //       .map(item => getRuleTypeDisplay(item))
            //       .filter((v, i, arr) => arr.indexOf(v) === i && v !== undefined && v !== null && v !== ``)
            //       distinctValues.value[key] = ['全部', ...unique]
            //     }else if(key === `reviewSuggestion`||key === `lineNumber`){
            //       distinctValues.value[key] = ``
            //     }else{
            //       distinctValues.value[key] = []
            //       {
            //         const unique = filteredDataAll.value
            //         .map(item => item[key])
            //         .filter((v, i, arr) => arr.indexOf(v) === i && v !== undefined && v !== null && v !== '')
            //         distinctValues.value[key] = ['全部', ...unique]
            //       }
            //     }
            //   })
            // }
        }
  
       
      }
    } catch (error) {
      console.error(`获取审查结果详情失败:`, error)
      loading.value = false
    } finally {
      loading.value = false
    }
  }
  
  
  const pagesChange = () => {
    getPageDataList()
  }
  const status = ref(``)
  const initPage = async (noLoading?) => {
    pages.value.pageNumber = 1
    await fetchReviewResultDetails(pages.value.pageNumber, pages.value.pageSize, noLoading)
  }
  
  const getColumnWidth = (index: number, columnName: string) => {
    if([0,3,5].includes(index)){
      return `90px`
    }else if([2].includes(index)){
      return `120`
    }else if([4].includes(index)){
      return `360`
    }else if([1].includes(index)){
      return `130px`
    } else {
      return `auto`
    } 
  }
  const router = useRouter()
  const goBack = () => {
    router.go(-1)
  }

  const recordChangeFn = async () => {
    // 保持当前页码，只刷新数据
    await fetchReviewResultDetails(pages.value.pageNumber, pages.value.pageSize)
  }
  
  const initDataFn = () => {
    if (rowItem.value.fileVersionId) {
      recordChangeFn()
    }
  }

  const fileName = ref(``)
  const fileVersionName = ref(``)
  if (row) {
    rowItem.value = JSON.parse(decodeURIComponent(row))
     console.log('rowItem.value', rowItem.value)
    fileName.value = rowItem.value.fileName || ''
    initDataFn()
  }
  
  const getfileName = (filePath)=>{
    if(!filePath){
      return ``
    }
    if(filePath.indexOf(`/`) === -1){
      return filePath
    }
    return filePath.split(`/`).pop()
  }
  
  const formatReviewText = (text: string) => {
    if (!text) return ``
    return String(text).replace(/\n/g, `<br />`)
  }
  
  const getRuleTypeDisplay = (row: any) => {
    if (!row) return ``
    const source = row.ruleSource !== undefined && row.ruleSource !== null ? String(row.ruleSource) : ``
    const type = row.ruleType !== undefined && row.ruleType !== null ? String(row.ruleType) : ``
    if (!source && !type) return ``
    if (!source) return type
    if (!type) return source
    return `${source}-${type}`
  }

  const gotoReviewResult = ()=>{
    router.push({
      "path": `/logicReviewResults`,
      "query": {
        "item": encodeURIComponent(JSON.stringify({resultId: rowItem.value.fileVersionId,version:rowItem.value.version??0})),
        path:`/reviewDetail`
      }
    })
  }

  const handleApprove = (list,type) => {
    if (type === 'APPROVE') {
      ElMessageBox.confirm(`确认通过复核吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const sentData = {
          "fileVersionId": rowItem.value.fileVersionId,
          "questionIds": list.map(r=>r.questionId),
          "version":fileVersionName.value,
          "rejectReason": "",
          "recheckResultStatus": 1
        }
        console.log('sentData', sentData)
        logicReviewRecheckResultSubmit(sentData).then(res=>{
          if (res.succ) {
            ElMessage.success('提交通过复核成功')
            recordChangeFn()
          }
        })
      }).catch(() => {})
    } else {
      ElMessageBox({
        title: '拒绝复核',
        message: h('div', { style: { position: 'relative' } }, [
          h('span', null, '请输入拒绝理由'),
          h('textarea', {
            id: 'rejectReason',
            style: {
              width: '100%',
              height: '80px',
              marginTop: '10px',
              padding: '8px',
              border: '1px solid #dcdfe6',
              borderRadius: '4px',
              resize: 'none'
            },
            placeholder: '请输入拒绝理由，最多500字',
            maxlength: 500,
            onInput: (e: any) => {
              const counter = document.getElementById('rejectReasonCounter')
              if (counter) {
                counter.textContent = `${e.target.value.length}/500`
              }
            }
          }),
          h('span', {
            id: 'rejectReasonCounter',
            style: {
              position: 'absolute',
              bottom: '-20px',
              right: '0',
              fontSize: '12px',
              color: '#909399'
            }
          }, '0/500')
        ]),
        confirmButtonText: '确定',
        cancelButtonText: '关闭',
        type: 'warning',
        showClose: true,
        closeOnClickModal: true,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            const textarea = document.getElementById('rejectReason') as HTMLTextAreaElement
            const value = textarea.value
            if (!value || value.trim().length === 0) {
              ElMessage.warning('拒绝理由不能为空')
              return
            }
            if (value.length > 500) {
              ElMessage.warning('拒绝理由不能超过500字')
              return
            }
            const sentData = {
              "fileVersionId": rowItem.value.fileVersionId,
              "questionIds": list.map(r=>r.questionId),
              "version": fileVersionName.value,
              "rejectReason": value,
              "recheckResultStatus": 2
            }
            logicReviewRecheckResultSubmit(sentData).then(res=>{
              if (res.succ) {
                ElMessage.success('提交拒绝复核成功')
                 recordChangeFn()
                done()
              }
            })
          } else {
            done()
          }
        }
      }).then(() => {}).catch(() => {})
    }
  }
  const selectable = (row) => !(row.recheckResultStatus==1||row.recheckResultStatus===2)
  const multipleSelection = ref([])
  const handleSelectionChange = (val) => {
    multipleSelection.value = val
  }
  const handleApproveBrash = (type) => {
    if (multipleSelection.value.length === 0) {
      ElMessage.warning('请选择要操作的复核项')
      return
    }
    handleApprove(multipleSelection.value,type)
   }
 
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="reviewResults" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
      <localTitle title="问题复核详情">
        <template #rightBtn>
          <el-button @click="goBack">返回</el-button>
        </template>
      </localTitle>
      
      <div class="fileName">
        代码文件名：<span>{{ fileName }}</span>
      </div>
  
      <div class="versionSearchBox">
        <div class="left">
          <div class="label">文件版本:</div>
          {{ fileVersionName }}
        </div>
        <div class="right">
          <div class="label">复核提交时间:</div>
          <span>{{ recheckTime }}</span>
          <!-- <el-button v-if="filteredDataAll&&filteredDataAll.length" type="primary" size="small" class="searchBtn" @click="gotoReviewResult">审查结果</el-button> -->
        </div>
      </div>
  
        <!-- const columnMapping = {
          "ruleSource": `规则类型`,
          "rule": `审查规则`,
          "fileName": `代码文件`,
          "lineNumber": `行号`,
          "modifySuggest": `审查意见`,

          "recheckConclusion": `复核结论`,
        } -->
      <div class="tableBox">
        <div class="search">
          <el-form ref="filterFormRef" :inline="true">
            <el-form-item label="规则类型">
              <el-select
                v-model="filterRuleSource"
                placeholder="请选择规则类型"
                clearable
                style="width: 240px"
                @change="pages.pageNumber = 1; getPageDataList()"
              >
                <el-option
                  v-for="item in ruleSourceOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="审查规则">
              <el-select
                v-model="filterRule"
                placeholder="请选择审查规则"
                clearable
                style="width: 240px"
                @change="pages.pageNumber = 1; getPageDataList()"
              >
                <el-option
                  v-for="item in ruleOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-form>
          <div>
            <el-button type="primary" size="small" class="searchBtn" @click="handleApproveBrash('APPROVE')">通过</el-button>
            <el-button type="danger" size="small" class="searchBtn" @click="handleApproveBrash('REJECT')">拒绝</el-button>
          </div>
        </div>
        <el-table :data="filteredData" border tooltip-effect="light" @selection-change="handleSelectionChange">
           <el-table-column type="selection" width="55" :selectable="selectable"/>
          <el-table-column label="序号" align="center" width="64px" class-name="col-index">
            <template #default="scope">
              {{ (pages.pageNumber - 1) * pages.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column
            v-for="(column, index) in Object.keys(columnMapping)"
            :key="index"
            :prop="column"
            :label="columnMapping[column]"
            align="left"
            :min-width="getColumnWidth(index, columnMapping[column])"
            :class-name="`col-${column}`"
            show-overflow-tooltip
          >
            <template #default="scope">
              <template v-if="columnMapping[column] === '审查意见'">
                <div class="contRow2" v-html="formatReviewText(scope.row[column])" />
              </template>
              <template v-else-if="columnMapping[column] === '复核结论'">
                <div class="isPass contRow2" style="text-align: center; justify-content: center">
                  {{ scope.row[column] }}
                </div>
              </template>
              <!-- <template v-else-if="columnMapping[column] === '错误原因'||columnMapping[column] === '机理说明'||columnMapping[column] === '审查规则'">
                <div class="contRow2">{{ scope.row[column] }}</div>
              </template> -->
              <template v-else-if="columnMapping[column] === '行号'">
                <div class="contRow2" align="center">
                  <a href="javascript:void(0)" @click.prevent="openCodeDialog(scope.row)">
                    {{ scope.row[column] }}
                  </a>
                </div>
              </template>
              <!-- <template v-else-if="columnMapping[column] === '错误代码'">
                <div class="contRow2" v-html="scope.row[column]? scope.row[column].replace(/\n/g, '<br>') : `` " />
              </template> -->
              <template v-else-if="columnMapping[column] === '代码文件'">
                <div class="contRow2">{{ getfileName(scope.row[column]) }}</div>
              </template>
              <!-- <template v-else-if="columnMapping[column] === '复核状态'">
                  <div v-if="scope.row[column] === 1" class="contRow2"> 复核通过 </div>
                  <div v-if="scope.row[column] === 2" class="contRow2"> 复核拒绝 </div>
              </template> -->
               <template v-else-if="columnMapping[column] === '复核结果'">
                  <div v-if="scope.row[column] === 1" class="contRow2" style="text-align: center; justify-content: center"> 复核通过 </div>
                  <div v-else-if="scope.row[column] === 2" class="contRow2" style="text-align: center; justify-content: center"> 复核拒绝 </div>
                  <div v-else class="contRow2" style="text-align: center; justify-content: center"> - </div>
                  <!-- <div v-if="scope.row[column]" class="contRow2">{{ scope.row[column] }}</div>
                  <div v-else> - </div> -->
                </template>
              <template v-else>
                <div class="contRow2">{{ scope.row[column] }}</div>
              </template>
            </template>
          </el-table-column>
          <el-table-column
              label="操作"
              width="140px"
              align="center"
            >
              <template #default="scope">
               <div style="display: flex;justify-content: center;width: 100%;"> 
                 <el-button
                  :disabled="scope.row.recheckResultStatus==1||scope.row.recheckResultStatus==2"
                  type="primary"
                  size="small"                 
                  style="padding: 4px 10px !important;margin: 0 5px;"
                  @click="handleApprove([scope.row],`APPROVE`)"
                >
                  通过
                </el-button>
                 <el-button
                  :disabled="scope.row.recheckResultStatus==1||scope.row.recheckResultStatus==2"
                  type="danger"
                  size="small"                 
                  style="padding: 4px 10px !important;margin: 0;"
                    @click="handleApprove([scope.row],`REJECT`)"
                >
                  拒绝
                </el-button>
               </div>
              </template>
            </el-table-column>
        </el-table>
        <div class="pagesBox">
          <el-pagination
            v-model:current-page="pages.pageNumber"
            v-model:page-size="pages.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50, 100]"
            :total="pages.total"
            @current-change="pagesChange"
            @size-change="pagesChange"
          />
        </div>
      </div>
      <!-- CodeDisplayDialog 组件 -->
      <CodeDisplayDialog
        v-model="dialogVisible"
        :title="dialogTitle"
        :width="dialogWidth"
        :code-display-props="codeDisplayProps"
        :footer-text="footerText"
        @ok="handleDialogOk"
        @cancel="handleDialogCancel"
        @line-click="handleLineClick"
        @copy="handleCodeCopy"
      />
    </div>
  </template>
  
  <style lang="scss" scoped>
  .reviewResults {
    position: relative;
    display: flex;
    flex-direction: column;
     .fileName {
      height: 56px;
      border-radius: 20px 20px 0px 0px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      color: #3a4c5f;
      .el-button{
        margin: 0 10px;

      }
      .el-text{
        color: #0462e3;
        line-height: 21px;
        margin-left: 20px;
      }
      .wbh {
        padding: 0;
        text-align: center;
        line-height: 28px;
        width: 88px;
        height: 28px;
        background: rgba(227, 158, 37, 0.2);
        border-radius: 6px;
        color: #e39e25;
      }
      .ybh {
        padding: 0;
        text-align: center;
        line-height: 28px;
        width: 88px;
        height: 28px;
        background: rgba(39, 202, 169, 0.12);
        border-radius: 6px;
        color: #22c9a8;
      }
    }
  
    .versionSearchBox {
      background: #dce8f5;
      box-shadow: 0px 6px 20px 0px rgba(3, 13, 28, 0.02);
      border-radius: 10px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      .right{
        width: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        span{
          margin: 0 10px;
        }
        .label{
          font-size: 16px;
          color: #3a4c5f;
          line-height: 56px;
          vertical-align: middle;
        }
        :deep(.el-select) {
          width: 50%;
          margin-right: 16px;
          .el-select__wrapper {
            height: 36px;
            line-height: 36px;
          }
        }
        
      }
      > .left {
        width: 50%;
        border-left: 1px solid #b1bfcf;
        display: flex;
        align-items: center;
        justify-content: center;
        .reviewTime {
          height: 56px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: #3a4c5f;
          line-height: 56px;
          > span {
            color: #0462e3;
            margin-right: 14px;
            line-height: normal;
            vertical-align: middle;
          }
        }
        .label {
          color: #555555;
          line-height: 17px;
          margin-right: 16px;
        }
        :deep(.el-select) {
          width: 370px;
          margin-right: 16px;
          .el-select__wrapper {
            height: 36px;
            line-height: 36px;
          }
        }
        .el-button {
          height: 28px;
          line-height: 28px;
          padding: 0 6px;
          font-size: 12px;
        }
      }
    }
  
    .num {
      width: 100%;
      margin: 14px 0;
      display: flex;
      > div {
        flex: 1;
        flex-shrink: 0;
        margin-right: 30px;
        &:last-child {
          margin: 0;
        }
      }
        .itemBox {
        height: 100px;
        background: linear-gradient(123deg, #eaf3f5, #ddeff9);
        box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
        border-radius: 20px;
        padding: 16px 24px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .titleRow {
          display: flex;
          justify-content: center;
          .label {
            font-size: 15px;
            color: #3a4c5f;
            line-height: 20px;
            display: block;
            text-align: center;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
        .contentRow {
          display: flex;
          align-items: center;
          justify-content: space-between;
          .left {
            display: flex;
            align-items: center;
            > img {
              width: 42px;
              height: auto;
            }
          }
          .right {
            display: flex;
            align-items: center;
            justify-content: flex-end;
          }
        }
        .value {
          display: flex;
          flex-direction: row;
          align-items: baseline;
          justify-content: flex-end;
          gap: 8px;
          flex-wrap: nowrap;
          .number {
            font-size: 28px !important;
            color: #6299f5;
            line-height: 36px;
            white-space: nowrap;
          }
          .unit {
            font-size: 18px;
            color: #3a4c5f;
            margin-right: 8px;
            white-space: nowrap;
          }
          &.wbh {
            .number {
              color: #e39e25 !important;
              font-size: 18px !important;
              font-weight: 600;
            }
          }
          &.ybh {
            .number {
              color: #22c9a8 !important;
              font-size: 18px !important;
              font-weight: 600;
            }
          }
        }
        &.itemBox1 {
          background: linear-gradient(123deg, #b5e2f0, #c6e5f6);
          box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value { .number { color: #6299f5; } }
        }
        &.itemBox2 {
          background: linear-gradient(123deg, #c7d6f5, #cde4fb);
          box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value { .number { color: #d9a732; } }
        }
         &.itemBox3 {
          background: linear-gradient(123deg, #eeddbc, #eee2c2);
        box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value { .number { color: #d9a732; } }
        }
        &.itemBox4 {
          background: linear-gradient(123deg, #c1d9f4, #c7e0f9);
          box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value { .number { color: #41aaff; font-size: 18px; } }
        }
      }
    }
    .tableBox {
      flex: 1;
      .search {
        height: 60px;
        padding: 0 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #fff;
        border-radius: 10px 10px 0 0;
        :deep(.el-form) {
          .el-form-item {
            margin-bottom: 0;
            margin-right: 20px;
            .el-form-item__label {
              font-size: 15px;
              padding-right: 10px;
            }
            .el-input,
            .el-input__inner,
            .el-select,
            .el-select__wrapper {
              height: 32px;
              line-height: 32px;
            }
          }
        }
      }
      :deep(.el-table) {
        background-color: transparent;
         height: calc(100% - 100px);
         max-height: 900px;
        .el-popper{
          max-width: 500px;
        }
        .el-table__body tr,
        .el-table__body td,
        .el-table__body td .cell {
          height: auto;
          min-height: 80px;
        }
        .el-table__body td .cell {
          font-size: 15px;
          line-height: 20px;
          align-items: center;
          padding: 6px 0;
        }
        a {
          color: #409eff;
          text-decoration: none;
  
          &:hover {
            color: #66b1ff;
            text-decoration: underline;
          }
        }
        .icon-butongguo1 {
          color: #ed5a3a;
          font-size: 18px;
          margin-right: 9px;
        }
        .icon-chenggong {
          color: #3bbe44;
          font-size: 18px;
          margin-right: 9px;
        }
        /* 确保单元格内容在垂直方向居中，并让文本块占满高度 */
        .cell{
          display: flex;
          align-items: center;
          justify-content: flex-start;
        }
        .contRow2{
          flex: 1;
          display: -webkit-box;
          height: auto;
          box-sizing: border-box;
          line-height: 20px;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: normal;
          overflow-wrap: break-word;
          word-break: break-all;
          padding-left: 12px;
          padding-right: 12px;
        }
        .auditText {
          -webkit-line-clamp: 3;
          white-space: normal;
        }
        .col-index .cell,
        .col-ruleType .cell,
        .col-lineNumber .cell,
        .col-isPassed .cell,
        .col-action .cell ,.col-recheckConclusion .cell,.col-recheckResultStatus .cell{
          align-items: center !important;
          justify-content: center;
          padding: 0 !important;
          min-height: 60px;
        }
      }
      .pagesBox {
        height: 50px;
        padding: 0 20px;
        .el-pagination {
          margin: 0;
          padding: 7px 10px;
        }
      }
    }
  
    :deep(.dialogCont) {
      padding: 0;
      border: 0;
      background: transparent;
      margin-top: 35vh;
  
      .el-dialog__header {
        padding: 0;
        height: 54px;
  
        .my-header {
          padding: 0 20px;
          height: 54px;
          line-height: 54px;
          background: #e0ecfa;
          display: flex;
          align-items: center;
          justify-content: space-between;
          background-size: contain;
          font-size: 16px;
          font-weight: 500;
          .status {
            color: #2487ff;
          }
        }
      }
  
      .el-dialog__body {
        padding: 0;
        // height: 184px;  162
        height: 346px;
  
        .contBox {
          height: 100%;
          background-color: #fff;
          position: relative;
          padding: 10px 20px 0;
          .tableCont {
            height: 184px;
            .el-table__inner-wrapper {
              background-color: #f5f5f5;
  
              .el-table__header-wrapper {
                background-color: transparent;
                display: flex;
                flex-direction: column;
                margin-bottom: 0px;
  
                .el-table__header {
                  tr {
                    height: 40px;
  
                    th {
                      font-size: 15px;
                      background: rgba(157, 175, 195, 0.3);
                      line-height: 40px;
                      color: #333;
                    }
                  }
                }
              }
  
              .el-table__body-wrapper {
                background-color: transparent;
  
                .el-table__body {
                  tbody {
                    background-color: #ffffff;
                  }
  
                  .el-table__row {
                    height: 40px;
                    background: #ffffff;
  
                    td {
                      line-height: 40px;
                      color: #333;
                    }
  
                    &:nth-child(2n + 1) {
                      background: rgba(157, 175, 195, 0.1);
                    }
  
                    &:last-child {
                      margin: 0;
  
                      td {
                        border-bottom: none;
                      }
                    }
                  }
                }
  
                .status {
                  .cell {
                    color: #bbbbbb;
                  }
  
                  .dot,
                  .dot > div {
                    position: relative;
                    box-sizing: border-box;
                  }
  
                  .dot {
                    display: flex;
                    align-items: center;
                    font-size: 0;
                    color: #bbbbbb;
                    position: absolute;
                    right: 4px;
                    top: 5px;
                  }
  
                  .dot.la-dark {
                    color: #bbbbbb;
                  }
  
                  .dot > div {
                    display: inline-block;
                    float: none;
                    background-color: currentColor;
                    border: 0 solid currentColor;
                  }
  
                  .dot {
                    width: 30px;
                    height: 18px;
                  }
  
                  .dot > div {
                    width: 4px;
                    height: 4px;
                    margin: 1px;
                    border-radius: 100%;
                    animation: ball-beat 0.7s -0.15s infinite linear;
                  }
  
                  .dot > div:nth-child(2n-1) {
                    animation-delay: -0.5s;
                  }
  
                  @keyframes ball-beat {
                    50% {
                      opacity: 0.2;
                      transform: scale(0.75);
                    }
  
                    100% {
                      opacity: 1;
                      transform: scale(1);
                    }
                  }
                }
  
                .tg {
                  color: #19bb5a;
                }
  
                .notg {
                  color: #de4848;
                }
  
                .bsy {
                  color: #d69d15;
                }
  
                .pszb {
                  position: relative;
                }
              }
            }
          }
          .numInfo {
            display: flex;
            justify-content: center;
            margin-top: 28px;
            > div {
              flex: 1;
              height: 100px;
              background: linear-gradient(123deg, #eaf3f5, #ddeff9);
              box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
              border-radius: 20px;
              padding: 24px;
              display: flex;
              align-items: center;
              margin-right: 12px;
              .left {
                width: 42px;
                > img {
                  width: 100%;
                  height: auto;
                }
              }
              .value {
                display: flex;
                flex-direction: column;
                padding-left: 20px;
                span {
                  font-size: 26px;
                  color: #45bfe2;
                  line-height: 36px;
                }
                .label {
                  font-size: 15px;
                  color: #3a4c5f;
                  line-height: 23px;
                }
              }
  
              &:nth-child(2) {
                background: linear-gradient(123deg, #edf2fc, #dfedfb);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #6299f5;
                }
              }
              &:nth-child(3) {
                background: linear-gradient(123deg, #f0f1fd, #dfe4fb);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #7e71ff;
                }
              }
              &:nth-child(4) {
                background: linear-gradient(123deg, #edf2fc, #cbf3e5);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #18c3ad;
                }
              }
              &:nth-child(5) {
                margin-right: 0;
                background: linear-gradient(123deg, #f3f1ed, #f1e6ce);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #d9a732;
                }
              }
            }
          }
        }
      }
  
      &.dialogCont1 {
        margin-top: 4vh;
  
        .el-dialog__body {
          padding: 0;
          height: 750px;
  
          .contBox {
            .tableCont {
              padding-left: 60px;
              padding-top: 10px;
            }
  
            .upload-demo {
              width: 400px;
            }
          }
        }
      }
  
      &.dialogCont2 {
        .el-dialog__body {
          padding: 0;
          height: 750px;
  
          .contBox {
            .tableCont {
              padding-left: 0;
              padding-top: 10px;
              .statusBox {
                .green,
                .red {
                  display: inline-block;
                  width: 10px;
                  height: 10px;
                  border-radius: 50%;
                  background-color: #18c3ad;
                }
                .red {
                  background-color: #f3a41c;
                }
              }
            }
          }
        }
      }
      &.dialogCont3 {
        margin-top: 30vh;
        .el-dialog__body {
          padding: 0;
          height: 200px;
          .el-form {
            padding-top: 20px;
            .upload-demo {
              width: 100%;
            }
          }
        }
      }
      &.dialogCont4 {
        margin-top: 20vh;
        .el-dialog__body {
          padding: 0;
          max-height: 600px;
          .contBox {
            height: 100%;
            background-color: #fff;
            position: relative;
            padding: 0 20px 0;
            display: flex;
            flex-direction: column;
            .title {
              font-weight: 400;
              font-size: 15px;
              color: #333333;
              line-height: 40px;
              position: relative;
              padding-left: 14px;
              > span {
                color: #3186e9;
              }
              &::before {
                content: " ";
                width: 6px;
                height: 14px;
                background: #3186e9;
                position: absolute;
                left: 0;
                top: 13px;
              }
            }
            .tableCont {
              flex: 1;
              height: 100%;
            }
          }
        }
      }
  
      .el-dialog__footer {
        box-shadow: 0px -1px 27px 0px rgba(42, 50, 57, 0.2);
        padding: 0 20px;
        background-color: #f6f6f6;
  
        .my-footer {
          height: 60px;
          display: flex;
          align-items: center;
          justify-content: center;
  
          > .right {
            flex: 1;
  
            .num {
              width: 100%;
              height: 54px;
              display: flex;
              align-items: center;
  
              > div {
                width: 180px;
                height: 32px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                background-color: #7d807d;
                background: url(../assets/images/btnbj.png) center no-repeat;
                margin-right: 10px;
                padding: 0 40px 0 30px;
                background-size: 100% 100%;
  
                > div {
                  display: flex;
                  align-items: center;
                  font-size: 13px;
                  color: #5491d5;
                  font-weight: 600;
  
                  &:last-child {
                    font-size: 15px;
                    color: #1985fd;
                  }
                }
  
                &:last-child {
                  margin: 0;
                }
              }
            }
          }
  
          .btn {
            .el-button {
              &.look {
                background: url(../assets/images/dan.png) center no-repeat;
                background-size: 100% 100%;
                font-size: 15px;
                color: #ffffff;
              }
  
              &.is-disabled {
                opacity: 0.5;
              }
            }
          }
        }
      }
    }
  }
  </style>
  <style lang="scss">
  .customClassElMessageBox {
    --el-messagebox-width: 580px;
    padding: 0;
    .el-message-box__header {
      height: 54px;
      line-height: 54px;
      background: #e0ecfa;
      .el-message-box__title {
        height: 54px;
        line-height: 54px;
        font-size: 16px;
        color: #333333;
        padding: 0 20px;
      }
      .el-message-box__headerbtn {
        top: 7px;
        right: 4px;
        .el-message-box__close {
          font-size: 20px;
        }
      }
    }
    .el-message-box__content {
      height: 231px;
      .el-message-box__container {
        display: flex;
        flex-direction: column;
        padding: 50px 0;
        .el-message-box__message {
          width: 285px;
          margin: 0 auto;
          font-size: 15px;
          color: #868a90;
          line-height: 20px;
          text-align: center;
        }
      }
      .el-message-box__status {
        font-size: 60px;
        color: #c3d2e4;
      }
    }
    .el-message-box__btns {
      width: 100%;
      height: 63px;
      background: #ffffff;
      box-shadow: 0px -1px 27px 0px rgba(42, 50, 57, 0.2);
      border-radius: 0px 0px 2px 2px;
      display: flex;
      align-items: center;
      justify-self: flex-end;
      padding: 0 20px;
      .el-button {
        width: 94px;
        height: 40px;
        background: linear-gradient(90deg, #e35f5f, #e57979);
        border-radius: 6px;
        line-height: 40px;
        text-align: center;
        padding: 0;
        &:hover {
          background: linear-gradient(90deg, #e57979, #e35f5f);
        }
      }
    }
  }
  .el-select-dropdown {
    .el-select-dropdown__item {
      max-width: 500px;
    }
  }
  
  .el-popper.is-customized {
    /* Set padding to ensure the height is 32px */
    padding: 6px 12px;
    background: rgba(255, 255, 255, 0.86);
  }
  
  .el-popper.is-customized .el-popper__arrow::before {
    background: rgba(255, 255, 255, 0.86);
    right: 0;
  }
  
  .reviewSuggestionBox {
    max-height: 400px;
    overflow-y: auto;
    white-space: pre-wrap;
    line-height: 20px;
    a {
      color: #409eff;
      text-decoration: none;
  
      &:hover {
        color: #66b1ff;
        text-decoration: underline;
      }
    }
  }
  </style>
  