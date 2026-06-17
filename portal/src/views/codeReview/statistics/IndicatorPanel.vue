<template>
  <div class="indicator-panel">
    <div class="card file">
      <div class="label">
        <img src="@/assets/images/circuitReview/wbwj.png" alt="已经审查文件" />
        <span>已审查代码文件</span>
      </div>
      <span class="number">
        {{ fileCount }}
      </span>
    </div>
    <div class="card total-review">
      <div class="label">
        <img src="@/assets/images/circuitReview/zscs.png" alt="总审查文件数" />
        <span>总审查文件数</span>
      </div>
      <span class="number">
        {{ totalReviewPointCount }}
      </span>
    </div>
    <div class="card total-pass">
      <div class="label">
        <img src="@/assets/images/circuitReview/ztg.png" alt="总通过审查文件数" />
        <span>总通过审查文件数</span>
      </div>
      <span class="number">
        {{ totalPassReviewPointCount }}
      </span>
    </div>
    <div class="card pass-rate">
      <div class="label">
        <img src="@/assets/images/circuitReview/tgl.png" alt="总通过率" />
        <span>总通过率</span>
      </div>
      <span class="number">
        {{ `${totalPassRate.toFixed(1)}%` }}
      </span>
    </div>
  </div>
</template>

<script setup>
  defineProps({
    fileCount: {
      type: Number,
      default: 0
    },
    totalReviewPointCount: {
      type: Number,
      default: 0
    },
    totalPassReviewPointCount: {
      type: Number,
      default: 0
    },
    totalPassRate: {
      type: Number,
      default: 0
    }
  })
</script>

<style lang='scss' scoped>
  .indicator-panel {
    display: flex;
    flex-direction: row;
    width: 100%;
    gap: 20px;

    .card {
      width: 100px;
      height: 100px;
      border-radius: 20px;
      padding-left: 30px;
      padding-right: 30px;

      flex-grow: 1;
      flex-shrink: 1;

      display: flex;
      align-items: center;
      justify-content: space-between;

      .label {
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 20px;

        > img {
          width: 48px;
          height: 48px;
        }

        > span {
          font-family: Microsoft YaHei;
          font-weight: 400;
          font-size: 15px;
          color: #3A4C5F;
          line-height: 23px;
        }
      }
       
      .number {
        font-family: Microsoft YaHei;
        font-weight: 400;
        font-size: 30px;
        line-height: 36px;
      }
    }

    .file {
      background: linear-gradient(123deg, #b5e2f0, #c6e5f6);
      box-shadow: 1px 3px 30px 0px rgba(3,13,28,0.03);

      .number {
        color: #4397F1;
      }
    }

    .total-review {
      background: linear-gradient(123deg, #c7d6f5, #cde4fb);
      box-shadow: 1px 3px 30px 0px rgba(3,13,28,0.03);

      .number {
        color: #45BFE2;
      }
    }

    .total-pass {
      background: linear-gradient(123deg, #eeddbc, #eee2c2);
      box-shadow: 1px 3px 30px 0px rgba(3,13,28,0.03);

      .number {
        color: #6299F5;
      }
    }

    .pass-rate {
      background: linear-gradient(123deg, #c1d9f4, #c7e0f9);
      box-shadow: 1px 3px 30px 0px rgba(3,13,28,0.03);

      .number {
        color: #D9A732;
      }
    }
  }
</style>
