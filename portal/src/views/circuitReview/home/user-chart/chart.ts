// import get3DPieOption from '../../lib/3d-pie/3d-pie'
// import getAlter3D from '../../lib/3d-pie/alter'

// const IS_3D = false

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

// const getOption2d = (dataset = []) => {
//     const dataItems = dataset.map((data, index) => {
//         const colorObj = dataset.length === 1 ? COLORS[3] : COLORS[(index + 3) % COLORS.length]
//         return {
//             name: data[0],
//             value: data[1],
//             itemStyle: {
//                 color: {
//                     type: 'linear',
//                     x: 0, y: 0,
//                     x2: 0, y2: 1,
//                     colorStops: [
//                         { offset: 0, color: colorObj.top },
//                         { offset: 1, color: colorObj.bottom }
//                     ]
//                 }
//             }
//         }
//     })

//     return {
//         tooltip: {
//             trigger: 'item'
//         },
//         legend: {
//             bottom: '5%',
//             left: 'center'
//         },
//         series: [{
//             name: '用户数量统计',
//             type: 'pie',
//             radius: ['35%', '45%'],
//             avoidLabelOverlap: false,
//             label: {
//                 formatter: '{b}: {c}'
//             },
//             emphasis: {
//                 label: {
//                     show: true,
//                     fontSize: 15,
//                     fontWeight: 'bold'
//                 }
//             },
//             data: dataItems
//         }]
//     };
// }

// export const COLORS_3D_PIE = [
//     '#5377F6',
//     '#183D7C',
//     '#93D4FB',
//     '#6FCFE7',
//     '#87EFBC',
//     '#F9D98E',
//     '#EDBA7D',
//     '#F09E76',
//     '#C58A78',
//     '#E58FDE',
//     '#CF86FD',
//     '#9074DF'
// ]

// const getOption3d = (dataset = [], highlightedName: string) => {
//     const datalist = dataset.map((data, index) => {
//         const color = dataset.length === 1 ? COLORS_3D_PIE[3] : COLORS_3D_PIE[(index + 3) % COLORS_3D_PIE.length]
//         return {
//             name: data[0],
//             value: data[1],
//             itemStyle: {
//                 color,
//             },
//         }
//     })
//     // return get3DPieOption(datalist)
//     return getAlter3D(datalist, highlightedName)
// }

// export const getOption = IS_3D ? getOption3d : getOption2d

export const getOption = (dataset = [], title) => {
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
                formatter: (val) => val.length > 6 ? val.slice(0,6) + '...' : val
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
            name: title,
            type: 'bar',
            stack: 'total',
            barMaxWidth: 40,
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