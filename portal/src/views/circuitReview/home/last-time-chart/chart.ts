type DataItemType = {
    department: string,
    reviewedCount: number,
    totalCount: number
}

export const getOption = (dataset: DataItemType[] = []) => {

    const labels = dataset.map(data => data.department)
    const reviewedDataItems = dataset.map((data, index) => {
        return {
            value: data.reviewedCount,
        }
    })

    const unreviewedDataItems = dataset.map((data, index) => {
        return {
            value: data.totalCount,
        }
    })

    return {
        xAxis: {
            type: 'category',
            data: labels,
            axisTick: { show: false },
            axisLine: { show: false },
            axisLabel: {
                interval: 0,
                rotate: 45,
                margin: 12,
            }
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            name: '已审查文件',
            type: 'line',
            emphasis: {
                focus: 'series'
            },
            data: reviewedDataItems
        },
        {
            name: '上传文件',
            type: 'line',
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