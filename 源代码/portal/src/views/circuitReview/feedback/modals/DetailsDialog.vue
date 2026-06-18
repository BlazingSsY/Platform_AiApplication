<template>
  <el-dialog
    class="db-modal-detail"
    title="查看意见反馈"
    :show-close="false"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    :model-value="showModalDetails"
    width="1200px"
    top="10vh"
    @update:model-value="$emit('update:show-modal-details', $event)"
    @closed="handleCancel"
  >
    <div class="feedback-container">
      <div class="leftCont">
        <div class="header">
          <div class="custom-header">
            <div class="title">
              {{ localData.title }}
            </div>        
            <div class="header-buttons">
              <!-- <div style="font-weight: bold; font-size: 18px; color: rgb(27 135 250); margin-bottom: 5px;">状态：</div> -->
              <el-dropdown
                trigger="click"
                popper-class="no-arrow-dropdown"
                :popper-options="{
                  modifiers: [
                    {
                      name: 'offset',
                      options: { offset: [0, 8] }
                    },
                    {
                      name: 'arrow',
                      options: { display: 'none' }
                    }
                  ]
                }"
                @command="(value) => changeStatus(value)"
                :disabled="loginName !== 'admin' && loginName !== 'jzadmin' && loginUserId !== localData.createUser"
              >
                <div class="el-dropdown-link">
                  <el-button
                    :type="STATUS_BUTTON_TYPE_MAP[localData.status]"
                    size="small"
                    :disabled="loginName !== 'admin' && loginName !== 'jzadmin' && loginUserId !== localData.createUser"
                    class="status-btn"
                  >
                    <template #default>
                      <div
                        class="dropdown-button"
                      >
                      {{ STATUS_LABEL_MAP[localData.status] }}
                      <el-icon class="el-icon--right"><arrow-down /></el-icon>
                      </div>
                    </template>
                  </el-button>
                </div>            
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="option in STATUS_OPTION_MAP[localData.status]"
                      :key="option.value"
                      :command="option.value"
                    >
                      <span color="red">
                        {{ STATUS_LABEL_MAP[option.value] }}
                      </span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>      
          <div class="info">
           <div>
             {{ localData.createUserName }}
            &nbsp;&nbsp;
            {{ createTime }}
           </div>
            <div v-if="localData.resultId" class="btn">
              <el-link type="warning" size="small" @click="goDetail">查看审查结果</el-link>
            </div>
          </div>
        </div>
        <div class="content">
         <span class="labelTitle">反馈内容：</span> {{ localData.suggestion }}        
        </div>
        <div class="content">
          <div style="display: flex; flex-wrap: wrap; gap: 16px; justify-content: flex-end; align-items: flex-end;">
            <template v-for="(file) in localData.appendFileList" :key="file.fileId">
              <div>
                <template v-if="isImage(file.fileName)">
                  <el-image
                    :src="getFileDownloadUrl(file)"
                    :preview-src-list="imagePreviewList(localData.appendFileList)"
                    :initial-index="getImageIndex(localData.appendFileList, file)"
                    fit="contain"
                    style="width: 80px; height: 80px; border: 1px solid #eee; border-radius: 4px; background: #fafafa;"
                  />
                </template>
                <template v-else>
                  <div class="fileBox">
                      <el-icon><Document /></el-icon>
                      <el-tooltip :content="file.fileName" effect="light" popper-class="refAnswertooltip" class="refAnswertooltip">
                        <el-link type="primary" style="max-height: 80px; display: flex; align-items: flex-end;" @click="downloadFn(file)">{{ file.fileName }}</el-link>
                      </el-tooltip>
                    </div>
                  <!-- <el-link class="fileName" type="primary" style="max-height: 80px; display: flex; align-items: flex-end;" @click="downloadFn(file)">{{ file.fileName }}</el-link> -->
                </template>
              </div>
            </template>
          </div>       
        </div>
        <div 
          id="replies-container"
          class="replies-container"
        >
          <div
            v-for="replyItem in repliesData"
            :id="replyItem.id"
            :key="replyItem.id"
            class="reply-item"
          >
            <div class="answer-info">
              <span class="userName">
                <span class="userIcon"><el-icon><UserFilled /></el-icon></span>
                {{ replyItem.createUserName }}:
              </span>
              <span class="dataBox">
                {{ replyItem.updateDate }}
                <el-dropdown @command="($event)=>handleCommand(replyItem, $event)">
                  <span class="el-dropdown-link">
                    <el-icon><MoreFilled /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="引用回复">引用回复</el-dropdown-item>
                      <el-dropdown-item command="删除回复" :disabled="loginName !== 'admin' && loginName !== 'jzadmin' && loginUserId !== localData.createUser">删除回复</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </span>
            </div>
            <div v-if="replyItem.refAnswer&&replyItem.refAnswer.answer" class="refAnswer">
              <el-tooltip :content="replyItem.refAnswer.answer" placement="bottom" effect="light" class="refAnswertooltip" popper-class="refAnswertooltip">
                <div class="content"> 
                    <div class="text"> 
                        {{ replyItem.refAnswer.answer }}
                    </div>
                    <div class="btn" @click="joinReply(replyItem.refAnswer)">
                      定位到此留言
                    </div>
                  </div>
              </el-tooltip>
            </div>
            <div class="answer-content">
             {{ replyItem.answer }}
            </div>
            <div style="display: flex; flex-wrap: wrap; gap: 16px; justify-content: flex-end; align-items: flex-end;">
              <template v-for="file in replyItem.appendFileList" :key="file.fileId">
                <div>
                  <template v-if="isImage(file.fileName)">
                    <el-image
                      :src="getFileDownloadUrl(file)"
                      :preview-src-list="imagePreviewList(replyItem.appendFileList)"
                      :initial-index="getImageIndex(replyItem.appendFileList, file)"
                      fit="contain"
                      style="width: 80px; height: 80px; border: 1px solid #eee; border-radius: 4px; background: #fafafa;"
                    />
                  </template>
                  <template v-else>
                    <div class="fileBox">
                      <el-icon><Document /></el-icon>
                      <el-tooltip class="refAnswertooltip" popper-class="refAnswertooltip" :content="file.fileName" effect="light">
                        <el-link type="primary" style="max-height: 80px; display: flex; align-items: flex-end;" @click="downloadFn(file)">{{ file.fileName }}</el-link>
                      </el-tooltip>
                    </div>
                  </template>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
      <div class="rightCont">
        <el-timeline style="width: 100%">
          <el-timeline-item
            v-for="(activity, index) in activities"
            :key="index"
            :icon="activity.icon"
            :type="activity.type"
            :color="activity.color"
            :size="activity.size"
            :hollow="activity.hollow"
            :timestamp="activity.timestamp"
            placement="top"
          >
          <span class="user"> 
            <span class="userIcon"><el-icon><UserFilled /></el-icon></span>
            {{ activity.content }}
          </span>
          <span>{{ STATUS_LABEL_MAP[activity.status] }}了意见反馈</span>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
    <template #footer>
      <el-button
        v-if="localData.status !== STATUS.CLOSED"
        type="primary"
        size="small"
        @click="openModalAddReply"
      >
        回复
      </el-button>
      <el-button @click="handleCancel">关闭</el-button>
    </template>
    <AddReplyDialog
      v-if="showModalAddReply"
      v-model:show-modal-add-reply="showModalAddReply"
      :current-row-id="currentRow.id"
      :ref-id="refId"
      @refresh-data="getRepliesData1"
    />
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import AddReplyDialog from './AddReplyDialog.vue'
import { STATUS_LABEL_MAP, STATUS_OPTION_MAP, STATUS_BUTTON_TYPE_MAP, STATUS } from '../constants'
import { changeFeedbackStatus, getReplies, getFileUrl,suggestionStatus,delReply } from '../api'
import { circuitFileVersionResult } from '@/ajax/circuitreview'
import store from "@/store/index"

