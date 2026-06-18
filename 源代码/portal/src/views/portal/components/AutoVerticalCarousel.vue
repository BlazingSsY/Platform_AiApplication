<template>
  <div class="flip-carousel-container">
    <div 
      class="carousel-viewport"
      @mouseenter="stopCarousel"
      @mouseleave="startCarousel"
    >
      <div 
        class="carousel-card"
        :class="{ flipped: isFlipping }"
      >
        <!-- 卡片正面 -->
        <div class="card-face front">
          <h3 class="slide-title">
            <span class="number">{{ currentNumber }}</span>
            {{ currentConsult.title }}
          </h3>
          <p class="slide-excerpt">{{ currentConsult.excerpt }}</p>
        </div>

        <!-- 卡片背面 -->
        <div class="card-face back">
          <h3 class="slide-title">
            <span class="number">{{ nextNumber }}</span>
            {{ nextConsult.title }}
          </h3>
          <p class="slide-excerpt">{{ nextConsult.excerpt }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';

// 1. 定义 Props
const props = defineProps({
  /**
   * 资讯数据列表
   * @type {Array<{id: number, title: string, excerpt: string}>}
   * @required
   */
  consultations: {
    type: Array,
    required: true,
    validator: (value) => {
      return value.length > 0 && value.every(item => 
        item.hasOwnProperty('id') && 
        item.hasOwnProperty('title') && 
        item.hasOwnProperty('excerpt')
      );
    }
  },

  /**
   * 自动轮播间隔时间 (毫秒)
   * @type {number}
   * @default 3000
   */
  interval: {
    type: Number,
    default: 3000,
    validator: (value) => value >= 1000 // 最小间隔为1秒
  }
});

// 2. 定义响应式状态
const currentIndex = ref(0);
const isFlipping = ref(false);
let carouselInterval = null;

// 3. 计算属性
const currentConsult = computed(() => {
  return props.consultations[currentIndex.value];
});

const nextConsult = computed(() => {
  const nextIdx = (currentIndex.value + 1) % props.consultations.length;
  return props.consultations[nextIdx];
});

const currentNumber = computed(() => {
  return currentIndex.value + 1;
});

const nextNumber = computed(() => {
  return ((currentIndex.value + 1) % props.consultations.length) + 1;
});

// 4. 核心方法
const startCarousel = () => {
  if (carouselInterval) clearInterval(carouselInterval);
  if (props.consultations.length <= 1) return;

  carouselInterval = setInterval(() => {
    flipCard();
  }, props.interval);
};

const stopCarousel = () => {
  if (carouselInterval) {
    clearInterval(carouselInterval);
    carouselInterval = null;
  }
};

const flipCard = () => {
  if (isFlipping.value) return;

  isFlipping.value = true;

  // 在翻转动画中间点更新索引
  setTimeout(() => {
    currentIndex.value = (currentIndex.value + 1) % props.consultations.length;
  }, 300); // 对应动画时长的一半

  // 动画结束后重置翻转状态
  setTimeout(() => {
    isFlipping.value = false;
  }, 600); // 对应动画总时长
};

// 5. 生命周期钩子
onMounted(() => {
  startCarousel();
});

onUnmounted(() => {
  stopCarousel();
});
</script>

<style scoped>
/* 容器样式 - 宽度为父容器的80% */
.flip-carousel-container {
  width: 80%;
  margin: 0 auto;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

/* 可视区域 */
.carousel-viewport {
  width: 100%;
  height: 200px;
  position: relative;
  perspective: 1500px; /* 开启3D透视效果 */
  background-color: #fff;
}

/* 翻转卡片 */
.carousel-card {
  width: 100%;
  height: 100%;
  position: relative;
  transform-style: preserve-3d; /* 子元素保持3D效果 */
  transition: transform 0.6s ease-in-out; /* 翻转过渡动画 */
}

/* 卡片正反面通用样式 */
.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden; /* 隐藏元素背面 */
  padding: 25px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 正面样式 */
.front {
  background-color: #fff;
}

/* 背面样式 */
.back {
  background-color: #f0f8fb;
  transform: rotateY(180deg); /* 背面初始旋转180度 */
}

/* 翻转状态 */
.carousel-card.flipped {
  transform: rotateY(180deg); /* 整体旋转180度，显示背面 */
}

/* 标题样式 */
.slide-title {
  margin: 0 0 10px 0;
  color: #2d3748;
  font-size: 1.2rem;
  border-bottom: 2px solid #42b983;
  padding-bottom: 8px;
  display: flex;
  align-items: center;
}

/* 序号样式 */
.slide-title .number {
  display: inline-block;
  background-color: #42b983;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  text-align: center;
  line-height: 24px;
  margin-right: 10px;
  font-size: 0.8rem;
}

/* 简介样式 */
.slide-excerpt {
  color: #4a5568;
  line-height: 1.5;
  font-size: 0.95rem;
  margin: 0;
}
</style>