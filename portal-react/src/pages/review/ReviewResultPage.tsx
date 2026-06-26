import { Button, Descriptions, Empty, Result, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function ReviewResultPage() {
  const module = useReviewModule();
  const navigate = useNavigate();

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>审查结果</h1>
          <p>{module.title}结果查看与报告下载入口。</p>
        </div>
        <Space>
          <Button onClick={() => navigate(module.listPath)}>查看复核列表</Button>
          <Button type="primary" onClick={() => navigate(module.uploadPath)}>
            新建审查
          </Button>
        </Space>
      </div>
      <Result
        status="info"
        title="请选择一条审查任务查看结果"
        subTitle="从问题复核列表进入详情后，可展示审查结论、问题定位、规则命中和报告导出。"
      />
      <Descriptions bordered column={2} className="result-placeholder">
        <Descriptions.Item label="审查应用">{module.title}</Descriptions.Item>
        <Descriptions.Item label="结果状态">待选择任务</Descriptions.Item>
        <Descriptions.Item label="审查文件" span={2}>
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无文件" />
        </Descriptions.Item>
      </Descriptions>
    </section>
  );
}
