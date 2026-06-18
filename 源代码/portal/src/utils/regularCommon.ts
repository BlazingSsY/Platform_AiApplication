/*
 * @Description: 模块名称
 * @Author: ym
 * @Date: 2021-03-05 09:23:06
 * @LastEditTime: 2024-01-23 10:30:39
 */
/* eslint-disable */
//汉字、数字、字母、下划线
export const isNameRegular = (rule: any, value: any, callback: any) => {
    const nameV = /^[a-zA-Z0-9_\-\u4e00-\u9fa5]{1,20}$/
    if (!nameV.test(value)) {
        callback(new Error(`请输入1-20位汉字/数字/字母/下划线`))
    } else {
        callback()
    }
}
// 密码 6-20
export const isPassRegular = (rule: any, value: any, callback: any) => {
    const nameV = /^[a-zA-Z0-9_]{6,20}$/
    if (!nameV.test(value)) {
        callback(new Error(`请输入6-20位数字/字母/下划线`))
    } else {
        callback()
    }
}
//手机号、座机号
export const isMobleAndPhoneRegular = (rule: any, value: any, callback: any) => {
    const telephoneReg =  /^0\d{2,3}-?\d{7,8}$/
    const phoneReg = /^1[3456789]\d{9}$/
    if (!value || value.length === 0) {
        callback(new Error('请输入正确的手机/座机号码'))
    } else if (!telephoneReg.test(value)&&!phoneReg.test(value)) {
        callback(new Error(`请输入正确的手机/座机号码`))
    } else {
        callback()
    }
}
//手机号
export const isMoblePhoneRegular = (rule: any, value: any, callback: any) => {
    const telephoneReg = /^1[3456789]\d{9}$/
    if (!value || value.length === 0) {
        callback(new Error(' '))
    } else if (!telephoneReg.test(value)) {
        callback(new Error(`请输入正确的手机号码`))
    } else {
        callback()
    }
}
export const isMoblePhoneChange = (rule: any, value: any, callback: any) => {
    value = value.replace(/[^\d]/g, ``)
    callback()
}
// 固定电话
export const isPhoneRegular = (rule: any, value: any, callback: any) => {
    const telephoneReg = /^([0-9]{3,4}-)?[0-9]{7,8}$/
    if (!value || value.length === 0) {
        callback(new Error(`请输入固定电话`))
    } else if (!telephoneReg.test(value)) {
        callback(new Error(`请输入正确的固定电话`))
    } else {
        callback()
    }
}
// 邮箱
export const isEmailRegular = (rule: any, value: any, callback: any) => {
    var emailRegExp = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
    var emailRegExp1 = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
    if ((!emailRegExp.test(value) && value != '') || (!emailRegExp1.test(value) && value != '')) {
      callback(new Error('请输入有效邮箱格式！'));
    } else {
      callback();
    }
}
// 数字、字母、下划线 4-20
export const isLoginRegular = (rule: any, value: any, callback: any) => {
    const reg = /^[\w\u4E00-\u9FA5\uF900-\uFA2D]*$/
    if (!reg.test(value)) {
        callback(new Error(`请输入4-20位数字/字母/下划线`))
    } else {
        callback()
    }
}
// 域名，机构别名 数字/字母/减号/下划线,不能以符号开头或结尾
export const isDomainRegular = (rule: any, value: any, callback: any) => {
    //const nameV = /^[a-zA-Z0-9](([-_a-zA-Z0-9]*[a-zA-Z0-9])?){1,20}$/; //机构别名 数字/字母/减号/下划线,不能以符号开头或结尾
    const nameV = /^[a-zA-Z0-9]{1,20}$/ //只能输入数字字母
    const bool = nameV.test(value)
    if (!bool) {
        //callback(new Error(`请输入1-20位数字/字母/减号/下划线,不能以符号开头或结尾`));
        callback(new Error(`只能输入1-20位数字或字母`))
    }
    callback()
}
// 备注：汉字/数字/字母/常用标点符号
export const isRemarksRegular = (rule: any, value: any, callback: any) => {
    const nameV = /^[a-zA-Z0-9_\u4e00-\u9fa5,\.;\:"'!/[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019]{0,150}$/
    if (!nameV.test(value)) {
        return callback(new Error(`请输入1-150位汉字/数字/字母/常用标点符号`))
    }
    callback()
}

export const isValidityAmount = (rule, value, callback) => {
    let pattern = /^-?0\.[0-9]{1,2}$|^0$|^-?[1-9]\d{0,10}\.\d{1,2}$|^-?[1-9]\d{0,10}$/
    rule = Object.assign({}, { min: 0, max: 1e10 }, rule)
    if (value === '') {
        callback(new Error(rule.message))
    } else if (!pattern.test(value)) {
        callback(new Error('输入值必须是有效数字,最多2位小数'))
    } else {
        callback()
    }
}

export const isValidityNumber = (rule, value, callback) => {
    
    let pattern = /^-?[0-9]+(\.[0-9]+)?$/
    rule = Object.assign({}, { min: 0, max: 1e10 }, rule)
    if (value === '') {
        callback(new Error(rule.message))
    } else if (!pattern.test(value)) {
        callback(new Error('输入值必须是数字'))
    } else if (value > rule.max) {
        callback(new Error(`输入值必须小于${rule.max}`))
    } else if (value < rule.min) {
        callback(new Error(`输入值必须大于${rule.min}`))
    } else {
        callback()
    }
}
