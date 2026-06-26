import { LogoutOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Dropdown, Space } from 'antd';
import type { MenuProps } from 'antd';
import { useAuthStore } from '@/store/auth';
import defaultAvatar from '@/assets/images/userDefault.png';

export default function UserMenu() {
  const userInfo = useAuthStore((state) => state.userInfo);
  const openLogin = useAuthStore((state) => state.openLogin);
  const logout = useAuthStore((state) => state.logout);
  const name = userInfo?.name || userInfo?.loginName || userInfo?.username;

  if (!name) {
    return (
      <button className="header-login-button" type="button" onClick={() => openLogin()}>
        登录
      </button>
    );
  }

  const items: MenuProps['items'] = [
    {
      key: 'logout',
      label: '退出登录',
      icon: <LogoutOutlined />,
      onClick: logout,
    },
  ];

  return (
    <Dropdown menu={{ items }} placement="bottomRight">
      <Space className="header-user-info">
        <Avatar src={userInfo?.profile || defaultAvatar} icon={<UserOutlined />} />
        <span>{name}</span>
      </Space>
    </Dropdown>
  );
}
