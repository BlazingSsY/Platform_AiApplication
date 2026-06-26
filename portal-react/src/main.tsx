import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConfigProvider, App as AntApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import 'antd/dist/reset.css';
import './styles/global.scss';
import App from './App';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: '#1b63f0',
          borderRadius: 4,
          fontFamily: '"Microsoft YaHei", "PingFang SC", Arial, sans-serif'
        },
        components: {
          Button: { borderRadius: 4 },
          Card: { borderRadiusLG: 6 },
          Modal: { borderRadiusLG: 6 },
          Table: { borderRadius: 4 }
        }
      }}
    >
      <AntApp>
        <App />
      </AntApp>
    </ConfigProvider>
  </React.StrictMode>
);
