import dayjs from 'dayjs';

export const getAccountDate = (item: any) => {
	if (item.accountDateStart && item.accountDateEnd) {
		const date1 = dayjs(item.accountDateStart);
		const date2 = dayjs(item.accountDateEnd);
		let formatted = ``;
		if (date1.year() === date2.year() && date1.month() === date2.month()) {
			// 跨越同一月份
			formatted = date1.format(`YYYY.M.D`) + `-` + date2.format(`D`);
		} else if (date1.year() === date2.year()) {
			// 跨越不同的月份但是在同一年内
			const formatted1 = date1.format(`YYYY.M.D`);
			const formatted2 = date2.format(`M.D`);
			formatted = `${formatted1} - ${formatted2}`;
		} else {
			// 跨越年份
			const formatted1 = date1.format(`YYYY.M.D`);
			const formatted2 = date2.format(`YYYY.M.D`);
			formatted = `${formatted1} - ${formatted2}`;
		}
		return formatted;
	}
	return ``;
};

export const getOptionValue = (options, value) => {
	const option = options.filter(item => item.id == value);
	return option.length ? option[0].name : ``;
};

export const shallowCopy = (obj1: any, obj2: any) => {
	console.error(obj1);
	for (const key in obj1) {
		if (obj2[key] || obj2[key] === 0) {
			obj1[key] = obj2[key];
		}
	}
	return obj1;
};
