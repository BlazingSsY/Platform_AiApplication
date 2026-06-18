<!--
 * @Description: 模块名称
 * @Author: ym
 * @Date: 2021-05-19 16:08:06
 * @LastEditTime: 2021-11-25 16:11:49
-->
<template>
	<div :class="{ customForm: true, towColumn: towColumn, isLook: isLook }">
		<el-form :ref="`customForm${id}`" :model="formInfo" :rules="rules" :label-width="formConfig.labelWidth || `130px`">
			<div v-for="item in formConfig.formList" :key="item.prop" class="item">
				<el-form-item v-if="!item.hidd" :label="isLook ? item.label + ` ：` : item.label" :prop="item.prop">
					<template v-if="item.slotName">
						<slot :name="item.slotName" />
					</template>
					<template v-else-if="isLook">
						<span v-if="isShowPassword || item.prop !== `password`">{{ formInfo[item.prop] }}</span>
						<span v-else class="passwordText">********</span>
						<span v-if="item.prop === `password`" class="passwords">
							<i v-solt:suffix class="el-icon-view" @click="onSeeClicked" />
						</span>
					</template>
					<template v-else-if="item.type === `dateRange`">
						<el-date-picker
							v-model="item[item.prop]"
							type="daterange"
							value-format="yyyy-MM-dd HH:mm:ss"
							end-placeholder="结束日期"
							range-separator="至"
							start-placeholder="开始日期"
							@change="changeFn($event, item)"
						/>
					</template>
					<template v-else-if="item.type === `select`">
						<el-select :value="formInfo[item.prop]" :disabled="item.cbDisabled ? item.cbDisabled(formInfo) : false" :placeholder="`请选择${item.label}`" @change="changeFn($event, item)">
							<el-option v-for="item1 in item.optionCb()" :key="item1.value" :label="item1.label" :value="item1.value" />
						</el-select>
					</template>
					<template v-else-if="item.type === `radio`">
						<el-radio v-for="radio in item.option" :key="radio.value" :value="formInfo[item.prop]" :label="radio.value">{{ radio.label }}</el-radio>
					</template>
					<template v-else>
						<el-input
							:value="formInfo[item.prop]"
							:type="item.inputType || ``"
							:placeholder="`请输入${item.label}`"
							:maxlength="item.maxlength || ``"
							:show-password="item.prop === `password`"
							:disabled="item.cbDisabled ? item.cbDisabled(formInfo) : false"
						/>
					</template>
				</el-form-item>
			</div>
		</el-form>
	</div>
</template>
<script setup lang="ts">
	import { defineProps } from 'vue';

	const { proxy } = getCurrentInstance() as any;
	const props = defineProps({
		'formConfig': {
			'type': Object,
			'required': true,
			'default': () => ({
				'formList': [],
				'formRules': {}
			})
		},
		'id': {
			'type': String,
			'required': false,
			'default': `1`
		},
		'isLook': {
			'type': Boolean,
			'required': false,
			'default': false
		},
		'formInfo': {
			'type': Object,
			'required': false,
			'default': () => ({})
		},
		'towColumn': {
			'type': Boolean,
			'required': false,
			'default': true
		}
	});

	const rules = ref({});
	const isShowPassword = ref(false);

	watch(
		() => props.isLook,
		() => {
			isShowPassword.value = false;
			setFormRules();
		},
		{ 'immediate': true }
	);

	watch(props.formConfig, () => {
		setFormRules();
	});

	const changeFn = (e: any, item: any) => {
		if (item.changeFn) {
			item.changeFn(e);
		}
	};

	const setFormRules = () => {
		if (props.isLook) {
			proxy.$refs[`customForm${props.id}`] && proxy.$refs[`customForm${props.id}`].clearValidate();
			rules.value = {};
		} else {
			rules.value = { ...proxy.formConfig.formRules };
		}
		proxy.$nextTick(() => {
			proxy.$refs[`customForm${props.id}`]?.clearValidate();
		});
	};

	const onSeeClicked = () => {
		isShowPassword.value = !isShowPassword.value;
	};
</script>
<style lang="scss" scoped>
	.towColumn {
		/deep/.el-form {
			display: flex;
			flex-wrap: wrap;
			justify-content: space-between;
			.item {
				width: 49%;
				.passwordText {
					vertical-align: middle;
				}
				.passwords {
					padding-left: 10px;
					> i {
						color: #4c8cff;
						&:hover {
							cursor: pointer;
							color: #0c49bb;
						}
					}
				}
			}
		}
	}
	.isLook {
		/deep/ .el-form {
			.el-form-item {
				background: #fff;
				margin-bottom: 0px;
			}
		}
	}
</style>
