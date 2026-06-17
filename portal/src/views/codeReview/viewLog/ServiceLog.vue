<template>
  <div class="service-log">
    <div class="control-panel service-panel">
      <el-button type="primary" size="small" @click="getLogData()"> 查询 </el-button>
      <div class="search-wrapper">
        <div class="search-panel">
          <el-input
            v-model="searchText"
            placeholder="搜索..."
            clearable
            @keyup.enter="findNext"
            @input="onSearchInput"
          />
          <button @click="findNext" title="下一个">↓</button>
          <button @click="findPrev" title="上一个">↑</button>
          <span v-if="searchMatches >= 0 && searchText" class="search-count">{{ searchCurrentMatch }}/{{ searchMatches }}</span>
        </div>
        <div class="right-buttons">
          <div class="theme-toggle" @click="toggleTheme">
            <el-icon><Sunny v-if="isDarkMode" /><Moon v-else /></el-icon>
          </div>
          <div class="refresh-btn" @click="refreshLog">
            <el-icon><Refresh /></el-icon>
          </div>
        </div>
      </div>
    </div>
    <div class="tableBox">
      <div class="log-editor-wrapper">
        <div v-if="detailLoading" v-loading="detailLoading" class="spin-center"/>
        <Codemirror
          v-show="!detailLoading"
          v-model="logContent"
          :extensions="extensions"
          read-only="readonly"
          :placeholder="'日志内容...'"
          @ready="handleReady"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Codemirror } from 'vue-codemirror'
import { javascript } from '@codemirror/lang-javascript'
import { EditorView } from '@codemirror/view'
import { SearchQuery, setSearchQuery, highlightSelectionMatches } from '@codemirror/search'
import { search } from '@codemirror/search'
import { ref, computed, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Sunny, Moon } from '@element-plus/icons-vue'
import { getServiceLog } from '@/ajax/sourcecodereview'

const view = shallowRef()
const logContent = ref('')
const detailLoading = ref(false)
const searchText = ref('')
const searchMatches = ref(0)
const searchCurrentMatch = ref(0)
let matches: any[] = []

const isDarkMode = ref(false)

const handleReady = (payload: any) => {
  view.value = payload.view
}

const lightTheme = EditorView.theme(
  {
    '&': {
      height: '100%',
      width: '100%',
    },
    '.cm-content': {
      fontSize: '14px',
      lineHeight: '1.5',
    },
    '.cm-searchMatch': {
      backgroundColor: '#ffdd33',
    },
  },
  { dark: false },
)

const darkTheme = EditorView.theme(
  {
    '&': {
      height: '100%',
      width: '100%',
      backgroundColor: '#1e1e1e',
    },
    '.cm-content': {
      fontSize: '14px',
      lineHeight: '1.5',
      color: '#d4d4d4',
    },
    '.cm-searchMatch': {
      backgroundColor: '#613214',
    },
    '.cm-line': {
      backgroundColor: '#1e1e1e',
    },
  },
  { dark: true },
)

const myTheme = computed(() => isDarkMode.value ? darkTheme : lightTheme)

const extensions = computed(() => {
  return [
    javascript(),
    myTheme.value,
    highlightSelectionMatches(),
    search({
      top: false,
    }),
  ]
})

const onSearchInput = () => {
  performSearch()
}

const performSearch = () => {
  if (!view.value || !searchText.value) {
    searchMatches.value = 0
    searchCurrentMatch.value = 0
    if (view.value) {
      view.value.dispatch({
        effects: setSearchQuery.of(new SearchQuery({ search: '' }))
      })
    }
    return
  }

  const query = new SearchQuery({
    search: searchText.value,
    caseSensitive: false,
  })

  const content = logContent.value
  matches = []
  let index = 0
  while (index < content.length) {
    const pos = content.indexOf(searchText.value, index)
    if (pos === -1) break
    matches.push({ from: pos, to: pos + searchText.value.length })
    index = pos + 1
  }

  searchMatches.value = matches.length
  searchCurrentMatch.value = matches.length > 0 ? 1 : 0

  if (matches.length > 0 && view.value) {
    view.value.dispatch({
      effects: setSearchQuery.of(query),
    })
    view.value.dispatch({
      selection: { anchor: matches[0].from, head: matches[0].to },
    })
  }
}

