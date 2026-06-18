<template>
  <div ref="chartContainer" class="chart-container"/>
</template>

<script setup>
import { ref, onMounted, watch, toRaw, onUnmounted } from 'vue';
import { debounce } from 'lodash-es';
import { useCharts } from '@/hooks/useCharts';
import { getOption } from './chart';

const props = defineProps({
    dataset: {
        type: Array,
        default: () => []
    },
});

const chartContainer = ref()
// const highlightedName = ref()
const { dataset } = props
const { initChart, handleResize } = useCharts(chartContainer)
// const highlightSeries = debounce((params) => {
//     highlightedName.value = params.seriesName !== highlightedName.value ? params.seriesName : undefined
// }, 200)

watch([
    () => dataset,
    // () => highlightedName.value
], ([
    nextDataset,
    // nextHighlightedName
]) => {
    // const option = getOption(toRaw(nextDataset), nextHighlightedName)
    const option = getOption(toRaw(nextDataset))
    // const chartInstance = initChart(option)
    // chartInstance.on('click', { seriesType: 'surface' }, highlightSeries)
    initChart(option)
}, { deep: true });


onMounted(() => {
    window.addEventListener('resize', handleResize)
});

onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.chart-container {
    width: 100%;
    height: 100%;
    border-radius: 20px;
}
</style>
