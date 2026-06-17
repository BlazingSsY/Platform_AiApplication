import { CONTROL_TYPES } from "../components/constants"
import { fileNameFormatter } from "../components/utils"

export const STATUS = {
    OPEN: 'OPEN',
    REOPEN: 'REOPEN',
    CLOSED: 'CLOSED',
    NEW_OPEN: 'NEW_OPEN',
}

export const STATUS_LABEL_MAP = {
    [STATUS.OPEN]: '开启',
    [STATUS.REOPEN]: '重新开启',
    [STATUS.CLOSED]: '关闭',
    [STATUS.NEW_OPEN]: '新建',
}

const CONFIG_TITLE = {
    key: 'title',
    label: '标题',
    align: 'left'
}

const CONFIG_SUGGESTION = {
    key: 'suggestion',
    label: '意见反馈'
}

const CONFIG_UPDATE_DATE = {
    key: 'updateDate',
    label: '最后更新',
    colWidth: 250
}

const CONFIG_CREATE_USER_NAME = {
    key: 'createUserName',
    label: '提出人',
    colWidth: 250
}

const CONFIG_STATUS = {
    key: 'status',
    label: '状态',
    colWidth: 150,
    formatter: (entity: any) => STATUS_LABEL_MAP[entity.status] || entity.status
}

const CONFIG_APPEND_FILES = {
    key: 'appendFileList',
    label: '附件文件',
    colWidth: 245,
    controlType: CONTROL_TYPES.FILES,
    isRequired: false,
    formatter: fileNameFormatter('appendFileList')
}



export const FEEDBACK_CONFIGS_MAP = {
    title: CONFIG_TITLE,
    suggestion: CONFIG_SUGGESTION,
    updateData: CONFIG_UPDATE_DATE,
    createUserName: CONFIG_CREATE_USER_NAME,
}

export const FEEDBACK_TABLE_CONFIG = [
    CONFIG_TITLE,
    CONFIG_UPDATE_DATE,
    CONFIG_CREATE_USER_NAME,
    CONFIG_APPEND_FILES,
    CONFIG_STATUS,
]

export const STATUS_OPTION_MAP = {
    [STATUS.OPEN]: [{
        label: STATUS_LABEL_MAP[STATUS.CLOSED],
        value: STATUS.CLOSED
    }],
    [STATUS.REOPEN]: [{
        label: STATUS_LABEL_MAP[STATUS.CLOSED],
        value: STATUS.CLOSED
    }],
    [STATUS.CLOSED]: [{
        label: STATUS_LABEL_MAP[STATUS.REOPEN],
        value: STATUS.REOPEN
    }]
}

export const STATUS_BUTTON_TYPE_MAP = {
    [STATUS.OPEN]: 'primary',
    [STATUS.REOPEN]: 'warning',
    [STATUS.CLOSED]: 'danger'
}