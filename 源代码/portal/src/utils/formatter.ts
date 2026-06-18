export function formatTime(timeInput: string | number | Date): string {
	const inputDate = new Date(timeInput);
	if (isNaN(inputDate.getTime())) {
		return String(timeInput);
	}

	const now = new Date();
	const isToday = (
		inputDate.getFullYear() === now.getFullYear() &&
		inputDate.getMonth() === now.getMonth() &&
		inputDate.getDate() === now.getDate()
	);

	if (!isToday) return String(timeInput);

	const diffMs = now.getTime() - inputDate.getTime();
	if (diffMs < 0) return String(timeInput);

	const diffSec = Math.floor(diffMs / 1000);
	if (diffSec >= 3600) {
		const hours = Math.floor(diffSec / 3600);
		return `${hours}小时前`;
	} else if (diffSec >= 60) {
		const minutes = Math.floor(diffSec / 60);
		return `${minutes}分钟前`;
	} else {
		return `${diffSec}秒前`;
	}
}

export function formatPercent(input: number | string): string | number {
	let value: number;
	if (typeof input === 'string') {
		value = parseFloat(input);
	} else {
		value = input;
	}
	if (typeof value !== 'number' || isNaN(value)) {
		return input;
	}
	return `${(value * 100).toFixed(2)}%`;
}


