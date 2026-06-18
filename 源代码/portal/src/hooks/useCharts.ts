import { shallowRef, onUnmounted, watch, type Ref } from 'vue';
import * as Echarts from 'echarts';
import type { EChartsOption } from 'echarts';
import 'echarts-gl';

export function useCharts(
  containerRef: Ref<HTMLDivElement | null>,
) {
  const chartInstance = shallowRef();

  const initChart = (option: EChartsOption = {}) => {
    if (!containerRef.value) {
      return;
    }
    chartInstance.value?.dispose();
    chartInstance.value = Echarts.init(containerRef.value);
    chartInstance.value.setOption(option);
    return chartInstance.value
  };

  const handleResize = () => {
    chartInstance.value?.resize({ animation: { duration: 300 } });
  };

  onUnmounted(() => {
    chartInstance.value?.dispose();
    window.removeEventListener('resize', handleResize);
  });
  
  return { chartInstance, initChart, handleResize };
}