const findNext = () => {
  if (!view.value || !searchText.value || matches.length === 0) return

  if (searchCurrentMatch.value < searchMatches.value) {
    searchCurrentMatch.value++
  } else {
    searchCurrentMatch.value = 1
  }

  const matchIndex = searchCurrentMatch.value - 1
  const match = matches[matchIndex]

  if (match && view.value) {
    view.value.dispatch({
      selection: { anchor: match.from, head: match.to },
    })
    view.value.dispatch({
      effects: EditorView.scrollIntoView(match.from, { y: 'center' }),
    })
  }
}

const findPrev = () => {
  if (!view.value || !searchText.value || matches.length === 0) return

  if (searchCurrentMatch.value > 1) {
    searchCurrentMatch.value--
  } else {
    searchCurrentMatch.value = searchMatches.value
  }

  const matchIndex = searchCurrentMatch.value - 1
  const match = matches[matchIndex]

  if (match && view.value) {
    view.value.dispatch({
      selection: { anchor: match.from, head: match.to },
    })
    view.value.dispatch({
      effects: EditorView.scrollIntoView(match.from, { y: 'center' }),
    })
  }
}

const getLogData = () => {
  detailLoading.value = true

  getServiceLog(5000)
    .then(res => {
      if (res.succ) {
        if (Array.isArray(res.content)) {
          logContent.value = res.content.join('\n')
        } else {
          logContent.value = res.content || ''
        }
        if (searchText.value) {
          performSearch()
        }
      } else {
        ElMessage.error(res.msg || '获取日志失败')
        logContent.value = ''
      }
    })
    .catch(error => {
      ElMessage.error('获取日志失败', error)
      logContent.value = ''
    })
    .finally(() => {
      detailLoading.value = false
    })
}

const refreshLog = () => {
  logContent.value = ''
  getLogData()
}

const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
}

defineExpose({
  getLog: getLogData
})
</script>

<style lang="scss" scoped>
.service-log {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.control-panel {
  width: 100%;
  padding: 10px 30px;
  background: #f5f7fa;
  box-shadow: 0px 8px 38px 0px rgba(3, 13, 28, 0.02);
  border-radius: 20px;

  display: flex;
  flex-direction: row;
  gap: 20px;
  align-items: center;

  .search-wrapper {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 12px;
  }

  .right-buttons {
    display: flex;
    flex-direction: row;
    gap: 12px;
    align-items: center;
  }

  &.service-panel {
    justify-content: flex-end;
  }
}

.search-toggle-btn {
  width: 32px;
  height: 32px;
  line-height: 32px;
  font-size: 18px;
  text-align: center;
  cursor: pointer;
  background: #f2f3f5;
  border-radius: 2px;
}

.search-panel {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  padding: 1px 8px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  height: 32px;
  box-sizing: border-box;

  :deep(.el-input) {
    min-width: 200px;
    height: 28px;

    .el-input__wrapper {
      padding: 0 8px;
      box-shadow: none;
      border: none;
      padding-right: 24px;
      min-height: 28px;
      height: 28px;
    }

    .el-input__inner {
      font-size: 14px;
      height: 26px;
      line-height: 26px;
    }
  }

  button {
    width: 26px;
    height: 26px;
    border: none;
    background: #f2f3f5;
    border-radius: 2px;
    cursor: pointer;
    font-size: 12px;
    line-height: 26px;

    &:hover {
      background: #e4e7ed;
    }
  }

  .search-count {
    font-size: 12px;
    color: #909399;
  }
}

.tableBox {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;

  .log-editor-wrapper {
    border: 1px solid #ebebeb;
    border-radius: 4px;
    overflow: hidden;
    position: relative;
    height: 500px;
  }

  :deep(.cm-editor) {
    height: 100%;
    overflow: hidden;
  }

  :deep(.cm-scroller) {
    overflow-x: auto !important;
    overflow-y: auto !important;
  }

  :deep(.cm-content) {
    white-space: pre !important;
  }

  :deep(.cm-searchMatch) {
    background-color: #ffdd33;
  }
}

.refresh-btn {
  width: 32px;
  height: 32px;
  line-height: 32px;
  font-size: 18px;
  text-align: center;
  cursor: pointer;
  background: #f2f3f5;
  border-radius: 2px;
}

.theme-toggle {
  width: 32px;
  height: 32px;
  line-height: 32px;
  font-size: 18px;
  text-align: center;
  cursor: pointer;
  background: #f2f3f5;
  border-radius: 2px;
}

.spin-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}
</style>
