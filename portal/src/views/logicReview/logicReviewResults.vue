<script setup lang="ts">
  import zscs from "@/assets/images/circuitReview/zscs.png"
  import hs from "@/assets/images/circuitReview/zonghaoshishichang.png"
  import ztg from "@/assets/images/circuitReview/ztg.png"
  import bhzt from "@/assets/images/circuitReview/bhzt.png"
  import imgWtgs from "@/assets/images/codeReview/questionnumber.png"
  import imgDmhs from "@/assets/images/codeReview/linenumber.png"
  import imgGzgs from "@/assets/images/codeReview/rulenumber.png"
  import localTitle from "./components/localTitle.vue"
  import table2excel from "js-table2excel"
  import * as sourceApi from "@/ajax/logicreview"
  import CodeDisplayDialog from '@/views/codeReview/components/CodeDisplay/CodeDisplayDialog.vue';
  
  
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
    console.log('代码审查id', versionFilters.record)
    const param = {
      resultId:  row.resultId, // versionFilters.record, //
      fileName: row.sourceFileName,  // offset version
      offset: row.lineNumber,
      version: versionFilters.version
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
    footerText.value = (row && row.reviewSuggestion ? String(row.reviewSuggestion) : '')
    // footerText.value = ' 机理说明：'+(row && row.explain ? String(row.explain) : '')
    // footerText.value += '\n 修改建议：'+ (row && row.reviewSuggestion ? String(row.reviewSuggestion) : '')
    // const ln = row && row.lineNumber ? row.lineNumber : NaN  
    // highlightedLines.value = !isNaN(ln) ? [ln] : []
    dialogTitle.value = (row && row.sourceFileName ? `${row.sourceFileName} ` : '') + (row && row.lineNumber ? `- 行 ${row.lineNumber}` : '')
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

  // 复核相关变量
  const auditModalVisible = ref(false)
  const currentRow = ref<any>(null)
  const selectedRows = ref<any[]>([]) // 批量选中的行
  const isBatchMode = ref(false) // 是否是批量模式
  const auditConclusion = ref('')
  const showFeedbackInput = ref(false)
  const feedbackText = ref('')
  const auditFormRef = ref()
  const isSubmitting = ref(false)

  // 问题描述placeholder - 根据复核结论动态提示
  const feedbackPlaceholderMap: Record<string, string> = {
    '规则不适用': '示例: 该模块为纯组合逻辑，审查规则要求时序电路使用非阻塞赋值，在此不适用。',
    '问题可例外': '示例: 该信号在测试台中仅作观察用，未参与功能逻辑，不影响设计正确性。',
    '复查无影响': '示例: 报告的未用信号已被综合工具自动优化，不影响最终网表功能。',
    '审查不正确': '示例: 该always块实际为锁存器结构而非触发器，审查工具误判了电路类型。',
  }
  const feedbackPlaceholder = computed(() => feedbackPlaceholderMap[auditConclusion.value] || '请输入问题描述')

  // 问题描述验证规则
  const feedbackRules = {
    auditConclusion: [
      { required: true, message: '请选择复核结论', trigger: 'blur' },
    ],
    feedbackText: [
      { required: true, message: '请输入问题描述', trigger: 'blur' },
      { max: 500, message: '反馈内容不能超过500个字符', trigger: 'blur' }
    ]
  }

  // 处理问题描述输入，手动限制长度
  const handleFeedbackInput = (value: string) => {
    if (value && value.length > 500) {
      feedbackText.value = value.slice(0, 500)
    }
  }

  // 复核结论选项
  const auditOptions = [
    { label: '规则不适用', value: '规则不适用' }, // RULE_NOT_APPLICABLE
    { label: '问题可例外', value: '问题可例外' }, // ISSUE_EXCEPTION
    { label: '复查无影响', value: '复查无影响' }, // REVIEW_NO_IMPACT
    { label: '审查不正确', value: '审查不正确' } // REVIEW_INCORRECT
  ]

  // 复审操作 - 单个
  const handleRecheck = (row: any) => {
    console.log('复审', row)
    currentRow.value = row
    isBatchMode.value = false
    selectedRows.value = []
    auditConclusion.value = ''
    showFeedbackInput.value = false
    feedbackText.value = ''
    auditModalVisible.value = true
  }

  // 处理表格选择变化
  const handleSelectionChange = (selection: any[]) => {
    selectedRows.value = selection
    console.log('选中行:', selection)
    console.log('选中行的questionIds:', selection.map(row => row.questionId))
  }

  // 批量申请复核
  const handleBatchRecheck = () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请至少选择一行记录')
      return
    }
    isBatchMode.value = true
    currentRow.value = null
    auditConclusion.value = ''
    showFeedbackInput.value = false
    feedbackText.value = ''
    auditModalVisible.value = true
  }

  // 提交复核结论
  const submitAudit = async () => {
    // 防止重复提交
    if (isSubmitting.value) {
      return
    }
    isSubmitting.value = true

    // 表单验证
    if (auditFormRef.value) {
      try {
        await auditFormRef.value.validate()
      } catch (error) {
        console.log('表单验证失败:', error)
        isSubmitting.value = false
        return
      }
    }

    // 验证问题描述
    // if (auditConclusion.value === 'REVIEW_INCORRECT' && !feedbackText.value?.trim()) {
    //   ElMessage.warning('当选择"审查不正确"时，必须填写问题描述')
    //   return
    // }

    // 验证问题描述长度
    if (feedbackText.value?.length > 500) {
      ElMessage.warning('问题描述不能超过500个字符')
      isSubmitting.value = false
      return
    }

    try {
      let param
      if (isBatchMode.value) {
        // 批量模式：使用选中的多行数据
        console.log('批量模式 - 选中的行:', selectedRows.value)
        const questionIds = selectedRows.value.map(row => row.questionId).filter(id => id)
        console.log('批量模式 - questionIds:', questionIds)
        param = {
          resultId: selectedRows.value[0]?.resultId,
          questionIds: questionIds,
          version: versionFilters.version,
          recheckConclusion: auditConclusion.value,
          questionDesc: feedbackText.value || ''
        }
      } else {
        // 单个模式
        console.log('单个模式 - currentRow:', currentRow.value)
        param = {
          resultId: currentRow.value.resultId,
          questionIds: currentRow.value.questionId ? [currentRow.value.questionId] : [],
          version: versionFilters.version,
          recheckConclusion: auditConclusion.value,
          questionDesc: feedbackText.value || ''
        }
      }
      console.log('提交复核:', param)
      const res: any = await sourceApi.logicReviewRecheck(param)
      if (res.succ) {
        // 关闭弹窗
        auditModalVisible.value = false
        // 清空选中
        selectedRows.value = []
        isBatchMode.value = false
        ElMessage.success('复核提交成功')
        // 刷新列表数据
        await filteredDataFn()
      } else {
        ElMessage.error(res.msg || '复核提交失败')
      }
    } catch (error) {
      console.error('提交复核失败:', error)
      ElMessage.error('复核提交失败')
    } finally {
      isSubmitting.value = false
    }
  }

  // 取消复核
  const cancelAudit = () => {
    auditModalVisible.value = false
    auditConclusion.value = ''
    feedbackText.value = ''
    currentRow.value = null
    selectedRows.value = []
    isBatchMode.value = false
  }

  const loading = ref(false)
  const route = useRoute()
  const { item ,path}: any = route.query
  const rowItem = ref<any>({})
  
  const list = ref([])
  const arr: any = reactive([])
  
  const filters = ref<{ [key: string]: string | undefined }>({})
  const distinctValues = ref<{ [key: string]: string[] }>({})
  const columns = ref<{ label: string; prop: string; name: string }[]>([])

  // 显示所有列按钮状态
  const showAllColumns = ref(false)

  // 判断当前页是否有复核相关数据（recheckResultStatus、rejectReason、recheckUserId任一存在）
  const hasRecheckData = computed(() => {
    return filteredData.value.some((row: any) => {
      return row.recheckResultStatus !== undefined && row.recheckResultStatus !== null && row.recheckResultStatus !== '' ||
             row.rejectReason !== undefined && row.rejectReason !== null && row.rejectReason !== '' ||
             row.recheckUserId !== undefined && row.recheckUserId !== null && row.recheckUserId !== ''
    })
  })

  // 判断是否有拒绝或审核中的数据（用于自动展开）
  const hasRejectedOrDoingRecheck = computed(() => {
    return filteredData.value.some((row: any) => {
      return row.recheckResultStatus === 2 || row.recheckStatus === '复核中'
    })
  })

  // 判断是否全部复核完成
  const isAllRecheckCompleted = computed(() => {
    if (!hasRecheckData.value) return false
    return filteredData.value.every((row: any) => {
      return row.recheckStatus === '复核完成' || !row.recheckStatus
    })
  })

  // 判断是否显示复核结论列（独立控制：全部完成或有拒绝时自动展开）
  const showRecheckConclusionColumn = computed(() => {
    if (isAllRecheckCompleted.value) return true
    if (hasRejectedOrDoingRecheck.value) return true
    return hasRecheckData.value && showAllColumns.value
  })

  // 判断是否显示问题描述列（跟随开关）
  const showQuestionDescColumn = computed(() => {
    return hasRecheckData.value && showAllColumns.value
  })
  
  // 定义表格列映射 - 按照显示顺序定义
  // 规则类型、审查规则、
  // 机理说明、审查结论、错误原因、审查意见、代码文件、代码行号、错误代码
  const columnOrder = [`ruleName`, `ruleType`,`isPassed`, `sourceFileName`, `lineNumber`, `reviewSuggestion`]
  const columnMapping = {
    "ruleType": `规则类型`,
    "ruleName": `审查规则`,
    "sourceFileName": `代码文件`,
    "lineNumber": `行号`,
    // "explain": `机理说明`,
    // "errorReason": `错误原因`,
    "reviewSuggestion": `审查意见`,
    // "errorCode": `错误代码`,
    "isPassed": `审查结论`,
  }
  const exportColumnMapping = {
    "ruleType": `规则类型`,
    "ruleName": `审查规则`,
    "sourceFileName": `代码文件`,
    "lineNumber": `行号`,
    // "explain": `机理说明`,
    // "errorReason": `错误原因`,
    "reviewSuggestion": `审查意见`,
    "errorCode": `错误代码`,
    "isPassed": `审查结论`,
    "recheckConclusion": `复核结论`,
    "questionDesc": `问题描述`,
    "recheckStatus": `复核状态`,
  }
  
  // 规则类型枚举映射
  const ruleTypeMapping = {
    "PROPOSITIONAL_RULE": `建议规则`,
    "NEEDFUL_RULE": `必要规则`,
    "COERCIVE_RULE": `强制规则`
  }
  
  // 是否通过选项配置
  const isPassedOptions = [
    { "label": `全部`, "value": ``, "display": `全部` },
    { "label": `通过`, "value": 1, "display": `通过` },
    { "label": `未通过`, "value": 0, "display": `未通过` }
  ]
  
  const getTableColumnName = (keyValue: string) => {
    return columnMapping[keyValue as keyof typeof columnMapping] || keyValue
  }
  
  // 计算过滤后的数据
  const filteredData = ref<any>([])
  const filteredDataAll = ref<any>([])
  const pages = ref({
    "pageSize": 10,
    "pageNumber": 1,
    "total": 1
  })
  
  const getParamsData = ()=>{
    const params: any = {}
      // 添加过滤条件
      if (filters.value.ruleName && filters.value.ruleName !== `全部`) {
        params.ruleName = filters.value.ruleName
      }
      if (filters.value.ruleType && filters.value.ruleType !== `全部`) {
        params.ruleType = filters.value.ruleType
      }
      if (filters.value.sourceFileName && filters.value.sourceFileName !== `全部`) {
        params.sourceFileName = filters.value.sourceFileName
      }
      if (filters.value.lineNumber && filters.value.lineNumber !== `全部`) {
        params.lineNumber = filters.value.lineNumber
      }
      if (filters.value.reviewSuggestion && filters.value.reviewSuggestion !== `全部`) {
        params.reviewSuggestion = filters.value.reviewSuggestion
      }
      if (filters.value.isPassed && filters.value.isPassed !== `全部`) {
        // 根据选择的显示文本找到对应的值
        const selectedOption = isPassedOptions.find(option => option.display === filters.value.isPassed)
        if (selectedOption && selectedOption.value !== ``) {
          params.isPassed = selectedOption.value
        }
      }
      return params
  }
  const getPageDataList = ()=>{
    const params: any = getParamsData()
    let filteredList = JSON.parse(JSON.stringify(filteredDataAll.value))
    if (Object.keys(params).length > 0) {
      filteredList = filteredDataAll.value.filter(item => {
        // 使用 every 方法确保多个条件是"并"的关系
        return Object.keys(params).every(key => {
          // 处理不同类型的字段比较
          if (key === 'isPassed') {
            // isPassed可能是数字类型，确保类型一致
            return Number(item[key]) == Number(params[key])
          } else if (key === 'ruleType') {
            const display = getRuleTypeDisplay(item)
            return String(display).includes(String(params[key]))
          } else {
            // 其他字段进行字符串匹配
            return String(item[key]).includes(String(params[key]))
          }
        })
      })
    }

    // 分页处理
    const { pageNumber, pageSize } = pages.value
    const startIndex = (pageNumber - 1) * pageSize
    const endIndex = startIndex + pageSize
    filteredData.value = filteredList.slice(startIndex, endIndex)
    // 更新总数，确保分页统计与过滤后数据一致
    pages.value.total = filteredList.length

    // 数据更新后，如果有拒绝或审核中的数据，自动打开显示所有列按钮
    // 直接使用当前页的 filteredData 判断，而不是依赖计算属性（避免响应式延迟）
    const hasRejectedOrDoing = filteredData.value.some((row: any) => {
      return row.recheckResultStatus === 2 || row.recheckStatus === 2
    })
    console.log('hasRecheckData', hasRecheckData.value)
    console.log('hasRejectedOrDoing', hasRejectedOrDoing)
    if (hasRejectedOrDoing) {
      showAllColumns.value = true
    }
  }
  // 获取审查结果详情数据
  const fetchReviewResultDetails = async (page = 1, pageSize = 10, noLoading?) => {
    if (!noLoading) {
      loading.value = true
    }
    try {
      const res = await sourceApi.getSourceCodeReviewResultVersion({"resultId": versionFilters.record,version:versionFilters.version})
      if (res.succ) {
        const resultData = res.content
        topInfoList[0].value = resultData.filesSize //审查文件数
        topInfoList[1].value = resultData.passFileNum //通过文件数
        topInfoList[2].value = resultData.duration //审查耗时
        // topInfoList[3].value = resultData.status === 2 ? "审查已通过" : "审查未通过" //审查状态
        // topInfoList[3].class = resultData.status === 2 ? "ybh" : "wbh" //审查状态类名
        if(resultData.reviewStatus===1){ // reviewStatus 1 通过 2 不通过
          topInfoList[3].value = "审查已通过"
          topInfoList[3].class = "ybh"
        }else{
          topInfoList[3].value = "审查未通过"
          topInfoList[3].class = "wbh"
        }
        topInfoList[4].value = resultData.useRuleSize //使用规则数
        topInfoList[5].value = resultData.questions //问题数
        topInfoList[6].value = resultData.filesLine //审查文件行数	
        filteredDataAll.value = resultData.detailVOList
  
        pages.value.total = resultData.detailVOList.length
  
        getPageDataList()
        // 记录初始化过滤选项
        if (distinctValues.value && Object.keys(distinctValues.value).length === 0&& filteredDataAll.value.length > 0) {
          // 为每个字段生成过滤选项
          columnOrder.forEach(key => {
            if (key === `isPassed`) {
              distinctValues.value[key] = isPassedOptions.map(option => option.display)
            } else if (key === `ruleType`) {
               const unique = filteredDataAll.value
               .map(item => getRuleTypeDisplay(item))
               .filter((v, i, arr) => arr.indexOf(v) === i && v !== undefined && v !== null && v !== ``)
               distinctValues.value[key] = ['全部', ...unique]
            }else if(key === `reviewSuggestion`||key === `lineNumber`){
               distinctValues.value[key] = ``
            }else{
              distinctValues.value[key] = []
              {
                const unique = filteredDataAll.value
                .map(item => item[key])
                .filter((v, i, arr) => arr.indexOf(v) === i && v !== undefined && v !== null && v !== '')
                distinctValues.value[key] = ['全部', ...unique]
              }
            }
          })
          // console.log(`---distinctValues.value---`, JSON.parse(JSON.stringify(distinctValues.value)))
        }
      }
    } catch (error) {
      console.error(`获取审查结果详情失败:`, error)
      loading.value = false
    } finally {
      loading.value = false
    }
  }
  
  const restFn = async () => {
    columns.value = []
    filteredDataAll.value = []
    filteredData.value = []
    distinctValues.value = {}
    filters.value = {}
    initPage()
  }
  
  const pagesChange = () => {
    // initPage()
    getPageDataList()
  }
  
  const filteredDataFn = () => {
    pages.value.pageNumber = 1
    fetchReviewResultDetails(1, pages.value.pageSize)
  }
  const status = ref(``)
  const initPage = async (noLoading?) => {
    pages.value.pageNumber = 1
    console.log(`---versionFilters.record---initPage`, versionFilters.record)
    
    // topInfoList[4].value = rowItem.value.useRuleSize
    // topInfoList[5].value = rowItem.value.questions
    // topInfoList[6].value = rowItem.value.filesLine
  
    // await sourceApi.getSourceCodeReviewResult(versionFilters.record).then(async res => {
    //   if (res.succ) {
    //     console.log('审查summary  res.content', res.content)
    //     // duration 结果耗时
    //     topInfoList[0].value = res.content.checkPoints //审查文件数
    //     topInfoList[1].value = res.content.passCheckPoints //通过文件数
    //     topInfoList[2].value = res.content.duration //审查耗时
    //     topInfoList[3].value = res.content.isClosedLoop === 1 ? "审查已通过" : "审查未通过" //审查状态
    //     topInfoList[3].class = res.content.isClosedLoop === 1 ? "ybh" : "wbh" //审查状态类名
    //     topInfoList[4].value = res.content.useRuleSize //使用规则数
    //     topInfoList[5].value = res.content.questions //问题数
    //     topInfoList[6].value = res.content.filesLine //审查文件行数	
    //     status.value = res.content.status 
    //     rowItem.value.status = res.content.status 
    //     rowItem.value.isClosedLoop = res.content.isClosedLoop
        await fetchReviewResultDetails(pages.value.pageNumber, pages.value.pageSize, noLoading)
    //     if (status.value === `IN_PROGRESS`||status.value === `ERROR`) {
    //       setTimeout(() => {
    //         initPage(true)
    //       }, 2000)
    //     }
    //   }
    // })
  }
  
  const getColumnWidth = (index: number, columnName: string) => {
    // console.log(index + " " + columnName)
    if([0,3,5].includes(index)){
      return `90px`
    }else if([2].includes(index)){
      return `100`
    }else if([4].includes(index)){
      return `360`
    }else if([1].includes(index)){
      return `130px`
    } else {
      return `auto`
    } 
  }
  const getCurrentTimestampStr = () => {
    const now = new Date()
    // 获取年、月、日、时、分、秒
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, `0`)
    const day = String(now.getDate()).padStart(2, `0`)
    const hours = String(now.getHours()).padStart(2, `0`)
    const minutes = String(now.getMinutes()).padStart(2, `0`)
    const seconds = String(now.getSeconds()).padStart(2, `0`)
  
    // 拼接成所需的格式
    return `${year}${month}${day}${hours}${minutes}${seconds}`
  }
  
  const onImportLoading = ref(false)
  const onImportTable = () => {
    onImportLoading.value = true
    sourceApi.getSourceCodeReviewResultVersion({"resultId": versionFilters.record, "version": versionFilters.version})
      .then(res => {
        onImportLoading.value = false
        if (res.succ) {
          const records = (res.content.detailVOList || []).map((item: any) => ({
            ...item,
            lineNumber: item.lineNumber !== undefined && item.lineNumber !== null ? String(item.lineNumber) : ``,
            reviewSuggestion: item.reviewSuggestion !== undefined && item.reviewSuggestion !== null ? String(item.reviewSuggestion) : ``,
            sourceFileName: item.sourceFileName !== undefined && item.sourceFileName !== null ? String(item.sourceFileName) : ``,
            errorCode: item.errorCode !== undefined && item.errorCode !== null ? String(item.errorCode) : ``,
            errorReason: item.errorReason !== undefined && item.errorReason !== null ? String(item.errorReason) : ``,
            ruleName: item.ruleName !== undefined && item.ruleName !== null ? String(item.ruleName) : ``,
              "ruleType": getRuleTypeDisplay(item),
            isPassed: item.isPassed === 1 ? `通过` : `未通过`,
            recheckConclusion: item.recheckConclusion !== undefined && item.recheckConclusion !== null ? String(item.recheckConclusion) : ``,
            questionDesc: item.questionDesc !== undefined && item.questionDesc !== null ? String(item.questionDesc) : ``,
            recheckStatus: item.recheckStatus === 1 ? `未复核` : item.recheckStatus === 2 ? `复核中` : item.recheckStatus === 3 && item.recheckResultStatus === 1 ? `复核同意` : item.recheckStatus === 3 && item.recheckResultStatus === 2 ? `复核拒绝` : item.recheckStatus === 3 ? `复核完成` : ``,
          }))
          list.value = records || []
          arr.splice(0)
          Object.keys(exportColumnMapping).forEach(key => {
            arr.push({
              "title": exportColumnMapping[key as keyof typeof exportColumnMapping],
              key,
              "type": `text`
            })
          })
          if (list.value && list.value.length) {
            table2excel(arr, list.value, `审查结果_` + rowItem.value.tel_file.replace(`.h`, ``).replace(`.zip`, ``).replace(`.c`, ``) + `_` + getCurrentTimestampStr() + `.xls`)
          } else {
            ElMessage.error(`没有数据可导出！`)
          }
        }
      })
      .catch(() => {
        onImportLoading.value = false
      })
  }
  const router = useRouter()
    const goBack = () => {
    // if (rowItem && rowItem.value.fromPath) { 目前实现起来有点困难，因为进入那个页面要传的参数太多了。
    //   console.log(`goBack`, rowItem.value.fromPath)
    //   router.push({ path: rowItem.value.fromPath })
    // } else {  
    //   // path:`/reviewDetail`
    // }
    if(path&&path===`/reviewDetail`){
      router.go(-1)
    }else{
      router.push({ path: `/logicDocumentReview` })
    }
  }
  
  const versionOptions = ref<any[]>([])
  const recordOptions = ref<any[]>([])
  const versionFilters = reactive({
    version: `` as any,
    record: `` as any,
    lastReviewTime: `` as string
  })


  const logicFileVersionFn = (fileId: any) => {
    sourceApi.logicFileVersion({ fileId }).then(res => {
      if (res.succ) {
        versionOptions.value = res.content
      }
    })
  }
  const logicFileResultFn = async (versionId: any) => {
    const res = await sourceApi.logicFileResult({ versionId })
    if (res.succ) {
      recordOptions.value = res.content || []
      if (recordOptions.value.length) {
        versionFilters.record = recordOptions.value[0].id
        versionFilters.lastReviewTime = recordOptions.value[0].reviewTime || ``
        pages.value.pageNumber = 1
        initPage()
      } else {
        rowItem.value.isClosedLoop = null
        versionFilters.record = ``
      }
    }
  }
  const recordChangeFn = async () => {
    pages.value.pageNumber = 1
    initPage()
  }
  const onFilterChange = async (key: string) => {
    pages.value.pageNumber = 1
    filteredDataFn()
  }
  
  const topInfoList: any = reactive([
    {
      name: `审查文件数`,
      value: 0,
      img: zscs,
      unit:`个`
    },
    {
      name: `通过审查文件数`,
      value: 0,
      img: ztg,
      unit:`个`
    },
    {
      name: `结果耗时`,
      value: 0,
      img: hs,
      unit:`秒`
    },
    {
      name: `状态`,
      value: ``,
      img: bhzt,
      class: ``
    },
    {
      name: `审查规则数`,
      value: 0,
      img: imgGzgs,
      unit:`条`
    },
    {
      name: `问题数`,
      value: 0,
      img: imgWtgs,
      unit:`个`
    },
    {
      name: `代码行数`,
      value: 0,
      img: imgDmhs,
      unit:`行`
    }
  ])
  const rulesListDataLocal = ref([])
  sourceApi.getSourceCodeReviewRules().then(res => {
    if (res.succ) {
      rulesListDataLocal.value = res.content.map((r: any) => {
        return {
          "label": r.name.replace(/^\s+|\s+$|[。.；;!！?？:：,，、()\[\]【】\{\}""']+$/gu, ``),
          "status": `审查中`
        }
      })
    }
  })
  const isFinish = ref(false)
  const finishInfo = ref<any>({})
  const showRuleModal = ref(false)
  const rulesListData = ref<any>([])
  const updatedRulesListData = ref<any>([])
  const generalCheckpoint = computed(() => {
    return updatedRulesListData.value.length
  })
  const passedCheckpoint = computed(() => generalCheckpoint.value - updatedRulesListData.value.filter(r => r.status.includes(`未通过`)).length)

  const goReviewResults = () => {
    if (recordOptions.value && recordOptions.value.length) {
      versionFilters.record = recordOptions.value[0].id
      recordChangeFn()
      showRuleModal.value = false
    }
  }
  
  const initDataFn = () => {
    // if (rowItem.value.fileVersionId) {
    //   versionFilters.version = rowItem.value.fileVersionId
    //   logicFileResultFn(versionFilters.version)
    // }
    if (rowItem.value.resultId) {
      versionFilters.record = rowItem.value.resultId
      recordChangeFn()
    }
  }
  const getDetailFn = async versionId => {
    const res = await sourceApi.logicFileResult({ versionId, includeAllStatus: true })
    if (res.succ) {
      if (res.content && res.content.length) {
        recordOptions.value = res.content || []
        versionFilters.record = recordOptions.value[0].id
        pages.value.pageNumber = 1
        initPage()
      }
    }
  }
  const getSourceCodeReviewResultSourceVersionsFn = (resultId)=>{
    sourceApi.getSourceCodeReviewResultSourceVersions({resultId:resultId}).then(res => {
      if (res.succ) {
        versionOptions.value = res.content || []
        if(versionOptions.value&&versionOptions.value.length){
          versionFilters.version = versionOptions.value[0]
        }
      }
    })  
  }

  if (item) {
    rowItem.value = JSON.parse(decodeURIComponent(item))
    console.log('query.item  =====> ', rowItem.value)
    // if (rowItem.value.status === `IN_PROGRESS` || rowItem.value.status === `ERROR` || !rowItem.value.status) {
    //   versionFilters.version = rowItem.value.fileVersionId
    //   getDetailFn(rowItem.value.fileVersionId)
    // } else {
      // logicFileVersionFn(rowItem.value.id)
      getSourceCodeReviewResultSourceVersionsFn(rowItem.value.resultId)
      initDataFn()
    // }
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

  const versionChangeFn = ()=>{
    if(versionFilters.version){
      recordChangeFn()
    }
  }

 
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="reviewResults" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
      <localTitle title="审查结果">
        <template #rightBtn>
          <!-- <el-button type="warning" @click="feedback">意见反馈</el-button> -->
          <el-button @click="goBack">返回</el-button>
        </template>
      </localTitle>
      
      <div class="versionSearchBox">
        <div class="right">
          <div class="fileName">
              代码文件名称：<el-text>{{ rowItem.tel_file }}</el-text>
              <!-- <div v-if="rowItem.isClosedLoop !== null" :class="{ wbh: rowItem.isClosedLoop === 0, ybh: rowItem.isClosedLoop === 1,middle:true }" style="cursor: default">
                {{ rowItem.isClosedLoop === 1 ? `审查已通过` : `审查未通过` }}
              </div> -->
              <!-- <el-button v-if="rowItem.isClosedLoop === null" type="info" disabled size="small" class="btn">{{ status.value === `IN_PROGRESS` || status.value === `ERROR` ? `文件正在审查` : `文件未审查` }}</el-button> -->
          </div>
          <!--  -->
        </div>
        <div class="left">
          <div class="label">代码文件版本：</div>
           <el-select v-model="versionFilters.version" placeholder="请选择" effect="light" popper-class="version-select-popper" @change="versionChangeFn">
              <el-option v-for="option in versionOptions" :key="option" :label="`${option}`" :value="option">
                <span class="version-option-text" :title="option">{{ option }}</span>
              </el-option>
            </el-select>
          <el-button v-loading="onImportLoading" type="primary" size="small" class="searchBtn" :disabled="versionFilters.record === ``" @click="onImportTable">导出审查结果</el-button>
        </div>
      </div>
  
      <div class="num">
        <div v-for="(items, index) in topInfoList" :key="index" class="itemBox itemBox5"> <!-- :class="`itemBox ` + `itemBox${index}`" -->
          <div class="titleRow">
            <span class="label">{{ items.name }}</span>
          </div>
          <div class="contentRow">
            <div class="left">
              <img :src="items.img" alt="" />
            </div>
            <div class="right">
              <span :class="`value ` + items.class">
                <span class="number">{{ items.value === null || items.value === undefined ? `--` : `${items.value}` }}</span>
                <span class="unit" v-if="items.unit">{{ items.unit }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
  
      <div class="tableBox">
        <div class="search">
          <el-form ref="ruleFormRef" :inline="true">
            <el-form-item v-for="(values, key) in distinctValues" :key="key" :label="getTableColumnName(key as string)">
              <el-select v-if="values && values.length" v-model="filters[key as string]" placeholder="全部" filterable style="width: 130px" @change="onFilterChange(key as string)">
                <el-option v-for="value in values" :key="value" :label="value" :value="value" />
              </el-select>
              <el-input v-else v-model="filters[key as string]" style="width: 130px" placeholder="请输入" />
            </el-form-item>
          </el-form>
          <div style="display: flex; align-items: center;">
            <div v-if="hasRecheckData" style="display: flex; align-items: center; margin-right: 12px; gap: 8px;">
              <span style="font-size: 15px; color: #606266;">显示所有列</span>
              <el-switch v-model="showAllColumns" style="--el-switch-on-color: #67c23a; --el-switch-off-color: #dcdfe6; --el-switch-size: 20px;" size="large" />
            </div>
            <el-button :disabled="!versionFilters.record" type="primary" @click="filteredDataFn()">搜索</el-button>
            <el-button :disabled="!versionFilters.record" @click="restFn">重置</el-button>
            <el-button :disabled="!versionFilters.record || selectedRows.length === 0" type="warning" @click="handleBatchRecheck">批量申请复核</el-button>
          </div>
        </div>
        <el-table :data="filteredData" border tooltip-effect="light" @selection-change="handleSelectionChange">
          <el-table-column type="selection" align="center" width="55" :selectable="(row) => row.isPassed === 0 && (!row.recheckStatus || row.recheckStatus === 1)" />
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
              <template v-if="columnMapping[column] === '规则类型'">
                <span>{{ getRuleTypeDisplay(scope.row) }}</span>
              </template>
              <template v-else-if="columnMapping[column] === '审查意见'">
                <div class="contRow2" v-if="scope.row.isPassed !== 1" v-html="formatReviewText(scope.row[column])" />
                <div class="contRow2" v-else>无问题</div>
              </template>
              <template v-else-if="columnMapping[column] === '审查结论'">
                <div class="isPass">
                  <span v-if="scope.row[column] == 0"> <i class="iconfont icon-butongguo1" />未通过 </span>
                  <span v-if="scope.row[column] == 1"> <i class="iconfont icon-chenggong" />通过 </span>
                </div>
              </template>
              <template v-else-if="columnMapping[column] === '错误原因'||columnMapping[column] === '机理说明'||columnMapping[column] === '审查规则'">
                <div class="contRow2">{{ scope.row[column] }}</div>
              </template>
              <template v-else-if="columnMapping[column] === '行号'">
                <div class="contRow2" align="center">
                  <a href="javascript:void(0)" @click.prevent="openCodeDialog(scope.row)">
                    {{ scope.row[column] }}
                  </a>
                </div>
              </template>
              <template v-else-if="columnMapping[column] === '错误代码'">
                <div class="contRow2" v-html="scope.row[column]? scope.row[column].replace(/\n/g, '<br>') : `` " />
              </template>
              <template v-else-if="columnMapping[column] === '代码文件'">
                <div class="contRow2">{{ getfileName(scope.row[column]) }}</div>
              </template>
              <template v-else>
                <div class="contRow2">{{ scope.row[column] }}</div>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100px"  class-name="col-action">
            <template #default="scope">
              <el-button
                v-if="scope.row.recheckStatus === 2"
                disabled
                type="info"
                size="small"
              >
                复核中
              </el-button>
              <el-button
                v-else-if="scope.row.recheckStatus === 3 && scope.row.recheckResultStatus === 1"
                disabled
                type="info"
                size="small"
              >
                复核同意
              </el-button>
              <el-button
                v-else-if="scope.row.recheckStatus === 3 && scope.row.recheckResultStatus === 2"
                disabled
                type="info"
                size="small"
              >
                复核拒绝
              </el-button>
              <el-button
                v-else
                :disabled="scope.row.isPassed !== 0"
                type="primary"
                size="small"
                @click="handleRecheck(scope.row)"
              >
                申请复核
              </el-button>
            </template>
          </el-table-column>
          <el-table-column v-if="showQuestionDescColumn" label="问题描述" align="left" min-width="200px" show-overflow-tooltip>
            <template #default="scope">
              <div class="contRow2">{{ scope.row.questionDesc || '-' }}</div>
            </template>
          </el-table-column>
          <el-table-column v-if="showRecheckConclusionColumn" label="复核结论" align="left" min-width="120px" show-overflow-tooltip>
            <template #default="scope">
              <!-- 复核通过: recheckResultStatus = 1 -->
              <div v-if="scope.row.recheckResultStatus === 1" class="contRow2">
                已通过: {{ scope.row.recheckConclusion || '-' }}
              </div>
              <!-- 复核拒绝: recheckResultStatus = 2 -->
              <div v-else-if="scope.row.recheckResultStatus === 2" class="contRow2">
                <div>已拒绝: {{ scope.row.recheckConclusion || '-' }}</div>
                <div>复核人: {{ scope.row.recheckUserId || '-' }}</div>
                <div>拒绝理由: {{ scope.row.rejectReason || '-' }}</div>
              </div>
              <!-- 其他情况（无复核结果状态） -->
              <div v-else class="contRow2">
                {{ scope.row.recheckConclusion || '-' }}
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
  
      <el-dialog v-model="showRuleModal" class="dialogCont" title="" :show-close="false" width="1200px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">审查进度</div>
            <div class="right status">{{ isFinish ? `审查完成` : `审查进行中` }}</div>
          </div>
        </template>
        <div class="contBox">
          <div class="tableCont">
            <el-table ref="rulesListRef" border class="custom-table-size" tooltip-effect="light" :data="rulesListData" height="100%">
              <el-table-column type="index" label="序号" align="center" width="100" />
              <el-table-column prop="label" label="规则名称" align="left" min-width="400px" show-overflow-tooltip />
              <el-table-column prop="status" label="审查结果" align="center" min-width="100px" class-name="status">
                <template #default="scope">
                  <div :class="{ tg: scope.row.status === `通过`, pszb: scope.row.status === `审查中`, notg: scope.row.status === `未通过`, bsy: scope.row.status === `不适用` }">
                    {{ scope.row.status }}
                    <div v-show="scope.row.status === `审查中`" class="dot">
                      <div />
                      <div />
                      <div />
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="numInfo">
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/scds.png" alt="" />
              </div>
              <div class="value">
                <span class="label">审查规则数</span>
                <span> {{ isFinish ? generalCheckpoint : `--` }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/ztg.png" alt="" />
              </div>
              <div class="value">
                <span class="label">通过规则数</span>
                <span> {{ isFinish ? passedCheckpoint : `--` }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/zscs.png" alt="" />
              </div>
              <div class="value">
                <span class="label">审查文件数</span>
                <span> {{ isFinish ? finishInfo.checkPoints : `--` }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/tgscds.png" alt="" />
              </div>
              <div class="value">
                <span class="label">通过审查文件数</span>
                <span> {{ isFinish ? finishInfo.passCheckPoints : `--` }} </span>
              </div>
            </div>
            <!-- <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/tgl.png" alt="" />
              </div>
              <div class="value">
                <span class="label">通过率</span>
                <span> {{ isFinish ? Math.round(finishInfo.passRate * 1000) / 10 + `%` : `--` }} </span>
              </div>
            </div> -->
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right" />
            <div class="btn">
              <el-button :disabled="!isFinish" @click="showRuleModal = false"> 关闭</el-button>
              <el-button type="primary" :disabled="!isFinish" @click="goReviewResults()"> 查看审查详情 </el-button>
            </div>
          </div>
        </template>
      </el-dialog>

    <!-- 复核弹窗 -->
    <el-dialog v-model="auditModalVisible" :title="isBatchMode ? '批量申请复核' : '申请复核'" width="500px" :close-on-click-modal="false">
    <el-form label-width="100px" ref="auditFormRef" :model="{ auditConclusion, feedbackText }" :rules="feedbackRules">
      <el-form-item label="复核结论：" prop="auditConclusion">
        <el-select v-model="auditConclusion" placeholder="请选择复核结论" style="width: 100%">
          <el-option
            v-for="item in auditOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="问题描述：" prop="feedbackText">
        <el-input
          v-model="feedbackText"
          type="textarea"
          :placeholder="feedbackPlaceholder"
          maxlength="500"
          show-word-limit
          :rows="4"
          @input="handleFeedbackInput"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelAudit">取消</el-button>
        <el-button type="primary" :disabled="isSubmitting" @click="submitAudit">提交</el-button>
      </span>
    </template>
  </el-dialog>

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
      >.right{
        width: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        .label{
          font-size: 16px;
          color: #3a4c5f;
          line-height: 56px;
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
        height: 100px;
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
              padding-right: 10px;
            }
            .el-input,
            .el-input__inner {
              height: 32px;
              line-height: 32px;
            }
          }
        }
      }
      :deep(.el-table) {
        background-color: transparent;
         height: calc(100% - 150px);
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
        .col-action .cell {
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
  