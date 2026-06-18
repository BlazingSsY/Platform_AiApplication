/*
 * @Description:
 * @Author: hui
 * @Date: 2021-03-03 09:25:05
 * @LastEditTime: 2021-11-15 18:04:50
 */
import store from '@/store/user/index';
import { types } from 'sass';
import Number = types.Number;

export default {
	'hasPermi': {
		mounted(el: any, binding: any) {
			const { value } = binding;
			const permissions = store.state.powerList;
			if (value) {
				const hasPermissions = permissions.some((permission: string) => permission === value);
				if (!hasPermissions) {
					el.parentNode && el.parentNode.removeChild(el);
				}
			}
		}
	},
	// 数字小于0 的时候，用绿色，大于0的时候用红色， 等于0 的时候用灰色 , v-changeColor="{val: '18', tofixed:2}" 18.00  如果是  v-changeColor= 18 那么结果是18;
	'changeColor': {
		mounted(el: any, binding: any) {
			const value = binding.value;
			const text = parseFloat(value);
			el.textContent = parseFloat(value);
			if (text > 0) {
				el.style.color = `#f00`;
			} else if (text < 0) {
				el.style.color = `#82cf4d`;
			} else {
				el.style.color = `#b1b9c7`;
			}
		},
		updated(el: any, binding: any) {
			const value = binding.value;
			console.error(`value`, value);
			const text = parseFloat(value);
			el.textContent = parseFloat(value);
			if (text > 0) {
				el.style.color = `#f00`;
			} else if (text < 0) {
				el.style.color = `#82cf4d`;
			} else {
				el.style.color = `#b1b9c7`;
			}
		}
	},
	'auto': {
		mounted(el: any, bindings: any) {
			if (!bindings.value) {
				return;
			}
			let { offsetWidth } = el;
			let contentWidth = el.scrollWidth;
			el.addEventListener(
				`mouseenter`,
				() => {
					offsetWidth = el.offsetWidth;
					contentWidth = el.scrollWidth;
					setTimeout(() => {
						const id = el.getAttribute(`aria-describedby`);
						const tooltip: any = document.querySelector(`#${id}`);
						if (tooltip && contentWidth <= offsetWidth) {
							tooltip.style.visibility = `hidden`;
						} else {
							tooltip.style.visibility = `visible`;
						}
					}, 10);
				},
				false
			);
		}
	}
};
