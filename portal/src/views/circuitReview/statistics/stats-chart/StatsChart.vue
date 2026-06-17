<template>
    <div class="stats-charts">
        <div class="chart-settings">
            <el-dropdown
                trigger="click"
                style="width: 130px;"
                :disabled="controlDisabled"
                @command="(value) => setCategory(value)"
            >
                <div class="el-dropdown-link">
                    <span>{{ chartSettings.category }}</span>
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu style="max-height: 30vh; min-width: 200px;">
                    <el-dropdown-item
                        v-for="category in categoryOptions"
                        :key="category"
                        :command="category"
                    >
                        {{ category }}
                    </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-dropdown
                style="width: 150px;"
                trigger="click"
                :disabled="controlDisabled"
                :title="TOOLTIP_MAP[chartSettings.valueField]"
                @command="(valueField) => setValueField(valueField)"
            >
                <div class="el-dropdown-link" >
                    <span>{{ chartSettings.valueField }}</span>
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu style="max-height: 30vh; min-width: 200px;">
                    <el-dropdown-item
                        v-for="valueField in valueFieldOptions"
                        :key="valueField"
                        :command="valueField"
                        :title="TOOLTIP_MAP[valueField]"
                    >
                        {{ valueField }}
                    </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-dropdown
                style="width: 95px;"
                trigger="click"
                :disabled="controlDisabled || chartTypeSelectDisabled"
                @command="(value) => setChartType(value)"
            >
                <div class="el-dropdown-link">
                    <span>{{ CHART_TYEP_OPTIONS.find(opt => opt.value === chartSettings.chartType)?.label }}</span>
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu style="max-height: 30vh; min-width: 200px;">
                    <el-dropdown-item
                        v-for="chartTypeOption in CHART_TYEP_OPTIONS"
                        :key="chartTypeOption.value"
                        :command="chartTypeOption.value"
                    >
                        {{ chartTypeOption.label }}
                    </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
        <div
            ref="chartContainer"
            :key="`chart-container-${width}`"
            class="chart-container"
            :style="`width: ${width}`"
        />
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import store from "@/store/index"
import { useCharts } from '@/hooks/useCharts';
import { is631Admin } from '@/hooks/user631Admin'
import {
    CHART_TYEPS,
    CHART_TYEP_OPTIONS
} from './constants';
import { getOption } from './chart';

const is631UserAdmin = is631Admin()
const userInfo = computed(() => store.state.user.userInfo)
const roleInfo = computed(() => userInfo.value?.role)
const roleName = computed(() => {
    return roleInfo.value ? roleInfo.value.name : ''
})

const isByUser = computed(() => is631UserAdmin.value || roleName.value === '普通用户')

const props = defineProps({
    chartData: {
        type: Object,
        default: () => {}
    },
    width: {
        type: String,
        default: () => '100%'
    },
    isSingleDepartment: {
        type: Boolean,
        default: false
    }
});

const controlDisabled = computed(() => !props.chartData || Object.keys(props.chartData).length === 0)
const chartTypeSelectDisabled = ref(false)
const chartContainer = ref()

const getCategoryOptions = (chartData, isByUser) => {
    if (chartData && chartData) {
        return Object.keys(chartData).filter((_category) => _category !== (isByUser ? '按单位' : '按用户'))
    } else {
        return []
    }
}

const getValueFieldOptions = (chartData, category) => {
    if (chartData && chartData[category]) {
        return Object.keys(chartData[category])
    } else {
        return []
    }
}

const chartSettings = ref({
    category: undefined,
    valueField: undefined,
    chartType: CHART_TYEP_OPTIONS[0].value
})

const categoryOptions = computed(() => 
  getCategoryOptions(props.chartData, isByUser.value)
)

const valueFieldOptions = computed(() => 
  getValueFieldOptions(props.chartData, chartSettings.value.category)
)

const setCategory = (value) => chartSettings.value.category = value
const setValueField = (value) => chartSettings.value.valueField = value
const setChartType = (value) => chartSettings.value.chartType = value

watch(categoryOptions, (newOptions) => {
    if (newOptions && newOptions.length > 0) {
        // 当只选择一个单位时，默认category为"按时间-月"
        if (props.isSingleDepartment && newOptions.includes('按时间-月')) {
            chartSettings.value.category = '按时间-月'
        } else {
            chartSettings.value.category = newOptions[0]
        }
    }
})

watch(valueFieldOptions, (newOptions) => {
    if (newOptions && newOptions.length > 0) {
        chartSettings.value.valueField = newOptions[0]
    }
})

const chartOption = ref({})
const { initChart, handleResize } = useCharts(chartContainer)
watch(
  [
    () => props.width,
    () => props.chartData,
    () => chartSettings.value.category,
    () => chartSettings.value.valueField,
    () => chartSettings.value.chartType
  ],
   async ([newWidth, newChartData, newCategory, newValueField, newChartType]) => {
        await nextTick();
        const currentSettings = {
            category: newCategory,
            valueField: newValueField,
            chartType: newChartType
        }
        chartOption.value = getOption(newChartData, currentSettings);
        initChart(chartOption.value);
  },
  { deep: true }
);

watch(
  [
    () => chartSettings.value.valueField,
  ],
  ([newValueField]) => {
    if (newValueField === '当前文件数量') {
        chartSettings.value.chartType = CHART_TYEPS.bar
        chartTypeSelectDisabled.value = true
    } else {
        chartTypeSelectDisabled.value = false
    }
  },
  { deep: true }
);

onMounted(() => {
    window.addEventListener('resize', handleResize)
});

onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
});

const TOOLTIP_MAP = {
    '当前文件数量': '统计不包括文件版本及已粉碎的文件',
    '已审文件数量': '统计文件版本并包含已粉碎的文件版本'
}
</script>

<style scoped>
.stats-charts {
    width: 100%;
    height: 100%;
    
    background: #F5F7FA;
    box-shadow: 0px 8px 38px 0px rgba(3,13,28,0.02);
    border-radius: 20px;

    position: relative;
}

.chart-settings {
    position: absolute;
    z-index: 10;
    height: 40px;
    top: 10px;
    left: 15px;

    display: flex;
    flex-direction: row;
    gap: 10px;
}

.chart-container {
    width: 100%;
    height: 100%;
}

:deep(.el-dropdown) {
    height: 30px;
    border-radius: 4px;
    background-color: #FFF;
    box-shadow: 0 0 0 1px #dcdfe6;
}

:deep(.el-dropdown-link) {
    width: 100%;
    height: 100%;
    padding-left: 15px;
    padding-right: 15px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
}
</style>