const userInfo = computed(() => store.state.user.userInfo)
const loginName = computed(() => userInfo.value?.loginName)
const loginUserId = computed(() => userInfo.value?.id)


const props = defineProps({
  showModalDetails: Boolean,
  currentRow: {
    type: Object,
    default: () => {}
  }
})

const emit = defineEmits([
  'update:show-modal-details',
  'submit-data',
  'refresh-data'
])

const activities = reactive([])
const createTime = ref(``)
const suggestionStatusFn = (suggestionId )=>{
  suggestionStatus({suggestionId}).then(res=>{
    if(res.succ){
      console.log('suggestionStatus', res.content)
      createTime.value = res.content && res.content.length && res.content[0].createDate
      activities.splice(0)
      res.content.forEach(r=>{
        activities.push({
          content: r.createUserName,
          status: r.status,
          timestamp: r.createDate,
          color: '#F5BB69',
        })
      })
    }
  })
}

const joinReply = (item)=>{
  const presentEle = document.getElementById(item.id)
  presentEle.scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'})
}


const localData = ref(props.currentRow)
console.log(`localData.value`,localData.value)
const detailsFormRef = ref()
const repliesData = reactive([])

const resetLocalData = (source) => {
  const newLocalData = { ...source };
  localData.value = newLocalData;
}

