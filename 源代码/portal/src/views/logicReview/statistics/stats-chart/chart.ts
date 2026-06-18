import { EChartOption } from "echarts"
import { CHART_TYEPS } from "./constants";

type Dataset = (string | number | object)[][]

type SettingsType = {
    valueField: string,
    isPercentage: boolean
}

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

const BAR_WIDTH = 40

export const barBuilder = (dataset: Dataset, settings: SettingsType) => {
    const { isPercentage } = settings
    const labels = dataset.map(data => data[0])
    let series: any[] = []

    // 计算数据的最大值，用于固定纵坐标
    let maxValue = 0
    if (dataset[0] && typeof dataset[0][1] === 'number') {
        maxValue = Math.max(...dataset.map(d => d[1] as number), 0)
    } else if (dataset[0] && typeof dataset[0][1] === 'object') {
        maxValue = Math.max(...dataset.map(d => {
            const obj = d[1] as any
            // 动态计算所有子项的和
            return Object.values(obj).reduce((sum: number, val: any) => sum + (typeof val === 'number' ? val : 0), 0)
        }), 0)
    }

    if (dataset[0] && typeof dataset[0][1] === 'number') {
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
        series = [{
            type: 'bar',
            barMaxWidth: BAR_WIDTH,
            data: dataItems
        }]
    } else if (dataset[0] && typeof dataset[0][1] === 'object') {
        let subKeys = Object.keys(dataset[0][1])
        series = subKeys.map((_key, subIndex) => ({
            name: _key,
            type: 'bar',
            stack: 'total',
            barMaxWidth: BAR_WIDTH,
            emphasis: {
                focus: 'series'
            },
            data: dataset.map((data, index) => {
                const colorObj = COLORS[index % COLORS.length]
                return {
                    value: data[1][_key],
                    itemStyle: {
                        color: colorObj.top,
                        opacity: 1 - (0.5 / (subKeys.length - 1)) * subIndex
                    }
                }
            })
        }))
    }

    const zoomEnd = labels.length <= 20 ? 100 : (20 / labels.length) * 100
    return {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            valueFormatter: (value) => isPercentage ? `${value}%` : value
        },
        grid: {
            top: '20%',
            left: '5%',
            right: '5%',
            containLabel: true
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
        series,
    }
}

export const lineBuilder = (dataset: Dataset, settings: SettingsType) => {
    const { isPercentage } = settings
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

    return {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            valueFormatter: (value) => isPercentage ? `${value}%` : value
        },
        grid: {
            top: '20%',
            left: '5%',
            right: '5%',
            bottom: '5%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: labels,
            axisTick: { show: false },
            axisLine: { show: false },
            axisLabel: {
                interval: 0,
                rotate: 45,
                margin: 12,
                formatter: (val) => val.length > 6 ? val.slice(0,4)+'...' : val
            }
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            type: 'line',
            data: dataItems
        }]
    }
}

export const pieBuilder = (dataset: Dataset, settings: SettingsType) => {
    const { valueField, isPercentage } = settings
    const dataItems = dataset.map((data, index) => {
        const colorObj = COLORS[index % COLORS.length]
        return {
            name: data[0],
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

    return {
        tooltip: {
            trigger: 'item',
            valueFormatter: (value) => isPercentage ? `${value}%` : value
        },
        legend: {
            show: dataItems.length <= 10,
            bottom: '5%',
            left: 'center'
        },
        series: [{
            name: valueField,
            type: 'pie',
            radius: '45%',
            avoidLabelOverlap: false,
            emphasis: {
                label: {
                    show: dataItems.length <= 10,
                    fontSize: 15,
                    fontWeight: 'bold',
                }
            },
            label: {
                formatter: isPercentage ? '{b}: {c}%' : '{b}: {c}'
            },
            data: dataItems
        }]
    };
}

export const builderMap = {
    [CHART_TYEPS.bar]: barBuilder,
    [CHART_TYEPS.line]: lineBuilder,
    [CHART_TYEPS.pie]: pieBuilder
}

export const getOption = (chartData = {}, chartSettings: any = {}) => {
    const { chartType, category, valueField } = chartSettings;
    const isPercentage = valueField === '平均通过率(%)'
    const dataObj = chartData && chartData[category] && chartData[category][valueField] ? chartData[category][valueField] : undefined
    const chartBuilder = builderMap[chartType]

    if (!dataObj || !chartBuilder) return {}

    let dataset: any[] = Object.entries(dataObj)

    // 当条件是"按单位"并且图形是柱状图时，按数值从大到小排序
    if (category === '按单位' && chartType === CHART_TYEPS.bar) {
        dataset.sort((a, b) => {
            const aValue = typeof a[1] === 'number'
                ? a[1]
                : Object.values(a[1] as any).reduce((sum: number, val: any) => sum + (typeof val === 'number' ? val : 0), 0)
            const bValue = typeof b[1] === 'number'
                ? b[1]
                : Object.values(b[1] as any).reduce((sum: number, val: any) => sum + (typeof val === 'number' ? val : 0), 0)
            return bValue - aValue
        })
    }

    return chartBuilder(dataset, { valueField, isPercentage })
}