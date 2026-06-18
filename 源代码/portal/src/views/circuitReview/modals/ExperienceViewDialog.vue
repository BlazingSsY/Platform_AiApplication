<template>
  <el-dialog
    class="experience-view-dialog"
    title="查看经验"
    :show-close="false"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    :model-value="modelValue"
    width="960px"
    top="6vh"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="view-container">
      <!-- 标题区域 -->
      <div class="title-section">
        <div class="title-badge">
          <el-icon><Collection /></el-icon>
        </div>
        <div class="title-main">
          <h2 class="exp-title">{{ data.title }}</h2>
          <div class="title-meta">
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              最后更新：{{ data.updateDate || '-' }}
            </span>
            <span class="meta-item like-item">
              <el-icon><StarFilled /></el-icon>
              {{ data.likeCount || 0 }} 次点赞
            </span>
          </div>
        </div>
      </div>

      <!-- 左右布局 -->
      <div class="main-body">
        <!-- 左侧：内容 -->
        <div class="left-panel">
          <div class="content-card">
            <div class="card-header">
              <el-icon><EditPen /></el-icon>
              <span>经验内容</span>
            </div>
            <div class="card-body">
              {{ data.content || '暂无内容' }}
            </div>
          </div>
        </div>

        <!-- 右侧：信息和附件 -->
        <div class="right-panel">
          <!-- 信息卡片组 -->
          <div class="info-cards">
            <div class="info-card">
              <div class="info-icon-wrapper user-bg">
                <el-icon><User /></el-icon>
              </div>
              <div class="info-content">
                <span class="info-label">贡献人</span>
                <span class="info-value">{{ data.contributor || '-' }}</span>
              </div>
            </div>

            <div class="info-card">
              <div class="info-icon-wrapper org-bg">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="info-content">
                <span class="info-label">单位</span>
                <span class="info-value">{{ data.organization || '-' }}</span>
              </div>
            </div>

            <div class="info-card">
              <div class="info-icon-wrapper contact-bg">
                <el-icon><Phone /></el-icon>
              </div>
              <div class="info-content">
                <span class="info-label">联系方式</span>
                <span class="info-value">{{ data.contact || '-' }}</span>
              </div>
            </div>
          </div>

          <!-- 附件区域 -->
          <div class="attachment-section">
            <div class="section-title">
              <el-icon><Paperclip /></el-icon>
              <span>附件</span>
              <span v-if="data.appendFileList && data.appendFileList.length" class="file-count">
                {{ data.appendFileList.length }}个
              </span>
            </div>
            <div v-if="data.appendFileList && data.appendFileList.length" class="attachment-list">
              <div
                v-for="item in data.appendFileList"
                :key="item.fileId"
                class="attachment-item"
                @click="downloadFile(item)"
              >
                <span class="file-icon-box">
                  <el-icon><Document /></el-icon>
                </span>
                <span class="file-name" :title="item.fileName">{{ item.fileName }}</span>
                <el-icon class="download-arrow"><Download /></el-icon>
              </div>
            </div>
            <div v-else class="no-attachment">
              <el-icon><FolderOpened /></el-icon>
              <span>暂无附件</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button type="primary" size="default" @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
interface ExperienceAttachment {
  id?: number
  fileId: string
  fileName: string
  fid: number
}

interface ExperienceItem {
  id: number
  version: number
  title: string
  content: string
  contributor: string
  organization: string
  contact: string
  appendFileList: ExperienceAttachment[]
  updateDate?: string
  likeCount?: number
  currentUserLiked?: boolean
}

const props = defineProps<{
  modelValue: boolean
  data: ExperienceItem
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const handleClose = () => {
  emit('update:modelValue', false)
}

const downloadFile = (item: ExperienceAttachment) => {
  window.open(`/circuitreview/common/v1/storage/download/${item.fileId}?fileId=${item.fileId}&fileName=${encodeURIComponent(item.fileName)}`)
}
</script>

<style lang="scss">
.experience-view-dialog {
  .el-dialog__header {
    display: none;
  }

  .el-dialog__footer {
    height: 64px;
    background: #ffffff;
    box-shadow: 0 -2px 20px 0 rgba(42, 50, 57, 0.12);
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin: 0;
    border-radius: 0 0 4px 4px;
  }

  .el-dialog__body {
    padding: 0;
    background: #f7f9fc;
  }
}
</style>

<style lang="scss" scoped>
.view-container {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 68vh;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background: #c8d6e5;
    border-radius: 3px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

/* ====== 标题区域 ====== */
.title-section {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  border-left: 4px solid #409EFF;
  flex-shrink: 0;
}

.title-badge {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #e8f4ff, #cce5ff);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .el-icon {
    font-size: 24px;
    color: #409EFF;
  }
}

.title-main {
  flex: 1;
  min-width: 0;
}

.exp-title {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1a3a5c;
  line-height: 1.4;
  word-break: break-all;
}

.title-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #8899aa;

  .el-icon {
    font-size: 15px;
  }
}

.like-item {
  color: #e6a23c;
  font-weight: 500;

  .el-icon {
    color: #e6a23c;
    font-size: 16px;
  }
}

/* ====== 左右布局主体 ====== */
.main-body {
  display: flex;
  gap: 20px;
  min-height: 0;
}

.left-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.right-panel {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ====== 内容卡片 ====== */
.content-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e4e7ed;
  flex-shrink: 0;

  .el-icon {
    font-size: 18px;
    color: #409EFF;
  }
}

.card-body {
  font-size: 15px;
  line-height: 1.8;
  color: #4a5568;
  white-space: pre-wrap;
  word-break: break-word;
  flex: 1;
}

/* ====== 信息卡片组 ====== */
.info-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-card {
  background: #ffffff;
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  gap: 12px;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 3px 16px rgba(0, 0, 0, 0.08);
  }
}

.info-icon-wrapper {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .el-icon {
    font-size: 18px;
    color: #ffffff;
  }
}

.user-bg {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
}
.org-bg {
  background: linear-gradient(135deg, #67c23a, #85d04a);
}
.contact-bg {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.info-label {
  font-size: 12px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ====== 附件区域 ====== */
.attachment-section {
  background: #ffffff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 10px;
  border-bottom: 1px dashed #e4e7ed;

  .el-icon {
    font-size: 16px;
    color: #409EFF;
  }
}

.file-count {
  margin-left: auto;
  font-size: 12px;
  font-weight: 400;
  color: #909399;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 6px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;

  &:hover {
    background: #ecf5ff;
    border-color: #b3d8ff;

    .download-arrow {
      opacity: 1;
      color: #409EFF;
    }

    .file-icon-box {
      background: #d9ecff;

      .el-icon {
        color: #409EFF;
      }
    }
  }
}

.file-icon-box {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: #ebeef5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;

  .el-icon {
    font-size: 17px;
    color: #909399;
  }
}

.file-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.download-arrow {
  font-size: 16px;
  color: #c0c4cc;
  opacity: 0;
  transition: all 0.2s;
  flex-shrink: 0;
}

.no-attachment {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 0;
  color: #c0c4cc;
  font-size: 13px;

  .el-icon {
    font-size: 18px;
  }
}
</style>
