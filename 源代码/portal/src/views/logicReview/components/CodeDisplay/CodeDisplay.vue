<template>
  <div class="code-display-container">

    <!-- 代码显示区域 -->
    <div
      ref="contentAreaRef"
      class="code-content-area"
      :class="{
        'with-line-numbers': props.showLineNumbers,
        'theme-light': true
      }"
      :style="{ maxHeight: props.maxHeight, overflow: 'auto' }"
    >
      <div class="code-lines-container">
        <div
          v-for="line in parsedLines"
          :key="line.lineNumber"
          class="code-line"
          :class="{ 'highlighted': line.isHighlighted }"
          :data-line-number="line.lineNumber"
          @click="handleLineClick(line.lineNumber)"
        >
          <!-- 行号 -->
          <span
            v-if="props.showLineNumbers"
            class="line-number"
            :class="{ 'highlighted-line-number': line.isHighlighted }"
          >
            {{ line.lineNumber }}
          </span>

          <!-- 代码内容 -->
          <span class="code-line-content">
            {{ line.content }}
          </span>

          <!-- 高亮标记图标（可选的视觉提示） -->
          <span
            v-if="line.isHighlighted"
            class="highlight-indicator"
            title="高亮行"
          >
          </span>
        </div>
      </div>
    </div>

    <!-- 高亮行数统计 -->
    <div
      class="highlighted-lines-summary"
    >
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, ref, nextTick } from 'vue';
import type { CodeDisplayProps, CodeLine } from './types';

// Props定义
const props = withDefaults(defineProps<CodeDisplayProps>(), {
  code: '',
  highlightedLines: () => [],
  showLineNumbers: true,
  language: 'c',
  theme: 'light',
  maxHeight: '400px'
});

// Emits定义
const emit = defineEmits<{
  'line-click': [lineNumber: number];
  'lines-parsed': [lines: CodeLine[]];
}>();

// 将代码解析为行数组
const parsedLines = computed<CodeLine[]>(() => {
  if (!props.code.trim()) {
    return [];
  }

  const lines = props.code.split('\n').map((content, index) => ({
    lineNumber: index + 1,
    content,
    isHighlighted: props.highlightedLines.includes(index + 1)
  }));

  // 触发解析完成事件
  emit('lines-parsed', lines);
  return lines;
});

// container ref 用于滚动定位
const contentAreaRef = ref<HTMLElement | null>(null);

// 当高亮行变化时，自动滚动到第一个高亮行
watch(
  () => props.highlightedLines,
  async (newVal) => {
    if (!newVal || newVal.length === 0) return;
    await nextTick();
    const first = newVal[0];
    const container = contentAreaRef.value;
    if (!container) return;
    const lineEl = container.querySelector(`[data-line-number="${first}"]`) as HTMLElement | null;
    if (lineEl) {
      lineEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  },
  { immediate: true, deep: true }
);

// 高亮行数统计
const highlightedLinesCount = computed(() => {
  return parsedLines.value.filter(line => line.isHighlighted).length;
});

// 语言显示名称映射
const getLanguageDisplayName = (language: string): string => {
  const languageMap: Record<string, string> = {
    'c': 'C',
    'cpp': 'C++',
    'java': 'Java',
    'python': 'Python',
    'javascript': 'JavaScript',
    'typescript': 'TypeScript',
    'html': 'HTML',
    'css': 'CSS',
    'json': 'JSON'
  };

  return languageMap[language] || language.toUpperCase();
};

// 监听代码内容变化，如果需要可以添加其他逻辑
watch(() => props.code, () => {
  // 可以在这里添加代码变化时的处理逻辑
});

// 行点击处理
const handleLineClick = (lineNumber: number) => {
  emit('line-click', lineNumber);
};
</script>

<style scoped lang="scss">
.code-display-container {
  position: relative;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f6f8fa;
  color: #24292e;
  border: 1px solid #e1e4e8;
}

.code-language-badge {
  position: absolute;
  top: 8px;
  right: 12px;
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 3px;
  z-index: 10;
  text-transform: uppercase;
  letter-spacing: 0.5px;

  background-color: rgba(0, 0, 0, 0.08);
  color: #586069;
}

.code-content-area {
  padding: 16px 0;
  font-size: 13px;
  line-height: 1.5;

  &.with-line-numbers {
    padding-left: 16px;
  }
}

.code-lines-container {
  counter-reset: line-counter;
}

.code-line {
  position: relative;
  padding: 1px 16px;
  min-height: 20px;
  display: flex;
  align-items: center;
  counter-increment: line-counter;
  white-space: pre;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;

  &:hover {
    background-color: rgba(0, 0, 0, 0.03);
  }

  &.highlighted {
    position: relative;
    background-color: rgba(255, 235, 59, 0.18);
    border-left: 3px solid #ffc107;
    margin-left: -3px;
    border-radius: 0 3px 3px 0;

    // 高亮渐变动画
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(
        90deg,
        transparent 0%,
        rgba(255, 255, 255, 0.3) 50%,
        transparent 100%
      );
      opacity: 0;
      animation: highlight-pulse 2s ease-in-out infinite;
        pointer-events: none;
    }
  }

  .line-number {
    display: inline-block;
    width: 40px;
    text-align: right;
    padding-right: 16px;
    user-select: none;
    flex-shrink: 0;
    color: #6a737d;

    &.highlighted-line-number {
      font-weight: bold;
      color: #d97706;
    }
  }

  .code-line-content {
    flex: 1;
    min-width: 0;
    overflow-x: auto;
    white-space: pre;
    scrollbar-width: thin;

    &::-webkit-scrollbar {
      height: 6px;
    }

    &::-webkit-scrollbar-track {
      background: rgba(0, 0, 0, 0.05);
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.2);
      border-radius: 3px;

      &:hover {
        background: rgba(0, 0, 0, 0.3);
      }
    }
  }

  .highlight-indicator {
    margin-left: 8px;
    opacity: 0.7;
    flex-shrink: 0;
    color: #ffc107;

    &:hover {
      opacity: 1;
    }
  }
}

.highlighted-lines-summary {
  font-size: 12px;
  text-align: center;
  border-top: 1px solid;
  background-color: rgba(255, 235, 59, 0.1);
  color: #8a6d3b;
  border-top-color: rgba(255, 235, 59, 0.3);
}

@keyframes highlight-pulse {
  0%, 100% {
    opacity: 0;
  }
  50% {
    opacity: 0.5;
  }
}
</style>