const refId = ref(``)
const handleCommand =(replyItem, command)=>{
  console.log(`replyItem`,replyItem)
  console.log(`command`,command)
  if(command===`引用回复`){
    closeAllModals()
    refId.value = replyItem.id
    showModalAddReply.value = true

  }else{
    ElMessageBox.confirm(`确定删除?`, `提示`, {
    "confirmButtonText": `确定`,
    "cancelButtonText": `取消`,
    "type": `warning`
  })
    .then(() => {
      delReply([replyItem]).then(res=>{
      if(res.succ){
        emit('refresh-data')
        getRepliesData( props.currentRow?.id)
      }
    })
    })
    .catch(() => {})
  }
}
const getRepliesData = (fId) => {
  if (fId) {
    getReplies({ fId, pageSize: 10000000, pageNumber: 1 })
    .then((res) => {
      if (res.succ)
        repliesData.splice(0)
        res.content.records.forEach((item) => {
          repliesData.push({ ...item })
        })
    })
  }
}
const getRepliesData1 = (fId)=>{
  // emit('refresh-data')
  getRepliesData(fId)
}
const router = useRouter()
const goDetail = ()=>{
  const currentRow = props.currentRow
  circuitFileVersionResult({
    fileId: currentRow.fileId,
  }).then(res=>{
    if(res.succ){
      const item = res.content.map((item) => ({
        "version": item.fileName,
        "fileVersionId": item.fileVersionId,
        "resultId": item.resultId, // 添加resultId字段
        "tel_file": item.fileName,
        "id": item.fileId,
        "total_cases": item.checkPoints || 0,
        "total_passed": item.passCheckPoints || 0,
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
      })).find(r=>r.fileVersionId===currentRow.fileVersionId)
      if(item){
        router.push({
          "path": `/reviewResults`,
          "query": {
            "item": encodeURIComponent(JSON.stringify(item))
          }
        })
      }else{
        ElMessage.error('未查询到文件版本结果')
      }
    }
  })
}

watch(() => props.currentRow, (newVal) => {
  resetLocalData(newVal)
  getRepliesData(props.currentRow?.id)
  if(newVal&&Object.keys(newVal).length>0){
    console.log(`newVal`,newVal)
    suggestionStatusFn(newVal.id)

  }
}, { immediate: true, deep: true })

const handleCancel = () => {
  emit('update:show-modal-details', false)
  resetLocalData(props.currentRow)
  if (detailsFormRef.value) {
    detailsFormRef.value.clearValidate();
  }
}

