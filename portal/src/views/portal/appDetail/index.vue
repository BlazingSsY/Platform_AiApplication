<script setup >
import { applicationsDetail } from '@/ajax/app.js'
import menuImg from "./img/menu.png"
import hiImg from "./img/hiImg.png"
import localTitle from "@/views/circuitReview/components/localTitle.vue"
const route = useRoute()
const router = useRouter()
const id = computed(()=>route.query.appId)
const detailData = ref({})
const applicationsDetailFn = ()=>{
    applicationsDetail({id:id.value}).then(res=>{
        if(res.succ){
            detailData.value = res.content
        }
    })
}

watch(()=>route.query.appId,val=>{
    applicationsDetailFn()

},{immediate:true,deep:true})

const goBack = ()=>{

    router.go(-1)

}

</script>

<template>
  <div class="appDetail">
        <localTitle :title="detailData.name" >
            <template #rightBtn>
                <el-button class="cancel-btn" @click="goBack">返回</el-button>
            </template>
        </localTitle>
        <div class="editor-content-view" v-html="detailData.description" />
    </div>
</template>

<style lang='scss'>
.cancel-btn {
  background-color: #ecf5ff;
}
.appDetail{
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
