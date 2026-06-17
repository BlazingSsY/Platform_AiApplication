<script setup >
import { informationsList} from '@/ajax/informations';
const defaultNotices = ref([])
const informationsListFn = ()=>{
	 informationsList().then(res=>{
		if(res.succ){
			defaultNotices.value = res.content || []
		}
	 })
}
informationsListFn()
const router = useRouter()
const goBack = ()=>{
    router.go(-1)
}   
const goDetail = (item)=>{
    router.push({
        path: '/applicationDetail',
        query: {
            id: item.id
        }
    })
}   
</script>

<template>
  <div class="applicationList">
    <div class="title">
        <!-- 技术资讯 -->
         <div>
            <img src="@/assets/images/zxlbImg.png" alt="">
         </div>
        <el-button class="cancel-btn" @click="goBack" size="mini">返回</el-button>
    </div>
    <div class="listBox">
      <div v-for="(item, index) in defaultNotices" :key="index" class="item" @click="goDetail(item)">
          <div class="title">{{ item.title }}</div>
          <div class="content">
            <div class="summary">{{ item.summary }}</div>
            <div class="date">{{ item.createDate }}</div>
          </div>
      </div>
    </div>
  </div>
</template>

<style lang='scss' scoped>
.cancel-btn {
  background-color: #ecf5ff;
}
.applicationList{
    height: 100%;
    min-height: 600px;
    padding-top: 6px;
    >.title{
        font-size: 16px;
        padding: 0 20px 0 0;
        height: 50px;
        background-color: #f5f5f5;
        display: flex;
        align-items: center;
        justify-content: space-between;
        // background: url('@/assets/images/zxlbImg.png') no-repeat left center;
        // background-size: contain;
        img{
            height: 50px;
            width: auto;   
        }
    }
    .listBox{
        height: calc(100% - 50px);
        overflow: auto;
        padding-top: 4px;
        .item{
            display: flex;
            flex-direction: column;
            margin-bottom: 4px;
            border-radius: 8px;
            cursor: pointer;
            background-color: #fafafa;
            padding: 6px;
            box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
            .title{
                font-size: 14px;
                font-weight: bold;
                line-height: 26px;
                padding: 0 10px;
                border-radius: 8px 8px 0 0;
                background-color: #fafafa;
            }
            .content{
                background-color: #fff;
                max-height: 140px;
                font-size: 14px;
                padding: 0 20px;
                padding-bottom: 20px;
                position: relative;
                border-radius: 0 0 8px 8px;
                // padding: 6px 6px 0;
                .summary{
                    line-height: 24px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
                .date{
                    font-size: 12px;
                    color: #999;
                    position: absolute;
                    bottom: 3px;
                    right: 10px;
                }  
            }
            
            &:hover{
                opacity: 0.8;
                // background-color: #f5f5f5;
            }
        }
    }
}

</style>
