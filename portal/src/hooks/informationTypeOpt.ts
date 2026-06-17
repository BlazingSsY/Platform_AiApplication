export const informationTypeOpt = [
    {
        value: 1,
        label: '新闻'
    },
    {
        value: 2,
        label: '通知'
    },
    {
        value: 3,
        label: '资讯'
    }
]   

export const informationTypeOptlabel = (value: number) => {
    const item = informationTypeOpt.find(i => i.value === value)
    return item?.label || ''
}