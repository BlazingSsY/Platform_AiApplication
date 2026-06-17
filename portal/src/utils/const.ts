import { forIn } from 'lodash-es';

const eachMagic = (obj: any) => {
	const arr = [] as any;
	for (const key in obj) {
		arr.push({ 'value': key, 'name': obj[key] });
	}
	return arr;
};

export const getInfomationStatus = {
	'WAIT_REVIEW': `待审核`,
	'REVIEW_PASS': `审核通过`,
	'REVIEW_REFUSE': `审核拒绝`,
	'NORMAL': `正常`,
	'CANCEL': `禁用`,
	'DRAFT': `草稿`
};

export const userReviewStatus = {
	'WAIT_REVIEW': `待审核`,
	'REVIEW_PASS': `审核通过`,
	'REVIEW_REFUSE': `审核拒绝`
};

export const userEnableStatus = {
	'ENABLED': `正常`,
	'DISABLED': `已禁用`
};

export const getInfomationStatusList = eachMagic(getInfomationStatus);
export const getUserReviewStatus = eachMagic(userReviewStatus);
export const getUserEnableStatus = eachMagic(userEnableStatus);

export const getFinancialProductsStatus = {
	'NORMAL': `正常`,
	'CANCEL': `禁用`
};

export const getFinancialProductsStatusList = eachMagic(getFinancialProductsStatus);

// 企业类型
export const getBusinessType = {
	'STATE_OWNED': `央企国企`,
	'MIXED_OWNERSHIP': `上市公司、国企占股的混改公司`,
	'PRIVATE_FULLY_PAID': `民企（全部实缴）`,
	'PRIVATE_PARTIALLY_PAID_OVER_10_YEARS': `民企（部分实缴）存续10年以上`,
	'PRIVATE_NOT_PAID_OVER_3_10_YEARS': `民企（未实缴）存续3`,
	'PRIVATE_NOT_PAID_UNDER_3_YEARS': `民企（未实缴，存续3年以下）`
};

export const getBusinessTypeList = eachMagic(getBusinessType);

// 企业用户状态
export const getUserSateType = {
	'CERTIFY': `认证`,
	'INFO': `信息`,
	'TRADING': `交易`,
	'RENEGE': `违约`
};

export const getUserSateTypeList = eachMagic(getUserSateType);

// 培训会议状态
export const getCompanyType = {
	'MEETING': `会议`,
	'TRAIN': `培训`
};
export const geCompanyTypeList = eachMagic(getCompanyType);

// 交易类型字典
export const getTradingType = {
	'LOCK': `交易锁定`,
	'RELEASED': `交易解锁`,
	'TRADE_VIOLATE': `撮合保证金违约扣除`,
	'TRADE_COMPENSATION': `撮合保证金违约赔付`,
	'MATCH_DEPOSIT_ADD': `撮合保证金预存`,
	'MATCH_DEPOSIT_REDUCE': `撮合保障金退回`,
	'PLATFORM_DEPOSIT_ADD': `平台保障金预存`,
	'PLATFORM_DEPOSIT_REDUCE': `平台保障金退回`,
	'PLAT_VIOLATE': `平台保障金违约扣除`,
	'MATCH_BILL': `撮合费账单支付`,
	'PLAT_COMPENSATION': `平台保障金违约赔付`,
	'PLAT_INCOME': `平台收益`
};
export const getTradingTypeList = eachMagic(getTradingType);

//

// 评级
export const getCreditType = {
	'RATING': `评级授信`,
	'INCREASE': `补充保证金授信`,
	'REDUCE': `自主降低授信`,
	'REDUCE_VIOLATE': `违约降低授信`,
	'INCREASE_LEVEL': `等级提升授信`
};
export const getCreditTypeList = eachMagic(getCreditType);

// 挂单的订单状态
export const getOrderRecordType = {
	'PADDING': `已挂单`,
	'DONE': `已成交`,
	'REVOKED': `已撤单`,
	'LOCKED': `已锁定`
};
export const getOrderRecordTypeList = eachMagic(getOrderRecordType);

// 订单状态
export const getTradingMethodType = {
	'FIXED': `一口价`,
	'BASIS': `基差价`,
	'BIDDING': `集合竞价`
};
export const getTradingMethodTypeList = eachMagic(getTradingMethodType);

// 成交的订单状态
export const getOverRecordType = {
	'WAIT': `未成交`,
	'DEAL': `交易成功`,
	'APPEAL': `申诉中`,
	'FAILED': `交易失败`
};
export const getOverRecordTypeList = eachMagic(getOverRecordType);

export const appealStatus = {
	'0': `待平台处理`,
	'1': `交易申诉成功`,
	'2': `交易申诉失败`,
	'3': `交易申诉撤销`,
	'4': `待申诉方处理/被申诉方已处理`,
	'5': `待被申诉方处理/申诉方已处理`,
	'6': `交易申诉已核准`,
	'7': `赔付保证金`,
	'8': `赔付保障金`,
	'9': `交易申诉已完结`
};
