<script setup >
import { informationTypeOpt ,informationTypeOptlabel} from '@/hooks/informationTypeOpt'
import localTitle from "@/views/circuitReview/components/localTitle.vue"
import { informationsDetail,informationAttachments} from '@/ajax/informations';
const route = useRoute()
const router = useRouter()
const informationsDetailData = ref({
  title:``,
  content:``
})
const appendFileList = ref([])

const getFileList = ()=>{
  informationAttachments({
      infoId : informationsDetailData.value.id
    }).then(res=>{
      if(res.succ){
        if(res.content && res.content.length){
          appendFileList.value = res.content.map(item=>({
            name: item.fileName,
            url: `/portal/common/v1/storage/download/${item.fileId}?fileName=${item.fileName}`,
          }))
        }
      }
    })
}
const informationsDetailFn = ()=>{
  informationsDetail({ id: route.query.id }).then(res=>{
		if(res.succ){
			informationsDetailData.value = res.content
      getFileList()
		}
	 })
}
informationsDetailFn()

const goBack = ()=>{
  router.go(-1)
}
</script>

<template>
  <div class="applicationDetail">
    <div class="appDetail">
        <localTitle :title="informationTypeOptlabel(informationsDetailData.type)+`详情`" >
            <template #rightBtn>
                <el-button class="cancel-btn" @click="goBack">返回</el-button>
            </template>
        </localTitle>
        <div class="detailHeader">
          <div class="imgBox">
            <div class="box">
              <img v-if="informationsDetailData.image" :src="`/portal/common/v1/storage/download/${informationsDetailData.image}?fileId=${informationsDetailData.image}`" alt="" >
              <img v-else src="@/assets/images/fengmian.png" alt="" >
              <!-- <el-icon v-else ><Picture /></el-icon> -->
              <!-- <div v-if="!informationsDetailData.image">暂无封面</div> -->
            </div>
          </div>
          <div class="info">
            <div class="title">{{ informationsDetailData.title }}</div>
            <div class="summary">{{ informationsDetailData.summary }}</div>
            <div class="appendFileList">
              <div class="label">附件</div>
              <div class="list">
                <div v-for="item in appendFileList" :key="item.id">
                  <el-link :href="item.url" target="_blank">{{ item.name }}</el-link>
                </div>
              </div>
            </div>
            <div class="date">{{ informationsDetailData.createDate }}</div>
          </div>  
        </div>
        <div class="editor-content-view" v-html="informationsDetailData.content" />
    </div>
  </div>
</template>

<style lang='scss' scoped>
.cancel-btn {
  background-color: #ecf5ff;
}
.applicationDetail{
  .detailHeader{
    display: flex;
    align-items: center;  
    background-color: #fefefe;
    border-bottom: 1px solid #e5e5e5;
    border-radius: 20px 0 0 0 ;
    position: relative;
    .imgBox{
      width: 30%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      >.box{
        height: 200px;
        width: 100%;
        padding: 10px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        border: 1px solid #e5e5e5;
        background-color: #f5f5f5;
        border-radius: 20px 0 0 0 ;
        border-bottom: 0;
        >img{
          width: auto;
          height: 100%;
          border-radius: 10px;
        }
        .el-icon{
          font-size: 140px;
          color: #ccc;
          font-weight: 500;
        }
        >div{
          text-align: center;
          line-height: 24px;
          color: #ccc;
        }
      }
    }
    .info{
      flex: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      padding: 20px 30px;
      position: absolute;
      left: 30%;
      top: 0;
      right: 0;
      bottom: 0;
      .title{
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 10px;
        width: 100%;
      }
      .summary{
        width: 100%;
        font-size: 16px;
        margin-bottom: 10px;
        height: 72px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        // 确保有固定高度以容纳两行文本
        max-height: calc(16px * 1.5 * 2); // 字体大小 * 行高 * 行数
        line-height: 1.5;
      }
      .appendFileList{
        flex: 1;
        display: flex;
        .label{
          font-size: 14px;
          margin-bottom: 10px;
          flex-shrink: 0;
          padding-top: 2px;
        }
        .list{
          width: 100%;
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          justify-content: flex-start;
          padding-left: 20px;
          >div{
            line-height: 22px;
          }
        } 
      }
      .date{
        width: 100%;
        text-align: right;
        color: #ccc;
      }

    }
  }
    .editor-content-view{
        padding: 20px 100px;
        background-color: #fff;
        border-radius: 4px;
        margin-bottom: 20px;
        line-height: 2em !important;
        ul, ol, li{
            list-style-type: normal;
        }
        a{
            color: #0078D7;
            text-decoration: underline;
        }

    }
    .editor-content-view p,
    .editor-content-view li {
    white-space: pre-wrap; /* 保留空格 */
    }

    .editor-content-view blockquote {
    border-left: 8px solid #d0e5f2;
    padding: 10px 10px;
    margin: 10px 0;
    background-color: #f1f1f1;
    }

    .editor-content-view code {
    font-family: monospace;
    background-color: #eee;
    padding: 3px;
    border-radius: 3px;
    }
    .editor-content-view pre>code {
    display: block;
    padding: 10px;
    }

    .editor-content-view table {
    border-collapse: collapse;
    }
    .editor-content-view td,
    .editor-content-view th {
    border: 1px solid #ccc;
    min-width: 50px;
    height: 20px;
    }
    .editor-content-view th {
    background-color: #f1f1f1;
    }

    .editor-content-view ul,
    .editor-content-view ol {
    padding-left: 20px;
    }

    .editor-content-view input[type="checkbox"] {
    margin-right: 5px;
    }
}

</style>
