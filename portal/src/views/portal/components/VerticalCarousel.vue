<template>
	<div class="vc-container" @mouseenter="pause" @mouseleave="resume">
		<div class="vc-left">
			<div class="vc-tag">
				<span>技术</span>
				<span>资讯</span>
			</div>
		</div>

		<div class="vc-center" @click="goDetail">
			<transition name="flip" mode="out-in">
				<div :key="currentIndex" class="vc-item">
					<div class="vc-title">{{ current.title }}</div>
					<div class="vc-desc">{{ current.summary }}</div>
				</div>
			</transition>
		</div>

		<div v-if="current && current.createDate" class="vc-right">
			<div class="vc-calendar">
				<div class="vc-day">{{ day }}</div>
				<div class="vc-month">{{ monthYear }}</div>
			</div>
		</div>

		<div class="more" @click="goList">更多</div>
	</div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { informationsPortalHome,informationsConfig} from '@/ajax/informations';
import { useRouter } from 'vue-router'
const router = useRouter()
const goList = ()=>{
	console.log(`goList`)
	router.push({ path: `/applicationList` })
}
const goDetail = ()=>{
	console.log(`goDetail`)
	router.push({
		path: '/applicationDetail',
		query: {
			id: current.value.id
		}
	})
}
// props: notices array of { title, content, date }
const props = defineProps({
	notices: { type: Array, default: () => [] },
	interval: { type: Number, default: 10000 }
})

const defaultInterval = ref(props.interval)

const informationsConfigFn = ()=>{
	informationsConfig().then(res=>{
		if(res.succ){
			defaultInterval.value = res.content.refreshInterval * 1000
			informationsListFn()
		}
	 })
}
informationsConfigFn()

// sample fallback data when none provided
const defaultNotices = ref([])
const currentIndex = ref(0)
const items = ref(defaultNotices.value)
const informationsListFn = ()=>{
	informationsPortalHome().then(res=>{
		if(res.succ){
			defaultNotices.value = res.content || []
			items.value = defaultNotices.value
			currentIndex.value = 0
			start()
		}
	 })
}

const timer = ref(null)
const paused = ref(false)

const current = computed(() => items.value[currentIndex.value] || {})

const day = computed(() => {
	if (!current.value.createDate) return ''
	const d = new Date(current.value.createDate)
	return String(d.getDate())
})

const monthYear = computed(() => {
	if (!current.value.createDate) return ''
	const d = new Date(current.value.createDate)
	const m = d.getMonth() + 1
	const y = d.getFullYear()
	return `${String(y)}-${m < 10 ? '0' + m : m}`
})

function next() {
	currentIndex.value = (currentIndex.value + 1) % items.value.length
}

function start() {
	stop()
	timer.value = setInterval(() => {
		if (!paused.value) next()
	}, defaultInterval.value)
}

function stop() {
	if (timer.value) {
		clearInterval(timer.value)
		timer.value = null
	}
}

function pause() {
	paused.value = true
}

function resume() {
	paused.value = false
}

onMounted(() => {
})

onBeforeUnmount(() => {
	stop()
})

watch(() => props.notices, (v) => {
	if (v && v.length) {
		items.value = v
		currentIndex.value = 0
	}
})

function formatDate(dateStr) {
	if (!dateStr) return ''
	const d = new Date(dateStr)
	return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(
		d.getDate()
	).padStart(2, '0')}`
}
</script>

<style scoped>
.vc-container {
	display: flex;
	align-items: center;
	background: linear-gradient(180deg,#ffffff,#f8fbff);
	border-radius: 6px;
	box-shadow: 0 1px 0 rgba(0,0,0,0.06);
	gap: 12px;
	overflow: hidden;
	width: 100%;
	height: 100%;
	padding: 14px 10px;
}
.more{
	flex-shrink: 0;
	padding: 0 20px;
	font-size: 16px;
	color: #9aa4b2;
	cursor: pointer;
	&:hover{
		opacity: 0.8;
	}

}
.vc-left {
	flex: 0 0 20px;
	display:flex;
	align-items:center;
}
.vc-tag{
	width: 120px;
	display:inline-block;
	color: white;
	padding: 6px 30px;
	border-radius: 4px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	border-right: 1px solid rgba(0,0,0,0.04);
	>span{
		font-size: 29px;
    	font-style: italic;
		color: #282e35;
		font-family: 'Microsoft YaHei', sans-serif;
		font-weight: 900;
		line-height: 31px;
		&:last-child{
			color: #f2ab39;
		}
	}
}
.vc-center{
	flex:1 1 auto;
	min-width: 0;
	position: relative;
	padding: 6px 10px 6px 30px;
	cursor: pointer;
	&:hover{
		opacity: 0.8;
	}
}
.vc-item{
	width:100%;
}
.vc-title{
	font-weight:600;
	color:#1f2d3d;
	white-space:nowrap;
	overflow:hidden;
	text-overflow:ellipsis;
	font-size:16px;
}
.vc-desc{
	margin-top:6px;
	color:#6b7280;
	font-size:13px;
	white-space:nowrap;
	overflow:hidden;
	text-overflow:ellipsis;
}
.vc-meta{
	margin-top:6px;
	color:#9aa4b2;
	font-size:12px;
}
.vc-right{
	flex-shrink: 0;
	width: 140px;
	padding: 0 20px 0 0;
	border-right: 1px solid rgba(0,0,0,0.04);
}
.vc-calendar{
	height:56px;
	display:flex;
	flex-direction:column;
	justify-content:center;
	align-items:center;
	padding:6px 8px;
	border-radius:4px;
}
.vc-day{
	font-size:28px;
	font-weight:700;
	color:#2b3b4a;
	line-height:1;
}
.vc-month{
	font-size:15px;
	color:#8b98a6;
	margin-top:2px;
}

/* flip transition */
.flip-enter-active, .flip-leave-active {
	transition: transform 0.6s ease, opacity 0.6s ease;
	transform-origin: 50% 0%;
}
.flip-enter-from {
	transform: rotateX(90deg);
	opacity: 0;
}
.flip-enter-to {
	transform: rotateX(0deg);
	opacity: 1;
}
.flip-leave-from {
	transform: rotateX(0deg);
	opacity: 1;
}
.flip-leave-to {
	transform: rotateX(-90deg);
	opacity: 0;
}

</style>

