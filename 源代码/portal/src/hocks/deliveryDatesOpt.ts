/*
 * @Author: 杨生辉
 * @LastEditors: 杨生辉
 * @LastEditTime: 2024-05-24 17:49:45
 * @FilePath: \commodities-trading-platform-managefront\src\hocks\deliveryDatesOpt.ts
 * @Description: 描述
 */
export const deliveryDatesOpt = [
	{
		'name': `现货`,
		'inStock': true,
		'start': {
			'stepDay': 0,
			'stepMonth': 0
		},
		'end': {
			'stepDay': 0,
			'stepMonth': 0
		}
	},
	{
		'name': `本月上旬`,
		'inStock': false,
		'start': {
			'stepDay': 0,
			'stepMonth': 0
		},
		'end': {
			'stepDay': 9,
			'stepMonth': 0
		}
	},
	{
		'name': `本月中旬`,
		'inStock': false,
		'start': {
			'stepDay': 10,
			'stepMonth': 0
		},
		'end': {
			'stepDay': 19,
			'stepMonth': 0
		}
	},
	{
		'name': `本月下旬`,
		'inStock': false,
		'start': {
			'stepDay': 20,
			'stepMonth': 0
		},
		'end': {
			'stepDay': 29,
			'stepMonth': 0
		}
	},
	{
		'name': `n+1月上旬`,
		'inStock': false,
		'start': {
			'stepDay': 0,
			'stepMonth': 1
		},
		'end': {
			'stepDay': 9,
			'stepMonth': 1
		}
	},
	{
		'name': `n+1月中旬`,
		'inStock': false,
		'start': {
			'stepDay': 10,
			'stepMonth': 1
		},
		'end': {
			'stepDay': 19,
			'stepMonth': 1
		}
	},
	{
		'name': `n+1月下旬`,
		'inStock': false,
		'start': {
			'stepDay': 20,
			'stepMonth': 1
		},
		'end': {
			'stepDay': 29,
			'stepMonth': 1
		}
	},
	{
		'name': `n+2月上旬`,
		'inStock': false,
		'start': {
			'stepDay': 0,
			'stepMonth': 2
		},
		'end': {
			'stepDay': 9,
			'stepMonth': 2
		}
	},
	{
		'name': `n+2月中旬`,
		'inStock': false,
		'start': {
			'stepDay': 10,
			'stepMonth': 2
		},
		'end': {
			'stepDay': 19,
			'stepMonth': 2
		}
	},
	{
		'name': `n+2月下旬`,
		'inStock': false,
		'start': {
			'stepDay': 20,
			'stepMonth': 2
		},
		'end': {
			'stepDay': 29,
			'stepMonth': 2
		}
	}
];
