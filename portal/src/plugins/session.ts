export const setSession = (name: string, content: string) => {
	if (!name) return;
	if (typeof content !== `string`) {
		content = JSON.stringify(content);
	}
	window.sessionStorage.setItem(name, content);
};
export const getSession = (name: string) => {
	if (!name) return;
	return window.sessionStorage.getItem(name);
};

export const removeSession = (name?: string) => {
	if (name) {
		window.sessionStorage.removeItem(name);
	} else {
		window.sessionStorage.clear();
	}
};
