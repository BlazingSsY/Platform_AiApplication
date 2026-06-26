import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  BarChartOutlined,
  DownloadOutlined,
  FileDoneOutlined,
  FileSearchOutlined,
  HomeOutlined,
  MessageOutlined,
  OrderedListOutlined,
  ProfileOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Layout, Menu } from 'antd';
import { reviewModules } from '@/routes/reviewRoutes';
import logoWhite from '@/assets/images/logo-white.png';

const { Header, Sider, Content } = Layout;

export default function ReviewLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const module =
    reviewModules.find((item) =>
      [
        item.basePath,
        item.homePath,
        item.uploadPath,
        item.listPath,
        item.detailPath,
        item.statisticsPath,
        item.toolPath,
        item.resultPath,
        item.rulePath,
        item.feedbackPath,
        item.logPath,
      ].includes(location.pathname),
    ) || reviewModules[0];

  const menuItems: MenuProps['items'] = [
    { key: module.homePath, label: '首页', icon: <HomeOutlined /> },
    { key: module.uploadPath, label: '文档审查', icon: <FileSearchOutlined /> },
    { key: module.resultPath, label: '审查结果', icon: <FileDoneOutlined /> },
    { key: module.listPath, label: '问题复核', icon: <UnorderedListOutlined /> },
    { key: module.rulePath, label: '规则列表', icon: <OrderedListOutlined /> },
    { key: module.statisticsPath, label: '统计分析', icon: <BarChartOutlined /> },
    { key: module.toolPath, label: '工具下载', icon: <DownloadOutlined /> },
    { key: module.feedbackPath, label: '意见反馈', icon: <MessageOutlined /> },
    { key: module.logPath, label: '日志查看', icon: <ProfileOutlined /> },
  ];

  return (
    <Layout className="review-layout">
      <Sider width={230} className="review-sider">
        <button className="review-brand" type="button" onClick={() => navigate('/home')}>
          <img src={logoWhite} alt="平台首页" />
          <span>{module.shortTitle}</span>
        </button>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header className="review-header">
          <div>
            <strong>{module.title}</strong>
            <span>智能化审查工作台</span>
          </div>
          <button type="button" onClick={() => navigate('/home')}>
            返回门户
          </button>
        </Header>
        <Content className="review-content">
          <Outlet context={module} />
        </Content>
      </Layout>
    </Layout>
  );
}
