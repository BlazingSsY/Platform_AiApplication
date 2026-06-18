const INNER_RADIUS = 5
const OUTER_RADIUS = 7
const PIPE_WIDTH = OUTER_RADIUS - INNER_RADIUS
const MID_RADIUS = INNER_RADIUS + PIPE_WIDTH / 2
const TOP_HEIGHT = 4
const TOP_HEIGHT_HIGHLIGHTED = 8
const BOTTOM_HEIGHT = -1
const TOTAL_STEPS = 360
const FRAME_LINE_COLOR = '#DBE9FA'

type ItemStyle = {
    color: string
}

type DataItem = {
    name: string,
    value: number,
    itemStyle: ItemStyle
}

type Dataset = DataItem[]

const generateArcLineData = (radius: number, z: number, startAngle: number, endAngle: number) => {
    const data: number[][] = []
    const oneStep = (2 * Math.PI) / TOTAL_STEPS
    for (let i = 0; i <= TOTAL_STEPS; i++) {
        const rad = i * oneStep
        if (rad > startAngle - oneStep && rad < endAngle + oneStep) {
            const x = radius * Math.cos(rad)
            const y = radius * Math.sin(rad)
            data.push([x, y, z])
        }
    }
    return data
}

const generateSeparateLines = (startAngle: number, endAngle: number, isHighlighted: boolean) => {
    const topHeight = isHighlighted ? TOP_HEIGHT_HIGHLIGHTED : TOP_HEIGHT
    const ps1 = [INNER_RADIUS * Math.cos(startAngle), INNER_RADIUS * Math.sin(startAngle), topHeight]
    const ps2 = [INNER_RADIUS * Math.cos(startAngle), INNER_RADIUS * Math.sin(startAngle), BOTTOM_HEIGHT]
    const ps3 = [OUTER_RADIUS * Math.cos(startAngle), OUTER_RADIUS * Math.sin(startAngle), BOTTOM_HEIGHT]
    const ps4 = [OUTER_RADIUS * Math.cos(startAngle), OUTER_RADIUS * Math.sin(startAngle), topHeight]
    const pe1 = [INNER_RADIUS * Math.cos(endAngle), INNER_RADIUS * Math.sin(endAngle), topHeight]
    const pe2 = [INNER_RADIUS * Math.cos(endAngle), INNER_RADIUS * Math.sin(endAngle), BOTTOM_HEIGHT]
    const pe3 = [OUTER_RADIUS * Math.cos(endAngle), OUTER_RADIUS * Math.sin(endAngle), BOTTOM_HEIGHT]
    const pe4 = [OUTER_RADIUS * Math.cos(endAngle), OUTER_RADIUS * Math.sin(endAngle), topHeight]
    return [
        [ps1, ps2],
        [ps2, ps3],
        [ps3, ps4],
        [ps4, ps1],
        [pe1, pe2],
        [pe2, pe3],
        [pe3, pe4],
        [pe4, pe1]
    ]
}

const getArcLineSeries = (startAngle: number, endAngle: number, isHighlighted: boolean) => {
    const topHeight = isHighlighted ? TOP_HEIGHT_HIGHLIGHTED : TOP_HEIGHT
    const params = [
        [INNER_RADIUS, topHeight],
        [INNER_RADIUS, BOTTOM_HEIGHT],
        [OUTER_RADIUS, topHeight],
        [OUTER_RADIUS, BOTTOM_HEIGHT]
    ]
    return params.map(([radius, z]) => {
        return {
            type: 'line3D',
            lineStyle: { color: FRAME_LINE_COLOR, width: 2 },
            data: generateArcLineData(radius, z, startAngle, endAngle)
        }
    })
}

const getSeparateLineSeries = (startAngle: number, endAngle: number, isHighlighted: boolean) => {
    const lines = generateSeparateLines(startAngle, endAngle, isHighlighted)
    return lines.map(([startPoint, endPoint]) => {
        return {
            type: 'line3D',
            lineStyle: { color: FRAME_LINE_COLOR, width: 2 },
            data: [startPoint, endPoint]
        }
    })
}

const generateEndCap = (
  angle: number, 
  isHighlighted: boolean,
  itemStyle: ItemStyle
) => {
  const topHeight = isHighlighted ? TOP_HEIGHT_HIGHLIGHTED : TOP_HEIGHT;
  return {
    type: 'surface',
    parametric: true,
    shading: 'color',
    wireframe: { show: false },
    itemStyle,
    parametricEquation: {
      u: { min: INNER_RADIUS, max: OUTER_RADIUS, step: 0.01 },
      v: { min: 0, max: 1, step: 0.01 },
      x: (u: number, v: number) => u * Math.cos(angle),
      y: (u: number, v: number) => u * Math.sin(angle),
      z: (u: number, v: number) => 
        BOTTOM_HEIGHT + v * (topHeight - BOTTOM_HEIGHT)
    }
  };
};

