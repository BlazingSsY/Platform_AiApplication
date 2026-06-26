import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Form, Input, Modal, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { fetchLoginInfo, login } from '@/api/portal';
import { useAuthStore } from '@/store/auth';

interface LoginFormValues {
  loginName: string;
  password: string;
}

export default function LoginModal() {
  const navigate = useNavigate();
  const [form] = Form.useForm<LoginFormValues>();
  const loginOpen = useAuthStore((state) => state.loginOpen);
  const loginRedirect = useAuthStore((state) => state.loginRedirect);
  const setTokens = useAuthStore((state) => state.setTokens);
  const setUserInfo = useAuthStore((state) => state.setUserInfo);
  const closeLogin = useAuthStore((state) => state.closeLogin);

  const handleLogin = async (values: LoginFormValues) => {
    const result = await login(values);
    if (result.succ === false) {
      return;
    }
    const content = (result.content || {}) as Record<string, any>;
    const token = result.options?.token || content.token || '';
    const refreshToken = result.options?.refreshToken || content.refreshToken || '';
    setTokens(token, refreshToken);

    const info = await fetchLoginInfo();
    if (info.succ !== false && info.content) {
      setUserInfo(info.content);
    }
    closeLogin();
    message.success('登录成功');
    if (loginRedirect) {
      navigate(loginRedirect);
    }
  };

  return (
    <Modal
      title="用户登录"
      open={loginOpen}
      footer={null}
      centered
      onCancel={closeLogin}
      destroyOnClose
      className="portal-login-modal"
    >
      <Form form={form} layout="vertical" onFinish={handleLogin} requiredMark={false}>
        <Form.Item name="loginName" label="账号" rules={[{ required: true, message: '请输入账号' }]}>
          <Input prefix={<UserOutlined />} placeholder="请输入账号" size="large" autoComplete="username" />
        </Form.Item>
        <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="请输入密码"
            size="large"
            autoComplete="current-password"
          />
        </Form.Item>
        <Button type="primary" htmlType="submit" size="large" block>
          登录
        </Button>
      </Form>
    </Modal>
  );
}