const isImage = (name) => /\.(png|jpe?g|gif|bmp|svg)$/i.test(name);

const getFileDownloadUrl = (item) => {
  if (!item || !item.fileId) return ''
  return `/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(item.fileName)}`
}

const imagePreviewList = (files = []) => {
  try {
    return (files || [])
      .filter((f) => f && isImage(f.fileName))
      .map((f) => getFileDownloadUrl(f))
  } catch (e) {
    return []
  }
}

const getImageIndex = (files = [], current) => {
  const list = imagePreviewList(files)
  const currentUrl = getFileDownloadUrl(current)
  return Math.max(0, list.findIndex((u) => u === currentUrl))
}

// 下载
const downloadFn = (item) => {
  if(item.fileId){
    window.open(`/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(item.fileName)}`)
  }
}


const changeStatus = (status) => {
  changeFeedbackStatus({
    ...localData.value,
    newStatus: status
  })
  .then(() => {
    ElMessage.success('修改状态成功')
    emit('refresh-data')
  })
  .catch((err) => {
    errMsg.value = err
    // eslint-disable-next-line no-console
    console.error(err)
    ElMessage.error('修改状态失败')
  })
}



// 弹窗管理
const showModalAddReply = ref(false)

const allModalStates = [
  showModalAddReply,
]

const closeAllModals = () => allModalStates.forEach(modalState => modalState.value = false)
// 弹窗-添加
const openModalAddReply = () => {
  closeAllModals()
  refId.value = ``
  showModalAddReply.value = true
}

// watch([
//   () => props.currentRow?.id,
// ], ([nextId]) => {
//   if (nextId) {
//     getRepliesData(props.currentRow?.id)
//   }
// },{ immediate: true })
</script>
<style lang="scss">
 .db-modal-detail{
    padding: 0 !important;
    .el-dialog__header{
      height: 55px;
      background: #E0ECFA;
      padding: 0 20px;
      font-weight: 400;
      font-size: 16px;
      color: #333333;
      line-height: 55px;
      margin: 0;
      border-radius: 2px;
    }
    .el-dialog__footer{
      height: 62px;
      background: #FFFFFF;
      box-shadow: 0px -1px 27px 0px rgba(42,50,57,0.2);
      padding: 0 20px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      margin:0 ;
    }
    .el-dialog__body{
      .feedback-container{
        padding: 0 20px;
        height: 100%;
        display: flex;
        overflow-y: auto;
        .leftCont{
          flex: 1;
          border-right: 1px solid #D9E2EC;
          padding: 10px;
          .header{
            .info {
              font-size: 15px;
              color: #ABAEB1;
              // position: relative;
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding-right: 0;
              padding-top: 4px;
              .btn{
                .el-link {
                  .el-link__inner{
                    font-size: 13px;
                    font-weight: 500;
                  }
                }
                // position: absolute;
                // top: 6px;
                // right: 0;
              }
            }
            .custom-header {
              display: flex;
              flex-direction: row;
              justify-content: space-between;
              margin-bottom: 0;
              .header-buttons {
                margin-left: 20px;
                display: flex;
                gap: 10px;
                align-items: flex-start;
              }
              .title {
                font-weight: bold;
                font-size: 18px;
                color: #0272F5;
              }
              .el-dropdown-link {
                font-size: 15px;
                font-weight: 500;
                cursor: pointer;

                .dropdown-button {
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
                }
              }
            }
          }
        }
        .rightCont{
          width: 300px;
          padding: 20px 10px 20px 20px ;
          .el-timeline{
            .el-timeline-item{
              .el-timeline-item__content{
                font-size: 15px;
                display: flex;
                >span{
                  display: flex;
                }
              }
              .user{
                color:#3899F3 ;
                margin-right: 4px;
                font-weight: 600;
                display: flex;
                align-items: center;
              }
              .userIcon{
                display: flex;
                padding: 2px;
                border-radius: 50%;
                background-color: #fff;
                margin-right: 4px;
                border: 1px solid #CACACA;
                .el-icon{
                  color:#0075E2;
                  font-size: 12px;
                }
              }
            }
            

          }
        }
      }

    }
  }
  .refAnswertooltip{
    >span{
      &:first-child{
        display: inline-block;
        max-width: 700px;
      }
    }

  }
