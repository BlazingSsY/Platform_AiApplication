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

type DataItemType = {
    department: string,
    reviewedCount: number,
    totalCount: number
}

export const getOption = (dataset: DataItemType[] = []) => {
    const labels = dataset.map(data => data.department)
    const reviewedDataItems = dataset.map((data, index) => {
        const colorObj = COLORS[index % COLORS.length]
        return {
            value: data.reviewedCount,
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

    const unreviewedDataItems = dataset.map((data, index) => {
        const colorObj = COLORS[index % COLORS.length]
        return {
            value: data.totalCount - data.reviewedCount,
            itemStyle: {
                color: colorObj.top + '77'
            }
        }
    })

    // 计算所有数据的最大值，用于固定纵坐标
    const maxTotalCount = Math.max(...dataset.map(d => d.totalCount), 0)

    const zoomEnd = unreviewedDataItems.length <= 20 ? 100 : (20 / unreviewedDataItems.length) * 100

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
            max: maxTotalCount,
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
            name: '已审查文件',
            type: 'bar',
            stack: 'total',
            barMaxWidth: BAR_WIDTH,
            emphasis: {
                focus: 'series'
            },
            data: reviewedDataItems
        },
        {
            name: '未审查文件',
            type: 'bar',
            stack: 'total',
            barMaxWidth: BAR_WIDTH,
            emphasis: {
                focus: 'series'
            },
            data: unreviewedDataItems
        }],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
    }
}