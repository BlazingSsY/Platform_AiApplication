<template>
  <div v-loading.fullscreen.lock="loading" element-loading-text="数据加载中..." class="home">
    <div class="section">
      <div class="item-container">
        <div class="label">
          <div class="icon" />
          <div class="text">硬件逻辑智能审查</div>
        </div>
        <div class="review-entry">
          <button @click="goToReview">
            <span>开始审查</span>
          </button>
        </div>
      </div>
      <div class="item-container-table">
        <div class="label">
          <div class="icon" />
          <div class="text">最近审查</div>
        </div>
        <div class="recent-table-container">
          <recent-table :table-data="recentTableData" />
        </div>
      </div>
    </div>
    <div class="chart-section">
      <div
        :class="[
          'item-container',
          isLargeChart ? 'chart-container-lg' : 'chart-container-sm'
        ]"
      >
        <div class="label">
          <div class="icon" />
          <div class="text">硬件逻辑智能审查数量统计</div>
        </div>
        <div class="review-chart-container">
          <review-chart :dataset="reviewChartData" />
        </div>
      </div>
      <div
        :class="[
          'item-container',
          isLargeChart ? 'chart-container-lg' : 'chart-container-sm'
        ]"
      >
        <div class="label">
          <div class="icon" />
          <div class="text">用户数量统计</div>
        </div>
        <div class="user-chart-container">
          <user-chart :dataset="userChartData" />
        </div>
      </div>
      <div
        :class="[
          'item-container',
          isLargeChart ? 'chart-container-lg' : 'chart-container-sm'
        ]"
      >
        <div class="label">
          <div class="icon" />
          <div class="text">问题数量统计</div>
        </div>
        <div class="issue-chart-container">
          <issue-chart :dataset="issueChartData" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import RecentTable from "./recent-table/RecentTable.vue"
// import RankTable from "./rank-table/RankTable.vue"
import ReviewChart from "./review-chart/ReviewChart.vue"
import UserChart from "./user-chart/UserChart.vue"
import IssueChart from "./issue-chart/IssueChart.vue"
import { getHomeData } from "./api"
import store from "@/store/index"
const router = useRouter()
const goToReview = () => {
  store.commit("CLEAR_PAGES_INFO_STROE")
  router.push("/logicDocumentReview")
}

const loading = ref(false)

const recentTableData = reactive([])
const rankTableData = reactive([])
const reviewChartData = reactive([])
const userChartData = reactive([])
const issueChartData = reactive([])

const isLargeChart = ref(false)
const LARGE_CHART_LIMIT = 12

const fetchData = () => {
  loading.value = true
  getHomeData()
    .then(res => {
      if (res.succ) {
        recentTableData.splice(0)
        rankTableData.splice(0)
        reviewChartData.splice(0)
        userChartData.splice(0)
        issueChartData.splice(0)

        const { mostRecentlyReviewedFiles, highestPassRateFiles, reviewedFilesCountByDepartment, totalFilesCountByDepartment, userCountByDepartment, reviewIssueCountByDepartment } = res.content

        mostRecentlyReviewedFiles.forEach(item => {
          recentTableData.push({ ...item })
        })

        highestPassRateFiles.forEach(item => {
          rankTableData.push({ ...item })
        })

        Object.entries(reviewedFilesCountByDepartment).forEach(([label, value]) => {
          reviewChartData.push({
            department: label,
            reviewedCount: value,
            totalCount: totalFilesCountByDepartment[label]
          })
        })

        Object.entries(userCountByDepartment).forEach(([label, value]) => {
          userChartData.push([label, value])
        })

        Object.entries(reviewIssueCountByDepartment).forEach(([label, value]) => {
          issueChartData.push([label, value])
        })

        // 按已审查文件数+未审查文件数之和从高到低排序
        reviewChartData.sort((a, b) => {
          const aTotal = Number(a.totalCount) || 0
          const bTotal = Number(b.totalCount) || 0
          return bTotal - aTotal
        })
        userChartData.sort((a, b) => b[1] - a[1])
        issueChartData.sort((a, b) => b[1] - a[1])

        if (reviewChartData.length >= LARGE_CHART_LIMIT ||
          userChartData >= LARGE_CHART_LIMIT ||
          issueChartData >= LARGE_CHART_LIMIT) {
            isLargeChart.value = true
        }
      }
    })
    .catch(error => {
      ElMessage.error("获取数据失败", error)
    })
    .finally(() => {
      loading.value = false
    })
}
fetchData()
</script>

