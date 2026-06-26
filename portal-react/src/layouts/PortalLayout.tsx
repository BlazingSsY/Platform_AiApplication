import { useEffect, useMemo, useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Spin, message } from 'antd';
import { DownOutlined } from '@ant-design/icons';
import LoginModal from '@/components/LoginModal';
import UserMenu from '@/components/UserMenu';
import { fetchApplications, fetchLoginInfo } from '@/api/portal';
import { useAppStore } from '@/store/app';
import { getToken, useAuthStore } from '@/store/auth';
import { openApplication } from '@/utils/openApplication';
import type { ApplicationItem } from '@/types';
import logo from '@/assets/images/logoHeader.png';
import banner from '@/assets/images/banner.png';

const systemFeatures: ApplicationItem[] = [
  { name: '用户管理', url: '/userList', status: 1 },
  { name: '组织管理', url: '/depList', status: 1 },
  { name: '应用管理', url: '/applicationManage', status: 1 },
  { name: '资讯管理', url: '/informationManage', status: 1 },
  { name: '菜单管理', url: '/menu', status: 1 },
];

const moduleMap = [
  { label: '设计', group: '设计研发' },
  { label: '生产', group: '生产制造' },
  { label: '管理', group: '运营管理' },
  { label: '算力', group: '算力资源' },
  { label: '办公', group: '办公管理' },
];

export default function PortalLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const setApplications = useAppStore((state) => state.setApplications);
  const applications = useAppStore((state) => state.applications);
  const setUserInfo = useAuthStore((state) => state.setUserInfo);
  const [loading, setLoading] = useState(false);
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    fetchApplications()
      .then((result) => {
        if (mounted && result.succ !== false) {
          setApplications(result.content || {});
        }
      })
      .catch(() => message.error('应用列表加载失败'))
      .finally(() => mounted && setLoading(false));

    if (getToken()) {
      fetchLoginInfo()
        .then((result) => {
          if (result.succ !== false && result.content) {
            setUserInfo(result.content);
          }
        })
        .catch(() => undefined);
    }
    return () => {
      mounted = false;
    };
  }, [setApplications, setUserInfo]);

  const dropdowns = useMemo(() => {
    const items = moduleMap.map((module) => ({
      name: module.label,
      features: applications[module.group] || [],
    }));
    items.push({ name: '系统', features: systemFeatures });
    return items.filter((item) => item.name === '系统' || item.features.length > 0);
  }, [applications]);

  const handleOpen = (item: ApplicationItem) => {
    openApplication(item, navigate);
    setActiveDropdown(null);
  };

  return (
    <Spin spinning={loading}>
      <div className="layout-portal">
        <header className="portal-header">
          <div className="portal-header-content">
            <button className="portal-logo-button" type="button" onClick={() => navigate('/home')}>
              <img className="portal-logo" src={logo} alt="平台首页" />
            </button>
            <nav className="portal-nav">
              <button
                type="button"
                className={location.pathname === '/home' ? 'active' : ''}
                onClick={() => navigate('/home')}
              >
                首页
              </button>
              {dropdowns.map((item) => (
                <div
                  className="portal-dropdown"
                  key={item.name}
                  onMouseEnter={() => setActiveDropdown(item.name)}
                  onMouseLeave={() => setActiveDropdown(null)}
                >
                  <button type="button" className={activeDropdown === item.name ? 'active' : ''}>
                    {item.name}
                    <DownOutlined />
                  </button>
                  <div className={`portal-dropdown-content ${activeDropdown === item.name ? 'show' : ''}`}>
                    {item.features.length === 0 ? (
                      <div className="portal-dropdown-empty">暂无应用</div>
                    ) : (
                      item.features.map((feature) => (
                        <button
                          key={`${feature.id || feature.name}-${feature.url}`}
                          className={Number(feature.status) === 1 ? '' : 'disabled'}
                          type="button"
                          onClick={() => handleOpen(feature)}
                        >
                          {feature.name}
                        </button>
                      ))
                    )}
                  </div>
                </div>
              ))}
            </nav>
            <UserMenu />
          </div>
        </header>
        <section className="portal-hero" style={{ backgroundImage: `url(${banner})` }} />
        <main className="portal-content-wrapper">
          <div className="portal-content">
            <Outlet />
          </div>
          <footer className="portal-footer">数字智能应用平台</footer>
        </main>
        <LoginModal />
      </div>
    </Spin>
  );
}
