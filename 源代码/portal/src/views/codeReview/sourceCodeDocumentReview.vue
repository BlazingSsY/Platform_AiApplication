<script setup lang="ts">
  import localTitle from "./components/localTitle.vue"
  import * as sourceApi from "@/ajax/sourcecodereview"
  import { GroupUserList, getDepartmentsSimpleItems} from "@/ajax"
  import { genFileId } from "element-plus"
  import fileSecretLevelEnum from "@/hocks/fileSecretLevelEnum"
  import store from "@/store/index"
  import { dayjs } from "element-plus"
  import table2excel from "js-table2excel"
  import { isAdmin } from "@/hooks/userIsAdmin"
  const isAdminUser = isAdmin()
  
  const userInfo = computed(() => store.state.user.userInfo)
  const userId = computed(() => userInfo.value?.id)
  const roleInfo = computed(() => userInfo.value?.role)
  const roleName = computed(() => (roleInfo.value ? roleInfo.value.name : ``))
  const loginName = computed(() => userInfo.value?.loginName)
  const userDepartmentId = computed(() => userInfo.value?.departmentId)
  
  const fileSecretLevel = fileSecretLevelEnum()
  
  const fileslistDataAll = ref<any>([])
  const rulesListRef = ref()
  const versionTableRef = ref()
  const rulesListDataLocal = ref<any[]>([])
  const rulesListData = ref<any[]>([])
  const updatedRulesListData = ref<any[]>([])
  
  const departmentsData = reactive<any[]>([])
  const usersData = ref<any[]>([])
  
  const timers = ref<any>([])
  const getDepartments = () => {
    getDepartmentsSimpleItems({ departmentId: userDepartmentId.value })
      .then(res => {
        if (res.succ) {
          departmentsData.splice(0)
          res.content.forEach((item: any) => {
            departmentsData.push({ ...item })
          })
        }
      })
      .catch(error => {
        ElMessage.error("获取单位数据失败", error)
      })
  }
  getDepartments()
  
  const searchObj = reactive({
    depId: ``,
    userId: ``
  })
  const depChange = () => {
    const params_: any = { departmentId: searchObj.depId }
    params_.pageSize = 10000
    GroupUserList(params_).then(res => {
      if (res.succ) {
        if (res.content && res.content.records) {
          usersData.value = res.content.records
        }
      }
    })
  }
  
  // 分页相关参数
  const pagination = ref({
    current: 1,
    size: 10,
    total: 0
  })

  const route = useRoute()
  if (store.state.pagesInfoStroe && store.state.pagesInfoStroe[route.path]) {
    pagination.value.size = store.state.pagesInfoStroe[route.path].size
    pagination.value.current = store.state.pagesInfoStroe[route.path].current
    if (store.state.pagesInfoStroe[route.path].searchObj) {
      searchObj.depId = store.state.pagesInfoStroe[route.path].searchObj.depId ?? ``
      searchObj.userId = store.state.pagesInfoStroe[route.path].searchObj.userId ?? ``
      if(searchObj.depId){
        depChange()
      }
    }
  }
  
  // 获取审查规则列表
  sourceApi.getSourceCodeReviewRules().then(res => {
    if (res.succ) {
      rulesListDataLocal.value = res.content.map((r: any) => {
        return {
          label: r.name.replace(/^\s+|\s+$|[。.；;!！?？:：,，、()\[\]【】\{\}""']+$/gu, ``),
          type: r.type,
          status: `审查中`
        }
      })
    }
  })
  
  const loading = ref(false)
  const loadingType = ref(1)
  const tableRef = ref()
  // 获取电路图文件列表
  const fetchCircuitFiles = async (page = 1, pageSize = 10) => {
    const params: any = {
      ...searchObj,
      pageNumber: page,
      pageSize
    }
    const res = await sourceApi.getSourceCodeFilePage(params)
    if (res.succ) {
      fileslistDataAll.value = []
      res.content.records.forEach((item: any) => {
        const obj = {
          ...item,
          id: item.id,
          version: item.version,
          fileVersionId: item.fileVersionId,
          resultId: item.resultId,
          tel_file: item.fileName,
          fileId: item.minioId,
          total_cases: item.checkPoints || 0,
          total_passed: item.passCheckPoints || 0,
          formatted_percentage: item.passRate || 0,
          loading: false,
          reviewTime: item.reviewTime || ``,
          status: item.status || ``,
          departmentName: item.departmentName || ``,
          ownerName: item.ownerName || ``,
          ownerId: item.ownerId || ``,
          isClosedLoop: item.isClosedLoop,
          minioId: item.minioId || ``,
          secretLevel: item.secretLevel,
          recordsList: item.recordsList || [],
              compatibleModels: item.compatibleModels || ``, // 配套机型
          productModel: item.productModel || ``, // 产品型号
          productName: item.productName || ``, // 产品名称
          configName: item.configName || ``, // 电路原理图号
          codeFileVersion: item.codeFileVersion || ``, // 版本
        }
        // 轮询审查结果状态
        const pollReviewStatus = reviewId => {
          sourceApi
            .getSourceCodeReviewResult(reviewId)
            .then(resultRes => {
              if (resultRes.succ && resultRes.content) {
                const resultData = resultRes.content
                console.log(`resultData`, reviewId, resultData)
                if (resultData.status === `IN_PROGRESS`||resultData.status === `ERROR`) {
                  // 审查还在进行中，继续轮询
                  timers.value.push(setTimeout(() => pollReviewStatus(reviewId), 2000)) // 每2秒轮询一次
                } else if (resultData.status === `FINISHED`) {
                  obj.status = `FINISHED`
                  obj.loading = false
                } else if (resultData.status === `FAILED`) {
                  obj.status = `FAILED`
                  obj.loading = false
                  ElMessage.error(resultData.errorMessage)
                }
              } else {
                console.error(`获取审查结果失败:`, resultRes.msg)
                obj.loading = false
              }
            })
            .catch(error => {
              console.error(`获取审查结果出错:`, error)
              obj.loading = false
            })
        }
        if (obj.status === `IN_PROGRESS`||obj.status === `ERROR`) {
          obj.loading = true
          timers.value.push(
            setTimeout(() => {
              console.log('will pollReviewStatus', obj.resultId)
              if (obj.resultId) {
                pollReviewStatus(obj.resultId) // obj.id --> obj.resultId 20251229
              }
              
            }, 2000)
          ) // 每2秒轮询一次
        }
        fileslistDataAll.value.push(obj)
      })
      pagination.value = {
        current: res.content.current,
        size: res.content.size,
        total: res.content.total
      }
  
      nextTick(() => {
        if (tableRef.value) {
          if (sortStroe.value && Object.keys(sortStroe.value).length > 0) {
            Object.keys(sortStroe.value).forEach(key => {
              tableRef.value.sort(key, sortStroe.value[key])
            })
          }
        }
      })
    }
  }
  
  const getStatusText = status => {
    if (status) {
      if (status === `IN_PROGRESS`) {
        return `正在审查`
      } else if (status === `FINISHED`) {
        return `审查完成`
      } else if (status === `ERROR`) {
        return `审查错误`
      } else if (status === `FAILED`) {
        return `审查失败`
      }
    } else {
      return "-"
    }
  }

    // 控制用户名列显示状态
  const showOwnerNameColumn = ref(false)

  // 监听用户名列显示状态变化，强制表格重新渲染以修复背景色问题
  watch(() => showOwnerNameColumn.value, () => {
    nextTick(() => {
      tableRef.value?.doLayout()
    })
  })

  
  const initData = async () => {
    loadingType.value = 1
    loading.value = true
    await fetchCircuitFiles(pagination.value.current, pagination.value.size)
    loading.value = false
  }
  initData()
  
  const keyNameMap = {
    规则类型: `rule_type`,
    审查规则: `rule_info`,
    器件型号: `component_model_name`,
    位号引脚: `position_pin_num`,
    审查意见: `result_msg`,
    是否通过: `passed`
  }
  
  // 审查记录
  const router = useRouter()
  const goReviewResults = (item: any) => {
    if (item.reviewTime) {
      // 通过路由参数传递数据，而不是使用localStorage
      store.commit("SET_PAGES_INFO_STROE", { path: route.path, info: { ...pagination.value, searchObj } })
      router.push({
        path: `/sourceCodeReviewResults`,
        query: {
          item: encodeURIComponent(JSON.stringify(item))
        }
      })
    }
  }
  
  const cancleDiaFn1 = () => {
    for (const timerElement of timers.value) {
      clearTimeout(timerElement)
    }
    timers.value = []
    fetchCircuitFiles(pagination.value.current, pagination.value.size)
    showRuleModal.value = false
  }
  
  const cancleDiaFn = () => {
    // 停止审查
    sourceApi
      .stopSourceCodeReview({ resultId: fileReviewId.value })
      .then(res => {
        if (res.succ) {
          ElMessage.success(`停止审查成功`)
          for (const timerElement of timers.value) {
            clearTimeout(timerElement)
          }
          timers.value = []
          fetchCircuitFiles(pagination.value.current, pagination.value.size)
          showRuleModal.value = false
        } else {
          ElMessage.error(res.msg || `停止审查失败`)
        }
      })
      .catch(error => {
        console.error(`停止审查出错:`, error)
        ElMessage.error(`停止审查出错`)
      })
  
    
  }
  const isFinish = ref(false)
  const finishInfo = ref<any>({})
  const reviewProgress = ref({
    totalFilesize:0,
    finishFilesize:0,
    fileName:``,
    fileRule:[]
  })
  const percentageValue = computed(() => {
    if (reviewProgress.value.totalFilesize > 0) {
      return Math.floor((reviewProgress.value.finishFilesize / reviewProgress.value.totalFilesize) * 100)
    } else {
      return 0
    }
  })  
  const taskNum = ref(0)
  const fileRuleLists = ref<any>([])
  const fileReviewId = ref(``)
  // start polling helper (shared for new and existing reviews)
  const startPolling = (reviewId: string, item: any, useWait = false) => {
    console.log('进入轮询 --- ', reviewId)
    
    // 添加 reviewId 有效性检查
    if (!reviewId || reviewId === 'undefined' || reviewId === 'null') {
      console.error('startPolling: reviewId 无效', reviewId)
      item.loading = false
      showRuleModal.value = false
      // ElMessage.error('审查ID无效，无法开始轮询')
      return
    }
    
    console.log('startPolling: reviewId 有效', reviewId)
    fileReviewId.value = reviewId
    const pollReviewStatus = () => {
      // 添加 reviewId 有效性检查
      if (!reviewId || reviewId === 'undefined' || reviewId === 'null') {
        console.error('pollReviewStatus: reviewId 无效', reviewId)
        item.loading = false
        showRuleModal.value = false
        // ElMessage.error('审查ID无效，无法开始轮询')
        return
      }
      
      console.log('pollReviewStatus: 开始查询 reviewId', reviewId)
      sourceApi.getSourceCodeReviewResult(reviewId).then(async resultRes => {
        console.log('进入轮询 --- resultRes ', resultRes)
        if (resultRes.succ) {
          const resultData = resultRes.content
          item.fileVersionId = resultData.fileVersionId
          item.status = resultData.status
          item.reviewTime = resultData.reviewTime
          console.log('进入轮询 --- resultData item', reviewId, resultData)
          if (resultData.status === `IN_PROGRESS` || resultData.status === `ERROR`) {
            sourceApi.getSourceCodeReviewProgress({ id: reviewId }).then(res1 => {
              if (res1.succ) {
                console.log(' getSourceCodeReviewProgress ---res1', res1)
                reviewProgress.value = res1.content
                // 始终用最新的 fileRule 列表，避免新增行时引用旧数组
                fileRuleLists.value = res1.content.fileRule || []
                // 在这里，根据 fileRuleLists 里的对象里的 status，把滚动条跳到最后一个status不是3的那个记录那里
                nextTick(() => {
                  if (fileRuleLists.value.length > 0) {
                    // console.log('寻找滚动目标')
                    // 1. 找到最后一个status不是3的记录的索引
                    let lastIndex = -1
                    for (let i = 0; i < fileRuleLists.value.length; i++) {
                      if (fileRuleLists.value[i].status !== 3) {
                        lastIndex = i
                      }
                    }
                    // console.log('lastIndex', lastIndex)
                    // 2. 如果找到了status不是3的记录，则滚动到该位置
                    if (lastIndex !== -1 && rulesListRef.value) {
                      const bodyWrapper = rulesListRef.value.bodyWrapper || rulesListRef.value.$el.querySelector('.el-table__body-wrapper')
                      if (bodyWrapper) {
                        const targetRow = bodyWrapper.querySelector(`.el-table__body tr:nth-child(${lastIndex + 1})`)
                        if (targetRow) {
                          const rowHeight = targetRow.offsetHeight || 40
                          const targetOffset = Math.max(0, lastIndex * rowHeight - rowHeight) // 把目标行放到视口上方一点
                          
                          // console.log('执行滚动目标', targetOffset)
                          if (typeof rulesListRef.value.setScrollTop === 'function') {
                            // console.log('执行滚动目标  function ', targetOffset)
                            rulesListRef.value.setScrollTop(targetOffset)
                          } else {                            
                            bodyWrapper.scrollTo({ top: targetOffset, behavior: 'smooth' })
                          }
                        }
                      }
                    }
                  }
                })
                /**
                 * 启动轮询审查状态的任务
                 * 每2秒查询一次审查进度，直到审查完成或失败
                 */
                timers.value.push(setTimeout(() => {
                  pollReviewStatus()
                }, 2000))
              }
            })
          } else if (resultData.status === `FINISHED`) {
            sourceApi.getSourceCodeReviewProgress({ id: reviewId }).then(res2 => {
              if (res2.succ) {
                reviewProgress.value = res2.content
                item.reviewTime = resultData.reviewTime
                currentReviewFile.value.reviewTime = item.reviewTime
                currentReviewFile.value.resultId = reviewId

                // 更新统计数据
                item.total_cases = resultData.checkPoints
                item.total_passed = resultData.passCheckPoints
                item.formatted_percentage = resultData.passRate

                fetchCircuitFiles(pagination.value.current, pagination.value.size)
                isFinish.value = true
                item.loading = false
                showRuleModal.value = false
              }
            })
          } else if (resultData.status === `FAILED`) {
            item.loading = false
            showRuleModal.value = false
            ElMessage.error(resultData.errorMessage)
          } else {
            // 其它状态
            item.loading = false
            showRuleModal.value = false
          }
        }
      })
    }

    if (useWait) {
      const waitTaskInfo = () => {
        sourceApi.waitTaskInfo({ id: reviewId }).then(res2 => {
          if (res2.succ) {
            taskNum.value = res2.content.taskNum
            if (taskNum.value > 0) {
              setTimeout(() => { waitTaskInfo() }, 2000)
            } else {
              pollReviewStatus()
            }
          }
        })
      }
      waitTaskInfo()
    } else {
      pollReviewStatus()
    }
  }

  // 规则确认 - 开始审查
  const confirmReview = () => {
    const item = currentReviewItem.value
    isFinish.value = false
    showRuleModal.value = true
    showRuleConfirmModal.value = false

    // If the review is already in progress, just resume polling and show modal
    if (item.status === `IN_PROGRESS` && item.resultId) {
      console.log('恢复已有审查任务:', { status: item.status, resultId: item.resultId })

      // 检查 resultId 有效性
      if (!item.resultId || item.resultId === 'undefined' || item.resultId === 'null') {
        console.error('恢复审查任务失败: resultId 无效', item.resultId)
        // ElMessage.error('无法恢复审查任务，审查ID无效')
        item.loading = false
        return
      }

      item.loading = true
      startPolling(item.resultId, item, false)
      return
    }

    // Otherwise, start a new review
    item.loading = true
    sourceApi.createSourceCodeReview({ fileVersionId: item.fileVersionId })
      .then(res => {
        if (res.succ) {
          const reviewId = res.content
          console.log('新建审查接口返回:', { succ: res.succ, content: reviewId, msg: res.msg })

          // 检查 reviewId 有效性
          if (!reviewId || reviewId === 'undefined' || reviewId === 'null') {
            console.error('新建审查失败: reviewId 无效', reviewId)
            // ElMessage.error('创建审查任务失败，返回的审查ID无效')
            item.loading = false
            showRuleModal.value = false
            return
          }

          console.log('新建后启动轮询 reviewId ', reviewId)
          // start polling with wait for tasks
          setTimeout(() => {
            startPolling(reviewId, item, true)
          }, 1000) // 延迟进入轮询

        } else {
          ElMessage.error(res.msg || `审查开始失败`)
          item.loading = false
          showRuleModal.value = false
        }
      })
      .catch(() => {
        item.loading = false
        showRuleModal.value = false
      })
  }

  // 规则确认 - 关闭模态窗
  const closeRuleConfirmModal = () => {
    showRuleConfirmModal.value = false
    currentReviewItem.value.loading = false
    allRulesList.value = []
  }

  // 点击审查按钮
  const reviewFn = (item: any, options: { resume?: boolean } = {}) => {
    currentReviewItem.value = item
    isReReview.value = !!item.reviewTime // 是否为再次审查

    // 如果是再次审查，直接进入原有流程
    if (isReReview.value) {
      reviewProgress.value = {
        totalFilesize: 0,
        finishFilesize: 0,
        fileName: ``,
        fileRule: []
      }
      fileRuleLists.value = []
      isFinish.value = false
      showRuleModal.value = true

      if (item.status === `IN_PROGRESS` && item.resultId) {
        console.log('恢复已有审查任务:', { status: item.status, resultId: item.resultId })

        if (!item.resultId || item.resultId === 'undefined' || item.resultId === 'null') {
          console.error('恢复审查任务失败: resultId 无效', item.resultId)
          item.loading = false
          return
        }

        item.loading = true
        startPolling(item.resultId, item, false)
        return
      }

      item.loading = true
      sourceApi.createSourceCodeReview({ fileVersionId: item.fileVersionId })
        .then(res => {
          if (res.succ) {
            const reviewId = res.content
            console.log('新建审查接口返回:', { succ: res.succ, content: reviewId, msg: res.msg })

            if (!reviewId || reviewId === 'undefined' || reviewId === 'null') {
              console.error('新建审查失败: reviewId 无效', reviewId)
              item.loading = false
              showRuleModal.value = false
              return
            }

            console.log('新建后启动轮询 reviewId ', reviewId)
            setTimeout(() => {
              startPolling(reviewId, item, true)
            }, 1000)

          } else {
            ElMessage.error(res.msg || `审查开始失败`)
            item.loading = false
            showRuleModal.value = false
          }
        })
        .catch(() => {
          item.loading = false
          showRuleModal.value = false
        })
      return
    }

    // 首次审查，调用获取所有规则接口，显示规则确认界面
    /**
     * filter
: 
{language: [], selectStatus: ["1"], ruleType: [], ruleSource: [], desc: ""}
     */
    sourceApi.sourceCodeReviewRuleAll({
      filter: {
        language: [],
        selectStatus: [`1`],
        ruleType: [],
        ruleSource: [],
        desc: ``
      }
    })
      .then(res => {
        if (res.succ) {
          allRulesList.value = res.content.rules || []
          showRuleConfirmModal.value = true
        } else {
          ElMessage.error(res.msg || '获取规则列表失败')
          item.loading = false
        }
      })
      .catch(() => {
        item.loading = false
        ElMessage.error('获取规则列表失败')
      })
  }
  
  const delFn = (item: any) => {
    ElMessageBox.confirm(`确定删除?`, `提示`, {
      confirmButtonText: `确定`,
      cancelButtonText: `取消`,
      type: `warning`
    })
      .then(() => {
        sourceApi
          .sourceCodeFileIsRecycle({
            fileIdList: [item.id],
            isRecycle: 1
          })
          .then(res => {
            if (res.succ) {
              ElMessage.success(`移入网表文件回收站成功！`)
              setTimeout(() => {
                fetchCircuitFiles(pagination.value.current, pagination.value.size)
              }, 200)
            }
          })
      })
      .catch(() => {})
  }
  const delRecordsFn = (item: any) => {
    ElMessageBox.confirm(`确定删除审查记录?`, `提示`, {
      confirmButtonText: `确定`,
      cancelButtonText: `取消`,
      type: `warning`
    })
      .then(() => {
        sourceApi.sourceCodeReviewResultFileId(item).then(res => {
          if (res.succ) {
            ElMessage.success(`删除审查记录成功！`)
            setTimeout(() => {
              // 删除后重新加载当前页数据
              fetchCircuitFiles(pagination.value.current, pagination.value.size)
            }, 200)
          }
        })
      })
      .catch(() => {})
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
  const list = ref<any[]>([])
  const columnMapping = {
    "ruleType": `规则类型`,
    "ruleCode": `规则编号`,
    "ruleName": `审查规则`,
    "sourceFileName": `代码文件`,
    "lineNumber": `代码行号`,
    "reviewSuggestion": `审查意见`,
    "isPassed": `是否通过`,
    "errorCode": `错误代码`,
    "errorReason": `错误原因`,
    "recheckConclusion": `复核结论`,
    "questionDesc": `问题描述`,
    "recheckStatus": `复核状态`
  }
  
  // 下载电路图文件
  const downloadFn = (item: any) => {
    item.onImportLoading = true
    sourceApi.sourceCodeReviewResultDetailList({"resultId": item.resultId})
      .then(res => {
        const arr: any[] = []
        item.onImportLoading = false
        if (res.succ) {
          const records = res.content.map((item: any) => ({
            ...item,
            lineNumber: item.lineNumber || ``,
            reviewSuggestion: item.reviewSuggestion || ``,
            sourceFileName: item.sourceFileName || ``,
            errorCode: item.errorCode || ``,
            errorReason: item.errorReason || ``,
            "ruleType": item.ruleType,
            isPassed: item.isPassed === 1 ? `通过` : `未通过`,
            recheckConclusion: item.recheckConclusion || ``,
            questionDesc: item.questionDesc || ``,
            recheckStatus: item.recheckStatus === 1 ? `未复核` : item.recheckStatus === 2 ? `复核中` : item.recheckStatus === 3 ? `复核完成` : ``,
          }))
          arr.splice(0)
          Object.keys(columnMapping).forEach(key => {
            arr.push({
              "title": columnMapping[key as keyof typeof columnMapping],
              key,
              "type": `text`
            })
          })
          if (records && records.length) {
            table2excel(arr,records, `审查结果_` + item.fileName.replace(`.h`, ``).replace(`.zip`, ``).replace(`.c`, ``) + `_` + getCurrentTimestampStr() + `.xls`)
          } else {
            ElMessage.error(`没有数据可导出！`)
          }
        }
      })
      .catch(() => {
        item.onImportLoading = false
      })
  }
  const crushFn = (item: any) => {
    ElMessageBox.confirm(`确定粉碎?`, `提示`, {
      confirmButtonText: `确定`,
      cancelButtonText: `取消`,
      type: `warning`
    })
      .then(() => {
        let params = {
          id: item.id,
          label: item.label,
          version: item.version
        }
        console.log('粉碎参数',params)
        sourceApi.deleteSourceCodeFile(params).then(res => {
          if (res.succ) {
            const deleteStatus = res.content?.delete
            const desc = res.content?.desc
            if (deleteStatus === '1') {
              // 粉碎成功
              ElMessage.success(`粉碎文件成功！`)
              setTimeout(() => {
                // 删除后重新加载当前页数据
                fetchCircuitFiles(pagination.value.current, pagination.value.size)
              }, 200)
            } else if (deleteStatus === '2') {
              // 不可粉碎，提示 desc 内容
              ElMessage.warning(desc || '该文件不可粉碎')
            } else {
              // 其他情况，默认成功处理
              ElMessage.success(`粉碎文件成功！`)
              setTimeout(() => {
                fetchCircuitFiles(pagination.value.current, pagination.value.size)
              }, 200)
            }
          }
        })
      })
      .catch(() => {})
  }
  
  // 导出审查结果
  
  
  // 上传文件
  const uploadFormRef = ref()
  const uploadShow = ref(false)
  // 配套机型 compatibleModels、产品型号 productModel、产品名称 productName、配置项名称 configName、版本  codeFileVersion
  const uploadForm = reactive<any>({
    fileSecretLevelEnum: `PUBLIC`,
    file: null,
    fileId: ``,
    isTestFile:0, //0：否；1：是
    isHasproductModel:false,
    compatibleModels: ``, // 配套机型
    productModel: ``, // 产品型号
    productName: ``, // 产品名称
    configName: ``, // 配置项名称
    codeFileVersion: ``, // 版本
  })
  const validateFile = (rule: any, value: any, callback: any) => {
    if (!value) {
      callback(new Error("请上传代码文件"))
    } else {
      callback()
    }
  }
  const uploadRules = reactive({
    // fileSecretLevelEnum: [{ required: true, message: `请选择涉密级别`, trigger: `change` }],
    file: [{ required: true, validator: validateFile, trigger: "change" }],
    "compatibleModels": [{ "required": true, "message": `请输入配套机型`, "trigger": `change` }], //配套机型
    "productModel": [{ "required": false, "message": `请输入产品型号`, "trigger": `blur` }],
    "productName": [{ "required": false, "message": `请输入产品名称`, "trigger": `blur` }],
    "configName": [{ "required": false, "message": `请输入配置项名称`, "trigger": `blur` }],
    "codeFileVersion": [{ "required": false, "message": `请输入版本`, "trigger": `blur` }]
  })
  const handleIsHasproductModelChange = () => {
     const isRequired = uploadForm.isHasproductModel
    // 重新创建 rules 对象以触发 Vue 的响应式更新
    Object.assign(uploadRules, {
      file: [{ required: true, validator: validateFile, trigger: "change" }],
      compatibleModels: [{ required: isRequired, message: `请输入配套机型`, trigger: `change` }],
      productModel: [{ required: isRequired, message: `请输入产品型号`, trigger: `blur` }],
      productName: [{ required: isRequired, message: `请输入产品名称`, trigger: `blur` }],
      configName: [{ required: isRequired, message: `请输入配置项名称`, trigger: `blur` }],
      codeFileVersion: [{ required: isRequired, message: `请输入版本`, trigger: `blur` }]
    })
    
    nextTick(() => {
      setTimeout(()=>{
        if (uploadFormRef.value) {
          uploadFormRef.value.clearValidate()
          if(uploadForm.file){
            handleBeforeAudio(uploadForm.file)
          }
        }
      })
    })
  }
   const isTestFileChange = ()=>{
    if(uploadForm.isTestFile === 1){
      uploadForm.isHasproductModel = false
    }else{
      uploadForm.isHasproductModel = true
    }

    handleIsHasproductModelChange()
  }
  const uploadFn = () => {
    uploadForm.fileSecretLevelEnum = `PUBLIC`
    uploadForm.file = null
    uploadForm.fileId = ``
    uploadForm.isTestFile = isAdminUser.value ? 1 : 0 //0：否；1：是
    uploadForm.isHasproductModel = isAdminUser.value ? false : true
    uploadForm.compatibleModels = `` // 配套机型
    uploadForm.productModel = `` // 产品型号
    uploadForm.productName = `` // 产品名称
    uploadForm.configName = `` // 配置项名称
    uploadForm.codeFileVersion = `` // 版本
    isTestFile.value = false
    uploadShow.value = true
    nextTick(()=>{
      handleIsHasproductModelChange()
    })
  }
  
  // 上传重命名提示文件
  const uploadRename = ref(false)
  const uploadRenameSubmit = () => {
    loading.value = true
    loadingType.value = 2
    let fileName: string;
    const fd = new FormData()
    if (uploadForm.file) {
        if (uploadForm.isTestFile === 1) {
          const file:any = uploadForm.file
          if (!file.name.startsWith('test-')) {
            fileName = 'test-' + file.name
            console.log('已为文件添加test-前缀:', fileName)
          } else {
            fileName = file.name
            console.log('文件名已包含test-前缀，使用原始文件名:', fileName)
          }
          const newFile = new File([file], fileName, { type: file.type })
          fd.append(`file`, newFile) // 将带前缀的文件保存到formData对象中
          console.log('已为文件添加test-前缀:', fileName)
        } else {
          fd.append(`file`, uploadForm.file) // 将文件保存到formData对象中
          console.log('未添加前缀，使用原始文件名:', uploadForm.file.name)
        }
      }
    console.log('上传参数',uploadForm)
    sourceApi
      .uploadSourceCodeVersionFile({
        fileId: uploadForm.fileId,
        fileSecretLevelEnum: `PUBLIC`,
        file: fd
      })
      .then(res => {
        loading.value = false
        if (res.succ) {
          // 上传后重新加载第一页数据
          uploadRename.value = false
          uploadShow.value = false
          pagination.value.current = 1
          fetchCircuitFiles(1, pagination.value.size)
        }
      })
      .catch(error => {
        loading.value = false
        ElMessage.error(`上传失败, 请重试或联系管理员`, error)
      })
  }
  const cancleFn = () => {
    uploadRename.value = false
    uploadShow.value = false
    loading.value = false
  }
  
  const accept1 = `.h,.c,.cpp,.hpp,.cxx,.java,.zip`
  const uploadRenameText = ref(``)
  const uploadElementAudio = ref()
   const isTestFile = ref(false)
  const handleBeforeAudio = async (file: any) => {
  
    const type = file.name.substring(file.name.lastIndexOf(`.`) + 1).toLowerCase()
    const acceptTypes = [`h`, `c`, `cpp`, `hpp`, `cxx`, `java`, `zip`]
    if (!acceptTypes.includes(type)) {
      ElMessage.error(`只支持 ${accept1} 类型文件`)
      return false
    }
    // zip文件不超过50M，其他文件不超过20M
    const maxSize = type === `zip` ? 50 : 20
    if (file.size / 1024 / 1024 > maxSize) {
      ElMessage.error(`文件必须小于${maxSize}M`)
      return false
    }
    console.log('用户信息:', userInfo.value)
    console.log('用户角色:', roleName.value)
    console.log('用户登录名:', loginName.value)
    console.log('isAdminUser值:', isAdminUser.value)
    // 当用户选择了有效文件后，清除之前的表单验证错误
    if (uploadFormRef.value) {
      uploadFormRef.value.clearValidate('file')
    }

    let fileName: string = file.name

    if(fileName.startsWith('test-')){
      isTestFile.value = true
      if(uploadForm.isTestFile != 1){
        uploadForm.isTestFile = 1
        isTestFileChange()
      }
    }else{
      isTestFile.value = false
    }

    if (uploadForm.isTestFile === 1 && !isTestFile.value ) {
      if (!file.name.startsWith('test-')) {
        fileName = 'test-' + file.name
        console.log('已为文件添加test-前缀:', fileName)
      } else {
        fileName = file.name
        console.log('文件名已包含test-前缀，使用原始文件名:', fileName)
      }
    } else {
      fileName = file.name
      console.log('未添加前缀，使用原始文件名:', fileName)
    }
    
    const result = await sourceApi.sourceCodeUploadCheckFile({fileName,isTestFile:uploadForm.isTestFile})
    if(result.succ){
      if(result.content.exist){
        uploadForm.file = file
        uploadForm.fileId = result.content.fileId
        uploadRenameText.value = `该文件名已经存在，是否继续上传`
        uploadRename.value = true
        return false
      }else if(result.content.pulverized){
        // 存在同名闭环项目，不可重复上传
        // uploadForm.file = file
        // uploadForm.fileId = result.content.fileId
        uploadForm.file = null
        uploadForm.fileId = ``
        uploadRenameText.value = `存在同名闭环项目，不可重复上传`
        uploadRename.value = true
        return false
      }
    }
    return true
  }
  const handleError = (error: any) => {
    ElMessage.error(`上传失败, 请重试`, error)
  }
  const onRemove = () => {}
  
  const uploadSubmit = async (formEl: any) => {
    if (!formEl) return
    await formEl.validate((valid: boolean, fields: any) => {
      if (valid) {
        loading.value = true
        loadingType.value = 2
        const fd = new FormData()
        // 如果用户角色为领导或登录用户名是admin，在文件名前添加test-前缀
        let fileName: string;
        if (uploadForm.file) {
          if (uploadForm.isTestFile === 1) {
            const file:any = uploadForm.file
            if (!file.name.startsWith('test-')) {
              fileName = 'test-' + file.name
              console.log('已为文件添加test-前缀:', fileName)
            } else {
              fileName = file.name
              console.log('文件名已包含test-前缀，使用原始文件名:', fileName)
            }
            const newFile = new File([file], fileName, { type: file.type })
            fd.append(`file`, newFile) // 将带前缀的文件保存到formData对象中
            console.log('已为文件添加test-前缀:', fileName)
          } else {
            fd.append(`file`, uploadForm.file) // 将文件保存到formData对象中
            console.log('未添加前缀，使用原始文件名:', uploadForm.file.name)
          }
        }
        sourceApi
          .sourceCodeUploadFile({ ...uploadForm, file: fd })
          .then(res => {
            loading.value = false
            if (res.succ) {
              // 上传后重新加载第一页数据
              uploadShow.value = false
              pagination.value.current = 1
              fetchCircuitFiles(1, pagination.value.size)
            }
          })
          .catch(error => {
            loading.value = false
            ElMessage.error(`上传失败, 请重试或联系管理员`, error)
          })
      } else {
        console.log(`error submit!`, fields)
      }
    })
  }
  const handleExceedAudio = (files: any[]) => {
    ;(uploadElementAudio.value as any)!.clearFiles()
    const file: any = files[0]
    file.uid = genFileId()
    ;(uploadElementAudio.value as any)!.handleStart(file)
    ;(uploadElementAudio.value as any)!.submit()
  }
  const httpRequestAudio = (fileObject: any) => {
    uploadForm.file = fileObject.file
    
    // 当文件被正确设置后，清除之前的表单验证错误
    if (uploadFormRef.value) {
      uploadFormRef.value.clearValidate('file')
    }
    
    return Promise.resolve()
  }
  
  const goReviewStatistics = () => {
    router.push({ path: `/sourceCodeStatistics` })
  }
  
  // 分页处理
  const handleCurrentChange = (page: number) => {
    pagination.value.current = page
    fetchCircuitFiles(page, pagination.value.size)
  }
  
  const handleSizeChange = (size: number) => {
    pagination.value.size = size
    pagination.value.current = 1
    fetchCircuitFiles(1, size)
  }
  
  const restFn = () => {
    searchObj.depId = ``
    searchObj.userId = ``
    initData()
  }
  const sortStroe = ref({})
  // 表格排序处理
  const handleSortChange = ({ prop, order }) => {
    sortStroe.value = {}
    sortStroe.value[prop] = order
    const data = [...fileslistDataAll.value]
    if (!order) {
      initData()
      return
    }
    data.sort((a: any, b: any) => {
      let aValue = a[prop]
      let bValue = b[prop]
      if (prop === `reviewTime`) {
        aValue = aValue || ``
        bValue = bValue || ``
      } else if (prop === `formatted_percentage` || prop === `total_cases` || prop === `total_passed`) {
        aValue = Number(aValue) || 0
        bValue = Number(bValue) || 0
      } else if (prop === `tel_file`) {
        aValue = aValue || ``
        bValue = bValue || ``
      }
      if (order === `ascending`) {
        return aValue > bValue ? 1 : -1
      } else {
        return aValue < bValue ? 1 : -1
      }
    })
    fileslistDataAll.value = data
  }
  
  // 审查规则动画相关
  const showRuleModal = ref(false)
  const displayedRules = ref<string[]>([])
  const isApiResponded = ref(false)
  const isShowingRules = ref(false)
  const currentRuleIndex = ref(0)
  const currentReviewFile = ref<any>({})

  // 规则确认界面相关
  const showRuleConfirmModal = ref(false)
  const allRulesList = ref<any[]>([])
  const currentReviewItem = ref<any>({})
  const isReReview = ref(false) // 是否为再次审查

  const startRuleAnimation = () => {
    isShowingRules.value = true
  }
  
  const generalCheckpoint = computed(() => {
    return updatedRulesListData.value.length
  })
  const passedCheckpoint = computed(() => generalCheckpoint.value - updatedRulesListData.value.filter((r: any) => r.status.includes(`未通过`)).length)
  const passingRate = computed(() => {
    const passRate = (passedCheckpoint.value / generalCheckpoint.value) * 100
    return passRate.toFixed(1) + `%`
  })
  
  // 意见反馈
  const ruleFormRef = ref()
  const ruleForm = reactive<any>({
    type: `1`,
    level: `1`,
    title: ``,
    desc1: `重现步骤:\n\n\n预期结果:\n\n\n实际结果:`,
    desc2: ``,
    desc3: ``,
    file1: [],
    file2: []
  })
  const rules = reactive<any>({
    type: [{ required: true, message: `请输入标题`, trigger: `blur` }],
    level: [{ required: true, message: `请输入标题`, trigger: `blur` }],
    title: [{ required: true, message: `请输入标题`, trigger: `blur` }]
  })
  const yjfkShow = ref(false)
  const yjfkFn = () => {
    yjfkShow.value = true
  }
  const submitFn = async (formEl: any) => {
    if (!formEl) return
    await formEl.validate((valid: boolean, fields: any) => {
      if (valid) {
        console.log(`submit!`)
      } else {
        console.log(`error submit!`, fields)
      }
    })
  }
  
  const handleRemove = () => {}
  const httpRequestFn1 = (file: any) => {
    console.log(file)
    return Promise.resolve()
  }
  const httpRequestFn = (file: any) => {
    console.log(file)
    return Promise.resolve()
  }
  const handleExceed = (file: any) => {
    console.log(file)
  }
  
  // 规则列表
  const gzlbShow = ref(false)
  const gzlbFn = async () => {
    router.push({ path: `/sourceCodeRuleList` })
  }
  
  // 查看版本
  const rowData = ref<any>(null)
  const viewVersionShow = ref(false)
  const viewVersionListData = ref<any>([])
  const viewVersionFn = (row: any) => {
    rowData.value = row
    sourceApi.sourceCodeFileVersionResult({ fileId: row.id }).then(res => {
      if (res.succ) {
        viewVersionListData.value = res.content.map((item: any) => ({
          version: item.fileName,
          fileVersionId: item.fileVersionId,
          resultId: item.resultId,
          tel_file: row.tel_file,
          id: item.fileId,
          total_cases: item.checkPoints || 0,
          total_passed: item.passCheckPoints || 0,
          formatted_percentage: item.passRate || 0,
          loading: false,
          reviewTime: item.reviewTime || ``,
          departmentName: item.departmentName || ``,
          ownerName: item.ownerName || ``,
          isClosedLoop: item.isClosedLoop,
          minioId: item.minioId || ``,
          secretLevel: item.secretLevel,
          recordsList: item.recordsList || []
        }))
        viewVersionShow.value = true
      }
    })
  }
  
  // 问题未关闭
  const uploadElementVersion = ref({})
  const uploadFormVersion = ref<any>({})
  const handleBeforeVersion = async (file: any, row: any) => {
    const fileSize = file.size / 1024 / 1024 < 100
    const type = file.name.substring(file.name.lastIndexOf(`.`) + 1)
    if (!(type == `h` || type == `c` || type == `zip`)) {
      ElMessage.error(`只支持 ${accept1} 类型文件`)
      return false
    } else if (!fileSize) {
      ElMessage.error(`文件必须小于100M`)
      return false
    }
    uploadFormVersion.value = row
    return true
  }
  const handleExceedVersion = (files: any[]) => {
    ;(uploadElementVersion.value as any)!.clearFiles()
    const file: any = files[0]
    file.uid = genFileId()
    ;(uploadElementVersion.value as any)!.handleStart(file)
    ;(uploadElementVersion.value as any)!.submit()
  }
  const httpRequestVersion = (fileObject: any) => {
    loading.value = true
    loadingType.value = 2
    const fd = new FormData()
    fd.append(`file`, fileObject.file)
    sourceApi
      .uploadSourceCodeVersionFile({
        fileId: uploadFormVersion.value.id,
        fileSecretLevelEnum: `PUBLIC`,
        file: fd
      })
      .then(res => {
        loading.value = false
        if (res.succ) {
          pagination.value.current = 1
          fetchCircuitFiles(1, pagination.value.size)
        }
      })
      .catch(error => {
        loading.value = false
        ElMessage.error(`上传失败, 请重试或联系管理员`, error)
      })
    return Promise.resolve()
  }
  
  onUnmounted(() => {
    for (const timerElement of timers.value) {
      clearTimeout(timerElement)
    }
    timers.value = []
  })
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="documentReview" element-loading-background="rgba(122, 122, 122, 0.8)" :element-loading-text="loadingType === 1 ? `数据加载中...` : `文件上传中...`">
      <localTitle title="代码文件列表">
        <template #rightBtn>
          <div style="display: flex; align-items: center; margin-left: 12px; gap: 8px;">
            <span style="font-size: 15px; color: #606266;">显示所有列</span>
            <el-switch v-model="showOwnerNameColumn" style="--el-switch-on-color: #67c23a; --el-switch-off-color: #dcdfe6; --el-switch-size: 20px;" size="large" />
          </div>
          <el-button type="primary" icon="Upload" style="margin-left: 12px" @click="uploadFn">上传文件</el-button>
          <!--        <el-button type="primary" icon="Tickets" style="margin-left: 12px" @click="gzlbFn">规则列表</el-button>-->
          <!-- <el-button type="primary" icon="ChatLineSquare" @click="yjfkFn"> 意见反馈 </el-button> -->
        </template>
      </localTitle>
      <div class="tableBox">
        <div v-if="roleName && roleName !== `普通用户`" class="search">
          <el-form ref="ruleFormRef" :inline="true">
            <el-form-item label="按单位">
              <el-select v-model="searchObj.depId" placeholder="选择单位" style="width: 240px" clearable @change="depChange">
                <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            <!-- <el-form-item label="按用户名">
              <el-select v-model="searchObj.userId" :disabled="searchObj.depId === ''" placeholder="选择用户名" style="width: 240px" clearable>
                <el-option v-for="item in usersData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item> -->
          </el-form>
          <div>
            <el-button type="primary" @click="initData()">搜索</el-button>
            <el-button @click="restFn">重置</el-button>
          </div>
        </div>
        <div class="tableWrapper">
          <el-table ref="tableRef" border
           tooltip-effect="light" class="custom-table-size" :fit="true" table-layout="fixed"
           :data="fileslistDataAll"
           :style="{ width:'100%' }"
           :height="fileslistDataAll.length > 20 ? 850 : undefined"
           :max-height="850"
>
          <el-table-column label="序号" align="center" width="64">
            <template #default="scope">
              {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="tel_file" label="代码文件" align="center" show-overflow-tooltip min-width="140px" />
          <el-table-column prop="departmentName" label="单位名称" align="center" min-width="100px" show-overflow-tooltip />
          <el-table-column v-if="showOwnerNameColumn" prop="ownerName" label="用户名" align="center" min-width="100px" show-overflow-tooltip >
               <template #default="scope">
                {{ scope.row.ownerName || `-` }}
              </template>
            </el-table-column>
          <el-table-column v-if="showOwnerNameColumn" prop="compatibleModels" label="配套机型" align="center" width="120px" show-overflow-tooltip >
            <template #default="scope">
              {{ scope.row.compatibleModels || `-` }}
            </template>
          </el-table-column>
          <el-table-column v-if="showOwnerNameColumn" prop="productModel" label="产品型号" align="center" min-width="120px" show-overflow-tooltip >
            <template #default="scope">
              {{ scope.row.productModel || `-` }}
            </template>
          </el-table-column>
          <el-table-column v-if="showOwnerNameColumn" prop="productName" label="产品名称" align="center" min-width="120px" show-overflow-tooltip >
            <template #default="scope">
              {{ scope.row.productName || `-` }}
            </template>
          </el-table-column>
          <el-table-column v-if="showOwnerNameColumn" prop="configName" label="配置名称" align="center" width="120px" show-overflow-tooltip >
            <template #default="scope">
              {{ scope.row.configName || `-` }}
            </template>
          </el-table-column>
          <el-table-column v-if="showOwnerNameColumn" prop="codeFileVersion" label="版本" align="center" width="100px" show-overflow-tooltip >
            <template #default="scope">
              {{ scope.row.codeFileVersion || `-` }}
            </template>
          </el-table-column>
          <el-table-column prop="total_cases" label="文件数" align="center" width="100px" >
            <template #default="scope">
              {{ scope.row.total_cases || `-` }}
            </template>
          </el-table-column>
          <el-table-column prop="filesLine" label="行数" align="center" width="100px" />
          <el-table-column prop="useRuleSize" label="审查规则数" align="center" width="110px" />
          <el-table-column prop="questions" label="问题数量" align="center" width="110px" />
          <!-- <el-table-column prop="ownerName" label="用户名" align="center" width="100px" show-overflow-tooltip  /> -->
          <!-- <el-table-column prop="total_passed" label="通过检查文件数" align="center" width="150px" >
            <template #default="scope">
              {{ scope.row.total_passed || `-` }}
            </template>
          </el-table-column> -->
          <!-- <el-table-column prop="formatted_percentage" label="通过率" align="center"  width="100px">
            <template #default="scope">
              <span :class="scope.row.isClosedLoop === 0 ? `redText` : ``">
                {{ scope.row.formatted_percentage ? (scope.row.formatted_percentage * 100).toFixed(1) + "%" : `-` }}
              </span>
            </template>
          </el-table-column> -->
          <el-table-column prop="reviewTime" label="审查时间" align="center" width="165px" >
            <template #default="scope">
              {{ scope.row.reviewTime || `-` }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="审查状态" align="center" width="115px" >
            <template #default="scope">
              {{ getStatusText(scope.row.status) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="350" class-name="opt" fixed="right">
            <template #default="scope">
              <!--            <el-button type="primary" size="small" :disabled="!scope.row.reviewTime || scope.row.loading" class="btn" @click="goReviewResults(scope.row)"> 查看记录 </el-button>-->
              <el-button v-if="scope.row.status === 'FINISHED'" class="btn" type="primary" size="small" @click="goReviewResults(scope.row)"> 查看结果 </el-button>
              <el-button v-else class="btn" type="primary" disabled size="small" @click="goReviewResults(scope.row)"> 查看结果 </el-button>
              <!-- <el-button v-if="scope.row.status !== 'FINISHED'" class="btn" type="primary" disabled size="small"> 查看结果 </el-button> -->
              <el-button v-if="!scope.row.reviewTime" v-loading="scope.row.loading" class="btn" type="primary" size="small" @click="reviewFn(scope.row)"> 审查 </el-button>
              <el-button
                v-else-if="scope.row.reviewTime && scope.row.status === 'IN_PROGRESS'"
                v-loading="scope.row.loading"
                class="btn"
                :type="scope.row.status === 'IN_PROGRESS'?`primary`:`danger`"
                
                size="small"
                @click="reviewFn(scope.row)"
>
                {{ '审查中' }}
              </el-button>
              <el-button
                v-else-if="scope.row.reviewTime && (scope.row.status === 'ERROR'||scope.row.status === 'FAILED')"
                class="btn"
                type="primary"                
                size="small"
                @click="reviewFn(scope.row)"
>
                {{ '再次审查' }}
              </el-button>
              <el-button 
                v-else-if="scope.row.reviewTime" 
                v-loading="scope.row.loading" 
                class="btn" 
                :disabled="scope.row.isRecycle==1"
                type="primary" 
                size="small" 
                @click="reviewFn(scope.row)"
                > 
                再次审查 
              </el-button>
              <el-button v-loading="scope.row.onImportLoading" :disabled="scope.row.status !== 'FINISHED'" type="primary" size="small" class="btn" @click="downloadFn(scope.row)">下载报告</el-button>
              <!--            <el-button v-if="loginName === `admin` || loginName === `jzadmin`" type="danger" size="small" class="btn" :disabled="!scope.row.reviewTime" @click="delRecordsFn(scope.row)">-->
              <!--              删除记录-->
              <!--            </el-button>-->
              <el-button type="danger" size="small" class="btn" :disabled="scope.row.status == 'IN_PROGRESS' || scope.row.isRecycle==1" @click="crushFn(scope.row)">粉碎文件</el-button>
              <!--            <el-button type="primary" size="small" class="btn" @click="viewVersionFn(scope.row)">查看版本</el-button>-->
              <!-- <el-button v-if="!scope.row.reviewTime" type="info" disabled size="small" class="btn">文件未审查</el-button> -->
              <!-- <el-upload
                v-else-if="scope.row.status === 'FINISHED'&&scope.row.isClosedLoop==0&&userId===scope.row.ownerId"
                ref="uploadElementVersion"
                class="upload-demo12"
                action="#"
                :accept="accept1"
                :show-file-list="false"
                :limit="1"
                :before-upload="(file)=>handleBeforeVersion(file,scope.row)"
                :on-exceed="handleExceedVersion"
                :http-request="httpRequestVersion"
                :on-remove="onRemove"
                :on-error="handleError"
              >
                <el-button type="warning" size="small" class="btn">审查不通过</el-button>
              </el-upload> -->
              <!-- <el-button v-else-if="scope.row.status === 'FINISHED'&&scope.row.isClosedLoop==0&&userId!==scope.row.ownerId" plan type="warning" size="small" class="btn btnWarning" style="cursor: default;opacity: 0.5;">审查不通过</el-button> -->
              <!-- <el-button v-else-if="scope.row.status==='IN_PROGRESS'&&scope.row.isClosedLoop==0" plan type="warning" size="small" class="btn btnWarning" style="cursor: default;opacity: 0.5;">文件审查中</el-button> -->
              <!-- <el-button v-else-if="scope.row.isClosedLoop==1" type="success" size="small" class="btn btnNoHover" disabled>审查已通过</el-button> -->
  
              <!--            <el-tooltip-->
              <!--              class="box-item"-->
              <!--              effect="dark"-->
              <!--              :content="userId==scope.row.ownerId?`点击 “问题未关闭按钮” 可上传电路图文件新版本`:`只允许文件所属用户上传新版本`"-->
              <!--              placement="bottom-start"-->
              <!--            >-->
              <!--              <el-icon v-if="scope.row.reviewTime&&scope.row.isClosedLoop==0" class="wranTooltip"><InfoFilled /></el-icon>-->
              <!--            </el-tooltip>-->
            </template>
          </el-table-column>
          </el-table>
        </div>
  
        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            class="el-pagination-size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
  
      <!-- 上传文件 -->
      <el-dialog v-if="uploadShow" v-model="uploadShow" class="dialogCont dialogCont3" title="" :show-close="false" width="600px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">上传文件</div>
          </div>
        </template>
        <div class="contBox">
          <el-form ref="uploadFormRef" style="max-width: 600px" :model="uploadForm" :rules="uploadRules" label-width="auto" class="demo-ruleForm" status-icon>
            <el-form-item prop="isHasproductModel" label="是否型号产品">
              <el-radio-group v-model="uploadForm.isHasproductModel" :disabled="uploadForm.isTestFile===1" @change="handleIsHasproductModelChange">
                <el-radio :label="true">有</el-radio>
                <el-radio :label="false">无</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="compatibleModels" label="配套机型" props="compatibleModels">
              <el-input v-model="uploadForm.compatibleModels" placeholder="请输入配套机型" maxlength="50" @input="(value) => uploadForm.compatibleModels = value.replace(/^\s+/, '')">
                <template #suffix>
                  <el-popover
                    title=""
                    content="需规范机型写法"
                    placement="top-start"
                  >
                    <template #reference>
                     <el-icon><Warning /></el-icon>
                    </template>
                  </el-popover>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="productModel" label="产品型号">
              <el-input v-model="uploadForm.productModel" placeholder="请输入产品型号" maxlength="50" @input="(value) => uploadForm.productModel = value.replace(/^\s+/, '')"/>
            </el-form-item>
            <el-form-item prop="productName" label="产品名称">
              <el-input v-model="uploadForm.productName" placeholder="请输入产品名称" maxlength="50" @input="(value) => uploadForm.productName = value.replace(/^\s+/, '')"/>
            </el-form-item>
            <el-form-item prop="configName" label="配置项名称">
              <el-input v-model="uploadForm.configName" placeholder="请输入配置项名称" maxlength="50" @input="(value) => uploadForm.configName = value.replace(/^\s+/, '')"/>
            </el-form-item>
            <el-form-item prop="codeFileVersion" label="版本">
              <el-input v-model="uploadForm.codeFileVersion" placeholder="请输入版本" maxlength="50" @input="(value) => uploadForm.codeFileVersion = value.replace(/^\s+/, '')"/>
            </el-form-item>
            <el-form-item prop="file">
              <el-upload
                ref="uploadElementAudio"
                class="upload-demo"
                action="#"
                :accept="accept1"
                :show-file-list="true"
                :limit="1"
                :before-upload="handleBeforeAudio"
                :on-exceed="handleExceedAudio"
                :http-request="httpRequestAudio"
                :on-remove="onRemove"
                :on-error="handleError"
                drag
              >
              <div class="uploadFile">
                <el-button type="primary" icon="Upload">上传文件</el-button>
                <p style="margin-top: 10px;">支持 .h,.c,.cpp,.hpp,.cxx,.java,.zip 类型文件</p>
              </div>
              <template #file="{ file }">
                  <div style="display: flex; align-items: center; gap: 6px;">
                    <!-- 成功图标 -->
                    <svg v-if="file.status === 'success'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16" style="flex-shrink: 0;">
                      <circle cx="8" cy="8" r="7" fill="#67c23a"/>
                      <path d="M5.5 8l2 2 3-3.5" fill="none" stroke="#fff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <!-- 失败图标 -->
                    <svg v-if="file.status === 'fail'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16" style="flex-shrink: 0;">
                      <circle cx="8" cy="8" r="7" fill="#f56c6c"/>
                      <path d="M5.5 5.5l5 5M10.5 5.5l-5 5" fill="none" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/>
                    </svg>
                    <!-- 上传中图标 -->
                    <svg v-if="file.status === 'uploading'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16" style="flex-shrink: 0; animation: uploadSpin 1s linear infinite;">
                      <circle cx="8" cy="8" r="6" fill="none" stroke="#409eff" stroke-width="2" stroke-dasharray="28" stroke-dashoffset="10" stroke-linecap="round"/>
                    </svg>
                    <span>{{ file.name }}</span>
                    <span style="margin-left: 2px; color: #909399; font-size: 12px;">({{ formatFileSize(file.size) }})</span>
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item prop="isTestFile" label="是否测试文件">
              <el-radio-group v-model="uploadForm.isTestFile" :disabled="isAdminUser || isTestFile" @change="isTestFileChange">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>
  
        <template #footer>
          <div class="my-footer">
            <div class="right">
              <el-button @click="uploadShow = false"> 关闭</el-button>
              <el-button type="primary" @click="uploadSubmit(uploadFormRef)"> 确定 </el-button>
            </div>
          </div>
        </template>
      </el-dialog>

      <!-- 规则确认界面 -->
      <el-dialog v-model="showRuleConfirmModal" class="dialogCont dialogCont7" title="" :show-close="false" width="70%" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">规则确认界面</div>
            <div class="right">
              <el-icon style="cursor: pointer; font-size: 20px;" @click="closeRuleConfirmModal"><Close /></el-icon>
            </div>
          </div>
        </template>
        <div class="contBox" style="height: 60vh; display: flex; flex-direction: column;">
          <div class="alert-tip" style="background-color: #fffbe6; border: 1px solid #ffe58f; padding: 12px 16px; margin-bottom: 20px; border-radius: 4px; color: #fa8c16; font-size: 14px; flex-shrink: 0;">
            <el-icon style="vertical-align: middle; margin-right: 8px;"><Warning /></el-icon>
            请仔细确认规则列表，开始审查后，无法修改文件的审查规则。
          </div>
          <div class="selected-count" style="margin-bottom: 12px; font-size: 14px; color: #606266; flex-shrink: 0;">
            已选中 <span style="color: #409eff; font-weight: bold;">{{ allRulesList.length }}</span> 条规则
          </div>
          <div class="tableCont" style="flex: 1; overflow: hidden;">
            <el-table
              border
              tooltip-effect="light"
              :data="allRulesList"
              height="100%"
            >
              <el-table-column type="index" label="序号" align="center" width="80" />
              <el-table-column prop="language" label="编程语言" align="center" min-width="120px" show-overflow-tooltip>
                <template #default="scope">
                  {{ Array.isArray(scope.row.language) ? scope.row.language.join(', ') : (scope.row.language===`C_PLUS`?`C++`:scope.row.language) }}
                </template>
              </el-table-column>
              <el-table-column prop="ruleType" label="规则类型" align="center" min-width="150px" show-overflow-tooltip />
              <el-table-column prop="ruleSource" label="规则来源" align="center" min-width="150px" show-overflow-tooltip />
              <el-table-column prop="desc" label="规则描述" align="left" min-width="300px" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right" />
            <div class="btn">
              <el-button @click="closeRuleConfirmModal">取消</el-button>
              <el-button type="primary" @click="confirmReview">确认</el-button>
            </div>
          </div>
        </template>
      </el-dialog>

      <!-- 审查规则动画弹窗 -->
      <el-dialog v-model="showRuleModal" class="dialogCont dialogCont60" title="" :show-close="false" width="80%" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">审查进度</div>
            <div class="right status">{{ isFinish ? `审查完成` : `审查进行中` }}</div>
          </div>
        </template>
        <div class="contBox contBox12">
           <div class="progressBox">
            <div class="progress">
              <div v-if="taskNum ==0" style="margin-right:20px">
                {{ reviewProgress.finishFilesize||0 }}/{{ reviewProgress.totalFilesize||0 }}
               </div>
              <el-progress :text-inside="true" :percentage="percentageValue" :stroke-width="23" striped />
              <div class="label">               
                <div v-if="taskNum!=0" class="taskNum">
                    资源排队中，您前面有 <span style="color: #FF4D4F;">{{ taskNum }}</span> 个任务在申请资源
                </div>
                <div v-else class="fileNameIng">
                  <span>正在审查: </span>
                  <el-tooltip :content="reviewProgress.fileName || ''" placement="top" effect="light">
                    <el-text class="review-file-name">{{ reviewProgress.fileName }}</el-text>
                  </el-tooltip>
                </div>
              </div>
            </div>
           </div>
          <div class="tableCont">
            <el-table ref="rulesListRef" border tooltip-effect="light" :data="fileRuleLists" height="100%">
              <el-table-column type="index" label="序号" align="center" width="100" />
              <el-table-column prop="ruleType" label="规则类型" align="left" width="240px" >
                <template #default="scope">
                  {{ scope.row.ruleSource + '-' + scope.row.ruleType }}
                </template>
              </el-table-column>
              <el-table-column prop="rule" label="规则名称" align="left" min-width="360px" show-overflow-tooltip />              
              <el-table-column prop="status" label="审查结果" align="center" width="200px" class-name="status">
                <template #default="scope">
                  <div :class="{ tg: reviewProgress.fileRule[scope.$index].status === 1, pszb:reviewProgress.fileRule[scope.$index].status === 3, notg:reviewProgress.fileRule[scope.$index].status === 2 }">
                    {{ reviewProgress.fileRule[scope.$index].status === 1 ? `通过` :reviewProgress.fileRule[scope.$index].status === 2 ? `未通过` : `审查中` }}
                    <div v-show="reviewProgress.fileRule[scope.$index].status === 1" class="dg">
                      <el-icon><Select /></el-icon>
                    </div>
                    <div v-show="reviewProgress.fileRule[scope.$index].status === 2" class="ng">
                      <!-- <i class="iconfont icon-butongguo1" />   -->
                       <el-icon><Close /></el-icon>                
                    </div>
                    <div v-show="reviewProgress.fileRule[scope.$index].status === 3" class="dot">
                      <div />
                      <div />
                      <div />
                    </div>
                  </div>
                </template>
              </el-table-column>
              <template #empty>
                <div class="tableLoading">
                  审查中
                  <div class="loadingd">
                    <div />
                    <div />
                    <div />
                  </div>
                </div>
              </template>
            </el-table>
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="btn">
              <el-button type="" @click="cancleDiaFn1"> 关闭</el-button>
              <el-button type="danger" @click="cancleDiaFn"> 停止审查</el-button>
              <el-button type="primary" :disabled="!isFinish" @click="goReviewResults(currentReviewFile)"> 查看审查结果 </el-button>
            </div>
          </div>
        </template>
      </el-dialog>
  
      <!-- 意见反馈 -->
      <el-dialog v-model="yjfkShow" class="dialogCont dialogCont1" title="" :show-close="false" width="878px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">意见反馈</div>
          </div>
        </template>
        <div class="contBox">
          <div class="tableCont">
            <el-form ref="ruleFormRef" style="max-width: 600px" :model="ruleForm" :rules="rules" label-width="auto" class="demo-ruleForm" status-icon>
              <el-form-item label="类型" prop="type">
                <el-radio-group v-model="ruleForm.type">
                  <el-radio value="1">系统问题</el-radio>
                  <el-radio value="2">优化建议</el-radio>
                  <el-radio value="3">功能需求</el-radio>
                  <el-radio value="4">点赞</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="优先级" prop="level">
                <el-radio-group v-model="ruleForm.level">
                  <el-radio value="1">高</el-radio>
                  <el-radio value="2">中</el-radio>
                  <el-radio value="3">低</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="标题" prop="title">
                <el-input v-model="ruleForm.name" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="描述" prop="desc">
                <el-input v-model="ruleForm.desc1" :rows="12" type="textarea" placeholder="描述" />
              </el-form-item>
              <el-form-item label="网表文件" prop="file1">
                <el-upload v-model:file-list="ruleForm.file1" class="upload-demo" action="" :on-remove="handleRemove" :limit="1" :on-exceed="handleExceed" :http-request="httpRequestFn">
                  <el-button type="primary">上传文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">请上传tel文件，大小不能超过500M.</div>
                  </template>
                </el-upload>
              </el-form-item>
              <el-form-item label="其他附件" prop="file2">
                <el-upload v-model:file-list="ruleForm.file2" class="upload-demo" action="" :on-remove="handleRemove" :limit="1" :on-exceed="handleExceed" :http-request="httpRequestFn1">
                  <el-button type="primary">上传文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">请上传jpg/png/word/pdf/zip文件，大小不能超过500M.</div>
                  </template>
                </el-upload>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right" />
            <div class="btn">
              <el-button @click="yjfkShow = false"> 关闭</el-button>
              <el-button type="primary" @click="submitFn(ruleFormRef)"> 提交</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
  
      <!-- 文件重名上传提示 -->
      <el-dialog v-model="uploadRename" title="提示" width="500" top="36vh">
        <span>{{ uploadRenameText }}</span>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="uploadRename = false"> {{ uploadForm.fileId ? '取消' : '关闭' }} </el-button>
            <el-button v-if="uploadForm.fileId" type="primary" @click="uploadRenameSubmit"> 上传 </el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 查看版本 -->
      <el-dialog v-model="viewVersionShow" class="dialogCont dialogCont4" title="" :show-close="false" width="1200px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">查看版本</div>
          </div>
        </template>
        <div class="contBox">
          <div class="title">
            电路图文件：<span>{{ rowData.tel_file }}</span>
          </div>
          <div class="tableCont">
            <el-table ref="versionTableRef" border tooltip-effect="light" :data="viewVersionListData" height="184px">
              <el-table-column prop="version" label="版本" align="center" />
              <el-table-column prop="total_cases" label="检查文件数" align="center" width="110px">
                <template #default="scope">
                  {{ scope.row.total_cases || `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="total_passed" label="通过检查文件数" align="center" width="130px">
                <template #default="scope">
                  {{ scope.row.total_passed || `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="isClosedLoop" label="状态" align="center" class-name="statusBox" width="120px">
                <template #default="scope">
                  <span :class="scope.row.isClosedLoop === 1 ? 'green' : 'red'" />
                  {{ scope.row.isClosedLoop === 1 ? "问题已关闭" : "问题未关闭" }}
                </template>
              </el-table-column>
              <el-table-column prop="formatted_percentage" label="通过率" align="center" width="100px">
                <template #default="scope">
                  {{ scope.row.formatted_percentage ? (scope.row.formatted_percentage * 100).toFixed(1) + "%" : `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="reviewTime" label="审查时间" align="center" width="160px">
                <template #default="scope">
                  {{ scope.row.reviewTime || `-` }}
                </template>
              </el-table-column>
  
              <el-table-column label="操作" align="center" :width="`140px`" class-name="opt">
                <template #default="scope">
                  <el-button type="primary" :disabled="!scope.row.reviewTime" size="small" class="btn" @click="goReviewResults(scope.row)"> 查看审查结果 </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right" />
            <div class="btn">
              <el-button @click="viewVersionShow = false"> 关闭</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <style lang="scss" scoped>
  .documentReview {
    display: flex;
    flex-direction: column;
    .tableBox {
      flex: 1;
      .tableWrapper {
        width: 100%;
        overflow-x: auto;
      }
      .tableWrapper :deep(.el-table) {
        width: 100% !important;
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
    font-size: 15px;
  
            }
          }
        }
      }
      .pagination-container {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        background-color: transparent;
        border-top: 1px solid #ebeef5;
        
      }
  
      :deep(.el-table) {
        background-color: transparent;
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
            &.el-button--danger{
              .el-loading-mask{
                background-color: rgba(250,181.5,181.5,0.5);
                .circular {
                  .path{
                    stroke:rgba(255, 0, 0,0.2)
                  }
                }
  
              }
            }
            &.btnNoHover {
              cursor: default;
              &.is-disabled {
                background-color: #28dad7 !important;
                color: #fff !important;
                border: 1px solid #28dad7 !important;
                cursor: not-allowed;
                border: 1px solid rgb(217, 217, 217) !important;
                &:hover {
                  background-color: #28dad7 !important;
                  color: #fff !important;
                  border: 1px solid #28dad7 !important;
                }
              }
            }
          }
          .wranTooltip {
            position: absolute;
            right: 4px;
            top: 15px;
            color: #8b8b8b;
          }
          .upload-demo12 {
            width: 80px;
            display: inline-block;
            margin-left: 12px;
          }
        }
        .redText {
          color: #f56c6c;
        }
      }
    }
  
    :deep(.dialogCont) {
      padding: 0;
      border: 0;
      background: transparent;
      margin-top: 35vh;
      &.dialogCont60{
        margin-top: 20vh;
        .el-dialog__body{
          height: calc( 60vh - 110px);
          .contBox12{
            .progressBox{
              .taskNum{
                font-size: 16px;
                font-weight: 500;
              }
              .progress{
                display: flex;
                align-items: center;
                gap: 12px;
                width: 80%;
                margin: 10px auto;
                .el-progress{
                  width: 70%;
                }
                .label{
                  width: 30%;
                  text-align: center;
                  
                  .review-file-name {
                    display: inline-block;
                    max-width: 80%;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    vertical-align: bottom;
                  }
                }
                .fileNameIng{
                  height: 23px;
                  line-height: 23px;
                  >span,.review-file-name{
                    display: inline-block;
                    vertical-align: middle;
                    height: 23px;
                    line-height: 23px;
                    &:first-child{
                      font-weight: 600;
                    }
                  }
                }
              }
            }
            .tableCont{
              height: calc( 100% - 42px) !important;
            }
          }
        }
      }
      &.dialogCont7{
        margin-top: 15vh;
        .el-dialog__body{
          height: auto;
          max-height: 70vh;
          overflow-y: auto;
        }
        .my-footer{
          justify-content: flex-end;
        }
      }
  
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
        height: 346px;
  
        .contBox {
          height: 100%;
          background-color: #fff;
          position: relative;
          padding: 10px 20px 0;
          .tableCont {
            height: 214px;
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
                    top: 1px;
                  }
                  .dg{
                    display: flex;
                    align-items: center;
                    position: absolute;
                    right: 4px;
                    top: 11px;
                    width: 30px;
                    height: 18px;
                    .el-icon{
                      margin-left: 3px;
                      color: #19bb5a;
                      font-size: 16px;
                    }
                  }
                  .ng{
                    display: flex;
                    align-items: center;
                    position: absolute;
                    right: 4px;
                    top: 11px;
                    width: 30px;
                    height: 18px;
                    .el-icon{
                      margin-left: 3px;
                      color: red;
                      font-size: 16px;
                    }
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
          .tableLoading {
              display: flex; /*让dot和文字在同一行*/
              align-items: center;
              justify-content: center;
              width: 100%;
              height: 100%;
              color: #2487ff;
              .loadingd {
                width: 28px;
                height: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 0;
                color: #2487ff;
                .la-dark {
                  color: #333;
                }
                > div {
                  display: inline-block;
                  float: none;
                  background-color: currentColor;
                  border: 0 solid currentColor;
                  width: 3px;
                  height: 3px;
                  margin: 1px;
                  border-radius: 100%;
                  animation: ball-beat 0.7s -0.15s infinite linear;
                  &:nth-child(2n-1) {
                    animation-delay: -0.5s;
                  }
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
            }
            .tableLoading1{
              height: 30px;
            }
        }
      }
      &.dialogCont60{
        margin-top: 20vh;
        .el-dialog__body{
          height: calc( 60vh - 110px);
          .contBox>.tableCont {
            height: 100%;
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
        margin-top: 15vh;
        .el-dialog__body {
          padding: 0;
          height: 600px;
          .el-form {
            padding: 20px 20px 0 20px;
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
          max-height: 800px;
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
              height: calc(100% - 40px);
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
  
  .rule-list {
    max-height: 400px;
    overflow-y: auto;
    padding: 10px;
  
    .rule-item {
      padding: 8px;
      border-bottom: 1px solid #eee;
      animation: fadeIn 0.3s ease-in-out;
    }
  
    .loading-text {
      text-align: center;
      font-size: 16px;
      color: #409eff;
  
      i {
        margin-left: 8px;
        animation: rotating 2s linear infinite;
      }
    }
  }
  
  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  @keyframes rotating {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  </style>
  