<template>
	<div class="tree-table">
		<!-- <el-button @click="this.$refs.form.validate()">表达校验</el-button> -->
		<el-form v-if="formData" ref="form" :model="formData" label-width="0px">
			<el-table ref="treeTable" :data="tableData" border :tree-props="{ children: `children`, hasChildren: `hasChildren` }" default-expand-all :row-key="rowKey">
				<el-table-column v-for="(item, index) in tableBaseConfig" :key="index + '_' + item[rowKey]" :label="item.label" :width="item.width || `auto`" :min-width="item.minWidth || `auto`">
					<template #default="scope">
						<template v-if="item.type === `text`">
							{{ scope.row[item.prop] }}
						</template>
						<template v-else-if="item.type === `html`">
							<div class="html-cell" v-html="scope.row[item.prop]" />
						</template>
						<template v-else-if="item.type === `input`">
							<el-form-item :prop="`rowForm_${scope.row[rowKey]}.${item.prop}`" :rules="item.rules">
								<el-input v-model="scope.row[item.prop]" :placeholder="`请输入${item.label}`" :disabled="typeof item.disabled == 'function' ? item.disabled(scope) : item.disabled" />
							</el-form-item>
						</template>
						<template v-else-if="item.type === `select`">
							<el-form-item :prop="`rowForm_${scope.row[rowKey]}.${item.prop}`" :rules="item.rules">
								<el-select
									v-model="scope.row[item.prop]"
									:placeholder="`请选择${item.label}`"
									:disabled="typeof item.disabled == 'function' ? item.disabled(scope) : item.disabled"
									@change="item.changeCallback && item.changeCallback($event, scope)"
								>
									<el-option v-for="item1 in item.optionCallback()" :key="item1.value" :label="item1.label" :value="item1.value" />
								</el-select>
							</el-form-item>
						</template>
						<template v-else-if="item.type === `checkbox`">
							<el-form-item :prop="`rowForm_${scope.row[rowKey]}.${item.prop}`" :rules="item.rules">
								<el-checkbox-group v-model="scope.row[item.prop]" @change="item.changeCallback && item.changeCallback($event, scope)">
									<el-checkbox v-for="ckb in item.optionCallback()" :key="ckb.value" :label="ckb.value" :value="ckb.label">{{ ckb.label }}</el-checkbox>
								</el-checkbox-group>
							</el-form-item>
						</template>
						<template v-else-if="item.type === `radio`">
							<el-form-item :prop="`rowForm_${scope.row[rowKey]}.${item.prop}`" :rules="item.rules">
								<el-radio
									v-for="radio in item.optionCallback()"
									:key="radio.value"
									v-model="scope.row[item.prop]"
									:label="radio.value"
									@change="item.changeCallback && item.changeCallback($event, scope)"
								>
									{{ radio.label }}
								</el-radio>
							</el-form-item>
						</template>
						<template v-else-if="item.type === `btnbar` && (item.show ? item.show() : true)">
							<el-button
								v-for="(btn, bIndex) in item.btns"
								v-show="typeof btn.show == 'function' ? btn.show(scope) : btn.show || true"
								:key="bIndex"
								:type="btn.type"
								:icon="btn.icon"
								:title="btn.title"
								:disabled="typeof btn.disabled == 'function' ? btn.disabled(scope) : btn.disabled"
								@click="onClickHandle(btn.clickCallback, scope)"
							>
								{{ btn.label }}
							</el-button>
						</template>
					</template>
				</el-table-column>
			</el-table>
		</el-form>
	</div>
