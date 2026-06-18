<script setup>
import { isPassRegular } from '@/utils/regularCommon';
import { usersPasswordchange } from '@/ajax/index';
import md5 from 'md5';
import store from "@/store/index";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  defaultLoginName: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:visible', 'success']);

const dialogVisible = ref(false);

watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal;
}, { immediate: true });

watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal);
});

const userInfo = computed(() => store.state.user.userInfo);
const currentLoginName = computed(() => props.defaultLoginName || store.state.changePasswordLoginName || userInfo.value?.loginName || '');

const notEquel = (rule, value, callback) => {
  if (value === formPass.value.password) {
    callback(new Error(`新密码不能和旧密码相等`));
  } else {
    callback();
  }
};

const confirmPasswordFilter = (rule, value, callback) => {
  if (formPass.value.newpassword && value !== formPass.value.newpassword) {
    callback(new Error(`两次输入的密码不一致！`));
  } else {
    callback();
  }
};

const formPassRef = ref();
const formPass = ref({
  'password': '',
  'newpassword': '',
  'confirmpassword': ''
});

const formPassRules = reactive({
  'password': [
    {
      'required': true,
      'message': `请输入旧密码！`,
      'trigger': `blur`
    },
    {
      'required': true,
      'validator': isPassRegular,
      'trigger': `blur`
    }
  ],
  'newpassword': [
    {
      'required': true,
      'message': `请输入密码`,
      'trigger': `blur`
    },
    {
      'required': true,
      'validator': notEquel,
      'trigger': `blur`
    },
    {
      'required': true,
      'validator': isPassRegular,
      'trigger': `blur`
    }
  ],
  'confirmpassword': [
    {
      'required': true,
      'message': `请再次输入密码`,
      'trigger': `blur`
    },
    {
      'required': true,
      'validator': isPassRegular,
      'trigger': `blur`
    },
    {
      'required': true,
      'validator': confirmPasswordFilter,
      'trigger': `blur`
    }
  ]
});

const canaleFn = () => {
  dialogVisible.value = false;
  formPass.value = {
    'password': '',
    'newpassword': '',
    'confirmpassword': ''
  };
};

const submitFn = (formEl) => {
  if (!formEl) return;
  formEl.validate(async valid => {
    if (valid) {
      const sentData = {
        'loginName': currentLoginName.value,
        'oldPassword': md5(formPass.value.password),
        'password': md5(formPass.value.newpassword)
      };
      await usersPasswordchange(sentData).then(res => {
        if (res.succ) {
          ElMessage.success(`密码修改成功！`);
          emit('success');
           // 刷新页面
          setTimeout(() => {
            location.reload();
          }, 400);
        }
      });
      dialogVisible.value = false;
    }
  });
};

watch(dialogVisible, (newVal) => {
  if (newVal) {
    formPass.value = {
      'password': '',
      'newpassword': '',
      'confirmpassword': ''
    };
  }
});
</script>

<template>
  <el-dialog v-model="dialogVisible" draggable title="修改密码" :destroy-on-close="true" :close-on-click-modal="false" :close-on-press-escape="false" width="40%" @close="dialogVisible = false">
    <el-form ref="formPassRef" class="mt20" :rules="formPassRules" :model="formPass" label-width="120px">
      <el-form-item label="登录名">
        <el-input :model-value="currentLoginName" :readonly="true" />
      </el-form-item>
      <el-form-item label="旧密码" prop="password">
        <el-input v-model="formPass.password" type="password" auto-complete="off" placeholder="请输入旧密码 " />
      </el-form-item>
      <el-form-item label="新密码" prop="newpassword">
        <el-input v-model="formPass.newpassword" type="password" autocomplete="off" placeholder="请输入新密码 " />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmpassword">
        <el-input v-model="formPass.confirmpassword" type="password" autocomplete="off" placeholder="请确认新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button class="cancel" @click="canaleFn"> 取 消 </el-button>
        <el-button type="primary" class="save" @click="submitFn(formPassRef)"> 确 定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.mt20 {
  margin-top: 20px;
}
</style>