<style lang="scss" scoped>
.home {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;

  gap: 30px;

  overflow-y: auto;

  .section {
    width: 100%;

    display: flex;
    flex-direction: row;
    gap: 20px;
    justify-content: space-between;
  }

  .chart-section {
    width: 100%;
    display: flex;
    flex-direction: row;
    gap: 20px;
    flex-wrap: wrap;
  }
}

.item-container-table {
  display: flex;
  flex-direction: column;
  gap: 20px;

  flex-grow: 1;

  .label {
    display: flex;
    flex-direction: row;
    gap: 8px;

    align-items: center;

    .icon {
      width: 12px;
      height: 20px;
      background: linear-gradient(90deg, rgba(32, 133, 255, 0.73), rgba(255, 255, 255, 0));
      box-shadow: 0px 0px 10px 0px rgba(38, 99, 241, 0.35);
    }

    .text {
      font-family: YouSheBiaoTiHei;
      font-weight: 400;
      font-size: 20px;
      color: #0e2036;
      line-height: 36px;
    }
  }
}

.item-container {
  display: flex;
  flex-direction: column;
  gap: 20px;

  flex-shrink: 1;

  .label {
    display: flex;
    flex-direction: row;
    gap: 8px;

    align-items: center;

    .icon {
      width: 12px;
      height: 20px;
      background: linear-gradient(90deg, rgba(32, 133, 255, 0.73), rgba(255, 255, 255, 0));
      box-shadow: 0px 0px 10px 0px rgba(38, 99, 241, 0.35);
    }

    .text {
      font-family: YouSheBiaoTiHei;
      font-weight: 400;
      font-size: 20px;
      color: #0e2036;
      line-height: 36px;
    }
  }
}

.chart-container-sm {
  width: 485px;
  flex-grow: 1;
}
.chart-container-lg {
  width: 100%;
}

.review-entry {
  width: 780px;
  height: 245px;
  background: url(@/assets/images/codeReview/entryNew1.png) no-repeat center center;
  box-shadow: 0px 3px 21px 0px rgba(33, 49, 73, 0.1);
  border-radius: 10px;

  display: flex;
  flex-direction: column;
  gap: 36px;
  align-items: center;
  justify-content: center;

  > span {
    height: 32px;
    font-family: Microsoft YaHei;
    font-weight: bold;
    font-size: 28px;
    color: #fffefe;
    line-height: 48px;
    text-shadow: 0px 5px 10px rgba(0, 59, 149, 0.69);
  }

  > button {
    height: 60px;
    width: 180px;
    background: #075bc9;
    padding-bottom: 14px;
    border-radius: 20px;
    padding: 0px 20px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    font-size: 28px;
    color: #ffffff;
    line-height: 58px;
    border: unset;

    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
}

.recent-table-container {
  height: 100%;
  flex-shrink: 1;
}

.recent-rank-container {
  height: 100%;
  flex-grow: 1;
}

.review-chart-container {
  width: 100%;
  height: 300px;

  background: #f5f7fa;
  box-shadow: 0px 8px 38px 0px rgba(3, 13, 28, 0.02);
  border-radius: 20px;
}

.user-chart-container {
  width: 100%;
  height: 300px;

  background: #f5f7fa;
  box-shadow: 0px 8px 38px 0px rgba(3, 13, 28, 0.02);
  border-radius: 20px;
}

.issue-chart-container {
  width: 100%;
  height: 300px;

  background: #f5f7fa;
  box-shadow: 0px 8px 38px 0px rgba(3, 13, 28, 0.02);
  border-radius: 20px;
}
</style>
