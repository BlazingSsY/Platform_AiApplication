<template>
  <div ref="chartContainer" class="chart-container"/>
</template>

<script setup>
import { ref, onMounted, watch, toRaw, onUnmounted } from 'vue';
import { useCharts } from '@/hooks/useCharts';
import { getOption } from './chart';

const props = defineProps({
    dataset: {
        type: Array,
        default: () => []
    },
});

const chartContainer = ref()
const { dataset } = props

let option = ref(getOption(toRaw(props.dataset)))
const { initChart, handleResize } = useCharts(chartContainer)

onMounted(() => {
    window.addEventListener('resize', handleResize)
});

watch(() => dataset, (nextDataset) => {
    option.value = getOption(toRaw(nextDataset))
    initChart(option.value)
}, { deep: true });

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
