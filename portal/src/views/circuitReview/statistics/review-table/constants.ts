const COMMON_CONFIG = [
    {
        key: 'fileName',
        label: '网表文件',
        colWidth: 280,
    },
    {
        key: 'checkPoints',
        label: '审查点数',
        colWidth: 100,
    },
    {
        key: 'failCheckPoints',
        label: '问题点数',
        colWidth: 100,
    },
    {
        key: "closedFailCheckPoints",
        label: "关闭问题点数",
        colWidth: 110
    },
    {
        key: 'reviewTime',
        label: '审查时间',
        colWidth: 180,
    },
    {
        key: 'passRate',
        label: '通过率',
        colWidth: 100
    },
    {
        key: 'isClosedLoop',
        label: '状态',
        colWidth: 120
    },
    {
        key: 'exceedHalfMonthNotClosed',
        label: '超半月未关闭',
        colWidth: 120,
    }
]

export const REVIEW_TABLE_CONFIG = [
    ...COMMON_CONFIG
]

export const REVIEW_TABLE_CONFIG_BY_USER = [
    ...COMMON_CONFIG
]