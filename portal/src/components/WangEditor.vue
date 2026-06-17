<template>
	<div style="border: 1px solid #ccc">
		<Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :default-config="toolbarConfig" :mode="mode" />
		<Editor v-model="valueHtml" :style="{ height: height + 'px', 'overflow-y': 'hidden' }" :default-config="editorConfig" :mode="mode" @onCreated="handleCreated" />
	</div>
</template>
<script setup>
	import '@wangeditor/editor/dist/css/style.css'; // 引入 css
	import { uploadFile } from '@/ajax/app';
	import { getCurrentInstance, defineProps, defineEmits } from 'vue';
	import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
	const { proxy } = getCurrentInstance();
	const props = defineProps({
		modelValue: {
			type: String,
			default: ''
		},
		'height': {
			type: Number,
			default: 300
		}
	})
	const emit = defineEmits(['update:modelValue']);
	const valueHtml = computed({
		get() {
			return props.modelValue;
		},
		set(value) {
			emit(`update:modelValue`, value);
		}
	});
	// 编辑器实例，必须用 shallowRef
	const editorRef = shallowRef();

	const mode = `simple`;
	// const toolbarConfig = {}
	// 工具栏配置
	const toolbarConfig = {
		'toolbarKeys': [
			// 菜单 key
			`headerSelect`,
			`bold`, // 加粗
			`italic`, // 斜体
			`through`, // 删除线
			`underline`, // 下划线
			`bulletedList`, // 无序列表
			`numberedList`, // 有序列表
			`color`, // 文字颜色
			`insertLink`,
			{
				'key': `group-image`,
				'title': `图片`,
				'iconSvg': `<svg viewBox="0 0 1024 1024"><path d="M959.877 128l0.123 0.123v767.775l-0.123 0.122H64.102l-0.122-0.122V128.123l0.122-0.123h895.775zM960 64H64C28.795 64 0 92.795 0 128v768c0 35.205 28.795 64 64 64h896c35.205 0 64-28.795 64-64V128c0-35.205-28.795-64-64-64zM832 288.01c0 53.023-42.988 96.01-96.01 96.01s-96.01-42.987-96.01-96.01S682.967 192 735.99 192 832 234.988 832 288.01zM896 832H128V704l224.01-384 256 320h64l224.01-192z"></path></svg>`,
				'menuKeys': [`insertImage`, `uploadImage`]
			},
			/*      {
        "key": "group-video",
        "title": "视频",
        "iconSvg": "<svg viewBox=\"0 0 1024 1024\"><path d=\"M981.184 160.096C837.568 139.456 678.848 128 512 128S186.432 139.456 42.816 160.096C15.296 267.808 0 386.848 0 512s15.264 244.16 42.816 351.904C186.464 884.544 345.152 896 512 896s325.568-11.456 469.184-32.096C1008.704 756.192 1024 637.152 1024 512s-15.264-244.16-42.816-351.904zM384 704V320l320 192-320 192z\"></path></svg>",
        "menuKeys": [
          "insertVideo",
          "uploadVideo"
        ]
      }, */
			`fontSize`, // 字体大小
			`lineHeight`, // 行高
			`delIndent`, // 缩进
			`indent`, // 增进
			`deleteImage`, // 删除图片
			`divider`, // 分割线
			`insertTable`, // 插入表格
			`justifyCenter`, // 居中对齐
			`justifyJustify`, // 两端对齐
			`justifyLeft`, // 左对齐
			`justifyRight`, // 右对齐
			`undo`, // 撤销
			`redo`, // 重做
			`clearStyle`, // 清除格式
			`fullScreen` // 全屏
		]
	};
	// 自定义上传图片
	const editorConfig = {
		'placeholder': `请输入内容...`, // 配置默认提示
		// readOnly: true,
		'MENU_CONF': {
			// 配置上传服务器地址
			'uploadImage': {
				// 小于该值就插入 base64 格式（而不上传），默认为 0
				'base64LimitSize': 5 * 1024, // 5kb
				// 单个文件的最大体积限制，默认为 2M
				// maxFileSize: 1 * 1024 * 1024, // 1M
				// // 最多可上传几个文件，默认为 100
				// maxNumberOfFiles: 5,
				// 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
				'allowedFileTypes': [`image/*`],

				onBeforeUpload(file) {
					const isJPG = file.type === `image/jpeg` || file.type === `image/png`;
					const isLt10M = file.size / 1024 / 1024 < 2;
					if (!isJPG) {
						proxy.$message.error(`上传图片只能是 JPG后者PNG 格式!`);
					}
					if (isJPG && !isLt10M) {
						proxy.$message.error(`上传图片大小不能超过 2MB!`);
					}
					return isJPG && isLt10M;
				},
				// 自定义上传
				customUpload(file, insertFn) {
					// 文件上传
					try {
						const formData = new FormData();
						formData.append(`file`, file);
						uploadFile(formData)
							.then(res => {
								if (res.succ) {
									const imgUrlId = res.content;
									insertFn(`/portal/common/v1/storage/download/${imgUrlId}?fileId=${imgUrlId}&fileName=${file.name}`);
								}
							})
							.catch(res => {
								ElMessage.error(`上传附件失败！`);
							});
					} catch (e) {
						ElMessage.error(`上传附件失败！`);
					}
					// 插入到富文本编辑器中，主意这里的三个参数都是必填的，要不然控制台报错：typeError: Cannot read properties of undefined (reading 'replace')
					// insertFn(result.data.data.url, result.data.data.name, result.data.data.name)
				}
			},
			'uploadVideo': {
				async customUpload(file, insertFn) {
					try {
						// 自定义上传云储存结束
					} catch (e) {
						console.log(`222`);
						ElMessage.error(`上传附件失败！`);
					}
				}
			}
		}
	};

	// 组件销毁时，也及时销毁编辑器
	onBeforeUnmount(() => {
		const { value } = editorRef;
		if (value === null) return;
		value.destroy();
	});
	const handleCreated = (editor) => {
		editorRef.value = editor; // 记录 editor 实例，重要！
	};
</script>
<style lang="scss">
.w-e-text-container {
    word-wrap: break-word;
    word-break: break-all;
  }
</style>
