<template>
    <div class="stats-charts">
        <div class="chart-settings">
            <el-dropdown
                trigger="click"
                style="width: 120px;"
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
                        v-for="category in CATEGORY_OPTIONS"
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
                @command="(valueField) => setValueField(valueField)"
            >
                <div class="el-dropdown-link">
                    <span>{{ chartSettings.valueField }}</span>
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu style="max-height: 30vh; min-width: 200px;">
                    <el-dropdown-item
                        v-for="valueField in valueFieldOptions"
                        :key="valueField"
                        :command="valueField"
                    >
                        {{ valueField }}
                    </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-dropdown
                style="width: 95px;"
                trigger="click"
                :disabled="controlDisabled"
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
import { useCharts } from '@/hooks/useCharts';
import {
    CATEGORY_OPTIONS,
    CHART_TYEP_OPTIONS
} from './constants';
import { getOption } from './chart';

const props = defineProps({
    chartData: {
        type: Object,
        default: () => {}
    },
    width: {
        type: String,
        default: () => '100%'
    }
});

const controlDisabled = computed(() => !props.chartData || Object.keys(props.chartData).length === 0)
const chartContainer = ref()
const getValueFieldOptions = (chartData, category) => {
    if (chartData && chartData[category]) {
        return Object.keys(chartData[category])
    } else {
        return []
    }
}

const chartSettings = ref({
    category: CATEGORY_OPTIONS[0],
    valueField: undefined,
    chartType: CHART_TYEP_OPTIONS[0].value
})
const setCategory = (value) => chartSettings.value.category = value
const setValueField = (value) => chartSettings.value.valueField = value
const setChartType = (value) => chartSettings.value.chartType = value

const valueFieldOptions = computed(() => 
  getValueFieldOptions(props.chartData, chartSettings.value.category)
)

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

onMounted(() => {
    window.addEventListener('resize', handleResize)
});

onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
});
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