</template>
<script setup lang="ts">
	import { defineProps } from 'vue';

	const { proxy, setupState } = getCurrentInstance() as any;
	const props = defineProps({
		'tableBaseConfig': {
			'type': Array,
			'required': false,
			'default': () => [
				{
					'label': `标题`,
					'prop': `name`,
					'type': `input` // text|html|input|select|radio|checkbox|btnbar
				},
				{
					'label': `操作`,
					'type': `btnbar`,
					'width': 150,
					'show': () => true,
					'btns': [
						{
							'type': `text`,
							'icon': `el-icon-plus`,
							'clickCallback': `addBrotherRow`
						},
						{
							'type': `text`,
							'icon': `el-icon-down`,
							'clickCallback': `addChildRow`
						},
						{
							'type': `text`,
							'icon': `el-icon-minus`,
							'clickCallback': `deleteRow`
						}
					]
				}
			]
		} as any,
		'tableData': {
			'type': Array,
			'required': false,
			'default': () => []
		},
		// 分页配置
		'paginationConfig': {
			'type': Object,
			'required': false,
			'default': () => ({
				'show': false,
				'total': 0,
				'pageNumber': 1,
				'pageSize': 20
			})
		},
		'isShowPagination': {
			'type': Boolean,
			'required': false,
			'default': false
		},
		'total': {
			'type': Number,
			'required': false,
			'default': 0
		},
		'rowKey': {
			'type': String,
			'required': false,
			'default': `id`
		}
	});

	// 根据表格数据构造表单字段
	const formData = computed(() => {
		const obj = {} as any;
		const func = (list: any[], rowkey: string) => {
			list.forEach((item, index) => {
				obj[`rowForm_${item[rowkey]}`] = item;
				if (item.children && Array.isArray(item.children)) {
					func(item.children, rowkey);
				}
			});
		};
		func(props.tableData, props.rowKey);
		return obj;
	});

	const onClickHandle = (clickCallback: any, scope: any) => {
		if (typeof clickCallback === `string`) {
			setupState[clickCallback](scope.row, props.tableData);
		} else if (typeof clickCallback === `function`) {
			clickCallback(scope);
		}
	};

	// 添加同级行节点
	const addBrotherRow = (rowData: any, list: any[]) => {
		if (!rowData && Array.isArray) {
			list.push(newRowData());
			return;
		}
		list.forEach((row, index) => {
			if (row[props.rowKey] === rowData[props.rowKey]) {
				list.splice(index + 1, 0, newRowData());
			} else if (Array.isArray(row.children)) {
				addBrotherRow(rowData, row.children);
			}
		});
	};

	// 添加子级行节点
	// rowData：当前操作行数据，list:要操作的列表，index:当前操作行索引
	const addChildRow = (rowData: any, list: any[]) => {
		if (Array.isArray(rowData.children)) {
			rowData.children.push(newRowData());
		} else {
			rowData.children = [newRowData()];
		}
		const arr = JSON.parse(JSON.stringify(list));
		list.splice(0, list.length, ...arr);
	};

	// 删除行节点，rowData：行数据，list:父节点列表
	const deleteRow = (rowData: any, list: any[]) => {
		list.forEach((row, index) => {
			if (row[props.rowKey] === rowData[props.rowKey]) {
				list.splice(index, 1);
			} else if (Array.isArray(row.children)) {
				deleteRow(rowData, row.children);
			}
		});
	};

	// 创建行数据模板
	const newRowData = () => {
		const rowObj = {} as any;
		rowObj[props.rowKey] = +new Date();
		props.tableBaseConfig.forEach((item: any) => {
			if (item.type === `checkbox`) {
				rowObj[item.prop] = [];
			} else {
				rowObj[item.prop] = item.default || null;
			}
		});
		return rowObj;
	};

	// 表单验证
	const validateForm = () => {
		const promise = new Promise((resolve, reject) => {
			proxy.$refs.form.validate((res: any) => {
				resolve(res);
			});
		});
		return promise;
	};
</script>
<style lang="scss" scoped>
	/deep/ .el-form {
		.el-form-item {
			background-color: transparent;
			margin-bottom: 0;
			.el-form-item__content {
				background-color: transparent;
			}
			.el-input__inner {
				background-color: transparent;
			}
			.el-form-item__error {
				top: auto;
				bottom: -13px;
				z-index: 111;
			}
		}
	}
	/deep/ .el-table {
		th {
			background: rgba($color: #6387ca, $alpha: 0.07);
			color: #444;
			padding: 8px 0;
		}
		td {
			padding: 10px 0;
			&:nth-child(1) .cell {
				display: flex;
				align-items: center;
			}
		}
		.cell {
			overflow: visible;
			.el-button {
				margin: 0px 5px 0px 0px;
			}
			.iconfont.icontianjiaziji {
				font-size: inherit;
			}
		}
	}
</style>
