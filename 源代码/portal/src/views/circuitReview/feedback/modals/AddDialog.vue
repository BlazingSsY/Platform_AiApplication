<template>
  <el-dialog
    class="db-modal"
    title="意见反馈"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    :model-value="showModalAdd"
    width="900px"
    @update:model-value="$emit('update:show-modal-add', $event)"
    @closed="handleCancel"
  >
    <el-form
      ref="addFormRef"
      :model="localData"
      class="form-container"
    >
    <div class="controls-panel">
      <el-form-item
        :label="CONFIG['title'].label"
        :title="CONFIG['title'].label"
        prop="title"
        :rules="{
          required: true,
          message: `${CONFIG['title'].label}不能为空`,
          trigger: ['blur', 'change'],
        }"
      >
        <el-input v-model="localData['title']" maxlength="50" :placeholder="`请输入${CONFIG['title'].label}`"/>
      </el-form-item>
      <el-form-item
        :label="CONFIG['suggestion'].label"
        :title="CONFIG['suggestion'].label"
        prop="suggestion"
      >
        <el-input
          v-model="localData['suggestion']"
          style="width: 100%; min-width: 300px"
          :autosize="{ minRows: 8 }"
          type="textarea"
          maxlength="500"
           show-word-limit
          :placeholder="`请输入${CONFIG['suggestion'].label}`"
        />
      </el-form-item>
      <el-form-item label="附件上传" prop="fileList">
        <el-upload v-model:file-list="fileList" :drag="true" :on-preview="onPreviewFn" class="upload-demo-file" action="#" :limit="10" :on-remove="removeAppendfile" :before-upload="beforeAvatarUploadFile" :http-request="uploadFile">
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
      <el-button v-loading="fileLoading" :disabled="fileLoading" type="primary" @click="handleSubmit">{{ fileLoading?`文件上传中...`:`提交` }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { addFeedback, storageUpload, getFileUrl } from '../api'
import { FEEDBACK_CONFIGS_MAP as CONFIG } from '../constants'

const props = defineProps({
  showModalAdd: Boolean,
  addFrom: {
    type: Object,
    default: () => {}
  } 
})

const emit = defineEmits([
  'update:show-modal-add',
  'cancleFn',
  'submit-data',
  'refresh-data'
])

const getInitLocalData = () => {
  const _localData = {};
  return _localData
};

const localData = ref(getInitLocalData())
const errMsg = ref()
const addFormRef = ref()

const isImage = (name) => /\.(png|jpe?g|gif|bmp|svg)$/i.test(name);

const handleCancel = () => {
  // emit('update:show-modal-add', false)
  emit('cancleFn', false)
  localData.value = getInitLocalData()
  //清空fileList中的数据
  appendFileList.value = [];
  fileList.value = [];
  if (addFormRef.value) {
    addFormRef.value.clearValidate();
  }
}
const router = useRouter()
const handleSubmit = async () => {
  if (!addFormRef.value) return
  addFormRef.value.validate()
  .then( (valid) => {
    if (valid) {
      // ElMessage.success('正在添加...')
      localData.value.appendFileList = appendFileList.value;
      console.log(localData.value)
      addFeedback(localData.value)
      .then(() => {
        ElMessage.success('添加成功')
        emit('refresh-data',`add`)
        emit('update:show-modal-add', false)
        localData.value = getInitLocalData()
        if(props.addFrom){
          router.replace({query:{}})
        }
      })
      .catch((err) => {
        errMsg.value = err
        // eslint-disable-next-line no-console
        console.error(err)
        ElMessage.error('添加失败')
      })
      .finally(() => {
        errMsg.value = undefined
        if (addFormRef.value) {
          addFormRef.value.clearValidate();
        }
        fileList.value = [];
        appendFileList.value = [];
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
const onPreviewFn = (file)=>{
  console.log(`file`,file)
  const item = fileList.value.filter(r=>r.uid==file.uid)
  console.log(`item`,item)
  if(item.length){
    if(item[0].fileId){
      window.open(`/circuitreview/common/v1/storage/download/${item[0].fileId}?fileId=${item[0].fileId}&fileName=${encodeURIComponent(item[0].fileName)}`)
    }
  }

}
const appendFileList = ref([]); // 新增
const uploadFile = (item) => {
  const formData = new FormData();
  const fileItem = item;
  formData.append(`file`, fileItem.file);  
  fileLoading.value = true
  fileList.value =fileList.value.map((r)=>{
      if(r.uid==fileItem.file.uid){
        return {
          ...r,
          status:`uploading`
        }
      }else{
        return r
      }
    })
  item.status = `uploading`
  storageUpload(formData).then((res) => {
    fileLoading.value = false
    if (res.succ) {
      item.status = `uploading`
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
      fileList.value =fileList.value.filter(r=>r.name!==fileItem.file.uid)
      if (addFormRef.value) {
        addFormRef.value.clearValidate();
      }
    }
  }).catch(()=>{
    fileLoading.value = false
    fileList.value =fileList.value.filter(r=>r.name!==fileItem.file.uid)
  })
};

if(props.addFrom){
  console.log(props.addFrom)
  localData.value.title = props.addFrom.title||``
  localData.value.fileId = props.addFrom.fileId||``
  localData.value.fileVersionId = props.addFrom.version||``
  localData.value.resultId = props.addFrom.record||``
  appendFileList.value = fileList.value = props.addFrom.appendFileList.map(r=>{
    return {
      name: r.fileName,
      fileName: r.fileName,
      fileId: r.fileId,
      // url: getFileUrl(r.fileId),
      status:`success`
    }
  })
}

</script>

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
</style>

