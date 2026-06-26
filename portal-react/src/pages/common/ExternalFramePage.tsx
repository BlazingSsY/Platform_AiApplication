import { Result } from 'antd';

export default function ExternalFramePage() {
  return (
    <Result
      status="info"
      title="外部应用入口"
      subTitle="该路径用于承载通过 Nginx 反代接入的外部应用。若需要嵌入式展示，请在网关中配置对应子路径反代。"
    />
  );
}