const getLabelSeries = (name: string, startAngle: number, endAngle: number, value: number, itemStyle: ItemStyle) => {
    const LABEL_LINE_LENGTH = 2
    const TURN_LENGTH = 2
    const midRadian = (startAngle + endAngle) / 2
    
    // const i = 1
    // const posX = Math.cos(midRadian) * (1 + Math.cos(Math.PI / 2));
    // const posY = Math.sin(midRadian) * (1 + Math.cos(Math.PI / 2));
    // const posZ = Math.log(Math.abs(value + 1)) * 0.1;
    // const flag = ((midRadian >= 0 && midRadian <= Math.PI / 2) || (midRadian >= 3 * Math.PI / 2 && midRadian <= Math.PI * 2)) ? 1 : -1;
    // let turningPosArr = [posX * (1.8) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posY * (1.8) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posZ * (2)]
    // let endPosArr = [posX * (1.9) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posY * (1.9) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posZ * (6)]

    const startPoint = [
        OUTER_RADIUS * Math.cos(midRadian),
        OUTER_RADIUS * Math.sin(midRadian),
        TOP_HEIGHT_HIGHLIGHTED
    ]
    // const turningPoint = [
    //     (OUTER_RADIUS + LABEL_LINE_LENGTH) * Math.cos(midRadian),
    //     (OUTER_RADIUS + LABEL_LINE_LENGTH) * Math.sin(midRadian),
    //     TOP_HEIGHT_HIGHLIGHTED
    // ]
    const endPoint = [
        (OUTER_RADIUS + LABEL_LINE_LENGTH) * Math.cos(midRadian),
        (OUTER_RADIUS + LABEL_LINE_LENGTH) * Math.sin(midRadian),
        TOP_HEIGHT_HIGHLIGHTED + LABEL_LINE_LENGTH
    ]

    let color = itemStyle.color;

    console.log('startPoint', startPoint)
    // console.log('turningPoint', turningPoint)
    console.log('endPoint', endPoint)
    return [
        {
            type: 'line3D',
            lineStyle: {
                color: '#CCC',
            },
            data: [
                startPoint,
                // turningPoint,
                endPoint
            ]
        },
        {
            type: 'scatter3D',
            label: {
                show: true,
                distance: 0,
                position: 'center',
                textStyle: {
                    color: '#ffffff',
                    backgroundColor: color,
                    borderWidth: 2,
                    fontSize: 14,
                    padding: 10,
                    borderRadius: 4,
                },
                formatter: '{b}'
            },
            symbolSize: 0,
            data: [{
                name: name + '\n' + value,
                value: endPoint
            }]
        }
    ];
}

const getItemSeries = (name: string, startValue: number, value: number, total: number, itemStyle: ItemStyle, isHighlighted: boolean) => {
    const startAngle = (startValue / total) * 2 * Math.PI
    const endAngle = ((startValue + value) / total) * 2 * Math.PI
    const step = TOTAL_STEPS * (value / total)
    
    return [
        {
            type: 'surface',
            name,
            parametric: true,
            shading: 'color',
            wireframe: { show: false },
            itemStyle,
            parametricEquation: {
                u: { min: 0, max: 2 * Math.PI, step: Math.PI / step },
                v: { min: 0, max: 2 * Math.PI, step: Math.PI / step },
                x: (u: number, v: number) => {
                    let _u = u
                    if (u < startAngle) {
                        _u = startAngle
                    }
                    if (u > endAngle) {
                        _u = endAngle
                    }
                    return (MID_RADIUS + (PIPE_WIDTH / 2) * Math.cos(v)) * Math.cos(_u)
                },
                y: (u: number, v: number) => {
                    let _u = u
                    if (u < startAngle) {
                        _u = startAngle
                    }
                    if (u > endAngle) {
                        _u = endAngle
                    }
                    return (MID_RADIUS + (PIPE_WIDTH / 2) * Math.cos(v)) * Math.sin(_u)
                },
                z: (u: number, v: number) => {
                    return Math.sin(v) > 0 ? (isHighlighted ? TOP_HEIGHT_HIGHLIGHTED : TOP_HEIGHT) : BOTTOM_HEIGHT;
                }
            }
        },
        generateEndCap(startAngle, isHighlighted, itemStyle),
        generateEndCap(endAngle, isHighlighted, itemStyle),
        ...getArcLineSeries(startAngle, endAngle, isHighlighted),
        ...getSeparateLineSeries(startAngle, endAngle, isHighlighted),
        ...(isHighlighted ? getLabelSeries(name, startAngle, endAngle, value, itemStyle) : [])
    ]
}

const getSeries = (dataset: Dataset, total: number, highlightedName: string) => {
    if (total === 0) return []
    let series: any = []
    let startValue = 0
    dataset.forEach(dataItem => {
        const isHighlighted = highlightedName === dataItem.name
        const itemSeries = getItemSeries(dataItem.name, startValue, dataItem.value, total, dataItem.itemStyle, isHighlighted)
        startValue += dataItem.value
        series = [
            ...series,
            ...itemSeries
        ]
    })
    return series
}

const getOption = (dataset: Dataset, highlightedName: string) => {
    const total = dataset.reduce((result, data) => {
        return result += data.value
    }, 0)

    const option = {
        xAxis3D: { type: 'value' },
        yAxis3D: { type: 'value' },
        zAxis3D: {
            type: 'value',
            min: BOTTOM_HEIGHT - 2,
            max: TOP_HEIGHT_HIGHLIGHTED + 2,
            scale: false
        },
        grid3D: {
            show: false,
            boxHeight: 20,
            bottom: '50%',
            viewControl: {
                autoRotate: false,
                // rotateSensitivity: 0,
                // zoomSensitivity: 0,
                // panSensitivity: 0,
                distance: 100,
                alpha: 25,
                beta: 60,
            },
        },
        series: getSeries(dataset, total, highlightedName)  
    }
    return option
}

export default getOption
