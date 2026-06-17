<template>
  <div ref="chartContainer" class="chart-container"/>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useCharts } from '@/hooks/useCharts';
import { getOption } from './chart';

const props = defineProps({
    dataset: {
        type: Array,
        default: () => []
    },
    title: {
        type: String,
        default: () => ''
    }
});

const chartContainer = ref()
const { dataset, title } = props
const { initChart, handleResize } = useCharts(chartContainer)

watch([() => dataset], ([nextDataset]) => {
    const option = getOption(toRaw(nextDataset), title)
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
