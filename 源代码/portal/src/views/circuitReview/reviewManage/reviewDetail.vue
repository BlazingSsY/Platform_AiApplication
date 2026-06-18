<script setup lang="ts">
  import { h } from 'vue'
  import scjcds from "@/assets/images/circuitReview/zscs.png"
  import wtg from "@/assets/images/circuitReview/wtg.png"
  import bhzt from "@/assets/images/circuitReview/bhzt.png"
  import tgl from "@/assets/images/circuitReview/tgl.png"
  import localTitle from "../components/localTitle.vue"
  import {
    getCircuitReviewResultDetailPage,
    getCircuitReviewResultFilters,
    circuitFileVersion,
    delCircuitReviewResult,
    circuitFileResult,
    circuitFileVersionFs,
    getCircuitReviewResult,
    createCircuitReview,
    getCircuitReviewRuleSummary,
    getCircuitReviewRules,
    getCircuitFile,
    getCircuitFileVersion,
    // circuitReviewResultDetailAuditPage,
    circuitReviewResultDetailAuditPageForExpert,
    circuitReviewResultDetailAuditPageForAdmin,
    circuitReviewResultEditAudit  
  } from "@/ajax/circuitreview"
  import store from "@/store/index"
  const isAdmin = computed(() => store.state.user?.userInfo?.role?.name===`管理员`)
  const isExpert = computed(() => store.state.user?.userInfo?.role?.name===`专家`)
  console.log('isAdmin',isAdmin.value, 'isExpert', isExpert.value)
  const userInfo = computed(() => store.state.user.userInfo)
  const userName = computed(() => userInfo.value?.name)
  
  const loading = ref(false)
  const route = useRoute()
  const { fileName,fileVersionName,id,auditSubmitTime, fileId, fileVersionId, isRecycle }: any = route.query
  console.log('route.query:', { fileName,fileVersionName,id,auditSubmitTime, fileId, fileVersionId, isRecycle })
  const rowItem = ref<any>({})
  
  const deviceTypeList = ref([`全部`])
  const reviewRuleList = ref([`全部`])
  const ruleTypeList = ref([`全部`, `建议规则`, `必要规则`, `强制规则`])
  const isPassedList = ref([`全部`, `通过`, `未通过`])
  const fileObj = ref<any>({})
  const fileVersionObj = ref<any>({})
  // 调用 getCircuitFile 打印结果
  if (fileId && isRecycle !== `1`) {
    getCircuitFile({ id: fileId }).then((res: any) => {      
      fileObj.value = { ...res.content,
        fileName,
        fileVersionName,
        id: fileId, // goto审查结果页面时用的id
        fileId,
        fileVersionId,
        tel_file: fileName,
        fromPath: `/reviewDetail`,
      }
      console.log('getCircuitFile 结果:', res, fileObj.value)
    })
  }
  // 调用 getCircuitFileVersion 打印结果
  if (fileVersionId && isRecycle !== `1`) {
    getCircuitFileVersion({ id: fileVersionId }).then((res: any) => {      
      fileVersionObj.value = res.content
      console.log('getCircuitFileVersion 结果:', res, fileVersionObj.value)
    })
  }

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
  
  // 定义表格列映射 - 按照显示顺序定义
  const columnOrder = computed(() => {
    const baseOrder = [`ruleType`,`ruleName`, `deviceType`, `tagPin`, `reviewSuggestion`, `isPassed`, `status`]
    // if (isAdmin.value) {
    //   return [...baseOrder, `issueFeedback`]
    // } else if (isExpert.value) {
    //   return [...baseOrder, `auditType`]
    // }
    return [...baseOrder, `auditType`, `auditCloseTime`, `auditUserName`, `issueFeedback`]
    // return baseOrder
  })
  const columnMapping = {
    "ruleType": `规则类型`,
    "ruleName": `审查规则`,
    "deviceType": `器件型号`,
    "tagPin": `位号管脚`,
    "reviewSuggestion": `审查意见`,
    "isPassed": `审查结论`,
    "status": `复核状态`,
    "issueFeedback": `问题描述`,
    "auditType": `复核结论`,
    "auditCloseTime": `复核时间`,
    "auditUserName": `复核人`
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
  const filteredDataAll = ref<any>([])

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
          "approvedAuditType": auditTypeMapping[item.approvedAuditType] || item.approvedAuditType // 转换复核结论为中文
        }))
        filteredData.value = records
  
        pages.value.total = res.content.total
        // 如果是第一次加载，初始化过滤选项
        if (columns.value && columns.value.length === 0) {
          // 为每个字段生成过滤选项
          columnOrder.value.forEach(key => {
            let values: string[] = []
            if (key !== 'approvedAuditType' && key !== 'issueFeedback' && key !== 'auditType') {
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
  
  const initPage = val => {
    fetchReviewResultDetails(pages.value.pageNumber, pages.value.pageSize)
    getCircuitReviewResult(versionFilters.record).then(res => {
      if (res.succ) {
        topInfoList[0].value = res.content.checkPoints
        topInfoList[1].value = res.content.passRate ? (res.content.passRate * 100).toFixed(1) + "%" : `-`
        topInfoList[2].value = res.content.failCheckPoints
        topInfoList[3].value = res.content.isClosedLoop === 1 ? "问题已关闭" : "问题未关闭"
        topInfoList[3].class = res.content.isClosedLoop === 1 ? "ybh" : "wbh"
      }
    })
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
    // 列顺序：序号、规则类型、审查规则、器件型号、位号管脚、审查意见、审查结论、复核状态、复核结论、问题描述
    if ([0].includes(index)) {
      return `86` // 规则类型列
    }else if ([1, 3].includes(index)) {
      return `200` // 器件型号列、位号管脚列
    } else if ([2].includes(index)) {
      return `140` // 审查规则列、审查意见列
    } else if ([5].includes(index)) {
      return `100` // 审查规则列、审查意见列
    }else if ([6].includes(index)) {
      return `100` // 审查规则列、审查意见列
    }else if ([4].includes(index)) {
      return `220` // 审查规则列、审查意见列
    } else if ([7].includes(index)) {
      return `120` // 复核结论列
    } else if ([8].includes(index)) {
      return `100` // 复核时间列
    } else if ([9].includes(index)) {
      return `90` // 复核人列
    } else if ([10].includes(index)) {
      return `120` // 问题描述列
    } else {
      return `auto` // 其他列
    }
  }
  const getColumnAlign = (column: { prop: string }) => {
    // 只有规则类型列居中，其他列（包括位号管脚、审查规则、器件型号、审查意见）都左对齐
    if (column.prop === 'ruleType'||column.prop === 'status') {
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
  const getShowOverflowTooltip = (column: { prop: string }) => {
    // 对于已经使用 popover 的列，不需要 show-overflow-tooltip
    const popoverColumns = ['tagPin']
    if (popoverColumns.includes(column.prop)) {
      return false
    }
    return true
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
  const onImportLoading = ref(false)
  const router = useRouter()
  const goBack = () => {
    router.push({ path: `/reviewList` })     
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
      }
    })
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
      window.open(`/circuitreview/common/v1/storage/download/${item.minioId}?fileId=${item.minioId}&fileName=${encodeURIComponent(item.fileName)}`)
    }
  }

  // 下载电路图版本文件
  const fileDownloadVersionFn = () => { 
    if (isRecycle ==1) {
      ElMessage.warning(`相应文件已被粉碎`)
      return
    }   
    if (fileVersionObj.value && fileVersionObj.value.minioId) {
      window.open(`/circuitreview/common/v1/storage/download/${fileVersionObj.value.minioId}?fileId=${fileVersionObj.value.minioId}&fileName=${encodeURIComponent(fileVersionObj.value.fileName)}`)
    }
  }
  const gotoReviewResult = () => { 
    if (isRecycle ==1) {
      ElMessage.warning(`相应文件已被粉碎`)
      return
    } 
    if (fileObj.value) {
      // 通过路由参数传递数据，而不是使用localStorage
      // store.commit("SET_PAGES_INFO_STROE", { path: route.path, info: { ...pagination.value, searchObj } })
      console.log(`fileObj.value`, fileObj.value)
      router.push({
        "path": `/reviewResults`,
        "query": {
          "item": encodeURIComponent(JSON.stringify({...fileObj.value, fileVersionId: fileObj.value.fileVersionId*1})),
          path:`/reviewDetail`
        }
      })
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
  const reviewFn = () => {
    const item = versionOptions.value.find(item => item.id === versionFilters.version)
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
                      setTimeout(pollReviewStatus, 2000) // 每2秒轮询一次
                    } else if (resultData.status === `FINISHED`) {
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
                      // 其他状态，结束轮询
                      console.log(`审查状态:`, resultData.status)
                      item.loading = false
                      showRuleModal.value = false
                      ElMessage.error(resultData.errorMessage)
                    } else {
                      // 其他状态，结束轮询
                      console.log(`审查状态:`, resultData.status)
                      item.loading = false
                      showRuleModal.value = false
                    }
                  } else {
                    console.error(`获取审查结果失败:`, resultRes.msg)
                    item.loading = false
                    showRuleModal.value = false
                  }
                })
                .catch(error => {
                  console.error(`获取审查结果出错:`, error)
                  item.loading = false
                  showRuleModal.value = false
                })
            }
  
            // 开始轮询
            pollReviewStatus()
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
  


  // --------列表
  const filteredData = ref<any>([])
  const pages = ref({
    "pageSize": 100,
    "pageNumber": 1,
    "total": 1
  })
  const getCircuitReviewResultDetailAuditPageFn = (id) => {
    const apiCall = isAdmin.value ? circuitReviewResultDetailAuditPageForAdmin : circuitReviewResultDetailAuditPageForExpert
    apiCall({...pages.value, resultAuditId:id}).then(res=>{
       if (res.succ) {
        // 复核结论映射
        const auditTypeMapping: Record<string, string> = {
          "RULE_NOT_APPLICABLE": "规则不适用",
          "ISSUE_EXCEPTION": "问题可例外",
          "REVIEW_NO_IMPACT": "复查无影响",
          "REVIEW_INCORRECT": "审查不正确"
        }
        filteredData.value = res.content.records.map(item => ({
          ...item,
          ruleType: ruleTypeMapping[item.ruleType] || item.ruleType, // 转换规则类型为中文
          // auditType: auditTypeMapping[item.auditType] || item.auditType, // 转换复核结论为中文
          "auditType": (() => {
            if (item.status !=='REJECTED') {
              return auditTypeMapping[item.auditType] || item.auditType
            } else {
              const rejectType = auditTypeMapping[item.auditType] || item.auditType || '-'
              const auditUser = item.auditUserName || '-'
              const rejectReason = item.rejectReason || '-'
              let auditTypeDisplay = `已拒绝。复核类型: ${rejectType}; 复核人: ${auditUser}; 拒绝理由: ${rejectReason}`
              return auditTypeDisplay
            }
          })()
        }))
        pages.value.total = res.content.total

         if (columns.value && columns.value.length === 0) {
          // 为每个字段生成过滤选项
          columnOrder.value.forEach(key => {
            let values: string[] = []
            if (key !== 'approvedAuditType' && key !== 'issueFeedback' && key !== 'auditType') {
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
    })
  } 

  const initFn = async () => {
    if(id){
       columns.value = []
      getCircuitReviewResultDetailAuditPageFn(id)
    }
    // 从路由参数中获取审查结果数据
    // if (item) {
    //   rowItem.value = JSON.parse(decodeURIComponent(item))
    //   console.log(`rowItem.value`, rowItem.value)
    //   console.log(`rowItem.value.fileVersionId`, rowItem.value.fileVersionId)
    //   // 获取审查规则过滤条件（若当前未选中审查记录将不执行）
    //   getCircuitReviewResultFiltersFn()
    //   circuitFileVersionFn(rowItem.value.id)
    //   if (rowItem.value.fileVersionId) {
    //     versionFilters.version = rowItem.value.fileVersionId
    //     await circuitFileResultFn(versionFilters.version, rowItem.value.resultId)
    //   } else {
    //     versionFilters.version = ``
    //   }
  
    //   if (versionFilters.record) {
    //     recordChangeFn()
    //   }
    // }
  }
  initFn()

  const handleApprove = (rowItem,type) => {
    if (type === 'APPROVE') {
      ElMessageBox.confirm(`确认通过复核吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        circuitReviewResultEditAudit({id:rowItem.id,auditActionType :type}).then(res=>{
          if (res.succ) {
            ElMessage.success('提交通过复核成功')
            getCircuitReviewResultDetailAuditPageFn(id)
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
            circuitReviewResultEditAudit({id:rowItem.id,auditActionType :type,rejectReason:value}).then(res=>{
              if (res.succ) {
                ElMessage.success('提交拒绝复核成功')
                getCircuitReviewResultDetailAuditPageFn(id)
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
  
  onUnmounted(() => {
    // 组件卸载时清理定时器
    if (resultDisplayTimer) {
      clearInterval(resultDisplayTimer)
      resultDisplayTimer = null
    }
  })
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="reviewResults" element-loading-background="rgba(122, 122, 122, 0.8)" element-loading-text="数据加载中...">
      <localTitle title="问题复核详情">
        <template #rightBtn>
          <el-button @click="goBack">返回</el-button>
        </template>
      </localTitle>
  
      <div class="fileName">
        电路图名称：<span>{{ fileName }}</span>
      </div>
  
      <div class="versionSearchBox">
        <div class="left">
          <div class="label">文件版本:</div>
          {{ fileVersionName }}
          &nbsp;&nbsp;&nbsp; <el-button type="primary" size="small" class="searchBtn" @click="fileDownloadVersionFn">下载</el-button>
        </div>
        <div class="right">
          <div class="label">复核提交时间:</div>
          {{ auditSubmitTime }}
          &nbsp;&nbsp;&nbsp; <el-button type="primary" size="small" class="searchBtn" @click="gotoReviewResult">审查结果</el-button>
        </div>
      </div>
  
      <div class="tableBox">
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
            <el-table-column :label="columns.length ? `序号` : ``" align="center" header-align="center" width="64px" class-name="center-aligned number-column">
              <template #default="scope">
                {{ (pages.pageNumber - 1) * pages.pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column
              v-for="(column, index) in columns"
              :key="index"
              :prop="column.prop"
              :label="column.name"
              :align="getColumnAlign(column)"
              :min-width="getColumnWidth(index)"
              :class-name="getColumnClassName(column)"
              :show-overflow-tooltip="getShowOverflowTooltip(column)"
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
                <template v-else-if="column.name === '复核状态'">
                    <div v-if="scope.row[column.prop] === `IN_PROCESS`" class="isPass"> 复核中 </div>
                    <div v-if="scope.row[column.prop] === `APPROVED`" class="isPass"> 复核通过 </div>
                    <div v-if="scope.row[column.prop] === `REJECTED`" class="isPass"> 复核拒绝 </div>  
                    <div v-if="scope.row[column.prop] === `CANCELLED`" class="isPass"> 复核取消 </div>
                </template>
                <template v-else-if="column.name === '规则类型'">
                    <div class="isPass"> {{ scope.row[column.prop] }} </div>
                </template>
                <template v-else-if="column.name === '问题描述'">
                  <div v-if="scope.row[column.prop]" class="cellTextClamp3">{{ scope.row[column.prop] }}</div>
                  <div v-else> - </div>
                </template>
                <template v-else-if="column.name === '复核时间'">
                  <div v-if="scope.row[column.prop]">{{ scope.row[column.prop] }}</div>
                  <div v-else> - </div>
                </template>
                <template v-else-if="column.name === '复核人'">
                  <div v-if="scope.row[column.prop]">{{ scope.row[column.prop] }}</div>
                  <div v-else> - </div>
                </template>
                <template v-else>
                  {{ scope.row[column.prop] }}
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
                  :disabled="scope.row.status !== `IN_PROCESS`"
                  type="primary"
                  size="small"                 
                  style="padding: 4px 10px !important;margin: 0 5px;"
                  @click="handleApprove(scope.row,`APPROVE`)"
                >
                  通过
                </el-button>
                 <el-button
                    :disabled="scope.row.status !== `IN_PROCESS`"
                  type="danger"
                  size="small"                 
                  style="padding: 4px 10px !important;margin: 0;"
                    @click="handleApprove(scope.row,`REJECT`)"
                >
                  拒绝
                </el-button>
               </div>
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
            @current-change="getCircuitReviewResultDetailAuditPageFn"
            @size-change="getCircuitReviewResultDetailAuditPageFn"
          />
        </div>
      </div>
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
      > span {
        color: #0462e3;
        margin-right: 14px;
        font-weight: 600;
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
        font-weight: 600;
        .label {
          font-weight: 500;
          color: #555555;
          line-height: 17px;
          margin-right: 8px;
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
          display: flex;
          align-items: center;
          white-space: normal;
          word-break: break-word;
          justify-content: flex-start;
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
        .el-table__body td .cell {
          white-space: normal;
          word-break: break-word;
        }
        .el-table__body td .cell .el-text,
        .el-table__body td .cell span,
        .el-table__body td .cell a {
          line-height: 24px;
        }
        .cellTextClamp3 {
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 3;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: normal;
          word-break: break-word;
          line-height: 1.5; // 使用相对单位
          max-height: 4.5em; // 1.5 × 3 = 4.5em
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
        .isPass{
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: center;
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
  