<script setup lang="ts">
  import scjcds from "@/assets/images/circuitReview/zscs.png"
  import wtg from "@/assets/images/circuitReview/wtg.png"
  import bhzt from "@/assets/images/circuitReview/bhzt.png"
  import tgl from "@/assets/images/circuitReview/tgl.png"
  import localTitle from "./components/localTitle.vue"
  import table2excel from "js-table2excel"
  import {
    getCircuitReviewResultDetailPage,
    getCircuitReviewResultDetailList,
    getCircuitReviewResultFilters,
    circuitFileVersion,
    delCircuitReviewResult,
    circuitFileResult,
    circuitFileVersionFs,
    getCircuitReviewResult,
    createCircuitReview,
    getCircuitReviewRuleSummary,
    getCircuitReviewRules,
    submitAuditResult,
    checkInAudit
  } from "@/ajax/circuitreview"
  import store from "@/store/index"
  const userInfo = computed(() => store.state.user.userInfo)
  const userName = computed(() => userInfo.value?.name)
  
  const loading = ref(false)
  const route = useRoute()
  const { item,path }: any = route.query
  console.log('item:', item)
  const rowItem = ref<any>({})
  
  const deviceTypeList = ref([`全部`])
  const reviewRuleList = ref([`全部`])
  const ruleTypeList = ref([`全部`, `建议规则`, `必要规则`, `强制规则`])
  const isPassedList = ref([`全部`, `通过`, `未通过`])
  const getCircuitReviewResultFiltersFn = async () => {
    if (!versionFilters.record) return
    // 获取过滤选项时，根据当前审查结论过滤条件来获取对应的候选值
    const params: any = { id: versionFilters.record }
    // 如果有审查结论过滤条件且不是"全部"，则添加到参数中
    if (filters.value.isPassed && filters.value.isPassed !== `全部`) {
      const selectedOption = isPassedOptions.find(option => option.display === filters.value.isPassed)
      if (selectedOption && selectedOption.value !== ``) {
        params.isPassed = selectedOption.value
      }
    }
    const res = await getCircuitReviewResultFilters(params)
    if (res.succ) {
      const deviceTypes: string[] = res.content.deviceTypeFilters || []
      const reviewRules: string[] = res.content.reviewRuleFilters || []
      const ruleTypeFilters: number[] = res.content.ruleTypeFilters || []
      const isPassedFilters: number[] = res.content.isPassedFilters || []
      // 去重并确保“全部”在首位
      const deviceTypeDeduped = Array.from(new Set(deviceTypes.filter(v => v && v !== `全部`)))
      const reviewRuleDeduped = Array.from(new Set(reviewRules.filter(v => v && v !== `全部`)))
      deviceTypeList.value = [`全部`, ...deviceTypeDeduped]
      reviewRuleList.value = [`全部`, ...reviewRuleDeduped]
      // 规则类型与审查结论使用接口返回的候选（整数 -> 展示）
      const ruleTypeIntToDisplay: Record<number, string> = { 1: `建议规则`, 2: `必要规则`, 3: `强制规则` }
      const ruleTypeDisplays = Array.from(new Set(ruleTypeFilters.map(rt => ruleTypeIntToDisplay[rt]).filter(Boolean)))
      ruleTypeList.value = [`全部`, ...ruleTypeDisplays]
      const isPassedIntToDisplay: Record<number, string> = { 1: `通过`, 0: `未通过` }
      const isPassedDisplays = Array.from(new Set(isPassedFilters.map(ip => isPassedIntToDisplay[ip]).filter(Boolean)))
      isPassedList.value = [`全部`, ...isPassedDisplays]
      // 若已构建过 columns，则回写以切换为下拉
      if (distinctValues.value.deviceType !== undefined) {
        distinctValues.value.deviceType = deviceTypeList.value
      }
      if (distinctValues.value.ruleName !== undefined) {
        distinctValues.value.ruleName = reviewRuleList.value
      }
      if (distinctValues.value.ruleType !== undefined) {
        distinctValues.value.ruleType = ruleTypeList.value
      }
      if (distinctValues.value.isPassed !== undefined) {
        distinctValues.value.isPassed = isPassedList.value
      }
    }
  }
  const list = ref([])
  const arr: any = reactive([])
  
  const filters = ref<{ [key: string]: string | undefined }>({})
  const distinctValues = ref<{ [key: string]: string[] }>({})
  const columns = ref<{ label: string; prop: string; name: string }[]>([])
  
  // 控制复核结论和问题描述列的显示
  const showAuditColumns = ref(false)
  
  // 显示所有列按钮状态
  const showAllColumns = ref(false)
  
  // 判断当前分页是否有复核被拒绝或审核中的数据（auditStatus == 'REJECTED' 或 auditTypeStatus == 'DOING'）
  const hasRejectedOrDoingRecheck = computed(() => {
    return filteredData.value.some((row: any) => row.auditStatus === 'REJECTED' || row.auditTypeStatus === 'DOING')
  })

  // 检查当前页问题描述是否有长度超过5个字符的数据
  const hasLongQuestionDesc = computed(() => {
    return filteredData.value.some((row: any) => {
      const desc = row.issueFeedback || ''
      return desc.length > 5
    })
  })

  // 检查当前页复核结论是否有长度超过5个字符的数据
  const hasLongRecheckConclusion = computed(() => {
    return filteredData.value.some((row: any) => {
      const conclusion = row.approvedAuditType || ''
      return conclusion.length > 5
    })
  })

  // 判断当前分页是否全部复核完成（有值的数据都是 FINISH）
  const isAllRecheckCompleted = computed(() => {
    if (filteredData.value.length === 0) return false
    // 过滤出有值的行
    const rowsWithValue = filteredData.value.filter((row: any) => row.auditTypeStatus)
    // 没有有值的行，不算全部完成
    if (rowsWithValue.length === 0) return false
    // 有值的都是 FINISH 就算全部复核完成
    return rowsWithValue.every((row: any) => row.auditTypeStatus === 'FINISH')
  })

  // 判断是否需要显示"显示所有列"按钮（有复核数据时显示）
  const needShowAllColumnsBtn = computed(() => {
    // 有复核结论列显示时，才需要显示该按钮
    return showAuditColumns.value && (hasRejectedOrDoingRecheck.value || isAllRecheckCompleted.value || 
      filteredData.value.some((row: any) => row.auditTypeStatus === 'FINISH'))
  })
  
  // 判断是否显示复核结论列（有拒绝/审核中数据时自动打开，或全部完成时始终显示）
  const showRecheckConclusionColumn = computed(() => {
    // 问题8：全部完成时，无论按钮状态都显示复核结论列
    if (isAllRecheckCompleted.value) return true
    // 有拒绝或审核中的数据时，自动显示复核结论列
    if (hasRejectedOrDoingRecheck.value) return true
    // 其他情况根据按钮状态
    return showAuditColumns.value && showAllColumns.value
  })
  
  // 判断是否显示问题描述列（统一根据按钮状态）
  const showQuestionDescColumn = computed(() => {
    return showAuditColumns.value && showAllColumns.value
  })
  
  // 定义表格列映射 - 按照显示顺序定义
  const columnOrder = [`ruleType`, `ruleName`, `deviceType`, `tagPin`, `reviewSuggestion`, `isPassed`]
  const columnMapping = {
    "ruleType": `规则类型`,
    "ruleName": `审查规则`,
    "deviceType": `器件型号`,
    "tagPin": `位号管脚`,
    "reviewSuggestion": `审查意见`,
    "isPassed": `审查结论`,
    "isPassed1": `复核状态`,
    "issueFeedback": `问题描述`,
    "approvedAuditType": `复核结论`,
  }
  
  // 规则类型枚举映射
  const ruleTypeMapping = {
    "PROPOSITIONAL_RULE": `建议规则`,
    "NEEDFUL_RULE": `必要规则`,
    "COERCIVE_RULE": `强制规则`
  }
  
  // 审查结论选项配置
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
    "pageSize": 100,
    "pageNumber": 1,
    "total": 1
  })
  // 获取审查结果详情数据
  const fetchReviewResultDetails = async (page = 1, pageSize = 10) => {
    loading.value = true
    try {
      const params: any = {
        "pageNumber": page,
        pageSize,
        "resultId": versionFilters.record // 使用审查结果ID
      }
  
      // 添加过滤条件
      if (filters.value.ruleName && filters.value.ruleName !== `全部`) {
        params.ruleName = filters.value.ruleName
      }
      if (filters.value.ruleType && filters.value.ruleType !== `全部`) {
        // 将中文规则类型转换回英文枚举值
        const englishRuleType = Object.keys(ruleTypeMapping).find(key => ruleTypeMapping[key] === filters.value.ruleType)
        params.ruleType = englishRuleType || filters.value.ruleType
      }
      if (filters.value.deviceType && filters.value.deviceType !== `全部`) {
        params.deviceType = filters.value.deviceType
      }
      if (filters.value.tagPin && filters.value.tagPin !== `全部`) {
        params.tagPin = filters.value.tagPin
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
  
      const res = await getCircuitReviewResultDetailPage(params)
      if (res.succ) {
        // 复核结论映射
        const auditTypeMapping: Record<string, string> = {
          "RULE_NOT_APPLICABLE": "规则不适用",
          "ISSUE_EXCEPTION": "问题可例外",
          "REVIEW_NO_IMPACT": "复查无影响",
          "REVIEW_INCORRECT": "审查不正确"
        }
        // 处理返回的数据
        const records = res.content.records.map((item: any) => ({
          ...item,
          "ruleType": ruleTypeMapping[item.ruleType] || item.ruleType, // 转换规则类型为中文
          "auditTypeStatus": (() => {
            if (item.auditStatus === 'CANCELLED') {
              return "CANCELLED" // 取消
            } else if (!item.auditType && !item.approvedAuditType) {
              return "INIT" // 初始化
            } else if (item.auditType && !item.approvedAuditType) {
              return "DOING" // 进行中
            } else {
              return "FINISH" // 已完成
            }
          })(), 
          "issueFeedback": (() => {
            if (item.auditStatus == 'REJECTED') {
              return  item.rejectIssueFeedback
            }
            return item.issueFeedback
          })(), 
          "approvedAuditType": (() => {
            // 显示规则：
            // 1. 当 auditType 和 approvedAuditType 都没有值时，显示 -
            // 2. 当 auditType 有值，而 approvedAuditType 没有值，显示 复核中
            // 3. 否则显示 approvedAuditType 的翻译值
            let auditTypeDisplay: string = ""
            if (!item.auditType && !item.approvedAuditType) {
              auditTypeDisplay = "-"
            } else if (item.auditType && !item.approvedAuditType) {
              auditTypeDisplay = `${auditTypeMapping[item.auditType]}`
            } else {
              auditTypeDisplay = auditTypeMapping[item.approvedAuditType] || item.approvedAuditType
            }
            // console.log('item.auditStatus', item.auditStatus)
            if (item.auditStatus == 'REJECTED') {
              const rejectType = auditTypeMapping[item.rejectAuditType] || item.rejectAuditType || '-'
              const auditUser = item.auditUserName || '-'
              const rejectReason = item.rejectReason || '-'
              auditTypeDisplay = `已拒绝。复核类型: ${rejectType}; 复核人: ${auditUser}; 拒绝理由: ${rejectReason}`
            }
            return auditTypeDisplay
          })()
        }))
        filteredData.value = records
        
        // 数据加载后，如果有拒绝或审核中的数据，自动打开显示所有列按钮
        nextTick(() => {
          if (hasRejectedOrDoingRecheck.value) {
            showAllColumns.value = true
          }
        })
  
        pages.value.total = res.content.total
        // 如果是第一次加载，初始化过滤选项
        if (columns.value && columns.value.length === 0) {
          // 为每个字段生成过滤选项
          columnOrder.forEach(key => {
            let values: string[] = []
            if (key !== 'approvedAuditType'  && key !== 'issueFeedback') {
              if (key === `isPassed`) {
                // 审查结论列使用候选
                values = isPassedList.value
              } else if (key === `ruleType`) {
                values = ruleTypeList.value
              } else if (key === `deviceType`) {
                values = deviceTypeList.value
              } else if (key === `ruleName`) {
                values = reviewRuleList.value
              }
              //console.log(`如果是第一次加载，初始化过滤选项key`, key, values)
              distinctValues.value[key] = values || []
            } 
            columns.value.push({
              "label": key,
              "prop": key,
              "name": getTableColumnName(key)
            })
          })
        }
      }
    } catch (error) {
      console.error(`获取审查结果详情失败:`, error)
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
    // 重置后设置审查结论默认值为"未通过"
    filters.value.isPassed = '未通过'
    await getCircuitReviewResultFiltersFn()
    initPage()
  }
  
  const filteredDataFn = () => {
    pages.value.pageNumber = 1
    fetchReviewResultDetails(1, pages.value.pageSize)
  }
  
  const getTopInfoList = () => {
    getCircuitReviewResult(versionFilters.record).then(res => {
      if (res.succ) {
        topInfoList[0].value = res.content.checkPoints
        topInfoList[1].value = res.content.passRate ? (res.content.passRate * 100).toFixed(1) + "%" : `-`
        topInfoList[2].value = res.content.failCheckPoints
        topInfoList[3].value = res.content.isClosedLoop === 1 ? "问题已关闭" : "问题未关闭"
        topInfoList[3].class = res.content.isClosedLoop === 1 ? "ybh" : "wbh"
        // resultAuditId 有值表示需要显示复核结论和问题描述列
        showAuditColumns.value = res.content.resultAuditId !== undefined && res.content.resultAuditId !== null
      }
    })
  }

  const initPage = val => {
    fetchReviewResultDetails(pages.value.pageNumber, pages.value.pageSize)
    getTopInfoList()
    if (val) {
      nextTick(() => {
        document.getElementById(`layoutWrap`).scrollTo({
          top: 300,
          behavior: "smooth"
        })
      })
    }
  }
  
  const getColumnWidth = index => {
    // 列顺序：0.规则类型 1.审查规则 2.器件型号 3.位号管脚 4.审查意见 5.审查结论
    if ([0].includes(index)) {
      return `86` // 规则类型列
    } else if ([5].includes(index)) {
      return `100` // 审查结论列
    } else if ([2, 3].includes(index)) {
      return `180` // 器件型号列、位号管脚列
    } else if ([1, 4].includes(index)) {
      return `300` // 审查规则列、审查意见列
    } else if ([6].includes(index)) {
      return `90` // 审查结论列
    } else {
      return `auto` // 其他列（复核结论、问题描述等）
    }
  }
  
  // 获取问题描述列宽度（当前页有超过5个字符的数据时为300px，否则120px）
  const getQuestionDescWidth = () => {
    if (hasLongQuestionDesc.value) {
      return `300px`
    }
    return `120px`
  }

  // 获取复核结论列宽度（所有复核完成时适合5个字的宽度，有超过5个字符的数据时为300px，否则120px）
  const getRecheckConclusionWidth = () => {
    // 当所有复核完成时，宽度适合5个字（约90px）
    if (isAllRecheckCompleted.value) {      
      return `105px`
    }
    // 当前页有超过5个字符的数据时为300px，否则120px
    if (hasLongRecheckConclusion.value) {
      return `300px`
    }
    return `105px`
  }
  const getColumnAlign = (column: { prop: string }) => {
    // 只有规则类型列居中，其他列（包括位号管脚、审查规则、器件型号、审查意见）都左对齐
    if (column.prop === 'ruleType') {
      return 'center'
    }
    return 'left'
  }
  
  const getColumnClassName = (column: { prop: string }) => {
    // 为规则类型列添加居中类，为审查规则、器件型号和审查意见列添加左对齐类
    if (column.prop === 'ruleType') {
      return 'center-aligned'
    } else if (column.prop === 'ruleName' || column.prop === 'deviceType' || column.prop === 'reviewSuggestion') {
      return 'left-aligned'
    }
    return ''
  }
  const getCurrentTimestampStr = () => {
    const now = new Date()
    // 获取年、月、日、时、分、秒
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, `0`) // 月份从0开始
    const day = String(now.getDate()).padStart(2, `0`)
    const hours = String(now.getHours()).padStart(2, `0`)
    const minutes = String(now.getMinutes()).padStart(2, `0`)
    const seconds = String(now.getSeconds()).padStart(2, `0`)
  
    // 拼接成所需的格式
    return `${year}${month}${day}${hours}${minutes}${seconds}`
  }

  const isJiZaiUser = computed(() => store.state.isJiZaiUser)
  // 可以添加页脚数据或方法
  const VITE_APP_IS_JIZAI = isJiZaiUser.value
  // const fileType = VITE_APP_IS_JIZAI?`atel`: `tel`
   const fileType = VITE_APP_IS_JIZAI?`.atel,.tel`: `.tel`
  const onImportLoading = ref(false)
  const onImportTable = () => {
    const params: any = {
      "resultId": versionFilters.record // 使用审查结果ID
    }
    // 添加过滤条件
    if (filters.value.ruleName && filters.value.ruleName !== `全部`) {
      params.ruleName = filters.value.ruleName
    }
    if (filters.value.ruleType && filters.value.ruleType !== `全部`) {
      // 将中文规则类型转换回英文枚举值
      const englishRuleType = Object.keys(ruleTypeMapping).find(key => ruleTypeMapping[key] === filters.value.ruleType)
      params.ruleType = englishRuleType || filters.value.ruleType
    }
    if (filters.value.deviceType && filters.value.deviceType !== `全部`) {
      params.deviceType = filters.value.deviceType
    }
    if (filters.value.tagPin && filters.value.tagPin !== `全部`) {
      params.tagPin = filters.value.tagPin
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
    onImportLoading.value = true
    getCircuitReviewResultDetailList(params)
      .then(res => {
        onImportLoading.value = false
        if (res.succ) {
          // 处理返回的数据
          // 位号管脚 审查意见
          // const columnMapping = {
          //   "ruleType": `规则类型`,
          //   "ruleName": `审查规则`,
          //   "deviceType": `器件型号`,
          //   "tagPin": `位号管脚`,
          //   "reviewSuggestion": `审查意见`,
          //   "isPassed": `审查结论`
          // }
           const auditTypeMapping: Record<string, string> = {
            "RULE_NOT_APPLICABLE": "规则不适用",
            "ISSUE_EXCEPTION": "问题可例外",
            "REVIEW_NO_IMPACT": "复查无影响",
            "REVIEW_INCORRECT": "审查不正确"
          }
    //          "approvedAuditType": `复核结论`,
    // "issueFeedback": `问题描述` 
          const records = res.content.map((item: any) => ({
            ...item,
            tagPin: item.tagPin || ``,
            reviewSuggestion: item.reviewSuggestion || ``,
            deviceType: item.deviceType || ``,
            "ruleType": ruleTypeMapping[item.ruleType] || item.ruleType, // 转换规则类型为中文
            isPassed: item.isPassed === 1 ? `通过` : `未通过`,
            approvedAuditType: (()=>{
              let auditTypeDisplay: string = ""
              if (!item.auditType && !item.approvedAuditType) {
                auditTypeDisplay = "-"
              } else if (item.auditType && !item.approvedAuditType) {
                auditTypeDisplay = `${auditTypeMapping[item.auditType]}`
              } else {
                auditTypeDisplay = auditTypeMapping[item.approvedAuditType] || item.approvedAuditType
              }
              // console.log('item.auditStatus', item.auditStatus)
              if (item.auditStatus == 'REJECTED') {
                const rejectType = auditTypeMapping[item.rejectAuditType] || item.rejectAuditType || '-'
                const auditUser = item.auditUserName || '-'
                const rejectReason = item.rejectReason || '-'
                auditTypeDisplay = `已拒绝。复核类型: ${rejectType}; 复核人: ${auditUser}; 拒绝理由: ${rejectReason}`
              }
              return auditTypeDisplay
            })(),
            "issueFeedback": (() => {
              if (item.auditStatus == 'REJECTED') {
                return  item.rejectIssueFeedback
              }
              return item.issueFeedback || `-`
            })(), 
            "isPassed1":(()=>{
              let auditTypeStatus = ``
               if (item.auditStatus === 'CANCELLED') {
                auditTypeStatus = "CANCELLED" // 取消
              } else if (!item.auditType && !item.approvedAuditType) {
                auditTypeStatus = "INIT" // 初始化
              } else if (item.auditType && !item.approvedAuditType) {
                auditTypeStatus = "DOING" // 进行中
              } else {
                auditTypeStatus = "FINISH" // 已完成
              }
              if(auditTypeStatus == 'DOING'){
                return `复核中`
              }else if(auditTypeStatus == 'CANCELLED'){
                return `复核取消`
              }else{
                return auditTypeStatus == 'FINISH' ? '复核完成' : '申请复核'
              }
            })(),
            "auditTypeStatus": (() => {
            if (item.auditStatus === 'CANCELLED') {
              return "CANCELLED" // 取消
            } else if (!item.auditType && !item.approvedAuditType) {
              return "INIT" // 初始化
            } else if (item.auditType && !item.approvedAuditType) {
              return "DOING" // 进行中
            } else {
              return "FINISH" // 已完成
            }
          })(), 
          }))
          // 更新导出用的数据
          list.value = records || []
          arr.splice(0)
          Object.keys(columnMapping).forEach(key => {
            arr.push({
              "title": columnMapping[key as keyof typeof columnMapping],
              key,
              "type": `text`
            })
          })
          if (list.value && list.value.length) {
            const item = versionOptions.value.find(item => item.id === versionFilters.version)
            let downloadFileName = ``
            if(fileType===`.atel,.tel`){
              downloadFileName = `审查结果_${rowItem.value.isClosedLoop === 1 ? `已关闭` : `未关闭`}_` + (item ? item.fileName : rowItem.value.tel_file).replace(`.atel`, ``).replace(`.tel`, ``) + `_` + getCurrentTimestampStr() + `.xls`
            }else{
              downloadFileName = `审查结果_${rowItem.value.isClosedLoop === 1 ? `已关闭` : `未关闭`}_` + (item ? item.fileName : rowItem.value.tel_file).replace(`${fileType}`, ``) + `_` + getCurrentTimestampStr() + `.xls`
            }
            table2excel(arr, list.value, downloadFileName)
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
      router.push({ path: `/documentReview` })
    }
  }
  
  const Run = (type: string, content: any) => {
    const windowLocal: any = window
    try {
      // 如果content有多个，如：[R603, R604, R601, R605, R606, R607, R602, R608]，需要弹出选择框，只能让用户选择一个
      let text = content
      if (type === `net_comp`) {
        let pattern = /\[[\S, ]+\]/g
        text = content.match(pattern)
        if (!text) {
          return
        }
        pattern = /[, ]/g
        const items = text[0].slice(1, -1).split(pattern)
        if (items && items.length > 0) {
          let objType = `COMP`
          if (type.match(/网络名：/g) || type.match(/以下列表中网名/g)) {
            objType = `NET`
          }
          const url1 = `VXinWebStartApp:\\` + objType + `||` + items[0]
          console.log(`打开url1`, url1)
          windowLocal.protocolCheck(url1, event => {
            ElMessageBox.confirm(`未检测到电路设计对应程序,请先安装程序！`, `提示`, {
              "confirmButtonText": `关闭`,
              "type": `warning`,
              "showCancelButton": false
            })
              .then(() => {})
              .catch(() => {})
          })
          // 最小化当前浏览器窗口
          window.moveTo(-9999, -9999)
          window.resizeTo(1, 1)
        }
      } else {
        let objType = `COMP`
        if (type.match(/./g)) {
          objType = `PIN`
        }
        const url = `VXinWebStartApp:\\` + objType + `||` + text
        windowLocal.protocolCheck(url, event => {
          ElMessageBox.confirm(`未检测到电路设计对应程序,请先安装程序！`, `提示`, {
            "confirmButtonText": `关闭`,
            "type": `warning`,
            "showCancelButton": false
          })
            .then(() => {})
            .catch(() => {})
        })
        // window.open(url);
        // 最小化当前浏览器窗口
        window.moveTo(-9999, -9999)
        window.resizeTo(1, 1)
      }
    } catch (e) {
      alert(e)
    }
  }
  
  // 处理位号管脚分隔，支持逗号、空格或两者混合分隔
  function splitPinValues(pinStr: string): string[] {
    if (!pinStr) return []
  
    // 先将连续的空格替换为单个空格
    const normalizedStr = pinStr.replace(/\s+/g, ` `)
  
    // 将空格替换为逗号（如果周围没有逗号）
    const commaSpaceStr = normalizedStr.replace(/([^,])\s([^,])/g, `$1,$2`)
  
    // 分割字符串并过滤空项
    return commaSpaceStr
      .split(`,`)
      .map(item => item.trim())
      .filter(item => item !== ``)
  }
  
  // 处理审查意见文字，将位号管脚变成可点击的链接
  function processReviewComment(comment: string) {
    if (!comment) return []
  
    // 使用正则表达式匹配位号管脚格式：[A-Z]+[0-9]+\.[A-Z]+[0-9]+ 或 [A-Z]+[0-9]+\.[0-9]+
    // 排除电压标注（如 VCC3.3V），即当点号后为纯数字时，数字后面不能紧跟 V/v
    const regex = /([A-Z]+[0-9]+\.(?:[A-Z]+[0-9]+|[0-9]+(?![Vv])))/g
  
    // 将评论文本拆分成组件引用和普通文本
    const parts: any = []
    let lastIndex = 0
    let match
  
    while ((match = regex.exec(comment)) !== null) {
      // 添加匹配前的文本
      if (match.index > lastIndex) {
        parts.push({
          "type": `text`,
          "content": comment.substring(lastIndex, match.index)
        })
      }
  
      // 添加匹配的组件引用
      parts.push({
        "type": `component`,
        "content": match[0]
      })
  
      lastIndex = regex.lastIndex
    }
  
    // 添加最后一部分文本
    if (comment && lastIndex < comment.length) {
      parts.push({
        "type": `text`,
        "content": comment.substring(lastIndex)
      })
    }
  
    return parts
  }
  
  // 头部选择
  const versionOptions = ref([])
  const recordOptions = ref([])
  const versionFilters = reactive({
    version: ``,
    record: ``
  })
  // 获取文件版本id
  const circuitFileVersionFn = fileId => {
    circuitFileVersion({ fileId }).then(res => {
      if (res.succ) {
        versionOptions.value = res.content
        console.log('versionOptions.value', versionOptions.value)
      }
    })
  }
  // 获取当前选中版本的文件名
  const getSelectedVersionFileName = () => {
    const item = versionOptions.value.find(item => item.id === versionFilters.version)
    return item ? item.fileName + ' ' + item.createDate : ``
  }

  // 获取审查记录列表
  const circuitFileResultFn = async (versionId, resultId) => {
    versionFilters.record = resultId
    const res = await circuitFileResult({ versionId })
    if (res.succ) {
      recordOptions.value = res.content || []
      console.log(`recordOptions.value`, recordOptions.value.length)
      if (recordOptions.value.length) {
        if (!resultId || recordOptions.value.filter(r => r.id === resultId).length === 0) {
          versionFilters.record = recordOptions.value.length ? recordOptions.value[0].id : ``
        }
        pages.value.pageNumber = 1
        // 设置审查结论默认值为"未通过"
        if (!filters.value.isPassed) {
          filters.value.isPassed = '未通过'
        }
        await getCircuitReviewResultFiltersFn()
        initPage()
      } else {
        versionFilters.record = ``
      }
    }
  }
  // 文件版本改变
  const initDetailData = () => {
    versionFilters.record = ``
    columns.value = []
    filteredDataAll.value = []
    filteredData.value = []
    distinctValues.value = {}
    filters.value = {}
    topInfoList[0].value = ``
    topInfoList[1].value = ``
    topInfoList[2].value = ``
    topInfoList[3].value = ``
  }
  const versionChangeFn = () => {
    initDetailData()
    circuitFileResultFn(versionFilters.version)
  }
  //审查记录改变
  const recordChangeFn = async () => {
    pages.value.pageNumber = 1
    // 设置审查结论默认值为"未通过"
    if (!filters.value.isPassed) {
      filters.value.isPassed = '未通过'
      await getCircuitReviewResultFiltersFn()
      filteredDataFn()
    } else {
      await getCircuitReviewResultFiltersFn()
      initPage()
    }
  }
  // 过滤项改变（用于处理 isPassed 改变时需要刷新联动下拉数据）
  const onFilterChange = async (key: string) => {
    // 任一联动项变化，统一带上四个下拉当前值请求过滤候选集
    await getCircuitReviewResultFiltersFn()
    filteredDataFn()
  }
  // 下载电路图文件
  const fileDownloadFn = () => {
    const item = versionOptions.value.find(item => item.id === versionFilters.version)
    if (item && item.minioId) {
      // 分离文件名和后缀
      const lastDotIndex = item.fileName.lastIndexOf('.')
      let fileNameWithoutExt, fileExt
      if (lastDotIndex > 0) {
        fileNameWithoutExt = item.fileName.substring(0, lastDotIndex)
        fileExt = item.fileName.substring(lastDotIndex) // 包含点号，如 .tel
      } else {
        fileNameWithoutExt = item.fileName
        fileExt = ''
      }
      
      // 组合新的文件名：文件名_部门名称_所有者名称.后缀
      const departmentName = rowItem.value.departmentName || ''
      const ownerName = rowItem.value.ownerName || ''
      const newFileName = `${fileNameWithoutExt}_${departmentName}_${ownerName}${fileExt}`
      window.open(`/circuitreview/common/v1/storage/download/${item.minioId}?fileId=${item.minioId}&fileName=${encodeURIComponent(newFileName)}`)
    }
  }
  // 粉碎版本文件
  const delFn = (item, type) => {
    circuitFileVersionFs([item]).then(res => {
      if (res.succ) {
        ElMessage.success(`粉碎该版本文件成功`)
        if (!type) {
          setTimeout(() => {
            versionFilters.version = ``
            versionFilters.record = ``
            initDetailData()
            circuitFileVersionFn(rowItem.value.id)
          }, 200)
        } else {
          router.push(`/documentReview`)
        }
      }
    })
  }
  const fileDeleteFn = () => {
    const item = versionOptions.value.find(item => item.id === versionFilters.version)
    if (item) {
      // 检查权限：admin可以粉碎任何文件，test-开头可以随时粉碎，其他正式文件必须关闭后才能粉碎
      const isAdmin = userName.value === 'admin'
      const isTestFile = item.fileName?.startsWith('test-')
      const isClosed = rowItem.value.isClosedLoop === 1

      if (!isAdmin && !isTestFile && !isClosed) {
        ElMessage.warning(`网表文件只有关闭后才能被粉碎!`)
        return
      }

      if (versionOptions.value.length === 1) {
        ElMessageBox.confirm("粉碎最后一个版本文件将粉碎整个网表文件，粉碎后将跳转到电路图列表页", "温馨提示", {
          customClass: `customClassElMessageBox`,
          confirmButtonText: "确认粉碎",
          showCancelButton: false,
          type: "warning"
        }).then(() => {
          delFn(item, 1)
        })
      } else {
        ElMessageBox.confirm(`确定粉碎该版本文件?`, `提示`, {
          "confirmButtonText": `确定`,
          "cancelButtonText": `取消`,
          "type": `warning`
        })
          .then(() => {
            delFn(item)
          })
          .catch(() => {})
      }
    }
    // if(versionOptions.value.length===1){
    //   ElMessageBox.confirm(`确定粉碎该版本文件?`, `提示`, {
    //       "confirmButtonText": `确定`,
    //       "cancelButtonText": `取消`,
    //       "type": `warning`
    //     })
    //       .then(() => {
    //         delFn(item)
    //     })
    //       .catch(() => {})
    // }else{
    //   const item = versionOptions.value.find(item=>item.id===versionFilters.version)
    //   if(item){
    //     ElMessageBox.confirm(`确定粉碎该版本文件?`, `提示`, {
    //       "confirmButtonText": `确定`,
    //       "cancelButtonText": `取消`,
    //       "type": `warning`
    //     })
    //       .then(() => {
    //         delFn(item)
    //     })
    //       .catch(() => {})
    //   }
    // }
  }
  // 导出Excel
  const exportTableExcelFn = () => {}
  // 删除审查记录
  const deleteReviewRecordFn = () => {
    ElMessageBox.confirm(`确定删除该审查记录?`, `提示`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
      .then(() => {
        delCircuitReviewResult({ id: versionFilters.record }).then(res => {
          if (res.succ) {
            ElMessage.success(`删除该审查记录成功`)
            setTimeout(() => {
              initDetailData()
              circuitFileResultFn(versionFilters.version)
            }, 200)
          }
        })
      })
      .catch(() => {})
  }
  
  const topInfoList: any = reactive([
    {
      name: `审查点数`,
      value: 0,
      img: scjcds
    },
    {
      name: `通过率`,
      value: 0,
      img: tgl
    },
    {
      name: `问题点数`,
      value: 0,
      img: wtg
    },
    {
      name: `状态`,
      value: ``,
      img: bhzt,
      class: ``
    }
  ])
  const rulesListDataLocal = ref([])
  // 获取审查规则列表
  getCircuitReviewRules().then(res => {
    if (res.succ) {
      rulesListDataLocal.value = res.content.map((r: any) => {
        return {
          "label": r.name.replace(/^\s+|\s+$|[。.；;!！?？:：,，、\[\]【】\{\}""']+$/gu, ``), // 去掉前后空格和结尾标点符号
          "status": `审查中`
        }
      })
    }
  })
  // 再次审查
  const isFinish = ref(false)
  const finishInfo = ref({})
  const showRuleModal = ref(false)
  const displayedRules = ref<string[]>([])
  const rulesListData = ref<any>([])
  const updatedRulesListData = ref<any>([])
  const isApiResponded = ref(false)
  const isShowingRules = ref(false)
  const currentRuleIndex = ref(0)
  const currentReviewFile = ref<any>({})
  let resultDisplayTimer = null
  const currentDisplayIndex = ref(0)
  const reviewResultsData = ref<any>([]) // 存储审查结果数据
  const currentPollFn = ref<(() => void) | null>(null) // 轮询函数引用，用于 visibilitychange 强制刷新
  let currentPollTimer: ReturnType<typeof setTimeout> | null = null

  // 页面切回时强制刷新审查进度
  const handleVisibilityChange = () => {
    console.log(`页面可见性改变: document.hidden = ${document.hidden}`)
    if (!document.hidden && showRuleModal.value) {
      if (currentPollFn.value) {
        console.log(`情况1：轮询还在跑，立即拉最新状态`)
        // 情况1：轮询还在跑，立即拉最新状态
        currentPollFn.value()
      } else if (reviewResultsData.value.length > 0) {
        // 情况2：后台已拿到结果但动画还没跑完，直接跳到结尾
        console.log(`情况2：后台已拿到结果但动画还没跑完，直接跳到结尾`)
        if (resultDisplayTimer) {
          clearInterval(resultDisplayTimer)
          resultDisplayTimer = null
        }
        reviewResultsData.value.forEach(result => {
          const targetRule = rulesListData.value.find(r => r.label === result.label)
          if (targetRule) {
            targetRule.status = result.status
          }
        })
        isFinish.value = true
        updatedRulesListData.value = reviewResultsData.value
        // 直接跳到结尾后，滚动到底部，让最后一行可见
        nextTick(() => {
          setTimeout(() => {
            const ruleList = document.querySelector(`.tableCont`)
            const lastRule = ruleList?.querySelector(`.el-scrollbar__wrap `)
            if (lastRule) {
              lastRule.scrollTop = lastRule.scrollHeight
            }
          }, 100)
        })
      }
    }
  }
  const generalCheckpoint = computed(() => {
    return updatedRulesListData.value.length
  })
  const passedCheckpoint = computed(() => generalCheckpoint.value - updatedRulesListData.value.filter(r => r.status.includes(`未通过`)).length)
  const passingRate = computed(() => {
    const passRate = (passedCheckpoint.value / generalCheckpoint.value) * 100
    return passRate.toFixed(1) + `%`
  })
  
  // 逐条展示审查结果的动画
  const startResultDisplayAnimation = () => {
    if (resultDisplayTimer) {
      clearInterval(resultDisplayTimer)
    }
    
    currentDisplayIndex.value = 0
    resultDisplayTimer = setInterval(() => {
      if (currentDisplayIndex.value < reviewResultsData.value.length) {
        // 滚动到当前显示的规则，保持当前行在可见范围的第10行
        const ruleList = document.querySelector(`.tableCont`)
        const lastRule = ruleList?.querySelector(`.el-scrollbar__wrap `)
        if (lastRule) {
          // 计算滚动位置，让当前行显示在可见范围的第10行
          // 如果当前行索引小于10，则保持顶部不变
          const targetScrollTop = Math.max(0, (currentDisplayIndex.value - 9) * 40)
          lastRule.scrollTop = targetScrollTop
        }
        
        // 更新当前行的状态为审查结果
        const currentResult = reviewResultsData.value[currentDisplayIndex.value]
        const targetRule = rulesListData.value.find(r => r.label === currentResult.label)
        if (targetRule) {
          targetRule.status = currentResult.status
        }
        
        currentDisplayIndex.value++
      } else {
        // 展示完成，清除定时器
        clearInterval(resultDisplayTimer)
        resultDisplayTimer = null
        isFinish.value = true
        
        // 更新最终的规则列表数据
        updatedRulesListData.value = reviewResultsData.value
        
        // 统计信息显示后，自动调整表格滚动位置，确保所有行都可见
        nextTick(() => {
          setTimeout(() => {
            const ruleList = document.querySelector(`.tableCont`)
            const lastRule = ruleList?.querySelector(`.el-scrollbar__wrap `)
            if (lastRule) {
              // 滚动到底部，让最后一行也可见
              lastRule.scrollTop = lastRule.scrollHeight
            }
          }, 100) // 等待统计信息渲染完成
        })
      }
    }, 200) // 每条显示500ms，提供足够时间查看结果
  }
  const reviewFn =async (name) => {
    const item:any = versionOptions.value.find(item => item.id === versionFilters.version)
    if(rowItem.value.isClosedLoop == 1){
      ElMessage.warning(`问题已关闭，无需再审查!`)  
      return true
    }
    if(name==`再次审查`){
       const res = await checkInAudit({fileId:item.fileId})
       console.log(`res`,res)
      // 复核中，不能再次审查
      if(res.succ && res.content.inAudit){
        ElMessage.warning(`复核中，不能再次审查!`)
        return true
      }
    }
    if (item && item.id) {
      isFinish.value = false
      showRuleModal.value = true
      isApiResponded.value = false
      currentReviewFile.value = item
      currentDisplayIndex.value = 0
      
      // 清除之前的定时器
      if (resultDisplayTimer) {
        clearInterval(resultDisplayTimer)
        resultDisplayTimer = null
      }
      
      rulesListData.value = JSON.parse(JSON.stringify(rulesListDataLocal.value)).map(item => ({
        ...item,
        "status": `审查中`
      }))
      isShowingRules.value = true
      nextTick(() => {
        const ruleList = document.querySelector(`.tableCont`)
        const lastRule = ruleList?.querySelector(`.el-scrollbar__wrap `)
        if (lastRule) {
          lastRule.scrollTop = 0
        }
      })
  
      item.loading = true
      createCircuitReview({ "fileVersionId": item.id })
        .then(res => {
          if (res.succ) {
            isApiResponded.value = true
            const reviewId = res.content
  
            // 轮询审查结果状态
            const pollReviewStatus = () => {
              getCircuitReviewResult(reviewId)
                .then(resultRes => {
                  if (resultRes.succ) {
                    const resultData = resultRes.content
  
                    if (resultData.status === `IN_PROGRESS`) {
                      // 审查还在进行中，继续轮询
                      currentPollTimer = setTimeout(pollReviewStatus, 2000) // 每2秒轮询一次
                    } else if (resultData.status === `FINISHED`) {
                      currentPollFn.value = null
                      if (currentPollTimer) {
                        clearTimeout(currentPollTimer)
                        currentPollTimer = null
                      }
                      finishInfo.value = resultData
                      // 审查完成，获取审查规则情况
                      getCircuitReviewRuleSummary(reviewId)
                        .then(summaryRes => {
                          if (summaryRes.succ) {
                            // 更新审查时间
                            item.reviewTime = resultData.reviewTime
                            currentReviewFile.value.reviewTime = item.reviewTime
                            currentReviewFile.value.resultId = reviewId
  
// 更新统计数据
                            item.total_cases = resultData.checkPoints
                            item.total_passed = resultData.passCheckPoints
                            item.formatted_percentage = resultData.passRate

                            // 存储审查结果数据，不直接更新显示
                            reviewResultsData.value = []
                            summaryRes.content.forEach((ruleItem: any) => {
                              const ruleName = ruleItem.rule.name.replace(/^\s+|\s+$|[。.；;!！?？:：,，、\[\]【】\{\}""']+$/gu, ``)
                              let status = `通过`
                              switch (ruleItem.summary) {
                                case `PASSED`:
                                  status = `通过`
                                  break
                                case `NOT_PASSED`:
                                  status = `未通过`
                                  break
                                case `NOT_APPLICABLE`:
                                  status = `不适用`
                                  break
                                default:
                                  status = `通过`
                              }
                              
                              reviewResultsData.value.push({
                                "label": ruleName,
                                status
                              })
                            })
                            
                            // 开始逐条展示审查结果
                            startResultDisplayAnimation()
                            
                            circuitFileResultFn(versionFilters.version)
                            item.loading = false
                          } else {
                            console.error(`获取审查规则情况失败:`, summaryRes.msg)
                            item.loading = false
                            showRuleModal.value = false
                          }
                        })
                        .catch(error => {
                          console.error(`获取审查规则情况出错:`, error)
                          item.loading = false
                          showRuleModal.value = false
                        })
                    } else if (resultData.status === `FAILED`) {
                      // 审查失败，结束轮询
                      console.log(`审查状态:`, resultData.status)
                      currentPollFn.value = null
                      if (currentPollTimer) {
                        clearTimeout(currentPollTimer)
                        currentPollTimer = null
                      }
                      item.loading = false
                      showRuleModal.value = false
                      ElMessage.error(resultData.errorMessage)
                    } else {
                      // 其他状态，结束轮询
                      console.log(`审查状态:`, resultData.status)
                      currentPollFn.value = null
                      if (currentPollTimer) {
                        clearTimeout(currentPollTimer)
                        currentPollTimer = null
                      }
                      item.loading = false
                      showRuleModal.value = false
                    }
                  } else {
                    console.error(`获取审查结果失败:`, resultRes.msg)
                    currentPollFn.value = null
                    if (currentPollTimer) {
                      clearTimeout(currentPollTimer)
                      currentPollTimer = null
                    }
                    item.loading = false
                    showRuleModal.value = false
                  }
                })
                .catch(error => {
                  console.error(`获取审查结果出错:`, error)
                  currentPollFn.value = null
                  if (currentPollTimer) {
                    clearTimeout(currentPollTimer)
                    currentPollTimer = null
                  }
                  item.loading = false
                  showRuleModal.value = false
                })
            }

            // 把轮询函数暴露给 visibilitychange 监听
            currentPollFn.value = () => {
              if (currentPollTimer) {
                clearTimeout(currentPollTimer)
                currentPollTimer = null
              }
              pollReviewStatus()
            }

            // 开始轮询
            currentPollFn.value()
          } else {
            item.loading = false
            showRuleModal.value = false
          }
        })
        .catch(() => {
          item.loading = false
          showRuleModal.value = false
        })
    }
  }
  const goReviewResults = () => {
    if (recordOptions.value && recordOptions.value.length) {
      versionFilters.record = recordOptions.value[0].id
      recordChangeFn()
      showRuleModal.value = false
    }
  }
  
  const initFn = async () => {
    // 从路由参数中获取审查结果数据
    if (item) {
      rowItem.value = JSON.parse(decodeURIComponent(item))
      console.log(`rowItem.value`, rowItem.value)
      console.log(`rowItem.value.fileVersionId`, rowItem.value.fileVersionId)
      // 获取审查规则过滤条件（若当前未选中审查记录将不执行）
      getCircuitReviewResultFiltersFn()
      circuitFileVersionFn(rowItem.value.id)
      if (rowItem.value.fileVersionId) {
        console.log(`rowItem.value.fileVersionId---`, rowItem.value.fileVersionId)
        versionFilters.version = rowItem.value.fileVersionId
        await circuitFileResultFn(versionFilters.version, rowItem.value.resultId)
      } else {
        versionFilters.version = ``
      }
  
      if (versionFilters.record) {
        recordChangeFn()
      }
    }
  }
  console.log(`initFn---`)
  initFn()
  // 复核相关变量
  const auditModalVisible = ref(false)
  const currentRow = ref(null)
  const auditConclusion = ref('')
  const showFeedbackInput = ref(false)
  const feedbackText = ref('')
  const auditFormRef = ref()
  const isSubmitting = ref(false)

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
    { label: '规则不适用', value: 'RULE_NOT_APPLICABLE' },
    { label: '问题可例外', value: 'ISSUE_EXCEPTION' },
    { label: '复查无影响', value: 'REVIEW_NO_IMPACT' },
    { label: '审查不正确', value: 'REVIEW_INCORRECT' }
  ]
  
  // 打开复核弹窗
  const openAuditModal = (row) => {
    currentRow.value = row
    auditConclusion.value = ''
    showFeedbackInput.value = false
    feedbackText.value = ''
    auditModalVisible.value = true
  }
  
  // 处理复核结论变化
  // const handleAuditConclusionChange = (value) => {
  //   if (value === '审查不正确') {
  //     showFeedbackInput.value = true
  //   } else {
  //     showFeedbackInput.value = false
  //     feedbackText.value = ''
  //   }
  // }
  
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

    if (!feedbackText.value?.trim()) {
      ElMessage.warning('必须填写问题描述')
      isSubmitting.value = false
      return
    }

    // 验证问题描述长度
    if (feedbackText.value?.length > 500) {
      ElMessage.warning('问题描述不能超过500个字符')
      isSubmitting.value = false
      return
    }

    try {
      console.log('提交复核:', currentRow.value.id, auditConclusion.value, feedbackText.value)
      const res = await submitAuditResult(
        currentRow.value.id,
        auditConclusion.value,
        feedbackText.value
      )
      if (res.succ) {
        // 关闭弹窗
        auditModalVisible.value = false
        ElMessage.success('复核提交成功')
        // 如果复核结论是"审查不正确"（被拒绝），自动打开显示所有列按钮
        if (auditConclusion.value === 'REVIEW_INCORRECT') {
          showAllColumns.value = true
        }
        // 刷新列表数据
        await fetchReviewResultDetails(pages.value.pageNumber, pages.value.pageSize)
        recordChangeFn()
      } else {
        // ElMessage.error(res.msg || '复核提交失败')
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
  }
  
  const { proxy } = getCurrentInstance() as any
  const feedback = () => {
    const itemVersion: any = versionOptions.value.filter((r: any) => r.id === versionFilters.version)
    if (itemVersion && itemVersion.length) {
      proxy.setSession(`reviewResultData`, JSON.stringify({...rowItem.value,fileVersionId:versionFilters.version,record:versionFilters.record}))
      const queryData = {
        title: `[电路图 <${itemVersion[0].fileName}> 存在的问题]`,
        appendFileList: [
          {
            fileName: itemVersion[0].fileName,
            fileId: itemVersion[0].minioId
          }
        ]
      }
      router.push({
        "path": `/feedback`,
        "query": {
          "queryData": JSON.stringify(queryData),
          "record": versionFilters.record,
          "version": versionFilters.version,
          "fileId": rowItem.value.id
        }
      })
    }
  }
  
  onMounted(() => {
    document.addEventListener(`visibilitychange`, handleVisibilityChange)
  })
  
  onUnmounted(() => {
    // 组件卸载时清理定时器
    if (resultDisplayTimer) {
      clearInterval(resultDisplayTimer)
      resultDisplayTimer = null
    }
    if (currentPollTimer) {
      clearTimeout(currentPollTimer)
      currentPollTimer = null
    }
    currentPollFn.value = null
    document.removeEventListener(`visibilitychange`, handleVisibilityChange)
  })
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="reviewResults" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
      <localTitle title="审查结果">
        <template #rightBtn>
          <el-button type="warning" @click="feedback">意见反馈</el-button>
          <el-button @click="goBack">返回</el-button>
        </template>
      </localTitle>
  
      <div class="fileName">
        <div class="label"> 电路图名称：</div>
        <el-text truncated>{{ rowItem.tel_file }}</el-text>
        <!-- <div :class="{ wbh: rowItem.isClosedLoop === 0, ybh: rowItem.isClosedLoop === 1 }" size="small" style="cursor: default">
          {{ rowItem.isClosedLoop === 1 ? `问题已关闭` : `问题未关闭` }}
        </div> -->
      </div>
  
      <div class="versionSearchBox">
        <div class="left">
          <div class="label">文件版本</div>
          <el-tooltip :content="getSelectedVersionFileName()" placement="top" :show-after="200" effect="light">
            <el-select v-model="versionFilters.version" placeholder="请选择" effect="light" popper-class="version-select-popper" @change="versionChangeFn">
              <el-option v-for="option in versionOptions" :key="option.id" :label="`${option.fileName} ${option.createDate}`" :value="option.id">
                <span class="version-option-text" :title="option.fileName + ' ' + option.createDate">{{ option.fileName }} {{ option.createDate }}</span>
              </el-option>
            </el-select>
          </el-tooltip>
          <el-button type="primary" size="small" class="searchBtn" :disabled="versionFilters.version === ``" @click="reviewFn(recordOptions.length ? `再次审查` : `审查`)">{{ recordOptions.length ? `再次审查` : `审查` }}</el-button>
          <el-button type="primary" size="small" class="searchBtn" :disabled="versionFilters.version === ``" @click="fileDownloadFn">下载</el-button>
          <el-button type="danger" size="small" class="searchBtn" :disabled="versionFilters.version === ``" @click="fileDeleteFn">粉碎版本文件</el-button>
        </div>
        <div class="right">
          <div class="label">审查记录</div>
          <el-select v-model="versionFilters.record" effect="light" :disabled="versionFilters.version === ``" placeholder="请选择" @change="recordChangeFn">
            <el-option v-for="option in recordOptions" :key="option.id" :label="`${option.reviewTime} 通过率:${parseInt(option.passRate * 10000) / 100}%`" :value="option.id" />
          </el-select>
          <el-button v-loading="onImportLoading" type="primary" size="small" class="searchBtn" :disabled="versionFilters.record === ``" @click="onImportTable">导出审查报告</el-button>
          <el-button v-if="userName === `机载管理员` || userName === `admin`" type="danger" size="small" class="searchBtn" :disabled="versionFilters.record === ``" @click="deleteReviewRecordFn">
            删除审查记录
          </el-button>
        </div>
      </div>
  
      <div class="num">
        <div v-for="(items, index) in topInfoList" :key="index" :class="`itemBox ` + `itemBox${index}`">
          <div class="left">
            <img :src="items.img" alt="" />
            <span class="label">{{ items.name }}</span>
          </div>
          <span :class="`value ` + items.class">{{ items.value != null && items.value !== '' ? items.value : 0 }}</span>
        </div>
      </div>
      <div class="tableBox">
        <div class="search">
          <el-form ref="ruleFormRef" :inline="true">
            <el-form-item v-for="(values, key) in distinctValues" :key="key" :label="getTableColumnName(key as string)">
              <el-select v-if="values && values.length" v-model="filters[key as string]" effect="light" placeholder="全部" style="width: 130px" @change="onFilterChange(key as string)">
                <!-- <el-option v-for="option in isPassedOptions" v-if="key === 'isPassed'" :key="option.display" :label="option.display" :value="option.display" /> -->

                <el-option v-for="value in values" :key="value" :label="value" :value="value">
                  <el-tooltip class="box-item" :content="value" placement="bottom-start" :disabled="value.length < 30" effect="light">
                    {{ value }}
                  </el-tooltip>
                </el-option>
              </el-select>
              <el-input v-else v-model="filters[key as string]" style="width: 130px" placeholder="请输入" />
            </el-form-item>
          </el-form>
          <div style="display: flex; align-items: center;">
            <div v-if="needShowAllColumnsBtn" style="display: flex; align-items: center; margin-right: 12px; gap: 8px;">
              <span style="font-size: 15px; color: #606266;">显示所有列</span>
              <el-switch v-model="showAllColumns" style="--el-switch-on-color: #67c23a; --el-switch-off-color: #dcdfe6; --el-switch-size: 20px;" size="large" />
            </div>
            <el-button :disabled="!versionFilters.record" type="primary" @click="filteredDataFn()">搜索</el-button>
            <el-button :disabled="!versionFilters.record" @click="restFn">重置</el-button>
          </div>
        </div>
        <div class="tableWrapper">
          <el-table
            border
            :data="filteredData"
            class="upload-demo12"
            tooltip-effect="light"
            :header-cell-style="{ 'text-align': 'center' }"
            :cell-style="{ 'vertical-align': 'middle' }"
            :fit="true"
            table-layout="fixed"
            style="width: 100%"
          >
            <el-table-column :label="columns.length ? `序号` : ``" align="center" header-align="center" width="64px" fixed class-name="center-aligned number-column">
              <template #default="scope">
                {{ (pages.pageNumber - 1) * pages.pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column
              v-for="(column, index) in columns"
              :key="index"
              :prop="column.prop"
              :label="column.name"
              :fixed="index < 2 ? 'left' : false"
              :align="getColumnAlign(column)"
              :min-width="getColumnWidth(index)"
              :class-name="getColumnClassName(column)"
              :show-overflow-tooltip="column.name !== '位号管脚'"
            >
              <template #default="scope">
                <template v-if="column.name === '位号管脚'">
                  <el-popover v-if="scope.row.tagPin" placement="bottom" trigger="hover" :popper-style="{ width: `500px` }">
                    <template #reference>
                      <div class="cellTextClamp3">
                        <span v-for="(item, index) in splitPinValues(scope.row[column.prop])" :key="index" style="display: inline">
                          <a href=" " @click.prevent="Run('comp_Pin', item.trim().replace(/\(/g, '').replace(/\)/g, '').replace(/'/g, ''))">
                            {{ item.trim().replace(/\(/g, "").replace(/\)/g, "").replace(/'/g, "") }}
                          </a>
                          <span v-if="splitPinValues(scope.row[column.prop]) && index < splitPinValues(scope.row[column.prop]).length - 1">, </span>
                        </span>
                      </div>
                    </template>
                    <div class="reviewSuggestionBox">
                      <span v-for="(item, index) in splitPinValues(scope.row[column.prop])" :key="index">
                        <a href=" " @click.prevent="Run('comp_Pin', item.trim().replace(/\(/g, '').replace(/\)/g, '').replace(/'/g, ''))">
                          {{ item.trim().replace(/\(/g, "").replace(/\)/g, "").replace(/'/g, "") }}
                        </a>
                        <span v-if="splitPinValues(scope.row[column.prop]) && index < splitPinValues(scope.row[column.prop]).length - 1">, </span>
                      </span>
                    </div>
                  </el-popover>
                  <span v-else> - </span>
                </template>
                <template v-else-if="column.name === '审查规则'">
                  <div class="cellTextClamp3">{{ scope.row[column.prop] }}</div>
                </template>
                <template v-else-if="column.name === '审查意见'">
                  <div v-if="scope.row.isPassed !== 1" class="cellTextClamp3">{{ scope.row[column.prop] }}</div>
                  <div v-else>无问题</div>
                </template>
                <!-- <template v-else-if="column.name === '审查意见'">
                  <el-popover v-if="scope.row.isPassed !== 1" placement="bottom" trigger="hover" :popper-style="{ width: `500px` }">
                    <template #reference>
                      <div class="cellTextClamp3">
                        <template v-for="(part, partIndex) in processReviewComment(scope.row[column.prop])" :key="partIndex">
                          <a v-if="part.type === 'component'" href="javascript:void(0)" style="color: #409eff; text-decoration: underline" @click.prevent="Run('PIN', part.content)">
                            {{ part.content }}
                          </a>
                          <template v-else>{{ part.content }}</template>
                        </template>
                      </div>
                    </template>
                    <div class="reviewSuggestionBox">
                      <template v-for="(part, partIndex) in processReviewComment(scope.row[column.prop])" :key="partIndex">
                        <a v-if="part.type === 'component'" href="javascript:void(0)" style="color: #409eff; text-decoration: underline" @click.prevent="Run('PIN', part.content)">
                          {{ part.content }}
                        </a>
                        <template v-else>{{ part.content }}</template>
                      </template>
                    </div>
                  </el-popover>
                  <span v-else>无问题</span>
                </template> -->
                <template v-else-if="column.name === '审查结论'">
                  <div class="isPass">
                    <span v-if="scope.row[column.prop] === 0"> <i class="iconfont icon-butongguo1" />未通过 </span>
                    <span v-if="scope.row[column.prop] === 1"> <i class="iconfont icon-chenggong" />通过 </span>
                  </div>
                </template>
                <template v-else-if="column.name === '复核结论'">
                  <div v-if="scope.row[column.prop]" class="cellTextClamp3">{{ scope.row[column.prop] }}</div>
                  <div v-else> - </div>
                </template>
                <template v-else-if="column.name === '器件型号'">
                  <div v-if="scope.row[column.prop]" class="cellTextClamp3">{{ scope.row[column.prop] }}</div>
                  <div v-else> - </div>
                </template>
                <template v-else-if="column.name === '问题描述'">
                  <div v-if="scope.row[column.prop]" class="cellTextClamp3">{{ scope.row[column.prop] }}</div>
                  <div v-else> - </div>
                </template>
                <template v-else>
                  {{ scope.row[column.prop] }}
                </template>
              </template>
            </el-table-column>

            <!-- 操作列 -->
            <el-table-column
              label="操作"
              width="120px"
              align="center"
            >
              <template #default="scope">
                <div style="display: flex;justify-content: center;width: 100%;">
                <el-button
                      v-if="scope.row.auditTypeStatus == 'DOING'"
                    :disabled="scope.row.auditTypeStatus == 'DOING'"
                    type="primary"
                    size="small"
                    class="searchBtn"
                    @click="openAuditModal(scope.row)"
                    >
                  复核中
                  </el-button>

                <el-button
                      v-else-if="scope.row.auditTypeStatus == 'CANCELLED'"
                    :disabled="scope.row.auditTypeStatus == 'CANCELLED'"
                    type="primary"
                    size="small"
                    class="searchBtn"
                    @click="openAuditModal(scope.row)"
                    >
                  复核取消
                  </el-button>
                  <el-button
                      v-else
                    :disabled="scope.row.isPassed !== 0 || scope.row.auditTypeStatus == 'FINISH'"
                    type="primary"
                    size="small"
                    class="searchBtn"
                    @click="openAuditModal(scope.row)"
                    >
                      {{ scope.row.auditTypeStatus == 'FINISH' ? '复核完成' : '申请复核' }}
                  </el-button>
                </div>
              </template>
            </el-table-column>

            <!-- 问题描述列 -->
            <el-table-column
              v-if="showQuestionDescColumn"
              label="问题描述"
              align="left"
              :min-width="getQuestionDescWidth()"
              show-overflow-tooltip
            >
              <template #default="scope">
                <div v-if="scope.row.issueFeedback" class="cellTextClamp3">{{ scope.row.issueFeedback }}</div>
                <div v-else> - </div>
              </template>
            </el-table-column>

            <!-- 复核结论列 -->
            <el-table-column
              v-if="showRecheckConclusionColumn"
              label="复核结论"
              align="left"
              :min-width="getRecheckConclusionWidth()"
              show-overflow-tooltip
            >
              <template #default="scope">
                <div v-if="scope.row.approvedAuditType" class="cellTextClamp3">{{ scope.row.approvedAuditType }}</div>
                <div v-else> - </div>
              </template>
            </el-table-column>   
          </el-table>
        </div>
        <div class="pagesBox">
          <el-pagination
            v-if="columns.length"
            v-model:current-page="pages.pageNumber"
            v-model:page-size="pages.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50, 100]"
            :total="pages.total"
            @current-change="initPage"
            @size-change="initPage"
          />
        </div>
      </div>
  
      <!-- 审查规则动画弹窗 -->
      <el-dialog v-model="showRuleModal" class="dialogCont progressDialog" title="" :show-close="false" width="1200px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">审查进度</div>
            <div class="right status">{{ isFinish ? `审查完成` : `审查进行中` }}</div>
          </div>
        </template>
        <div class="contBox">
          <div class="tableCont">
            <el-table ref="rulesListRef" border tooltip-effect="light" :data="rulesListData" height="100%">
              <el-table-column type="index" label="序号" align="center" width="100" />             
              <el-table-column prop="label" label="规则名称" align="left" min-width="400px" show-overflow-tooltip>
                <template #default="scope">
                  {{ scope.row.label && !scope.row.label.endsWith('。') ? scope.row.label + '。' : scope.row.label }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="审查结果" align="center" width="140px" class-name="status">
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
          <div v-if="isFinish && !resultDisplayTimer" class="numInfo">
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/scds.png" alt="" />
              </div>
              <div class="value">
                <span class="label">审查规则数</span>
                <span> {{ generalCheckpoint }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/ztg.png" alt="" />
              </div>
              <div class="value">
                <span class="label">通过规则数</span>
                <span> {{ passedCheckpoint }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/zscs.png" alt="" />
              </div>
              <div class="value">
                <span class="label">审查点数</span>
                <span> {{ finishInfo.checkPoints }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/wtg.png" alt="" />
              </div>
              <div class="value">
                <span class="label">问题点数</span>
                <span> {{ finishInfo.failCheckPoints }} </span>
              </div>
            </div>
            <div>
              <div class="left">
                <img src="@/assets/images/circuitReview/tgl.png" alt="" />
              </div>
              <div class="value">
                <span class="label">通过率</span>
                <span> {{ Math.round(finishInfo.passRate * 1000) / 10 + `%` }} </span>
              </div>
            </div>
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
    <el-dialog v-model="auditModalVisible" title="申请复核" width="500px" :close-on-click-modal="false">
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
          placeholder="请输入问题描述"
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
</div>
</template>
  
  <style lang="scss" scoped>
  .reviewResults {
    position: relative;
    display: flex;
    flex-direction: column;
    .fileName {
      height: 56px;
      background: #d1e1f4;
      border-radius: 20px 20px 0px 0px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      color: #3a4c5f;
      .el-text{
        color: #0462e3;
        line-height: 21px;
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
      border-radius: 0 0 20px 20px;
      height: 90px;
      display: flex;
      align-items: center;
      > div {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
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
          font-size: 15px;
        }
      }
      .left {
        border-right: 1px solid #b1bfcf;
      }
    }
  
    .num {
      width: 100%;
      margin: 28px 0;
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
        background: linear-gradient(123deg, #b5e2f0, #c6e5f6);
        box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
        border-radius: 20px;
        padding: 24px 40px;
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
          .label {
            font-size: 15px;
            color: #3a4c5f;
            line-height: 23px;
            margin-left: 30px;
          }
        }
        .value {
          font-size: 28px !important;
          color: #6299f5;
          line-height: 36px;
          &.wbh {
            color: #e39e25 !important;
            font-size: 24px !important;
          }
          &.ybh {
            color: #22c9a8 !important;
            font-size: 24px !important;
          }
        }
  
  
        &.itemBox1 {
          background: linear-gradient(123deg, #c7d6f5, #cde4fb);
          box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value {
            color: #6299f5;
          }
        }
        &.itemBox2 {
          background: linear-gradient(123deg, #eeddbc, #eee2c2);
          box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value {
            color: #d9a732;
          }
        }
        &.itemBox3 {
          background: linear-gradient(123deg, #c1d9f4, #c7e0f9);
          box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
          .value {
            color: #41aaff;
            font-size: 26px;
          }
        }
      }
    }
    .tableBox {
      flex: 1;
      .tableWrapper {
        width: 100%;
        overflow-x: auto;
      }
      .tableWrapper :deep(.el-table) {
        width: 100% !important; // keep the table stretched even after manual column resize
        min-width: 1200px;
      }
      .tableWrapper :deep(.el-table__inner-wrapper),
      .tableWrapper :deep(.el-table__header-wrapper table),
      .tableWrapper :deep(.el-table__body-wrapper table) {
        width: 100% !important;
      }
      .search {
        height: 100px;
        padding: 0 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #fff;
        :deep(.el-form) {
          .el-form-item {
            margin-bottom: 0;
            margin-right: 20px;
            .el-form-item__label {
              padding-right: 10px;
              font-size: 15px;
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
        .el-table__body td .cell{
          white-space: normal;
          word-break: break-word;
        }
        .el-table__body td .cell.center-aligned {
          justify-content: center;
        }
        .el-table__body td .cell.left-aligned {
          justify-content: flex-start;
        }
        .el-table__body tr .number-column {
          text-align: center;
        }
        .el-table__body tr .number-column .cell {
          justify-content: center !important;
        }
        .el-table__body tr,
        .el-table__body td {
          height: 78px;
        }
        .cellTextClamp3 {
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 3;
          overflow: hidden;
          white-space: normal;
          word-break: break-word;
          line-height: 26px;
          max-height: 78px;
          align-self: center;
        }
        // 修改 tooltip 主题为 light
        .el-tooltip__popper.is-light {
          background-color: #fff;
          border: 1px solid #e4e7ed;
  
          .el-tooltip__arrow::before {
            border: 1px solid #e4e7ed;
            background: #fff;
          }
        }
        background-color: transparent;
        //height: calc(100vh - 150px);
        .opt {

          .btn {
            height: 28px;
            line-height: 28px;
            padding: 0 4px;
            min-width: 70px;
            text-align: center;
          }
        }
        //max-height: 800px;
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
              background: linear-gradient(123deg, #b5e2f0, #c6e5f6);
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
                background: linear-gradient(123deg, #c7d6f5, #cde4fb);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #6299f5;
                }
              }
              &:nth-child(3) {
                background: linear-gradient(123deg, #c1d9f4, #c7e0f9);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #7e71ff;
                }
              }
              &:nth-child(4) {
                background: linear-gradient(123deg, #f3f1ed, #f1e6ce);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #d9a732;
                }
              }
              &:nth-child(5) {
                margin-right: 0;
                background: linear-gradient(123deg, #eeddbc, #eee2c2);
                box-shadow: 1px 3px 30px 0px rgba(3, 13, 28, 0.03);
                span {
                  color: #d9a732;
                }
              }
            }
          }
        }
      }
  
      &.progressDialog {
        margin-top: 15vh;
        .el-dialog__body {
          height: 60vh;
        }

        .contBox {
          display: flex;
          flex-direction: column;

          .tableCont {
            flex: 1;
            height: auto;
            min-height: 260px;
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
    a {
      color: #409eff;
      text-decoration: none;
  
      &:hover {
        color: #66b1ff;
        text-decoration: underline;
      }
    }
  }
  .reviewSuggestionBox1{
    max-height: 100px;
  }
  </style>
  