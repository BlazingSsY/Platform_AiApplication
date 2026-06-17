<template>
  <el-dialog
    class="db-modal code-display-dialog"
    :title="props.title"
    :close-on-click-modal="true"
    :destroy-on-close="true"
    :model-value="props.modelValue"
    :width="props.width"
    align-center
    draggable
    @update:model-value="$emit('update:modelValue', $event)"
    @closed="handleDialogClosed"
    @open="handleDialogOpen"
  >
    <!-- 代码显示组件 -->
    <CodeDisplay
      :code="props.codeDisplayProps.code"
      :highlighted-lines="props.codeDisplayProps.highlightedLines"
      :show-line-numbers="props.codeDisplayProps.showLineNumbers"
      :language="props.codeDisplayProps.language"
      theme="light"
      :max-height="actualMaxHeight"
      @line-click="handleLineClick"
      @lines-parsed="handleLinesParsed"
    />

    <template #footer>
      <div class="dialog-footer">
        <div><b>审查意见</b></div>
        <div v-if="props.footerText" class="footer-text">
          {{ props.footerText }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import CodeDisplay from './CodeDisplay.vue';
import type { CodeDisplayDialogProps, CodeLine } from './types';

// Props定义
const props = withDefaults(defineProps<CodeDisplayDialogProps>(), {
  modelValue: false,
  title: '代码详情',
  width: '960px',
  closeOnClickModal: false,
  onClose: () => {},
  onOpen: () => {},
  footerText: ` _`,
  codeDisplayProps: () => ({
    code: '',
    highlightedLines: [],
    showLineNumbers: true,
    language: 'c'
  })
});

// Emits定义
const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  'ok': [];
  'cancel': [];
  'copy': [code: string];
  'line-click': [lineNumber: number];
}>();

// Refs
const totalLines = ref(0);
const highlightedLinesCount = ref(0);

// 计算属性（仅保留 maxHeight，删除 theme 相关）

const actualMaxHeight = computed(() => {
  // Return 60vh for the dialog height as requested
  return '60vh';

  /**
     const viewportHeight = window.innerHeight;
  const maxHeight = viewportHeight * 0.8;
  const minHeight = 320;
  const finalHeight = Math.max(maxHeight, minHeight);
  return props.codeDisplayProps.maxHeight || finalHeight + 'px';
   */
});

// 方法
const handleDialogClosed = () => {
  props.onClose();
  emit('cancel');
};

const handleDialogOpen = () => {
  props.onOpen();
};

const handleCancel = () => {
  emit('update:modelValue', false);
  emit('cancel');
};

const handleLineClick = (lineNumber: number) => {
  emit('line-click', lineNumber);
  // ElMessage.info(`点击了第 ${lineNumber} 行`);
};

const handleLinesParsed = (lines: CodeLine[]) => {
  totalLines.value = lines.length;
  highlightedLinesCount.value = lines.filter(line => line.isHighlighted).length;
};

</script>

<style scoped lang="scss">
.code-display-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #e1e4e8;
    margin-right: 0;
  }

  :deep(.el-dialog__title) {
    font-weight: 500;
    font-size: 16px;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 20px;
    border-top: 1px solid #e1e4e8;
  }
}

.dialog-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #e1e4e8;
  background-color: #fafbfc;
  min-height: 46px;

  .toolbar-left,
  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .lines-info {
    font-size: 12px;
    color: #586069;
    margin-left: 12px;
  }

  :deep(.el-button) {
    --el-button-text-color: #586069;
    --el-button-hover-text-color: #24292e;
    --el-button-hover-border-color: #d1d5da;
    --el-button-hover-bg-color: #f6f8fa;
  }
}

.dialog-footer {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  width: 100%;
  padding: 8px 0;
}

.footer-text {
  font-size: 15px;
  line-height: 1.6;
  color: #606266;
  flex: 1;
  text-align: left;
  padding-right: 16px;
  word-break: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
}

// 确保对话框垂直居中
:deep(.el-overlay) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-dialog) {
  margin: 0 !important;
}
</style>
