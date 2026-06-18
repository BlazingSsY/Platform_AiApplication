import { EChartOption } from "echarts"

const BAR_WIDTH = 40
const COLORS = [
    {
        top: '#6688FF',
        bottom: '#4D74FF'
    },
    {
        top: '#2D6DC4',
        bottom: '#093C80'
    },
    {
        top: '#36B5F3',
        bottom: '#78D2FF'
    },
    {
        top: '#5AF3B8',
        bottom: '#38E09F'
    },
    {
        top: '#FFCF37',
        bottom: '#FFD982'
    }
]

export const getOption = (dataset = []) => {
    const labels = dataset.map(data => data[0])
    const dataItems = dataset.map((data, index) => {
        const colorObj = COLORS[index % COLORS.length]
        return {
            value: data[1],
            itemStyle: {
                color: {
                    type: 'linear',
                    x: 0, y: 0,
                    x2: 0, y2: 1,
                    colorStops: [
                        { offset: 0, color: colorObj.top },
                        { offset: 1, color: colorObj.bottom }
                    ]
                }
            }
        }
    })

    // 计算所有数据的最大值，用于固定纵坐标
    const maxValue = Math.max(...dataset.map(d => d[1]), 0)

    const zoomEnd = dataItems.length <= 20 ? 100 : (20 / dataItems.length) * 100

    return {
        xAxis: {
            type: 'category',
            data: labels,
            axisTick: { show: false },
            axisLine: { show: false },
            axisLabel: {
                interval: 0,
                margin: 12,
                formatter: (val) => val.length > 6 ? val.slice(0,6)+'...' : val
            }
        },
        yAxis: {
            type: 'value',
            min: 0,
            max: maxValue,
            scale: false
        },
        dataZoom: [
            {
                show: true,
                start: 0,
                end: zoomEnd,
                showDetail: false,
                zoomLock: true,
                moveHandleSize: 15,
                height: 0,
                bottom: 25
            },
        ],
        series: [{
            name: '问题数量统计',
            type: 'bar',
            stack: 'total',
            barMaxWidth: BAR_WIDTH,
            emphasis: {
                focus: 'series'
            },
            data: dataItems
        }],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
    }
}
