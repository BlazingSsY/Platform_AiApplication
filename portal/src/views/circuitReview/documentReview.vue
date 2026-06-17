<script setup lang="ts">
  import localTitle from "./components/localTitle.vue"
  import {
    circuitFileIsRecycle,
    uploadCheckFile,
    deleteCircuitFile,
    getCircuitFilePage,
    createCircuitReview,
    uploadFile,
    getCircuitReviewRules,
    getCircuitReviewResult,
    getCircuitReviewRuleSummary,
    circuitReviewResultFileId,
    uploadVersionFile,
    circuitFileVersion,
    circuitFileVersionResult,
    circuitFileRecyclePage,
    circuitFileResultDel,
    checkInAudit
  } from "@/ajax/circuitreview"
  import { GroupUserList, getDepartmentsSimpleItems } from "@/ajax/index"
  import { dayjs, genFileId } from "element-plus"
  import fileSecretLevelEnum, { getFileSecretLevelEnumStr } from "@/hocks/fileSecretLevelEnum"
  import store from "@/store/index"
  import { is631Admin } from "@/hooks/user631Admin"
  import { isAdmin } from "@/hooks/userIsAdmin"
  import { isAdminAndJz } from "@/hooks/userAdminAndJz"
  const is631UserAdmin = is631Admin()
  const isAdminUser = isAdmin()
  const isUserAdminAndJz = isAdminAndJz()

  const userInfo = computed(() => store.state.user.userInfo)
  const userId = computed(() => userInfo.value?.id)
  const roleInfo = computed(() => userInfo.value?.role)
  const roleName = computed(() => {
    // name 普通用户
    return roleInfo.value ? roleInfo.value.name : ``
  })
  
  // userInfo.value?.loginName===`admin`
  const loginName = computed(() => userInfo.value?.loginName)
  const userDepartmentId = computed(() => userInfo.value.departmentId)
  
  const fileSecretLevel = fileSecretLevelEnum()
  
  const fileslistDataAll = ref<any>([])
  const rulesListRef = ref()
  const rulesListDataLocal = ref([])
  const rulesListData = ref<any>([])
  const updatedRulesListData = ref<any>([])
  
  const departmentsData = reactive([])
  const usersData = ref([])

  // 控制用户名列显示状态
  const showOwnerNameColumn = ref(false)

  // 监听用户名列显示状态变化，强制表格重新渲染以修复背景色问题
  watch(() => showOwnerNameColumn.value, () => {
    nextTick(() => {
      tableRef.value?.doLayout()
    })
  })

  const getDepartments = () => {
    getDepartmentsSimpleItems({ departmentId: userDepartmentId.value })
      .then(res => {
        if (res.succ) {
          departmentsData.splice(0)
          res.content.forEach(item => {
            departmentsData.push({ ...item })
          })
          if (!isUserAdminAndJz.value) {
            searchObj.depId = departmentsData[0].id
            depChange()
          }
        }
      })
      .catch(error => {
        ElMessage.error("获取单位数据失败", error)
      })
  }
  getDepartments()
  const searchObj = reactive({
    depId: ``,
    userId: ``,
    fileName:``
  })
  const depChange = () => {
    const params_ = {
      "departmentId": searchObj.depId
    }
    params_.pageSize = 10000
    GroupUserList(params_).then(res => {
      if (res.succ) {
        if (res.content && res.content.records) {
          usersData.value = res.content.records
          searchObj.userId = ``
        }
      }
    })
  }
  // 分页相关参数
  const pagination = ref({
    "current": 1,
    "size": 50,
    "total": 0
  })
  
  const route = useRoute()
  if (store.state.pagesInfoStroe && store.state.pagesInfoStroe[route.path]) {
    console.log(store.state.pagesInfoStroe[route.path])
    pagination.value.size = store.state.pagesInfoStroe[route.path].size
    pagination.value.current = store.state.pagesInfoStroe[route.path].current
    if (store.state.pagesInfoStroe[route.path].searchObj) {
      searchObj.depId = store.state.pagesInfoStroe[route.path].searchObj.depId ?? ``
      searchObj.userId = store.state.pagesInfoStroe[route.path].searchObj.userId ?? ``
      searchObj.fileName = store.state.pagesInfoStroe[route.path].searchObj.fileName ?? ``
      if(searchObj.depId){
        depChange()
      }
    }
  }
  
  // 获取审查规则列表
  getCircuitReviewRules().then(res => {
    if (res.succ) {
      rulesListDataLocal.value = res.content.map((r: any) => {
        return {
          "label": r.name.replace(/^\s+|\s+$|[.；;!！?？:：,，、\[\]【】\{\}""']+$/gu, ``), // 去掉前后空格和结尾标点符号
          "status": `审查中`
        }
      })
    }
  })
  
  const loading = ref(false)
  const loadingType = ref(1)
  
  interface TableDataRow {
    tel_file: string
    id?: string
    total_cases?: number
    total_passed?: number
    formatted_percentage?: number | string
    loading?: boolean
  }
  
  const recordsObjData = ref<any>({})
  const sortStroe = reactive({
    sortField: ``,
    sortDirection: ``
  })
  const tableRef = ref()
  // 获取电路图文件列表
  const fetchCircuitFiles = async (page = 1, pageSize = 10) => {
    const params: any = {
      ...searchObj,
      ...sortStroe,
      "pageNumber": page,
      pageSize
    }
  
    const res = await getCircuitFilePage(params)
    if (res.succ) {
      // 直接使用新的数据结构
      fileslistDataAll.value = res.content.records.map((item: any) => ({
        "id": item.id,
        "version": item.version,
        "fileVersionId": item.fileVersionId,
        "resultId": item.resultId, // 添加resultId字段
        "tel_file": item.fileName,
        "fileId": item.minioId,
        "total_cases": item.checkPoints || 0,
        "total_passed": item.passCheckPoints || 0,
        "total_failed": item.failCheckPoints || 0,
        "formatted_percentage": item.passRate || 0,
        "loading": false,
        "reviewTime": item.reviewTime || ``,
        "departmentName": item.departmentName || ``,
        "ownerName": item.ownerName || ``,
        "ownerId": item.ownerId || ``,
        "isClosedLoop": item.isClosedLoop,
        "minioId": item.minioId || ``,
        "secretLevel": item.secretLevel,
        "recordsList": item.recordsList || [],
            "compatibleModels": item.compatibleModels || ``, // 配套机型
        "productModel": item.productModel || ``, // 产品型号
        "productName": item.productName || ``, // 产品名称
        "diagramNumber": item.diagramNumber || ``, // 电路原理图号
        "diagramVersion": item.diagramVersion || ``, // 版本
      }))
  
      // 更新分页信息
      pagination.value = {
        "current": res.content.current,
        "size": res.content.size,
        "total": res.content.total
      }
    }
  }
  
  const initData = async () => {
    loadingType.value = 1
    loading.value = true
    await fetchCircuitFiles(pagination.value.current, pagination.value.size)
    loading.value = false
  }
  onMounted(()=>{
    if(roleName.value){
      initData()
    }else{
      setTimeout(()=>{   
        initData()
      },300)
    }
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
  
  const keyNameMap = {
    "规则类型": `rule_type`,
    "审查规则": `rule_info`,
    "器件型号": `component_model_name`,
    "位号引脚": `position_pin_num`,
    "审查意见": `result_msg`,
    "是否通过": `passed`
  }
  
  // 审查记录
  const router = useRouter()
  const goReviewResults = item => {
    if (item.reviewTime) {
      // 通过路由参数传递数据，而不是使用localStorage
      store.commit("SET_PAGES_INFO_STROE", { path: route.path, info: { ...pagination.value, searchObj } })
      router.push({
        "path": `/reviewResults`,
        "query": {
          "item": encodeURIComponent(JSON.stringify(item))
        }
      })
    }
  }
  
  const isFinish = ref(false)
  const finishInfo = ref({})
  const gdNum = ref(0)
  const reviewFn = async(item,type?) => {
    if(item.isClosedLoop == 1){
      ElMessage.warning(`问题已关闭，无需再审查!`)  
      return true
    }
    if(type){
      const res = await checkInAudit({fileId:item.id})
      // 复核中，不能再次审查
      if(res.succ && res.content.inAudit){
        ElMessage.warning(`复核中，不能再次审查!`)
        return true
      }
    }
    isFinish.value = false
    showRuleModal.value = true
    isApiResponded.value = false
    currentReviewFile.value = item
    currentDisplayIndex.value = 0
    gdNum.value = 0
    
    // 清除之前的定时器
    if (resultDisplayTimer) {
      clearInterval(resultDisplayTimer)
      resultDisplayTimer = null
    }
    
    rulesListData.value = JSON.parse(JSON.stringify(rulesListDataLocal.value)).map(item => ({
      ...item,
      "status": `审查中`
    }))
    console.log(rulesListData.value)
    isShowingRules.value = true
    nextTick(() => {
      const ruleList = document.querySelector(`.tableCont`)
      const lastRule = ruleList?.querySelector(`.el-scrollbar__wrap `)
      if (lastRule) {
        lastRule.scrollTop = 0
      }
    })

    item.loading = true
    createCircuitReview({ "fileVersionId": item.fileVersionId })
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
                    getCircuitReviewRuleSummary(reviewId) // 12-25 用summary  （getCircuitReviewRuleSummary 里的rule）覆盖进度窗口的rules -- rulesListData
                      .then(summaryRes => {
                        if (summaryRes.succ) {
                          // 更新审查时间
                          item.reviewTime = resultData.reviewTime
                          currentReviewFile.value.reviewTime = item.reviewTime
                          currentReviewFile.value.resultId = reviewId
  
// 更新统计数据
                          item.total_cases = resultData.checkPoints
                          item.total_passed = resultData.passCheckPoints
                          item.total_failed = resultData.failCheckPoints
                          item.formatted_percentage = resultData.passRate
                          
                          // 存储审查结果数据，不直接更新显示
                          reviewResultsData.value = []
                          summaryRes.content.forEach((ruleItem: any) => {
                            const ruleName = ruleItem.rule.name.replace(/^\s+|\s+$|[.；;!！?？:：,，、\[\]【】\{\}""']+$/gu, ``)
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
                          
                          fetchCircuitFiles(pagination.value.current, pagination.value.size)
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
  
  const delFn = item => {
    ElMessageBox.confirm(`确定删除?`, `提示`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
      .then(() => {
        circuitFileIsRecycle({
          fileIdList: [item.id],
          isRecycle: 1
        }).then(res => {
          if (res.succ) {
            ElMessage.success(`移入网表文件回收站成功！`)
            setTimeout(() => {
              // 删除后重新加载当前页数据
              fetchCircuitFiles(pagination.value.current, pagination.value.size)
            }, 200)
          }
        })
      })
      .catch(() => {})
  }
  const delRecordsFn = item => {
    ElMessageBox.confirm(`确定删除审查记录?`, `提示`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
      .then(() => {
        circuitReviewResultFileId(item).then(res => {
          if (res.succ) {
            ElMessage.success(`删除审查记录成功！`)
            setTimeout(() => {
              // 删除后重新加载当前页数据
              fetchCircuitFiles(pagination.value.current, pagination.value.size)
            }, 200)
          }
        })
        // circuitFileIsRecycle({
        //   fileIdList: [item.id],
        //   isRecycle: 1
        // }).then(res => {
        //   if (res.succ) {
        //     ElMessage.success(`移入网表文件回收站成功！`)
        //     setTimeout(() => {
        //       // 删除后重新加载当前页数据
        //       fetchCircuitFiles(pagination.value.current, pagination.value.size)
        //     }, 200)
        //   }
        // })
      })
      .catch(() => {})
  }
  // 下载电路图文件
  const downloadFn = item => {
    if (item.fileId) {
      // 分离文件名和后缀
      const lastDotIndex = item.tel_file.lastIndexOf('.')
      let fileNameWithoutExt, fileExt
      if (lastDotIndex > 0) {
        fileNameWithoutExt = item.tel_file.substring(0, lastDotIndex)
        fileExt = item.tel_file.substring(lastDotIndex) // 包含点号，如 .tel
      } else {
        fileNameWithoutExt = item.tel_file
        fileExt = ''
      }
      
      // 组合新的文件名：文件名_部门_所有者.后缀
      const newFileName = `${fileNameWithoutExt}_${item.departmentName}_${item.ownerName}${fileExt}`
      window.open(`/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(newFileName)}`)
    }
  }
  const crushFn = async item => {
    // 检查权限：admin可以粉碎任何文件，test-开头可以随时粉碎
    // 没有审查结果且是当前用户上传的文件且只有1个版本时可以粉碎，其他正式文件必须关闭后才能粉碎
    const isAdmin = loginName.value === 'admin'
    const isTestFile = item.tel_file?.startsWith('test-')
    const isClosed = item.isClosedLoop === 1
    const isOwnerAndNoReview = !item.reviewTime && item.ownerName === loginName.value

    // 如果是"无审查结果+当前用户上传"的情况，需要额外检查版本数量
    if (isOwnerAndNoReview && !isAdmin && !isTestFile) {
      const res = await circuitFileVersion({ fileId: item.id })
      if (res.succ && res.content.length > 1) {
        ElMessage.warning(`文件有多个版本，不能粉碎！`)
        return
      }
    }

    if (!isAdmin && !isTestFile && !isOwnerAndNoReview && !isClosed) {
      ElMessage.warning(`网表文件只有关闭后才能被粉碎!`)
      return
    }

    ElMessageBox.confirm(`确定粉碎?`, `提示`, {
      "confirmButtonText": `确定`,
      "cancelButtonText": `取消`,
      "type": `warning`
    })
      .then(() => {
        deleteCircuitFile([item]).then(res => {
          if (res.succ) {
            ElMessage.success(`粉碎文件成功！`)
            setTimeout(() => {
              // 删除后重新加载当前页数据
              fetchCircuitFiles(pagination.value.current, pagination.value.size)
            }, 200)
          }
        })
        // circuitFileIsRecycle({
        //   fileIdList: [item.id],
        //   isRecycle: 1
        // }).then(res => {
        //   if (res.succ) {
        //     deleteCircuitFile([item]).then(res => {
        //       if (res.succ) {
        //         ElMessage.success(`粉碎文件成功！`)
        //         setTimeout(() => {
        //           // 删除后重新加载当前页数据
        //           fetchCircuitFiles(pagination.value.current, pagination.value.size)
        //         }, 200)
        //       }
        //     })
        //   }
        // })
      })
      .catch(() => {})
  }
  
  // 上传文件
  const uploadFormRef = ref()
  const uploadShow = ref(false)
  // 配套机型 compatibleModels、产品型号 productModel、产品名称 productName、电路原理图号 diagramNumber、版本 diagramVersion 
  const uploadForm = reactive({
    fileSecretLevelEnum: `PUBLIC`,
    file: null,
    fileId: ``, 
    isTestFile:0, //0：否；1：是
    isHasproductModel:false,
    compatibleModels: ``, // 配套机型
    productModel: ``, // 产品型号
    productName: ``, // 产品名称
    diagramNumber: ``, // 电路原理图号
    diagramVersion: ``, // 版本
  })
  const validateFile = (rule: any, value: any, callback: any) => {
    if (!value) {
      callback(new Error("请上传电路文件"))
    } else {
      callback()
    }
  }
  const uploadRules = reactive({
    "file": [{ "required": true, validator: validateFile, trigger: "change" }],
    "compatibleModels": [{ "required": true, "message": `请输入配套机型`, "trigger": `change` }], //配套机型
    "productModel": [{ "required": false, "message": `请输入产品型号`, "trigger": `blur` }],
    "productName": [{ "required": false, "message": `请输入产品名称`, "trigger": `blur` }],
    "diagramNumber": [{ "required": false, "message": `请输入电路原理图号`, "trigger": `blur` }],
    "diagramVersion": [{ "required": false, "message": `请输入版本`, "trigger": `blur` }]
  })
  const handleIsHasproductModelChange = () => {
    const isRequired = uploadForm.isHasproductModel
    // 重新创建 rules 对象以触发 Vue 的响应式更新
    Object.assign(uploadRules, {
      file: [{ required: true, validator: validateFile, trigger: "change" }],
      compatibleModels: [{ required: isRequired, message: `请输入配套机型`, trigger: `change` }],
      productModel: [{ required: isRequired, message: `请输入产品型号`, trigger: `blur` }],
      productName: [{ required: isRequired, message: `请输入产品名称`, trigger: `blur` }],
      diagramNumber: [{ required: isRequired, message: `请输入电路原理图号`, trigger: `blur` }],
      diagramVersion: [{ required: isRequired, message: `请输入版本`, trigger: `blur` }]
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
    // uploadForm.isTestFile = 1 //0：否；1：是
    // uploadForm.isHasproductModel = false

     uploadForm.isTestFile = isAdminUser.value ? 1 : 0 //0：否；1：是
    uploadForm.isHasproductModel = isAdminUser.value ? false : true

    uploadForm.compatibleModels = `` // 配套机型
    uploadForm.productModel = `` // 产品型号
    uploadForm.productName = `` // 产品名称
    uploadForm.diagramNumber = `` // 电路原理图号
    uploadForm.diagramVersion = `` // 版本
    isTestFile.value = false
    uploadShow.value = true

    nextTick(()=>{
      handleIsHasproductModelChange()
    })
  }
  
  // 上传重命名提示文件
  const uploadRename = ref(false)
  const uploadRenameText = ref(``)
  const uploadRenameSubmit = () => {
    if(uploadForm.fileId){
      loading.value = true
      loadingType.value = 2
      const fd = new FormData() // 新建一个FormData()对象，这就相当于你新建了一个表单
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

      uploadVersionFile({
        fileId: uploadForm.fileId,
        // fileName :uploadForm.file.name,
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
    }else{
      uploadRename.value = false
    }
    
    // uploadFile({ ...uploadForm, file: fd })
    //   .then(res => {
    //     loading.value = false
    //     if (res.succ) {
    //       // 上传后重新加载第一页数据
    //       uploadRename.value = false
    //       uploadShow.value = false
    //       pagination.value.current = 1
    //       fetchCircuitFiles(1, pagination.value.size)
    //     }
    //   })
    //   .catch(error => {
    //     loading.value = false
    //     ElMessage.error(`上传失败, 请重试或联系管理员`, error)
    //   })
  }
  const cancleFn = () => {
    uploadRename.value = false
    uploadShow.value = false
    loading.value = false
  }
	const isJiZaiUser = computed(() => store.state.isJiZaiUser)
  const isTestFile = ref(false)
  console.log('isJiZaiUser', isJiZaiUser.value)
  // 可以添加页脚数据或方法
  const VITE_APP_IS_JIZAI = isJiZaiUser.value
  const fileType = VITE_APP_IS_JIZAI?`.atel,.tel`: `.tel`
  console.log('fileType', fileType)
  const accept1 = fileType
  const uploadElementAudio = ref()
  const handleBeforeAudio = async file => {
    // eslint-disable-next-line no-useless-escape
    // const suffix = accept1.replace(/./g, ``).replace(/\,/, `|`)
    const fileSize = file.size / 1024 / 1024 < 100
    const fileExtension = file.name.split('.').pop().toLowerCase();
    if (fileType.toLowerCase().indexOf(`.${fileExtension}`) === -1) {
      ElMessage.error(`只支持 ${accept1} 类型文件`)
      return false
    } else if (!fileSize) {
      ElMessage.error(`文件必须小于100M`)
      return false
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

    

    const result = await uploadCheckFile({fileName,isTestFile:uploadForm.isTestFile})
    if(result.succ && result.content.inAudit&&result.content.exist){
      uploadForm.file = file
      uploadForm.fileId = result.content.fileId
      uploadRenameText.value = `当前版本审查结果复核中，上传新版本会取消当前复核，是否上传新版本？`
      uploadRename.value = true
    } else if(result.succ && !result.content.inAudit&&result.content.exist){
      uploadForm.file = file
      uploadForm.fileId = result.content.fileId
      uploadRenameText.value = `该文件名已经存在，将形成文件新版本，是否继续上传？`
      uploadRename.value = true
    }else if(result.succ && result.content.conflictWithRecycledFile){
      uploadForm.file = null
      uploadForm.fileId = ``
      uploadElementAudio.value!.clearFiles()
      uploadRenameText.value = `统计分析中已存在该文件名称，需更改名称后上传审查。`
      uploadRename.value = true
    }
    return true
  }
  const handleError = error => {
    ElMessage.error(`上传失败, 请重试`, error)
  }
  const onRemove = () => {
    console.log(`onRemove`)
    // uploadForm.file = null
  }
  
  const uploadSubmit = async (formEl: any) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        loading.value = true
        loadingType.value = 2
        const fd = new FormData() // 新建一个FormData()对象，这就相当于你新建了一个表单
        
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
        
        uploadFile({ ...uploadForm, file: fd })
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
  const handleExceedAudio = files => {
    uploadElementAudio.value!.clearFiles()
    const file = files[0]
    file.uid = genFileId()
    uploadElementAudio.value!.handleStart(file)
    uploadElementAudio.value!.submit()
  }
  const httpRequestAudio = (fileObject: any) => {
    uploadForm.file = fileObject.file
    // loading.value = true
    // loadingType.value = 2
    // const fd = new FormData() // 新建一个FormData()对象，这就相当于你新建了一个表单
    // fd.append(`file`, fileObject.file) // 将文件保存到formData对象中
    // uploadFile(fd)
    //   .then(res => {
    //     loading.value = false
    //     if (res.succ) {
    //       // 上传后重新加载第一页数据
    //       pagination.value.current = 1
    //       fetchCircuitFiles(1, pagination.value.size)
    //     }
    //   })
    //   .catch(error => {
    //     loading.value = false
    //     ElMessage.error(`上传失败, 请重试或联系管理员`, error)
    //   })
    // return Promise.resolve()
  }
  
  const goReviewStatistics = () => {
    router.push({
      "path": `/reviewStatistics`
    })
  }
  
  // 分页处理
  const handleCurrentChange = (page: number) => {
    pagination.value.current = page
    fetchCircuitFiles(page, pagination.value.size)
    nextTick(() => {
      document.getElementById(`layoutWrap`).scrollTo({
        top: 50,
        behavior: "smooth"
      })
    })
  }
  
  const handleSizeChange = (size: number) => {
    pagination.value.size = size
    pagination.value.current = 1 // 切换每页大小时重置到第一页
    fetchCircuitFiles(1, size)
    nextTick(() => {
      document.getElementById(`layoutWrap`).scrollTo({
        top: 50,
        behavior: "smooth"
      })
    })
  }
  
  const restFn = () => {
    searchObj.depId = ``
    searchObj.userId = ``
    searchObj.fileName = ``
    // 除了jzadmin和admin，其他都锁死成自己单位吧
    if (!isUserAdminAndJz.value) {
      searchObj.depId = departmentsData[0].id
    }
    initData()
  }
  const sortObj = {
    "tel_file": `FILE_NAME`,
    "departmentName": `DEPARTMENT_NAME`,
    "ownerName": `OWNER_NAME`,
    "compatibleModels": `COMPATIBLE_MODELS`, // 新增
    "productModel": `PRODUCT_MODEL`, // 新增
    "productName": `PRODUCT_NAME`, // 新增
    "diagramNumber": `DIAGRAM_NUMBER`, // 新增
    "diagramVersion": `DIAGRAM_VERSION`, // 新增
    "total_cases": `CHECK_POINTS`,
    "total_passed": `PASS_CHECK_POINTS`,
    "total_failed": `FAIL_CHECK_POINTS`,
    "formatted_percentage": `PASS_RATE`,
    "reviewTime": `REVIEW_TIME`
  }
  // 表格排序处理
  const handleSortChange = ({ prop, order }) => {
    if (order) {
      sortStroe.sortField = sortObj[prop] ?? ``
      sortStroe.sortDirection = order === `ascending` ? `ASC` : order === `descending` ? `DESC` : ``
    } else {
      sortStroe.sortField = ``
      sortStroe.sortDirection = ``
    }
  
    initData()
  }
  // 审查规则动画相关
  const showRuleModal = ref(false)
  const displayedRules = ref<string[]>([])
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
    console.log(`handleVisibilityChange`)
    if (!document.hidden && showRuleModal.value) {
      if (currentPollFn.value) {
        console.log(`轮询还在跑，立即拉最新状态`)
        // 情况1：轮询还在跑，立即拉最新状态
        currentPollFn.value()
      } else if (reviewResultsData.value.length > 0) {
        console.log(`后台已拿到结果但动画还没跑完，直接跳到结尾`)
        // 情况2：后台已拿到结果但动画还没跑完，直接跳到结尾
        if (resultDisplayTimer) {
          clearInterval(resultDisplayTimer)
          resultDisplayTimer = null
        }
        // 把所有结果一次性应用到 rulesListData
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
  
  const startRuleAnimation = () => {
    isShowingRules.value = true
  }
  
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
  
  const generalCheckpoint = computed(() => {
    return updatedRulesListData.value.length
  })
  const passedCheckpoint = computed(() => generalCheckpoint.value - updatedRulesListData.value.filter(r => r.status.includes(`未通过`)).length)
  const passingRate = computed(() => {
    const passRate = (passedCheckpoint.value / generalCheckpoint.value) * 100
    return passRate.toFixed(1) + `%`
  })
  
  // 意见反馈
  const ruleFormRef = ref()
  const ruleForm = reactive<any>({
    "type": `1`,
    "level": `1`,
    "title": ``,
    "desc1": `重现步骤:
  
  
  预期结果:
  
  
  实际结果:`,
    "desc2": ``,
    "desc3": ``,
    "file1": [],
    "file2": []
  })
  const rules = reactive<any>({
    "type": [{ "required": true, "message": `请输入标题`, "trigger": `blur` }],
    "level": [{ "required": true, "message": `请输入标题`, "trigger": `blur` }],
    "title": [{ "required": true, "message": `请输入标题`, "trigger": `blur` }]
  })
  const yjfkShow = ref(false)
  const yjfkFn = () => {
    yjfkShow.value = true
  }
  const submitFn = async (formEl: any) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
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
    // ruleForm.file2.push(file);
    return Promise.resolve()
  }
  const httpRequestFn = (file: any) => {
    console.log(file)
    // ruleForm.file1.push(file);
    return Promise.resolve()
  }
  const handleExceed = file => {
    console.log(file)
  }
  
  // 规则列表
  const gzlbShow = ref(false)
  const gzlbFn = async () => {
    // 每次点击都获取最新的审查规则数据
    // const res = await getCircuitReviewRules();
    // if (res.succ) {
    // 	rulesListDataLocal.value = res.content.map((r: any) => {
    // 		return {
    // 			'label': r.name.replace(/^\s+|\s+$|[.；;!！?？:：,，、()\[\]【】\{\}""']+$/gu, ``), // 去掉前后空格和结尾标点符号
    // 			'status': `审查中`
    // 		};
    // 	});
    // }
    // gzlbShow.value = true;
    router.push({ path: `/ruleList` })
  }
  
  // 查看版本
  const rowData = ref(null)
  const viewVersionShow = ref(false)
  const viewVersionListData = ref<any>([])
  const viewVersionFn = row => {
    rowData.value = row
    circuitFileVersionResult({ fileId: row.id }).then(res => {
      if (res.succ) {
        viewVersionListData.value = res.content.map((item: any) => ({
          // "id": item.id,
          "version": item.fileName,
          "fileVersionId": item.fileVersionId,
          "resultId": item.resultId, // 添加resultId字段
          "tel_file": row.tel_file,
          "id": item.fileId,
          "total_cases": item.checkPoints || 0,
          "total_passed": item.passCheckPoints || 0,
          "total_failed": item.failCheckPoints || 0,
          "formatted_percentage": item.passRate || 0,
          "loading": false,
          "reviewTime": item.reviewTime || ``,
          "departmentName": item.departmentName || ``,
          "ownerName": item.ownerName || ``,
          "isClosedLoop": item.isClosedLoop,
          "minioId": item.minioId || ``,
          "fileId": item.fileId || ``,
          "secretLevel": item.secretLevel,
          "recordsList": item.recordsList || []
        }))
        viewVersionShow.value = true
      }
    })
  }
  
  // 问题未关闭
  const uploadElementVersion = ref({})
  const uploadFormVersion = ref({})
  const handleBeforeVersion = async (file, row) => {
    const fileSize = file.size / 1024 / 1024 < 100
  
    const fileExtension = file.name.split('.').pop().toLowerCase()
    // if (fileExtension !== fileType.toLowerCase()) {
   if (fileType.toLowerCase().indexOf(`.${fileExtension}`) === -1) {
      ElMessage.error(`只支持 ${accept1} 类型文件`)
      return false
    } else if (!fileSize) {
      ElMessage.error(`文件必须小于100M`)
      return false
    }
    uploadFormVersion.value = row
    const res = await checkInAudit({fileId:row.id})
    // 复核中，不能再次审查
    if(res.succ && res.content.inAudit){
      // ElMessage.warning(`复核中，不能再次审查!`)
      uploadRenameText.value = `当前版本审查结果复核中，上传新版本会取消当前复核，是否上传新版本？`
      uploadRename.value = true
      uploadForm.file = file
      uploadForm.fileId = row.id
      return false
    }
    return true
  }
  const handleExceedVersion = files => {
    uploadElementVersion.value!.clearFiles()
    const file = files[0]
    file.uid = genFileId()
    uploadElementVersion.value!.handleStart(file)
    uploadElementVersion.value!.submit()
  }
  const httpRequestVersion = (fileObject: any) => {
    loading.value = true
    loadingType.value = 2
    const fd = new FormData() // 新建一个FormData()对象，这就相当于你新建了一个表单
    fd.append(`file`, fileObject.file) // 将文件保存到formData对象中
    uploadVersionFile({
      // fileName :uploadFormVersion.value.tel_file,
      fileId: uploadFormVersion.value.id,
      fileSecretLevelEnum: `PUBLIC`,
      file: fd
    })
      .then(res => {
        loading.value = false
        if (res.succ) {
          // pagination.value.current = 1
          fetchCircuitFiles(pagination.value.current, pagination.value.size)
        }
      })
      .catch(error => {
        loading.value = false
        ElMessage.error(`上传失败, 请重试或联系管理员`, error)
      })
  }

  // 查看已粉碎文件
  const shreddedFilesShow = ref(false)
  const shreddedFilesPagination = ref({ 
    "current": 1,
    "size": 50,
    "total": 0
  })
  const searchObjShreddedFiles = reactive({
    depId: ``,
    userId: ``,
    fileName:``
  })
  const depsearchObjShreddedFilesUsersData = ref([])
  const depsearchObjShreddedFilesChange = () => {
    const params_ = {
      "departmentId": searchObjShreddedFiles.depId
    }
    params_.pageSize = 10000
    GroupUserList(params_).then(res => {
      if (res.succ) {
        if (res.content && res.content.records) {
          depsearchObjShreddedFilesUsersData.value = res.content.records
          searchObjShreddedFiles.userId = ``
        }
      }
    })
  }
  const shreddedFilesListData = reactive([])
  const selectedRowsAct = ref([])
  const handleSelectionChange = (val: any) => {
    selectedRowsAct.value = val
  }
  const batchDelFn = (row)=>{
    ElMessageBox.confirm(`确认删除？`, `提示`, {
      confirmButtonText: `确定`,
      cancelButtonText: `取消`,
      type: `warning`
    }).then(() => {
      let resultData = []
      if(row){
        resultData = [{version:row.version,id:row.fileId,label:row.fileName}]
      }else{
        resultData = selectedRowsAct.value.map(r=>({version:r.version,id:r.fileId,label:r.fileName}))
      }
        circuitFileResultDel(resultData).then(res=>{
          if (res.succ) {
            ElMessage.success(`删除成功`)
            searchObjShreddedFilesInitData()
          }
        })  
    })
     
  }
  const searchObjShreddedFilesInitData = ()=>{
    circuitFileRecyclePage({
      "pageNumber": shreddedFilesPagination.value.current,
      "pageSize": shreddedFilesPagination.value.size,
      ...searchObjShreddedFiles
    }).then(res=>{
      if (res.succ) {
        if (res.content && res.content.records) {
          shreddedFilesListData.splice(0)
          res.content.records.forEach((item: any) => {
            shreddedFilesListData.push(item)
          })
        }
        shreddedFilesPagination.value.total = res.content.total
      }
    })
  }
  const searchObjShreddedFilesRestFn = ()=>{
    searchObjShreddedFiles.depId = ``
    searchObjShreddedFiles.userId = ``
    searchObjShreddedFiles.fileName = ``
    shreddedFilesPagination.value.current = 1
    shreddedFilesPagination.value.size = 50
    shreddedFilesPagination.value.total = 0
    searchObjShreddedFilesInitData()
  }
  const shreddedFilesFn = () => {
    searchObjShreddedFiles.depId = ``
    searchObjShreddedFiles.userId = ``
    searchObjShreddedFiles.fileName = ``
    shreddedFilesPagination.value.current = 1
    shreddedFilesPagination.value.size = 50
    shreddedFilesPagination.value.total = 0
    if (!isUserAdminAndJz.value) {
      searchObjShreddedFiles.depId = departmentsData[0].id
      depsearchObjShreddedFilesChange()
    }
    searchObjShreddedFilesInitData()
    shreddedFilesShow.value = true
  }
  </script>
  
  <template>
    <div v-loading.fullscreen.lock="loading" class="documentReview" element-loading-background="rgba(122, 122, 122, 0.8)" :element-loading-text="loadingType === 1 ? `数据加载中...` : `文件上传中...`">
      <localTitle title="电路图列表">
        <template #rightBtn>
          <div style="display: flex; align-items: center; margin-left: 12px; gap: 8px;">
            <span style="font-size: 15px; color: #606266;">显示所有列</span>
            <el-switch v-model="showOwnerNameColumn" style="--el-switch-on-color: #67c23a; --el-switch-off-color: #dcdfe6; --el-switch-size: 20px;" size="large" />
          </div>
          <el-button v-if="loginName===`admin`" type="danger" icon="Tickets" style="margin-left: 12px" @click="shreddedFilesFn">查看已粉碎文件</el-button>
          <el-button type="primary" icon="Upload" style="margin-left: 12px" @click="uploadFn">上传文件</el-button>
          <!--        <el-button type="primary" icon="Tickets" style="margin-left: 12px" @click="gzlbFn">规则列表</el-button>-->
          <!-- <el-button type="primary" icon="ChatLineSquare" @click="yjfkFn"> 意见反馈 </el-button> -->
        </template>
      </localTitle>
      <div class="tableBox">
        <div class="search">
          <el-form ref="ruleFormRef" :inline="true">
            <el-form-item v-if="roleName && roleName !== `普通用户`" label="按单位">
              <el-select v-model="searchObj.depId" placeholder="选择单位" style="width: 240px" clearable :disabled="!isUserAdminAndJz" @change="depChange">
                <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item v-if="roleName && roleName !== `普通用户`" label="按用户名">
              <el-select v-model="searchObj.userId" :disabled="searchObj.depId === ''" placeholder="选择用户名" style="width: 240px" clearable>
                <el-option v-for="item in usersData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="网表文件名">
              <el-input v-model="searchObj.fileName" placeholder="输入网表文件名" style="width: 240px" clearable />
            </el-form-item>
          </el-form>
          <div>
            <el-button type="primary" @click="initData()">搜索</el-button>
            <el-button @click="restFn">重置</el-button>
          </div>
        </div>

        <div class="tableWrapper">
          <el-table
            ref="tableRef"
            border
            class="custom-table-size"
            :data="fileslistDataAll"
            :tooltip-options="{ placement: 'bottom' }"
            :fit="true"
            table-layout="fixed"
            style="width: 100%"
            :style="{ height: roleName && roleName == `普通用户` ? ` calc(100% - 60px)` : ` calc(100% - 160px)`, width: '100%' }"
            :header-cell-style="{ textAlign: 'center' }"
            tooltip-effect="light"
            @sort-change="handleSortChange"
          >
            <el-table-column label="序号" align="center" width="64">
              <template #default="scope">
                {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="tel_file" label="网表文件" align="left" show-overflow-tooltip sortable="custom" min-width="300px" />
            <el-table-column prop="departmentName" label="单位名称" align="center" min-width="120px" show-overflow-tooltip sortable="custom" />
            <el-table-column v-if="showOwnerNameColumn" prop="ownerName" label="用户名" align="center" min-width="100px" show-overflow-tooltip sortable="custom">
               <template #default="scope">
                {{ scope.row.ownerName || `-` }}
              </template>
            </el-table-column>
            <el-table-column v-if="showOwnerNameColumn" prop="compatibleModels" label="配套机型" align="center" width="120px" show-overflow-tooltip sortable="custom" >
              <template #default="scope">
                {{ scope.row.compatibleModels || `-` }}
              </template>
            </el-table-column>
            <el-table-column v-if="showOwnerNameColumn" prop="productModel" label="产品型号" align="center" width="120px" show-overflow-tooltip sortable="custom" >
              <template #default="scope">
                {{ scope.row.productModel || `-` }}
              </template>
            </el-table-column>
            <el-table-column v-if="showOwnerNameColumn" prop="productName" label="产品名称" align="center" width="120px" show-overflow-tooltip sortable="custom" >
              <template #default="scope">
                {{ scope.row.productName || `-` }}
              </template>
            </el-table-column>
            <el-table-column v-if="showOwnerNameColumn" prop="diagramNumber" label="电路原理图号" align="center" width="140px" show-overflow-tooltip sortable="custom" >
              <template #default="scope">
                {{ scope.row.diagramNumber || `-` }}
              </template>
            </el-table-column>
            <el-table-column v-if="showOwnerNameColumn" prop="diagramVersion" label="版本" align="center" width="100px" show-overflow-tooltip sortable="custom" >
              <template #default="scope">
                {{ scope.row.diagramVersion || `-` }}
              </template>
            </el-table-column>
            <el-table-column prop="total_cases" label="审查点数" align="center" min-width="120px" sortable="custom">
              <template #default="scope">
                {{ scope.row.total_cases || `-` }}
              </template>
            </el-table-column>
            <el-table-column prop="total_failed" label="问题点数" align="center" min-width="120px" sortable="custom">
              <template #default="scope">
                {{ scope.row.reviewTime ? (scope.row.total_failed || 0) : `-` }}
              </template>
            </el-table-column>
            <el-table-column prop="formatted_percentage" label="通过率" align="center" sortable="custom" min-width="100px">
              <template #default="scope">
                {{ scope.row.formatted_percentage ? (scope.row.formatted_percentage * 100).toFixed(1) + "%" : `-` }}
              </template>
            </el-table-column>
            <el-table-column prop="reviewTime" label="审查时间" align="center" width="180px" sortable="custom">
              <template #default="scope">
                {{ scope.row.reviewTime || `-` }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="324" class-name="opt" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" :disabled="!scope.row.reviewTime || scope.row.loading" class="btn" @click="goReviewResults(scope.row)"> 查看记录 </el-button>
                <el-button v-if="!scope.row.reviewTime" v-loading="scope.row.loading" class="btn" type="primary" size="small" @click="reviewFn(scope.row)">
                   审查 
                  </el-button>
                <el-button v-if="scope.row.reviewTime" v-loading="scope.row.loading" class="btn" type="primary" size="small" @click="reviewFn(scope.row,2)">
                   再次审查
                </el-button>
                <el-button v-if="!scope.row.reviewTime" type="info" disabled size="small" class="btn">文件未审查</el-button>
                <el-upload
                  v-if="scope.row.isClosedLoop == 0 && userId === scope.row.ownerId"
                  ref="uploadElementVersion"
                  class="upload-demo12"
                  action="#"
                  :accept="`${fileType}`"
                  :show-file-list="false"
                  :limit="1"
                  :before-upload="file => handleBeforeVersion(file, scope.row)"
                  :on-exceed="handleExceedVersion"
                  :http-request="httpRequestVersion"
                  :on-remove="onRemove"
                  :on-error="handleError"
                >
                  <el-button type="warning" size="small" class="btn">问题未关闭</el-button>
                </el-upload>
                <el-button v-if="scope.row.isClosedLoop == 0 && userId !== scope.row.ownerId" type="warning" size="small" class="btn btnWarning" style="cursor: default; opacity: 0.5">
                  问题未关闭
                </el-button>
                <el-button v-if="scope.row.isClosedLoop == 1" type="success" size="small" class="btn btnNoHover" disabled>问题已关闭</el-button>
                <el-tooltip :disabled="!(scope.row.reviewTime && scope.row.isClosedLoop == 0)" class="box-item" :content="userId == scope.row.ownerId ? `点击 “问题未关闭按钮” 可上传电路图文件新版本` : `只允许文件所属用户上传新版本`" placement="bottom-start" effect="light">
                  <el-icon class="wranTooltip"><InfoFilled /></el-icon>
                </el-tooltip>
                <!-- 折叠操作 -->
                <el-dropdown>
                  <span class="el-dropdown-link">
                    <el-icon class="el-icon--right"><MoreFilled /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item>
                        <el-button type="primary" size="small" class="btn btnXZ" @click="downloadFn(scope.row)">下载文件</el-button>
                      </el-dropdown-item>
                      <el-dropdown-item v-if="loginName === `jzadmin` || loginName === `admin`">
                        <el-button type="danger" size="small" class="btn" :disabled="!scope.row.reviewTime" @click="delRecordsFn(scope.row)">
                          删除记录
                        </el-button>
                      </el-dropdown-item>
                      <el-dropdown-item>
                         <el-button type="danger" size="small" class="btn" @click="crushFn(scope.row)">粉碎文件</el-button>
                      </el-dropdown-item>
                      <el-dropdown-item>
                        <el-button type="primary" size="small" class="btn" @click="viewVersionFn(scope.row)">查看版本</el-button>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </div>
  
        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
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
            <el-form-item prop="isTestFile" label="是否测试文件">
              <el-radio-group v-model="uploadForm.isTestFile" :disabled="isAdminUser || isTestFile" @change="isTestFileChange">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="isHasproductModel" label="是否型号产品">
              <el-radio-group v-model="uploadForm.isHasproductModel" :disabled="uploadForm.isTestFile===1" @change="handleIsHasproductModelChange">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
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
            <el-form-item prop="diagramNumber" label="电路原理图号">
              <el-input v-model="uploadForm.diagramNumber" placeholder="请输入电路原理图号" maxlength="50" @input="(value) => uploadForm.diagramNumber = value.replace(/^\s+/, '')"/>
            </el-form-item>
            <el-form-item prop="diagramVersion" label="版本">
              <el-input v-model="uploadForm.diagramVersion" placeholder="请输入版本" maxlength="50" @input="(value) => uploadForm.diagramVersion = value.replace(/^\s+/, '')"/>
            </el-form-item>
            <el-form-item prop="file" label="上传文件">
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
                <p style="margin-top: 10px;">支持 {{ fileType }} 类型文件</p>
              </div>
              </el-upload>
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
              <el-button type="primary" :disabled="!isFinish" @click="goReviewResults(currentReviewFile)"> 查看审查详情 </el-button>
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
  
      <!-- 规则列表 -->
      <el-dialog v-model="gzlbShow" class="dialogCont dialogCont1 dialogCont2" title="" :show-close="false" width="878px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">规则列表</div>
          </div>
        </template>
        <div class="contBox">
          <div class="tableCont">
            <el-table ref="rulesListRef" border tooltip-effect="light" :data="rulesListDataLocal" height="100%">
              <el-table-column type="index" label="序号" align="center" width="100" />
              <el-table-column prop="label" label="规则名称" align="left" min-width="400px" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
        <template #footer>
          <div class="my-footer">
            <div class="right" />
            <div class="btn">
              <el-button @click="gzlbShow = false"> 关闭</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
  
      <!-- 文件重名上传提示 -->
      <el-dialog v-model="uploadRename" title="提示" width="500" top="36vh">
        <span>{{ uploadRenameText }}</span>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancleFn"> 取消 </el-button>
            <el-button type="primary" @click="uploadRenameSubmit">{{ uploadForm.fileId?`上传`:`确定` }} </el-button>
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
            <el-table ref="rulesListRef" border tooltip-effect="light" :data="viewVersionListData" height="100%" show-overflow-tooltip>
              <el-table-column prop="version" label="版本" align="center" show-overflow-tooltip />
              <el-table-column prop="total_cases" label="审查点数" align="center" width="110px">
                <template #default="scope">
                  {{ scope.row.total_cases || `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="total_failed" label="问题点数" align="center" width="130px">
                <template #default="scope">
                  {{ scope.row.reviewTime ? (scope.row.total_failed || 0) : `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="isClosedLoop" label="状态" align="center" class-name="statusBox" width="120px">
                <template #default="scope">
                  <span :class="scope.row.isClosedLoop === 1 ? 'green' : 'red'" />
                  {{ !scope.row.reviewTime?`文件未审查`: scope.row.isClosedLoop === 1 ? "问题已关闭" : "问题未关闭" }}
                </template>
              </el-table-column>
              <el-table-column prop="formatted_percentage" label="通过率" align="center" width="100px">
                <template #default="scope">
                  {{ scope.row.formatted_percentage ? (scope.row.formatted_percentage * 100).toFixed(1) + "%" : `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="reviewTime" label="审查时间" align="center" width="260px">
                <template #default="scope">
                  {{ scope.row.reviewTime || `-` }}
                </template>
              </el-table-column>
  
              <el-table-column label="操作" align="center" :width="`140px`" class-name="opt">
                <template #default="scope">
                  <el-button type="primary" :disabled="!scope.row.reviewTime" size="small" class="btn" @click="goReviewResults(scope.row)"> 查看记录 </el-button>
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

      <!-- 已粉碎电路图列表 -->
      <el-dialog v-model="shreddedFilesShow" class="dialogCont dialogCont5" title="" width="1200px" :close-on-click-modal="false" :close-on-press-escape="false">
        <template #header>
          <div class="my-header">
            <div class="left">已粉碎电路图列表</div>
          </div>
        </template>
        <div class="contBox">
          <div class="search">
            <el-form ref="ruleFormRef" :inline="true">
              <el-form-item v-if="roleName && roleName !== `普通用户`" label="按单位">
                <el-select v-model="searchObjShreddedFiles.depId" placeholder="选择单位" style="width: 200px" clearable :disabled="!isUserAdminAndJz" @change="depsearchObjShreddedFilesChange">
                  <el-option v-for="item in departmentsData" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="roleName && roleName !== `普通用户`" label="按用户名">
                <el-select v-model="searchObjShreddedFiles.userId" :disabled="searchObjShreddedFiles.depId === ''" placeholder="选择用户名" style="width: 200px" clearable>
                  <el-option v-for="item in depsearchObjShreddedFilesUsersData" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="网表文件名">
                <el-input v-model="searchObjShreddedFiles.fileName" placeholder="输入网表文件名" style="width: 200px" clearable />
              </el-form-item>
            </el-form>
            <div>
              <el-button type="primary" @click="searchObjShreddedFilesInitData()">搜索</el-button>
              <el-button @click="searchObjShreddedFilesRestFn">重置</el-button>
            </div>
          </div>
          <div class="btnList">
            <el-button type="danger" size="small" class="btn" :disabled="selectedRowsAct.length === 0" @click="batchDelFn()">
                  批量删除
            </el-button>
          </div>
          <div class="tableCont">
            <el-table ref="shreddedFilesListRef" border tooltip-effect="light" :data="shreddedFilesListData" height="100%" @selection-change="handleSelectionChange">
              <el-table-column type="selection" align="center" width="55" />
              <el-table-column label="序号" align="center" width="64">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="fileName" label="网表文件" align="left" show-overflow-tooltip sortable="custom" min-width="300px" />
              <el-table-column prop="departmentName" label="单位名称" align="center" min-width="120px" show-overflow-tooltip sortable="custom">
                <template #default="scope">
                  {{ scope.row.departmentName || `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="ownerName" label="用户名" align="center" min-width="120px" show-overflow-tooltip sortable="custom">
                <template #default="scope">
                  {{ scope.row.ownerName || `-` }}
                </template>
              </el-table-column>
              <el-table-column prop="createDate" label="创建时间" align="center" min-width="160px" sortable="custom">
              <template #default="scope">
                {{ scope.row.createDate || `-` }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="120" class-name="opt" fixed="right">
              <template #default="scope">
                <el-button type="danger" size="small" class="btn" @click="batchDelFn(scope.row)">
                  删除记录
                </el-button>
              </template>
            </el-table-column>
            </el-table>
          </div>
           <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="shreddedFilesPagination.current"
            v-model:page-size="shreddedFilesPagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="shreddedFilesPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="searchObjShreddedFilesInitData"
            @size-change="searchObjShreddedFilesInitData"
          />
        </div>
        </div>
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
              font-size: 15px;
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
        height: calc(100vh - 160px);
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
            color: #8b8b8b;
            height: 28px;
            line-height: 28px;
            padding-left: 8px;
          }
  
          .el-popper {
            &.is-light {
              border: 1px solid #e4e7ed;
              background: #fff;
              color: #606266;
            }
          }
          .upload-demo12 {
            width: 85px;
            display: inline-block;
            margin-left: 12px;
          }
        }
        .el-tooltip__trigger{
          margin-right: 0;
          margin-left: 0px;
          vertical-align: middle;
        }
      
        .el-dropdown{
          margin-left: 5px;
          .el-dropdown-link{
            display: flex;
            align-items: center;
            justify-content: center;
            height: 28px;
            line-height: 28px;
            cursor: pointer;
          }
  
        }
        .el-icon{
          margin-right: 0;
        }
      }
      //:deep(.btnXZ){
      //  width: 76px !important;
      //}
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
            text-align: center;
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
        margin-top: 15vh;
        .el-dialog__body {
          padding: 0;
          height: 590px;
          .el-form {
            padding-top: 20px;
            .upload-demo {
              width: 100%;
            }
            .el-form-item__label {
              font-size: 15px;
              padding-right: 10px;
              position: relative;
              padding-left: 8px;
              &::before {
                position: absolute;
                left: 0;
                top: 4px;
              }
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
      &.dialogCont5{
        margin-top: 15vh;
        .el-dialog__body {
          padding: 0;
          max-height: 800px;
          height: 600px;
         }
        .contBox{
          height: 100%;
          display: flex;
          flex-direction: column;
          .search {
            padding: 10px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #fff;
            .el-form {
              .el-form-item {
                margin-bottom: 0;
                margin-right: 20px;
                .el-form-item__label {
                  font-size: 15px;
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
          .btnList{
            padding: 7px 0px 14px;
            }
          .tableCont{
            flex: 1;
          }
          .pagination-container {
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: flex-end;
            background-color: transparent;
            border-top: 1px solid #ebeef5;
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