</style>

<style lang="scss" scoped>
  :deep(.el-form-item__label) {
    display: inline-block;
    width: 100px;
    padding: 0px 10px;
    margin-right: 10px;
    font-size: 15px;
    text-wrap: wrap;
    font-family: PingFangSC-regular;

    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .feedback-container {
    min-height: 40vh;
    max-height:680px;

    .content {
      padding: 10px 0 10px 0;
      font-size: 15px;
      color: #666;
      margin-bottom: 10px;
      line-height: 24px;
      .labelTitle{
        font-weight: bold;
        font-size: 15px;
        color: #333333;
      }
     :deep(.fileName){
        width: 100%;
        .el-link__inner{
          display: inline-block;
          width: 80px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .controls {
      padding: 10px;
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      gap: 10px;
    }
  }
  
  .replies-container {
    max-height: 30vh;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    padding-bottom: 20px;

    .reply-item {
      color: #333;
      margin: 10px;
      padding: 5px 10px;
      background: #F5F7F9;
      border-radius: 6px;
    }

    .answer-info {
      font-size: 12px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      padding: 4px 0;
      color: #647A9B;
      .userName{
        display: flex;
      }
      .userIcon{
        display: flex;
        padding: 4px;
        border-radius: 50%;
        background-color: #fff;
        margin-right: 4px;
        .el-icon{
          color:#0075E2;
        }
      }
      .dataBox{
        :deep(.el-dropdown){
          margin-left: 4px;
          .el-tooltip__trigger{
            .el-icon{
              transform: rotate(90deg);
              cursor: pointer;
            }

          }
        }

      }
    }

    .answer-content {
      margin-top: 10px;
      padding-left: 1rem;
      padding-bottom: 10px;
      font-size: 15px;
    }
    .refAnswer{
      padding-left: 1rem;
      font-size: 15px;
      .content{
        background: #E9EEF4;
        border-radius: 6px;
        color: #86909C;
        width: 100%;
        padding: 4px 10px;
        height: 36px;
        line-height: 36px;
        position: relative;
        padding-right: 104px;
        >.text{
          position: absolute;
          left: 10px;
          right: 104px;
          top: 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;  
        }
        .btn{
          position: absolute;
          right: 0;
          top: 4px;
          width: 104px;
          height: 28px;
          background: #C2EAE4;
          border-radius: 6px;
          text-align: center;
          line-height: 28px;
          font-size: 15px;
          color: #00BBA7;
          cursor: pointer;
        }
      }
    }

   
  }
  .fileBox{
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: space-between;
      height: 80px;
      width: 80px;
      padding: 10px 0 2px;
      border: 1px solid rgb(238, 238, 238);
      background: rgb(250, 250, 250);
      .el-icon{
        color: #dcdee0;
        font-size: 46px;
      }
      :deep(.el-link){
        padding: 0 2px;
        font-size: 12px;
        .el-link__inner{
          display: inline-block;
          width:76px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

  .reply-button {
    user-select: none;
    cursor: pointer;
    font-size: 15px;
    color: #1b87fa;
  }

  .status-btn:disabled {
    background: #f5f5f5 !important;
    color: #bbb !important;
    border-color: #eee !important;
    cursor: not-allowed !important;
    box-shadow: none !important;
  }

  .file-link {
    color: #1b87fa;
    text-decoration: underline;
    cursor: pointer;
    transition: color 0.2s;
  }
  .file-link:hover {
    color: #0056b3;
    background: #eaf4ff;
  }
  a.file-link {
    cursor: pointer !important;
  }    
</style>
