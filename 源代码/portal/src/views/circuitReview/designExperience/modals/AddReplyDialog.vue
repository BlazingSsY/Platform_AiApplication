<template>
  <el-dialog
    class="db-modal-detail"
    title="回复"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    :model-value="showModalAddReply"
    @update:model-value="$emit('update:show-modal-add-reply', $event)"
    @closed="handleCancel"
  >
    <el-form
      ref="addReplyFormRef"
      :model="localData"
      class="form-container"
    >
    <div class="controls-panel">
      <el-form-item
        prop="reply"
      >
        <el-input
          v-model="localData['reply']"
          style="width: 100%; min-width: 300px"
          :autosize="{ minRows: 4 }"
          type="textarea"
            maxlength="500"
           show-word-limit
            placeholder="请输入回复内容"
        />
      </el-form-item>
      <el-form-item label="附件上传" prop="fileList">
        <el-upload v-model:file-list="fileList" :drag="true" class="upload-demo-file" action="#" :limit="10" :on-remove="removeAppendfile" :before-upload="beforeAvatarUploadFile" :http-request="uploadFile">
          <el-button type="primary">点击上传</el-button>
          <template #tip>
            <div class="el-upload__tip">将附件拖拽至框内，或点击上传，无附件可不上传</div>
          </template>
        </el-upload>
      </el-form-item>      
    </div>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button v-loading="fileLoading" :disabled="fileLoading" type="primary" @click="handleSubmit" >{{ fileLoading?`文件上传中...`:`提交` }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { addReply, storageUpload} from '../api'

const props = defineProps({
  showModalAddReply: Boolean,
  currentRowId: {
    type: Number,
    default: 0
  },
  refId: {
    type: String,
    default: ``
  }
})

const emit = defineEmits([
  'update:show-modal-add-reply',
  'refresh-data'
])

const getInitLocalData = () => ({ reply: '' })

const localData = ref(getInitLocalData())
const addReplyFormRef = ref()

const isImage = (name) => /\.(png|jpe?g|gif|bmp|svg)$/i.test(name);

const handleCancel = () => {
  emit('update:show-modal-add-reply', false)
  localData.value = getInitLocalData()
  //清空fileList中的数据
  appendFileList.value = [];
  fileList.value = [];  
  if (addReplyFormRef.value) {
    addReplyFormRef.value.clearValidate();
  }
}

const handleSubmit = async () => {
  if (!addReplyFormRef.value || !props.currentRowId) return
  addReplyFormRef.value.validate()
  .then((valid) => {
    if (valid) {
      ElMessage.success('正在添加...')
      addReply({
        fid: props.currentRowId,
        reply: localData.value.reply,
        appendFileList: appendFileList.value,
        refId: props.refId??``
      })
      .then((res) => {
        if (res.succ) {
          ElMessage.success('添加成功')
          emit('refresh-data', props.currentRowId)
          emit('update:show-modal-add-reply', false)
          localData.value = getInitLocalData()
        }
      })
      .catch((err) => {
        // eslint-disable-next-line no-console
        console.error(err)
        ElMessage.error('添加失败')
      })
      .finally(() => {
        if (addReplyFormRef.value) {
          addReplyFormRef.value.clearValidate();          
        }
        //清空fileList中的数据
        appendFileList.value = [];
        fileList.value = [];        
      })
    }
  })
}

// 删除附件
const removeAppendfile = (file, fileList) => {
  const index = appendFileList.value.findIndex(item => item.fileName === file.name);
  if (index !== -1) {
    appendFileList.value.splice(index, 1);
  }
}

//附件上传
const fileLoading = ref(false)
const fileList = ref([]);
const beforeAvatarUploadFile = rawFile => {
  const isLt50M = rawFile.size / 1024 / 1024 < 50;
  if (!isLt50M) {
    ElMessage.error(`上传文件大小不能超过 50MB!!`);
  }
  return isLt50M;
};

const appendFileList = ref([]); // 新增
const uploadFile = (item) => {
  const formData = new FormData();
  const fileItem = item;
  formData.append(`file`, fileItem.file);  
  fileLoading.value = true
  storageUpload(formData).then((res) => {
    fileLoading.value = false
    if (res.succ) {
      appendFileList.value.push({
        fileName: item.file.name,
        fileId: res.content, // 假设返回的就是文件ID
      });   
      fileList.value =fileList.value.map((r)=>{
        if(r.uid==fileItem.file.uid){
          return {
            ...r,
            status:`success`,
            fileId: res.content,
            fileName: item.file.name,
          }
        }else{
          return r
        }
      })
    }else{
      //清空fileList中的数据
      console.log( fileList.value)
      fileList.value =fileList.value.filter(r=>r.name!==item.file.name)
      if (addReplyFormRef.value) {
        addReplyFormRef.value.clearValidate();
      }
    }
  }).catch(()=>{
    fileList.value =fileList.value.filter(r=>r.name!==item.file.name)
    fileLoading.value = false
  })
};


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
      padding: 20px;
    }
  }
</style>
<style lang="scss" scoped>
  :deep(.el-form-item__label) {
    display: inline-block;
  
    font-size: 15px;
    text-wrap: wrap;
    font-family: PingFangSC-regular;

    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
</style>
